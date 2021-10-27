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

import com.acuity.visualisations.web.dao.MappingDao;
import com.acuity.visualisations.web.entity.MappedColumnInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;

public class MappingPredictionServiceTest {

    @InjectMocks
    private MappingPredictionService mappingPredictionService;

    @Mock
    private SourceService sourceService;

    @Mock
    private MappingDao mappingDao;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    static long PROJECT_ID_0 = 100;
    static long PROJECT_ID_1 = 101;
    static long PROJECT_ID_2 = 102;

    static String FILE_DEM_1_CSV = "path/dem.csv";
    static String FILE_DEM_1_SAS = "path/dem.sas";

    static long FILE_RULE_ID_1 = 201;
    static long FILE_RULE_ID_2 = 202;

    static long FILE_RULE_ID_11 = 211;
    static long FILE_RULE_ID_12 = 212;
    static long FILE_RULE_ID_13 = 213;

    static long FILE_RULE_ID_21 = 221;
    static long FILE_RULE_ID_22 = 222;

    static final List<MappedColumnInfo> MAPPED_COLUMNS = Arrays.asList(
            //Columns set 1
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_1, "STUDY", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_1, "PART", FILE_DEM_1_CSV),

            //Columns set 2
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_2, "STUDY", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_2, "PART", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_2, "SUBJECT", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_2, "XXX", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_2, "YYY", FILE_DEM_1_CSV),

            //Project 1

            //Columns set 11 (patient data from the dem.csv)
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_11, "STUDY", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_11, "PART", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_11, "SUBJECT", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_11, "AGE", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_11, "RACE", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_11, "SEX", FILE_DEM_1_CSV),

            //Columns set 12 (patient data from the dem.sas)
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_12, "STUDY", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_12, "PART", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_12, "SUBJECT", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_12, "AGE", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_12, "RACE", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_12, "SEX", FILE_DEM_1_SAS),

            //Columns set 13 (patient data from the dem.sas with additional fields)
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_13, "STUDY", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_13, "PART", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_13, "SUBJECT", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_13, "AGE", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_13, "RACE", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_13, "SEX", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_13, "COLUMNX", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_1, FILE_RULE_ID_13, "COLUMNY", FILE_DEM_1_SAS),

            //Project 2

            //Columns set 21 (patient data from the dem.csv, but for another project)
            new MappedColumnInfo(PROJECT_ID_2, FILE_RULE_ID_21, "STUDY", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_2, FILE_RULE_ID_21, "PART", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_2, FILE_RULE_ID_21, "SUBJECT", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_2, FILE_RULE_ID_21, "AGE", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_2, FILE_RULE_ID_21, "RACE", FILE_DEM_1_CSV),
            new MappedColumnInfo(PROJECT_ID_2, FILE_RULE_ID_21, "SEX", FILE_DEM_1_CSV),


            //Columns set 22 (patient data from the dem.sas, but for another project)
            new MappedColumnInfo(PROJECT_ID_2, FILE_RULE_ID_22, "STUDY", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_2, FILE_RULE_ID_22, "PART", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_2, FILE_RULE_ID_22, "SUBJECT", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_2, FILE_RULE_ID_22, "AGE", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_2, FILE_RULE_ID_22, "RACE", FILE_DEM_1_SAS),
            new MappedColumnInfo(PROJECT_ID_2, FILE_RULE_ID_22, "SEX", FILE_DEM_1_SAS)

    );


    @Test
    public void emptySourceColumnsListShouldBeNotMatchedTest() throws Exception {
        Long fileDescriptionId = 1L;
        String FILE_IN = "path/test.csv";

        List<String> emptySourceColumns = Arrays.asList();

        when(sourceService.getColumnNames(FILE_IN)).thenReturn(emptySourceColumns);

        Assert.assertNull(mappingPredictionService.lookForSimilarFileRuleId(fileDescriptionId, PROJECT_ID_1, FILE_IN));

        verify(sourceService).getColumnNames(FILE_IN);
        verifyNoMoreInteractions(sourceService);
    }

    @Test
    public void emptyMappedColumnsListShouldBeNotMatchedTest() throws Exception {
        Long fileDescriptionId = 1L;
        String FILE_IN = "path/test.csv";
        List<MappedColumnInfo> emptyMappedColumns = Arrays.asList();

        List<String> sourceColumns = Arrays.asList("STUDY", "PART", "SUBJECT", "AGE", "RACE");

        when(sourceService.getColumnNames(FILE_IN)).thenReturn(sourceColumns);
        when(mappingDao.getMappedColumnInfosByFileDescriptionId(fileDescriptionId)).thenReturn(emptyMappedColumns);

        Assert.assertNull(mappingPredictionService.lookForSimilarFileRuleId(fileDescriptionId, PROJECT_ID_1, FILE_IN));

        verify(sourceService).getColumnNames(FILE_IN);
        verify(mappingDao).getMappedColumnInfosByFileDescriptionId(fileDescriptionId);
        verifyNoMoreInteractions(sourceService);
        verifyNoMoreInteractions(mappingDao);
    }

    @Test
    public void unknownSourceColumnsListShouldBeNotMatchedTest() throws Exception {
        Long fileDescriptionId = 1L;
        String FILE_IN = "path/test.csv";

        List<String> sourceColumns = Arrays.asList("STUDY", "PART", "IDDQD", "IDKFA");

        when(sourceService.getColumnNames(FILE_IN)).thenReturn(sourceColumns);
        when(mappingDao.getMappedColumnInfosByFileDescriptionId(fileDescriptionId)).thenReturn(MAPPED_COLUMNS);

        Assert.assertNull(mappingPredictionService.lookForSimilarFileRuleId(fileDescriptionId, PROJECT_ID_1, FILE_IN));

        verify(sourceService).getColumnNames(FILE_IN);
        verify(mappingDao).getMappedColumnInfosByFileDescriptionId(fileDescriptionId);
        verifyNoMoreInteractions(sourceService);
        verifyNoMoreInteractions(mappingDao);
    }


    @Test
    public void forTheSameColumnsSameProjectWinTest() throws Exception {
        Long fileDescriptionId = 1L;
        String FILE_IN = "path/test.csv";

        List<String> sourceColumns = Arrays.asList("STUDY", "PART", "SUBJECT", "AGE", "RACE", "SEX");

        when(sourceService.getColumnNames(FILE_IN)).thenReturn(sourceColumns);
        when(mappingDao.getMappedColumnInfosByFileDescriptionId(fileDescriptionId)).thenReturn(MAPPED_COLUMNS);

        Long result = mappingPredictionService.lookForSimilarFileRuleId(fileDescriptionId, PROJECT_ID_1, FILE_IN);
        Assert.assertTrue(result == FILE_RULE_ID_11 || result == FILE_RULE_ID_12);

        verify(sourceService).getColumnNames(FILE_IN);
        verify(mappingDao).getMappedColumnInfosByFileDescriptionId(fileDescriptionId);
        verifyNoMoreInteractions(sourceService);
        verifyNoMoreInteractions(mappingDao);
    }

    @Test
    public void forTheSameColumnsSameFileNameWinTest() throws Exception {
        Long fileDescriptionId = 1L;
        String FILE_IN = "anotherpath/dem.sas";

        List<String> sourceColumns = Arrays.asList("STUDY", "PART", "SUBJECT", "AGE", "RACE", "SEX");

        when(sourceService.getColumnNames(FILE_IN)).thenReturn(sourceColumns);
        when(mappingDao.getMappedColumnInfosByFileDescriptionId(fileDescriptionId)).thenReturn(MAPPED_COLUMNS);

        Long result = mappingPredictionService.lookForSimilarFileRuleId(fileDescriptionId, PROJECT_ID_0, FILE_IN);
        Assert.assertTrue(result == FILE_RULE_ID_12 || result == FILE_RULE_ID_22);

        verify(sourceService).getColumnNames(FILE_IN);
        verify(mappingDao).getMappedColumnInfosByFileDescriptionId(fileDescriptionId);
        verifyNoMoreInteractions(sourceService);
        verifyNoMoreInteractions(mappingDao);
    }

    @Test
    public void forTheSameColumnsAndSameProjectSameFileNameWinTest() throws Exception {
        Long fileDescriptionId = 1L;
        String FILE_IN = "anotherpath/dem.csv";

        List<String> sourceColumns = Arrays.asList("STUDY", "PART", "SUBJECT", "AGE", "RACE", "SEX");

        when(sourceService.getColumnNames(FILE_IN)).thenReturn(sourceColumns);
        when(mappingDao.getMappedColumnInfosByFileDescriptionId(fileDescriptionId)).thenReturn(MAPPED_COLUMNS);

        Assert.assertEquals(Long.valueOf(FILE_RULE_ID_11), mappingPredictionService.lookForSimilarFileRuleId(fileDescriptionId, PROJECT_ID_1, FILE_IN));

        verify(sourceService).getColumnNames(FILE_IN);
        verify(mappingDao).getMappedColumnInfosByFileDescriptionId(fileDescriptionId);
        verifyNoMoreInteractions(sourceService);
        verifyNoMoreInteractions(mappingDao);
    }

    @Test
    public void maxMatchedColumnsWinTest() throws Exception {
        Long fileDescriptionId = 1L;
        String FILE_IN = "path/test.csv";

        List<String> sourceColumns = Arrays.asList("STUDY", "PART", "SUBJECT", "AGE", "RACE", "SEX", "COLUMNX", "COLUMNY", "COLUMNZ");

        when(sourceService.getColumnNames(FILE_IN)).thenReturn(sourceColumns);
        when(mappingDao.getMappedColumnInfosByFileDescriptionId(fileDescriptionId)).thenReturn(MAPPED_COLUMNS);

        Long result = mappingPredictionService.lookForSimilarFileRuleId(fileDescriptionId, PROJECT_ID_1, FILE_IN);
        Assert.assertTrue(result == FILE_RULE_ID_13);

        verify(sourceService).getColumnNames(FILE_IN);
        verify(mappingDao).getMappedColumnInfosByFileDescriptionId(fileDescriptionId);
        verifyNoMoreInteractions(sourceService);
        verifyNoMoreInteractions(mappingDao);
    }

    @Test
    public void partialMatchButSameFileNameShouldBeMatchedTest() throws Exception {
        Long fileDescriptionId = 1L;
        String FILE_IN = "path/dem.csv";

        List<String> sourceColumns = Arrays.asList("STUDY", "PART", "XXX", "YYY", "ZZZ");

        when(sourceService.getColumnNames(FILE_IN)).thenReturn(sourceColumns);
        when(mappingDao.getMappedColumnInfosByFileDescriptionId(fileDescriptionId)).thenReturn(MAPPED_COLUMNS);

        Assert.assertEquals(Long.valueOf(FILE_RULE_ID_2), mappingPredictionService.lookForSimilarFileRuleId(fileDescriptionId, PROJECT_ID_1, FILE_IN));

        verify(sourceService).getColumnNames(FILE_IN);
        verify(mappingDao).getMappedColumnInfosByFileDescriptionId(fileDescriptionId);
        verifyNoMoreInteractions(sourceService);
        verifyNoMoreInteractions(mappingDao);
    }

    @Test
    public void dropLastBackSlashTest(){
        Assert.assertEquals("c:/path", MappingPredictionService.dropLastBackSlash("c:/path"));
        Assert.assertEquals("c:/path", MappingPredictionService.dropLastBackSlash("c:/path/"));
        Assert.assertEquals("c:/path", MappingPredictionService.dropLastBackSlash("c:/path//"));

        Assert.assertEquals("c:\\path", MappingPredictionService.dropLastBackSlash("c:\\path"));
        Assert.assertEquals("c:\\path", MappingPredictionService.dropLastBackSlash("c:\\path\\"));
        Assert.assertEquals("c:\\path", MappingPredictionService.dropLastBackSlash("c:\\path\\\\"));

    }
}
