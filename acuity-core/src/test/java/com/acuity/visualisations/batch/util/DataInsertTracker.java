package com.acuity.visualisations.batch.util;

import java.util.Hashtable;

public class DataInsertTracker {

	private Hashtable<String, Hashtable<String, Hashtable<String, Hashtable<Class<?>, Integer>>>> allDataHt;
	private static DataInsertTracker theInstance = new DataInsertTracker();
	
	private DataInsertTracker() {
		allDataHt = new Hashtable<String, Hashtable<String, Hashtable<String, Hashtable<Class<?>, Integer>>>>();
	}

	public static DataInsertTracker getInstance() {
		return theInstance;
	}

	public void setEntityCount(String projectName, String studyCode, Class<?> entityClass, int count) {
		// We will use nested Hashtables here.
		// The Project Hashtable will store a bunch of Study Hashtables
		// Each StudyHashtable will have an EntityType Hashtable containing the count of the entities it want to write to the database
		Hashtable<Class<?>, Integer> entityHt = new Hashtable<Class<?>, Integer>();
		entityHt.put(entityClass, count);

		Hashtable<String, Hashtable<Class<?>, Integer>> studyHt = new Hashtable<String, Hashtable<Class<?>, Integer>>();
		String sKey = studyCode + entityClass;
		studyHt.put(sKey, entityHt);

		Hashtable<String, Hashtable<String, Hashtable<Class<?>, Integer>>> projectHt = new Hashtable<String, Hashtable<String, Hashtable<Class<?>, Integer>>>();
		String pKey = projectName + studyCode;
		projectHt.put(pKey, studyHt);

		allDataHt.put(projectName, projectHt);
	}

	public int getEntityCount(String projectName, String studyCode, Class<?> entityClass) {

		Integer entityCount = 0;

		Hashtable<String, Hashtable<String, Hashtable<Class<?>, Integer>>> projHt = allDataHt.get(projectName);

		String pKey = projectName + studyCode;
		Hashtable<String, Hashtable<Class<?>, Integer>> studyHt = projHt.get(pKey);

		String sKey = studyCode + entityClass;
		Hashtable<Class<?>, Integer> entityHt = studyHt.get(sKey);

		entityCount = (Integer) entityHt.get(entityClass);

		return entityCount.intValue();
	}

}
