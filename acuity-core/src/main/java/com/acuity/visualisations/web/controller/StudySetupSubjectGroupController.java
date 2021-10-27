package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.web.service.wizard.study.StudySetupSubjectGroupService;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGroup;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGrouping;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGroupingType;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGroupings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Created by knml167 on 20/05/14.
 */
@Controller
@RequestMapping("/studygroupingssetup")
public class StudySetupSubjectGroupController {

    @Autowired
    private StudySetupSubjectGroupService studySetupSubjectGroupService;

    @RequestMapping(value = "/subject-grouping-types", method = RequestMethod.GET)
    @ResponseBody
    public List<SubjectGroupingType> listSubjectGroupingTypes() {
        return studySetupSubjectGroupService.listSubjectGroupingTypes();
    }

    @RequestMapping(value = "/{studyId}/subjectgroups", method = RequestMethod.GET)
    @ResponseBody
    public SubjectGroupings getSubjectGroupings(@PathVariable("studyId") long studyId) {
        return studySetupSubjectGroupService.selectSubjectGroups(studyId);
    }

    @RequestMapping(value = "/{studyId}/subjectgroups/{groupId}", method = RequestMethod.GET)
    @ResponseBody
    public SubjectGroup getSubjectGroup(@PathVariable("studyId") long studyId, @PathVariable("groupId") long groupId) {
        return studySetupSubjectGroupService.selectSubjectGroup(groupId);
    }

    @RequestMapping(value = "/{studyId}/subjectgroups", method = RequestMethod.POST)
    @ResponseBody
    public List<SubjectGroup> saveSubjectGroups(@PathVariable("studyId") long studyId, @RequestBody List<SubjectGroup> subjectGroups) {
        return studySetupSubjectGroupService.saveSubjectGroups(studyId, subjectGroups);
    }

    @RequestMapping(value = "{studyId}/subjectgroups/save-subject-grouping", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void saveSubjectGrouping(@PathVariable("studyId") long studyId, @RequestBody SubjectGrouping subjectGrouping) {
        studySetupSubjectGroupService.saveSubjectGrouping(studyId, subjectGrouping);
    }

    @RequestMapping(value = "/{studyId}/subjectgroups/{groupId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteSubjectGroup(@PathVariable("studyId") long studyId, @PathVariable("groupId") long groupId) {
        studySetupSubjectGroupService.deleteSubjectGroup(groupId);
    }

}
