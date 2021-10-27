//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.07.02 at 02:46:40 PM MSK 
//


package com.acuity.visualisations.transform.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for EntityDescriptionRule complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="EntityDescriptionRule">
 *   &lt;complexContent>
 *     &lt;extension base="{http://visualisations.acuity.com/EntitiesDescription}EntityBaseRule">
 *       &lt;sequence>
 *         &lt;element name="uniqueFields" type="{http://visualisations.acuity.com/EntitiesDescription}EntityFieldSetRule"/>
 *         &lt;element name="secondaryFields" type="{http://visualisations.acuity.com/EntitiesDescription}EntityFieldSetRule"/>
 *         &lt;element name="referredBy" type="{http://visualisations.acuity.com/EntitiesDescription}EntityReferedByRule" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="referBy" type="{http://visualisations.acuity.com/EntitiesDescription}EntityFieldSetRule" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntityDescriptionRule", propOrder = {
        "mandatory",
        "uniqueFields",
        "secondaryFields",
        "referredBy",
        "referBy"
})
public class EntityDescriptionRule
        extends EntityBaseRule {

    @XmlElement(required = true)
    protected EntityFieldSetRule uniqueFields;
    @XmlElement(required = true)
    protected EntityFieldSetRule secondaryFields;
    protected List<EntityReferedByRule> referredBy;
    protected List<EntityFieldSetRule> referBy;
    protected boolean mandatory;

    /**
     * Gets the value of the uniqueFields property.
     *
     * @return possible object is
     * {@link EntityFieldSetRule }
     */
    public EntityFieldSetRule getUniqueFields() {
        return uniqueFields;
    }

    /**
     * Sets the value of the uniqueFields property.
     *
     * @param value allowed object is
     *              {@link EntityFieldSetRule }
     */
    public void setUniqueFields(EntityFieldSetRule value) {
        this.uniqueFields = value;
    }

    /**
     * Gets the value of the secondaryFields property.
     *
     * @return possible object is
     * {@link EntityFieldSetRule }
     */
    public EntityFieldSetRule getSecondaryFields() {
        return secondaryFields;
    }

    /**
     * Sets the value of the secondaryFields property.
     *
     * @param value allowed object is
     *              {@link EntityFieldSetRule }
     */
    public void setSecondaryFields(EntityFieldSetRule value) {
        this.secondaryFields = value;
    }

    /**
     * Gets the value of the referredBy property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the referredBy property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferredBy().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityReferedByRule }
     */
    public List<EntityReferedByRule> getReferredBy() {
        if (referredBy == null) {
            referredBy = new ArrayList<EntityReferedByRule>();
        }
        return this.referredBy;
    }

    /**
     * Gets the value of the referBy property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the referBy property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferBy().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityFieldSetRule }
     */
    public List<EntityFieldSetRule> getReferBy() {
        if (referBy == null) {
            referBy = new ArrayList<EntityFieldSetRule>();
        }
        return this.referBy;
    }

    /**
     * Checks whether processing this entity is fail-fast.
     * <p/>
     * <p/>
     * This accessor method returns a flag to determine whether to
     * continue the batch process if the entity information is missing.
     */
    public boolean isMandatory() {
        return mandatory;
    }
}