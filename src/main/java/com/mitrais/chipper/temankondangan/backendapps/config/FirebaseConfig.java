package com.mitrais.chipper.temankondangan.backendapps.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuthException;
import com.mitrais.chipper.temankondangan.backendapps.BackendAppsApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    private final static Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Primary
    @Bean
    public void firebaseInit() throws IOException, FirebaseAuthException {
        InputStream serviceAccount = BackendAppsApplication.class.getResourceAsStream("/serviceAccountKey.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://temankondangan-92149.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        logger.info("Firebase app: {} initialized", FirebaseApp.getInstance().getName());
    }
}
