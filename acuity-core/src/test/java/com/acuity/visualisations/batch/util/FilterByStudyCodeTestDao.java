package com.acuity.visualisations.batch.util;

import com.acuity.visualisations.dal.util.State;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

@Repository
public class FilterByStudyCodeTestDao extends ACUITYTestDaoSupport {

	private static final String STUDY_NAME = "D1234C00001";
	private static final String PROJECT_NAME = "TEST1234";

	public Integer getTMDSCount() {
		return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(getTMDSCountQuery());
            ps.setString(1, STUDY_NAME);
            ps.setString(2, PROJECT_NAME);
            return ps;
        }, (ResultSet rs) -> {
            Integer count = null;
            if (rs.next()) {
                count = rs.getInt("count_tmds");
            }
            return count;
        });
	}

	public Set<State> getTMDSState() {
		return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(getTMDSStateQuery());
            ps.setString(1, STUDY_NAME);
            ps.setString(2, PROJECT_NAME);
            return ps;
        }, (ResultSet rs) -> {
            Set<State> states = new HashSet<State>();
            while (rs.next()) {
                String stateStr = rs.getString("tmds_state");
                State state = State.getState(stateStr);
                states.add(state);
            }
            return states;
        });
	}

	protected String getTMDSStateQuery() {
		return "select mds_state as tmds_state " + "from RESULT_TRG_MED_DOS_SCHEDULE, RESULT_PATIENT, RESULT_STUDY, RESULT_PROJECT "
				+ "where mds_pat_id = RESULT_PATIENT.pat_id and RESULT_PATIENT.pat_std_id = RESULT_STUDY.std_id and RESULT_STUDY.std_prj_id = RESULT_PROJECT.prj_id "
				+ "and RESULT_STUDY.std_name=? and RESULT_PROJECT.prj_name=?";
	}

	protected String getTMDSCountQuery() {
		return "select count(1) as count_tmds " + "from RESULT_TRG_MED_DOS_SCHEDULE, RESULT_PATIENT, RESULT_STUDY, RESULT_PROJECT "
				+ "where mds_pat_id = RESULT_PATIENT.pat_id and RESULT_PATIENT.pat_std_id = RESULT_STUDY.std_id and RESULT_STUDY.std_prj_id = RESULT_PROJECT.prj_id "
				+ "and RESULT_STUDY.std_name=? and RESULT_PROJECT.prj_name=?";
	}
}
