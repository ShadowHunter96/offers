package com.example.demo.controller;

import com.example.demo.dto.EmployeeDto;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.security.ApiSecurityErrorHandler;
import com.example.demo.security.SecurityConfig;
import com.example.demo.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import({SecurityConfig.class, ApiSecurityErrorHandler.class})
@DisplayName("EmployeeController (MockMvc) Tests")
class EmployeeControllerWebMvcTest {

    private static final RequestPostProcessor MANAGER = httpBasic("manager", "manager123");

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    @DisplayName("POST /api/employees returns 201 with active employee")
    void createReturns201() throws Exception {
        EmployeeDto created = new EmployeeDto();
        created.setId(UUID.randomUUID());
        created.setFirstName("John");
        created.setLastName("Doe");
        created.setEmail("john.doe@example.com");
        created.setActive(true);
        when(employeeService.create(any())).thenReturn(created);

        mvc.perform(post("/api/employees").with(MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("invalid e-mail returns 400 with scope email")
    void invalidEmailReturns400() throws Exception {
        mvc.perform(post("/api/employees").with(MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].scope").value("email"));
        verifyNoInteractions(employeeService);
    }

    @Test
    @DisplayName("duplicate e-mail (RESOURCE_CONFLICT) is mapped to 409")
    void duplicateEmailReturns409() throws Exception {
        when(employeeService.create(any())).thenThrow(new ApiException(ErrorCode.RESOURCE_CONFLICT,
                "employee.email", "Employee with given email already exists."));

        mvc.perform(post("/api/employees").with(MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].errorCode").value("RESOURCE_CONFLICT"));
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} returns 204 without body")
    void deactivateReturns204() throws Exception {
        UUID id = UUID.randomUUID();

        mvc.perform(delete("/api/employees/{id}", id).with(MANAGER))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(employeeService).deactivate(id);
    }
}
