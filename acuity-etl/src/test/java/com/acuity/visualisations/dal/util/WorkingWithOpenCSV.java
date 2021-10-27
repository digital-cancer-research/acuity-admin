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

package com.acuity.visualisations.dal.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class WorkingWithOpenCSV {
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkingWithOpenCSV.class);
	
	class LabcodeObject {
		String sampleName;
		String testName;
	}
	
	@Test
	public void testCSVFile() throws Exception {
		InputStream is = getClass().getClassLoader().getResourceAsStream("Labcodes_2012-12-04.csv");
		CSVReader reader = new CSVReader(new InputStreamReader(is));
		String[] nextLine;

		HashMap<String, LabcodeObject> map = new HashMap<String, WorkingWithOpenCSV.LabcodeObject>();
		
		while((nextLine = reader.readNext()) != null) {
			LabcodeObject lo = new LabcodeObject();
			String sampleName = nextLine[1].replaceAll("'", "''");
			String testName = nextLine[0].replaceAll("'", "''");
			lo.sampleName = sampleName;
			lo.testName = testName;
			
			String labcode = nextLine[2];
			if(!map.containsKey(labcode)) {
				map.put(labcode, lo);
			} else if(!lo.sampleName.equals(map.get(labcode).sampleName) || !lo.testName.equals(map.get(labcode).testName)) {
				LOGGER.error("Error!!");
			}
		}
		int i = 1;
		StringBuilder sb = new StringBuilder();
		for (String labcode: map.keySet()) {
			sb.append("insert into labcode_lookup(LCL_ID, LCL_LABCODE, LCL_TEST_NAME, LCL_SAMPLE_NAME) values(");
			sb.append(i++);
			sb.append(",'");
			sb.append(labcode);
			sb.append("','");
			sb.append(map.get(labcode).testName);
			sb.append("','");
			sb.append(map.get(labcode).sampleName);
			sb.append("');");
			sb.append("\r\n");
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("test2013.txt"));
		bw.write(sb.toString());
		try {
			bw.close();
		} catch(Exception ignore) {
			//ignore
		}
		
		try {
			reader.close();
		} catch(Exception ignore) {
			
		}
		
		LOGGER.debug(String.valueOf(map.size()));
		LOGGER.debug(String.valueOf(sb));
	}
	
}
