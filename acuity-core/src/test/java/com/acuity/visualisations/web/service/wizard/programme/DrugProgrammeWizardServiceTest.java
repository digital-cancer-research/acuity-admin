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

package com.acuity.visualisations.web.service.wizard.programme;

import com.acuity.visualisations.mapping.dao.IProjectRuleDao;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.web.dao.cdbp.CDBPProjectDao;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.emptySortedMap;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author adavliatov.
 * @since 28.11.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class DrugProgrammeWizardServiceTest {

    @InjectMocks
    private DrugProgrammeWizardService wizardService = new DrugProgrammeWizardService();

    @Mock
    private IProjectRuleDao projectRuleDao;
    @Mock
    private CDBPProjectDao cdbpProjectDao;

    @Before
    public void before() {
        when(projectRuleDao.searchByDrugs(null)).thenReturn(emptyList());
        when(projectRuleDao.searchByDrugs(emptyList())).thenReturn(emptyList());

    }

    @Test
    @SneakyThrows
    public void getSearchProjectResult_Empty() {
        assertEquals(wizardService.getSearchProjectResult(null, null), emptySortedMap());
        assertEquals(wizardService.getSearchProjectResult(emptyList(), emptyList()), emptySortedMap());
        assertEquals(wizardService.getSearchProjectResult(null, emptyList()), emptySortedMap());
        assertEquals(wizardService.getSearchProjectResult(emptyList(), emptyList()), emptySortedMap());
        verify(projectRuleDao, times(4)).searchByDrugs(emptySet());
        verifyZeroInteractions(cdbpProjectDao);
    }

    @Test
    public void getSearchProjectResult_OnlyAcuityProjectListIsNotEmpty() throws Exception {
        StudyRule sr1 = new StudyRule();
        sr1.setId(1L);
        sr1.setStudyCode("sr1");

        StudyRule sr2 = new StudyRule();
        sr1.setId(2L);
        sr1.setStudyCode("sr2");

        StudyRule sr3 = new StudyRule();
        sr1.setId(3L);
        sr1.setStudyCode("sr3");


        ProjectRule acuityPrj1 = new ProjectRule();
        acuityPrj1.setDrugId("prj1");
        acuityPrj1.setStudyRules(asList(sr1, sr2));

        ProjectRule acuityPrj2 = new ProjectRule();
        acuityPrj2.setDrugId("prj2");
        acuityPrj2.setStudyRules(singletonList(sr3));

        List<ProjectRule> acuityPrj = asList(acuityPrj1, acuityPrj2);

        Map<String, ProjectRule> result = new TreeMap<>();
        result.put(acuityPrj2.getDrugId(), acuityPrj2);
        result.put(acuityPrj1.getDrugId(), acuityPrj1);


        when(projectRuleDao.searchByDrugs(asList("prj1", "prj2"))).thenReturn(asList(acuityPrj1, acuityPrj2));
        assertEquals(wizardService.getSearchProjectResult(acuityPrj, null), result);

        verifyZeroInteractions(cdbpProjectDao);
    }

    @Test
    public void getSearchProjectResult_OnlyCdbpProjectListIsNotEmpty() throws Exception {
        ProjectRule cdbpPrj1 = new ProjectRule();
        cdbpPrj1.setDrugId("prj1");
        cdbpPrj1.setTotalStudyCount(10);
        cdbpPrj1.setNumberOfAcuityEnabledStudies(0);
        ProjectRule cdbpPrj2 = new ProjectRule();
        cdbpPrj2.setDrugId("prj2");
        cdbpPrj2.setTotalStudyCount(20);
        cdbpPrj2.setNumberOfAcuityEnabledStudies(0);


        List<ProjectRule> cdbpPrj = asList(cdbpPrj1, cdbpPrj2);

        Map<String, ProjectRule> result = new TreeMap<>();
        result.put(cdbpPrj1.getDrugId(), cdbpPrj1);
        result.put(cdbpPrj2.getDrugId(), cdbpPrj2);

        //Regardless what projectRuleDao returns (here we assume projectRuleDao returns the same but in reality it doesn't)
        when(projectRuleDao.searchByDrugs(asList("prj1", "prj2"))).thenReturn(asList(cdbpPrj1, cdbpPrj2));
        assertEquals(wizardService.getSearchProjectResult(null, cdbpPrj), result);

        when(projectRuleDao.searchByDrugs(asList("prj1", "prj2"))).thenReturn(singletonList(cdbpPrj1));
        assertEquals(wizardService.getSearchProjectResult(null, cdbpPrj), result);

        when(projectRuleDao.searchByDrugs(asList("prj1", "prj2"))).thenReturn(emptyList());
        assertEquals(wizardService.getSearchProjectResult(null, cdbpPrj), result);

        verifyZeroInteractions(cdbpProjectDao);
    }

    @Test
    public void getSearchProjectResult_BothListsAreNotEmptyAndProperlyMerged() throws Exception {

        //fill acuity related
        StudyRule sr1 = new StudyRule();
        sr1.setId(1L);
        sr1.setStudyCode("sr1");
        StudyRule sr2 = new StudyRule();
        sr1.setId(2L);
        sr1.setStudyCode("sr2");
        StudyRule sr3 = new StudyRule();
        sr1.setId(3L);
        sr1.setStudyCode("sr3");
        StudyRule sr4 = new StudyRule();
        sr1.setId(4L);
        sr1.setStudyCode("sr4");

        ProjectRule acuityPrj1 = new ProjectRule();
        acuityPrj1.setDrugId("prj1");
        acuityPrj1.setStudyRules(asList(sr1, sr2, sr3));
        acuityPrj1.setNumberOfAcuityEnabledStudies(3);

        ProjectRule acuityPrj2 = new ProjectRule();
        acuityPrj2.setDrugId("prj2");
        acuityPrj2.setStudyRules(singletonList(sr4));
        acuityPrj2.setNumberOfAcuityEnabledStudies(1);


        ProjectRule acuityPrj3 = new ProjectRule();
        acuityPrj3.setDrugId("prj3");
        acuityPrj3.setNumberOfAcuityEnabledStudies(0);

        //not in cdbp
        ProjectRule acuityPrj4 = new ProjectRule();
        acuityPrj4.setDrugId("prj4");
        acuityPrj4.setNumberOfAcuityEnabledStudies(0);

        List<ProjectRule> acuityProjects = asList(acuityPrj1, acuityPrj2, acuityPrj3, acuityPrj4);

        //fill cdbp related
        ProjectRule cdbpPrj1 = new ProjectRule();
        cdbpPrj1.setDrugId("prj1");
        cdbpPrj1.setTotalStudyCount(10);
        cdbpPrj1.setNumberOfAcuityEnabledStudies(0);


        ProjectRule cdbpPrj2 = new ProjectRule();
        cdbpPrj2.setDrugId("prj2");
        cdbpPrj2.setTotalStudyCount(20);
        cdbpPrj2.setNumberOfAcuityEnabledStudies(0);

        ProjectRule cdbpPrj3 = new ProjectRule();
        cdbpPrj3.setDrugId("prj3");
        cdbpPrj3.setTotalStudyCount(30);
        cdbpPrj3.setNumberOfAcuityEnabledStudies(0);

        //not in acuity
        ProjectRule cdbpPrj5 = new ProjectRule();
        cdbpPrj5.setDrugId("prj5");
        cdbpPrj5.setTotalStudyCount(50);
        cdbpPrj5.setNumberOfAcuityEnabledStudies(0);

        List<ProjectRule> cdbpProjects = asList(cdbpPrj1, cdbpPrj2, cdbpPrj3, cdbpPrj5);
        Map<String, ProjectRule> result;

        result = new TreeMap<>();
        result.put(acuityPrj1.getDrugId(), acuityPrj1);
        result.put(acuityPrj2.getDrugId(), acuityPrj2);
        result.put(acuityPrj3.getDrugId(), acuityPrj3);
        result.put(acuityPrj4.getDrugId(), acuityPrj4);
        result.put(cdbpPrj5.getDrugId(), cdbpPrj5);

        final SortedMap<String, ProjectRule> wizardResult = wizardService.getSearchProjectResult(acuityProjects, cdbpProjects);
        when(projectRuleDao.searchByDrugs(asList("pr1", "prj2", "prj3", "prj5"))).thenReturn(acuityProjects.subList(0, acuityProjects.size() - 1));
        assertEquals(wizardResult, result);
        assertEquals(wizardResult.size(), 5);
        assertEquals(wizardResult.get("prj6"), null);

        //test totalStudyMerge & acuityEnabled
        Comparator<ProjectRule> comparator =
                (prj1, prj2) ->
                        prj1.getDrugId().compareTo(prj2.getDrugId())
                                + (prj1.getNumberOfAcuityEnabledStudies() - prj2.getNumberOfAcuityEnabledStudies())
                                + (prj1.getTotalStudyCount() - prj2.getTotalStudyCount());

        final ProjectRule resultPrj5 = wizardResult.get("prj5");
        assertEquals(Objects.compare(resultPrj5, cdbpPrj5, comparator), 0);
        assertEquals(resultPrj5.getNumberOfAcuityEnabledStudies(), 0);

        final ProjectRule resultPrj1 = wizardResult.get("prj1");
        assertEquals(resultPrj1.getNumberOfAcuityEnabledStudies(), 3);
        assertEquals(resultPrj1.getTotalStudyCount(), 10);
    }
}
