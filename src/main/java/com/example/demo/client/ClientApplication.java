package com.example.demo.client;

import org.springframework.web.reactive.function.client.WebClient;
import com.example.demo.client.config.WebClientConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

public class ClientApplication {

    private final WebClient webClient;
    private final Tasks tasks;

    public static void main(String[] args) {
        ClientApplication app = new ClientApplication();
        app.run();
    }

    public ClientApplication() {
        WebClientConfig webClientConfig = new WebClientConfig();
        this.webClient = webClientConfig.webClient();
        this.tasks = new Tasks(webClient);
    }

    /**
     * The entry point of the client application.
     * It initializes the web client and concurrently executes various tasks.
     */
    public void run() {
        System.out.println("ClientApplication started successfully.");

        // Create an ExecutorService with a fixed number of threads (e.g., 5)
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Create a CountDownLatch with the number of tasks to wait for
        CountDownLatch latch = new CountDownLatch(10); // Adjust to the number of tasks

        // Execute various tasks concurrently
        executeTaskInThread(executor, tasks.OwnersNamesPhones());
        executeTaskInThread(executor, tasks.NumberOfPets());
        executeTaskInThread(executor, tasks.NumberOfDogs());
        executeTaskInThread(executor, tasks.PetsSortedByWeight());
        executeTaskInThread(executor, tasks.AverageAndStdDevOfWeights());
        executeTaskInThread(executor, tasks.NameOfEldestPet());
        executeTaskInThread(executor, tasks.averagePetsPerOwner());
        executeTaskInThread(executor, tasks.ownerNamesAndPetCountsSorted());
        executeTaskInThread(executor, tasks.ownerNamesAndPetNamesForTask9());
        executeTaskInThread(executor, tasks.taskGetPetByIdWithRetry());

        // Wait for all tasks to complete
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Shutdown the executor when all tasks are submitted
        executor.shutdown();

    }

    /**
     * Executes a client task function in a separate thread, prints a description of
     * the task.
     *
     * @param executor        The ExecutorService for task execution.
     * @param taskFunction    The task function to be executed.
     * @param taskDescription The description of the task to be printed.
     */
    private void executeTaskInThread(ExecutorService executor, Runnable taskFunction) {
        executor.execute(() -> {
            taskFunction.run();
        });
    }
}
