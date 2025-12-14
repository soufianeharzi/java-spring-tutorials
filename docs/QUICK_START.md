# Quick Start

## Run Any Module

```bash
cd <module-folder>
./mvnw spring-boot:run
```

## Module Ports

| Module | Port | URL |
|--------|------|-----|
| 01-spring-hello-rest | 8080 | http://localhost:8080/greeting |
| 02-spring-scheduling-tasks | N/A | Console output only |
| 03-quote-service | 8080 | http://localhost:8080/api/random |
| 03-spring-consuming-rest | 8081 | http://localhost:8081/quote |
| 04-spring-relational-data-access | N/A | Console output only |

## Running the 03 Modules Together

The quote-service and consuming-rest modules work together:

1. **Start quote-service first** (port 8080):
   ```bash
   cd 03-quote-service
   ./mvnw spring-boot:run
   ```

2. **Start consuming-rest second** (port 8081):
   ```bash
   cd 03-spring-consuming-rest
   ./mvnw spring-boot:run
   ```

3. **Test the integration**:
   ```bash
   curl http://localhost:8081/quote
   ```

## Run All Tests

From the repo root:
```bash
./mvnw test
```
