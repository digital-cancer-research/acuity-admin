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
