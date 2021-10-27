package com.acuity.visualisations.batch.test;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.acuity.visualisations.batch.util.EventTypeTestDao;

@Component
public class EventTypeTest implements TestPattern {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventTypeTest.class);

	@Autowired
	private EventTypeTestDao eventTypeTestDao;

	@Override
	public void setSource(int launchNumber) {
		return;
	}

	@Override
	public void checkResult(int launchNumber) {
		checkIfMedDRAValuesAreUpperCased();
	}

	private void checkIfMedDRAValuesAreUpperCased() {
		Map<String, Set<String>> medDRAValues = eventTypeTestDao.getMedDRAValues();
		boolean checkPT = checkMedDRAValueIsUpperCased(medDRAValues, "PT");
		boolean checkSOC = checkMedDRAValueIsUpperCased(medDRAValues, "SOC");
		boolean checkLLT = checkMedDRAValueIsUpperCased(medDRAValues, "LLT");
		boolean checkHLT = checkMedDRAValueIsUpperCased(medDRAValues, "HLT");
		if (checkPT && checkSOC && checkLLT && checkHLT) {
			LOGGER.info("All MedDRA values are uppercased");
		}
	}

	private boolean checkMedDRAValueIsUpperCased(Map<String, Set<String>> medDRAValues, String medDRAName) {
		boolean flag = true;
		if (medDRAValues.containsKey(medDRAName)) {
			for (String medDRAValue : medDRAValues.get(medDRAName)) {
				if (medDRAValue != null && !isUpperCased(medDRAValue)) {
					LOGGER.error("Not uppercased MedDRA value: " + medDRAValue);
					flag = false;
				}
			}
		}
		return flag;
	}

	private boolean isUpperCased(String value) {
		if (value == null) {
			return false;
		}
		return value.equals(value.toUpperCase(Locale.ENGLISH));
	}

}
