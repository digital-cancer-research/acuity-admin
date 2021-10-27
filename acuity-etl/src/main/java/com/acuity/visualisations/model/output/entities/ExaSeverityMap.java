package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.data.util.Util;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ExaSeverityMap extends TimestampedEntity {

    private String studyGuid;

    private String excClass;

    private String depotGcs;
    private String syscortTrt;
    private String icsTrt;
    private String antibioticsTrt;
    private String hospit;
    private String emerTrt;

    public String getStudyGuid() {
        return studyGuid;
    }

    public void setStudyGuid(String studyGuid) {
        this.studyGuid = studyGuid;
    }

    public String getExcClass() {
        return excClass;
    }

    public void setExcClass(String excClass) {
        this.excClass = excClass;
    }

    public String getDepotGcs() {
        return depotGcs;
    }

    public void setDepotGcs(String depotGcs) {
        this.depotGcs = depotGcs;
    }

    public String getSyscortTrt() {
        return syscortTrt;
    }

    public void setSyscortTrt(String syscortTrt) {
        this.syscortTrt = syscortTrt;
    }

    public String getIcsTrt() {
        return icsTrt;
    }

    public void setIcsTrt(String icsTrt) {
        this.icsTrt = icsTrt;
    }

    public String getAntibioticsTrt() {
        return antibioticsTrt;
    }

    public void setAntibioticsTrt(String antibioticsTrt) {
        this.antibioticsTrt = antibioticsTrt;
    }

    public String getHospit() {
        return hospit;
    }

    public void setHospit(String hospit) {
        this.hospit = hospit;
    }

    public String getEmerTrt() {
        return emerTrt;
    }

    public void setEmerTrt(String emerTrt) {
        this.emerTrt = emerTrt;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("depotGcs", depotGcs).
                append("syscortTrt", syscortTrt).
                append("icsTrt", icsTrt).
                append("antibioticsTrt", antibioticsTrt).
                append("hospit", hospit).
                append("emerTrt", emerTrt).
                toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("excClass", excClass).
                append("depotGcs", depotGcs).
                append("syscortTrt", syscortTrt).
                append("icsTrt", icsTrt).
                append("antibioticsTrt", antibioticsTrt).
                append("hospit", hospit).
                append("emerTrt", emerTrt).
                toString();
    }

}
