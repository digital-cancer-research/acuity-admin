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

package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.dal.util.State;
import com.acuity.visualisations.model.output.OutputEntity;
import com.acuity.visualisations.transform.entity.EntityBaseRule;
import com.acuity.visualisations.transform.entity.EntityDescriptionRule;
import com.acuity.visualisations.transform.entity.EntityFieldSetRule;
import com.acuity.visualisations.transform.entity.EntityReferedByRule;
import com.acuity.visualisations.transform.table.TableBaseRule;
import com.acuity.visualisations.transform.table.TableFieldSetRule;
import com.acuity.visualisations.transform.table.TableRule;
import com.acuity.visualisations.util.HashUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import static com.acuity.visualisations.data.util.Util.currentDate;

public abstract class TimestampedEntity implements OutputEntity {

    private String guid;
    private State state;

    private Date dateCreated;
    private Date dateUpdated;

    private String projectName;
    private String studyName;

    private String sha1ForUniqueFields;

    private int intHashForSecondaryFields;

    private Map<String, String> sha1ForReferencedFields = new HashMap<String, String>();
    private Map<String, String> sha1ForReferFields = new HashMap<String, String>();
    private String sha1ForDatabaseConstraint;

    private String sourceName;
    private int rowNumber;
    private Map<String, String> sourceRecord = new HashMap<String, String>();

    public TimestampedEntity() {
        dateCreated = currentDate();
        dateUpdated = currentDate();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getId() {
        return guid;
    }

    public void setId(String guid) {
        this.guid = guid;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void initId() {
        if (this instanceof Project || this instanceof Study) {
            this.guid = UUID.randomUUID().toString().replaceAll("-", "");
            this.state = State.JUST_INSERTED;
        }
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public void update() {
        dateUpdated = currentDate();
    }

    public String getSha1ForUniqueFields() {
        return sha1ForUniqueFields;
    }

    public void setSha1ForUniqueFields(String sha1ForUniqueFields) {
        this.sha1ForUniqueFields = sha1ForUniqueFields;
    }


    public Map<String, String> getSha1ForReferencedFields() {
        return sha1ForReferencedFields;
    }

    public String getFirstSha1ForReferencedFields() {
        Iterator<Entry<String, String>> iterator = sha1ForReferencedFields.entrySet().iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        return iterator.next().getValue();
    }

    public void setSha1ForReferencedFields(Map<String, String> sha1ForReferencedFields) {
        this.sha1ForReferencedFields = sha1ForReferencedFields;
    }

    public Map<String, String> getSha1ForReferFields() {
        return sha1ForReferFields;
    }

    public void setSha1ForReferFields(Map<String, String> sha1ForReferFields) {
        this.sha1ForReferFields = sha1ForReferFields;
    }

    public String getSha1ForDatabaseConstraint() {
        return sha1ForDatabaseConstraint;
    }

    public void setSha1ForDatabaseConstraint(String sha1ForDatabaseConstraint) {
        this.sha1ForDatabaseConstraint = sha1ForDatabaseConstraint;
    }

    public String getSha1ForUniqueFields(EntityDescriptionRule rule) throws Exception {
        List<EntityBaseRule> uniqueFieldsRules = rule.getUniqueFields().getField();
        return HashUtil.getSha1ForFieldList(this, uniqueFieldsRules);
    }

    public void setIntHashForSecondaryFields(int intHashForSecondaryFields) {
        this.intHashForSecondaryFields = intHashForSecondaryFields;
    }

    public int getIntHashForSecondaryFields() {
        return intHashForSecondaryFields;
    }

    public int getIntHashForSecondaryFields(EntityDescriptionRule rule) throws Exception {
        EntityFieldSetRule secondaryFields = rule.getSecondaryFields();
        if (secondaryFields == null) {
            return 0;
        }
        List<EntityBaseRule> secondaryFieldsRules = secondaryFields.getField();
        return HashUtil.getIntHashForFieldList(this, secondaryFieldsRules);
    }

    public Map<String, String> getSha1ForReferencedFields(EntityDescriptionRule rule) throws Exception {
        Map<String, String> sha1ForReferencedFields = new HashMap<>();
        List<EntityReferedByRule> referencedFieldsRules = rule.getReferredBy();
        for (EntityReferedByRule fieldSetRule : referencedFieldsRules) {
            String sha1ForFieldList;
            if (fieldSetRule.isEqualToUniqueSet()) {
                sha1ForFieldList = getSha1ForUniqueFields();
            } else {
                sha1ForFieldList = HashUtil.getSha1ForFieldList(this, fieldSetRule.getField());
            }
            sha1ForReferencedFields.put(fieldSetRule.getName(), sha1ForFieldList);
        }
        return sha1ForReferencedFields;
    }

    public Map<String, String> getSha1ForReferFields(EntityDescriptionRule rule) throws Exception {
        Map<String, String> sha1ForReferFields = new HashMap<String, String>();
        List<EntityFieldSetRule> referencedFieldsRules = rule.getReferBy();
        for (EntityFieldSetRule fieldSetRule : referencedFieldsRules) {
            String sha1ForFieldList = HashUtil.getSha1ForFieldList(this, fieldSetRule.getField());
            sha1ForReferFields.put(fieldSetRule.getName(), sha1ForFieldList);
        }
        return sha1ForReferFields;
    }

    public String getSha1ForDatabaseConstraint(TableRule rule) throws Exception {
        TableFieldSetRule uniqueConstraint = rule.getUniqueFields();
        if (uniqueConstraint == null) {
            return null;
        }
        List<TableBaseRule> uniqueFields = uniqueConstraint.getField();
        return HashUtil.getSha1ForFieldListForUniqueConstraint(this, uniqueFields);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().isAssignableFrom(this.getClass())) {
            return false;
        }
        TimestampedEntity entity = (TimestampedEntity) obj;
        return this.getSha1ForUniqueFields().equals(entity.getSha1ForUniqueFields())
                && this.getIntHashForSecondaryFields() == entity.getIntHashForSecondaryFields();
    }

    @Override
    public int hashCode() {
        return getSha1ForUniqueFields().hashCode();
    }

    @Override
    public String uniqueFieldsToString() {
        return "";
    }

    public String allFieldsToString() {
        return "";
    }

    @Override
    public String toString() {
        return uniqueFieldsToString();
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public void addSourceColumn(String sasColumnName, String sasColumnValue) {
        sourceRecord.put(sasColumnName, sasColumnValue);
    }

    @Override
    public String getSourceColumnValue(String sourceColumnName) {
        return sourceRecord.get(sourceColumnName);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
