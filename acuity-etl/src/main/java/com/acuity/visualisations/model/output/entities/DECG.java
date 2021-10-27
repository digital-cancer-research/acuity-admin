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
