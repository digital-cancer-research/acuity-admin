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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class for the pages tag in help.xml
 */
@XmlRootElement
public class Pages {

    /**
     * A collection of {@link Page} objects
     */
    private Set<Page> pageCollection = new HashSet<Page>();

    /**
     * @return A collection of {@link Page} objects
     */
    public Set<Page> getPageCollection() {
        return pageCollection;
    }

    /**
     * Called from JAXB
     *
     * @param pages A collection of {@link Page} objects
     */
    @XmlElement(name = "page")
    public void setPageCollection(Set<Page> pages) {
        this.pageCollection = pages;
    }
}
