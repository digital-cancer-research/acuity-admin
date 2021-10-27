package com.acuity.visualisations.model.output;

import com.acuity.visualisations.transform.entity.EntityDescriptionRule;

public final class OutputEntityUtil {

    private OutputEntityUtil() {
    }
    
    public static void setSha1(OutputEntity entity, EntityDescriptionRule entityDescriptionRule) throws Exception {
        entity.setIntHashForSecondaryFields(entity.getIntHashForSecondaryFields(entityDescriptionRule));
        entity.setSha1ForUniqueFields(entity.getSha1ForUniqueFields(entityDescriptionRule));
        entity.setSha1ForReferencedFields(entity.getSha1ForReferencedFields(entityDescriptionRule));
        entity.setSha1ForReferFields(entity.getSha1ForReferFields(entityDescriptionRule));

    }
}
