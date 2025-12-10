# ScheduledTasks

This class defines a simple scheduled job that prints the current time to the log every 5 seconds.

```java
@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
    }
}
```

## `@Component`

- Marks this class as a Spring bean.
- Spring will create an instance of `ScheduledTasks` and manage it in the application context.
- Needed so Spring can find and run the scheduled method.

## Logger Field

```java
private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
```

- Creates a logger for this class.
- `log.info(...)` writes an informational log message.
- Logging is preferred over `System.out.println` in Spring apps.

## Date Format Field

```java
private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
```

- Defines how the time will be printed.
- `"HH:mm:ss"` means hours:minutes:seconds in 24-hour time.
- Example output: `13:45:07`

## `@Scheduled(fixedRate = 5000)`

```java
@Scheduled(fixedRate = 5000)
public void reportCurrentTime() { ... }
```

- Tells Spring to run this method on a schedule.
- `fixedRate = 5000` means run every 5000 milliseconds (every 5 seconds).
- Spring uses the scheduler enabled by `@EnableScheduling` in the main app class.

## Method Body

```java
log.info("The time is now {}", dateFormat.format(new Date()));
```

- `new Date()` gets the current date and time.
- `dateFormat.format(...)` turns it into a string like `"13:45:07"`.
- `"The time is now {}"` is a message template.
- `{}` is replaced by the formatted time.
- The final log line looks like: `The time is now 13:45:07`