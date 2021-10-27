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

package com.acuity.visualisations.report.entity;

import com.acuity.visualisations.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class contains report data relating to a particular source data file
 */
public class FileReport {

    private DB db = DBMaker
            .newTempFileDB()
            .transactionDisable()
            .deleteFilesAfterClose()
            .asyncWriteEnable()
//            .mmapFileEnableIfSupported()
            .make();

    /**
     * A map of the source column name as the key and the ACUITY column name as the value
     */
    @Getter
    private Map<String, Column> columns = new HashMap<>();

    /**
     * A set containing the raw subjects
     */
    @Getter
    private Set<String> rawSubjects = new HashSet<>();

    /**
     * A set containing the ACUITY data entities
     */
    @Getter
    private Set<String> acuityEntities = new HashSet<>();

    /**
     * A map of the ACUITY entities that are not mapped to source fields.
     */
    @Getter
    private Map<String, List<String>> unmappedEntityFields = new HashMap<>();

    /**
     * The number of rows that were successfully uploaded during the ETL process
     */
    @Getter
    private int rowsUploaded = 0;

    /**
     * File size in bytes
     */
    @Getter
    @Setter
    private long fileSize;

    /**
     * Closes MapDB
     */
    public void closeMapDB() {
        db.close();
    }

    public void setColumn(String rawColName) {
        getColumn(rawColName).isMapped = false;
    }

    /**
     * Adds a parsed subject to the rawSubjects local instance variable set.
     *
     * @param subject The subject to add
     */
    public void addParsedSubject(String subject) {
        rawSubjects.add(subject);
    }

    /**
     * Adds a ACUITY data entity to the acuityEntities local instance variable set.
     *
     * @param entityName The name of the entity to add
     */
    public void addAcuityEntity(String entityName) {
        acuityEntities.add(entityName);
    }

    /**
     * Sets the given column to be flagged as being mapped
     *
     * @param rawColName The data source column name
     * @param colName    The ACUITY column name
     * @param entityName The name of the ACUITY entity that the column belongs to
     */
    public void setColumnMapped(String rawColName, String colName, String entityName) {
        getColumn(rawColName).isMapped = true;
        getColumn(rawColName).colName = colName;
        getColumn(rawColName).entityName = entityName;
    }

    /**
     * Associates a data source error to the given source column
     *
     * @param rawColName The raw data source column
     * @param errorDesc  The description of the error
     */
    public void addDataSourceError(String rawColName, String errorDesc) {
        Column column = getColumn(rawColName);
        column.colErrors.add(new Pair<ColumnErrorType, String>(ColumnErrorType.DATA_SOURCE_ERROR, errorDesc));
    }

    /**
     * Increments the number of rows that have been uploaded
     */
    public void incRowsUploaded() {
        rowsUploaded++;
    }

    /**
     * Increments the number of parsed columns
     *
     * @param rawColName The name of the raw data source column name
     */
    public void incColumnParsed(String rawColName) {
        Column column = getColumn(rawColName);
        column.parsedTotal++;
    }

    /**
     * Associates a column error to the given raw data source column
     *
     * @param rawColName The name of the raw data source column
     * @param errorDesc  The description of the error
     */
    public void addColumnError(String rawColName, String errorDesc) {
        Column column = getColumn(rawColName);
        column.colErrors.add(new Pair<ColumnErrorType, String>(ColumnErrorType.MAPPING_ERROR, errorDesc));
    }

    /**
     * Adds a ACUITY entity field that isn't mapped to the report
     *
     * @param entityName The internal ACUITY entity name
     * @param fieldName  The name of the field
     */
    public void addEntityFieldNotMappedError(String entityName, String fieldName) {
        if (!unmappedEntityFields.containsKey(entityName)) {
            unmappedEntityFields.put(entityName, new ArrayList<String>());
        }
        unmappedEntityFields.get(entityName).add(fieldName);
    }

    /**
     * Associates a value error to the source data column and value
     *
     * @param rawColName The name of the raw data source column
     * @param rawValue   The value with an error
     * @param isCritical Set to true if the value cannot be parsed, otherwise
     *                   set to false if the value needed modification prior
     *                   to parsing. E.g. Changing a number string to a float
     * @param errorDesc  A description of the error
     */
    public void addValueError(String rawColName, String rawValue, boolean isCritical, String errorDesc) {
        Column column = getColumn(rawColName);
        Map<ValueError, Integer> valueErrorCountMap = column.getValueErrorCountMap();
        ValueError valueError = new ValueError(isCritical ? ValueErrorType.PARSE_ERROR : ValueErrorType.PARSE_WARNING, errorDesc, rawValue);

        column.parseFailures++;
        column.getValErrors().add(valueError);

        if (!valueErrorCountMap.containsKey(valueError)) {
            valueErrorCountMap.put(valueError, 0);
        }
        valueErrorCountMap.put(valueError, valueErrorCountMap.get(valueError) + 1);
    }

    /**
     * Gets the column object associated with the raw column name
     *
     * @param rawColName The source column name
     * @return The Column object associated with the raw column name
     */
    private Column getColumn(String rawColName) {
        return columns.computeIfAbsent(rawColName, s -> new Column(rawColName, false));
    }

    /**
     * Represents a source data column
     */
    public final class Column implements Serializable {

        private static final long serialVersionUID = -615503619101861202L;
        /**
         * The total number of times the column has been parsed
         */
        private int parsedTotal;

        /**
         * The total number of times the column has not been parsed
         */
        private int parseFailures;

        /**
         * The ACUITY column name
         */
        private String colName;

        /**
         * The data source column name
         */
        private String rawColName;

        /**
         * Whether the column has been mapped to a ACUITY data field
         */
        private boolean isMapped = false;

        /**
         * The name of the ACUITY entity that this column belongs to
         */
        private String entityName;

        /**
         * A list of errors associated with the column
         */
        private List<Pair<ColumnErrorType, String>> colErrors = new ArrayList<>();

        /**
         * A map of errors counts associated with the column
         */
        private Map<ValueError, Integer> valueErrorCountMap = new HashMap<>();

        /**
         * Creates a new Column object with the given source column name
         *
         * @param rawColName The source column name
         * @param mapped     Whether the column has been mapped to a ACUITY field
         */
        private Column(String rawColName, boolean mapped) {
            this.rawColName = rawColName;
            isMapped = mapped;
        }

        public int getParsedTotal() {
            return parsedTotal;
        }

        public int getParseFailures() {
            return parseFailures;
        }

        public String getRawColName() {
            return rawColName;
        }

        public boolean isMapped() {
            return isMapped;
        }

        public List<Pair<ColumnErrorType, String>> getColErrors() {
            return colErrors;
        }

        public Map<ValueError, Integer> getValueErrorCountMap() {
            return valueErrorCountMap;
        }

        public Set<ValueError> getValErrors() {
            db.checkNotClosed();
            return db.getHashSet(rawColName);
        }

        public String getFullName() {
            return this.entityName + "." + this.colName;
        }
    }

    /**
     * Contains information for errors associated with a source value
     */
    public static final class ValueError implements Serializable {

        private static final long serialVersionUID = -5127539239024867557L;
        /**
         * The type of error. A warning or error.
         */
        private ValueErrorType errorType;

        /**
         * A description of the error
         */
        private String errorDesc;

        /**
         * The source value
         */
        private String rawValue;

        /**
         * Creates a new instance of the class with the given values
         *
         * @param errorType The type of error. A warning or error.
         * @param errorDesc A description of the error.
         * @param rawValue  The source data value
         */
        private ValueError(ValueErrorType errorType, String errorDesc, String rawValue) {
            this.errorType = errorType;
            this.errorDesc = errorDesc;
            this.rawValue = rawValue;
        }

        public ValueErrorType getErrorType() {
            return errorType;
        }

        public String getErrorDesc() {
            return errorDesc;
        }

        public String getRawValue() {
            return rawValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ValueError)) {
                return false;
            }

            ValueError that = (ValueError) o;

            return (errorDesc != null ? errorDesc.equals(that.errorDesc) : that.errorDesc == null)
                    && errorType == that.errorType
                    && (rawValue != null ? rawValue.equals(that.rawValue) : that.rawValue == null);
        }

        @Override
        public int hashCode() {
            int result = errorType != null ? errorType.hashCode() : 0;
            result = 31 * result + (errorDesc != null ? errorDesc.hashCode() : 0);
            result = 31 * result + (rawValue != null ? rawValue.hashCode() : 0);
            return result;
        }
    }
}
