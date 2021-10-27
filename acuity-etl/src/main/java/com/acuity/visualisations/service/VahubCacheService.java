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
