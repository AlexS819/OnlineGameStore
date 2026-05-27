package com.sochka.onlinegamestore.infrastructure;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
2:  * Zero-dependency environment variable loader that populates System properties from a local .env file.
3:  */
@Slf4j
public class EnvLoader {

    public static void load() {
        File envFile = new File(".env");
        if (!envFile.exists()) {
            log.info("No local .env file discovered at root. Relying on system environment variables.");
            return;
        }

        log.info("Loading environment configurations from local .env file...");
        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int eqIdx = line.indexOf('=');
                if (eqIdx > 0) {
                    String key = line.substring(0, eqIdx).trim();
                    String value = line.substring(eqIdx + 1).trim();
                    
                    // Remove quotes if present
                    if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    } else if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    }

                    System.setProperty(key, value);
                    // Safe log that doesn't leak secrets
                    if (key.toLowerCase().contains("pass") || key.toLowerCase().contains("secret") || key.toLowerCase().contains("key")) {
                        log.debug("Loaded env key: {} = [SECURED]", key);
                    } else {
                        log.debug("Loaded env key: {} = {}", key, value);
                    }
                }
            }
            log.info("Environment configurations loaded successfully.");
        } catch (IOException e) {
            log.error("Failed to parse local .env file: {}", e.getMessage());
        }
    }
}
