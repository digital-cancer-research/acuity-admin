package com.acuity.visualisations.util;

import com.acuity.visualisations.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class ETLConfiguration {

    private static final String STUDIES_CONF_FOLDER = "etl.studies.conf";
    private static final String IGNORE_UNPARSED_FILES = "etl.ignore.unparsed";

    private String studiesConfFolder;
    private String ignoreUnparsed;

    private static final Logger LOGGER = LoggerFactory.getLogger(ETLConfiguration.class);

    @PostConstruct
    private void init() throws Exception {
        String propertiesFileName = "sas_tool.properties";
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream(propertiesFileName);
            if (inputStream == null) {
                throw new ConfigurationException("Can't find configuration file");
            }
            properties.load(inputStream);
            ignoreUnparsed = properties.getProperty(IGNORE_UNPARSED_FILES);

            studiesConfFolder = properties.getProperty(STUDIES_CONF_FOLDER);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    LOGGER.error("Can't close input stream for file: {}", propertiesFileName);
                }
            }
        }
    }

    public boolean getIgnoreUnparsedFiles() {
        return Boolean.parseBoolean(ignoreUnparsed);
    }

    public String getStudiesConfFolder() {
        return studiesConfFolder;
    }

}
