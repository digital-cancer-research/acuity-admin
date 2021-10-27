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

import com.acuity.visualisations.model.output.SmartEntity;
import com.acuity.visualisations.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Biomarker extends TimestampedEntity implements SmartEntity {

    private static final String SHORT_VAR = "short variant";
    private static final String COPY_NUM_ALTER = "copy number alteration";
    private static final String REARRANGEMENT = "rearrangement";
    private static final String SHORT_VAR_REGEXP = "(short)[-\\s](variant)";
    private static final String COPY_NUM_ALTER_REGEXP = "(copy)[-\\s](number)[-\\s](alteration)";
    private static final String REARRANGEMENT_REGEXP = "(rearrangement)";
    private static final String DASH = "-";

    protected String part;
    protected String subject;
    private String patientGuid;

    private String gene;
    private String sampleType;
    private String sampleId;
    private Integer variantCount;
    private String cDNAChange;
    private String somaticStatus;
    private String aminoAcidChange;
    private String chromosomeNumber;
    private Integer chromosomeLocationStart;
    private Integer chromosomeLocationEnd;
    private String genomeLocation;
    private String externalVarianId;
    private Integer totalReads;
    private Integer germilineFreq;
    private String variantType;
    private String mutationType;
    private Integer mutantAlleleFreq;
    private Integer copyNumber;
    private String chromosomeInstabilityNumber;
    private Integer tumourMutationBurden;
    private String copyNumberAlterationType;
    private String rearrGene1;
    private String rearrDesc;

    @Override
    public void complete() {
        normalizeVariantType();
        parseGenomeLocation();
        geneNameToUpperCase();
    }

    /**
     * Normalize variant type as there are three available value for the field only.
     * Variant type defines where value take from for alteration type.
     * An alteration type must exist, that's why variant type is mandatory.
     * If there is no a value in correspondent field, then file is supposed to be considered as incorrect =>
     * => that's why setting null to variant type (it is not nullable actually)
     */
    private void normalizeVariantType() {
        String varTypeLower = variantType.toLowerCase();
        if (varTypeLower.matches(SHORT_VAR_REGEXP)) {
            setVariantType(StringUtil.isEmpty(mutationType) ? null : SHORT_VAR);
        } else if (varTypeLower.matches(COPY_NUM_ALTER_REGEXP)) {
            setVariantType(StringUtil.isEmpty(copyNumberAlterationType) ? null : COPY_NUM_ALTER);
        } else if (varTypeLower.matches(REARRANGEMENT_REGEXP)) {
            setVariantType(StringUtil.isEmpty(rearrDesc) ? null : REARRANGEMENT);
        } else {
            setVariantType(null);
        }
    }

    /**
     * All the chromosome location info comes from the genome location field.
     * When the end is not defined then consider end equals to start.
     */
    private void parseGenomeLocation() {
        if (!(genomeLocation == null || "-".equals(genomeLocation))) {
            String[] genomePosition = genomeLocation.split(":|_");
            if (genomePosition.length >= 2) {
                setChromosomeNumber(genomePosition[0]);
                setChromosomeLocationStart(Integer.valueOf(genomePosition[1]));
            }
            chromosomeLocationEnd = genomePosition.length > 2 ? Integer.valueOf(genomePosition[2])
                    : chromosomeLocationStart;
        }
    }

    /**
     * To avoid consideration of the same genes written in different cases.
     */
    private void geneNameToUpperCase() {
        setGene(gene.toUpperCase());
    }
}
