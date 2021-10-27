package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.web.service.GitState;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class GitController {

    private final GitState gitRepositoryState;

    @ResponseBody
    @RequestMapping(value = "/build-info", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("@environment.getProperty('spring.profiles.active').contains('local-no-security') or hasRole('DEVELOPMENT_TEAM')")
    public GitState getBuildInfo() {
        return gitRepositoryState;
    }
}
