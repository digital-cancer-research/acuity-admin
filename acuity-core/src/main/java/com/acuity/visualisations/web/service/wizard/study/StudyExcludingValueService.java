package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.mapping.dao.ICommonStaticDao;
import com.acuity.visualisations.mapping.dao.IExcludingValueDao;
import com.acuity.visualisations.mapping.dao.IStudyRuleDao;
import com.acuity.visualisations.mapping.entity.EntityRule;
import com.acuity.visualisations.mapping.entity.ExcludingValue;
import com.acuity.visualisations.mapping.entity.FieldRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.service.IEmailService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by knml167 on 28/03/14.
 */
@Service
public class StudyExcludingValueService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(StudyExcludingValueService.class);

    @Autowired
    private IExcludingValueDao excludingValueDao;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private IStudyRuleDao studyRuleDao;

    @Autowired
    private ICommonStaticDao dao;

    @Transactional(readOnly = true)
    public List<ExcludingValue> getExcludingValuesByStudyRule(long studyRuleId) {
        return excludingValueDao.getExcludingValuesByStudyRule(studyRuleId);
    }

    @Transactional
    public void saveStudyExcludingValues(StudyRule study, List<ExcludingValue> excludingValues) {
        long studyRuleId = study.getId();
        List<ExcludingValue> oldList = getExcludingValuesByStudyRule(studyRuleId);

        for (ExcludingValue value : excludingValues) {
            value.setStudyRuleId(studyRuleId);
        }

        if (!(oldList.containsAll(excludingValues) && excludingValues.containsAll(oldList))) {
            LOGGER.debug("list changed");
            excludingValueDao.deleteStudyExcludingValues(studyRuleId);
            excludingValueDao.insertExcludingValues(excludingValues);

            List<EntityRule> entityRules = dao.getStaticData();

            String oldListStr = getEvTableLine(oldList, entityRules);
            String excludingValuesStr = getEvTableLine(excludingValues, entityRules);
            emailService.sendStudyExclusionValuesChangedEmail(study, oldListStr, excludingValuesStr);
        }
    }

    private static String getEvTableLine(List<ExcludingValue> oldList, List<EntityRule> entityRules) {
        return StringUtils.join(oldList.stream().map(ev ->
                getEvStr(entityRules, ev)).collect(Collectors.toList()), "\n");
    }

    private static String getEvStr(List<EntityRule> entityRules, ExcludingValue ev) {
        EntityRule entityRule = entityRules.stream().filter(er ->
                er.getFieldRules().stream().filter(fr -> ev.getFieldRuleId().equals(fr.getId())).count() > 0).findFirst().get();
        FieldRule fieldRule = entityRules.stream().flatMap(er ->
                er.getFieldRules().stream()).filter(fr -> ev.getFieldRuleId().equals(fr.getId())).findFirst().get();
        String mapping = entityRule.getName();
        String field = fieldRule.getDescription().getText();
        return String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>", mapping, field, ev.getValue());
    }
}
