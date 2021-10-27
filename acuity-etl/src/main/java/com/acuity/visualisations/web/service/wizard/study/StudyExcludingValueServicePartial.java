package com.acuity.visualisations.web.service.wizard.study;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acuity.visualisations.mapping.dao.IExcludingValueDao;
import com.acuity.visualisations.mapping.entity.ExcludingValue;

@Service
public class StudyExcludingValueServicePartial {

	@Autowired
	private IExcludingValueDao excludingValueDao;

	@Transactional(readOnly = true)
	public List<ExcludingValue> getExcludingValuesByStudyRule(long studyRuleId) {
		return excludingValueDao.getExcludingValuesByStudyRule(studyRuleId);
	}
}
