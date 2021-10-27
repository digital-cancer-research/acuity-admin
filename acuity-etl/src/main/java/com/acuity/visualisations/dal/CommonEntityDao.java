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

package com.acuity.visualisations.dal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
@PropertySource({
        "classpath:/com/acuity/visualisations/dal/CommonEntityDao.xml",
        "classpath:/com/acuity/visualisations/dal/DoseDataStandardisation.xml",
        "classpath:/com/acuity/visualisations/dal/AdverseEventDataStandardisation.xml"
})
public class CommonEntityDao extends ACUITYDaoSupport {

    @Value("${UPDATE_FIRST_DOSE_DATE}")
    private String UPDATE_FIRST_DOSE_DATE;

    @Value("${UPDATE_BASELINE_DATE}")
    private String UPDATE_BASELINE_DATE;

    @Value("${UPDATE_PAT_COUNTRY}")
    private String UPDATE_PAT_COUNTRY;

    @Value("${UPDATE_PAT_RAND_DATE}")
    private String UPDATE_PAT_RAND_DATE;

    @Value("${UPDATE_EXACERBATIONS_SET_CUSTOM_SEVERITY}")
    private String UPDATE_EXACERBATIONS_SET_CUSTOM_SEVERITY;

    @Value("${UPDATE_PATIENT_WITHDRAWAL}")
    private String UPDATE_PATIENT_WITHDRAWAL;

    @Value("${UPDATE_SUBJECT_STUDY_STATUS}")
    private String UPDATE_SUBJECT_STUDY_STATUS;

    @Value("${UPDATE_LAST_ETL_RUN}")
    private String UPDATE_LAST_ETL_RUN;

    @Value("${UPDATE_SUBJECT_BEST_TUMOUR_RESPONSES}")
    private String UPDATE_SUBJECT_BEST_TUMOUR_RESPONSES;

    @Value("${SELECT_CUSTOM_GROUPINGS_WITH_THE_SAME_NAMES_AS_RESULT_GROUPINGS}")
    private String SELECT_CUSTOM_GROUPINGS_WITH_THE_SAME_NAMES_AS_RESULT_GROUPINGS;


    @Value("${DISCARD_ALL_DOSE_VALUES_THAT_HAPPEN_IN_THE_FUTURE}")
    private String DISCARD_ALL_DOSE_VALUES_THAT_HAPPEN_IN_THE_FUTURE;

    @Value("${DISCARD_ALL_DISC_VALUES_THAT_HAPPEN_IN_THE_FUTURE}")
    private String DISCARD_ALL_DISC_VALUES_THAT_HAPPEN_IN_THE_FUTURE;

    @Value("${DISCARD_DOSE_EVENTS_AFTER_DEATH}")
    private String DISCARD_DOSE_EVENTS_AFTER_DEATH;

    @Value("${DISCARD_DISC_EVENTS_AFTER_DEATH}")
    private String DISCARD_DISC_EVENTS_AFTER_DEATH;

    @Value("${DISCARD_DOSE_EVENTS_AFTER_WITHDRAWAL}")
    private String DISCARD_DOSE_EVENTS_AFTER_WITHDRAWAL;

    @Value("${DISCARD_DISC_EVENTS_AFTER_WITHDRAWAL}")
    private String DISCARD_DISC_EVENTS_AFTER_WITHDRAWAL;

    @Value("${SET_A_PERIOD_STATUS_ON_EACH_DOSE_EVENT}")
    private String SET_A_PERIOD_STATUS_ON_EACH_DOSE_EVENT;

    @Value("${IMPUTE_ANY_MISSING_END_EVENT_FOR_THE_LAST_DOSE_RECORD}")
    private String IMPUTE_ANY_MISSING_END_EVENT_FOR_THE_LAST_DOSE_RECORD;

    @Value("${IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_DOSE_DISCONTINUATION_EVENTS}")
    private String IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_DOSE_DISCONTINUATION_EVENTS;


    @Value("${IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_THE_START_EVENTS_OF_SUBSEQUENT_DOSE_RECORDS}")
    private String IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_THE_START_EVENTS_OF_SUBSEQUENT_DOSE_RECORDS;

    @Value("${IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_A_WITHDRAWAL_COMPLETION_EVENTS}")
    private String IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_A_WITHDRAWAL_COMPLETION_EVENTS;

    @Value("${IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_A_DEATH_EVENTS}")
    private String IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_A_DEATH_EVENTS;

    @Value("${FINISH_DOSE_SUBSEQUENT_PERIOD_TYPES}")
    private String FINISH_DOSE_SUBSEQUENT_PERIOD_TYPES;

    @Value("${AES_UPDATE_ONGOINGS}")
    private String AES_UPDATE_ONGOINGS;

    @Value("${AES_DISCARD_INVALID_1}")
    private String AES_DISCARD_INVALID_1;

    @Value("${AES_DISCARD_INVALID_2}")
    private String AES_DISCARD_INVALID_2;

    private String SELECT_STUDY_RULE_ID_BY_STUDY_UID =
            "SELECT msr_id FROM result_study, map_study_rule WHERE msr_study_code=std_name AND std_id=?";

    private String RENAME_GROUPING_IN_MAP_SUBJECT_GROUP_RULE =
            "UPDATE map_subject_group_rule SET msgr_name=? WHERE msgr_study_id=? AND msgr_name=?";

    @Value("${CLEAR_EDIARY_MEDICATION_USAGE}")
    private String CLEAR_EDIARY_MEDICATION_USAGE;

    @Value("${CLEAR_EDIARY_OBSERVATIONS}")
    private String CLEAR_EDIARY_OBSERVATIONS;

    @Value("${UPDATE_EDIARY_MEDICATION_USAGE}")
    private String UPDATE_EDIARY_MEDICATION_USAGE;

    @Value("${UPDATE_EDIARY_OBSERVATIONS}")
    private String UPDATE_EDIARY_OBSERVATIONS;

    @Value("${UPDATE_DATASET_LAST_EVENT_DATE}")
    private String UPDATE_DATASET_LAST_EVENT_DATE;

    public void calculatePatientFirstDoseDate(String studyGuid, long studyRuleId) {
        logger.info("Calculate First IP Dose Dates for Study {}", studyGuid);
        getJdbcTemplate().update(UPDATE_FIRST_DOSE_DATE, studyGuid);

        logger.info("Calculate Baseline dates for Study {}", studyGuid);
        getJdbcTemplate().update(UPDATE_BASELINE_DATE, studyRuleId, studyRuleId, studyGuid);
    }

    public void calculatePatientCountry(String studyGuid, long studyRuleId) {
        logger.info("Calculate Patient Countries for Study {}", studyGuid);
        getJdbcTemplate().update(UPDATE_PAT_COUNTRY, studyRuleId, studyGuid);
    }

    public void calculatePatientRandDate(String studyGuid) {
        logger.info("Calculate Patient Randomisation Dates for Study {}", studyGuid);
        getJdbcTemplate().update(UPDATE_PAT_RAND_DATE, studyGuid);
    }

    public void calculateCustomExacerbationSeverity(String studyGuid) {
        logger.info("Calculate Custom Exacerbation Severity for Study {}", studyGuid);
        getJdbcTemplate().update(UPDATE_EXACERBATIONS_SET_CUSTOM_SEVERITY, studyGuid);
    }

    public void calculateWithdrawal(String studyGuid) {
        logger.info("Calculate Withdrawal Reasons for Study {}", studyGuid);
        getJdbcTemplate().update(UPDATE_PATIENT_WITHDRAWAL, studyGuid);
    }

    public void calculateSubjectStudyStatus(String studyGuid) {
        logger.info("Calculate Subject's Study Status for Study {}", studyGuid);
        getJdbcTemplate().update(UPDATE_SUBJECT_STUDY_STATUS, studyGuid);
    }

    public void setLastETLRunDate(String studyGuid) {
        logger.info("Set last ETL run for Study {}", studyGuid);
        getJdbcTemplate().update(UPDATE_LAST_ETL_RUN, studyGuid);
    }


    public void aesPostProcessing(String studyGuid) {
        logger.info("AES_UPDATE_ONGOINGS {}", studyGuid);
        getJdbcTemplate().update(AES_UPDATE_ONGOINGS, studyGuid);
        logger.info("AES_DISCARD_INVALID_1 {}", studyGuid);
        getJdbcTemplate().update(AES_DISCARD_INVALID_1, studyGuid);
        logger.info("AES_DISCARD_INVALID_2 {}", studyGuid);
        getJdbcTemplate().update(AES_DISCARD_INVALID_2, studyGuid);
    }

    public void tumourPostProcessing(String studyGuid) {
        logger.info("UPDATE_SUBJECT_BEST_TUMOUR_RESPONSES {}", studyGuid);
        getJdbcTemplate().update(UPDATE_SUBJECT_BEST_TUMOUR_RESPONSES, studyGuid);
    }

    public void dosesPostProcessing(String studyGuid) {
        //
        logger.info("DISCARD_ALL_DOSE_VALUES_THAT_HAPPEN_IN_THE_FUTURE {}", studyGuid);
        getJdbcTemplate().update(DISCARD_ALL_DOSE_VALUES_THAT_HAPPEN_IN_THE_FUTURE, studyGuid);

        //
        logger.info("DISCARD_ALL_DISC_VALUES_THAT_HAPPEN_IN_THE_FUTURE {}", studyGuid);
        getJdbcTemplate().update(DISCARD_ALL_DISC_VALUES_THAT_HAPPEN_IN_THE_FUTURE, studyGuid);

        //
        logger.info("DISCARD_DOSE_EVENTS_AFTER_DEATH {}", studyGuid);
        getJdbcTemplate().update(DISCARD_DOSE_EVENTS_AFTER_DEATH, studyGuid);

        //
        logger.info("DISCARD_DISC_EVENTS_AFTER_DEATH {}", studyGuid);
        getJdbcTemplate().update(DISCARD_DISC_EVENTS_AFTER_DEATH, studyGuid);

        //
        logger.info("DISCARD_DOSE_EVENTS_AFTER_WITHDRAWAL {}", studyGuid);
        getJdbcTemplate().update(DISCARD_DOSE_EVENTS_AFTER_WITHDRAWAL, studyGuid);

        //
        logger.info("DISCARD_DISC_EVENTS_AFTER_WITHDRAWAL {}", studyGuid);
        getJdbcTemplate().update(DISCARD_DISC_EVENTS_AFTER_WITHDRAWAL, studyGuid);

        logger.info("SET_A_PERIOD_STATUS_ON_EACH_DOSE_EVENT {}", studyGuid);
        getJdbcTemplate().update(SET_A_PERIOD_STATUS_ON_EACH_DOSE_EVENT, studyGuid);

        //
        logger.info("IMPUTE_ANY_MISSING_END_EVENT_FOR_THE_LAST_DOSE_RECORD {}", studyGuid);
        getJdbcTemplate().update(IMPUTE_ANY_MISSING_END_EVENT_FOR_THE_LAST_DOSE_RECORD, studyGuid, studyGuid);

        //
        logger.info("IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_THE_START_EVENTS_OF_SUBSEQUENT_DOSE_RECORDS {}", studyGuid);
        getJdbcTemplate().update(IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_THE_START_EVENTS_OF_SUBSEQUENT_DOSE_RECORDS, studyGuid);

        //
        logger.info("IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_DOSE_DISCONTINUATION_EVENTS {}", studyGuid);
        getJdbcTemplate().update(IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_DOSE_DISCONTINUATION_EVENTS, studyGuid);

        //
        logger.info("IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_A_WITHDRAWAL_COMPLETION_EVENTS {}", studyGuid);
        getJdbcTemplate().update(IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_A_WITHDRAWAL_COMPLETION_EVENTS, studyGuid);

        //
        logger.info("IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_A_DEATH_EVENTS {}", studyGuid);
        getJdbcTemplate().update(IMPUTE_END_EVENTS_FOR_DOSE_RECORDS_BASED_ON_A_DEATH_EVENTS, studyGuid);

        //
        logger.info("FINISH_DOSE_SUBSEQUENT_PERIOD_TYPES {}", studyGuid);
        getJdbcTemplate().update(FINISH_DOSE_SUBSEQUENT_PERIOD_TYPES, studyGuid);
    }

    public long getStudyRuleId(String studyGuid) {
        return getJdbcTemplate().queryForObject(SELECT_STUDY_RULE_ID_BY_STUDY_UID, Long.class, studyGuid);
    }

    public void resolveGroupingNameConflicts(String studyGuid, long studyRuleId) {
        logger.info("Resolving conflicts in grouping names for Study {}", studyGuid);

        List<String> groupingNamesWithConflict = getJdbcTemplate().queryForList(
                SELECT_CUSTOM_GROUPINGS_WITH_THE_SAME_NAMES_AS_RESULT_GROUPINGS,
                String.class, studyRuleId, studyRuleId);

        if (groupingNamesWithConflict.isEmpty()) {
            logger.info("Conflicts not found");
        } else {
            String timestampedPrefix = (new SimpleDateFormat("yyyyMMddHHmm")).format(new Date());

            for (String oldGroupingName : groupingNamesWithConflict) {
                String newGroupingName = oldGroupingName + "_" + timestampedPrefix;

                logger.info("Renaming {} to {}", oldGroupingName, newGroupingName);


                getJdbcTemplate().update(RENAME_GROUPING_IN_MAP_SUBJECT_GROUP_RULE,
                        newGroupingName, studyRuleId, oldGroupingName);
            }
            logger.info("Conflicts resolved");
        }
    }

    public void updateEDiaryMedicationUsage(String studyGuid) {
        logger.info("CLEAR_EDIARY_MEDICATION_USAGE {}", studyGuid);
        getJdbcTemplate().update(CLEAR_EDIARY_MEDICATION_USAGE, studyGuid);

        logger.info("UPDATE_EDIARY_MEDICATION_USAGE {}", studyGuid);
        getJdbcTemplate().update(UPDATE_EDIARY_MEDICATION_USAGE, studyGuid);
    }

    public void updateEDiaryObservations(String studyGuid) {
        logger.info("CLEAR_EDIARY_OBSERVATIONS {}", studyGuid);
        getJdbcTemplate().update(CLEAR_EDIARY_OBSERVATIONS, studyGuid);

        logger.info("UPDATE_EDIARY_OBSERVATIONS {}", studyGuid);
        getJdbcTemplate().update(UPDATE_EDIARY_OBSERVATIONS, studyGuid);
    }

    public void updateDatasetLastEventDate(String studyGuid) {
        logger.info("UPDATE_DATASET_LAST_EVENT_DATE {}", studyGuid);
        getJdbcTemplate().update(UPDATE_DATASET_LAST_EVENT_DATE, studyGuid, studyGuid);
    }
}
