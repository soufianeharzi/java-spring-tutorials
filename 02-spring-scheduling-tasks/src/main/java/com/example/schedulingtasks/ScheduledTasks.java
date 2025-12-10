package com.example.schedulingtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Simple scheduled task that logs the current time every 5 seconds.
 *
 * <p>Spring creates this bean and runs the {@link #reportCurrentTime()} method
 * on a fixed schedule because of the {@code @Scheduled} annotation.</p>
 */
@Component
public class ScheduledTasks {
    // This class can contain methods annotated with @Scheduled to perform scheduled tasks.

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
    }
}
