package edu.abga.foodmatch.service;

import edu.abga.foodmatch.model.Recipe;
import edu.abga.foodmatch.model.User;
import edu.abga.foodmatch.repository.RecipeRepository;
import edu.abga.foodmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsible for managing automated notifications
 * and system communications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    /**
     * Scheduled task that orchestrates the "Weekend Recipe" campaign.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional(readOnly = true)
    public void scheduleWeekendRecipe() {
        log.info("📢 Iniciando campaña de 'Receta del Fin de Semana'...");

        List<User> allUsers = userRepository.findAll();
        List<Recipe> allRecipes = recipeRepository.findAll();

        if (allUsers.isEmpty() || allRecipes.isEmpty()) {
            log.info("No hay usuarios o recetas suficientes para la campaña.");
            return;
        }

        // Elegimos la primera receta (en un caso real, la más popular o una aleatoria)
        Recipe recipeOfTheWeek = allRecipes.get(0);

        // Simulamos dividir a los usuarios en "lotes" de 2 en 2 para enviarlos en paralelo
        int batchSize = 2;
        for (int i = 0; i < allUsers.size(); i += batchSize) {
            int end = Math.min(i + batchSize, allUsers.size());
            List<User> batch = allUsers.subList(i, end);

            // ¡MAGIA! Delegamos el lote al pool de hilos
            sendEmailBatchAsync(batch, recipeOfTheWeek);
        }
    }

    /**
     * Processes and sends a notification batch asynchronously in parallel.
     *
     * @param usersBatch Subset of recipient users in this batch.
     * @param recipe Featured recipe included in the message body.
     */
    @Async("notificationTaskExecutor")
    public void sendEmailBatchAsync(List<User> usersBatch, Recipe recipe) {
        String threadName = Thread.currentThread().getName();
        log.info("⚙️ [{}] Procesando lote de {} usuarios...", threadName, usersBatch.size());

        for (User user : usersBatch) {
            try {
                // Simulamos lo que tarda en enviarse un email real (1 segundo por email)
                Thread.sleep(1000);

                log.info("📧 [{}] Email enviado a {} ({}): ¡Prueba a cocinar {} este finde!",
                        threadName, user.getName(), user.getEmail(), recipe.getTitle());

            } catch (InterruptedException e) {
                log.error("Error enviando email en hilo {}", threadName);
                Thread.currentThread().interrupt();
            }
        }
        log.info("✅ [{}] Lote completado.", threadName);
    }
}