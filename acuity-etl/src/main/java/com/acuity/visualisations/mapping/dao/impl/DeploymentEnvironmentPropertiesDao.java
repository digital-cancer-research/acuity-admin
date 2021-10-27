package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.mapping.dao.IDeploymentEnvironmentPropertiesDao;
import com.acuity.visualisations.util.Pair;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Repository
public class DeploymentEnvironmentPropertiesDao extends ACUITYDaoSupport implements IDeploymentEnvironmentPropertiesDao {
    private Map<Pair<String, String>, String> cachedValues = null;

    private Map<Pair<String, String>, String> getCachedValues() {
        if (cachedValues == null) {
            cachedValues = getJdbcTemplate().query("SELECT ENVIRONMENT_NAME,PROPERTY_NAME,PROPERTY_VALUE FROM UTIL_DEPLOYMENT_ENV_PROPS",
                    (ResultSet rs) -> {
                        Map<Pair<String, String>, String> result = new HashMap<Pair<String, String>, String>();
                        while (rs.next()) {
                            result.put(new Pair<String, String>(rs.getString("DEP_ENV"), rs.getString("DEP_NAME")), rs.getString("DEP_VALUE"));
                        }
                        return result;
                    });
        }
        return cachedValues;
    }

    public String getPropertyValue(String environmentName, String propertyName) {
        return getCachedValues().get(new Pair<String, String>(environmentName, propertyName));
    }
}
