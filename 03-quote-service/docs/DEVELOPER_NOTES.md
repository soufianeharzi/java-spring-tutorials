# Developer Notes

## Index

| File                        | Role                                        | Notes                            |
|-----------------------------|---------------------------------------------|----------------------------------|
| `Quote.java`                | Record for JSON response                    | Shared JSON shape with consumer  |
| `Value.java`                | Nested record (`id`, `quote` text)          | Shared JSON shape with consumer  |
| `QuoteController.java`      | REST endpoints, serves quotes               | Streams, `@PathVariable`         |
| `QuoteServiceApplication.java` | Main Spring Boot application entry point | Boots the app on port 8080       |
| `application.properties`    | Configuration                               | Default port 8080                |
| `QuoteControllerTest.java`  | Tests for all 3 endpoints                   | `@WebMvcTest`, MockMvc           |

---

## Files

### [Quote.java](../src/main/java/com/example/quoteservice/Quote.java)
Shared JSON shape between quote-service and consuming client.

This record represents the full JSON response structure:
```json
{
  "type": "success",
  "value": { "id": 1, "quote": "..." }
}
```

**Why a record?**
- Immutable by default (thread-safe)
- Auto-generated `equals()`, `hashCode()`, `toString()`
- Jackson automatically maps record components to/from JSON
- No boilerplate getters/setters needed

```java
public record Quote(String type, Value value) { }
```

---

### [Value.java](../src/main/java/com/example/quoteservice/Value.java)
Shared JSON shape between quote-service and consuming client.

Represents the nested "value" part of the JSON: an `id` and the `quote`
text itself.

```java
public record Value(Long id, String quote) { }
```

---

### [QuoteController.java](../src/main/java/com/example/quoteservice/QuoteController.java)

The REST controller that serves quotes as JSON. See also [concepts/quote-controller.md](concepts/quote-controller.md) for detailed explanation.

**Annotations:**
- `@RestController` - combines `@Controller` + `@ResponseBody`, returns JSON directly
- `@RequestMapping("/api")` - sets base path for all endpoints
- `@GetMapping` - maps HTTP GET to methods
- `@PathVariable` - extracts path segments (e.g., `/api/5` → `id=5`)

**Data structure:**
```java
private static final List<Value> QUOTES = List.of(...);
```
- `List.of()` creates an immutable list (Java 9+)
- Static field means quotes are shared across all requests
- Immutability is thread-safe for concurrent access

**Endpoints:**
| Path | Method | Returns |
|------|--------|---------|
| `/api/` | `all()` | All quotes as List<Quote> |
| `/api/random` | `random()` | Single random quote |
| `/api/{id}` | `byId()` | Quote by ID or "failure" response |

**Thread-safe random selection:**
```java
Value value = QUOTES.get(ThreadLocalRandom.current().nextInt(QUOTES.size()));
```
- `ThreadLocalRandom` avoids contention in concurrent requests
- Each thread gets its own Random instance
- See [ADR-0003](adr/ADR-0003-use-threadlocalrandom.md) for rationale

---

### [QuoteServiceApplication.java](../src/main/java/com/example/quoteservice/QuoteServiceApplication.java)

Standard Spring Boot entry point. Nothing special here - just boots the application.

```java
@SpringBootApplication
public class QuoteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuoteServiceApplication.class, args);
    }
}
```

**What `@SpringBootApplication` does:**
- `@Configuration` - marks class as bean definition source
- `@EnableAutoConfiguration` - enables Spring Boot auto-config
- `@ComponentScan` - scans current package for `@Component`, `@RestController`, etc.

---

### application.properties

Configuration file at `src/main/resources/application.properties`.

```properties
# Server runs on default port 8080
# No explicit configuration needed for this module
```

**Port configuration:**
- Default: `8080`
- Override with: `server.port=8081`
- Or via command line: `./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`

**Note:** The consuming-rest module runs on port 8081 to avoid conflict when both run simultaneously.