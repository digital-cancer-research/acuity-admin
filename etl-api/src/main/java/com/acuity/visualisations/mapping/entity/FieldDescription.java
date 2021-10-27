package com.acuity.visualisations.mapping.entity;

public class FieldDescription extends MappingEntity implements StaticEntity {

    private String text;

    public FieldDescription() {
    }

    public FieldDescription(String text, Long id) {
        this.text = text;
        setId(id);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FieldDescription other = (FieldDescription) obj;
        if (text == null) {
            if (other.text != null) {
                return false;
            }
        } else {
            if (!text.equals(other.text)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "FieldDescription [text=" + text + "]";
    }

}
