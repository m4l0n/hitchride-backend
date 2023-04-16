package com.m4l0n.hitchride.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;

@Configuration
public class FirebaseConfig {

    @Value("classpath:serviceaccount.json")
    Resource firebaseServiceAccountKey;

    @Bean
    @Lazy
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }

    @PostConstruct
    public void initializeFirebase() {
        try {
//            InputStream serviceAccountKeyStream = new ByteArrayInputStream(firebaseServiceAccountKey.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(firebaseServiceAccountKey.getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }

}
