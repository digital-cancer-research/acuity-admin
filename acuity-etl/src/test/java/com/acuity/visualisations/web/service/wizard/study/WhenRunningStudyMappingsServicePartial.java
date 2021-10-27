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

import java.util.Arrays;
import java.util.Collections;

import com.acuity.visualisations.mapping.dao.IMappingRuleDao;
import com.acuity.visualisations.mapping.dao.IRelationDao;
import com.acuity.visualisations.mapping.dao.impl.StudyRuleDao;
import com.acuity.visualisations.mapping.entity.*;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;


@RunWith(JUnitParamsRunner.class)
public class WhenRunningStudyMappingsServicePartial {

    @InjectMocks
    StudyMappingsServicePartial service;

    @Mock
    StudyRuleDao studyRuleDao;

    @Mock
    IRelationDao relationDao;

    @Mock
    IMappingRuleDao mappingRuleDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void thenStudyRuleShouldNotBeUpdatedOnValidateStudy_IfItHasValidFileRule() {
        StudyRule studyRule = new StudyRule();
        studyRule.setStudyCompleted(true);
        studyRule.setStudyValid(true);
        studyRule.setStudyEnabled(true);

        FileRule fileRule1 = new FileRule();
        fileRule1.setAcuityEnabled(true);
        fileRule1.getDescriptions().add(new FileDescription() {{
            setSectionId(FileSection.PATIENT_INFORMATION_SECTION);
        }});

        studyRule.setFileRules(Arrays.asList(fileRule1));
        service.validateStudyEnabled(studyRule);

        verifyZeroInteractions(studyRuleDao);
    }

    @Test
    public void thenStudyRuleShouldBeUpdatedOnValidateStudy_IfItHasNotValidFileRule() {
        StudyRule studyRule = new StudyRule();
        studyRule.setStudyCompleted(true);
        studyRule.setStudyValid(true);
        studyRule.setStudyEnabled(true);

        FileRule fileRule1 = new FileRule();
        fileRule1.setAcuityEnabled(false);
        fileRule1.getDescriptions().add(new FileDescription() {{
            setSectionId(FileSection.PATIENT_INFORMATION_SECTION);
        }});

        studyRule.setFileRules(Arrays.asList(fileRule1));
        service.validateStudyEnabled(studyRule);

        verify(studyRuleDao).update(any());
        verifyNoMoreInteractions(studyRuleDao);
    }

    @Test
    public void thenStudyRuleShouldNotBeUpdatedOnValidateStudy_IfItHasOneValidFileRuleAndOneInvalid() {
        StudyRule studyRule = new StudyRule();
        studyRule.setStudyCompleted(true);
        studyRule.setStudyValid(true);
        studyRule.setStudyEnabled(true);

        FileRule fileRule1 = new FileRule();
        fileRule1.setAcuityEnabled(true);
        fileRule1.getDescriptions().add(new FileDescription() {{
            setId(101L);
            setSectionId(FileSection.PATIENT_INFORMATION_SECTION);
        }});

        FileRule fileRule2 = new FileRule();
        fileRule2.setAcuityEnabled(false);
        fileRule2.getDescriptions().add(new FileDescription() {{
            setId(101L);
            setSectionId(FileSection.PATIENT_INFORMATION_SECTION);
        }});

        studyRule.setFileRules(Arrays.asList(fileRule1, fileRule2));

        service.validateStudyEnabled(studyRule);

        verifyZeroInteractions(studyRuleDao);
    }

    @Test
    public void thenMappingRuleDeleteOperationShouldFail_IfArgumentsIsNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("rule");
        service.deleteMappingRule(null);
    }

    @Test
    public void thenMappingRuleDeleteOperationShouldJaveNoDaoInteractions_IfArgumentsHasEmptyOrNullColumnRules() {
        MappingRule rule = new MappingRule();
        service.deleteMappingRule(rule);
        rule.setColumnRules(Collections.emptyList());
        service.deleteMappingRule(rule);
        verifyZeroInteractions(relationDao);
    }
}
