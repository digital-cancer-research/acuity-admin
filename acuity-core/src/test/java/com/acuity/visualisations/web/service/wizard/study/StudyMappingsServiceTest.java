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

package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.data.CloudFileData;
import com.acuity.visualisations.data.LocalFileData;
import com.acuity.visualisations.data.provider.MatchingDataProvider;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StudyMappingsServiceTest {
    private static StudyMappingsService service = new StudyMappingsService();

    private MatchingDataProvider dataProvider = mock(MatchingDataProvider.class);

    @Before
    public void setUp() throws Exception {
        service.setProvider(dataProvider);
    }

    @Test
    public void testMakeProperFileName() {
        String azureFile = "azure-file://acuitydata/dem_junk.csv";
        String localFile = "local/file/dose_junk.csv";

        when(dataProvider.get(any(String.class))).thenReturn(new CloudFileData(null, false));
        when(dataProvider.get(localFile)).thenReturn(new LocalFileData(null));

        String shareWithoutSlashes = "acuitydata\\\\dose_junk.csv";
        String shareWithSlashes = "\\\\acuitydata\\\\dose_junk.csv";
        assertThat(service.makeProperFileName(azureFile), is(azureFile));
        assertThat(service.makeProperFileName(localFile), is(localFile));
        assertThat(service.makeProperFileName(shareWithoutSlashes), is("\\\\".concat(shareWithoutSlashes)));
        assertThat(service.makeProperFileName(shareWithSlashes), is(shareWithSlashes));
    }

}
