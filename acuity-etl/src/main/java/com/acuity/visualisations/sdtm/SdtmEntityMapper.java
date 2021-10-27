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

package com.acuity.visualisations.sdtm;

import com.acuity.visualisations.batch.processor.SdtmParsers;
import com.acuity.visualisations.exception.InvalidDataFormatException;
import com.acuity.visualisations.model.output.entities.AdverseEvent;
import com.acuity.visualisations.model.output.entities.Chemotherapy;
import com.acuity.visualisations.model.output.entities.ConcomitantMedSchedule;
import com.acuity.visualisations.model.output.entities.Death;
import com.acuity.visualisations.model.output.entities.ECG;
import com.acuity.visualisations.model.output.entities.EventType;
import com.acuity.visualisations.model.output.entities.LVEF;
import com.acuity.visualisations.model.output.entities.Laboratory;
import com.acuity.visualisations.model.output.entities.MedDosDisc;
import com.acuity.visualisations.model.output.entities.MedDosingSchedule;
import com.acuity.visualisations.model.output.entities.Medicine;
import com.acuity.visualisations.model.output.entities.Patient;
import com.acuity.visualisations.model.output.entities.PrimaryTumourLocation;
import com.acuity.visualisations.model.output.entities.Radiotherapy;
import com.acuity.visualisations.model.output.entities.RecistAssessment;
import com.acuity.visualisations.model.output.entities.RecistNonTargetLesion;
import com.acuity.visualisations.model.output.entities.RecistTargetLesion;
import com.acuity.visualisations.model.output.entities.SeriousAdverseEvent;
import com.acuity.visualisations.model.output.entities.Test;
import com.acuity.visualisations.model.output.entities.Visit;
import com.acuity.visualisations.model.output.entities.Vital;
import com.acuity.visualisations.model.output.entities.WithdrawalCompletion;
import com.acuity.visualisations.sdtm.entity.SdtmEntity;
import com.acuity.visualisations.sdtm.entity.SdtmEntityAE;
import com.acuity.visualisations.sdtm.entity.SdtmEntityCE;
import com.acuity.visualisations.sdtm.entity.SdtmEntityCM;
import com.acuity.visualisations.sdtm.entity.SdtmEntityDM;
import com.acuity.visualisations.sdtm.entity.SdtmEntityDS;
import com.acuity.visualisations.sdtm.entity.SdtmEntityEG;
import com.acuity.visualisations.sdtm.entity.SdtmEntityEX;
import com.acuity.visualisations.sdtm.entity.SdtmEntityFA;
import com.acuity.visualisations.sdtm.entity.SdtmEntityLB;
import com.acuity.visualisations.sdtm.entity.SdtmEntityMH;
import com.acuity.visualisations.sdtm.entity.SdtmEntityRS;
import com.acuity.visualisations.sdtm.entity.SdtmEntitySV;
import com.acuity.visualisations.sdtm.entity.SdtmEntityTR;
import com.acuity.visualisations.sdtm.entity.SdtmEntityTU;
import com.acuity.visualisations.sdtm.entity.SdtmEntityVS;
import com.acuity.visualisations.sdtm.entity.SdtmEntityZE;
import com.acuity.visualisations.sdtm.entity.SdtmKey;
import com.acuity.visualisations.transform.parser.BigDecimalParser;
import com.acuity.visualisations.transform.parser.DateParser;
import com.acuity.visualisations.transform.rule.ParserRule;
import com.acuity.visualisations.transform.standard.CDASHDoseFrequency;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.springframework.util.StringUtils.isEmpty;

public class SdtmEntityMapper {
    private static final String VISITDTC = "VISITDTC";

    private final DateParser sdtmDateParser = new DateParser(ParserRule.MONTH_FIRST);
    private final BigDecimalParser sdtmBigDecimalParser = new BigDecimalParser();

    /**
     * Get QVAL value from supplemental data by sdtmKey (usubjid) for specified sdtmEntity
     *
     * @param sdtmSuppData
     * @param sdtmKey
     * @param sdtmEntity
     * @param qnam
     * @return
     */
    private static String supplement(SdtmSuppData sdtmSuppData, SdtmKey sdtmKey, SdtmEntity sdtmEntity, String qnam) {
        if (sdtmSuppData != null) {
            List<String> supps = sdtmSuppData.get(sdtmKey, sdtmEntity.getSeq(), qnam);
            if (supps != null) {
                return supps.get(0);
            }
        }
        return null;
    }

    private Integer parseInt(String val) {
        try {
            return val == null ? null : Integer.parseInt(val);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseLocalDateTime(String val) {
        try {
            if (!isEmpty(val)) {
                Temporal value = sdtmDateParser.parse(val);
                if (value instanceof LocalDate) {
                    LocalDateTime.from(value);
                } else if (value instanceof LocalDateTime) {
                    return (LocalDateTime) value;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(Double val) {
        try {
            return val == null ? null : BigDecimal.valueOf(val);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String val) {
        try {
            return val == null ? null : sdtmBigDecimalParser.parse(val);
        } catch (InvalidDataFormatException e) {
            return null;
        }
    }

    public Patient mapPatient(SdtmEntityDM sdtmEntity, SdtmKey sdtmKey) throws InvalidDataFormatException {
        Patient entity = new Patient();

        entity.setStudyName(sdtmKey.getStudyId());
        entity.setCentre(SdtmParsers.parseCentre(sdtmKey.getSubjectId()));
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setSex(sdtmEntity.getSex());
        entity.setRace(sdtmEntity.getRace());
        entity.setVisitDate(parseLocalDateTime(sdtmEntity.getDmdtc()));
        entity.setBirthDate(parseLocalDateTime(sdtmEntity.getBrthdtc()));

        return entity;
    }

    public Visit mapVisit(SdtmEntitySV sdtmEntity, SdtmKey sdtmKey) throws InvalidDataFormatException {
        Visit entity = new Visit();

        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setVisitDate(parseLocalDateTime(sdtmEntity.getSvstdtc()));
        entity.setVisitNumber(parseBigDecimal(sdtmEntity.getVisitnum()));

        return entity;
    }

    public Death mapDeath(SdtmEntityCE sdtmEntityCE, SdtmKey sdtmKey) throws InvalidDataFormatException {
        Death entity = new Death();

        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));
        entity.setDate(parseLocalDateTime(sdtmEntityCE.getCestdtc()));

        return entity;
    }

    public MedDosingSchedule mapDoseSchedule(SdtmEntityEX sdtmEntity, SdtmKey sdtmKey, SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        MedDosingSchedule entity = new MedDosingSchedule();

        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setDrug(sdtmEntity.getExtrt());
        entity.setStartDate(parseLocalDateTime(sdtmEntity.getExstdtc()));
        entity.setEndDate(parseLocalDateTime(sdtmEntity.getExendtc()));

        entity.setReasonForActionTaken(sdtmEntity.getExadj());

        String frequencyName = CDASHDoseFrequency.getCdashNameBySdtmName(sdtmEntity.getExdosfrq());
        entity.setFreqName(frequencyName);

        entity.setDose(parseBigDecimal(sdtmEntity.getExdose()));
        entity.setDoseUnit(sdtmEntity.getExdosu());

        entity.setActionTaken(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "EXACN"));

        entity.complete();

        return entity;
    }

    public MedDosDisc mapDoseDiscontinuation(SdtmEntityDS sdtmEntity, SdtmKey sdtmKey, SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        MedDosDisc entity = new MedDosDisc();

        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));
        entity.setIpdcDate(parseLocalDateTime(sdtmEntity.getDsstdtc()));
        entity.setIpdcReas(sdtmEntity.getDsdecod());

        entity.setDrugName(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "SD"));
        entity.setIpdcSpec(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "DSSPFY"));

        return entity;
    }

    public Chemotherapy mapChemotherapy(SdtmEntityCM sdtmEntity, SdtmKey sdtmKey, SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        Chemotherapy entity = new Chemotherapy();
        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setVisit(parseBigDecimal(sdtmEntity.getVisitnum()));
        entity.setVisitDate(parseLocalDateTime(supplement(sdtmSuppData, sdtmKey, sdtmEntity, VISITDTC)));

        entity.setChemoStartDate(parseLocalDateTime(sdtmEntity.getCmstdtc()));
        entity.setChemoEndDate(parseLocalDateTime(sdtmEntity.getCmendtc()));
        if (sdtmEntity.getCmdose() != null) {
            entity.setNumberOfCycles(sdtmEntity.getCmdose().intValue());
        }

        entity.setPreferredNameOfMed(sdtmEntity.getCmdecod());
        entity.setChemoClass(sdtmEntity.getCmscat());
        entity.setTreatmentStatus(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "CXTRTST"));
        entity.setBestResponse(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "CXBRESP"));

        return entity;
    }

    public Radiotherapy mapRadioherapy(SdtmEntityCM sdtmEntity, SdtmKey sdtmKey, SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        Radiotherapy entity = new Radiotherapy();
        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setVisit(parseBigDecimal(sdtmEntity.getVisitnum()));

        entity.setVisitDate(parseLocalDateTime(supplement(sdtmSuppData, sdtmKey, sdtmEntity, VISITDTC)));

        entity.setRadioStartDate(parseLocalDateTime(sdtmEntity.getCmstdtc()));
        entity.setRadioEndDate(parseLocalDateTime(sdtmEntity.getCmendtc()));
        entity.setRadioSiteOrRegion(sdtmEntity.getCmloc());

        entity.setRadiationDose(parseBigDecimal(sdtmEntity.getCmdose()));

        entity.setTreatmentStatus(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "CXTRTST"));
        String supp = supplement(sdtmSuppData, sdtmKey, sdtmEntity, "NOFRACRY");

        entity.setNumberOfDoses(parseInt(supp));
        return entity;
    }

    public Medicine mapMedicine(SdtmEntityCM sdtmEntity, SdtmKey sdtmKey, SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        Medicine entity = new Medicine();
        entity.setDrugName(sdtmEntity.getCmtrt());
        entity.setDrugParent(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "CMGROUP"));
        return entity;
    }

    public ConcomitantMedSchedule mapConcomitantMedSchedule(SdtmEntityCM sdtmEntity, SdtmKey sdtmKey, SdtmSuppData sdtmSuppData)
            throws InvalidDataFormatException {
        ConcomitantMedSchedule entity = new ConcomitantMedSchedule();
        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setDrugName(sdtmEntity.getCmtrt());
        entity.setDrugParent(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "CMGROUP"));

        entity.setDose(parseBigDecimal(sdtmEntity.getCmdose()));
        entity.setDoseUnit(sdtmEntity.getCmdosu());

        Integer frequency = SdtmParsers.parseDoseFrequency(sdtmEntity.getCmdosfrq());
        entity.setFrequency(frequency == null ? sdtmEntity.getCmdosfrq() : frequency.toString());

        entity.setAtcCode(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "ATCCD"));
        entity.setStartDate(parseLocalDateTime(sdtmEntity.getCmstdtc()));
        entity.setEndDate(parseLocalDateTime(sdtmEntity.getCmendtc()));
        entity.setTreatmentReason(sdtmEntity.getCmindc());
        return entity;
    }

    public AdverseEvent mapAdverseEvent(SdtmEntityAE sdtmEntity, SdtmKey sdtmKey, Map<SdtmKey, List<SdtmEntityFA>> sdtmFaData, SdtmSuppData sdtmSuppData) {
        AdverseEvent entity = new AdverseEvent();

        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setStartDate(parseLocalDateTime(sdtmEntity.getAestdtc()));
        entity.setEndDate(parseLocalDateTime(sdtmEntity.getAeendtc()));

        entity.setPT(sdtmEntity.getAedecod());
        entity.setHLT(sdtmEntity.getAehlt());
        entity.setSOC(sdtmEntity.getAesoc());
        entity.setLLT(sdtmEntity.getAellt());
        entity.setAeText(sdtmEntity.getAeterm());
        entity.setMaxSeverity(sdtmEntity.getAetoxgr());

        entity.setSerious(SdtmParsers.parseYesNo(sdtmEntity.getAeser()));

        entity.setActionTaken(sdtmEntity.getAeacn());
        entity.setCausality(sdtmEntity.getAerel());

        if (entity.getActionTaken() != null) {
            entity.setInitialActionTakenForIpDrugs(new String[]{entity.getActionTaken()});
        }

        if (entity.getCausality() != null) {
            entity.setCausalityForIpDrugs(new String[]{entity.getCausality()});
        }

        String ipDrug = supplement(sdtmSuppData, sdtmKey, sdtmEntity, "IP");
        String adDrug = supplement(sdtmSuppData, sdtmKey, sdtmEntity, "AD");

        entity.setIpDrugs(new String[]{ipDrug});
        entity.setAdDrugs(new String[]{adDrug});

        //RCT-2707 logic + Natalie's logic
        if (sdtmFaData != null) {
            List<SdtmEntityFA> faList = sdtmFaData.get(sdtmKey);
            if (faList != null) {
                Map<LocalDateTime, AeTriple> aeGradesInfo = new TreeMap<>();

                for (SdtmEntityFA sdtmEntityFA : faList) {
                    if (sdtmEntity.getAespid().equals(sdtmEntityFA.getFaspid())) {

                        if ("AETOXGR".equals(sdtmEntityFA.getFatestcd())
                                || "CTCGRADE".equals(sdtmEntityFA.getFatestcd())) {

                            if (sdtmEntity.getAestdtc().equals(sdtmEntityFA.getFadtc())) {
                                // Mapping to 'Starting CTC grade for AE'
                                entity.setStartingCtcGrade(sdtmEntityFA.getFastresc());
                            } else {
                                // Mapping to 'AE CTC grade changes' and 'AE CTC grade change dates'
                                LocalDateTime changeDate = parseLocalDateTime(sdtmEntityFA.getFadtc());
                                aeGradesInfo.computeIfAbsent(changeDate, k -> new AeTriple()).ctcGrade = sdtmEntityFA.getFastresc();
                            }
                        } else if ("IPACTION".equals(sdtmEntityFA.getFatestcd())
                                || "AEACN".equals(sdtmEntityFA.getFatestcd())) {
                            if (sdtmEntity.getAestdtc().equals(sdtmEntityFA.getFadtc())) {
                                entity.setInitialActionTakenForIpDrugs(new String[]{sdtmEntityFA.getFaorres()});
                            } else {
                                LocalDateTime changeDate = parseLocalDateTime(sdtmEntityFA.getFadtc());
                                aeGradesInfo.computeIfAbsent(changeDate, k -> new AeTriple()).ipActionTaken = sdtmEntityFA.getFaorres();
                            }
                        } else if ("ADACTION".equals(sdtmEntityFA.getFatestcd())
                                || "AEACNAD".equals(sdtmEntityFA.getFatestcd())) {
                            if (sdtmEntity.getAestdtc().equals(sdtmEntityFA.getFadtc())) {
                                entity.setInitialActionTakenForAdDrugs(new String[]{sdtmEntityFA.getFaorres()});
                            } else {
                                LocalDateTime changeDate = parseLocalDateTime(sdtmEntityFA.getFadtc());
                                aeGradesInfo.computeIfAbsent(changeDate, k -> new AeTriple()).adActionTaken = sdtmEntityFA.getFaorres();
                            }
                        }
                    }
                }
                entity.setCtcGradeChangeDates(aeGradesInfo.keySet().toArray());
                String[] ctcGrades = new String[aeGradesInfo.size()];
                String[] ipActionsTakens = new String[aeGradesInfo.size()];
                String[] adActionsTakens = new String[aeGradesInfo.size()];

                int i = 0;

                for (AeTriple aeTriple : aeGradesInfo.values()) {
                    ctcGrades[i] = aeTriple.ctcGrade;
                    ipActionsTakens[i] = aeTriple.ipActionTaken;
                    adActionsTakens[i] = aeTriple.adActionTaken;
                    i++;
                }
                entity.setCtcGradeChanges(ctcGrades);
                entity.setChangedActionTakenForIpDrugs(ipActionsTakens);
                entity.setChangedActionTakenForAdDrugs(adActionsTakens);
            }
        }

        return entity;
    }

    private static class AeTriple {
        private String ctcGrade;
        private String ipActionTaken;
        private String adActionTaken;

    }

    public EventType mapEventType(SdtmEntityAE sdtmEntity, SdtmKey sdtmKey, SdtmSuppData sdtmSuppData)
            throws InvalidDataFormatException {
        EventType entity = new EventType();
        entity.setPT(sdtmEntity.getAedecod());
        entity.setHLT(sdtmEntity.getAehlt());
        entity.setLLT(sdtmEntity.getAellt());

        entity.setSOC(sdtmEntity.getAesoc());
        entity.setMedDRAVersion(parseBigDecimal(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "MEDDRAV")));
        return entity;
    }

    public SeriousAdverseEvent mapSeriousAdverseEvent(SdtmEntityAE sdtmEntity, SdtmKey sdtmKey, SdtmSuppData sdtmSuppData)
            throws InvalidDataFormatException {
        SeriousAdverseEvent entity = new SeriousAdverseEvent();
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setAeText(sdtmEntity.getAeterm());
        entity.setIsResultInDeath(SdtmParsers.parseYesNo(sdtmEntity.getAesdth()));
        entity.setIsLifeThreatening(SdtmParsers.parseYesNo(sdtmEntity.getAeslife()));
        entity.setIsHospitalizationRequired(SdtmParsers.parseYesNo(sdtmEntity.getAeshosp()));
        entity.setIsDisability(SdtmParsers.parseYesNo(sdtmEntity.getAesdisab()));
        entity.setIsCongenitalAnomaly(SdtmParsers.parseYesNo(sdtmEntity.getAescong()));
        entity.setIsOtherSeriousEvent(SdtmParsers.parseYesNo(sdtmEntity.getAesmie()));

        entity.setAeDescription(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "AEDESC01"));

        entity.setAeFindOutDate(parseLocalDateTime(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "SAEIADTC")));
        entity.setAeBecomeSeriousDate(parseLocalDateTime(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "SAEDTC")));

        entity.setHospitalizationDate(parseLocalDateTime(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "SAEHODTC")));
        entity.setDischargeDate(parseLocalDateTime(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "SAEDIDTC")));
        entity.setIsCausedByStudy(SdtmParsers.parseYesNo(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "SAECAUSP")));
        entity.setStudyProcedure(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "SAESP"));
        return entity;
    }

    public RecistTargetLesion mapRecistTargetLesion(SdtmEntityTR sdtmEntity, SdtmKey sdtmKey,
                                                    SdtmSuppData sdtmSuppData,
                                                    Map<SdtmKey, List<SdtmEntityTU>> sdtmTuData,
                                                    Map<SdtmKey, List<SdtmEntityRS>> sdtmRsData) {

        RecistTargetLesion entity = new RecistTargetLesion();
        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));
        entity.setVisitDate(parseLocalDateTime(supplement(sdtmSuppData, sdtmKey, sdtmEntity, VISITDTC)));

        if ("R1PRES".equals(sdtmEntity.getTrtestcd())) {
            entity.setLesionPresent(SdtmParsers.parseYesNo(sdtmEntity.getTrstresc()));
        } else {
            entity.setLesionPresent(SdtmParsers.NO);
        }

        if ("LDIAM".equals(sdtmEntity.getTrtestcd()) || "SAXIS".equals(sdtmEntity.getTrtestcd())) {
            entity.setLesionDiameter(parseBigDecimal(sdtmEntity.getTrstresc()));
        }

        entity.setVisitNumber(parseBigDecimal(sdtmEntity.getVisitnum()));
        entity.setLesionDate(parseLocalDateTime(sdtmEntity.getTrdtc()));
        entity.setLesionNumber(sdtmEntity.getTrlinkid()); //Remove the 'T' from TULINKID

        if (sdtmEntity.getTrlinkid() != null) {
            List<SdtmEntityTU> tuList = sdtmTuData.get(sdtmKey);
            if (tuList != null) {
                Optional<SdtmEntityTU> tu = tuList.stream().filter(r -> sdtmEntity.getTrlinkid().equals(r.getTulinkid())
                        && "TUMIDENT".equals(r.getTutestcd())).findFirst();
                if (tu.isPresent()) {
                    SdtmEntityTU sdtmEntityTU = tu.get();
                    entity.setLesionSite(sdtmEntityTU.getTuloc());
                }
            }

            List<SdtmEntityRS> rsList = sdtmRsData.get(sdtmKey);
            if (rsList != null) {
                Optional<SdtmEntityRS> rs = rsList.stream().filter(r -> sdtmEntity.getTrlinkid().equals(r.getRslinkid())
                        && "TRGRESP".equals(r.getRstestcd())).findFirst();
                if (rs.isPresent()) {
                    entity.setInvestigatorsResponse(rs.get().getRsstresc());
                }
            }
        }
        return entity;
    }

    public void merge(RecistTargetLesion source, RecistTargetLesion target) {
        if (SdtmParsers.NO.equals(target.getLesionPresent()) && source.getLesionPresent() != null) {
            target.setLesionPresent(source.getLesionPresent());
        }

        if (source.getLesionDiameter() != null) {
            target.setLesionDiameter(source.getLesionDiameter());
        }
    }

    public void merge(RecistNonTargetLesion source, RecistNonTargetLesion target) {
        if (SdtmParsers.NO.equals(target.getLesionPresent()) && source.getLesionPresent() != null) {
            target.setLesionPresent(source.getLesionPresent());
        }
    }

    public RecistNonTargetLesion mapRecistNonTargetLesion(SdtmEntityTR sdtmEntity, SdtmKey sdtmKey,
                                                          SdtmSuppData sdtmSuppData,
                                                          Map<SdtmKey, List<SdtmEntityTU>> sdtmTuData) {

        RecistNonTargetLesion entity = new RecistNonTargetLesion();
        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));
        entity.setVisitDate(parseLocalDateTime(supplement(sdtmSuppData, sdtmKey, sdtmEntity, VISITDTC)));

        if ("R2PRES".equals(sdtmEntity.getTrtestcd())) {
            entity.setLesionPresent(SdtmParsers.parseYesNo(sdtmEntity.getTrstresc()));
        } else {
            entity.setLesionPresent(SdtmParsers.NO);
        }

        entity.setVisitNumber(parseBigDecimal(sdtmEntity.getVisitnum()));
        entity.setLesionDate(parseLocalDateTime(sdtmEntity.getTrdtc()));
        if (sdtmEntity.getTrlinkid() != null) {
            List<SdtmEntityTU> tuList = sdtmTuData.get(sdtmKey);
            if (tuList != null) {
                Optional<SdtmEntityTU> tu = tuList.stream().filter(r -> sdtmEntity.getTrlinkid().equals(r.getTulinkid())
                        && "TUMIDENT".equals(r.getTutestcd())).findFirst();
                if (tu.isPresent()) {
                    SdtmEntityTU sdtmEntityTU = tu.get();
                    entity.setLesionSite(sdtmEntityTU.getTuloc());
                }
            }
        }
        return entity;
    }

    public RecistAssessment mapRecistAssessment(SdtmEntityRS sdtmEntity, SdtmKey sdtmKey, SdtmSuppData sdtmSuppData,
                                                Map<SdtmKey, List<SdtmEntityTU>> sdtmTuData, SdtmSuppData sdtmRsSuppData) {

        RecistAssessment entity = new RecistAssessment();
        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));
        entity.setVisitDate(parseLocalDateTime(supplement(sdtmSuppData, sdtmKey, sdtmEntity, VISITDTC)));
        entity.setVisit(parseBigDecimal(sdtmEntity.getVisitnum()));

        entity.setNewLesionsSinceBaseline(SdtmParsers.NO);

        List<SdtmEntityTU> tuList = sdtmTuData.get(sdtmKey);
        if (tuList != null && sdtmEntity.getRslinkid() != null) {
            Optional<SdtmEntityTU> tu = tuList.stream().filter(r -> sdtmEntity.getRslinkid().equals(r.getTulinkid())
                    && "TUMIDENT".equals(r.getTutestcd()) && "NEW".equals(r.getTustresc())).findFirst();
            if (tu.isPresent()) {
                SdtmEntityTU sdtmEntityTU = tu.get();

                entity.setNewLesionSite(sdtmEntityTU.getTuloc());
                entity.setNewLesionDate(parseLocalDateTime(sdtmEntityTU.getTudtc()));

                // If TULOC is not null for the following filters then value is Yes
                entity.setNewLesionsSinceBaseline(sdtmEntityTU.getTuloc() == null ? SdtmParsers.NO : SdtmParsers.YES);
            }
        }

        if ("OVRLRESP".equals(sdtmEntity.getRstestcd())) {
            entity.setOverallRecistResponse(sdtmEntity.getRsstresc());
        } else if ("INVOPRES".equals(sdtmEntity.getRstestcd())) {
            entity.setInvOpinion(sdtmEntity.getRsstresc());
        }

        String diff = supplement(sdtmRsSuppData, sdtmKey, sdtmEntity, "RSSPFY");
        if (diff != null) {
            entity.setReasonAssessmentsDiffer(diff);
        }

        if (entity.getOverallRecistResponse() != null && entity.getInvOpinion() != null) {
            boolean agree = entity.getOverallRecistResponse().equals(entity.getInvOpinion());
            entity.setInvAgreeWithRecistResponse(agree ? SdtmParsers.YES : SdtmParsers.NO);
        }

        return entity;
    }

    public Test mapTest(SdtmEntityLB sdtmEntity, SdtmKey sdtmKey) throws InvalidDataFormatException {
        Test entity = new Test();
        entity.setVisit(parseBigDecimal(sdtmEntity.getVisitnum()));
        entity.setDate(parseLocalDateTime(sdtmEntity.getLbdtc()));
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));
        return entity;
    }

    public Test mapTest(SdtmEntityVS sdtmEntity, SdtmKey sdtmKey) throws InvalidDataFormatException {
        Test entity = new Test();
        entity.setVisit(parseBigDecimal(sdtmEntity.getVisitnum()));
        entity.setDate(parseLocalDateTime(sdtmEntity.getVsdtc()));
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));
        return entity;
    }

    public Test mapTest(SdtmEntityEG sdtmEntity, SdtmKey sdtmKey) throws InvalidDataFormatException {
        Test entity = new Test();
        entity.setVisit(parseBigDecimal(sdtmEntity.getVisitnum()));
        entity.setDate(parseLocalDateTime(sdtmEntity.getEgdtc()));
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));
        return entity;
    }

    public Test mapTest(SdtmEntityZE sdtmEntity, SdtmKey sdtmKey) throws InvalidDataFormatException {
        Test entity = new Test();
        entity.setVisit(parseBigDecimal(sdtmEntity.getVisitnum()));
        entity.setDate(parseLocalDateTime(sdtmEntity.getZedtc()));
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));
        return entity;
    }

    public Laboratory mapLaboratory(SdtmEntityLB sdtmEntity, SdtmKey sdtmKey) throws InvalidDataFormatException {
        Laboratory entity = new Laboratory();

        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setLabCode(sdtmEntity.getLbtest());
        entity.setDate(parseLocalDateTime(sdtmEntity.getLbdtc()));

        entity.setLaboratoryValue(parseBigDecimal(sdtmEntity.getLbstresn()));
        entity.setLaboratoryUnit(sdtmEntity.getLbstresu());
        entity.setRefLow(parseBigDecimal(sdtmEntity.getLbstnrlo()));
        entity.setRefHigh(parseBigDecimal(sdtmEntity.getLbstnrhi()));

        return entity;
    }

    public Vital mapVital(SdtmEntityVS sdtmEntity, SdtmKey sdtmKey) {
        Vital entity = new Vital();

        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setDate(parseLocalDateTime(sdtmEntity.getVsdtc()));

        entity.setTestName(sdtmEntity.getVstest());
        entity.setTestResult(parseBigDecimal(sdtmEntity.getVsstresn()));
        entity.setResultUnit(sdtmEntity.getVsstresu());
        return entity;
    }

    public ECG mapEcg(SdtmEntityEG sdtmEntity, SdtmKey sdtmKey, SdtmSuppData sdtmSuppData) {
        ECG entity = new ECG();

        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setDate(parseLocalDateTime(sdtmEntity.getEgdtc()));

        if ("QRSDUR".equals(sdtmEntity.getEgtestcd())) {
            entity.setQrs(parseInt(sdtmEntity.getEgstresc()));
        } else if ("RRMEAN".equals(sdtmEntity.getEgtestcd())) {
            entity.setRr(parseInt(sdtmEntity.getEgstresc()));
        } else if ("PRMEAN".equals(sdtmEntity.getEgtestcd())) {
            entity.setPr(parseInt(sdtmEntity.getEgstresc()));
        } else if ("QTMEAN".equals(sdtmEntity.getEgtestcd())) {
            entity.setQt(parseInt(sdtmEntity.getEgstresc()));
        } else if ("INTP".equals(sdtmEntity.getEgtestcd())) {
            entity.setEvaluation(sdtmEntity.getEgstresc());
        }

        entity.setAbnormality(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "EGSPFY"));

        return entity;
    }

    private static List<String> lvefMethods = Arrays.asList(
            "ECHOCARDIOGRAPHY",
            "RADIONUCLIDE VENTRICULOGRAPHY",
            "MYOCARDIAL PERFUSION IMAGING",
            "MAGNETIC RESONANCE IMAGING",
            "MULTI-DETECTOR COMPUTED TOMOGRAPHY"
    );

    private boolean isLvefMethodValid(String method) {
        return !StringUtils.isEmpty(method) && lvefMethods.contains(method.toUpperCase());
    }

    public LVEF mapLvef(SdtmEntityEG sdtmEntity, SdtmKey sdtmKey) {
        LVEF entity = new LVEF();

        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setDate(parseLocalDateTime(sdtmEntity.getEgdtc()));

        entity.setLvef(parseInt(sdtmEntity.getEgstresn()));
        entity.setMethod(StringUtils.isEmpty(sdtmEntity.getEgmethod()) ? null
                : (isLvefMethodValid(sdtmEntity.getEgmethod()) ? sdtmEntity.getEgmethod() : "Other"));
        entity.setMethodOther(StringUtils.isEmpty(sdtmEntity.getEgmethod()) ? null
                : (isLvefMethodValid(sdtmEntity.getEgmethod()) ? null : sdtmEntity.getEgmethod()));

        return entity;
    }

    public LVEF mapLvef(SdtmEntityZE sdtmEntity, SdtmKey sdtmKey) {
        LVEF entity = new LVEF();

        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        entity.setDate(parseLocalDateTime(sdtmEntity.getZedtc()));

        entity.setLvef(parseInt(sdtmEntity.getZestresc()));
        entity.setMethod(StringUtils.isEmpty(sdtmEntity.getZemethod()) ? null
                : (isLvefMethodValid(sdtmEntity.getZemethod()) ? sdtmEntity.getZemethod() : "Other"));
        entity.setMethodOther(StringUtils.isEmpty(sdtmEntity.getZemethod()) ? null
                : (isLvefMethodValid(sdtmEntity.getZemethod()) ? null : sdtmEntity.getZemethod()));

        return entity;
    }

    public PrimaryTumourLocation mapPrimaryTumourLocation(SdtmEntityFA sdtmEntity, SdtmKey sdtmKey, Map<SdtmKey, List<SdtmEntityMH>> sdtmMhData) {
        PrimaryTumourLocation entity = new PrimaryTumourLocation();
        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));
        entity.setPrimaryTumLocation(sdtmEntity.getFastresc());
        entity.setPrimaryTumComment(sdtmEntity.getFaorres());
        if (sdtmMhData != null) {
            List<SdtmEntityMH> mhList = sdtmMhData.get(sdtmKey);
            SdtmEntityMH mhItem = mhList.stream().filter(mh -> mh.getMhterm() != null
                    && mh.getMhterm().equalsIgnoreCase(sdtmEntity.getFastresc() + " CANCER")
                    && "FIRST DIAGNOSIS".equalsIgnoreCase(mh.getMhscat())).findFirst().orElse(null);
            if (mhItem != null) {
                entity.setOriginalDiagnosisDate(parseLocalDateTime(mhItem.getMhstdtc()));
            }
        }
        return entity;
    }

    public WithdrawalCompletion mapWithdrawalCompletion(SdtmEntityDS sdtmEntity, SdtmKey sdtmKey, SdtmSuppData sdtmSuppData) {
        WithdrawalCompletion entity = new WithdrawalCompletion();

        entity.setStudyName(sdtmKey.getStudyId());
        entity.setSubject(SdtmParsers.parseSubject(sdtmKey.getSubjectId()));

        if ("STUDY COMPLETED".equals(sdtmEntity.getDsdecod())) {
            entity.setPrematurelyWithdrawn("Yes");
        } else {
            entity.setPrematurelyWithdrawn("No");
        }

        entity.setWithdrawalCompletionDate(parseLocalDateTime(sdtmEntity.getDsstdtc()));
        entity.setMainReason(sdtmEntity.getDsterm());
        entity.setSpecification(supplement(sdtmSuppData, sdtmKey, sdtmEntity, "DSSPFY"));

        return entity;
    }

    public void merge(ECG source, ECG target) {
        if (source.getQrs() != null) {
            target.setQrs(source.getQrs());
        }
        if (source.getRr() != null) {
            target.setRr(source.getRr());
        }
        if (source.getPr() != null) {
            target.setPr(source.getPr());
        }
        if (source.getQt() != null) {
            target.setQt(source.getQt());
        }
        if (source.getEvaluation() != null) {
            target.setEvaluation(source.getEvaluation());
        }
        if (source.getAbnormality() != null) {
            target.setAbnormality(source.getAbnormality());
        }
    }
}
