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

import com.acuity.visualisations.mapping.dao.IFileRuleDao;
import com.acuity.visualisations.mapping.dao.IProjectRuleDao;
import com.acuity.visualisations.mapping.dao.IStudyRuleDao;
import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.web.service.uploadreports.UploadReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
@Transactional(readOnly = true)
@Service
public class AdminService {

    @Autowired
    private IProjectRuleDao projectRuleDao;

    @Autowired
    private IStudyRuleDao studyRuleDao;

    @Autowired
    private IFileRuleDao fileRuleDao;

    @Autowired
    private UIFunctionsManager functionsManager;

    @Autowired
    private UploadReportRepository uploadReportRepository;

    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void removeProject(Long projectId) {
        projectRuleDao.removeProject(projectId);
    }

    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void removeStudy(Long studyId) {
        studyRuleDao.removeStudy(studyId);
    }

    public StudyRule getStudyRule(long studyId) {
        return studyRuleDao.getStudyById(studyId);
    }

    public StudyRule getStudyRule(String code) {
        return studyRuleDao.getStudyByCode(code);
    }

    public List<FileRule> getFileRules(long studyId) {
        return fileRuleDao.getFileRulesByStudy(studyId);
    }

    public ProjectRule getProjectRule(Long projectId) {
        ProjectRule project = projectRuleDao.getProjectById(projectId);
        project.setTotalStudyCount(getDrugStudyCount(project.getDrugId()));
        return project;
    }

    @Async
    @Transactional(rollbackFor = Throwable.class)
    public void cleanStudiesRelatedInformationAsync(Map<String, List<String>> studies) {
        functionsManager.deleteStudies(studies);
        studies.values().stream()
               .flatMap(List::stream)
               .forEach(study -> {
                   uploadReportRepository.removeStudyExceptionsReport(study);
                   uploadReportRepository.removeStudyTableReport(study);
                   uploadReportRepository.removeStudyFieldReport(study);
                   uploadReportRepository.removeStudyValueReport(study);
                   uploadReportRepository.removeStudyRelatedSummaryReport(study);
               });
    }

    public int getDrugStudyCount(String drugId) {
        Set<String> acuityStudyCodes = new HashSet<>(studyRuleDao.getStudyCodesByDrug(drugId));
        return acuityStudyCodes.size();
    }
}
