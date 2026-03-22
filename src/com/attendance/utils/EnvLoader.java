package com.attendance.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * EnvLoader - Reads key=value pairs from a .env file.
 * Call EnvLoader.load(".env") once at app startup,
 * then use EnvLoader.get("KEY") anywhere in the project.
 */
public class EnvLoader {

    private static final Map<String, String> envMap = new HashMap<>();
    private static boolean loaded = false;

    // -------------------------------------------------------
    // Load the .env file from the given file path
    // -------------------------------------------------------
    public static void load(String filePath) {

        if (loaded) {
            System.out.println("[EnvLoader] Already loaded, skipping.");
            return;
        }

        System.out.println("[EnvLoader] Loading: " + filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip blank lines and comment lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int equalsIndex = line.indexOf('=');

                if (equalsIndex <= 0) {
                    System.out.println("[EnvLoader] WARNING: Skipping invalid line " + lineNumber + " -> " + line);
                    continue;
                }

                String key   = line.substring(0, equalsIndex).trim();
                String value = line.substring(equalsIndex + 1).trim();

                envMap.put(key, value);
            }

            loaded = true;
            System.out.println("[EnvLoader] .env file loaded successfully. (" + envMap.size() + " keys found)");

        } catch (IOException e) {
            System.err.println("[EnvLoader] ERROR: Could not read .env file at path: " + filePath);
            System.err.println("[EnvLoader] Details: " + e.getMessage());
            System.err.println("[EnvLoader] Make sure .env exists in the project root folder.");
        }
    }

    // -------------------------------------------------------
    // Get a value by key (returns empty string if not found)
    // -------------------------------------------------------
    public static String get(String key) {
        String value = envMap.get(key);
        if (value == null) {
            System.err.println("[EnvLoader] WARNING: Key not found in .env -> " + key);
            return "";
        }
        return value;
    }

    // -------------------------------------------------------
    // Get a value by key with a fallback default
    // -------------------------------------------------------
    public static String get(String key, String defaultValue) {
        return envMap.getOrDefault(key, defaultValue);
    }
}
