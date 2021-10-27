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

package com.acuity.visualisations.data.provider;

import com.acuity.visualisations.data.Data;
import com.acuity.visualisations.data.provider.azure.AzureFileStorageDataProvider;
import com.acuity.visualisations.data.provider.local.LocalFileSystemDataProvider;
import com.acuity.visualisations.data.provider.samba.SambaDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StubDataProviderTest {

    private static final String LOCAL_STORAGE_PREFIX = "local://";
    @Mock
    private AzureFileStorageDataProvider azureFileStorageDataProvider;
    @Mock
    private SambaDataProvider sambaDataProvider;
    @Mock
    private LocalFileSystemDataProvider localFileSystemDataProvider;

    private final StubDataProvider stubDataProvider = new StubDataProvider();
    @Mock
    private List<DataProvider> providers;

    @InjectMocks
    private MatchingDataProvider matchingDataProvider;

    @Before
    public void setUp() {
        when(providers.stream())
                .thenReturn(
                        Stream.of(
                                azureFileStorageDataProvider,
                                sambaDataProvider,
                                localFileSystemDataProvider,
                                stubDataProvider)
                );
        when(sambaDataProvider.match(anyString())).thenCallRealMethod();
        when(azureFileStorageDataProvider.match(anyString())).thenCallRealMethod();
        when(localFileSystemDataProvider.match(anyString())).thenAnswer(invocationOnMock ->
        {
            String location = (String) invocationOnMock.getArguments()[0];
            return location.contains(LOCAL_STORAGE_PREFIX);
        });
    }

    @Test
    public void checkStubProviderValidatesInvalidLocations() {
        //given
        String locationToCheck = "file://disext_junk.csv";
        Data stubData = stubDataProvider.get(locationToCheck);
        //when
        Data dataReturnedByCompositeDataProvider = matchingDataProvider.get(locationToCheck);
        //then
        assertThat(stubData).isEqualTo(dataReturnedByCompositeDataProvider);
        assertThat(stubData.exists()).isFalse();
    }
}
