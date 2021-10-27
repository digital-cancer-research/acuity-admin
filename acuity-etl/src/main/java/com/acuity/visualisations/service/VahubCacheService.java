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

package com.acuity.visualisations.service;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Service class for operations over ACUITY cache files (i.e. kryo files and ehcache cache)
 */
@Slf4j
@Service
public class VahubCacheService {

    @Value("${acuity.vahub.url}")
    private String remoteUrl;
    @Value("${acuity.vahub.username}")
    private String username;
    @Value("${acuity.vahub.password}")
    private String password;
    private WebTarget target;

    private static final String VAHUB_ENDPOINT_PATH = "/resources/security";
    private static final String VAHUB_RELOAD_ACUITY_DATASET_CACHE = "/reload/acuity/%s";

    @PostConstruct
    private void addBasicAuth() {
        HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basicBuilder()
                .nonPreemptive()
                .credentials(username, password)
                .build();
        Client client = ClientBuilder.newClient();
        client.register(authFeature);
        target = client.target(remoteUrl).path(VAHUB_ENDPOINT_PATH);
    }


    public void reloadAcuityDatasetCaches(Long datasetId) {
        WebTarget newTarget = target.path(String.format(VAHUB_RELOAD_ACUITY_DATASET_CACHE, datasetId));
        log.debug("Calling GET {} to reload dataset {} cache", newTarget.getUri().toString(), datasetId);
        Response response = newTarget.request(APPLICATION_JSON).get();
        if (response.getStatus() == HttpStatus.OK.value()) {
            log.info("VAHub reload cache endpoint touched for dataset {}", datasetId);
        } else {
            log.warn("VAHub reload cache endpoint call failed for dataset {}, response code is {}", datasetId, response.getStatus());
        }
    }

}
