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

import com.acuity.visualisations.mapping.AuditAction;
import com.acuity.visualisations.mapping.entity.AuditEntity;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.service.JobService;
import com.acuity.visualisations.web.auth.PermissionHelper;
import com.acuity.visualisations.web.auth.UserInfoHolder;
import com.acuity.visualisations.web.dto.ProjectInfoDTO;
import com.acuity.visualisations.web.dto.StudyInfoDTO;
import com.acuity.visualisations.web.exception.AdminException;
import com.acuity.visualisations.web.service.AdminService;
import com.acuity.visualisations.web.service.AuditService;
import com.acuity.visualisations.web.service.SyncService;
import com.acuity.visualisations.web.service.wizard.instance.ProjectAndStudyService;
import com.acuity.visualisations.web.util.JsonUtil;
import com.acuity.va.security.acl.domain.AcuityObjectIdentity;
import com.acuity.va.security.acl.domain.AcuityObjectIdentityWithPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Controller
@RequestMapping("/admin")
public class AdminController extends AbstractController {

    public static final String VIEW_NAME = "admin";
    private static final String ACCESS_DENIED = "Access denied";

    @Autowired
    private List<JobService> jobServices;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProjectAndStudyService projectAndStudyService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private PermissionHelper permissionHelper;

    @Autowired
    private SyncService syncService;

    @Value("${acuity.vahub.url}")
    private String webappUrl;

    @ResponseStatus(value = HttpStatus.MOVED_PERMANENTLY)
    @RequestMapping(value = "/")
    public String redirectNoSlash() {
        return "redirect:../admin";
    }

    @RequestMapping
    public ModelAndView admin(Authentication authentication) throws IOException {
        final ModelAndView modelAndView = new ModelAndView(VIEW_NAME);

        List<ProjectRule> projectsInAcuity = projectAndStudyService.getAllProjectRulesWithStudyRules();

        if (!permissionHelper.isGlobalAdmin(authentication)) {
            List<AcuityObjectIdentityWithPermission> identities = permissionHelper.getUserIdentities(authentication);

            Set<Long> drugProgrammes = identities.stream()
                    .filter(AcuityObjectIdentity::thisDrugProgrammeType)
                    .filter(AcuityObjectIdentityWithPermission::getCanView)
                    .map(AcuityObjectIdentity::getId)
                    .collect(toSet());

            projectsInAcuity = projectsInAcuity.stream()
                    .filter(projectRule -> drugProgrammes.contains(projectRule.getId()))
                    .collect(toList());
        }

        modelAndView.addObject("projectsInAcuity", JsonUtil.toJson(projectsInAcuity));
        modelAndView.addObject("acuity.vahub.url", JsonUtil.toJson(webappUrl));
        return modelAndView;
    }

    @PreAuthorize("@permissionHelper.isDrugProgrammeAdmin(#projectId, authentication)")
    @RequestMapping("/list-studies-by-project")
    @ResponseBody
    public List<StudyRule> listStudiesByProject(@RequestParam("projectId") Long projectId) throws AdminException {
        try {
            return projectAndStudyService.searchStudiesByProjectId(projectId);
        } catch (Exception e) {
            throw new AdminException("Exception caught when listing studies by project id: ", e);
        }
    }

    @PreAuthorize("@permissionHelper.isGlobalAdmin(authentication)")
    @RequestMapping(value = "/remove-project", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void removeProject(final HttpServletRequest request, @RequestParam("projectId") Long projectId) throws AdminException {
        try {
            ProjectRule projectRule = adminService.getProjectRule(projectId);
            List<String> studyCodes = projectAndStudyService.searchStudiesByProjectId(projectId).stream()
                                                           .map(StudyRule::getStudyCode)
                                                           .collect(toList());
            auditService.logAction(AuditAction.DELETE, AuditEntity.PROJECT, projectId, projectRule.getDrugId(),
                    "Deleted drug project " + projectRule.getDrugId());
            adminService.removeProject(projectId);
            ControllerUtils.decreaseTotalProgrammesCount(request.getSession());
            jobServices.forEach(JobService::refreshJobList);
            if (projectRule.getNumberOfAcuityEnabledStudies() > 0) {
                syncService.runSynchronizationAsync();
                adminService.cleanStudiesRelatedInformationAsync(singletonMap(projectRule.getDrugId(), studyCodes));
            }
        } catch (Exception e) {
            throw new AdminException("Exception caught when removing a project: ", e);
        }
    }

    @RequestMapping(value = "/remove-study", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void removeStudy(final HttpServletRequest request, @RequestParam("studyId") Long studyId) throws AdminException {
        try {
            StudyRule studyRule = adminService.getStudyRule(studyId);

            if (permissionHelper.isCurrentUserDrugProgrammeAdmin(studyRule.getProjectId())) {
                String name = studyRule.getStudyName();
                auditService.logAction(AuditAction.DELETE, AuditEntity.STUDY, studyId, name, "Deleted study " + name);
                adminService.removeStudy(studyId);
                ControllerUtils.decreaseTotalStudiesCount(request.getSession());
                jobServices.forEach(JobService::refreshJobList);
                if (studyRule.isStudyValid()) {
                    adminService.cleanStudiesRelatedInformationAsync(singletonMap(studyRule.getDrugProgramme(),
                            singletonList(studyRule.getStudyCode())));
                    syncService.runSynchronizationAsync();
                }
            } else {
                throw new AccessDeniedException(ACCESS_DENIED);
            }
        } catch (Exception e) {
            throw new AdminException("Exception caught when removing a study: ", e);
        }
    }

    @PreAuthorize("@permissionHelper.isDrugProgrammeAdmin(#projectId, authentication)")
    @RequestMapping(value = "/programme-info", method = RequestMethod.POST)
    @ResponseBody
    public ProjectInfoDTO getProgrammeInformation(@RequestParam("projectId") Long projectId) throws AdminException {
        try {

            ProjectRule projectRule = adminService.getProjectRule(projectId);

            ProjectInfoDTO res = new ProjectInfoDTO(projectRule);
            res.setCanAddStudy(true);
            res.setCanAddSummaryTable(true);
            res.setCanDeleteProject(true);
            res.setCanEditProject(true);
            return res;
        } catch (Exception e) {
            throw new AdminException("Exception caught when retrieving programme information: ", e);
        }
    }

    @RequestMapping(value = "/study-info", method = RequestMethod.POST)
    @ResponseBody
    public StudyInfoDTO getStudyInformation(@RequestParam("studyId") Long studyId) throws AdminException {
        try {
            StudyRule studyRule = adminService.getStudyRule(studyId);

            if (!permissionHelper.isCurrentUserDrugProgrammeAdmin(studyRule.getProjectId())) {
                throw new AccessDeniedException(ACCESS_DENIED);
            }

            StudyInfoDTO out = new StudyInfoDTO(studyRule);
            out.setFileRules(adminService.getFileRules(studyId));
            out.setCanDeleteStudy(true);
            out.setCanEditStudy(true);
            return out;
        } catch (Exception e) {
            throw new AdminException("Exception caught when retrieving study information: ", e);
        }
    }

    @PreAuthorize("@permissionHelper.isDrugProgrammeAdmin(#projectId, authentication)")
    @RequestMapping(value = "/edit-project", method = RequestMethod.POST)
    public String editProject(@RequestParam("projectId") String projectId, RedirectAttributes redirectAttributes) throws AdminException {
        try {
            redirectAttributes.addFlashAttribute("projectId", projectId);
            return "redirect:/programme-setup";
        } catch (Exception e) {
            throw new AdminException("Exception caught when retrieving project information: ", e);
        }
    }

    @RequestMapping(value = "/edit-study", method = RequestMethod.POST)
    public String editStudy(@RequestParam("studyId") String studyId, RedirectAttributes redirectAttributes) throws AdminException {
        try {
            StudyRule studyRule = adminService.getStudyRule(Long.parseLong(studyId));

            if (!permissionHelper.isCurrentUserDrugProgrammeAdmin(studyRule.getProjectId())) {
                throw new AccessDeniedException(ACCESS_DENIED);
            }

            redirectAttributes.addFlashAttribute("studyId", studyId);
            return "redirect:/study-setup";
        } catch (Exception e) {
            throw new AdminException("Exception caught when retrieving study information: ", e);
        }
    }

    @RequestMapping(value = "/user-info", method = RequestMethod.POST)
    @ResponseBody
    public Object getMyInfo() throws AdminException {
        return UserInfoHolder.getUserInfo();

    }


}
