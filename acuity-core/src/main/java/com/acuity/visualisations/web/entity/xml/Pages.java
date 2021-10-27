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
