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

package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.mapping.AuditAction;
import com.acuity.visualisations.mapping.dao.IAEGroupRuleDao;
import com.acuity.visualisations.mapping.dao.ILabGroupRuleDao;
import com.acuity.visualisations.mapping.dao.IStudyRuleDao;
import com.acuity.visualisations.mapping.entity.Audit;
import com.acuity.visualisations.mapping.entity.AuditEntity;
import com.acuity.visualisations.mapping.entity.ColumnRule;
import com.acuity.visualisations.mapping.entity.CustomLabcodeLookup;
import com.acuity.visualisations.mapping.entity.ExcludingValue;
import com.acuity.visualisations.mapping.entity.FieldRule;
import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.FileSection;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.GroupValueBase;
import com.acuity.visualisations.mapping.entity.MappingRule;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.mapping.entity.StudyBaselineDrug;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.mapping.entity.StudySubjectGrouping;
import com.acuity.visualisations.mapping.entity.SubjectGroupRule;
import com.acuity.visualisations.service.ETLJobService;
import com.acuity.visualisations.service.JobService;
import com.acuity.visualisations.web.auth.PermissionHelper;
import com.acuity.visualisations.web.auth.UserInfoHolder;
import com.acuity.visualisations.web.dto.FileRuleDTO;
import com.acuity.visualisations.web.dto.GroupRuleDTO;
import com.acuity.visualisations.web.dto.MapRuleDTO;
import com.acuity.visualisations.web.dto.MapRulesDTO;
import com.acuity.visualisations.web.dto.MappingStatusDTO;
import com.acuity.visualisations.web.dto.PrimitiveObjWrapper;
import com.acuity.visualisations.web.dto.StudyBaselineDrugs;
import com.acuity.visualisations.web.dto.SubjectGroupValuesDTO;
import com.acuity.visualisations.web.exception.ClinicalStudyException;
import com.acuity.visualisations.web.service.AuditService;
import com.acuity.visualisations.web.service.IStudyMappingsServicePartial;
import com.acuity.visualisations.web.service.MappingPredictionService;
import com.acuity.visualisations.web.service.SyncService;
import com.acuity.visualisations.web.service.wizard.instance.GroupingsService;
import com.acuity.visualisations.web.service.wizard.study.StudyAltLabCodeService;
import com.acuity.visualisations.web.service.wizard.study.StudyBaselineDrugService;
import com.acuity.visualisations.web.service.wizard.study.StudyDrugsExplorerService;
import com.acuity.visualisations.web.service.wizard.study.StudyExcludingValueService;
import com.acuity.visualisations.web.service.wizard.study.StudyGroupingsService;
import com.acuity.visualisations.web.service.wizard.study.StudyMappingsService;
import com.acuity.visualisations.web.service.wizard.study.StudyRelationService;
import com.acuity.visualisations.web.service.wizard.study.StudyWizardService;
import com.acuity.visualisations.web.util.Consts;
import com.acuity.visualisations.web.util.GroupingsUtils;
import com.acuity.visualisations.web.util.JsonUtil;
import com.acuity.visualisations.web.workflow.ClinicalStudyWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

@Controller
@Slf4j
public class ClinicalStudyController extends AbstractController {
    private static final String TEXT_PLAIN = "text/plain";
    private static final String ACCESS_DENIED_MESSAGE = "Access denied: study ";
    private static final String VIEW_NAME = "study-setup/main";
    private static final String EDIT_STUDY_WORKFLOW = "editStudyWorkflow";
    private static final String STUDY_RULES_SEARCH = "studyRulesSearch";
    private static final String FILE_SECTIONS = "fileSections";
    private static final String PHASE_TYPES = "phaseTypes";
    private static final String AGGREGATION_FUNCTIONS = "aggregationFunctions";
    //if you will need to add more values in this list, it would be better to update MAP_FIELD_DESCRIPTION table instead,
    //add new column and store in this column value like 'Gene CtDna' for example to match filed with tooltip
    private static final List<String> FIELDS_WITH_ALTERNATIVE_DESCRIPTION = Arrays.asList("Gene");
    public static final String CBIO_PORTAL_URL = "cBioPortalUrl";
    private static final String AML_ENABLED_GLOBALLY = "amlEnabledGlobally";
    private static final String TAB_ID = "TabId";


    @Autowired
    private StudyWizardService clinicalStudyService;

    @Autowired
    private StudyDrugsExplorerService studyDrugsExplorerService;

    @Autowired
    private StudyAltLabCodeService studyAltLabCodeService;

    @Autowired
    private StudyExcludingValueService excludingValueService;

    @Autowired
    private StudyGroupingsService studyGroupingsService;

    @Autowired
    private StudyMappingsService studyMappingsService;

    @Autowired
    private IStudyMappingsServicePartial studyMappingsServicePartial;

    @Autowired
    private List<JobService> jobServices;

    @Autowired
    private AuditService auditService;

    @Autowired
    private MappingPredictionService mappingPredictionService;

    @Autowired
    private PermissionHelper permissionHelper;

    @Autowired
    private IStudyRuleDao studyRuleDao;

    @Autowired
    private StudyBaselineDrugService studyBaselineDrugService;

    @Autowired
    private GroupingsService groupingsService;

    @Autowired
    private StudyRelationService studyRelationService;

    @Autowired
    private IAEGroupRuleDao aeGroupRuleDao;

    @Autowired
    private ILabGroupRuleDao labGroupRuleDao;

    @Autowired
    private DataProvider provider;

    @Autowired
    private SyncService syncService;

    @Value("${acuity.vahub.url}")
    private String webappUrl;

    @Value("${integration.cbioportal.url:#{null}}")
    private String cbioportalUrl;

    @Value("${azureml.enable:false}")
    private boolean amlEnabledGlobally;

    @RequestMapping("/study-fix-missing-mapping-rules-for-all-studies")
    @ResponseBody
    public String fixMappingRules(HttpServletResponse response) {
        response.setContentType(TEXT_PLAIN);
        logger.info("Fixing missing mapping rules");
        List<StudyRule> studyRules = studyRuleDao.getAllStudyRules();
        List<String> studyCodes = new ArrayList<>();
        for (StudyRule studyRule : studyRules) {
            logger.info(studyRule.getStudyCode());
            studyMappingsServicePartial.getExistingFileRules(studyRule, true);
            studyCodes.add(studyRule.getStudyCode());
        }
        logger.info("Fixed");
        return String.join("\n", studyCodes);
    }

    @RequestMapping("/study-setup")
    public ModelAndView addClinicalStudy(final HttpServletRequest request, @ModelAttribute("studyId") String studyId,
                                         @ModelAttribute("studyIds") String studyIdsString)
            throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            final ModelAndView modelAndView = new ModelAndView(VIEW_NAME);
            HttpSession session = request.getSession();
            ControllerUtils.clearClinicalStudyWorkflow(session, browserTabId);
            if (StringUtils.hasText(studyId)) {
                StudyRule selectedStudy = clinicalStudyService.getStudyById(Long.parseLong(studyId));

                if (!permissionHelper.isCurrentUserDrugProgrammeAdmin(selectedStudy.getProjectId())) {
                    throw new AccessDeniedException(ACCESS_DENIED_MESSAGE + selectedStudy.getStudyCode());
                }

                ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(session, browserTabId);
                SortedMap<String, StudyRule> studies = new TreeMap<>();
                studies.put(selectedStudy.getStudyCode(), selectedStudy);
                ControllerUtils.setStudySearchResult(session, studies);
                selectClinicalStudy(selectedStudy, workflow);
                modelAndView.addObject(EDIT_STUDY_WORKFLOW, JsonUtil.toJson(workflow));
            }
            if (StringUtils.hasText(studyIdsString)) {
                List<Long> result = new ArrayList<>();
                String[] studyIdsArray = studyIdsString.split(",");
                for (String studyIdString : studyIdsArray) {
                    studyIdString = studyIdString.trim();
                    Long id = Long.valueOf(studyIdString);
                    if (id > 0) {
                        result.add(id);
                    }
                }
                SortedMap<String, StudyRule> studyRules = clinicalStudyService.getStudies(result);
                ControllerUtils.setStudySearchResult(session, studyRules);
                modelAndView.addObject(STUDY_RULES_SEARCH, JsonUtil.toJson(new ArrayList<>(studyRules.values())));
            }
            if (CollectionUtils.isEmpty(ControllerUtils.getFileSections(session))) {
                List<FileSection> fileSections = studyMappingsServicePartial.getFileSections();
                ControllerUtils.setFileSections(session, fileSections);
            }
            modelAndView.addObject("acuity.vahub.url", JsonUtil.toJson(webappUrl));
            modelAndView.addObject(FILE_SECTIONS, JsonUtil.toJson(ControllerUtils.getFileSections(session)));
            modelAndView.addObject(AGGREGATION_FUNCTIONS, JsonUtil.toJson(studyMappingsServicePartial.getAggregationFunctions()));
            modelAndView.addObject(PHASE_TYPES, JsonUtil.toJson(StudyRule.getAllStudyPhaseTypesList()));
            modelAndView.addObject(CBIO_PORTAL_URL, StringUtils.isEmpty(cbioportalUrl) ? null : JsonUtil.toJson(cbioportalUrl));
            modelAndView.addObject(AML_ENABLED_GLOBALLY, amlEnabledGlobally);
            Integer totalStudiesCount = ControllerUtils.getTotalStudiesCount(session);
            if (totalStudiesCount == null) {
                totalStudiesCount = clinicalStudyService.getTotalStudiesCount();
                ControllerUtils.setTotalStudiesCount(session, totalStudiesCount);
            }
            return modelAndView;
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when showing initial study setup page: ", e);
        }
    }

    @RequestMapping("/study-setup-total-count")
    @ResponseBody
    public Integer getStudiesCount(final HttpServletRequest request) {
        return ControllerUtils.getTotalStudiesCount(request.getSession());
    }

    @RequestMapping("/study-setup-search-studies")
    @ResponseBody
    public List<StudyRule> searchStudy(final HttpServletRequest request, @RequestParam("query") String query) throws ClinicalStudyException {
        try {
            Map<String, StudyRule> result = ControllerUtils.getStudySearchResult(request.getSession());
            return result.values().stream()
                    .filter(st -> permissionHelper.isCurrentUserDrugProgrammeAdmin(st.getProjectId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when searching for studies: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-run-search")
    @ResponseStatus(value = HttpStatus.OK)
    public void runSearch(final HttpServletRequest request, @RequestParam("query") String query) throws ClinicalStudyException {
        try {
            HttpSession session = request.getSession();
            List<StudyRule> acuityResults = new ArrayList<>();
            List<StudyRule> cdbpResults = new ArrayList<>();
            ControllerUtils.setACUITYStudyResults(session, acuityResults);
            ControllerUtils.setCDBPStudyResults(session, cdbpResults);
            String queryId = clinicalStudyService.runSearchQuery(query, acuityResults, cdbpResults);
            ControllerUtils.setQueryId(session, queryId);
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when searching for studies: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-query-status")
    @ResponseBody
    public DeferredResult<Boolean> getQueryStatus(final HttpServletRequest request) throws ClinicalStudyException {
        try {
            HttpSession session = request.getSession();
            final String queryId = ControllerUtils.getQueryId(session);
            final DeferredResult<Boolean> result = new DeferredResult<>(2 * 60000L, false);
            clinicalStudyService.getStatus(queryId, result);
            return result;
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception when checking query status", e);
        }
    }

    @RequestMapping(value = "/study-cancel-query", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void cancelQuery(final HttpServletRequest request) throws ClinicalStudyException {
        String queryId = ControllerUtils.getQueryId(request.getSession());
        clinicalStudyService.cancelQuery(queryId);
    }

    @RequestMapping(value = "/study-get-search-result", method = RequestMethod.POST)
    @ResponseBody
    public List<StudyRule> getSearchResult(final HttpServletRequest request) throws ClinicalStudyException {
        try {
            HttpSession session = request.getSession();
            List<StudyRule> acuityResults = ControllerUtils.getACUITYStudyResults(session);
            List<StudyRule> cdbpResults = ControllerUtils.getCDBPStudyResults(session);
            SortedMap<String, StudyRule> result = clinicalStudyService.getSearchStudyResult(acuityResults, cdbpResults);
            ControllerUtils.setStudySearchResult(request.getSession(), result);
            return result.values().stream()
                    .filter(st -> permissionHelper.isCurrentUserDrugProgrammeAdmin(st.getProjectId())).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when retrieve results for clinical studies", e);
        }
    }

    @RequestMapping(value = "/study-edit-programme-setup", method = RequestMethod.POST)
    public String editProgramme(@RequestParam("drugId") String drugProgramme, RedirectAttributes redirectAttributes)
            throws ClinicalStudyException {
        try {
            redirectAttributes.addFlashAttribute("drugId", drugProgramme);
            return "redirect:/programme-setup";
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when redirect to programme setup: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-select-study")
    @ResponseBody
    public ClinicalStudyWorkflow selectStudy(final HttpServletRequest request, @RequestParam("studyCode") String code)
            throws ClinicalStudyException {
        try {
            HttpSession session = request.getSession();
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            Map<String, StudyRule> searchResult = ControllerUtils.getStudySearchResult(session);

            ControllerUtils.clearClinicalStudyWorkflow(session, browserTabId);
            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
            StudyRule selectedStudy = searchResult.get(code);
            selectClinicalStudy(selectedStudy, workflow);
            return workflow;
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when selecting a study: ", e);
        }
    }

    private void fillStudyGroupsInstances(Long selectedStudyId, ClinicalStudyWorkflow workflow) {
        List<GroupRuleBase> groups = studyGroupingsService.getStudyGroups(selectedStudyId);
        workflow.setGroupings(groups);
    }

    private Long selectClinicalStudy(StudyRule selectedStudy, ClinicalStudyWorkflow workflow) {
        ProjectRule parentProject = clinicalStudyService.getParentProject(selectedStudy);
        if (!permissionHelper.isCurrentUserDrugProgrammeAdmin(parentProject.getId())) {
            throw new AccessDeniedException("Access denied");
        }
        if (selectedStudy.getId() != null) {
            fillStudyGroupsInstances(selectedStudy.getId(), workflow);
        }

        workflow.setParentProject(parentProject);
        Long id = clinicalStudyService.saveStudy(selectedStudy);
        studyMappingsServicePartial.getExistingFileRules(selectedStudy, true);
        workflow.setSelectedStudy(selectedStudy);
        selectedStudy.setStudySubjectGrouping(studyGroupingsService.getAllStudySubjectGroupings(id));
        List<GroupRuleBase> projectGroupRules = new ArrayList<GroupRuleBase>();
        projectGroupRules.addAll(aeGroupRuleDao.listGropingsByRelation(id));
        projectGroupRules.addAll(labGroupRuleDao.listGropingsByRelation(id));
        selectedStudy.setProjectGroupRules(projectGroupRules);
        selectedStudy.setStudySubjectGrouping(studyGroupingsService.getAllStudySubjectGroupings(id));
        List<MappingStatusDTO> mappingStatus = studyMappingsService.validateMappings(selectedStudy);
        workflow.setCompleteMappings(mappingStatus);
        return id;
    }

    @RequestMapping(value = "/study-setup-edit-study", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void editStudy(final HttpServletRequest request, @RequestBody StudyRule updatedStudy) throws ClinicalStudyException {
        try {
            StudyRule study = extractClinicalStudyRuleOrThrowError(request);
            study.setStudyName(updatedStudy.getStudyName());
            study.setClinicalStudyName(updatedStudy.getClinicalStudyName());
            study.setClinicalStudyId(updatedStudy.getClinicalStudyId());
            study.setPhaseType(updatedStudy.getPhaseType());
            study.setPhase(updatedStudy.getPhase());
            study.setType(updatedStudy.getType());
            study.setDeliveryModel(updatedStudy.getDeliveryModel());
            study.setPrimarySource(updatedStudy.getPrimarySource());
            study.setBlinding(updatedStudy.isBlinding());
            study.setRandomisation(updatedStudy.isRandomisation());
            study.setRegulatory(updatedStudy.isRegulatory());
            study.setScheduled(updatedStudy.isScheduled());
            study.setxAxisLimitedToVisit(updatedStudy.isxAxisLimitedToVisit());
            study.setAutoAssignedCountry(updatedStudy.isAutoAssignedCountry());
            study.setCronExpression(updatedStudy.getCronExpression());
            study.setFirstSubjectInPlanned(updatedStudy.getFirstSubjectInPlanned());
            study.setDatabaseLockPlanned(updatedStudy.getDatabaseLockPlanned());
            study.setAmlEnabled(updatedStudy.isAmlEnabled());
            Long id = clinicalStudyService.saveStudy(study);
            if (study.getId() == null) {
                auditService.logAction(AuditAction.CREATE, AuditEntity.STUDY, id, updatedStudy.getStudyCode(), "Created study " + updatedStudy.getStudyCode());
            } else {
                auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, id, updatedStudy.getStudyCode(), "Updated study " + updatedStudy.getStudyCode());
            }
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when editing study properties: ", e);
        }
    }

    @RequestMapping(value = "/study-setup-cBioPortal-config", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void editCBioPortalConfig(final HttpServletRequest request, @RequestBody StudyRule updatedStudy) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
            StudyRule study = workflow.getSelectedStudy();
            study.setProfilesMask(updatedStudy.getProfilesMask());
            study.setCbioPortalStudyCode(updatedStudy.getCbioPortalStudyCode());
            Long id = clinicalStudyService.saveStudy(study);
            if (study.getId() != null) {
                auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, id, study.getStudyCode(), "Updated study " + study.getStudyCode());
            } else {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when editing study properties: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-save-file-rule")
    @ResponseBody
    public FileRuleDTO addFileRule(final HttpServletRequest request, @RequestBody FileRuleDTO fileRuleDTO) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);

            StudyRule studyRule = workflow.getSelectedStudy();

            if (!permissionHelper.isCurrentUserDrugProgrammeAdmin(studyRule.getProjectId())) {
                throw new AccessDeniedException(ACCESS_DENIED_MESSAGE + studyRule.getStudyCode());
            }

            FileRule fileRule;
            if (fileRuleDTO.getId() == null) {


                String filePath = fileRuleDTO.getDataSourceLocation();

                //Predict file name
                if ("filePrediction".equals(fileRuleDTO.getDataSourceLocationType()) && !StringUtils.isEmpty(studyRule.getPrimarySource())) {
                    filePath = mappingPredictionService.lookForSimilarFile(fileRuleDTO.getTypeId(), studyRule.getPrimarySource());
                    if (StringUtils.isEmpty(filePath) && fileRuleDTO.isSkipFileRuleCreationIfFilePredictionFailed()) {
                        //don't create empty file rule if file prediction failed
                        return null;
                    }
                    fileRuleDTO.setDataSourceLocation(filePath);
                }

                //Create new file rule
                fileRule = studyMappingsService.createFileRule(fileRuleDTO, studyRule);

                //Predict mappings
                if (!StringUtils.isEmpty(filePath) && fileRuleDTO.isMappingsPrediction()) {
                    tryToPredictMappingRules(fileRule);
                }
            } else {
                fileRule = studyMappingsService.updateFileRule(fileRuleDTO, workflow.getSelectedStudy());
            }

            studyMappingsServicePartial.saveFileRule(fileRule);
            studyMappingsServicePartial.saveMappingRules(fileRule);
            List<MappingStatusDTO> mappingStatus = studyMappingsService.validateMappings(workflow.getSelectedStudy());
            workflow.setCompleteMappings(mappingStatus);
            studyMappingsService.validateStudyEnabled(workflow.getSelectedStudy());
            auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, workflow.getSelectedStudy().getId(), workflow.getSelectedStudy().getStudyCode(),
                    "Updated mapping rule for " + workflow.getSelectedStudy().getStudyCode() + ", " + fileRule.getDescriptions().get(0).getDisplayName());
            return new FileRuleDTO(fileRule);
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when adding new mapping: ", e);
        }
    }

    private void tryToPredictMappingRules(FileRule targetFileRule) {
        logger.debug("Started mapping prediction");

        Long similarFileRuleId = mappingPredictionService.lookForSimilarFileRuleId(targetFileRule.getDescriptions().get(0).getId(),
                targetFileRule.getStudyRule().getProjectId(), targetFileRule.getName());

        if (similarFileRuleId != null) {
            logger.debug("Found similar file rule with id " + similarFileRuleId);
            FileRule similarFileRule = studyMappingsServicePartial.getFileRule(similarFileRuleId);

            for (MappingRule targetMappingRule : targetFileRule.getMappingRules()) {
                loop:
                for (MappingRule similarMappingRule : similarFileRule.getMappingRules()) {
                    //identify mapping rule by field rule
                    for (FieldRule fieldRule : targetMappingRule.getFieldRules()) {
                        for (FieldRule similarFieldRule : similarMappingRule.getFieldRules()) {
                            if (fieldRule.getId().equals(similarFieldRule.getId())) {
                                //Copy mapping rule fields
                                targetMappingRule.setValue(similarMappingRule.getValue());
                                targetMappingRule.setFmtName(similarMappingRule.getFmtName());
                                targetMappingRule.setAggregationFunction(similarMappingRule.getAggregationFunction());
                                targetMappingRule.setAggregationFunctionId(similarMappingRule.getAggregationFunctionId());

                                //Copy column rules
                                if (targetMappingRule.getColumnRules().isEmpty()) {
                                    for (ColumnRule similarColumnRule : similarMappingRule.getColumnRules()) {
                                        ColumnRule targetColumnRule = new ColumnRule();
                                        targetColumnRule.setName(similarColumnRule.getName());
                                        targetColumnRule.setMappingRuleId(targetMappingRule.getId());
                                        targetColumnRule.setMappingRule(targetMappingRule);
                                        targetMappingRule.getColumnRules().add(targetColumnRule);
                                    }
                                }
                                break loop;
                            }
                        }
                    }
                }
            }
        }
        logger.debug("Prediction finished");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/import-study-mapping")
    @ResponseBody
    public List<MappingStatusDTO> importStudyMapping(final HttpServletRequest request,
                                                     @RequestParam(required = false) Boolean replaceExisting,
                                                     @RequestParam MultipartFile upload) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
            replaceExisting = replaceExisting == null ? false : replaceExisting;
            List<MappingStatusDTO> result = studyMappingsService.importMappings(upload.getInputStream(), workflow.getSelectedStudy(), replaceExisting);
            workflow.setCompleteMappings(result);
            studyMappingsServicePartial.validateStudyCompleted(workflow.getSelectedStudy(), ControllerUtils.getFileSections(request.getSession()));
            studyMappingsServicePartial.checkValidStatus(workflow.getSelectedStudy());
            studyMappingsService.validateStudyEnabled(workflow.getSelectedStudy());
            auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, workflow.getSelectedStudy().getId(), workflow.getSelectedStudy().getStudyCode(),
                    "Imported study mappings for " + workflow.getSelectedStudy().getStudyCode() + (replaceExisting ? " replacing existing" : ""));
            return result;
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when importing study mapping: ", e);
        }

    }


    @RequestMapping("/export-study-mapping")
    public void exportStudyMapping(final HttpServletRequest request, final HttpServletResponse response) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
            List<FileRule> fileRules = workflow.getSelectedStudy().getFileRules();
            response.setContentType("application/csv");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + workflow.getSelectedStudy().getStudyCode() + "_mappings.csv");

            byte[] result = studyMappingsService.exportMappings(fileRules);
            StreamUtils.copy(result, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when exporting study mapping: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-get-file-rules")
    @ResponseBody
    public List<FileRuleDTO> getFileRules(final HttpServletRequest request) throws ClinicalStudyException {
        try {
            String tabId = WebUtils.getCookie(request, TAB_ID).getValue();
            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), tabId);
            return workflow
                    .getSelectedStudy()
                    .getFileRules()
                    .stream()
                    .map(FileRuleDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when getting mapping information: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-delete-file-rule")
    @ResponseBody
    public List<MappingStatusDTO> deleteFileRule(final HttpServletRequest request, @RequestParam Long id) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);

            StudyRule study = workflow.getSelectedStudy();

            if (!permissionHelper.isCurrentUserDrugProgrammeAdmin(study.getProjectId())) {
                throw new AccessDeniedException(ACCESS_DENIED_MESSAGE + study.getStudyCode());
            }

            FileRule fileRule = study.getFileRule(id);
            studyMappingsServicePartial.deleteFileRule(fileRule, study);
            studyMappingsServicePartial.validateStudyCompleted(workflow.getSelectedStudy(), ControllerUtils.getFileSections(request.getSession()));
            studyMappingsServicePartial.checkValidStatus(workflow.getSelectedStudy());
            studyMappingsService.validateStudyEnabled(workflow.getSelectedStudy());
            List<MappingStatusDTO> valid = studyMappingsService.validateMappings(study);
            auditService.logAction(AuditAction.DELETE, AuditEntity.STUDY, workflow.getSelectedStudy().getId(), workflow.getSelectedStudy().getStudyCode(),
                    "Deleted file rule for " + workflow.getSelectedStudy().getStudyCode() + ", " + fileRule.getDescriptions().get(0).getDisplayName());
            return valid;
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when deleting a mapping: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-get-mapping-rules")
    @ResponseBody
    public List<MapRuleDTO> addMappingRules(final HttpServletRequest request, @RequestParam(value = "fileRuleId") Long fileRuleId)
            throws ClinicalStudyException {
        try {
            List<MappingRule> mappingRules = extractClinicalStudyRuleOrThrowError(request)
                    .getFileRule(fileRuleId)
                    .getMappingRules();
            return getCollapsedAnsSortedList(mappingRules);
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when getting mapping rules: ", e);
        }
    }

    private List<MapRuleDTO> getCollapsedAnsSortedList(List<MappingRule> model) {
        HashMap<String, MapRuleDTO> map = new HashMap<>();
        model
                .stream()
                .filter(rule -> {
                    final FieldRule fieldRule = rule.getFirstFieldRule();
                    return !map.containsKey(fieldRule.getDescription().getText())
                            || map.get(fieldRule.getDescription().getText()).getOrder().floatValue() > fieldRule.getOrder().floatValue();
                })
                .forEach(rule -> {
                    final FieldRule fieldRule = rule.getFirstFieldRule();
                    String text = fieldRule.getDescription().getText();
                    if (FIELDS_WITH_ALTERNATIVE_DESCRIPTION.contains(text)) {
                        final String entityRuleId = fieldRule.getEntityRuleId();
                        text = text + '[' + entityRuleId + ']'; //e.g. Gene -> Gene[69]
                    }
                    rule.setHelpText(HelpController.getHelpText("dataMappings", text.toLowerCase()));
                    map.put(text, new MapRuleDTO(rule));
                });
        List<MapRuleDTO> result = new ArrayList<>(map.values());
        result.sort((o1, o2) -> o1.getOrder().floatValue() - o2.getOrder().floatValue() > 0
                ? 1 : (o1.getOrder().floatValue() - o2.getOrder().floatValue() < 0 ? -1 : 0));
        return result;
    }

    private void expandDuplicates(MapRulesDTO dto, List<MappingRule> model) {
        model
                .stream()
                .filter(rule -> dto.findMapRuleById(rule.getId()) == null)
                .forEach(rule -> {
                    MapRuleDTO src = dto.findMapRuleByDataField(rule.getFieldRules().get(0).getDescription().getText());
                    if (src != null) {
                        MapRuleDTO dup = new MapRuleDTO(rule);
                        dup.setSourceData(src.getSourceData());
                        dup.setDecodingInfo(src.getDecodingInfo());
                        dup.setDefaultValue(src.getDefaultValue());
                        dup.setAgrFunctions(src.getAgrFunctions());
                        dto.getMapRules().add(dup);
                    }
                });
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-save-mapping-rules")
    @ResponseBody
    public List<MappingStatusDTO> saveMappingRules(final HttpServletRequest request, @RequestBody MapRulesDTO mapRules) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
            StudyRule study = workflow.getSelectedStudy();
            study.setMappingModifiedDate(new Date());
            selectClinicalStudy(study, workflow);
            if (!permissionHelper.isCurrentUserDrugProgrammeAdmin(study.getProjectId())) {
                throw new AccessDeniedException(ACCESS_DENIED_MESSAGE + study.getStudyCode());
            }
            FileRule fileRule = study.getFileRule(mapRules.getFileRuleId());
            fileRule.setStudyRule(study);
            fileRule.setAcuityEnabled(mapRules.isStudyAcuityEnabled());

            studyMappingsServicePartial.saveFileRule(fileRule);

            List<MappingRule> mappingRules = fileRule.getMappingRules();

            expandDuplicates(mapRules, mappingRules);

            Map<Long, MapRuleDTO> mapRuleDTOToIdMap = new HashMap<>();

            for (MapRuleDTO dto : mapRules.getMapRules()) {
                mapRuleDTOToIdMap.put(dto.getId(), dto);
            }

            Iterator<MappingRule> mappingRuleIterator = mappingRules.iterator();

            while (mappingRuleIterator.hasNext()) {
                MappingRule rule = mappingRuleIterator.next();
                if (mapRuleDTOToIdMap.containsKey(rule.getId())) {
                    studyMappingsService.upateMapRule(mapRuleDTOToIdMap.get(rule.getId()), rule);
                    mapRuleDTOToIdMap.remove(rule.getId());
                } else {
                    mappingRuleIterator.remove();
                    studyMappingsService.deleteMapRule(rule);
                }
            }

            Map<MappingRule, String> newRulesToFieldNames = new HashMap<>();

            for (MapRuleDTO dto : mapRuleDTOToIdMap.values()) {
                MappingRule mapRule = studyMappingsService.createMapRule(dto, fileRule);
                mappingRules.add(mapRule);
                newRulesToFieldNames.put(mapRule, dto.getDataField());
            }

            studyMappingsServicePartial.saveMappingRules(fileRule);

            for (Map.Entry<MappingRule, String> entry : newRulesToFieldNames.entrySet()) {
                entry.getKey().getFieldRules().add(studyMappingsServicePartial.saveDynamicFieldRule(entry.getKey(), entry.getValue()));
            }

            List<MappingStatusDTO> result = studyMappingsService.validateMappings(study);
            workflow.setCompleteMappings(result);
            studyMappingsServicePartial.validateStudyCompleted(study, ControllerUtils.getFileSections(request.getSession()));
            studyMappingsServicePartial.checkValidStatus(study);
            studyMappingsService.validateStudyEnabled(study);
            auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, study.getId(), study.getStudyCode(),
                    "Updated column rules for " + study.getStudyCode() + ", " + fileRule.getDescriptions().get(0).getDisplayName());
            return result;
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when saving mapping rules: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-get-mapping-statuses")
    @ResponseBody
    public List<MappingStatusDTO> getMappingStatuses(final HttpServletRequest request) throws ClinicalStudyException {
        try {
            return studyMappingsService.validateMappings(extractClinicalStudyRuleOrThrowError(request));
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when calculating mapping status: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-summary")
    @ResponseBody
    public ClinicalStudyWorkflow getSummary(final HttpServletRequest request) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            return ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when retrieving dataset summary: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-get-subject-grouping-names-from-result-table")
    @ResponseBody
    public List<String> getSubjectGroupingNamesFromResultTable(final HttpServletRequest request) {
        return studyGroupingsService.getSubjectGroupingNamesFromResultTable(extractClinicalStudyRuleOrThrowError(request).getId());
    }
    // grouping methods

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-save-group")
    public void addGroup(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(required = false) MultipartFile upload)
            throws ClinicalStudyException {
        try {
            response.setContentType(TEXT_PLAIN);

            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);

            StudyRule study = workflow.getSelectedStudy();

            if (!permissionHelper.isCurrentUserDrugProgrammeAdmin(study.getProjectId())) {
                throw new AccessDeniedException(ACCESS_DENIED_MESSAGE + study.getStudyCode());
            }

            GroupRuleBase group = new SubjectGroupRule();
            group.setParentId(study.getId());
            fillGroupFromParameter(group, request);

            String remoteLocation = request.getParameter("remoteLocation");
            String sourceType = request.getParameter("studyDataSource");
            String message = studyGroupingsService.updateGroup(group, sourceType, remoteLocation, upload);
            auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, study.getId(), study.getStudyCode(),
                    "Updated groupings for " + study.getStudyCode());
            if (StringUtils.hasText(message)) {
                writeGroup(new GroupRuleDTO(group, message), response);
                return;
            }

            workflow.addGrouping(group);

            writeGroup(new GroupRuleDTO(group, Consts.EMPTY_STRING), response);
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when saving study group: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-edit-group")
    @ResponseStatus(value = HttpStatus.OK)
    public void editGroup(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(required = false) MultipartFile upload)
            throws ClinicalStudyException {
        try {
            response.setContentType(TEXT_PLAIN);
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
            String idString = request.getParameter("id");
            Long id = null;
            try {
                id = Long.parseLong(idString);
            } catch (NumberFormatException ex) {
                log.error("Number format exception", ex);
            }
            GroupRuleBase group = workflow.getGroup(id);
            fillGroupFromParameter(group, request);

            String remoteLocation = request.getParameter("remoteLocation");
            String sourceType = request.getParameter("studyDataSource");
            String message = studyGroupingsService.updateGroup(group, sourceType, remoteLocation, upload);
            auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, workflow.getSelectedStudy().getId(), workflow.getSelectedStudy().getStudyCode(),
                    "Updated groupings for " + workflow.getSelectedStudy().getStudyCode());
            if (StringUtils.hasText(message)) {
                writeGroup(new GroupRuleDTO(group, message), response);
                return;
            }

            writeGroup(new GroupRuleDTO(group, Consts.EMPTY_STRING), response);
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when editing study group: ", e);
        }
    }

    private void fillGroupFromParameter(GroupRuleBase group, final HttpServletRequest request) {
        String name = normalizeSpace(request.getParameter("name"));
        String defaultValue = request.getParameter("defaultValue");
        boolean headerRow = Boolean.parseBoolean(request.getParameter("headerRow"));
        boolean isReady = Boolean.parseBoolean((request.getParameter("ready")));

        group.setName(name);
        group.setDefaultValue(defaultValue);
        group.setHeaderRow(headerRow);
        group.setReady(isReady);
    }

    private void writeGroup(GroupRuleDTO group, final HttpServletResponse response) throws ClinicalStudyException {
        String result;
        Writer writer = null;
        try {
            result = JsonUtil.toJson(group);
            writer = response.getWriter();
            writer.write(result);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new ClinicalStudyException("Error writing group to response", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    getLogger().warn("Unable to close writer when writing group values to HttpServletResponse", e);
                }
            }
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-delete-group")
    @ResponseBody
    public GroupRuleBase deleteGroup(final HttpServletRequest request, @RequestParam("groupId") Long groupId) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
            StudyRule study = workflow.getSelectedStudy();

            if (!permissionHelper.isCurrentUserDrugProgrammeAdmin(study.getProjectId())) {
                throw new AccessDeniedException(ACCESS_DENIED_MESSAGE + study.getStudyCode());
            }

            GroupRuleBase group = workflow.getGroup(groupId);
            studyGroupingsService.deleteGroup(group);
            workflow.deleteGroup(groupId);
            auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, study.getId(), study.getStudyCode(),
                    "Deleted grouping for " + study.getStudyCode());
            return group;
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when deleting a group: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-refresh-group")
    @ResponseBody
    public GroupRuleBase refreshGroupById(final HttpServletRequest request, @RequestParam("groupId") Long groupId) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
            GroupRuleBase group = workflow.getGroup(groupId);
            if (group.getDataSource() != null && StringUtils.hasLength(group.getDataSource())) {
                group.clearGroupValues();
                GroupingsUtils.readGroupingValuesFromSharedLocation(group, group.getDataSource(), provider);
            }
            return group;
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when refreshing a custom group in a study setup", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-save-group-values")
    @ResponseBody
    public void saveGroupValues(final HttpServletRequest request, @RequestBody SubjectGroupValuesDTO dto) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);

            StudyRule study = workflow.getSelectedStudy();

            if (!permissionHelper.isCurrentUserDrugProgrammeAdmin(study.getProjectId())) {
                throw new AccessDeniedException(ACCESS_DENIED_MESSAGE + study.getStudyCode());
            }

            GroupRuleBase grp = workflow.getGroup(dto.getGroupId());
            studyGroupingsService.deleteGroupValues(grp);
            grp.getValues().clear();
            grp.getValues().addAll(dto.getValues());
            for (GroupValueBase val : grp.getValues()) {
                val.setGroupId(grp.getId());
            }
            studyGroupingsService.saveGroupValues(grp);
            auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, study.getId(), study.getStudyCode(),
                    "Updated grouping values for " + study.getStudyCode());
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when saving group values: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-get-group-values")
    @ResponseBody
    public List<GroupValueBase> getValuesData(final HttpServletRequest request, @RequestParam("groupId") Long groupId) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
            GroupRuleBase grp = workflow.getGroup(groupId);
            return grp.getValues();
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when getting group values: ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-get-projects")
    @ResponseBody
    public List<ProjectRule> getProjectsInACUITY() throws ClinicalStudyException {
        try {
            return clinicalStudyService
                    .getCompletedProjects()
                    .stream()
                    .filter(pr -> permissionHelper.isCurrentUserDrugProgrammeAdmin(pr.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when getting projects : ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-exist")
    @ResponseBody
    public PrimitiveObjWrapper<Boolean> isStudyExist(@RequestParam("studyCode") String studyCode) throws ClinicalStudyException {
        try {
            StudyRule study = clinicalStudyService.getStudyInACUITYByCode(studyCode);
            if (study != null) {
                return new PrimitiveObjWrapper<>(true);
            }
            study = clinicalStudyService.getStudyInCDBPByCode(studyCode);
            if (study != null) {
                return new PrimitiveObjWrapper<>(true);
            }
            return new PrimitiveObjWrapper<>(false);
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when getting projects : ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-add-study")
    @ResponseBody
    public ClinicalStudyWorkflow addStudyInACUITY(final HttpServletRequest request, @RequestBody StudyRule study) throws ClinicalStudyException {
        try {
            HttpSession session = request.getSession();
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            ControllerUtils.clearClinicalStudyWorkflow(session, browserTabId);

            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(session, browserTabId);
            study.setCreatedBy(UserInfoHolder.getUserInfo().getDisplayName());
            Long id = selectClinicalStudy(study, workflow);
            auditService.logAction(AuditAction.CREATE, AuditEntity.STUDY, id, study.getStudyCode(),
                    "Created study rule " + workflow.getSelectedStudy().getStudyCode());
            ControllerUtils.increaseTotalStudiesCount(session);
            return workflow;
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when add study : ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-revert-study")
    @ResponseBody
    public ClinicalStudyWorkflow revertStudyInACUITY(final HttpServletRequest request, @RequestParam("studyCode") String studyCode)
            throws ClinicalStudyException {
        try {
            HttpSession session = request.getSession();
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            ControllerUtils.clearClinicalStudyWorkflow(session, browserTabId);

            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(session, browserTabId);
            StudyRule selectedStudy = Optional
                    .ofNullable(clinicalStudyService.getStudyInACUITYByCode(studyCode))
                    .orElseGet(() -> clinicalStudyService.getStudyInCDBPByCode(studyCode));

            if (selectedStudy != null && !permissionHelper.isCurrentUserDrugProgrammeAdmin(selectedStudy.getProjectId())) {
                throw new AccessDeniedException(ACCESS_DENIED_MESSAGE + workflow.getSelectedStudy().getStudyCode());
            }

            if (selectedStudy == null) {
                throw new ClinicalStudyException("Can't find study by code");
            }

            if (selectedStudy.getStatus() == StudyRule.Status.notInAcuity) {
                workflow.setSelectedStudy(selectedStudy);
                return workflow;
            }

            Long id = selectClinicalStudy(selectedStudy, workflow);
            auditService.logAction(AuditAction.CREATE, AuditEntity.STUDY, id, selectedStudy.getStudyCode(),
                    "Created study rule " + workflow.getSelectedStudy().getStudyCode());

            return workflow;
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when getting exist study : ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup-finish")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public StudyRule studyFinish(final HttpServletRequest request, @RequestParam("confirm") boolean confirm,
                                 @RequestParam("upload") boolean upload) throws ClinicalStudyException {
        try {
            StudyRule study = extractClinicalStudyRuleOrThrowError(request);

            if (study.isStudyValid() != confirm) {
                study.setStudyValid(confirm);
                clinicalStudyService.saveStudy(study);
                syncService.runSynchronizationAsync();
            }
            auditService.logAction(AuditAction.CREATE, AuditEntity.STUDY, study.getId(), study.getStudyCode(),
                    "Completed study rule " + study.getStudyCode());
            studyMappingsService.validateStudyEnabled(study);
            jobServices.forEach(JobService::refreshJobList);
            if (upload && study.isStudyEnabled()) {
                jobServices.stream()
                        .filter(js -> js instanceof ETLJobService)
                        .forEach(js -> js.runJobNow(study.getStudyCode()));
            }
            study.setDrugProgramme(clinicalStudyService.getParentProject(study).getDrugId());
            return study;
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when finish study wizard: ", e);
        }
    }

    @RequestMapping(value = "/study-setup/set-use-alt-lab-codes")
    @ResponseStatus(value = HttpStatus.OK)
    public void setAltLabCodesMode(HttpServletRequest request, boolean use) throws ClinicalStudyException {
        StudyRule study = extractClinicalStudyRuleOrThrowError(request);
        study.setStudyUseAltLabCodes(use);
        clinicalStudyService.saveStudy(study);
        auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, study.getId(), study.getStudyCode(),
                "Updated study rule use alt. labcodes " + study.getStudyCode());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup/get-alt-lab-codes")
    @ResponseBody
    public List<CustomLabcodeLookup> getAltLabCodes(HttpServletRequest request) throws ClinicalStudyException {
        String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
        ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
        return studyAltLabCodeService.getAltLabCodes(workflow.getSelectedStudy().getId());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup/save-alt-lab-codes")
    @ResponseStatus(value = HttpStatus.OK)
    public void saveAltLabCodes(HttpServletRequest request, @RequestBody List<CustomLabcodeLookup> labcodes) throws ClinicalStudyException {
        StudyRule study = extractClinicalStudyRuleOrThrowError(request);

        studyAltLabCodeService.saveAltLabCodes(study.getId(), labcodes);
        auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, study.getId(), study.getStudyCode(),
                "Updated study rule's alt. labcodes " + study.getStudyCode());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup/get-excluding-values")
    @ResponseBody
    public List<ExcludingValue> getExcludingValues(HttpServletRequest request) throws ClinicalStudyException {
        String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

        ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
        List<ExcludingValue> res = workflow.getSelectedStudy().getExcludingValues();
        if (res == null) {
            res = excludingValueService.getExcludingValuesByStudyRule(workflow.getSelectedStudy().getId());
            workflow.getSelectedStudy().setExcludingValues(res);
        }
        return res;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup/save-excluding-values")
    @ResponseStatus(value = HttpStatus.OK)
    public void saveExcludingValues(HttpServletRequest request, @RequestBody List<ExcludingValue> excludingValues) throws ClinicalStudyException {
        StudyRule study = extractClinicalStudyRuleOrThrowError(request);
        excludingValueService.saveStudyExcludingValues(study, excludingValues);
        study.setExcludingValues(excludingValues);
        auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, study.getId(), study.getStudyCode(),
                "Updated study rule's excluding values " + study.getStudyCode());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup/load-study-baseline-drugs")
    @ResponseBody
    public StudyBaselineDrugs getStudyBaselineDrugs(HttpServletRequest request) throws ClinicalStudyException {
        StudyRule studyRule = extractClinicalStudyRuleOrThrowError(request);

        StudyBaselineDrugs studyBaselineDrugs = new StudyBaselineDrugs();
        studyBaselineDrugs.setUseCustomDrugsForBaseline(studyRule.isUseCustomDrugsForBaseline());

        Set<String> drugNames = studyDrugsExplorerService.getStudyDrugNames(studyRule);

        List<StudyBaselineDrug> customBaselineDrugs = studyBaselineDrugService.loadStudyBaselineDrugs(studyRule.getId());

        for (String drugName : drugNames) {
            boolean addAsCustomBaselineDrug = true;
            for (StudyBaselineDrug customBaselineDrug : customBaselineDrugs) {
                if (drugName.equals(customBaselineDrug.getDrugName())) {
                    addAsCustomBaselineDrug = false;
                    break;
                }
            }
            if (addAsCustomBaselineDrug) {
                customBaselineDrugs.add(new StudyBaselineDrug(drugName, false));
            }
        }
        studyBaselineDrugs.setCustomBaselineDrugs(customBaselineDrugs);

        return studyBaselineDrugs;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup/save-study-baseline-drugs")
    @ResponseStatus(HttpStatus.OK)
    public void saveStudyBaselineDrugs(HttpServletRequest request,
                                       @RequestBody StudyBaselineDrugs studyBaselineDrugs) throws ClinicalStudyException {
        StudyRule studyRule = extractClinicalStudyRuleOrThrowError(request);

        studyRule.setUseCustomDrugsForBaseline(studyBaselineDrugs.isUseCustomDrugsForBaseline());

        clinicalStudyService.saveStudy(studyRule);

        studyBaselineDrugService.saveStudyBaselineDrugs(studyRule.getId(), studyBaselineDrugs.getCustomBaselineDrugs());

        auditService.logAction(AuditAction.MODIFY, AuditEntity.STUDY, studyRule.getId(), studyRule.getStudyCode(),
                "Updated study rule use custom drugs for baseline " + studyRule.getStudyCode());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/study-setup/history")
    @ResponseBody
    public AuditController.Audits getHistory(HttpServletRequest request, @RequestBody AuditController.Page page) {
        String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

        ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
        StudyRule studyRule = workflow.getSelectedStudy();

        if (studyRule == null) {
            return new AuditController.Audits(Collections.emptyList(), 0);
        }

        if (!permissionHelper.isCurrentUserDrugProgrammeAdmin(studyRule.getProjectId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE + studyRule.getStudyCode());
        }

        Integer limit = page.getPageSize();
        Integer offset = (page.getPageNum() - 1) * page.getPageSize();
        Audit requestedActions = Audit.builder().resourceType(AuditEntity.STUDY).resourceId(studyRule.getId()).build();
        int count = auditService.getActionCount(requestedActions);
        List<Audit> items = auditService.searchAction(offset, limit, requestedActions);
        return new AuditController.Audits(items, count);
    }


    @RequestMapping(value = "/study-setup/selected-study-subject-groupings")
    @ResponseBody
    public StudySubjectGrouping getStudySubjectGrouping(final HttpServletRequest request) {
        String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

        ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
        StudyRule studyRule = workflow.getSelectedStudy();
        if (studyRule.getId() != null) {
            studyRule.setStudySubjectGrouping(studyGroupingsService.getSelectedStudySubjectGroupings(studyRule.getId()));
        }
        return studyRule.getStudySubjectGrouping();
    }

    @RequestMapping(value = "/study-setup/save-study-subject-groupings", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void saveStudySubjectGroupings(HttpServletRequest request,
                                          @RequestBody StudySubjectGrouping studySubjectGrouping) {
        String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

        ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);

        workflow.getSelectedStudy().setStudySubjectGrouping(studySubjectGrouping);

        studyGroupingsService.saveSelectedStudySubjectGroupings(studySubjectGrouping);
    }

    @RequestMapping(value = "/study-get-project-groupings", method = RequestMethod.POST)
    @ResponseBody
    public Map<GroupRuleBase.ProjectGroupType, List<GroupRuleBase>> getProjectGroupings(final HttpServletRequest request) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
            List<GroupRuleBase> availableProjectGroupings = workflow.getAvailableProjectGroupings();
            availableProjectGroupings.clear();
            Map<GroupRuleBase.ProjectGroupType, List<GroupRuleBase>> result = groupingsService.getProjectGroupings(workflow.getParentProject());
            availableProjectGroupings.addAll(result.get(GroupRuleBase.ProjectGroupType.ae));
            availableProjectGroupings.addAll(result.get(GroupRuleBase.ProjectGroupType.lab));
            return result;
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when getting project grouping at the ACUITY Study Setup", e);
        }
    }

    @RequestMapping(value = "/study-select-project-groupings", method = RequestMethod.POST)
    @ResponseBody
    public List<GroupRuleBase> selectProjectGroupings(final HttpServletRequest request,
                                                      @RequestParam(value = "ids[]", required = false) List<Long> ids) throws ClinicalStudyException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

            ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
            StudyRule studyRule = workflow.getSelectedStudy();

            studyRule.getProjectGroupRules().clear();
            for (GroupRuleBase group : workflow.getAvailableProjectGroupings()) {
                if (ids != null && ids.contains(group.getId())) {
                    studyRule.getProjectGroupRules().add(group);
                }
            }
            studyRelationService.saveProjectGroupingsRelation(studyRule);
            return studyRule.getProjectGroupRules();
        } catch (Exception e) {
            throw new ClinicalStudyException("Exception caught when selecting project grouping at the ACUITY Study Setup", e);
        }
    }

    private StudyRule extractClinicalStudyRuleOrThrowError(final HttpServletRequest request) throws AccessDeniedException {
        String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
        ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);

        StudyRule study = workflow.getSelectedStudy();

        if (!permissionHelper.isCurrentUserDrugProgrammeAdmin(study.getProjectId())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE + study.getStudyCode());
        }
        return study;
    }

}
