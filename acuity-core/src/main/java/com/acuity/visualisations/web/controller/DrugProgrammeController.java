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
import com.acuity.visualisations.mapping.AeSeverityType;
import com.acuity.visualisations.mapping.AuditAction;
import com.acuity.visualisations.mapping.entity.AEGroupRule;
import com.acuity.visualisations.mapping.entity.AEGroupValueRule;
import com.acuity.visualisations.mapping.entity.AuditEntity;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.GroupRuleBase.ProjectGroupType;
import com.acuity.visualisations.mapping.entity.GroupValueBase;
import com.acuity.visualisations.mapping.entity.LabGroupRule;
import com.acuity.visualisations.mapping.entity.LabGroupValueRule;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.web.auth.UserInfoHolder;
import com.acuity.visualisations.web.dto.DrugProgrammeGroupValuesDTO;
import com.acuity.visualisations.web.dto.GroupRuleDTO;
import com.acuity.visualisations.web.dto.PrimitiveObjWrapper;
import com.acuity.visualisations.web.dto.ProjectGroupValueDTO;
import com.acuity.visualisations.web.exception.DrugProgrammeException;
import com.acuity.visualisations.web.service.AdminService;
import com.acuity.visualisations.web.service.AuditService;
import com.acuity.visualisations.web.service.wizard.programme.DrugProgrammeWizardService;
import com.acuity.visualisations.web.service.wizard.programme.ProgrammeGroupingsService;
import com.acuity.visualisations.web.util.Consts;
import com.acuity.visualisations.web.util.GroupingsUtils;
import com.acuity.visualisations.web.util.JsonUtil;
import com.acuity.visualisations.web.workflow.DrugProgramWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.google.common.collect.Lists.newArrayList;

@PreAuthorize("@permissionHelper.isGlobalAdmin(authentication)")
@Controller
@RequestMapping("/programme-setup")
@Slf4j
public class DrugProgrammeController extends AbstractController {
    public static final String VIEW_NAME = "programme-setup/main";

    public static final String EDIT_PROGRAMME_WORKFLOW = "editProgrammeWorkflow";
    public static final String PROGRAMME_SEARCH_RESULT = "programmeSearchResult";
    private static final String TAB_ID = "TabId";


    @Autowired
    private DrugProgrammeWizardService drugProgrammeService;

    @Autowired
    private ProgrammeGroupingsService programmeGroupingsService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private DataProvider provider;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView addDrugProject(final HttpServletRequest request,
                                       @ModelAttribute("projectId") String editProjectId, @ModelAttribute("drugId") String editDrugId)
            throws DrugProgrammeException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            final ModelAndView modelAndView = new ModelAndView(VIEW_NAME);
            HttpSession session = request.getSession();
            if (StringUtils.hasText(editProjectId)) {
                ControllerUtils.clearProgrammeWorkflow(session, browserTabId);
                DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(session, browserTabId);
                ProjectRule selectedProject = drugProgrammeService.getProjectById(Long.parseLong(editProjectId));
                SortedMap<String, ProjectRule> projects = new TreeMap<String, ProjectRule>();
                projects.put(selectedProject.getDrugId(), selectedProject);
                ControllerUtils.setProgrammesSearchResult(session, projects);
                selectProject(selectedProject, workflow);
                modelAndView.addObject(EDIT_PROGRAMME_WORKFLOW, JsonUtil.toJson(workflow));
            }
            Integer totalProgrammesCount = ControllerUtils.getTotalProgrammesCount(session);
            if (totalProgrammesCount == null) {
                totalProgrammesCount = drugProgrammeService.getTotalDrugProgrammesCount();
                ControllerUtils.setTotalProgrammesCount(session, totalProgrammesCount);
            }
            if (StringUtils.hasText(editDrugId)) {
                ControllerUtils.clearProgrammeWorkflow(session, browserTabId);
                SortedMap<String, ProjectRule> projects = drugProgrammeService.searchProject(editDrugId);
                ControllerUtils.setProgrammesSearchResult(session, projects);
                modelAndView.addObject(PROGRAMME_SEARCH_RESULT, JsonUtil.toJson(new ArrayList<ProjectRule>(projects.values())));
            }
            return modelAndView;
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when get drug programme ", e);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/programme-search")
    @ResponseBody
    public List<ProjectRule> searchProject(@RequestParam("query") String query, final HttpServletRequest request) throws DrugProgrammeException {
        try {
            HttpSession session = request.getSession();
            Map<String, ProjectRule> projects = ControllerUtils.getProgrammesSearchResult(session);
            if (CollectionUtils.isEmpty(projects)) {
                return new ArrayList<>();
            }
            return newArrayList(projects.values());
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when searching for drug programme " + query, e);
        }
    }

    /**
     * Searches for drug programme in the CDBP database by ACTIVE_SUBSTANCE, PROJECT_CODE or ACTIVITY_CODE.
     *
     * @param query   String that is entered in the UI
     * @param request Request information
     * @throws DrugProgrammeException
     */
    @RequestMapping(method = RequestMethod.POST, value = "/programme-run-search")
    @ResponseStatus(value = HttpStatus.OK)
    public void search(@RequestParam("query") String query, final HttpServletRequest request) throws DrugProgrammeException {
        try {
            HttpSession session = request.getSession();
            List<ProjectRule> acuityResults = new ArrayList<>();
            List<ProjectRule> cdbpResults = new ArrayList<>();
            ControllerUtils.setACUITYResults(session, acuityResults);
            ControllerUtils.setCDBPResults(session, cdbpResults);
            String queryId = drugProgrammeService.runSearchQuery(query, acuityResults, cdbpResults);
            ControllerUtils.setQueryId(session, queryId);
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when searching for drug programme " + query, e);
        }
    }


    @RequestMapping(value = "/programme-cancel-query", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void cancelQuery(final HttpServletRequest request) throws DrugProgrammeException {
        String queryId = ControllerUtils.getQueryId(request.getSession());
        drugProgrammeService.cancelQuery(queryId);
    }

    @RequestMapping(value = "/programme-get-search-result", method = RequestMethod.POST)
    @ResponseBody
    public List<ProjectRule> getSearchProgrammesResult(final HttpServletRequest request) throws DrugProgrammeException {
        try {
            HttpSession session = request.getSession();
            List<ProjectRule> acuityResults = ControllerUtils.getACUITYResults(session);
            List<ProjectRule> cdbpResults = ControllerUtils.getCDBPResults(session);
            SortedMap<String, ProjectRule> projects = drugProgrammeService.getSearchProjectResult(acuityResults, cdbpResults);
            ControllerUtils.setProgrammesSearchResult(request.getSession(), projects);
            return newArrayList(projects.values());
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when searching for drug programme", e);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/programme-query-status")
    @ResponseBody
    public DeferredResult<Boolean> getQueryStatus(final HttpServletRequest request) throws DrugProgrammeException {
        try {
            HttpSession session = request.getSession();
            final String queryId = ControllerUtils.getQueryId(session);
            final DeferredResult<Boolean> result = new DeferredResult<Boolean>(2 * 60000L, false);
            drugProgrammeService.getStatus(queryId, result);
            return result;
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception when checking query status", e);
        }
    }

    /**
     * Updates a drug programme and sets Admin and Data Owner values
     *
     * @param request        Request information
     * @param updatedProject Project that contains updated Admin and Data Owner values
     * @throws DrugProgrammeException
     */
    @RequestMapping(value = "/programme-edit", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void editProject(final HttpServletRequest request, @RequestBody ProjectRule updatedProject) throws DrugProgrammeException {
        String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
        String drugProgrammeName = updatedProject.getDrugProgrammeName();
        boolean createDashboard = updatedProject.isCreateDashboard();
        AeSeverityType aeSeverityType = updatedProject.getAeSeverityType();
        try {
            DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(request.getSession(), browserTabId);
            ProjectRule project = workflow.getSelectedProject();
            project.setDrugProgrammeName(drugProgrammeName);
            project.setCreateDashboard(createDashboard);
            project.setAeSeverityType(aeSeverityType);
            Long id;
            if (project.getId() == null) {
                id = drugProgrammeService.saveProject(project);
                auditService.logAction(AuditAction.CREATE, AuditEntity.PROJECT, id, project.getDrugId(), "Added drug project " + id);
            } else {
                id = drugProgrammeService.saveProject(project);
                auditService.logAction(AuditAction.CREATE, AuditEntity.PROJECT, project.getId(), project.getDrugId(), "Updated drug project " + id);
            }

        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when editing a drug programme parameters", e);
        }
    }

    private void selectProject(ProjectRule selectedProject, DrugProgramWorkflow workflow) {
        assert selectedProject != null;

        selectedProject.setNumberOfAcuityEnabledStudies(adminService.getDrugStudyCount(selectedProject.getDrugId()));
        selectedProject.setAcuityEnabled(true);
        selectedProject.setAeSeverityType(Optional.ofNullable(selectedProject.getAeSeverityType()).orElse(AeSeverityType.CTC_GRADES));
        workflow.setSelectedProject(selectedProject);
        if (selectedProject.getId() != null) {
            Long selectedProjectId = selectedProject.getId();
            List<GroupRuleBase> groups = programmeGroupingsService.getProjectGroups(selectedProjectId);
            workflow.setGroupings(groups);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/programme-select")
    @ResponseBody
    public DrugProgramWorkflow selectProject(final HttpServletRequest request, @RequestParam("drugProgramme") String drugProgramme)
            throws DrugProgrammeException {
        try {
            HttpSession session = request.getSession();
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            Map<String, ProjectRule> searchResult = ControllerUtils.getProgrammesSearchResult(session);

            ControllerUtils.clearProgrammeWorkflow(session, browserTabId);
            DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(session, browserTabId);
            ProjectRule selectedProject = searchResult.get(drugProgramme);
            selectProject(selectedProject, workflow);
            drugProgrammeService.saveProject(selectedProject);
            return workflow;
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when selecting a drug programme", e);
        }

    }

    // grouping methods

    @RequestMapping(method = RequestMethod.POST, value = "/programme-save-group")
    public void addGroup(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(required = false) MultipartFile upload)
            throws DrugProgrammeException {
        try {
            response.setContentType("text/plain");
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(request.getSession(), browserTabId);

            GroupRuleBase group = request.getParameter("groupType").equals("ae") ? new AEGroupRule() : new LabGroupRule();

            fillGroupFromRequest(request, group);

            String remoteLocation = request.getParameter("remoteLocation");

            String sourceType = request.getParameter("dataSource");

            group.setParentId(workflow.getSelectedProject().getId());
            String message = programmeGroupingsService.updateGroup(group, sourceType, remoteLocation, upload);
            auditService.logAction(AuditAction.CREATE, AuditEntity.PROJECT, workflow.getSelectedProject().getId(), workflow.getSelectedProject().getDrugId(),
                    "Added custom grouping for " + workflow.getSelectedProject().getDrugId());
            if (StringUtils.hasText(message)) {
                writeGroupToResponse(response, new GroupRuleDTO(group, message));
                return;
            }
            workflow.addGrouping(group);

            writeGroupToResponse(response, new GroupRuleDTO(group, Consts.EMPTY_STRING));
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when saving a custom group in a drug programme", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/programme-edit-group")
    public void editGroup(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(required = false) MultipartFile upload)
            throws DrugProgrammeException {
        Writer writer = null;
        try {
            response.setContentType("text/plain");
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

            DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(request.getSession(), browserTabId);
            GroupRuleBase group = ControllerUtils.getProgrammesEditGroup(request.getSession());

            fillGroupFromRequest(request, group);

            String sourceType = request.getParameter("dataSource");
            String remoteLocation = request.getParameter("remoteLocation");

            String message = programmeGroupingsService.updateGroup(group, sourceType, remoteLocation, upload);
            auditService.logAction(AuditAction.MODIFY, AuditEntity.PROJECT, workflow.getSelectedProject().getId(), workflow.getSelectedProject().getDrugId(),
                    "Updated custom grouping for " + workflow.getSelectedProject().getDrugId());
            if (StringUtils.hasText(message)) {
                writeGroupToResponse(response, new GroupRuleDTO(group, message));
                return;
            }
            workflow.editGrouping(group);
            writeGroupToResponse(response, new GroupRuleDTO(group, Consts.EMPTY_STRING));
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when editing a custom group in a drug programme", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.warn("Writer was not closed", e);
                }
            }
        }
    }

    private void fillGroupFromRequest(final HttpServletRequest request, GroupRuleBase group) {
        group.setName(request.getParameter("name"));
        group.setDefaultValue(request.getParameter("defaultValue"));
        group.setHeaderRow(Boolean.parseBoolean(request.getParameter("headerRow")));
        group.setReady(Boolean.parseBoolean((request.getParameter("ready"))));
    }

    private void writeGroupToResponse(final HttpServletResponse response, GroupRuleDTO group) throws Exception {
        Writer writer = null;
        try {
            String result = JsonUtil.toJson(group);
            writer = response.getWriter();
            writer.write(result);
            writer.flush();
        } catch (Exception e) {
            throw new DrugProgrammeException("Error writing group to response", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.warn("Writer was not closed", e);
                }
            }
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/programme-delete-group")
    @ResponseBody
    public GroupRuleBase deleteGroup(final HttpServletRequest request, @RequestParam("groupId") Long groupId, @RequestParam("groupType") String groupType)
            throws DrugProgrammeException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(request.getSession(), browserTabId);
            GroupRuleBase grp = workflow.getGroup(groupId.toString() + groupType);
            programmeGroupingsService.deleteGroup(grp);
            auditService.logAction(AuditAction.DELETE, AuditEntity.PROJECT, workflow.getSelectedProject().getId(), workflow.getSelectedProject().getDrugId(),
                    "Deleted custom grouping for " + workflow.getSelectedProject().getDrugId());
            workflow.deleteGroup(groupId.toString() + groupType);
            return grp;
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when deleting a custom group in a drug programme", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/programme-get-group")
    @ResponseBody
    public GroupRuleBase getGroupById(final HttpServletRequest request, @RequestParam("groupId") Long groupId, @RequestParam("groupType") String groupType)
            throws DrugProgrammeException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(request.getSession(), browserTabId);
            GroupRuleBase grp = workflow.getGroup(groupId.toString() + groupType);
            ControllerUtils.setProgrammesEditGroup(request.getSession(), grp);
            return grp;
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when getting a custom group in a drug programme", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/programme-refresh-group")
    @ResponseBody
    public GroupRuleBase refreshGroupById(final HttpServletRequest request, @RequestParam("groupId") Long groupId, @RequestParam("groupType") String groupType)
            throws DrugProgrammeException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(request.getSession(), browserTabId);
            GroupRuleBase group = workflow.getGroup(groupId.toString() + groupType);
            if (group.getDataSource() != null && StringUtils.hasLength(group.getDataSource())) {
                group.clearGroupValues();
                GroupingsUtils.readGroupingValuesFromSharedLocation(group, group.getDataSource(), provider);
            }
            return group;
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when refreshing a custom group in a drug programme", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/programme-save-group-values")
    @ResponseStatus(HttpStatus.OK)
    public void saveGroupValues(final HttpServletRequest request, @RequestBody DrugProgrammeGroupValuesDTO data) throws DrugProgrammeException {
        try {
            DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(request.getSession(), WebUtils.getCookie(request, TAB_ID).getValue());
            GroupRuleBase grp = workflow.getGroup(data.getId().toString() + data.getGroupType());
            grp.getValues().clear();
            for (ProjectGroupValueDTO val : data.getValues()) {
                if (grp.getType() == ProjectGroupType.ae) {
                    AEGroupValueRule aeValueRule = new AEGroupValueRule();
                    aeValueRule.setGroupId(grp.getId());
                    aeValueRule.setName(val.getName());
                    aeValueRule.setPt(val.getPt());
                    grp.getValues().add(aeValueRule);
                } else {
                    LabGroupValueRule labValueRule = new LabGroupValueRule();
                    labValueRule.setGroupId(grp.getId());
                    labValueRule.setName(val.getName());
                    labValueRule.setLabCode(val.getLabCode());
                    labValueRule.setDescription(val.getDescription());
                    grp.getValues().add(labValueRule);
                }
            }
            programmeGroupingsService.saveGroupValues(grp);
            auditService.logAction(AuditAction.MODIFY, AuditEntity.PROJECT, workflow.getSelectedProject().getId(), workflow.getSelectedProject().getDrugId(),
                    "Saved custom grouping values for " + workflow.getSelectedProject().getDrugId());
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when saving a custom group values in a drug programme", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/programme-delete-group-values")
    @ResponseStatus(HttpStatus.OK)
    public void deleteGroupValues(final HttpServletRequest request, @RequestBody DrugProgrammeGroupValuesDTO data) throws DrugProgrammeException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
            DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(request.getSession(), browserTabId);
            GroupRuleBase grp = workflow.getGroup(data.getId().toString() + data.getGroupType());
            grp.getValues().clear();
            for (ProjectGroupValueDTO val : data.getValues()) {
                if (grp.getType() == ProjectGroupType.ae) {
                    AEGroupValueRule aeValueRule = new AEGroupValueRule();
                    aeValueRule.setGroupId(grp.getId());
                    aeValueRule.setName(val.getName());
                    aeValueRule.setPt(val.getPt());
                    grp.getValues().add(aeValueRule);
                } else {
                    LabGroupValueRule labValueRule = new LabGroupValueRule();
                    labValueRule.setGroupId(grp.getId());
                    labValueRule.setName(val.getName());
                    labValueRule.setLabCode(val.getLabCode());
                    labValueRule.setDescription(val.getDescription());
                    grp.getValues().add(labValueRule);
                }
            }
            programmeGroupingsService.deleteGroupValues(grp);
            auditService.logAction(AuditAction.MODIFY, AuditEntity.PROJECT, workflow.getSelectedProject().getId(), workflow.getSelectedProject().getDrugId(),
                    "Saved custom grouping values for " + workflow.getSelectedProject().getDrugId());
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when saving a custom group values in a drug programme", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/programme-get-group-values")
    @ResponseBody
    public List<GroupValueBase> getValuesData(final HttpServletRequest request,
                                              @RequestParam("groupId") Long groupId,
                                              @RequestParam("groupType") String groupType)
            throws DrugProgrammeException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

            DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(request.getSession(), browserTabId);
            GroupRuleBase grp = workflow.getGroup(groupId.toString() + groupType);
            return grp.getValues();
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when getting a custom group values in a drug programme", e);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = "/programme-setup-exist")
    @ResponseBody
    public PrimitiveObjWrapper<Boolean> isProgrammeExist(final HttpServletRequest request,
                                                         @RequestParam("drugId") String drugId)
            throws DrugProgrammeException {
        try {
            boolean exist = drugProgrammeService.isProjectExistByDrug(drugId);
            if (exist) {
                return new PrimitiveObjWrapper<Boolean>(exist);
            }
            ProjectRule project = drugProgrammeService.getProjectInCDBPByCode(drugId);
            if (project != null) {
                return new PrimitiveObjWrapper<Boolean>(true);
            }
            return new PrimitiveObjWrapper<Boolean>(false);
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when getting project : ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/programme-setup-add-programme")
    @ResponseBody
    public DrugProgramWorkflow addProgrammeInACUITY(final HttpServletRequest request, @RequestBody ProjectRule project) throws DrugProgrammeException {
        try {
            HttpSession session = request.getSession();
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

            ControllerUtils.clearProgrammeWorkflow(session, browserTabId);
            DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(session, browserTabId);
            project.setCreatedBy(UserInfoHolder.getUserInfo().getDisplayName());
            project.setCreationDate(new Date());
            selectProject(project, workflow);
            Long id = drugProgrammeService.saveProject(project);
            auditService.logAction(AuditAction.CREATE, AuditEntity.PROJECT, id, project.getDrugId(), "Added drug project " + id);
            ControllerUtils.increaseTotalProgrammesCount(session);
            return workflow;
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when add programme : ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/programme-setup-revert-programme")
    @ResponseBody
    public DrugProgramWorkflow revertStudyInACUITY(final HttpServletRequest request, @RequestParam("drugId") String drugId) throws DrugProgrammeException {
        try {
            String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();

            ControllerUtils.clearProgrammeWorkflow(request.getSession(), browserTabId);
            DrugProgramWorkflow workflow = ControllerUtils.getProgrammeWorkflow(request.getSession(), browserTabId);
            ProjectRule project = drugProgrammeService.getProjectByDrug(drugId);
            if (project == null) {
                project = drugProgrammeService.getProjectInCDBPByCode(drugId);
            }

            if (project == null) {
                throw new DrugProgrammeException("Can't find programme by drug");
            }
            selectProject(project, workflow);
            if (project.getId() == null) {
                project.setAcuityEnabled(true);
                drugProgrammeService.saveProject(project);
                workflow.setSelectedProject(project);
            }
            return workflow;
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception caught when getting exist programme : ", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/programme-get-summary")
    @ResponseBody
    public DrugProgramWorkflow getSummaryData(final HttpServletRequest request) {
        return ControllerUtils.getProgrammeWorkflow(request.getSession(), WebUtils.getCookie(request, TAB_ID).getValue());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/programme-total-count")
    @ResponseBody
    public Integer getProgrammesCount(final HttpServletRequest request) {
        return ControllerUtils.getTotalProgrammesCount(request.getSession());
    }

    /**
     * Sends an email to a ACUITY system admin when a new ACUITY-enabled drug programme is created
     *
     * @param request The HTTP request.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/programme-email")
    @ResponseStatus(value = HttpStatus.OK)
    public void sendProjectCreationEmail(final HttpServletRequest request) throws DrugProgrammeException {
        try {
            ProjectRule selectedProject = ControllerUtils.getProgrammeWorkflow(
                    request.getSession(),
                    WebUtils.getCookie(request, TAB_ID).getValue()
            )
                    .getSelectedProject();
            if (selectedProject.getId() != null && selectedProject.getCompleted() != DrugProgrammeWizardService.PROJECT_COMPLETED) {
                selectedProject.setCompleted(DrugProgrammeWizardService.PROJECT_COMPLETED);
                drugProgrammeService.projectCompleted(selectedProject.getId());
                String name = selectedProject.getDrugId();
                auditService.logAction(AuditAction.MODIFY, AuditEntity.PROJECT, selectedProject.getId(), name, "Project " + name + " completed");
            }
        } catch (Exception e) {
            throw new DrugProgrammeException("Exception when sending programme creation email", e);
        }
    }


}
