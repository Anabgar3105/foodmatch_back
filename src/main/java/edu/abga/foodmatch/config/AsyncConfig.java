package edu.abga.foodmatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Clase de configuración para la gestión de concurrencia y tareas programadas.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    /**
     * Configura e inicializa un pool de hilos personalizado.
     * Este pool de hilos se utiliza para ejecutar procesos en paralelo, aislando
     * tareas pesadas del flujo principal de peticiones HTTP.
     *
     * @return El ejecutor de tareas configurado con los límites de concurrencia establecidos.
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