package com.acuity.visualisations.mapping.dao;

/**
 * Created by knml167 on 10/19/2016.
 */
public interface IClinicalStudyDao {
    void insertOrUpdateClinicalStudy(Long projectId, String id, String name);
}
