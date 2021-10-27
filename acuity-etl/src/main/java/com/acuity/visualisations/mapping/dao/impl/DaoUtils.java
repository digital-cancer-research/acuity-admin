package com.acuity.visualisations.mapping.dao.impl;


public final class DaoUtils {

    private DaoUtils() {
    }

    public static String generateQuestionMarks(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append("?");
            if (i != size - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
