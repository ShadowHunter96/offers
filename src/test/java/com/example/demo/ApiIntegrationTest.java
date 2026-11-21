package com.example.demo;

import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.OfferRepository;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OfferRepository offerRepository;

    @BeforeEach
    void clearDatabase() {
        offerRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void createsEmployeeWithGeneratedIdAndDefaultActiveFlag() {
        String employeeId = createEmployee().jsonPath().getString("id");
        var saved = employeeRepository.findById(UUID.fromString(employeeId)).orElseThrow();
        assertEquals("john.doe@example.com", saved.getEmail());
        assertTrue(saved.isActive());
    }

    @Test
    void updatesAndRetrievesEmployee() {
        String employeeId = createEmployee().jsonPath().getString("id");
        request().body(employeeJson("Jane", "jane.doe@example.com"))
                .put("/api/employees/" + employeeId).then().statusCode(200)
                .body("id", equalTo(employeeId)).body("firstName", equalTo("Jane"));
        request().get("/api/employees/id/" + employeeId).then().statusCode(200)
                .body("email", equalTo("jane.doe@example.com"));
    }

    @Test
    void listsEmployees() {
        createEmployee();
        request().get("/api/employees").then().statusCode(200)
                .body("size()", equalTo(1)).body("[0].firstName", equalTo("John"));
    }

    @Test
    void findsEmployeeByFirstName() {
        createEmployee();
        request().get("/api/employees/firstName/John").then().statusCode(200)
                .body("email", equalTo("john.doe@example.com"));
    }

    @Test
    void findsEmployeeByLastName() {
        createEmployee();
        request().get("/api/employees/lastName/Doe").then().statusCode(200)
                .body("firstName", equalTo("John"));
    }

    @Test
    void deactivatesEmployeeWithoutDeletingRecord() {
        String employeeId = createEmployee().jsonPath().getString("id");
        request().delete("/api/employees/" + employeeId).then().statusCode(204)
                .body(is(emptyString()));
        var saved = employeeRepository.findById(UUID.fromString(employeeId)).orElseThrow();
        assertFalse(saved.isActive());
        request().delete("/api/employees/" + employeeId).then().statusCode(204);
    }

    @Test
    void rejectsDuplicateEmployeeWithoutAddingRecord() {
        createEmployee();
        request().body(employeeJson("Jane", "john.doe@example.com"))
                .post("/api/employees").then().statusCode(409)
                .body("errors.errorCode", hasItem("RESOURCE_CONFLICT"));
        assertEquals(1, employeeRepository.count());
    }

    @Test
    void createsOfferWithGeneratedIdAndDraftStatus() {
        String offerId = createOffer();
        var saved = offerRepository.findById(UUID.fromString(offerId)).orElseThrow();
        assertEquals("DRAFT", saved.getStatus().name());
        assertNotNull(saved.getOfferNumber());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void listsAndRetrievesOffers() {
        String offerId = createOffer();
        request().get("/api/offers").then().statusCode(200)
                .body("size()", equalTo(1)).body("[0].id", equalTo(offerId));
        request().get("/api/offers/" + offerId).then().statusCode(200)
                .body("title", equalTo("API implementation"));
    }

    @Test
    void updatesDraftAndPersistsChanges() {
        String offerId = createOffer();
        request().body(offerJson().replace("API implementation", "Updated API"))
                .put("/api/offers/" + offerId).then().statusCode(200)
                .body("status", equalTo("DRAFT")).body("title", equalTo("Updated API"));
        assertEquals("Updated API", offerRepository.findById(UUID.fromString(offerId))
                .orElseThrow().getTitle());
    }

    @Test
    void deletesDraftAndRemovesDatabaseRecord() {
        String offerId = createOffer();
        request().delete("/api/offers/" + offerId).then().statusCode(204)
                .body(is(emptyString()));
        assertFalse(offerRepository.existsById(UUID.fromString(offerId)));
    }

    @Test
    void submitsAcceptsAndCancelsOfferWithPersistedTransitions() {
        String offerId = createOffer();
        transition(offerId, "submit", "SUBMITTED");
        transition(offerId, "accept", "ACCEPTED");
        transition(offerId, "cancel", "CANCELLED");
    }

    @Test
    void rejectsSubmittedOfferAndPreventsTerminalTransition() {
        String offerId = createOffer();
        transition(offerId, "submit", "SUBMITTED");
        transition(offerId, "reject", "REJECTED");
        request().post("/api/offers/" + offerId + "/cancel").then().statusCode(409)
                .body("errors.errorCode", hasItem("BUSINESS_RULE_VIOLATION"));
        assertEquals("REJECTED", offerRepository.findById(UUID.fromString(offerId))
                .orElseThrow().getStatus().name());
    }

    @Test
    void refusesAcceptingDraftWithoutChangingState() {
        String offerId = createOffer();
        request().post("/api/offers/" + offerId + "/accept").then().statusCode(409);
        assertEquals("DRAFT", offerRepository.findById(UUID.fromString(offerId))
                .orElseThrow().getStatus().name());
    }

    @Test
    void refusesDeletingSubmittedOfferWithoutRemovingRecord() {
        String offerId = createOffer();
        transition(offerId, "submit", "SUBMITTED");
        request().delete("/api/offers/" + offerId).then().statusCode(409);
        assertTrue(offerRepository.existsById(UUID.fromString(offerId)));
    }

    @ParameterizedTest
    @CsvSource({"/api/offers/", "/api/employees/id/"})
    void returnsStructuredNotFoundForMissingRecord(String path) {
        request().get(path + UUID.randomUUID()).then().statusCode(404)
                .body("status", equalTo(404))
                .body("errors.errorCode", hasItem("RESOURCE_NOT_FOUND"));
    }

    @ParameterizedTest
    @CsvSource({"/api/offers/not-a-uuid", "/api/employees/id/not-a-uuid"})
    void rejectsMalformedIdentifiers(String path) {
        request().get(path).then().statusCode(400);
    }

    @ParameterizedTest
    @CsvSource({
            "John,invalid-email",
            "J,john.doe@example.com"
    })
    void rejectsInvalidEmployeeAndLeavesDatabaseEmpty(String firstName, String email) {
        request().body(employeeJson(firstName, email)).post("/api/employees")
                .then().statusCode(400);
        assertEquals(0, employeeRepository.count());
    }

    @ParameterizedTest
    @CsvSource({
            "currency,usd",
            "currency,EURO",
            "title,AB"
    })
    void rejectsInvalidOfferTextAndLeavesDatabaseEmpty(String field, String value) {
        String original = field.equals("currency") ? "EUR" : "API implementation";
        String body = offerJson().replace(original, value);
        request().body(body).post("/api/offers").then().statusCode(400);
        assertEquals(0, offerRepository.count());
    }

    @ParameterizedTest
    @CsvSource({"0", "-1", "1000000000"})
    void rejectsInvalidPriceAndLeavesDatabaseEmpty(String price) {
        request().body(offerJson().replace("123.45", price)).post("/api/offers")
                .then().statusCode(400);
        assertEquals(0, offerRepository.count());
    }

    @Test
    void rejectsMalformedJson() {
        request().body("{invalid").post("/api/offers").then().statusCode(400);
        assertEquals(0, offerRepository.count());
    }

    private RequestSpecification request() {
        return given().baseUri("http://localhost").port(port).contentType("application/json");
    }

    private Response createEmployee() {
        return request().body(employeeJson("John", "john.doe@example.com"))
                .post("/api/employees").then().statusCode(201)
                .body("id", notNullValue()).extract().response();
    }

    private String createOffer() {
        return request().body(offerJson()).post("/api/offers").then().statusCode(201)
                .body("id", notNullValue()).body("status", equalTo("DRAFT"))
                .extract().jsonPath().getString("id");
    }

    private void transition(String offerId, String operation, String expectedStatus) {
        request().post("/api/offers/" + offerId + "/" + operation).then().statusCode(200)
                .body("status", equalTo(expectedStatus));
        assertEquals(expectedStatus, offerRepository.findById(UUID.fromString(offerId))
                .orElseThrow().getStatus().name());
    }

    private String employeeJson(String firstName, String email) {
        return """
                {"firstName":"%s","lastName":"Doe","email":"%s","hourlyRate":50}
                """.formatted(firstName, email);
    }

    private String offerJson() {
        return """
                {"customerId":"00000000-0000-0000-0000-000000000001", "title":"API implementation",
                 "currency":"EUR", "priceTotal":123.45}
                """;
    }
}