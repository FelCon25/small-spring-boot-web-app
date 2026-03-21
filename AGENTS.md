# AGENTS.md

## Project Snapshot
- Treat this repo as an early-stage Spring Boot 4 service scaffold with one live endpoint and many placeholder packages.
- Use `src/main/java/com/example/demo/DemoApplication.java` as the application root (`@SpringBootApplication` entrypoint).
- Start feature work under `src/main/java/com/example/demo/` package folders (`controller`, `service`, `repository`, `entity`, `dto`, `exception`, `config`, `security`) rather than creating parallel top-level packages.

## Architecture and Data Flow
- Follow the current HTTP flow: request -> controller method -> inline response; only `HelloController` is implemented now.
- Use `src/main/java/com/example/demo/controller/HelloController.java` as the style reference for MVC annotations (`@RestController`, `@GetMapping`).
- Note current endpoint contract exactly before changing behavior: `GET /hello` returns a plain `String` greeting and accepts `name` as an unannotated method parameter.
- Expect no service/repository boundary yet; empty `service/` and `repository/` indicate intended layering but no implemented business/data logic.
- Assume JPA autoconfiguration is active because runtime/test classpath includes JPA test starter and PostgreSQL driver from `pom.xml`.

## Build, Run, and Test Workflows
- Prefer Maven Wrapper (`./mvnw`) for all local and CI-like commands.
- Use `./mvnw spring-boot:run` for app startup; if datasource properties are missing, startup fails during datasource/JPA initialization.
- Use `./mvnw test` for default tests; current `DemoApplicationTests` context load fails without DB configuration.
- When you need green tests quickly, either provide datasource properties (for PostgreSQL) in `application.properties`/profile files or disable DB/JPA auto-config in test scope.
- Check generated test diagnostics in `target/surefire-reports/` after failures.

## Project-Specific Conventions
- Keep configuration centralized in `src/main/resources/application.properties`; only `spring.application.name=demo` exists now.
- Treat commented dependencies in `pom.xml` (`spring-boot-starter-data-jpa`, `spring-boot-starter-security`, and matching security test starter) as intentional toggles, not dead text.
- Keep Java baseline at 21 (`<java.version>21</java.version>` in `pom.xml`).
- Use `HELP.md` for Spring Boot 4.0.4 reference links when adding features that touch Web, JPA, Validation, or Security.

## Integration Points and External Dependencies
- Current external runtime dependency is PostgreSQL (`org.postgresql:postgresql`) with no committed connection properties.
- Web stack is servlet MVC (`spring-boot-starter-webmvc`), not WebFlux.
- Validation support is prewired (`spring-boot-starter-validation`), so prefer Jakarta validation annotations on DTOs once DTO layer is introduced.
- Packaging uses `spring-boot-maven-plugin`; build outputs and reports are under `target/`.

