package com.acuity.visualisations.model.output.entities;

import com.acuity.visualisations.model.output.SmartEntity;
import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@ToString(callSuper = true)
public class PatientData extends TimestampedEntity implements SmartEntity {
    private String subject;
    private String part;
    private String measurementName;
    private BigDecimal value;
    private String unit;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime measurementDate;
    @AcuityField(transform = AcuityFieldTransformation.INITIATING_EVENT_00_00_01)
    private LocalDateTime reportDate;
    private String comment;
    private String sourceType;
    private String patientGuid;

    // from patient's device
    private String sourceGiud;
    private String sourceDeviceName;
    private String sourceDeviceVersion;
    private String sourceDeviceType;


    @Override
    public void complete() {
        if (sourceType != null) {
            setSourceType(StringUtils.capitalize(sourceType.toLowerCase()));
        }
    }
}
