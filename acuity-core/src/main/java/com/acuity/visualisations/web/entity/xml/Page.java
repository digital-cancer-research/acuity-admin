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
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class for the page tag in help.xml
 */
@XmlRootElement(namespace = "com.acuity.visualisations.web.entity.xml.Pages")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Page {

    /**
     * The name of the JSP file that is having the help text
     */
    private String pageName;

    /**
     * A collection of {@link Help} objects containing the help text and
     * the HTML ID being referenced
     */
    private Set<Help> help = new HashSet<Help>();

    /**
     * @return The name of the JSP file that is having the help text
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * Used only by JAXB
     *
     * @param pageName The name of the JSP file that is having the help text
     */
    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    /**
     * @return A collection of {@link Help} objects containing the help text and
     *         the HTML ID being referenced
     */
    public Set<Help> getHelp() {
        return help;
    }

    /**
     * Used only by JAXB
     *
     * @param help A collection of {@link Help} objects containing the help text and
     *             the HTML ID being referenced
     */
    public void setHelp(Set<Help> help) {
        this.help = help;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.pageName.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return this.pageName.equals(obj);
        } else {
            return false;
        }
    }
}
