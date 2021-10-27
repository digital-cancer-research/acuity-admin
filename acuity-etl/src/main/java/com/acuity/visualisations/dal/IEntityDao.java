package com.acuity.visualisations.dal;

import com.acuity.visualisations.batch.holders.RowParameters;
import com.acuity.visualisations.mapping.OctetString;
import com.acuity.visualisations.model.output.OutputEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IEntityDao<T extends OutputEntity> extends IBasicEntityDao<T> {

    Map<OctetString, RowParameters> findHash(String studyId, Class<?> entityClass);

    Map<OctetString, RowParameters> findHash(String studyId, Map<OctetString, RowParameters> hashes, Class<?> entityClass);

    HashMap<String, String> findIdsByHash(List<String> hashList);

    HashMap<String, String> findIdsByRefHash(List<String> hashList);

    void updateState(List<T> entities);

    void deleteByIds(List<String> ids);

    /**
     * This method is used in data load report table in column 'Subjects-data table'.
     *
     * @param studyName study name
     * @return subjects ids list or empty list in case when table not linked to RESULT_PATIENT table
     * (for instance, RESULT_EVENT_TYPE)
     */
    List<String> getSubjectsIdsByStudyName(String studyName);

}
