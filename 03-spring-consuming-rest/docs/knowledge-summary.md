# Knowledge Summary

## [Quote.java](../src/main/java/com/example/consumingrest/Quote.java)

```Java
@JsonIgnoreProperties(ignoreUnknown = true)
public record Quote(String type, Value value) { }
```

`@JsonIgnoreProperties(ignoreUnknown = true)` tells Jackson to ignore any
JSON properties that do not have a corresponding field in the Quote record.
This is useful if the JSON response contains extra data that I do not care
about.

This file defines a `Quote` record. Using a record lets me represent the
response with less boilerplate than a normal Java class.

**`Quote` has two fields:**
- `type` - a `String` representing the type of response
  (for example, `"success"`)
- `value` - a `Value` object that holds the `id` and the actual quote text

Spring, through Jackson, uses this record to map the JSON response from
the quote-service into Java, and then back into JSON when my `/quote`
endpoint returns it.

```JSON
{
  "type": "success",
  "value" : { "id": 1, "quote": "..."}
}
```

Records replace all that boilerplate - no manual constructors, getters,
`toString()`, `equals()`, or `hashCode()`. One line does it all.

There are many ways you can code this, for example using final vs using
private fields, getters vs direct field access, etc. This is why using
records is nice - it reduces the complexity of defining data-holding
types, and it simply works with Jackson for JSON mapping.



### Example using private fields so field is mutable

```Java
import com.example.consumingrest.Value;

public class Quote {

  // 1. Private fields (instance variables)
  private String type;
  private Value value;

  // 2. A constructor to initialize the fields
  public Quote(String type, Value value) {
    this.type = type;
    this.value = value;
  }

  // 3. Public accessor methods (getters)
  public String getType() {
    return type;
  }

  public Value getValue() {
    return value;
  }

  // 4. A public mutator method (setter),
  // void because it does not return anything
  public void setType(String type) {
    this.type = type;
  }

  public void setValue(Value value) {
    this.value = value;
  }
}
```



## Step by step explanation of mutable fields with setters

### Step 1: What exists in the class?

| Component       | What it does                   | Code                                             |
|-----------------|--------------------------------|--------------------------------------------------|
| **Fields**      | Data storage inside the object | `private String type;`<br>`private Value value;` |
| **Constructor** | How the object is created      | `public Quote() { }`                             |
| **Setters**     | How we change the fields       | `setType(...)`, `setValue(...)`                  |
| **Getters**     | How we read the fields         | `getType()`, `getValue()`                        |

**Summary:**

| Component       | Purpose                            |
|-----------------|------------------------------------|
| **Fields**      | Data storage inside the object     |
| **Constructor** | How the object is created          |
| **Setters**     | How we change the fields           |
| **Getters**     | How we read the fields             |



### Step 2: When are the fields "constructed"?

**When you call this (e.g., inside
[QuoteController.java](../src/main/java/com/example/consumingrest/QuoteController.java)):**

```java
Quote q = new Quote();
```

**This happens:**
1. JVM allocates a new Quote object.
2. The fields `type` and `value` are created inside that object and get
   default values:
   - `type` = null
   - `value` = null
3. The constructor `public Quote()` runs (it does nothing extra here).

**After `new Quote()`:**
- The object exists.
- The fields exist, but they are still null.

**Key Point:** Nothing has called `setType` or `setValue` yet.



### Step 3: When are the fields "set"?

**When you call this (e.g., inside
[QuoteController.java](../src/main/java/com/example/consumingrest/QuoteController.java)):**

```java
q.setType("success");
q.setValue(new Value(1L, "some quote"));
```

**This happens:**

| Method | What runs | Result |
|--------|-----------|--------|
| `setType("success")` | `this.type = type;` | `type` changes from `null` to `"success"` |
| `setValue(...)` | `this.value = value;` | `value` changes from `null` to `Value` instance |

**Summary:**
- The fields are created at construction (`new Quote()`).
- They are filled/changed when setters are called.



### Step 4: How does Jackson use this?

**When Jackson deserializes JSON into Quote, it does:**

1. `Quote q = new Quote();` (calls the no-arg constructor)
2. `q.setType(jsonTypeValue);`
3. `q.setValue(jsonValueObject);`

**Key Point:** Same exact steps as above, just done automatically by Jackson.



### Step 5: Why can these fields not be final?

**What happens to the field `type`:**

| When                            | What happens                          |
|---------------------------------|---------------------------------------|
| At construction (`new Quote()`) | `type` is created with default `null` |
| Later, in `setType(...)`        | `type` is assigned a real value       |

That means the field changes after the object is constructed.

**If you wrote:**

```java
private final String type;
private final Value value;
```

**Java rules say:**
- A final field must be assigned exactly once, in a constructor or at
  declaration.
- You cannot assign to it later in a setter.

**So with final:**
- `public Quote() { }` is illegal unless you set both `type` and `value`
  inside it.
- `setType` and `setValue` would not be allowed to do `this.type = ...`
  or `this.value = ...` because that would change a final field.

**This is why:**

> "This pattern needs non-final fields because we change them after
> construction."

**We change them here:**

```java
public void setType(String type) {
  this.type = type;  // this is a change after construction
}
```

If the field was `final`, this line would not be allowed.



### Final Summary

| What | When |
|------|------|
| Object created | `new Quote()` runs |
| Fields exist | At creation, but are `null` |
| Fields filled | When setters are called |
| Why not final | Setters change fields after construction |

**Next:** Once this makes sense, we can talk about the other pattern where
you set final fields in the constructor only. But for now, this is the
core idea.
