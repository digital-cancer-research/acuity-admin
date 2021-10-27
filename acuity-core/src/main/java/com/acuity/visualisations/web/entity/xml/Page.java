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
