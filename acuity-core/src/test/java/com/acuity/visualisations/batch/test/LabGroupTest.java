package com.acuity.visualisations.batch.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.acuity.visualisations.batch.util.CsvSourceUtil;
import com.acuity.visualisations.batch.util.LabGroupTestDao;

@Component
public class LabGroupTest implements TestPattern {

	@Autowired
	private LabGroupTestDao labGroupTestDao;

	private static final Map<String, String> LABCODES = new HashMap<String, String>();
	static {
		LABCODES.put("55104", "4003");
		LABCODES.put("55112", "4045");
		LABCODES.put("4005", "55101");
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(LabGroupTest.class);

	private static final String DEFAULT_FK = "Other labs";

	@Override
	public void setSource(int launchNumber) {
		if (launchNumber == 1) {
			return;
		} else if (launchNumber == 2) {
			setLaboratoryGroupSource();
		} else if (launchNumber == 3) {
			revertLaboratoryGroupSource();
		} else {
			return;
		}

	}

	@Override
	public void checkResult(int launchNumber) {
		checkLabGroupResult(launchNumber);

	}

	private void checkLabGroupResult(int launchNumber) {
		if (launchNumber == 1) {
			checkForlabCode(LABCODES.keySet());
			checkForDefaultLabcode(LABCODES.values());
		} else if (launchNumber == 2) {
			checkForlabCode(LABCODES.values());
			checkForDefaultLabcode(LABCODES.keySet());
		} else if (launchNumber == 3) {
			checkForlabCode(LABCODES.keySet());
			checkForDefaultLabcode(LABCODES.values());
		} else {
			return;
		}
	}

	private void checkForDefaultLabcode(Collection<String> labCodesToCheck) {
		Map<String, Set<String>> labCodes2 = labGroupTestDao.getLabCodes(new ArrayList<String>(labCodesToCheck));
		for (Map.Entry<String, Set<String>> entry : labCodes2.entrySet()) {
			if (entry.getValue().size() != 1) {
				LOGGER.error("To much labGroups for one labcode: " + entry.getKey());
			} else {
				String labcode = entry.getValue().toArray(new String[0])[0];
				if (labCodesToCheck.contains(entry.getKey()) && labcode.equals(DEFAULT_FK)) {
					LOGGER.info("Apropriate labGroup for labCode: " + entry.getKey());
				} else {
					LOGGER.error("Invalid labgroup labcode: " + DEFAULT_FK + " for laboratory labcode: " + entry.getKey());
				}
			}
		}
	}

	private void checkForlabCode(Collection<String> labCodesToCheck) {
		Map<String, Set<String>> labCodes = labGroupTestDao.getLabCodes(new ArrayList<String>(labCodesToCheck));
		for (Map.Entry<String, Set<String>> entry : labCodes.entrySet()) {
			if (entry.getValue().size() != 1) {
				LOGGER.error("To much labGroups for one labcode: " + entry.getKey());
			} else {
				String labcode = entry.getValue().toArray(new String[0])[0];
				if (labCodesToCheck.contains(entry.getKey()) && entry.getKey().equals(labcode)) {
					LOGGER.info("Apropriate labGroup for labCode: " + entry.getKey());
				} else {
					LOGGER.error("Invalid labgroup labcode: " + labcode + " for laboratory labcode: " + entry.getKey());
				}
			}
		}
	}

	private void setLaboratoryGroupSource() {
		for (Map.Entry<String, String> entry : LABCODES.entrySet()) {
			HashMap<String, String> columnsToFind = new HashMap<String, String>();
			columnsToFind.put("LABCODE", entry.getKey());
			HashMap<String, Object> columnsToSet = new HashMap<String, Object>();
			columnsToSet.put("LABCODE", entry.getValue());
			CsvSourceUtil.modifySource2("studies/TEST1234/D1234C00001/labgroups.csv", columnsToFind, columnsToSet);
		}
	}

	private void revertLaboratoryGroupSource() {
		for (Map.Entry<String, String> entry : LABCODES.entrySet()) {
			HashMap<String, String> columnsToFind = new HashMap<String, String>();
			columnsToFind.put("LABCODE", entry.getValue());
			HashMap<String, Object> columnsToSet = new HashMap<String, Object>();
			columnsToSet.put("LABCODE", entry.getKey());
			CsvSourceUtil.modifySource2("studies/TEST1234/D1234C00001/labgroups.csv", columnsToFind, columnsToSet);
		}
	}

}
