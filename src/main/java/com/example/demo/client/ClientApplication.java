package com.example.demo.client;

import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.client.config.WebClientConfig;

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
     * It initializes the web client and sequentially executes various tasks.
     */
    public void run() {
        System.out.println("ClientApplication started successfully.");

        // Execute various tasks
        executeTask(tasks.OwnersNamesPhones(), "Task 1: Get owner names and phones");
        executeTask(tasks.NumberOfPets(), "Task 2: Get the number of pets");
        executeTask(tasks.NumberOfDogs(), "Task 3: Get the number of dogs");
        executeTask(tasks.PetsSortedByWeight(), "Task 4: Get pets sorted by weight");
        executeTask(tasks.AverageAndStdDevOfWeights(), "Task 5: Get average and standard deviation of weights");
        executeTask(tasks.NameOfEldestPet(), "Task 6: Get the name of the eldest pet");
        executeTask(tasks.averagePetsPerOwner(), "Task 7: Get the average number of pets per owner");
        executeTask(tasks.ownerNamesAndPetCountsSorted(), "Task 8: Get owner names and pet IDs sorted by pet count");
        executeTask(tasks.ownerNamesAndPetNamesForTask9(), "Task 9: Get owner names and pet names sorted by pet count");
    }

    /**
     * Executes a client task function, prints a description of the task,
     * then waits for a second to let the task finish its execution.
     *
     * @param taskFunction    The task function to be executed.
     * @param taskDescription The description of the task to be printed.
     */
    private void executeTask(Runnable taskFunction, String taskDescription) {
        System.out.println(taskDescription);
        taskFunction.run();
        delay();
    }

    /**
     * Pauses the execution for a second.
     */
    private void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
