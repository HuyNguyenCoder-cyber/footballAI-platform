# Football Platform

AI-assisted football editorial platform.

Tech stack:
- Spring Boot MVC
- Thymeleaf
- Spring Data JPA
- SQL Server
- Bootstrap

Local development:
- Keep `src/main/resources/application.properties` for the stable server/default config.
- Use `SPRING_PROFILES_ACTIVE=local` in IntelliJ to load `src/main/resources/application-local.properties`.
- The local profile points to the VPS SQL Server host `205.145.47.252:1433` and database `Football_AI_DEV`.
