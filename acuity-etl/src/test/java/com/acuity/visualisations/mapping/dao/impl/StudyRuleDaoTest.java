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
