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
