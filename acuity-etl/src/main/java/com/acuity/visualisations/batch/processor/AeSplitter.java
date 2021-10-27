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

package com.acuity.visualisations.batch.processor;


import com.acuity.visualisations.model.output.entities.AE;
import com.acuity.visualisations.model.output.entities.AdverseEvent;
import com.acuity.visualisations.model.output.entities.AeActionTaken;
import com.acuity.visualisations.model.output.entities.AeCausality;
import com.acuity.visualisations.model.output.entities.AeNumActionTaken;
import com.acuity.visualisations.model.output.entities.AeNumCycleDelayed;
import com.acuity.visualisations.model.output.entities.AeSeverity;
import com.acuity.visualisations.model.output.entities.ConcomitantMedSchedule;
import com.acuity.visualisations.model.output.entities.Drug;
import com.acuity.visualisations.model.output.entities.MedDosingSchedule;
import com.acuity.visualisations.model.output.entities.TimestampedEntity;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformation;
import com.acuity.visualisations.model.acuityfield.AcuityFieldTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

public final class AeSplitter {
private static final String NOT_ALIGNED_MESSAGE = "{} not aligned";
    private static final Logger LOGGER = LoggerFactory.getLogger(AeSplitter.class);

    private  AeSplitter() {
    }

    /**
     * Create set of items (AE, Drugs, Causalities, Severities, Actions Taken) from the AdverseEvent
     *
     * @param adverseEvent
     * @return
     */
    public static List<TimestampedEntity> splitAdverseEventToEntities(AdverseEvent adverseEvent) {
        List<TimestampedEntity> out = new ArrayList<>();

        // Create Investigational Drugs
        Drug[] ipDrugs = null;
        int ipDrugsCount = 0;
        if (adverseEvent.getIpDrugs() != null) {
            ipDrugs = new Drug[adverseEvent.getIpDrugs().length];
            for (int i = 0; i < adverseEvent.getIpDrugs().length; i++) {
                Object drugName = adverseEvent.getIpDrugs()[i];
                if (!isEmpty(drugName)) {
                    Drug ipDrug = new Drug(drugName.toString());
                    ipDrugs[i] = ipDrug;
                    out.add(ipDrug);
                    ipDrugsCount++;
                }
            }
        }

        //Create Additional Drugs
        Drug[] adDrugs = null;
        int adDrugsCount = 0;
        if (adverseEvent.getAdDrugs() != null) {
            adDrugs = new Drug[adverseEvent.getAdDrugs().length];
            for (int i = 0; i < adverseEvent.getAdDrugs().length; i++) {
                Object drugName = adverseEvent.getAdDrugs()[i];
                if (!isEmpty(drugName)) {
                    Drug adDrug = new Drug(drugName.toString());
                    adDrugs[i] = adDrug;
                    out.add(adDrug);
                    adDrugsCount++;
                }
            }
        }

        //Create Adverse Event
        AE ae = new AE();
        ae.setAeText(adverseEvent.getAeText());
        ae.setComment(adverseEvent.getComment());
        ae.setSerious(adverseEvent.getSerious());
        ae.setNumber(adverseEvent.getNumber());

        ae.setOutcome(adverseEvent.getOutcome());
        ae.setDoseLimitingToxicity(adverseEvent.getDoseLimitingToxicity());
        ae.setTimePoint(adverseEvent.getTimePoint());
        ae.setImmuneMediated(adverseEvent.getImmuneMediated());
        ae.setInfusionReaction(adverseEvent.getInfusionReaction());
        ae.setRequiredTreatment(adverseEvent.getRequiredTreatment());
        ae.setCausedSubjectWithdrawal(adverseEvent.getCausedSubjectWithdrawal());

        ae.setSubject(adverseEvent.getSubject());
        ae.setPart(adverseEvent.getPart());
        ae.setPT(adverseEvent.getPT());
        ae.setHLT(adverseEvent.getHLT());
        ae.setSOC(adverseEvent.getSOC());
        ae.setSuspectedEndpoint(adverseEvent.getSuspectedEndpoint());
        ae.setSuspectedEndpointCategory(adverseEvent.getSuspectedEndpointCategory());
        ae.setAeOfSpecialInterest(adverseEvent.getAeOfSpecialInterest());
        ae.setStartDate(adverseEvent.getStartDate());
        out.add(ae);

        //Create Causalities
        if (ipDrugsCount > 0) {
            pushAeCausality(out, ae, ipDrugs, adverseEvent.getCausalityForIpDrugs(),
                    "Causalities for Investigational Drugs");
        }

        if (adDrugsCount > 0) {
            pushAeCausality(out, ae, adDrugs, adverseEvent.getCausalityForAdDrugs(),
                    "Causalities for Additional Drugs");
        }

        //Create Severities
        AeSeverity severity = new AeSeverity();
        severity.setAe(ae);
        severity.setStartDate(adverseEvent.getStartDate());
        severity.setEndDate(adverseEvent.getEndDate());

        severity.setSeverity(adverseEvent.getMaxSeverity());

        out.add(severity);

        if (ipDrugsCount > 0) {
            pushAeActionsTaken(out, severity, ipDrugs, adverseEvent.getInitialActionTakenForIpDrugs(),
                    "Initial Actions Taken for Investigational Drugs");
        }

        if (adDrugsCount > 0) {
            pushAeActionsTaken(out, severity, adDrugs, adverseEvent.getInitialActionTakenForAdDrugs(),
                    "Initial Actions Taken for Additional Drugs");
        }

        Object[] gradeChanges = adverseEvent.getCtcGradeChanges();
        Object[] changeDates = adverseEvent.getCtcGradeChangeDates();


        if (gradeChanges != null && changeDates != null && gradeChanges.length == changeDates.length) {
            boolean hasNonEmptyPairsToIterate = false;

            for (Object gradeChange : gradeChanges) {
                if (!isEmpty(gradeChange)) {
                    hasNonEmptyPairsToIterate = true;
                    severity.setSeverity(adverseEvent.getStartingCtcGrade());
                    break;
                }
            }

            if (!hasNonEmptyPairsToIterate) {
                return out;
            }

            int changesCount = 0;
            AeSeverity[] severities = new AeSeverity[gradeChanges.length];

            for (int i = 0; i < gradeChanges.length; i++) {
                if (changeDates[i] != null && !isEmpty(gradeChanges[i])) {
                    LocalDateTime nextStartDate = AcuityFieldTransformer.transform((LocalDateTime) changeDates[i],
                            AcuityFieldTransformation.INITIATING_EVENT_00_00_01);
                    LocalDateTime prevEndDate = ((LocalDateTime) changeDates[i]).minusSeconds(1);

                    severity.setEndDate(prevEndDate);

                    AeSeverity severity1 = new AeSeverity();
                    severity1.setAe(ae);
                    severity1.setStartDate(nextStartDate);
                    severity1.setEndDate(adverseEvent.getEndDate());
                    severity1.setSeverity((String) gradeChanges[i]);
                    out.add(severity1);
                    severities[i] = severity1;

                    severity = severity1;
                    changesCount++;
                }
            }

            LOGGER.debug("Grade changes: {}/{}", changesCount, gradeChanges.length);

            if (ipDrugsCount > 0) {
                pushAeActionsTaken(out, severities, ipDrugs, adverseEvent.getChangedActionTakenForIpDrugs(), gradeChanges,
                        "Changed Actions Taken for Investigational Drugs");
            }

            if (adDrugsCount > 0) {
                pushAeActionsTaken(out, severities, adDrugs, adverseEvent.getChangedActionTakenForAdDrugs(), gradeChanges,
                        "Changed Actions Taken for Additional Drugs");
            }
        } else {
            LOGGER.warn("Grade changes and dates not aligned");
        }

        return out;
    }


    /**
     * Extract Causality entities and push them to the entity stream
     *
     * @param targetEntityStream
     * @param ae
     * @param drugs
     * @param causalities
     * @param comment
     * @return number of created entities
     */
    private static int pushAeCausality(List<TimestampedEntity> targetEntityStream, AE ae, Drug[] drugs, Object[] causalities, String comment) {
        int count = 0;
        if (causalities != null && drugs.length == causalities.length) {
            for (int i = 0; i < causalities.length; i++) {
                if (drugs[i] != null && !isEmpty(causalities[i])) {
                    AeCausality aeCausality = new AeCausality(ae, drugs[i], causalities[i].toString());
                    targetEntityStream.add(aeCausality);
                    count++;
                }
            }
        } else {
            LOGGER.warn(NOT_ALIGNED_MESSAGE, comment);
        }
        return count;
    }

    /**
     * Extract Action Taken entities and push them to the entity stream
     *
     * @param targetEntityStream
     * @param severity
     * @param drugs
     * @param actionsTaken
     * @param comment
     * @return number of created entities
     */
    private static int pushAeActionsTaken(List<TimestampedEntity> targetEntityStream, AeSeverity severity, Drug[] drugs,
                                          Object[] actionsTaken, String comment) {
        int count = 0;
        if (actionsTaken != null && drugs.length == actionsTaken.length) {
            for (int i = 0; i < actionsTaken.length; i++) {
                if (drugs[i] != null && !isEmpty(actionsTaken[i])) {
                    AeActionTaken actionTaken = new AeActionTaken(severity, drugs[i], actionsTaken[i].toString());
                    targetEntityStream.add(actionTaken);
                    count++;
                }
            }
        } else {
            LOGGER.warn(NOT_ALIGNED_MESSAGE, comment);
        }
        return count;
    }

    private static int pushAeActionsTaken(List<TimestampedEntity> targetEntityStream, AeSeverity[] severities, Drug[] drugs,
                                          Object[] actionsTaken, Object[] gradeChanges, String comment) {
        int count = 0;
        if (actionsTaken != null && gradeChanges != null && (gradeChanges.length * drugs.length) == actionsTaken.length) {
            for (int i = 0; i < gradeChanges.length; i++) {
                if (severities[i] != null) {
                    pushAeActionsTaken(targetEntityStream, severities[i], drugs,
                            Arrays.copyOfRange(actionsTaken, i * drugs.length, (i + 1) * drugs.length), comment);
                    count++;
                }
            }
        } else {
            LOGGER.warn(NOT_ALIGNED_MESSAGE, comment);
        }
        return count;
    }

    public static List<TimestampedEntity> splitAeNumValues(MedDosingSchedule medDosingSchedule) {
        List<TimestampedEntity> out = new ArrayList<>();
        if (medDosingSchedule.getAeNumCausedActionTaken() != null) {
            for (Object numActionTaken : medDosingSchedule.getAeNumCausedActionTaken()) {
                AeNumActionTaken actionTakenEntity = new AeNumActionTaken();
                actionTakenEntity.setNumActionTaken(numActionTaken);
                actionTakenEntity.setDrug(medDosingSchedule.getDrug());
                actionTakenEntity.setStartDate(medDosingSchedule.getStartDate());
                actionTakenEntity.setSubject(medDosingSchedule.getSubject());
                actionTakenEntity.setProjectName(medDosingSchedule.getProjectName());
                actionTakenEntity.setStudyName(medDosingSchedule.getStudyName());
                out.add(actionTakenEntity);
            }
        }
        if (medDosingSchedule.getAeNumCausedTreatmentCycleDelayed() != null) {
            for (Object numCycleDelayed : medDosingSchedule.getAeNumCausedTreatmentCycleDelayed()) {
                AeNumCycleDelayed cycleDelayedEntity = new AeNumCycleDelayed();
                cycleDelayedEntity.setNumCycleDelayed(numCycleDelayed);
                cycleDelayedEntity.setDrug(medDosingSchedule.getDrug());
                cycleDelayedEntity.setStartDate(medDosingSchedule.getStartDate());
                cycleDelayedEntity.setSubject(medDosingSchedule.getSubject());
                cycleDelayedEntity.setProjectName(medDosingSchedule.getProjectName());
                cycleDelayedEntity.setStudyName(medDosingSchedule.getStudyName());
                out.add(cycleDelayedEntity);
            }
        }
        return out;
    }

    static List<TimestampedEntity> splitAeNumConmeds(ConcomitantMedSchedule concomitantMedSchedule) throws CloneNotSupportedException {
        List<TimestampedEntity> out = new ArrayList<>();
        if (concomitantMedSchedule.getAeNum() != null) {
            List<Object> nonEmptyAeNums = Arrays.stream(concomitantMedSchedule.getAeNum())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (nonEmptyAeNums.size() > 0) {
                for (Object aeNum : nonEmptyAeNums) {
                    ConcomitantMedSchedule conmedEntity = (ConcomitantMedSchedule) concomitantMedSchedule.clone();
                    conmedEntity.setAeNumber((Integer) aeNum);
                    out.add(conmedEntity);
                }
            } else {
                out.add(concomitantMedSchedule);
            }
        } else {
            out.add(concomitantMedSchedule);
        }
        return out;
    }
}
