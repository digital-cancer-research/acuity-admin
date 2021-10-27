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

package com.acuity.visualisations.web.dao;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.web.entity.EmailDetailsEntity;
import com.acuity.visualisations.web.entity.EmailDetailsEntity.EmailType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This is the DAO class for gathering the parameters of emails that need to be sent 
 */
@Repository
public class EmailDetailsDao extends ACUITYDaoSupport {

    /**
     * The SELECT statement for getting the emails from the database
     */
    private static final String SELECT_EMAIL_DATEILS_SQL = "SELECT * FROM UTIL_EMAIL_DETAILS WHERE emd_email_type = ?";

    /**
     * @return The email parameters for when a new ACUITY project is created
     */
    public Optional<EmailDetailsEntity> getAcuityProjectEnabledEmailDetails() {
        return getJdbcTemplate().query(
                SELECT_EMAIL_DATEILS_SQL, new Object[]{EmailType.DRUG_PROJECT_ENABLED.toString()}, ROW_MAPPER)
                .stream().findFirst();
    }

     /**
     * @return The email parameters for when a new ACUITY project is created
     */
    public Optional<EmailDetailsEntity> getStudyJobsFailedEmailDetails() {
        return getJdbcTemplate().query(
                SELECT_EMAIL_DATEILS_SQL, new Object[]{EmailType.STUDY_JOBS_FAILED.toString()}, ROW_MAPPER)
                .stream().findFirst();
    }

     /**
     * @return The email parameters for when study key config values are changed
     */
    public Optional<EmailDetailsEntity> getStudyConfigChangedDetails() {
        return getJdbcTemplate().query(
                SELECT_EMAIL_DATEILS_SQL, new Object[]{EmailType.STUDY_SETUP_VALUES_CHANGED.toString()}, ROW_MAPPER)
                .stream().findFirst();
    }

     /**
     * @return The email parameters for when study excluding values are changed
     */
    public Optional<EmailDetailsEntity> getStudyExcludingValuesChangedDetails() {
        return getJdbcTemplate().query(
                SELECT_EMAIL_DATEILS_SQL, new Object[]{EmailType.STUDY_EXCLUDING_VALUES_CHANGED.toString()}, ROW_MAPPER)
                .stream().findFirst();
    }

    public Optional<EmailDetailsEntity>  getMissingSourceFilesDetails() {
        return getJdbcTemplate().query(
                SELECT_EMAIL_DATEILS_SQL, new Object[]{EmailType.FILES_NOT_ACCESSIBLE.toString()}, ROW_MAPPER)
                .stream().findFirst();
    }

    /**
     * The RowMapper for the SELECT statement(s)
     */
    private static final RowMapper<EmailDetailsEntity> ROW_MAPPER = (rs, rowNum) -> {
        EmailDetailsEntity email = new EmailDetailsEntity();
        email.setEmailType(rs.getString("EMD_EMAIL_TYPE"));
        email.setToAddresses(rs.getString("EMD_TO_ADDRESSES"));
        email.setCcAddresses(rs.getString("EMD_CC_ADDRESSES"));
        email.setFromAddress(rs.getString("EMD_FROM_ADDRESS"));
        email.setSubject(rs.getString("EMD_SUBJECT"));
        email.setAttachments(rs.getString("EMD_ATTACHMENTS"));
        email.setText(rs.getString("EMD_TEXT"));
        return email;
    };
}
