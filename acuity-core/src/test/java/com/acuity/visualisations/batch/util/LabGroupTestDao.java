package com.acuity.visualisations.batch.util;

import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class LabGroupTestDao extends ACUITYTestDaoSupport {

	private static final String STUDY_NAME = "D1234C00001";
	private static final String PROJECT_NAME = "TEST1234";

	public Map<String, Set<String>> getLabCodes(final List<String> labCodes) {
		return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(getSelectQuery(labCodes.size()));
            int i = 0;
            for (; i < labCodes.size(); i++) {
                ps.setString(i + 1, labCodes.get(i));
            }
            ps.setString(i + 1, STUDY_NAME);
            ps.setString(i + 2, PROJECT_NAME);
            return ps;
        }, (ResultSet rs) -> {
            Map<String, Set<String>> result = new HashMap<String, Set<String>>();
            while (rs.next()) {
                String labCode = rs.getString("LAB_CODE");
                String lgrLabCode = rs.getString("LGR_LAB_CODE");
                if (!result.containsKey(labCode)) {
                    result.put(labCode, new HashSet<String>());
                }
                result.get(labCode).add(lgrLabCode);
            }
            return result;
        });
	}
	
	private String getSelectQuery(int count) {
		StringBuilder builder = new StringBuilder();
		builder.append("select lab_code, lgr_lab_code from RESULT_LABORATORY, RESULT_LAB_GROUP where RESULT_LABORATORY.lab_lgr_id=RESULT_LAB_GROUP.lgr_id and lab_code in (");
		for (int i = 0; i < count; i++) {
			builder.append("?, ");
		}
		builder.setLength(builder.length() - 2);
		builder.append(") and LGR_STD_ID in (select std_id from RESULT_STUDY, RESULT_PROJECT where std_prj_id=RESULT_PROJECT.prj_id and RESULT_STUDY.std_name=? and RESULT_PROJECT.prj_name=?)");
		return builder.toString();
	}
}
