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

package com.acuity.visualisations.mapping.dao.impl;


import com.acuity.visualisations.mapping.StudyType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class StudyRuleDaoTest {

    @Test
    public void createSqlSelectStudyRulesTest() {
        final String sql = "select * from MAP_STUDY_RULE LEFT JOIN MAP_CLINICAL_STUDY ON MCS_STUDY_ID = MSR_MCS_STUDY_ID AND MSR_PRJ_ID = MCS_MPR_ID INNER JOIN MAP_PROJECT_RULE ON (MSR_PRJ_ID = MPR_ID) WHERE MSR_PRJ_ID = ?";

        StudyType nnn = new StudyType(null, null, null);
        StudyType ntt = new StudyType(null, true, true);
        StudyType nnt = new StudyType(null, null, true);
        StudyType tnt = new StudyType(true, null, true);
        StudyType ttt = new StudyType(true, true, true);
        StudyType fff = new StudyType(false, false, false);

        //Check empty
        Assert.assertEquals(sql,
                StudyRuleDao.createSqlSelectStudyRules(Collections.<StudyType>emptyList()));

        Assert.assertEquals(sql,
                StudyRuleDao.createSqlSelectStudyRules(Arrays.asList(nnn)));

        Assert.assertEquals(sql,
                StudyRuleDao.createSqlSelectStudyRules(Arrays.asList(nnn, nnn)));

        Assert.assertEquals(sql + " and ((MSR_RANDOMISED=true and MSR_REGULATORY=true))",
                StudyRuleDao.createSqlSelectStudyRules(Arrays.asList(ntt)));

        Assert.assertEquals(sql + " and ((MSR_REGULATORY=true))",
                StudyRuleDao.createSqlSelectStudyRules(Arrays.asList(nnt)));

        Assert.assertEquals(sql + " and ((MSR_BLINDED=true and MSR_REGULATORY=true))",
                StudyRuleDao.createSqlSelectStudyRules(Arrays.asList(tnt)));

        Assert.assertEquals(sql + " and ((MSR_BLINDED=true and MSR_RANDOMISED=true and MSR_REGULATORY=true))",
                StudyRuleDao.createSqlSelectStudyRules(Arrays.asList(ttt)));

        Assert.assertEquals(sql + " and ((MSR_BLINDED=true and MSR_RANDOMISED=true and MSR_REGULATORY=true) or (MSR_BLINDED=false and MSR_RANDOMISED=false and MSR_REGULATORY=false))",
                StudyRuleDao.createSqlSelectStudyRules(Arrays.asList(ttt, fff)));

    }
}
