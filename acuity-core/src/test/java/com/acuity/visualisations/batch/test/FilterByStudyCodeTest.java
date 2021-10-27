package com.acuity.visualisations.batch.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.acuity.visualisations.batch.util.CsvSourceUtil;
import com.acuity.visualisations.batch.util.FilterByStudyCodeTestDao;
import com.acuity.visualisations.dal.util.State;

@Component
public class FilterByStudyCodeTest implements TestPattern {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilterByStudyCodeTest.class);
	@Autowired
	private FilterByStudyCodeTestDao byStudyCodeTestDao;

	@Override
	public void setSource(int launchNumber) {
		if (launchNumber == 1) {
		} else if (launchNumber == 2) {
			setTMDSSource();
		} else if (launchNumber == 3) {
			resetTMDSSource();
		} else {
			return;
		}
	}

	@Override
	public void checkResult(int launchNumber) {
		if (launchNumber == 1) {
			checkForNotEmpty();
			checkForState(State.JUST_INSERTED);
		} else if (launchNumber == 2) {
			checkForNotEmpty();
			checkForState(State.JUST_INSERTED);
		} else if (launchNumber == 3) {
			checkForNotEmpty();
			checkForState(State.SYNCHRONIZED);
		} else {
			return;
		}
	}

	private void checkForState(State state) {
		Set<State> tmdsState = byStudyCodeTestDao.getTMDSState();
		if (tmdsState.size() != 1) {
			String message = String.format("Too much states for targer med dosing schedule. Expected only one state - %s", state);
			LOGGER.error(message);
			return;
		}
		if (!tmdsState.contains(state)) {
			String message = String.format("Invalid state for target med dosing schedule records - %s. Expected: %s", new Object[] {
					tmdsState.iterator().next(), state });
			LOGGER.error(message);
			return;
		}
		String message = String.format("State of target med dosing schedule records: %s, as expected", state);
		LOGGER.info(message);
	}

	private void checkForNotEmpty() {
		int tmdsCount = byStudyCodeTestDao.getTMDSCount();
		if (tmdsCount > 0) {
			LOGGER.info("Number of target med dosing schedule records: " + tmdsCount + ". Not empty as expected");
		} else {
			LOGGER.error("Target med dosing schedule table is empty.");
		}
	}

	private void setTMDSSource() {
		HashMap<String, List<String>> columnsToFind = new HashMap<String, List<String>>();
		columnsToFind.put("STUDY", Arrays.asList(new String[] { "D1234C00001" }));
		HashMap<String, Object> columnsToSet = new HashMap<String, Object>();
		columnsToSet.put("STUDY", "D1234C0001B");
		CsvSourceUtil.modifySource("/TEST1234/D1234C00001/dose_junk.csv", columnsToFind, columnsToSet);
	}

	private void resetTMDSSource() {
		HashMap<String, List<String>> columnsToFind = new HashMap<String, List<String>>();
		columnsToFind.put("STUDY", Arrays.asList(new String[] { "D1234C0001B" }));
		HashMap<String, Object> columnsToSet = new HashMap<String, Object>();
		columnsToSet.put("STUDY", "D1234C00001");
		CsvSourceUtil.modifySource("/TEST1234/D1234C00001/dose_junk.csv", columnsToFind, columnsToSet);
	}

}
