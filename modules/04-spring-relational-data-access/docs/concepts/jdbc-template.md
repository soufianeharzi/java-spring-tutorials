# JdbcTemplate and the Main Application

This doc explains the main application class that uses Spring's `JdbcTemplate` to interact with the database.

## The Imports

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
```

| Import | What It Does |
|--------|--------------|
| `Logger`, `LoggerFactory` | SLF4J logging - prints messages to console |
| `CommandLineRunner` | Interface that runs code after Spring starts |
| `SpringApplication` | Bootstraps and launches the Spring app |
| `@SpringBootApplication` | Marks this as the main Spring Boot class |
| `JdbcTemplate` | Spring's helper for database operations |
| `List`, `Stream`, `Collectors` | Java collections and stream API |

---

## @SpringBootApplication

```java
@SpringBootApplication
public class RelationalDataAccessApplication implements CommandLineRunner {
```

`@SpringBootApplication` is a shortcut that combines three annotations:

| Annotation | What It Does |
|------------|--------------|
| `@Configuration` | This class can define Spring beans |
| `@EnableAutoConfiguration` | Spring Boot auto-configures based on dependencies (sees H2, sets up datasource) |
| `@ComponentScan` | Scans this package for other Spring components |

---

## CommandLineRunner

```java
public class RelationalDataAccessApplication implements CommandLineRunner {

    @Override
    public void run(String... strings) throws Exception {
        // This code runs after Spring Boot starts
    }
}
```

`CommandLineRunner` is an interface with one method: `run()`.

**Why use it?**
- Code in `run()` executes after Spring Boot fully starts
- The application context is ready (beans are created, database is connected)
- Good for demo apps, data seeding, or one-time startup tasks

**The `String... strings` parameter:**
- These are command-line arguments passed to the app
- We don't use them in this example

---

## Logger

```java
private static final Logger log = LoggerFactory.getLogger(RelationalDataAccessApplication.class);
```

**What this does:**
- Creates a logger for this class
- `LoggerFactory.getLogger()` creates a logger named after the class
- `private static final` - one logger shared by all instances, never changes

**Using the logger:**
```java
log.info("Creating tables");
log.info("Inserting customer record for {} {}", name[0], name[1]);
```

- `log.info()` prints an INFO level message
- `{}` are placeholders that get filled with the values after the string
- Better than `System.out.println()` - gives timestamps, log levels, class names

---

## JdbcTemplate Injection

```java
private final JdbcTemplate jdbcTemplate;

public RelationalDataAccessApplication(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
}
```

**Constructor Injection:**
- Spring sees the constructor needs a `JdbcTemplate`
- Spring auto-creates a `JdbcTemplate` (because we have `spring-boot-starter-jdbc`)
- Spring passes it to our constructor
- We save it in a field to use later

**Why `private final`?**
- `private` - only this class can access it
- `final` - can't be reassigned after construction (immutable reference)

---

## JdbcTemplate.execute() - DDL Statements

```java
jdbcTemplate.execute("DROP TABLE IF EXISTS customers");
jdbcTemplate.execute("CREATE TABLE customers(" +
        "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");
```

**`execute()`** runs SQL that doesn't return data (DDL statements).

| SQL | What It Does |
|-----|--------------|
| `DROP TABLE IF EXISTS customers` | Deletes the table if it exists (clean slate) |
| `CREATE TABLE customers(...)` | Creates a new table |

**The table structure:**
- `id SERIAL` - auto-incrementing integer (H2 syntax)
- `first_name VARCHAR(255)` - string up to 255 characters
- `last_name VARCHAR(255)` - string up to 255 characters

---

## Stream Processing - Preparing Data

```java
List<Object[]> splitUpNames = Stream.of("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long")
        .map(name -> name.split(" "))
        .collect(Collectors.toList());
```

**Step by step:**

1. `Stream.of("John Woo", "Jeff Dean", ...)` - creates a stream of full names
2. `.map(name -> name.split(" "))` - splits each name into `["John", "Woo"]`
3. `.collect(Collectors.toList())` - collects into a `List<Object[]>`

**Result:**
```
[
  ["John", "Woo"],
  ["Jeff", "Dean"],
  ["Josh", "Bloch"],
  ["Josh", "Long"]
]
```

**Why `Object[]`?**
- `batchUpdate()` expects `List<Object[]>` for the parameter values
- Each `Object[]` is one row's worth of parameters

---

## Logging the Inserts

```java
splitUpNames.forEach(name -> log.info("Inserting customer record for {} {}", name[0], name[1]));
```

- `forEach()` loops through each name array
- `name[0]` is first name, `name[1]` is last name
- `{}` placeholders get replaced with the values

**Output:**
```
Inserting customer record for John Woo
Inserting customer record for Jeff Dean
Inserting customer record for Josh Bloch
Inserting customer record for Josh Long
```

---

## JdbcTemplate.batchUpdate() - Bulk Insert

```java
jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);
```

**`batchUpdate()`** inserts multiple rows efficiently in one database call.

**Parameters:**
1. SQL with `?` placeholders: `INSERT INTO customers(first_name, last_name) VALUES (?,?)`
2. List of parameter arrays: `splitUpNames`

**How it works:**
- For each `Object[]` in the list, it runs the INSERT
- `?` placeholders get replaced with array values
- `["John", "Woo"]` becomes `INSERT INTO customers(first_name, last_name) VALUES ('John', 'Woo')`

**Why use `?` placeholders?**
- Prevents SQL injection attacks
- JDBC properly escapes the values
- Never concatenate user input into SQL strings!

---

## JdbcTemplate.query() - Select with RowMapper

```java
jdbcTemplate.query(
        "SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
        (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")),
        "Josh")
.forEach(customer -> log.info(customer.toString()));
```

**`query()` parameters:**
1. SQL query with `?` placeholder
2. RowMapper lambda - converts each row to a Customer
3. `"Josh"` - value for the `?` placeholder

**The RowMapper lambda:**
```java
(rs, rowNum) -> new Customer(
    rs.getLong("id"),
    rs.getString("first_name"),
    rs.getString("last_name")
)
```

| Parameter | What It Is |
|-----------|------------|
| `rs` | `ResultSet` - the current row from the database |
| `rowNum` | Row number (0, 1, 2...) - we don't use it here |

| Method | What It Does |
|--------|--------------|
| `rs.getLong("id")` | Gets the `id` column as a `long` |
| `rs.getString("first_name")` | Gets `first_name` column as a `String` |

**The result:**
- `query()` returns a `List<Customer>`
- We call `.forEach()` to log each one

**Output:**
```
Customer[id=3, firstName='Josh', lastName='Bloch']
Customer[id=4, firstName='Josh', lastName='Long']
```

---

## The Complete Flow

1. **Spring Boot starts** - auto-configures H2 database and JdbcTemplate
2. **Constructor injection** - Spring passes JdbcTemplate to our class
3. **run() executes** - CommandLineRunner kicks in after startup
4. **Create table** - `execute()` runs DDL
5. **Prepare data** - Stream splits names into arrays
6. **Insert rows** - `batchUpdate()` inserts all customers
7. **Query data** - `query()` finds customers named Josh
8. **Log results** - RowMapper converts rows to Customer objects, we log them

---

## JdbcTemplate Methods Summary

| Method | Use Case | Returns |
|--------|----------|---------|
| `execute(sql)` | DDL (CREATE, DROP, ALTER) | void |
| `update(sql, params...)` | Single INSERT, UPDATE, DELETE | int (rows affected) |
| `batchUpdate(sql, list)` | Multiple INSERTs efficiently | int[] (rows affected per statement) |
| `query(sql, rowMapper, params...)` | SELECT returning multiple rows | List<T> |
| `queryForObject(sql, rowMapper, params...)` | SELECT returning one row | T |

---

## References

- [Spring Docs: JdbcTemplate](https://docs.spring.io/spring-framework/reference/data-access/jdbc/core.html)
- [Spring Guide: Relational Data Access](https://spring.io/guides/gs/relational-data-access/)
- [SLF4J Logger](https://www.slf4j.org/manual.html)