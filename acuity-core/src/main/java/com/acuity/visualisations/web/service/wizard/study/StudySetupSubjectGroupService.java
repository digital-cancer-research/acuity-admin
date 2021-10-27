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

package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.web.service.AdminService;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGroup;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGroupDosing;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGroupDosingSchedule;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGrouping;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGroupingType;
import com.acuity.visualisations.web.service.wizard.study.vo.SubjectGroupings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;

@Service
public class StudySetupSubjectGroupService {

    @Autowired
    private StudySetupSubjectGroupRepository studySetupSubjectGroupRepository;

    @Autowired
    private AdminService adminService;

    public SubjectGroupings selectSubjectGroups(long studyId) {
        List<SubjectGroup> subjectGroups = studySetupSubjectGroupRepository.selectSubjectGroups(studyId);
        List<SubjectGrouping> savedGroupings = studySetupSubjectGroupRepository.selectSavedSubjectGroupings(studyId);

        StudyRule studyRule = adminService.getStudyRule(studyId);

        List<SubjectGrouping> subjectGroupings = new ArrayList<>();

        Map<String, List<SubjectGroup>> groupMap = subjectGroups.stream().collect(Collectors.groupingBy(SubjectGroup::getGroupingName));

        for (Map.Entry<String, List<SubjectGroup>> entry : groupMap.entrySet()) {
            List<SubjectGroup> groups = entry.getValue().stream().filter(g -> g.getGroupName() != null).collect(toList());

            String groupingName = entry.getKey();

            SubjectGrouping subjectGrouping = null;
            for (SubjectGrouping savedGrouping : savedGroupings) {
                if (savedGrouping.getName().equals(groupingName)) {
                    subjectGrouping = savedGrouping;
                    break;
                }
            }
            if (subjectGrouping == null) {
                subjectGrouping = new SubjectGrouping(groupingName);
            }
            subjectGrouping.setGroups(groups);

            if (subjectGrouping.getType() == null) {
                subjectGrouping.setType(SubjectGroupingType.NONE);
            }

            subjectGroupings.add(subjectGrouping);
        }
        return new SubjectGroupings(studyRule.getStudyName(), studyRule.getStudyCode(), subjectGroupings);
    }

    public SubjectGroup selectSubjectGroup(long groupId) {
        return studySetupSubjectGroupRepository.selectSubjectGroup(groupId);
    }

    private SubjectGroup saveSubjectGroup(long subjectGroupingId, SubjectGroup subjectGroup) {
        if (subjectGroup.getId() == null) {
            studySetupSubjectGroupRepository.insertSubjectGroup(subjectGroupingId, subjectGroup);
        } else {
            studySetupSubjectGroupRepository.updateSubjectGroup(subjectGroup);
            studySetupSubjectGroupRepository.deleteSubjectGroupDosings(subjectGroup.getId());
        }

        if (!CollectionUtils.isEmpty(subjectGroup.getDosings())) {
            for (SubjectGroupDosing subjectGroupDosing : subjectGroup.getDosings()) {

                subjectGroupDosing.setDosingContinuity(calculateDosingContinuity(subjectGroupDosing));
                subjectGroupDosing.setDoseSchedule(calculateDoseCohort(subjectGroupDosing));

                studySetupSubjectGroupRepository.insertSubjectGroupDosing(subjectGroup.getId(), subjectGroupDosing);
                if (!CollectionUtils.isEmpty(subjectGroupDosing.getSchedule())) {
                    for (SubjectGroupDosingSchedule subjectGroupDosingSchedule : subjectGroupDosing.getSchedule()) {
                        studySetupSubjectGroupRepository.insertSubjectGroupDosingSchedule(subjectGroupDosing.getId(), subjectGroupDosingSchedule);
                    }
                }
            }
        }

        return studySetupSubjectGroupRepository.selectSubjectGroup(subjectGroup.getId());
    }

    public List<SubjectGroup> saveSubjectGroups(long studyId, List<SubjectGroup> subjectGroups) {
        List<SubjectGroup> out = new ArrayList<>();

        List<SubjectGrouping> savedGroupings = studySetupSubjectGroupRepository.selectSavedSubjectGroupings(studyId);

        List<String> groupingNames = subjectGroups.stream().map(SubjectGroup::getGroupingName).distinct().collect(toList());

        Map<String, Long> groupingNameIdMap = new HashMap<>();

        for (String groupingName : groupingNames) {
            for (SubjectGrouping savedGrouping : savedGroupings) {
                if (savedGrouping.getName().equals(groupingName)) {
                    groupingNameIdMap.put(groupingName, savedGrouping.getId());
                    break;
                }
            }

            if (!groupingNameIdMap.containsKey(groupingName)) {
                SubjectGrouping subjectGrouping = new SubjectGrouping(groupingName);
                studySetupSubjectGroupRepository.insertSubjectGrouping(studyId, subjectGrouping);
                groupingNameIdMap.put(groupingName, subjectGrouping.getId());
            }
        }

        for (SubjectGroup subjectGroup : subjectGroups) {
            out.add(saveSubjectGroup(groupingNameIdMap.get(subjectGroup.getGroupingName()), subjectGroup));
        }
        return out;
    }

    public void deleteSubjectGroup(long groupId) {
        studySetupSubjectGroupRepository.deleteSubjectGroup(groupId);
    }

    /**
     * RCT-3721 Calculate Dose Cohort Continuity
     *
     * @param subjectGroupDosing
     * @return
     */

    protected static String calculateDosingContinuity(SubjectGroupDosing subjectGroupDosing) {
        List<SubjectGroupDosingSchedule> scheduler = subjectGroupDosing.getSchedule();

        // In the case where the user has not completed any table rows for a given dose cohort and drug,
        // the string value should be recorded as "Unspecified".
        if (CollectionUtils.isEmpty(scheduler)) {
            return "Unspecified";
        }

        // If there are no 'on' periods at all, it will be labelled as "Unspecified".
        long dosedCount = scheduler.stream().filter(SubjectGroupDosingSchedule::isDosing).count();

        if (dosedCount == 0) {
            return "Unspecified";
        }

        // If a drug has a period of 'on' dosing with continuous duration it will be labelled as "Continuous".
        if (scheduler.stream().filter(s -> s.isDosing() && (s.getDuration() == null || s.getDuration() == 0)).count() > 0) {
            return "Continuous";
        }

        // If a drug has a repeating cycle that only involves 'on' periods it will be labelled as "Continuous".
        // If a drug has repeated cycling including both 'on' and 'off' periods,
        // it will be labelled as "Cycling intermittent".
        boolean continuous = false;
        boolean repeat = false;
        for (SubjectGroupDosingSchedule schedule : scheduler) {
            if (schedule.isRepeat()) {
                repeat = true;
                if (schedule.isDosing()) {
                    continuous = true;
                } else {
                    continuous = false;
                    break;
                }
            }
        }

        if (repeat && continuous) {
            return "Continuous";
        }

        if (repeat) {
            return "Cycling intermittent";
        }

        // If a drug has no continuous dosing or cycling element involving an 'on' period, has at least one 'on' period,
        // and also has no 'off' periods at all it will be labelled as "Limited dosing".
        // If a drug has no continuous dosing or cycling element involving an 'on' period, has at least one 'on' period,
        // and does have 'off' periods it will be labelled as "Limited intermittent dosing".
        if (dosedCount > 0) {
            if (dosedCount == scheduler.size()) {
                return "Limited dosing";
            } else {
                return "Limited intermittent dosing";
            }
        }

        return "Should never happen";
    }

    /**
     * RCT-3720 Calculate Dose Cohort from Subject Dosing information
     *
     * @return The string value generated by sequentially stepping through each record in the schedule table (shown in the RCT-3628) for a given group and drug
     */
    protected static String calculateDoseCohort(SubjectGroupDosing subjectGroupDosing) {
        if (CollectionUtils.isEmpty(subjectGroupDosing.getSchedule())) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        Boolean repeat = null;

        for (SubjectGroupDosingSchedule schedule : subjectGroupDosing.getSchedule()) {
            if (repeat != null) {
                if (repeat == schedule.isRepeat()) {
                    sb.append("; ");
                } else {
                    sb.append(" | ");
                }
            }

            if (schedule.isRepeat() && (repeat == null || repeat != schedule.isRepeat())) {
                sb.append("Cycle: ");
            }
            if (schedule.isDosing()) {
                sb.append("On ");
            } else {
                sb.append("Off ");
            }

            String optionalSeparator = "";
            if (schedule.getDuration() != null && schedule.getDuration() > 0) {
                sb.append(schedule.getDuration());
                if (!isEmpty(schedule.getDurationUnit())) {
                    sb.append(" ").append(schedule.getDurationUnit().toHumanString(schedule.getDuration()));
                }
                optionalSeparator = ", ";
            }

            if (schedule.isDosing()) {
                if (schedule.getDose() != null && schedule.getDose() > 0) {
                    sb.append(optionalSeparator).append(new DecimalFormat("#.###").format(schedule.getDose()));
                    if (!isEmpty(schedule.getDoseUnit())) {
                        sb.append(schedule.getDoseUnit());
                    }
                    optionalSeparator = ", ";
                }

                if ("Unknown".equals(schedule.getFrequencyTerm())) {
                    sb.append(optionalSeparator).append("Unknown frequency");
                } else if ("Other".equals(schedule.getFrequencyTerm()) || isEmpty(schedule.getFrequencyTerm())) {
                    sb.append(optionalSeparator)
                            .append(schedule.getFrequency() != null && schedule.getFrequency() > 0 ? schedule.getFrequency() : "?")
                            .append("-per-")
                            .append(!isEmpty(schedule.getFrequencyUnit()) ? schedule.getFrequencyUnit().toLowerCase() : "?");
                } else {
                    sb.append(optionalSeparator).append(schedule.getFrequencyTerm());
                }
            }


            repeat = schedule.isRepeat();
        }
        return sb.toString();
    }

    public void saveSubjectGrouping(long studyId, SubjectGrouping subjectGrouping) {
        SubjectGrouping savedSubjectGrouping = studySetupSubjectGroupRepository.selectSavedSubjectGroupingByName(studyId, subjectGrouping.getName());
        if (savedSubjectGrouping == null) {
            studySetupSubjectGroupRepository.insertSubjectGrouping(studyId, subjectGrouping);
        } else {
            subjectGrouping.setId(savedSubjectGrouping.getId());
            studySetupSubjectGroupRepository.updateSubjectGrouping(subjectGrouping);
        }
    }

    public List<SubjectGroupingType> listSubjectGroupingTypes() {
        return studySetupSubjectGroupRepository.selectSubjectGroupingTypes().stream()
                .sorted(Comparator.comparing(SubjectGroupingType::getId)).collect(Collectors.toList());
    }
}
