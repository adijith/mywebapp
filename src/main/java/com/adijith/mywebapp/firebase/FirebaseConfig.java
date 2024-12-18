package com.adijith.mywebapp.firebase;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void initializeFirebase() {
        try {
            // Load the service account file from the classpath
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");

            if (serviceAccount == null) {
                throw new IOException("Service account key file not found in the classpath.");
            }

            // Firebase Realtime Database URL
            String databaseUrl = "https://bus-stops-5772d-default-rtdb.firebaseio.com/";  // Correct URL

            // Build FirebaseOptions with the service account credentials and database URL
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(databaseUrl)  // Set the correct database URL here
                    .build();

            // Initialize Firebase only if it hasn't been initialized
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully.");
            } else {
                logger.info("Firebase is already initialized.");
            }
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase: " + e.getMessage(), e);
            // Optionally, rethrow or handle the exception as per the requirement
        }
    }
}
