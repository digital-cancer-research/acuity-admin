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
import java.util.ArrayList;
import java.util.List;

import com.acuity.visualisations.model.output.SmartEntity;
import com.acuity.visualisations.model.output.SplitEntity;
import com.acuity.visualisations.model.acuityfield.AcuityField;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ECG extends TimestampedEntity implements SmartEntity, SplitEntity<EG> {

    private String testGuid;
    private Integer qrs;
    private Integer rr;
    private Integer pr;
    private Integer qt;
    private Integer qtcf;
    private String evaluation;
    private String abnormality;
    private String significant;
    private String subject;
    private String part;

    @AcuityField(transform = AcuityFieldTransformation.MEASUREMENT_EVENT_12_00_00)
    private LocalDateTime date;

    public List<EG> split() {
        List<EG> out = new ArrayList<>();
        if (qrs != null) {
            out.add(new EG(subject, part, date, "Summary (Mean) QRS Duration", new BigDecimal(qrs),
                    null, evaluation, abnormality, significant));
        }
        if (rr != null) {
            out.add(new EG(subject, part, date, "Summary (Mean) RR Duration", new BigDecimal(rr),
                    null, evaluation, abnormality, significant));
        }
        if (pr != null) {
            out.add(new EG(subject, part, date, "Summary (Mean) PR Duration", new BigDecimal(pr),
                    null, evaluation, abnormality, significant));
        }
        if (qt != null) {
            out.add(new EG(subject, part, date, "Summary (Mean) QT Duration", new BigDecimal(qt),
                    null, evaluation, abnormality, significant));
        }
        if (qtcf != null) {
            out.add(new EG(subject, part, date, "QTcF - Fridericia's Correction Formula", new BigDecimal(qtcf),
                    null, evaluation, abnormality, significant));
        }
        return out;
    }

    public ECG() {
        initId();
    }

    /**
     * Calculate QTcF field from QT and RR, if QTcF is absent.
     * https://en.wikipedia.org/wiki/QT_interval
     */
    @Override
    public void complete() {
        if (qtcf == null && qt != null && qt > 0 && rr != null && rr > 0) {
            qtcf = (int) (qt / Math.pow(rr / 1000d, 1 / 3d));
        }
    }
}
