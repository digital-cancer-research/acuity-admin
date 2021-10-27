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
