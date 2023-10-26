package com.example.demo.client;

import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.client.config.WebClientConfig;
import com.example.demo.client.service.OwnerServiceClient;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.demo.server.model.Owner;

public class ClientApplication {

    public static void main(String[] args) {

        try {
            ClientApplication clientApplication = new ClientApplication();

            clientApplication.run();

        } catch (Exception e) {
            System.out.println("ClientApplication failed to start.");
            System.out.println(e.getMessage());
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run() {

        System.out.println("ClientApplication started successfully.");

        // Confige the web client
        WebClientConfig webClientConfig = new WebClientConfig();
        WebClient webClient = webClientConfig.webClient();

        // Get all owners
        getOwnersNamesPhones(webClient);
    }

    public void getOwnersNamesPhones(WebClient webClient) {

        // Create the service client
        OwnerServiceClient ownerServiceClient = new OwnerServiceClient(webClient);

        // Subscribe to getAllOwners and print the data when it's available
        ownerServiceClient.getAllOwners()
                .subscribe(owner -> {
                    // Handle each received owner here
                    System.out.println("Owner Name: " + owner.getName());
                    System.out.println("Owner Phone Number: " + owner.getPhone_number());
                    // Add more owner details as needed
                },
                        error -> {
                            // Handle errors if they occur
                            System.err.println("Error fetching owners: " + error.getMessage());
                        });

    }

}
