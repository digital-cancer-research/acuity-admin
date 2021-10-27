/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
