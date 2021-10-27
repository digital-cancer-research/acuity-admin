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

import com.acuity.visualisations.batch.holders.configuration.SdtmDataHolder;
import com.acuity.visualisations.batch.processor.AeSplitter;
import com.acuity.visualisations.exception.InvalidDataFormatException;
import com.acuity.visualisations.model.output.OutputModelChunk;
import com.acuity.visualisations.model.output.OutputModelChunkImpl;
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
import com.acuity.visualisations.model.output.entities.TimestampedEntity;
import com.acuity.visualisations.model.output.entities.Visit;
import com.acuity.visualisations.model.output.entities.Vital;
import com.acuity.visualisations.model.output.entities.WithdrawalCompletion;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SdtmEntityProcessor {

    private SdtmEntityMapper sdtmEntityMapper = new SdtmEntityMapper();

    private SdtmDataHolder sdtmDataHolder;

    public SdtmEntityProcessor(SdtmDataHolder sdtmDataHolder) {
        this.sdtmDataHolder = sdtmDataHolder;
    }

    @SuppressWarnings("unchecked")
    public OutputModelChunk processSdtm(String file, List<String> entityNames) throws Exception {
        OutputModelChunk output = new OutputModelChunkImpl();

        SdtmData sdtmMainData = sdtmDataHolder.getMainData(file);
        SdtmSuppData sdtmSuppData = sdtmDataHolder.getSuppData(file);

        switch (sdtmMainData.getDomain()) {
            case AE:
                processAE(output, entityNames, file, sdtmMainData, sdtmSuppData);
                break;
            case CE:
                processCE(output, entityNames, sdtmMainData, sdtmSuppData);
                break;
            case CM:
                processCM(output, entityNames, sdtmMainData, sdtmSuppData);
                break;
            case DM:
                processDM(output, entityNames, sdtmMainData, sdtmSuppData);
                break;
            case DS:
                processDS(output, entityNames, sdtmMainData, sdtmSuppData);
                break;
            case EX:
                processEX(output, entityNames, sdtmMainData, sdtmSuppData);
                break;
            case EG:
                processEG(output, entityNames, sdtmMainData, sdtmSuppData);
                break;
            case LB:
                processLB(output, sdtmMainData);
                break;
            case SV:
                processSV(output, entityNames, sdtmMainData, sdtmSuppData);
                break;
            case TR:
                processTR(output, entityNames, file, sdtmMainData, sdtmSuppData);
                break;
            case RS:
                processRS(output, entityNames, file, sdtmMainData, sdtmSuppData);
                break;
            case VS:
                processVS(output, entityNames, sdtmMainData, sdtmSuppData);
                break;
            case ZE:
                processZE(output, entityNames, sdtmMainData, sdtmSuppData);
                break;
            case FA:
                processFA(output, entityNames, file, sdtmMainData, sdtmSuppData);
                break;
            default:
                return output;
        }
        return output;
    }

    private void processFA(OutputModelChunk output, List<String> entityNames, String file,
                           SdtmData<SdtmEntityFA> sdtmMainData, SdtmSuppData sdtmSuppData) {
        String mhFile = SdtmDomain.getSiblingFile(sdtmMainData.getDomain(), file, SdtmDomain.MH);
        Map<SdtmKey, List<SdtmEntityMH>> sdtmRsData = null;
        if (mhFile != null) {
            sdtmRsData = sdtmDataHolder.getMainData(mhFile).getData();
        }
        for (Map.Entry<SdtmKey, List<SdtmEntityFA>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();
            for (SdtmEntityFA sdtmEntity : sdtmEntry.getValue()) {
                if ("PTUMLOC".equalsIgnoreCase(sdtmEntity.getFatestcd())) {
                    PrimaryTumourLocation ptlEntity = sdtmEntityMapper.mapPrimaryTumourLocation(sdtmEntity, sdtmKey, sdtmRsData);
                    output.addEntity(ptlEntity);
                }
            }
        }
    }

    private void processZE(OutputModelChunk output, List<String> entityNames, SdtmData<SdtmEntityZE> sdtmMainData,
                           SdtmSuppData sdtmSuppData) throws Exception {
        for (Map.Entry<SdtmKey, List<SdtmEntityZE>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();

            for (SdtmEntityZE sdtmEntity : sdtmEntry.getValue()) {
                if ("LVEF".equalsIgnoreCase(sdtmEntity.getZetestcd())) {
                    LVEF lvefEntity = sdtmEntityMapper.mapLvef(sdtmEntity, sdtmKey);
                    Test testEntity = sdtmEntityMapper.mapTest(sdtmEntity, sdtmKey);
                    output.addEntity(testEntity);
                    output.addEntity(lvefEntity);
                }
            }
        }

    }

    private void processAE(OutputModelChunk output, List<String> entityNames, String file, SdtmData<SdtmEntityAE> sdtmMainData,
                           SdtmSuppData sdtmSuppData) throws Exception {
        String faFile = SdtmDomain.getSiblingFile(sdtmMainData.getDomain(), file, SdtmDomain.FA);
        Map<SdtmKey, List<SdtmEntityFA>> sdtmFaData = null;
        if (sdtmDataHolder.getMainData(faFile) != null) {
            sdtmFaData = sdtmDataHolder.getMainData(faFile).getData();
        }
        boolean processSeriousAdverseEvent = entityNames.contains("SeriousAdverseEvent");

        for (Map.Entry<SdtmKey, List<SdtmEntityAE>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();
            for (SdtmEntityAE sdtmEntity : sdtmEntry.getValue()) {


                AdverseEvent aev = sdtmEntityMapper.mapAdverseEvent(sdtmEntity, sdtmKey, sdtmFaData, sdtmSuppData);


                List<AdverseEvent> aes = aeChanges(aev);
                output.addEntities(aes);

                List<TimestampedEntity> newAeStuff = AeSplitter.splitAdverseEventToEntities(aev);
                output.addEntities(newAeStuff);

                EventType evt = sdtmEntityMapper.mapEventType(sdtmEntity, sdtmKey, sdtmSuppData);
                output.addEntity(evt);

                if (processSeriousAdverseEvent && "Y".equalsIgnoreCase(sdtmEntity.getAeser())) {
                    SeriousAdverseEvent sae = sdtmEntityMapper.mapSeriousAdverseEvent(sdtmEntity, sdtmKey, sdtmSuppData);
                    output.addEntity(sae);
                }
            }
        }
    }

    private void processLB(OutputModelChunk output, SdtmData<SdtmEntityLB> sdtmMainData) throws InvalidDataFormatException {
        for (Map.Entry<SdtmKey, List<SdtmEntityLB>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();
            for (SdtmEntityLB sdtmEntity : sdtmEntry.getValue()) {
                Test testEntity = sdtmEntityMapper.mapTest(sdtmEntity, sdtmKey);
                output.addEntity(testEntity);

                Laboratory labEntity = sdtmEntityMapper.mapLaboratory(sdtmEntity, sdtmKey);
                output.addEntity(labEntity);
            }
        }
    }

    private void processCE(OutputModelChunk output, List<String> entityNames, SdtmData<SdtmEntityCE> sdtmMainData,
                           SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        for (Map.Entry<SdtmKey, List<SdtmEntityCE>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();
            for (SdtmEntityCE sdtmEntity : sdtmEntry.getValue()) {
                if ("DEATH".equalsIgnoreCase(sdtmEntity.getCecat())) {
                    Death dth = sdtmEntityMapper.mapDeath(sdtmEntity, sdtmKey);
                    output.addEntity(dth);
                }
            }
        }
    }

    private void processEX(OutputModelChunk output, List<String> entityNames, SdtmData<SdtmEntityEX> sdtmMainData,
                           SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        for (Map.Entry<SdtmKey, List<SdtmEntityEX>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();
            for (SdtmEntityEX sdtmEntity : sdtmEntry.getValue()) {
                MedDosingSchedule dose = sdtmEntityMapper.mapDoseSchedule(sdtmEntity, sdtmKey, sdtmSuppData);
                output.addEntity(dose);
            }
        }
    }

    private void processSV(OutputModelChunk output, List<String> entityNames, SdtmData<SdtmEntitySV> sdtmMainData,
                           SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        for (Map.Entry<SdtmKey, List<SdtmEntitySV>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();
            for (SdtmEntitySV sdtmEntity : sdtmEntry.getValue()) {
                Visit vis = sdtmEntityMapper.mapVisit(sdtmEntity, sdtmKey);
                output.addEntity(vis);
            }
        }
    }

    private void processRS(OutputModelChunk output, List<String> entityNames, String file, SdtmData<SdtmEntityRS> sdtmMainData,
                           SdtmSuppData sdtmSuppData)
            throws InvalidDataFormatException {
        String tuFile = SdtmDomain.getSiblingFile(sdtmMainData.getDomain(), file, SdtmDomain.TU);
        Map<SdtmKey, List<SdtmEntityTU>> sdtmTuData = sdtmDataHolder.getMainData(tuFile).getData();

        SdtmSuppData sdtmRsSuppData = sdtmDataHolder.getSuppData(file);

        for (Map.Entry<SdtmKey, List<SdtmEntityRS>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();
            for (SdtmEntityRS sdtmEntity : sdtmEntry.getValue()) {
                if ("OVRLRESP".equalsIgnoreCase(sdtmEntity.getRstestcd()) || "INVOPRES".equalsIgnoreCase(sdtmEntity.getRstestcd())) {
                    RecistAssessment entity = sdtmEntityMapper.mapRecistAssessment(sdtmEntity, sdtmKey, sdtmSuppData, sdtmTuData, sdtmRsSuppData);
                    output.addEntity(entity);
                }
            }
        }
    }

    private void processTR(OutputModelChunk output, List<String> entityNames, String file, SdtmData<SdtmEntityTR> sdtmMainData,
                           SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        boolean processRecistTargetLesion = entityNames.contains("RecistTargetLesion");
        boolean processRecistNonTargetLesion = entityNames.contains("RecistNonTargetLesion");

        String tuFile = SdtmDomain.getSiblingFile(sdtmMainData.getDomain(), file, SdtmDomain.TU);
        Map<SdtmKey, List<SdtmEntityTU>> sdtmTuData = sdtmDataHolder.getMainData(tuFile).getData();

        String rsFile = SdtmDomain.getSiblingFile(sdtmMainData.getDomain(), file, SdtmDomain.RS);
        Map<SdtmKey, List<SdtmEntityRS>> sdtmRsData = sdtmDataHolder.getMainData(rsFile).getData();

        for (Map.Entry<SdtmKey, List<SdtmEntityTR>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();

            // Need to merge RecistTargetLesion by lesionDate and lesionNumber
            Map<String, RecistTargetLesion> mergeRecistTargetLesion = new HashMap<>();

            // Need to merge RecistNonTargetLesion by lesionDate and lesionSite
            Map<String, RecistNonTargetLesion> mergeRecistNonTargetLesion = new HashMap<>();

            for (SdtmEntityTR sdtmEntity : sdtmEntry.getValue()) {
                if (processRecistTargetLesion && "TARGET".equalsIgnoreCase(sdtmEntity.getTrgrpid())) {
                    RecistTargetLesion entity = sdtmEntityMapper.mapRecistTargetLesion(sdtmEntity, sdtmKey, sdtmSuppData, sdtmTuData, sdtmRsData);
                    String uKey = entity.getLesionDate() + "|" + entity.getLesionNumber();
                    if (mergeRecistTargetLesion.containsKey(uKey)) {
                        sdtmEntityMapper.merge(entity, mergeRecistTargetLesion.get(uKey));
                    } else {
                        mergeRecistTargetLesion.put(uKey, entity);
                    }
                }

                if (processRecistNonTargetLesion && "NON-TARGET".equalsIgnoreCase(sdtmEntity.getTrgrpid())) {
                    RecistNonTargetLesion entity = sdtmEntityMapper.mapRecistNonTargetLesion(sdtmEntity, sdtmKey, sdtmSuppData, sdtmTuData);
                    String uKey = entity.getLesionDate() + "|" + entity.getLesionSite();
                    if (mergeRecistTargetLesion.containsKey(uKey)) {
                        sdtmEntityMapper.merge(entity, mergeRecistNonTargetLesion.get(uKey));
                    } else {
                        mergeRecistNonTargetLesion.put(uKey, entity);
                    }
                }
            }
            mergeRecistTargetLesion.values().forEach(output::addEntity);
            mergeRecistNonTargetLesion.values().forEach(output::addEntity);
        }
    }

    private void processDS(OutputModelChunk output, List<String> entityNames, SdtmData<SdtmEntityDS> sdtmMainData,
                           SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        for (Map.Entry<SdtmKey, List<SdtmEntityDS>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();
            for (SdtmEntityDS sdtmEntity : sdtmEntry.getValue()) {
                if (sdtmEntity.getDsterm() != null && sdtmEntity.getDsterm().startsWith("TREATMENT STOPPED DUE TO ")) {
                    MedDosDisc disc = sdtmEntityMapper.mapDoseDiscontinuation(sdtmEntity, sdtmKey, sdtmSuppData);
                    output.addEntity(disc);
                }

                if ("DISPOSITION EVENT".equalsIgnoreCase(sdtmEntity.getDscat()) && "REASON STUDY DISCONTINUED".equalsIgnoreCase(sdtmEntity.getDsscat())) {
                    WithdrawalCompletion withdrawalCompletion = sdtmEntityMapper.mapWithdrawalCompletion(sdtmEntity, sdtmKey, sdtmSuppData);
                    output.addEntity(withdrawalCompletion);
                }
            }
        }
    }

    private void processDM(OutputModelChunk output, List<String> entityNames, SdtmData<SdtmEntityDM> sdtmMainData,
                           SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        for (Map.Entry<SdtmKey, List<SdtmEntityDM>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();
            for (SdtmEntityDM sdtmEntity : sdtmEntry.getValue()) {
                Patient pat = sdtmEntityMapper.mapPatient(sdtmEntity, sdtmKey);
                output.addEntity(pat);
            }
        }
    }

    private void processCM(OutputModelChunk output, List<String> entityNames, SdtmData<SdtmEntityCM> sdtmMainData,
                           SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        boolean processChemotherapy = entityNames.contains("Chemotherapy");
        boolean processRadiotherapy = entityNames.contains("Radiotherapy");
        boolean processConcomitant = entityNames.contains("ConcomitantMedSchedule");

        for (Map.Entry<SdtmKey, List<SdtmEntityCM>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();
            for (SdtmEntityCM sdtmEntity : sdtmEntry.getValue()) {
                if (processChemotherapy && "CHEMOTHERAPY".equalsIgnoreCase(sdtmEntity.getCmscat())) {
                    Chemotherapy therapy = sdtmEntityMapper.mapChemotherapy(sdtmEntity, sdtmKey, sdtmSuppData);
                    output.addEntity(therapy);

                } else if (processRadiotherapy && "RADIOTHERAPY".equalsIgnoreCase(sdtmEntity.getCmscat())) {
                    Radiotherapy therapy = sdtmEntityMapper.mapRadioherapy(sdtmEntity, sdtmKey, sdtmSuppData);
                    output.addEntity(therapy);
                } else if (processConcomitant && "GENERAL CONCOMITANT MEDICATION".equalsIgnoreCase(sdtmEntity.getCmcat())) {
                    Medicine medicine = sdtmEntityMapper.mapMedicine(sdtmEntity, sdtmKey, sdtmSuppData);
                    output.addEntity(medicine);

                    ConcomitantMedSchedule concom = sdtmEntityMapper.mapConcomitantMedSchedule(sdtmEntity, sdtmKey, sdtmSuppData);
                    output.addEntity(concom);
                }
            }
        }
    }

    private void processVS(OutputModelChunk output, List<String> entityNames, SdtmData<SdtmEntityVS> sdtmMainData,
                           SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        // final List<String> filter = Arrays.asList("PULSE", "SYSBP", "DIABP", "WEIGHT", "HEIGHT", "SPO2");

        for (Map.Entry<SdtmKey, List<SdtmEntityVS>> sdtmEntry : sdtmMainData.getData().entrySet()) {
            SdtmKey sdtmKey = sdtmEntry.getKey();

            // Need to merge Vital entities, one Vital for one day
            for (SdtmEntityVS sdtmEntity : sdtmEntry.getValue()) {
                // if (filter.contains(sdtmEntity.getVstestcd())) {
                Vital vitalEntity = sdtmEntityMapper.mapVital(sdtmEntity, sdtmKey);
                Test testEntity = sdtmEntityMapper.mapTest(sdtmEntity, sdtmKey);
                output.addEntity(testEntity);
                output.addEntity(vitalEntity);
                // }
            }
        }
    }

    private void processEG(OutputModelChunk output, List<String> entityNames, SdtmData<SdtmEntityEG> sdtmMainData,
                           SdtmSuppData sdtmSuppData) throws InvalidDataFormatException {
        final List<String> filter = Arrays.asList("QRSDUR", "RRMEAN", "PRMEAN", "QTMEAN", "INTP");

        boolean processEcg = entityNames.contains("ECG");
        boolean processLvef = entityNames.contains("LVEF");

        if (processEcg) {
            for (Map.Entry<SdtmKey, List<SdtmEntityEG>> sdtmEntry : sdtmMainData.getData().entrySet()) {
                SdtmKey sdtmKey = sdtmEntry.getKey();

                // Need to merge per day
                Map<LocalDateTime, ECG> testEcg = new HashMap<>();

                for (SdtmEntityEG sdtmEntity : sdtmEntry.getValue()) {
                    if (filter.contains(sdtmEntity.getEgtestcd())) {
                        ECG ecgEntity = sdtmEntityMapper.mapEcg(sdtmEntity, sdtmKey, sdtmSuppData);

                        if (testEcg.containsKey(ecgEntity.getDate())) {
                            sdtmEntityMapper.merge(ecgEntity, testEcg.get(ecgEntity.getDate()));
                        } else {
                            Test testEntity = sdtmEntityMapper.mapTest(sdtmEntity, sdtmKey);
                            output.addEntity(testEntity);
                            testEcg.put(ecgEntity.getDate(), ecgEntity);
                        }
                    }
                }

                testEcg.values().forEach(output::addEntity);
            }
        }

        if (processLvef) {
            for (Map.Entry<SdtmKey, List<SdtmEntityEG>> sdtmEntry : sdtmMainData.getData().entrySet()) {
                SdtmKey sdtmKey = sdtmEntry.getKey();

                for (SdtmEntityEG sdtmEntity : sdtmEntry.getValue()) {
                    if ("LVEF".equalsIgnoreCase(sdtmEntity.getEgtestcd())) {
                        LVEF lvefEntity = sdtmEntityMapper.mapLvef(sdtmEntity, sdtmKey);
                        Test testEntity = sdtmEntityMapper.mapTest(sdtmEntity, sdtmKey);
                        output.addEntity(testEntity);
                        output.addEntity(lvefEntity);
                    }
                }
            }
        }
    }

    /**
     * Copied from InputModelChunkProcessorImpl
     */
    private static List<AdverseEvent> aeChanges(AdverseEvent in) throws CloneNotSupportedException {
        //RCT-898
        Object[] changes = in.getCtcGradeChanges();
        Object[] dates = in.getCtcGradeChangeDates();

        if (changes == null || dates == null || dates.length != changes.length) {
            return Collections.singletonList(in);
        }

        //check if we have non-empty data to iterate
        boolean hasNonEmptyPairsToIterate = false;
        for (int i = 0; i < changes.length; i++) {
            if (dates[i] != null || (changes[i] != null && !((String) changes[i]).isEmpty())) {
                hasNonEmptyPairsToIterate = true;
                break;
            }
        }

        if (!hasNonEmptyPairsToIterate) {
            return Collections.singletonList(in);
        }

        List<AdverseEvent> aes = new ArrayList<AdverseEvent>();

        String guid = UUID.randomUUID().toString().replaceAll("-", "");

        AdverseEvent ae = (AdverseEvent) in.clone();
        ae.setGroupGuid(guid);
        ae.setMaxSeverity(in.getStartingCtcGrade());
        aes.add(ae);

        for (int i = 0; i < changes.length; i++) {
            if (dates[i] != null || (changes[i] != null && !((String) changes[i]).isEmpty())) {
                LocalDateTime date = (LocalDateTime) dates[i];
                if (date != null) { // -1 minute to exclude AE periods overlap
                    date = date.minusMinutes(1);
                }
                ae.setEndDate(date);

                AdverseEvent ae1 = (AdverseEvent) in.clone();
                ae1.setGroupGuid(guid);
                ae1.setMaxSeverity((String) changes[i]);
                ae1.setStartDate((LocalDateTime) dates[i]);
                ae1.setEndDate(in.getEndDate());
                aes.add(ae1);
                ae = ae1;
            }
        }
        return aes;
    }
}
