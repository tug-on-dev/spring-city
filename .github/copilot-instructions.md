# Spring PetClinic AI Development Guide

## Architecture Overview

This is a Spring Boot 4.0 application demonstrating classic MVC patterns with JPA persistence. The codebase uses a **package-by-feature** structure under `org.springframework.samples.petclinic`:
- `owner/` - Owner and Pet management (entities, controllers, repositories)
- `vet/` - Veterinarian management
- `system/` - Cross-cutting concerns (caching, web config, welcome page)
- `model/` - Base entity classes (`BaseEntity`, `NamedEntity`, `Person`)

**Key Design Pattern**: Controllers interact directly with Spring Data JPA repositories (no service layer). Example: `OwnerController` → `OwnerRepository`.

## Database Configuration

The app supports **profile-based database switching**:
- Default: H2 in-memory (`spring.datasource` auto-configured)
- MySQL: `spring.profiles.active=mysql` or run with `-Dspring.profiles.active=mysql`
- PostgreSQL: `spring.profiles.active=postgres`

Database initialization uses `spring.sql.init.schema-locations` and `data-locations` in `application.properties`. Each DB has schema/data files in `src/main/resources/db/{h2,mysql,postgres}/`.

**Docker setup**: Use `docker compose up mysql` or `docker compose up postgres` to start databases with credentials matching `application-{mysql,postgres}.properties`.

## Build & Run Commands

### Maven (primary build tool)
```bash
./mvnw package                 # Build JAR
./mvnw spring-boot:run         # Run with hot reload
./mvnw test                    # Run all tests
./mvnw package -P css          # Recompile SCSS → CSS (required after Bootstrap/SCSS changes)
./mvnw spring-boot:build-image # Build container image
```

### Gradle (alternative)
```bash
./gradlew build                # Build JAR (output: build/libs/)
./gradlew bootRun              # Run application
```

**CSS Compilation**: SCSS sources in `src/main/scss/` compile to `src/main/resources/static/resources/css/petclinic.css`. Always run `./mvnw package -P css` after modifying `.scss` files.

## Testing Patterns

### Integration Tests with Database Profiles
- `PetClinicIntegrationTests` - Default H2 database
- `MySqlIntegrationTests` - Uses Testcontainers (`@ServiceConnection`, `MySQLContainer`)
- `PostgresIntegrationTests` - Uses Docker Compose (`spring.docker.compose.skip.in-tests=false`)

**Test applications as main methods**: `PetClinicIntegrationTests.main()`, `MysqlTestApplication.main()` include Spring Boot DevTools for fast feedback. Run these directly in your IDE for development.

### Unit Tests
- `@WebMvcTest(ControllerName.class)` - Controller tests with `MockMvc` (see `VisitControllerTests`)
- `@MockitoBean` - Mock repositories in controller tests

## Code Conventions

### Entity Patterns
- Base hierarchy: `BaseEntity` (id field) → `NamedEntity` (name field) → `Person` (firstName, lastName)
- ID generation: `@GeneratedValue(strategy = GenerationType.IDENTITY)` on `BaseEntity.id`
- Relationships: Use `@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)` with `@JoinColumn` (see `Owner.pets`, `Pet.visits`)

### Controller Patterns
- Use `@InitBinder` to set `dataBinder.setDisallowedFields("id")` (prevents mass assignment)
- `@ModelAttribute` methods populate model objects (e.g., `findOwner(@PathVariable Integer ownerId)`)
- Validation: Prefer custom `Validator` over Bean Validation for complex rules (see `PetValidator`)
- Return view names as strings (e.g., `"owners/createOrUpdateOwnerForm"`)

### Repository Patterns
- Extend `JpaRepository<Entity, Integer>` (no `@Repository` needed due to Spring Data)
- Use derived query methods: `Page<Owner> findByLastNameStartingWith(String lastName, Pageable pageable)`
- Document with Javadoc referencing Spring Data docs

### Thymeleaf Templates
- Location: `src/main/resources/templates/`
- Use fragments: `th:replace="~{fragments/layout :: layout (~{::body},'menu')}"`
- Form binding: `th:object="${owner}"`, `th:field="*{firstName}"`
- Internationalization: `th:text="#{key}"` (messages in `src/main/resources/messages/`)

## Code Quality

The build enforces:
- **Spring Java Format**: Auto-validated via `spring-javaformat-maven-plugin` (run `./mvnw spring-javaformat:apply` to fix)
- **nohttp-checkstyle**: Fails builds with HTTP URLs (use HTTPS)
- **Java 17+**: Required minimum version

## Caching

`CacheConfiguration` sets up JCache with Caffeine. The `vets` cache is pre-configured. Add new caches via `JCacheManagerCustomizer`.

## Native Image Support

The project includes `PetClinicRuntimeHints` for GraalVM native image compilation. Tests with `@DisabledInNativeImage` are skipped in native builds.

## File Organization Specifics

- **Static resources**: `src/main/resources/static/resources/{css,images,fonts}`
- **Webjars**: Bootstrap and Font Awesome accessed via `/webjars/` (e.g., `th:href="@{/webjars/font-awesome/css/font-awesome.min.css}"`)
- **Kubernetes**: Deployment manifests in `k8s/`
