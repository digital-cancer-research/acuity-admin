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

package com.acuity.visualisations.model.output;

import com.acuity.visualisations.dal.util.State;
import com.acuity.visualisations.transform.entity.EntityDescriptionRule;
import com.acuity.visualisations.transform.table.TableRule;

import java.util.Date;
import java.util.Map;

public interface OutputEntity extends Cloneable {

    String getId();

    void setId(String guid);

    State getState();

    void setState(State state);

    String getStudyName();

    void setStudyName(String studyName);

    String getProjectName();

    void setProjectName(String projectName);

    Date getDateCreated();

    void setDateCreated(Date dateCreated);

    Date getDateUpdated();

    void setDateUpdated(Date dateUpdated);

    void update();

    String getSha1ForUniqueFields();

    void setSha1ForUniqueFields(String sha1ForUniqueFields);

    void setSha1ForDatabaseConstraint(String sha1ForDatabaseConstraint);

    Map<String, String> getSha1ForReferencedFields();

    String getFirstSha1ForReferencedFields();

    Map<String, String> getSha1ForReferFields();

    String getSha1ForDatabaseConstraint();

    void setSha1ForReferencedFields(Map<String, String> sha1ForReferencedFields);

    void setSha1ForReferFields(Map<String, String> sha1ForReferFields);

    String getSha1ForUniqueFields(EntityDescriptionRule rule) throws Exception;

    Map<String, String> getSha1ForReferencedFields(EntityDescriptionRule rule) throws Exception;

    Map<String, String> getSha1ForReferFields(EntityDescriptionRule rule) throws Exception;

    String getSha1ForDatabaseConstraint(TableRule rule) throws Exception;

    String uniqueFieldsToString();

    String allFieldsToString();

    int getRowNumber();

    void setRowNumber(int rowNumber);

    String getSourceName();

    void setSourceName(String sourceName);

    void addSourceColumn(String sasColumnName, String sasColumnValue);

    String getSourceColumnValue(String sourceColumnName);

    /**
     * Validate entity (check that all mandatory fields are not null)
     * @return
     */
    boolean isValid();

    int getIntHashForSecondaryFields();

    int getIntHashForSecondaryFields(EntityDescriptionRule rule) throws Exception;

    void setIntHashForSecondaryFields(int intHashForSecondaryFields);

    Object clone() throws CloneNotSupportedException;
}
