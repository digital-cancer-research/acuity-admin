package com.acuity.visualisations.web.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * POJO bean to hold information about the state of the git
 */
@Data
@Component
public class GitState {

    @Value("${git.build.time}")
    @JsonProperty
    private String buildTime;
    @Value("${git.commit.id.abbrev}")
    @JsonProperty
    private String lastCommitId;
    @Value("${git.commit.time}")
    @JsonProperty
    private String lastCommitTime;
    @Value("${git.branch}")
    @JsonProperty
    private String builtBranch;
}
