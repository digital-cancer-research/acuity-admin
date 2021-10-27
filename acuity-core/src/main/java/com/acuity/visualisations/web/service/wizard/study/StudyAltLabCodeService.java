package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.mapping.dao.ICustomLabcodeLookupDao;
import com.acuity.visualisations.mapping.entity.CustomLabcodeLookup;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class StudyAltLabCodeService {

    @Autowired
    private ICustomLabcodeLookupDao customLabcodeLookupDao;

    public List<CustomLabcodeLookup> getAltLabCodes(long studyRuleId) {
        return customLabcodeLookupDao.getLabCodesByStudyRule(studyRuleId);
    }

    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void saveAltLabCodes(long studyRuleId, List<CustomLabcodeLookup> labcodes) {

        for (Iterator<CustomLabcodeLookup> it = labcodes.iterator(); it.hasNext();) {
            CustomLabcodeLookup labcode = it.next();
            if (StringUtils.isEmpty(labcode.getLabcode()) || StringUtils.isEmpty(labcode.getSampleName())
                    || StringUtils.isEmpty(labcode.getSampleName())) {
                it.remove();
            } else {
                labcode.setStudyRuleId(studyRuleId);
            }
        }

        customLabcodeLookupDao.deleteAllLabCodesByStudyRule(studyRuleId);
        customLabcodeLookupDao.insertLabCodes(labcodes);
    }
}
