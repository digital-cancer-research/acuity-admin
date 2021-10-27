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

package com.acuity.visualisations.web.dao;

import com.acuity.visualisations.web.entity.xml.Help;
import com.acuity.visualisations.web.entity.xml.Page;
import com.acuity.visualisations.web.entity.xml.Pages;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class reads in the help.xml file. The help.xml file contains elements of page names,
 * which contain elements of page element and help text.  The page names refer to JSP file
 * names, the page element refers to the HTML ID that has help text associated with.
 * <p/>
 * <pre>
 * &lt;pages>
 *   &lt;page>
 *     &lt;pageName>admin&lt;/pageName>
 *     &lt;help>
 *       &lt;pageElement>drugProgramme&lt;/pageElement>
 *       &lt;helpText>The given name of the drug programme&lt;/helpText>
 *     &lt;/help>
 *   &lt;/page>
 * &lt;/pages>
 * </pre>
 */
@Repository
public class HelpDao implements IHelpDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Map<String, String>> parseHelpXml() throws JAXBException, IOException {

        // Parse the XML
        JAXBContext jaxbContext = JAXBContext.newInstance(Pages.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        Pages helpXml = (Pages) jaxbUnmarshaller.unmarshal(new ClassPathResource("help.xml").getInputStream());

        // Load the Pages object into a HashMap
        Map<String, Map<String, String>> helpMap = new HashMap<String, Map<String, String>>();
        for (Page page : helpXml.getPageCollection()) {

            Map<String, String> elementHelpMap = new HashMap<String, String>();
            for (Help help : page.getHelp()) {
                elementHelpMap.put(help.getPageElement().toLowerCase(), help.getHelpText().replaceAll(" +", " "));
            }

            helpMap.put(page.getPageName(), elementHelpMap);
        }

        return helpMap;
    }
}
