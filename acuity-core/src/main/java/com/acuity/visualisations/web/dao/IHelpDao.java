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
