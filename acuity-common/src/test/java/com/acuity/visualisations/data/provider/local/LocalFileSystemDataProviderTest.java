package com.acuity.visualisations.data.provider.local;

import au.com.bytecode.opencsv.CSVReader;
import com.acuity.visualisations.data.Data;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LocalFileSystemDataProviderTest {

    private static final String CURRENT_TEST_DIRECTORY = System.getProperty("user.dir");
    private static final String RELATIVE_TEST_FILE_PATH = "/src/test/resources/test.csv";
    private static final String TEST_FILE_PATH = CURRENT_TEST_DIRECTORY.concat(RELATIVE_TEST_FILE_PATH);
    private static final String PREFIX = "local://";

    LocalFileSystemDataProvider localFileSystemDataProvider = new LocalFileSystemDataProvider();

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(localFileSystemDataProvider, "path", CURRENT_TEST_DIRECTORY);
        ReflectionTestUtils.setField(localFileSystemDataProvider, "prefix", PREFIX);
    }

    @Test
    public void localDataProviderShouldMatchLocationsWithLocalPrefix() {
        String correctLocation = "local://" + RELATIVE_TEST_FILE_PATH;
        assertThat(localFileSystemDataProvider.match(correctLocation)).isTrue();
    }

    @Test
    public void localDataProviderShouldNotMatchLocationWithNoLocalPrefix() {
        String incorrectLocation = "something://acuitydata/some_file.csv";
        assertThat(localFileSystemDataProvider.match(incorrectLocation)).isFalse();
    }

    @Test
    public void localDataProviderShouldResolveLocalPath() {
        //given
        String correctLocation = "local://" + RELATIVE_TEST_FILE_PATH;
        Path expectedFilePath = Paths.get(TEST_FILE_PATH);
        //when
        Optional<Path> optionalPath = localFileSystemDataProvider.composeLocalPathFromExternalLocation(correctLocation);
        //then
        assertThat(optionalPath.isPresent()).isTrue();
        Path path = optionalPath.get();
        assertThat(path.toString()).doesNotContain(PREFIX);
        assertThat(path).isEqualTo(expectedFilePath);
        assertThat(path.toString()).contains(CURRENT_TEST_DIRECTORY);
    }

    @Test
    public void localDataProviderShouldMatchAnyPathWithCorrectPrefix() {
        //attempt to access external directories on local machine by user input
        String location = "local://../some_file.csv";
        boolean isMatch = localFileSystemDataProvider.match(location);
        assertThat(isMatch).isTrue();
    }

    @Test
    public void shouldGiveEmptyStubDataIfPathNotMatchesSourceFileDir() {
        //attempt to access external directories on local machine by user input
        String location = "local://../some_file.csv";
        Data data = localFileSystemDataProvider.get(location);
        assertThat(data.exists()).isFalse();
        assertThat(data.isDirectory()).isFalse();
    }

    @Test
    public void shouldGiveLocalFileDataIfGivenPathMatchesSourceFolderLocation() {
        String location = "local://" + RELATIVE_TEST_FILE_PATH;
        Data data = localFileSystemDataProvider.get(location);
        assertThat(data.exists()).isTrue();
        assertThat(data.isDirectory()).isFalse();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(data.stream()))) {
            List<String[]> allLines = csvReader.readAll();
            assertThat(allLines.size()).isEqualTo(1);
            assertThat(allLines.get(0)).contains("Column1", "Column2", "Column3");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
