package com.acuity.visualisations.sdtm.entity;

import com.acuity.visualisations.batch.reader.tablereader.TableRow;


public abstract class SdtmEntity {
    protected String seq;

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public abstract void read(TableRow row);

    public String readString(TableRow row, String column) {
        Object value = row.getValue(column);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return ((String) value).trim();
        }
        return value.toString();
    }

    /**
     * Read string and save memory
     *
     * @param row
     * @param column
     * @return
     */
    public String readStringIntern(TableRow row, String column) {
        Object value = row.getValue(column);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return ((String) value).trim().intern();
        }
        return value.toString().intern();
    }

    public Double readDouble(TableRow row, String column) {
        Object value = row.getValue(column);
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof String) {
            try {
                return Double.valueOf(((String) value).trim());
            } catch (Exception e) {
                return null;
            }
        }
        return Double.valueOf(value.toString());
    }
}
