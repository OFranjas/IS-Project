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
     *
     * @param fileName The name of the file (without path).
     * @param data     The data to be written to the file.
     */
    public static void writeToFile(String fileName, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(OUTPUT_DIR, fileName).toString()))) {
            writer.write(data);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + fileName);
            e.printStackTrace();
        }
    }
}
