package com.acuity.visualisations.web.dao;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Map;

/**
 * Interface for the repository class that reads in the UI help text
 */
public interface IHelpDao {

    /**
     * Parses the help.xml file into a map of page name with the key and
     * a map of HTML ID and help text as the value.
     *
     * @throws IOException   Thrown when loading the help.xml file
     * @throws JAXBException Thrown when parsing the help.xml file
     * @returns A map of page name with the key and a map of HTML ID and
     * help text as the value
     */
    Map<String, Map<String, String>> parseHelpXml() throws JAXBException, IOException;
}
