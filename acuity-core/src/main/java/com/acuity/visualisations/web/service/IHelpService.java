package com.acuity.visualisations.web.service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Map;

/**
 * Gets the help text for UI elements from HelpRepository
 */
public interface IHelpService {

    /**
     * Gets the help text from {@link com.acuity.visualisations.web.dao.HelpDao}
     *
     * @return The help text from {@link com.acuity.visualisations.web.dao.HelpDao}
     *         as a HashMap with the JSP page as the key and a value containing a map
     *         of HTML element and help text as the value.
     * @throws IOException   Thrown when loading the help.xml file
     * @throws JAXBException Thrown when parsing the help.xml file
     */
    Map<String, Map<String, String>> getHelp() throws JAXBException, IOException;
}
