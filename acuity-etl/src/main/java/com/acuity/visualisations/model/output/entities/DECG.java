package com.acuity.visualisations.model.output.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import com.acuity.visualisations.data.util.Util;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.builder.ToStringBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
public class DECG extends TimestampedEntity {

	private String testGuid;
	private String measurementLabel;
	private String measurementValue;
	private String abnormality;
	private String evaluation;
	private String significant;
	private String subject;
	private String part;

	private String protocolScheduleTimepoint;
	private String method;
	private Integer beatGroupNumber;
	private Integer beatNumberWithinBeatGroup;
	private Integer numberOfBeatsInAverageBeat;
	private BigDecimal beatGroupLengthInSec;
	private String comment;

	@AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
	private LocalDateTime date;

	@Override
    public String uniqueFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("subject", subject).
                append("part", part).
                append("date", date).
                toString();
    }

    @Override
    public String allFieldsToString() {
        return new ToStringBuilder(this, Util.getToStringStyle()).
                append("subject", subject).
                append("part", part).
                append("date", date).
                append("measurementLabel", measurementLabel).
                append("measurementValue", measurementValue).
                append("abnormality", abnormality).
                toString();
    }

}
