package com.example.demo.client.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Utility class to manage file outputs for client queries.
 * Provides methods to write data to specific files.
 */
public class FileOutputUtil {

    /**
     * Base directory for the output files.
     */
    private static final String OUTPUT_DIR = "output/";

    /**
     * Writes the given data to a specific file.
     * The file is created in the output directory if it does not exist.
     * If the file already exists, append the data to the end of the file.
     * 
     * @param fileName The name of the file (without path).
     * @param data     The data to be written to the file.
     */
    public static void writeToFile(String fileName, String data) {
        try {

            // Write the data to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_DIR + fileName, true));
            writer.append(data);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

    }

    /**
     * Clears the contents of a specific file.
     * 
     * 
     * @param fileName The name of the file (without path).
     */
    public static void clearFile(String fileName) {
        try {
            // Create the output directory if it does not exist
            Paths.get(OUTPUT_DIR).toFile().mkdirs();

            // Create the file if it does not exist
            Paths.get(OUTPUT_DIR + fileName).toFile().createNewFile();

            // Clear the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_DIR + fileName));
            writer.write("");
            writer.close();
        } catch (IOException e) {
            System.err.println("Error clearing file: " + e.getMessage());
        }
    }
}