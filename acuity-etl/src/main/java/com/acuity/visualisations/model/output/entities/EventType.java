package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.data.util.Util;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Locale;

public class EventType extends TimestampedEntity {

    private String studyGuid;
    private String soc;
    private String pt;
    private String hlt;
    private String llt;
    private BigDecimal medDRAVersion;

    public EventType() {
        initId();
    }

    public String getSOC() {
        if (soc == null) {
            return null;
        }
        return soc.toUpperCase(Locale.ENGLISH);
    }

    public void setSOC(String soc) {
        if (soc == null) {
            this.soc = null;
        } else {
            this.soc = soc.toUpperCase(Locale.ENGLISH);
        }
    }

    public String getPT() {
        if (pt == null) {
            return null;
        }
        return pt.toUpperCase(Locale.ENGLISH);
    }

    public void setPT(String pt) {
        if (pt == null) {
            this.pt = null;
        } else {
            this.pt = pt.toUpperCase(Locale.ENGLISH);
        }
    }

    public String getHLT() {
        if (hlt == null) {
            return null;
        }
        return hlt.toUpperCase(Locale.ENGLISH);
    }

    public void setHLT(String hlt) {
        if (hlt == null) {
            this.hlt = null;
        } else {
            this.hlt = hlt.toUpperCase(Locale.ENGLISH);
        }
    }

    public String getLLT() {
        if (llt == null) {
            return null;
        }
        return llt.toUpperCase(Locale.ENGLISH);
    }

    public void setLLT(String llt) {
        if (llt == null) {
            this.llt = null;
        } else {
            this.llt = llt.toUpperCase(Locale.ENGLISH);
        }
    }

    public String getStudyGuid() {
        return studyGuid;
    }

    public void setStudyGuid(String studyGuid) {
        this.studyGuid = studyGuid;
    }

    public BigDecimal getMedDRAVersion() {
        return medDRAVersion;
    }

    public void setMedDRAVersion(BigDecimal medDRAVersion) {
        this.medDRAVersion = medDRAVersion;
    }

    @Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("PT", pt).append("HLT", hlt).append("SOC", soc).toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).append("PT", pt).append("HLT", hlt).append("LLT", llt)
                .append("SOC", soc)
                .append("medDRAVersion", medDRAVersion)
                .toString();
    }

}
