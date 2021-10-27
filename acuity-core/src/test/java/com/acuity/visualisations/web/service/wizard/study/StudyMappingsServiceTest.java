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
