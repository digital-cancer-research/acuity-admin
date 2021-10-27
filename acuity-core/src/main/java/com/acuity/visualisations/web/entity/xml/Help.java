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

package com.acuity.visualisations.web.entity.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains a list of the help text for each "?" icon displayed on the
 * admin UI pages
 */
@XmlRootElement(namespace = "entities.Page")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Help {

    /**
     * The HTML element ID that the help text is referencing
     */
    private String pageElement;

    /**
     * The help text to display
     */
    private String helpText;

    /**
     * @return The HTML element ID that the help text is referencing
     */
    public String getPageElement() {
        return pageElement;
    }

    /**
     * Only used by JAXB
     *
     * @param pageElement The HTML element ID that the help text is referencing
     */
    public void setPageElement(String pageElement) {
        this.pageElement = pageElement;
    }

    /**
     * @return The help text to display
     */
    public String getHelpText() {
        return helpText;
    }

    /**
     * Only used by JAXB
     *
     * @param helpText The help text to display
     */
    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.pageElement.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return this.pageElement.equals(obj);
        } else {
            return false;
        }
    }
}
