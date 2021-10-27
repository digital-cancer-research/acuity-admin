package com.acuity.visualisations.batch.writer;

import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtil;
import com.acuity.visualisations.model.output.OutputEntity;

import java.util.Arrays;
import java.util.List;

public final class MaxRowUpdatePolicy {
    private MaxRowUpdatePolicy() {
    }

    public static OutputEntity applyUpdatePolicySingle(List<OutputEntity> toUpdate, ConfigurationUtil<?> configurationUtil) {
        if (toUpdate.size() < 2) {
            return toUpdate.get(0);
        }
        OutputEntity latestEntity = null;
        for (OutputEntity entity : toUpdate) {
            if (latestEntity == null || bigger(entity, latestEntity, configurationUtil)) {
                latestEntity = entity;
            }
        }
        return latestEntity;
    }

    public static OutputEntity applyUpdatePolicySingle(List<OutputEntity> toUpdate, String[] dataFileNames) {
        if (toUpdate.size() < 2) {
            return toUpdate.get(0);
        }
        OutputEntity latestEntity = null;
        for (OutputEntity entity : toUpdate) {
            if (latestEntity == null || bigger(entity, latestEntity, dataFileNames)) {
                latestEntity = entity;
            }
        }
        return latestEntity;
    }

    private static boolean bigger(OutputEntity entity1, OutputEntity entity2, ConfigurationUtil<?> configurationUtil) {
        List<String> dataFiles = Arrays.asList(configurationUtil.getDataFileNames());
        String dataFile1 = entity1.getSourceName();
        String dataFile2 = entity2.getSourceName();
        int dataFileIndex1 = dataFiles.indexOf(dataFile1);
        int dataFileIndex2 = dataFiles.indexOf(dataFile2);
        if (dataFileIndex1 != dataFileIndex2) {
            return dataFileIndex1 > dataFileIndex2;
        }
        return entity1.getRowNumber() > entity2.getRowNumber();
    }

    private static boolean bigger(OutputEntity entity1, OutputEntity entity2, String[] dataFileNames) {
        List<String> dataFiles = Arrays.asList(dataFileNames);
        String dataFile1 = entity1.getSourceName();
        String dataFile2 = entity2.getSourceName();
        int dataFileIndex1 = dataFiles.indexOf(dataFile1);
        int dataFileIndex2 = dataFiles.indexOf(dataFile2);
        if (dataFileIndex1 != dataFileIndex2) {
            return dataFileIndex1 > dataFileIndex2;
        }
        return entity1.getRowNumber() > entity2.getRowNumber();
    }
}
