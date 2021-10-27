
package com.acuity.visualisations.model.output.entities;

import java.time.LocalDateTime;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DiseaseExtent extends TimestampedEntity {
    protected String part;
    protected String subject;

    private String patientGuid;

    private String metastaticLocallyAdvanced;
    private String siteOfLocalMetastaticDisease;
    private String otherLocallyAdvancedSites;
    private String otherMetastaticSites;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime visitDate;
    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime recentProgressionDate;
    private String recurrenceOfEarlierCancer;
}
