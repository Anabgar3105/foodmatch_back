package edu.abga.foodmatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration class for setting up asynchronous processing and scheduling in the FoodMatch application.
 * <p>This class enables asynchronous method execution and scheduling of tasks, and defines a custom thread pool executor for handling asynchronous tasks related to notifications.
 * The thread pool is configured with a core pool size of 3, a maximum pool size of 10, and a queue capacity of 100.</p>
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    /**
     * Defines a custom thread pool executor for handling asynchronous tasks related to notifications, such as sending emails.
     * The executor is configured with a core pool size of 3, a maximum pool size of 10, and a queue capacity of 100.
     * @return the configured Executor instance that will be used for executing asynchronous tasks in the application.
     */
    @Bean(name = "notificationTaskExecutor")
    public Executor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("EmailWorker-");
        executor.initialize();
        return executor;
    }
}