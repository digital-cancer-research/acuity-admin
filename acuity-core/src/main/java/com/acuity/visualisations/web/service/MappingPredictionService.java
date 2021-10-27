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

package com.acuity.visualisations.web.service;

import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.mapping.dao.IFileRuleDao;
import com.acuity.visualisations.web.dao.MappingDao;
import com.acuity.visualisations.web.entity.MappedColumnInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MappingPredictionService {
    @Autowired
    private SourceService sourceService;

    @Autowired
    private MappingDao mappingDao;

    @Autowired
    private IFileRuleDao fileRuleDao;

    @Autowired
    private DataProvider provider;

    /**
     * @param fileDescriptionId
     * @param path              path to samba file directory (windows format)
     * @return path+fileName
     */
    public String lookForSimilarFile(long fileDescriptionId, String path) {
        path = dropLastBackSlash(path);
        List<String> files = fileRuleDao.getFileRuleNamesByFileDescriptionId(fileDescriptionId);
        //extract only filenames
        for (int i = 0; i < files.size(); i++) {
            files.set(i, getFileName(files.get(i)));
        }

        files = getMostCommonFiles(files);

        //check files to exists
        for (String fileName : files) {
            String filePath = path + '/' + fileName;
            log.debug("Checking: {}", filePath);
            try {
                if (provider.get(filePath).exists()) {
                    log.debug("Exists");
                    return path + '\\' + fileName;
                }
            } catch (Exception e) {
                log.debug("Not found: {}", filePath);
            }
        }
        return null;
    }

    private static String columnNamesString(List<MappedColumnInfo> mappedColumnInfos) {
        StringBuilder sb = new StringBuilder();

        for (MappedColumnInfo mappedColumnInfo : mappedColumnInfos) {
            sb.append(mappedColumnInfo.getColumnName()).append('|');
        }
        return sb.toString();
    }


    public Long lookForSimilarFileRuleId(long fileDescriptionId, long projectId, String filepath) {
        List<String> columnNames;
        try {
            columnNames = sourceService.getColumnNames(filepath);
        } catch (Exception e) {
            log.warn("Can't read file: {}", filepath, e);
            return null;
        }

        List<MappedColumnInfo> mappedColumns = mappingDao.getMappedColumnInfosByFileDescriptionId(fileDescriptionId);

        Map<Long, List<MappedColumnInfo>> mappedColumnsMapByFileRuleId = new HashMap<>();

        for (MappedColumnInfo mappedColumn : mappedColumns) {
            List<MappedColumnInfo> mappedColumnInfos = mappedColumnsMapByFileRuleId.get(mappedColumn.getFileRuleId());
            if (mappedColumnInfos == null) {
                mappedColumnInfos = new ArrayList<>();
                mappedColumnsMapByFileRuleId.put(mappedColumn.getFileRuleId(), mappedColumnInfos);
            }
            mappedColumnInfos.add(mappedColumn);
        }

        //Calculate popularity of columns
        Map<String, Integer> popularity = new HashMap<>();

        for (List<MappedColumnInfo> mappedColumnInfos : mappedColumnsMapByFileRuleId.values()) {
            String columnNamesString = columnNamesString(mappedColumnInfos);
            popularity.put(columnNamesString, popularity.containsKey(columnNamesString) ? popularity.get(columnNamesString) + 1 : 1);
        }

        Map<Long, Float> scores = new HashMap<>();

        for (Long fileRuleId : mappedColumnsMapByFileRuleId.keySet()) {
            float score = 0;

            List<MappedColumnInfo> mappedColumnInfos = mappedColumnsMapByFileRuleId.get(fileRuleId);

            for (MappedColumnInfo mappedColumnInfo : mappedColumnInfos) {

                boolean matched = false;

                for (String columnName : columnNames) {
                    if (columnName.equals(mappedColumnInfo.getColumnName())) {
                        matched = true;
                        break;
                    }
                }

                if (matched) {
                    score += 1;
                } else {
                    score -= 1;
                }
            }

            if (score > 2) {
                MappedColumnInfo sample = mappedColumnInfos.get(0);
                //match project id
                if (sample.getProjectId() == projectId) {
                    score += 0.5;
                }

                //match file name
                if (compareFileNames(filepath, sample.getFileRuleFilePath())) {
                    score += 0.1;
                }
            }
            scores.put(fileRuleId, score);
        }

        //search FileRule.id with max score
        float maxScore = 0;
        Long fileRuleIdWithMaxScore = null;

        for (Map.Entry<Long, Float> score : scores.entrySet()) {
            if (score.getValue() > maxScore) {
                maxScore = score.getValue();
                fileRuleIdWithMaxScore = score.getKey();
            }
        }
        return maxScore > 3 ? fileRuleIdWithMaxScore : null;
    }

    private static boolean compareFileNames(String filePath1, String filePath2) {
        String fileName1 = getFileName(filePath1);
        String fileName2 = getFileName(filePath2);
        return !StringUtils.isEmpty(fileName1) && !StringUtils.isEmpty(fileName2) && fileName1.equals(fileName2);
    }

    static String dropLastBackSlash(String path) {
        path = path.trim();

        if (path.endsWith("\\") || path.endsWith("/")) {
            path = path.replaceAll("(/|\\\\)+$", "");
        }
        return path;
    }

    private static String getFileName(String filePath) {
        int lastSlashIndex = filePath.lastIndexOf('/');
        if (lastSlashIndex < 0) {
            lastSlashIndex = filePath.lastIndexOf('\\');
        }
        if (lastSlashIndex < 0) {
            return null;
        } else {
            return filePath.substring(lastSlashIndex + 1);
        }
    }


    private static List<String> getMostCommonFiles(List<String> strings) {
        final Map<String, Integer> counter = new HashMap<>();

        for (String str : strings) {
            counter.put(str, 1 + (counter.containsKey(str) ? counter.get(str) : 0));
        }

        List<String> list = new ArrayList<>(counter.keySet());
        Collections.sort(list, (a, b) -> counter.get(b) - counter.get(a));
        return list;
    }
}
