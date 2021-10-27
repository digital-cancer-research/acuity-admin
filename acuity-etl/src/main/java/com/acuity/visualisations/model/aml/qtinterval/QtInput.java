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

package com.acuity.visualisations.model.aml.qtinterval;

import com.acuity.visualisations.model.aml.AdverseEvent;
import com.acuity.visualisations.model.aml.Conmed;
import com.acuity.visualisations.model.aml.Ecg;
import com.acuity.visualisations.model.aml.Lab;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Getter
@Builder
public class QtInput {

    private String subject;
    private String studyId;
    private String ecgId;
    private Double qtInterval;
    private Boolean diarrhoea;
    private Boolean conmed;
    private Double magnesium;
    private String magnesiumUnit;
    private Double potassium;
    private String potassiumUnit;

    public static QtInput build(Ecg ecg, List<AdverseEvent> aes, List<Conmed> conmeds, List<Lab> labs) {
        Date ecgDate = ecg.getDate();
        Boolean diarrhoea = null;
        Boolean conmed = null;
        if (ecgDate != null) {
            diarrhoea = aes.stream()
                    .anyMatch(ae -> ae.isDiarrhoea()
                            && ae.getStartDate() != null && ecgDate.after(ae.getStartDate())
                            && (ae.getEndDate() == null || ecgDate.before(ae.getEndDate())));
            conmed = conmeds.stream()
                    .anyMatch(cm -> cm.getStartDate() != null && ecgDate.after(cm.getStartDate())
                            && (cm.getEndDate() == null || ecgDate.before(cm.getEndDate())));
        }
        Lab mgLab = getLastLabOfSameVisitNumberOrEcgDateByLabcode(ecg, labs, "magnesium");
        Lab kLab = getLastLabOfSameVisitNumberOrEcgDateByLabcode(ecg, labs, "potassium");

        return QtInput.builder()
                .subject(ecg.getSubject())
                .studyId(ecg.getStudyId())
                .ecgId(ecg.getId())
                .qtInterval(ecg.getQtInterval())
                .diarrhoea(diarrhoea)
                .conmed(conmed)
                .magnesium(mgLab == null ? null : mgLab.getValue())
                .magnesiumUnit(mgLab == null ? null : mgLab.getUnit())
                .potassium(kLab == null ? null : kLab.getValue())
                .potassiumUnit(kLab == null ? null : kLab.getUnit())
                .build();
    }


    /**
     * This method matches ECG and a laboratory of {@code labcode}.
     * Here is the logic of matching:
     * If ECG has visit number, then we will go through labs comparing with its
     * visit numbers. If the result is non-empty, we will take the latest laboratory.
     * If ECG does not have visit number, then take the latest laboratory of the same day as ECG date
     * @param ecg a patient's ECG
     * @param labs all patient's labs
     * @param labcode labcode
     * @return
     */
    private static Lab getLastLabOfSameVisitNumberOrEcgDateByLabcode(Ecg ecg, List<Lab> labs, String labcode) {
        labs = labs.stream()
                .filter(l -> l.getLabCode() != null && l.getLabCode().toLowerCase().contains(labcode))
                .filter(l -> l.getTestDate() != null)
                .collect(toList());
        // calculate labs of same visit number as ecg's visit number, visit number is not mandatory
        if (ecg.getVisitNumber() != null) {
            List<Lab> labsOfOneDayWithEcg = labs.stream()
                    .filter(l -> ecg.getVisitNumber().equals(l.getVisitNumber()))
                    .collect(toList());
            if (!labsOfOneDayWithEcg.isEmpty()) {
                return labsOfOneDayWithEcg.stream()
                        .max(comparing(Lab::getTestDate)).orElse(null);
            }
        }
        // if no match by visit number found then choose of max sample date within ecg date
        return labs.stream()
                .filter(lab -> DateUtils.isSameDay(ecg.getDate(), lab.getTestDate()))
                .max(comparing(Lab::getTestDate)).orElse(null);
    }

}
