package com.acuity.visualisations.web.service;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.concurrent.CompletableFuture;

/**
 * Service class for synchronizing information about datasets and studies between services
 */
@Slf4j
@Service
public class SyncService {
    private static final String REMOTE_ENDPOINT_PATH = "resources/security/sync";
    private WebTarget target;
    @Value("${acuity.vasecurity.url}")
    private String remoteUrl;
    @Value("${acuity.vasecurity.username}")
    private String username;
    @Value("${acuity.vasecurity.password}")
    private String password;

    @Autowired
    private Environment environment;

    @PostConstruct
    private void addBasicAuth() {
        HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basicBuilder()
                .nonPreemptive()
                .credentials(username, password)
                .build();
        Client client = ClientBuilder.newClient();
        client.register(authFeature);
        target = client.target(remoteUrl).path(REMOTE_ENDPOINT_PATH);
    }

    public void runSynchronizationAsync() {
        if (!environment.acceptsProfiles("local-no-security")) {
            CompletableFuture.runAsync(() -> {
                try {
                    target.request().head();
                    log.info("Va-Security is reached");
                } catch (Exception e) {
                    log.error("Could not reach Va-Security, exception message is {}", e.getMessage());
                }
            });
        }
    }
}
