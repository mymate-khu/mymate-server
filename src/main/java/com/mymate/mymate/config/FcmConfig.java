package com.mymate.mymate.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FcmConfig {

    @Value("${fcm.project-id}")
    private String projectId;

    @Value("${fcm.service-account-key-path:}")
    private String serviceAccountKeyPath;

    @PostConstruct
    public void initializeFirebase() {
        try {
            GoogleCredentials credentials = getCredentials();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("✅ Firebase 초기화 완료 - Project ID: {}", projectId);
        } catch (Exception e) {
            log.error("❌ Firebase 초기화 실패", e);
            throw new RuntimeException("Firebase 초기화 실패", e);
        }
    }

    private GoogleCredentials getCredentials() throws IOException {
        if (serviceAccountKeyPath != null && !serviceAccountKeyPath.isEmpty()) {
            if (serviceAccountKeyPath.startsWith("classpath:")) {
                String cp = serviceAccountKeyPath.substring("classpath:".length());
                ClassPathResource resource = new ClassPathResource(cp);
                return GoogleCredentials.fromStream(resource.getInputStream());
            } else {
                try (InputStream is = new FileInputStream(serviceAccountKeyPath)) {
                    return GoogleCredentials.fromStream(is);
                }
            }
        } else {
            return GoogleCredentials.getApplicationDefault();
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}


