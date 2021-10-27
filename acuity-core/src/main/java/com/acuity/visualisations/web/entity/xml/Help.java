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
