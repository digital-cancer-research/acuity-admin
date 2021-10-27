package com.acuity.visualisations.mapping.dao;

public interface IDeploymentEnvironmentPropertiesDao {

	String getPropertyValue(String environmentName, String propertyName);
}
