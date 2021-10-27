package com.acuity.visualisations.data.util;

public final class FileTypeUtil {

    private static final String SAS_EXTENSION = ".sas7bdat";
    private static final String CSV_EXTENSION = ".csv";

    private FileTypeUtil() {
    }

    public static boolean isSasFile(String fullPathToFile) {
        return fullPathToFile.endsWith(SAS_EXTENSION);
    }

    public static boolean isCsvFile(String fullPathToFile) {
        return fullPathToFile.endsWith(CSV_EXTENSION);
    }

}
