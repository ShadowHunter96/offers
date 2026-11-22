# Offers – REST API ve Spring Boot

Modelová aplikace k bakalářské práci *Vytváření a testování REST API pomocí Spring Boot*
(Unicorn vysoká škola, 2026). REST API pro správu zaměstnanců a obchodních nabídek
se stavovým automatem nabídky, validací vstupů, jednotným zpracováním chyb,
zabezpečením HTTP Basic a automatizovanými testy.

## Technologie

Java 21, Spring Boot 4.0.1 (Spring MVC, Spring Data JPA, Bean Validation, Spring Security),
PostgreSQL / H2, springdoc-openapi 3.0.3, JUnit 6 + Mockito, MockMvc, REST Assured 5.5.6,
Postman/Newman 6.2.1, JaCoCo 0.8.13, GitHub Actions.

## Struktura

```
src/main/java/com/example/demo/
  controller/  EmployeeController, OfferController
  service/     služby, mapovače, generátor čísel nabídek
  repository/  Spring Data JPA repository
  entity/ dto/ enums/
  exception/   ApiExceptionHandler, ApiErrorResponse, ErrorCode
  security/    SecurityConfig (HTTP Basic, role VIEWER/MANAGER), ApiSecurityErrorHandler
src/test/java/com/example/demo/
  service/     jednotkové testy služeb (JUnit + Mockito)
  exception/   jednotkové testy handleru chyb
  controller/  testy webové vrstvy (@WebMvcTest + MockMvc)
  ApiIntegrationTest      HTTP integrační testy (REST Assured, H2)
  OffersApplicationTests  test načtení kontextu
postman/       kolekce a prostředí pro Postman/Newman
scripts/       verify-local.ps1 – úplné lokální ověření
documentation/ api-docs.yml – vygenerovaný popis OpenAPI
.github/workflows/ci.yml
```

## Zabezpečení

| Uživatel (výchozí) | Role | Oprávnění |
|---|---|---|
| `viewer` / `viewer123` | VIEWER | pouze čtení (GET) |
| `manager` / `manager123` | MANAGER | čtení, zápis, přechody stavů nabídky |

Hesla jsou ukázková; mimo lokální vývoj se nastavují proměnnými prostředí
`OFFERS_VIEWER_PASSWORD` a `OFFERS_MANAGER_PASSWORD`. Dokumentace (`/v3/api-docs`,
`/swagger-ui/index.html`) je veřejná. Odpovědi 401/403 mají stejnou strukturu jako ostatní chyby API.

## Spuštění a testy

```powershell
mvn clean verify                 # 98 testů JUnit + report JaCoCo (target/site/jacoco)
.\scripts\verify-local.ps1       # verify + spuštění JAR s H2 + Postman kolekce přes Newman
java -jar target/demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2   # běh bez databáze
java -jar target/demo-0.0.1-SNAPSHOT.jar                               # běh s PostgreSQL (localhost:5432)
```

Reporty: `target/surefire-reports`, `target/site/jacoco`, `target/newman-report.*`.
Stejné kroky provádí workflow GitHub Actions (`.github/workflows/ci.yml`) při každém push a pull requestu.
