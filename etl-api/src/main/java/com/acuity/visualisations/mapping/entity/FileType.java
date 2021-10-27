package com.acuity.visualisations.mapping.entity;

public class FileType extends MappingEntity implements StaticEntity {

    private String type;
    private String delimiter;

    public FileType() {
    }

    public FileType(String type, String delimiter) {
        super();
        this.type = type;
        this.delimiter = delimiter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((delimiter == null) ? 0 : delimiter.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        FileType other = (FileType) obj;
        if (delimiter == null) {
            if (other.delimiter != null) {
                return false;
            }
        } else {
            if (!delimiter.equals(other.delimiter)) {
                return false;
            }
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else {
            if (!type.equals(other.type)) {
                return false;
            }
        }
        return true;
    }

}
