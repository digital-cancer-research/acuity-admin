package com.acuity.visualisations.batch.util;

import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class EventTypeTestDao extends ACUITYTestDaoSupport {

	private static final String STUDY_NAME = "D1234C00001";
	private static final String PROJECT_NAME = "TEST1234";

	public Map<String, Set<String>> getMedDRAValues() {
		return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(getSelectMedDRAValuesQuery());
            ps.setString(1, STUDY_NAME);
            ps.setString(2, PROJECT_NAME);
            return ps;
        }, (ResultSet rs) -> {
            HashMap<String, Set<String>> result = new HashMap<String, Set<String>>();
            while (rs.next()) {
                String ptValue = rs.getString("EVT_PT");
                if (!result.containsKey("PT")) {
                    result.put("PT", new HashSet<String>());
                }
                result.get("PT").add(ptValue);
                String socValue = rs.getString("EVT_SOC");
                if (!result.containsKey("SOC")) {
                    result.put("SOC", new HashSet<String>());
                }
                result.get("SOC").add(socValue);
                String lltValue = rs.getString("EVT_LLT");
                if (!result.containsKey("LLT")) {
                    result.put("LLT", new HashSet<String>());
                }
                result.get("LLT").add(lltValue);
                String hltValue = rs.getString("EVT_HLT");
                if (!result.containsKey("HLT")) {
                    result.put("HLT", new HashSet<String>());
                }
                result.get("HLT").add(hltValue);
            }
            return result;
        });
	}

	protected String getSelectMedDRAValuesQuery() {
		return "select evt_pt, evt_soc, evt_llt, evt_hlt " + "from RESULT_EVENT_TYPE " + "where evt_std_id in "
				+ "(select std_id from RESULT_STUDY, RESULT_PROJECT where std_prj_id=RESULT_PROJECT.prj_id and RESULT_STUDY.std_name=? and RESULT_PROJECT.prj_name=?)";
	}
}
