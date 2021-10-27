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

package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.web.service.IHelpService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller class for getting text associated with a "?" on the UI
 */
@Controller
public class HelpController implements InitializingBean {

    /**
     * A map of page as the key with a value of a map containing the
     * page element as the key and a value of the help text
     */
    private static Map<String, Map<String, String>> helpMap = new HashMap<String, Map<String, String>>();

    /**
     * An implementation of the help service layer class that fetches
     * the help
     */
    @Autowired
    private IHelpService service;

    /**
     * Gets the help text associated with the given JSP page and page element
     *
     * @param jspPage     The name of the JSP page without the file extension
     * @param pageElement A reference to the element on the UI page the the
     *                    help text is associated with
     * @returns The help text to go with the UI's "?"
     */
    public static String getHelpText(String jspPage, String pageElement) {
        return HelpController.helpMap.get(jspPage).get(pageElement.toLowerCase());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        HelpController.helpMap = this.service.getHelp();
    }
}
