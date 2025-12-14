# Customer Domain Model

A simple data class that represents a customer from the database. Uses a Java record to avoid writing boilerplate code.

## Overview

A domain model is a class that represents data in your application. Instead of passing around raw database values, we create a `Customer` object that holds the id, first name, and last name together.

## Why Use a Record?

A **record** is a special Java class (added in Java 14) that automatically generates code for you.

Without a record, you'd have to write:

```java
public class Customer {
    private final long id;
    private final String firstName;
    private final String lastName;

    // Constructor
    public Customer(long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters
    public long id() { return id; }
    public String firstName() { return firstName; }
    public String lastName() { return lastName; }

    // equals(), hashCode(), toString() - even more code!
}
```

With a record, one line does all of that:

```java
public record Customer(long id, String firstName, String lastName) { }
```

**What the record gives you automatically:**
- Constructor that takes all fields
- Getter methods: `id()`, `firstName()`, `lastName()`
- `equals()` - compares all fields
- `hashCode()` - generates hash from all fields
- `toString()` - prints all field values

## @Override and toString()

### What is @Override?

`@Override` is an annotation that tells the compiler: "I'm replacing a method that exists in the parent class."

```java
@Override
public String toString() {
    // my custom version
}
```

**Why use it?**
- The compiler checks that you're actually overriding something
- If you make a typo (like `tostring()` instead of `toString()`), the compiler catches it
- Makes code clearer - you can see this method replaces an inherited one

### Why Override toString()?

Records give you a default `toString()` that looks like:

```
Customer[id=3, firstName=Josh, lastName=Bloch]
```

But the guide overrides it to use a custom format:

```
Customer[id=3, firstName='Josh', lastName='Bloch']
```

The custom version adds quotes around the string values. This is a style choice - you might want different formatting for logging or debugging.

## String.format()

`String.format()` creates a string by filling in placeholders with values.

```java
String.format("Customer[id=%d, firstName='%s', lastName='%s']", id, firstName, lastName)
```

**The placeholders:**
- `%d` = decimal number (for `long`, `int`)
- `%s` = string (for `String` and most objects)

**How it works:**
1. First `%d` gets replaced with `id`
2. First `%s` gets replaced with `firstName`
3. Second `%s` gets replaced with `lastName`

**Example:**
```java
String.format("Hello %s, you are %d years old", "Alice", 25)
// Result: "Hello Alice, you are 25 years old"
```

## The Complete Customer Record

```java
public record Customer(long id, String firstName, String lastName) {

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }
}
```

**Line by line:**
1. `public record Customer(...)` - defines a record with three fields
2. `@Override` - we're replacing the default toString()
3. `public String toString()` - method that returns a string representation
4. `String.format(...)` - creates the formatted string
5. The format string with `%d` and `%s` placeholders
6. `id, firstName, lastName` - values that fill the placeholders

## In This Module

### Mapping to Database

| Record Field | DB Column | Type |
|--------------|-----------|------|
| `id` | `id` | `long` |
| `firstName` | `first_name` | `String` |
| `lastName` | `last_name` | `String` |

Note: Java uses camelCase (`firstName`), databases typically use snake_case (`first_name`). The RowMapper handles this translation.

### RowMapper Lambda

```java
(rs, rowNum) -> new Customer(
    rs.getLong("id"),
    rs.getString("first_name"),
    rs.getString("last_name")
)
```

This lambda converts each database row into a Customer object:
- `rs` = ResultSet (the current row from the database)
- `rowNum` = which row number (we don't use it here)
- `rs.getLong("id")` = get the `id` column as a long
- `rs.getString("first_name")` = get the `first_name` column as a String

## Related Concepts

- [JdbcTemplate](jdbc-template.md) - Uses Customer with RowMapper
- [Java Records](https://docs.oracle.com/en/java/javase/17/language/records.html) - Language feature

## References

- [Java 17 Records](https://docs.oracle.com/en/java/javase/17/language/records.html)
- [String.format() Javadoc](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html#format(java.lang.String,java.lang.Object...))
- [Spring Guide: Relational Data Access](https://spring.io/guides/gs/relational-data-access/)