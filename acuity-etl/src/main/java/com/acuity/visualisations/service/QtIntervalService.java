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

import com.acuity.visualisations.dal.dao.SourceDao;
import com.acuity.visualisations.dal.dao.aml.AdverseEventDao;
import com.acuity.visualisations.dal.dao.aml.AlgorithmOutputDao;
import com.acuity.visualisations.dal.dao.aml.ConmedDao;
import com.acuity.visualisations.dal.dao.aml.EcgDao;
import com.acuity.visualisations.dal.dao.aml.LabDao;
import com.acuity.visualisations.model.aml.AdverseEvent;
import com.acuity.visualisations.model.aml.AlgorithmMetadata;
import com.acuity.visualisations.model.aml.AlgorithmOutcome;
import com.acuity.visualisations.model.aml.Conmed;
import com.acuity.visualisations.model.aml.Ecg;
import com.acuity.visualisations.model.aml.EventType;
import com.acuity.visualisations.model.aml.Lab;
import com.acuity.visualisations.model.aml.qtinterval.QtInput;
import com.acuity.visualisations.model.aml.qtinterval.QtOutput;
import com.acuity.visualisations.model.output.OutputEntityUtil;
import com.acuity.visualisations.model.output.entities.Source;
import com.acuity.visualisations.transform.entity.EntityDescriptionRule;
import com.acuity.visualisations.util.HashUtil;
import com.acuity.visualisations.util.QtJsonUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.acuity.visualisations.data.util.Util.currentDate;
import static com.acuity.visualisations.util.ConfigurationUtil.getEntityDescriptionRuleByName;
import static com.acuity.visualisations.util.HashUtil.MD5;
import static java.util.Collections.emptyList;

@Slf4j
@Service
public class QtIntervalService {

    @Value("${azureml.webservice.qtalgorithm.url}")
    private String webserviceUrl;
    @Value("${azureml.webservice.qtalgorithm.apikey}")
    private String apiKey;
    @Value("${azureml.webservice.qtalgorithm.workspaces}")
    private String workspace;
    @Value("${azureml.webservice.qtalgorithm.service}")
    private String service;
    @Value("${azureml.webservice.qtalgorithm.api-version}")
    private String apiVersion;
    @Value("${azureml.webservice.qtalgorithm.details}")
    private String details;

    private final EcgDao ecgDao;
    private final ConmedDao conmedDao;
    private final LabDao labDao;
    private final AdverseEventDao adverseEventDao;
    private final AlgorithmOutputDao algorithmOutputDao;
    private final SourceDao sourceDao;

    @Autowired
    public QtIntervalService(@NotNull @Qualifier("amlEcgDao") EcgDao ecgDao, @NotNull @Qualifier("amlConmedDao") ConmedDao conmedDao,
                             @NotNull @Qualifier("amlLabDao") LabDao labDao, @NotNull @Qualifier("amlAdverseEventDao") AdverseEventDao adverseEventDao,
                             @NotNull AlgorithmOutputDao algorithmOutputDao, @NotNull SourceDao sourceDao) {
        this.ecgDao = ecgDao;
        this.conmedDao = conmedDao;
        this.labDao = labDao;
        this.adverseEventDao = adverseEventDao;
        this.algorithmOutputDao = algorithmOutputDao;
        this.sourceDao = sourceDao;
    }

    private static final String SOURCE_TYPE_SOFTWARE = "software";

    @Transactional
    public void run(String projectName, @NonNull String studyId) throws Exception {

        log.debug("Async retrieving ecg, ae, lab and conmed data from database and grouping by subject");
        CompletableFuture<Map<String, List<Ecg>>> ecgFuture = CompletableFuture
                .supplyAsync(() -> {
                    log.debug("Filter out ECGs already calculated");
                    Map<String, Ecg> qtIntervalsMeasurements = ecgDao.getByStudyIdWithQtMeasurement(studyId)
                            .stream().collect(Collectors.toMap(Ecg::getId, Function.identity()));
                    if (!qtIntervalsMeasurements.isEmpty()) {
                        List<AlgorithmOutcome> algorithmOutcome =
                                algorithmOutputDao.getByEventIds(qtIntervalsMeasurements.keySet());
                        algorithmOutcome.forEach(out -> qtIntervalsMeasurements.remove(out.getEventId()));
                    }
                    return qtIntervalsMeasurements.values();
                })
                .thenApply(ecgs -> ecgs.stream().collect(Collectors.groupingBy(Ecg::getSubject)));
        CompletableFuture<Map<String, List<AdverseEvent>>> aeFuture = CompletableFuture
                .supplyAsync(() -> adverseEventDao.getByStudyId(studyId))
                .thenApply(aes -> aes.stream().collect(Collectors.groupingBy(AdverseEvent::getSubject)));
        CompletableFuture<Map<String, List<Lab>>> labFuture = CompletableFuture
                .supplyAsync(() -> labDao.getByStudyIdWithPotassiumMagnesiumFromSerumInPredefinedUnits(studyId))
                .thenApply(labs -> labs.stream().collect(Collectors.groupingBy(Lab::getSubject)));
        CompletableFuture<Map<String, List<Conmed>>> conmedFuture = CompletableFuture
                .supplyAsync(() -> conmedDao.getByStudyId(studyId))
                .thenApply(conmeds -> conmeds.stream().collect(Collectors.groupingBy(Conmed::getSubject)));

        log.debug("Gathering data to prepare algorithm input");
        List<QtInput> input = buildUpInput(ecgFuture.get(), aeFuture.get(), conmedFuture.get(), labFuture.get());

        if (input.isEmpty()) {
            log.debug("No ECG data to send to Azure ML to calculate");
        } else {
            log.debug("Preparing request body to Azure ML");
            String body = QtJsonUtil.convertInputToJson(input);

            log.debug("Sending data to Azure ML");
            String qtIntervalResult = invokeAlgorithmCalculation(body);

            List<QtOutput> result = QtJsonUtil.extractAlgorithmResult(qtIntervalResult);
            log.debug("Algorithm result size: " + result.size());

            AlgorithmMetadata algorithmMetadata = QtJsonUtil.extractAlgorithmMetadata(qtIntervalResult);
            log.debug("Algorithm name: " + algorithmMetadata.getName() + "; Algorithm version: " + algorithmMetadata.getVersion());

            log.debug("Check if current source already exists");
            String sourceId = HashUtil.getHashForAlgorithm(MD5, algorithmMetadata.getName(), algorithmMetadata.getVersion(), SOURCE_TYPE_SOFTWARE);
            saveSourceIfAbsent(sourceId, algorithmMetadata.getName(), algorithmMetadata.getVersion());

            log.debug("Save algorithm outputs to database");
            List<AlgorithmOutcome> outputs = buildAlgorithmOutput(result, sourceId);
            int[] updated = algorithmOutputDao.save(outputs);
            log.debug("Updated: " + Arrays.stream(updated).sum() + " of " + outputs.size());
        }
        log.debug("Running QT interval algorithm for study " + studyId + " has completed");
    }

    @Transactional
    public void saveSourceIfAbsent(String sourceId, String name, String version) throws Exception {
        if (!sourceDao.exist(sourceId)) {
            Source source = new Source(name, version, SOURCE_TYPE_SOFTWARE);
            source.setId(sourceId);
            EntityDescriptionRule entityDescriptionRule = getEntityDescriptionRuleByName("Source");
            OutputEntityUtil.setSha1(source, entityDescriptionRule);
            sourceDao.insert(source);
        }
    }

    private List<AlgorithmOutcome> buildAlgorithmOutput(List<QtOutput> amlOutput, String sourceId) {
        Date createDate = currentDate();
        return amlOutput.stream()
                .map(out -> AlgorithmOutcome.builder()
                        .id(UUID.randomUUID().toString())
                        .eventId(out.getEcgId())
                        .result(out.getResult())
                        .eventType(EventType.ECG)
                        .sourceId(sourceId)
                        .createdDate(createDate)
                        .updatedDate(createDate)
                        .build())
                .collect(Collectors.toList());
    }

    private List<QtInput> buildUpInput(Map<String, List<Ecg>> ecgs, Map<String, List<AdverseEvent>> aes,
                                       Map<String, List<Conmed>> conmeds, Map<String, List<Lab>> labs) {
        List<QtInput> input = new ArrayList<>();
        ecgs.forEach((subj, ecgsPerSubj) -> ecgsPerSubj.forEach(ecg -> input.add(
                QtInput.build(ecg,
                        aes.getOrDefault(subj, emptyList()),
                        conmeds.getOrDefault(subj, emptyList()),
                        labs.getOrDefault(subj, emptyList())))));
        return input;
    }

    private String invokeAlgorithmCalculation(String body) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();

        String url = new StringBuilder().append(webserviceUrl)
                .append("/workspaces/").append(workspace)
                .append("/services/").append(service)
                .append("/execute")
                .append("?api-version=").append(apiVersion)
                .append("&details=").append(details)
                .toString();

        log.debug("URL:" + url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new ByteArrayEntity(body.getBytes()));
        httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        httpPost.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPost.addHeader(HttpHeaders.ACCEPT, "application/json");

        HttpResponse response = client.execute(httpPost);

        return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name());
    }

}
