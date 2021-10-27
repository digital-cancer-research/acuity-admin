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

package com.acuity.visualisations.util;

import com.acuity.visualisations.mapping.OctetString;
import com.acuity.visualisations.model.output.OutputEntity;
import com.acuity.visualisations.transform.entity.EntityBaseRule;
import com.acuity.visualisations.transform.table.TableBaseRule;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.zip.Adler32;

public final class HashUtil {

    public static final String SHA1 = "SHA-1";
    public static final String MD5 = "MD5";

    private HashUtil() {
    }

    public static String getHashForSha1(Object... objects) throws NoSuchAlgorithmException {
        return getHashForAlgorithm(SHA1, objects);
    }

    public static String getHashForAlgorithm(String algorithm, Object... objects) throws NoSuchAlgorithmException {
        String str = objectsToString(objects);
        return str == null ? null : generateHash(str, algorithm);
    }

    private static String generateHash(String text, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(text.getBytes(StandardCharsets.UTF_8));
        byte[] hash = md.digest();
        return OctetString.bytesToHexString(hash);
    }

    public static String objectsToString(Object... objects) {
        if (objects == null || objects.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Object obj : objects) {
            String str;
            if (obj == null) {
                str = "null";
            } else if (obj instanceof Date) {
                Date date = (Date) obj;
                DateFormat df = date.getTime() % 60000 == 0 ? new SimpleDateFormat("yyyy-MM-dd HH:mm") : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                str = df.format(date);
            } else if (obj instanceof String || obj instanceof Integer ||  obj instanceof Double || obj instanceof Long || obj instanceof BigDecimal
                    || obj instanceof LocalDateTime) {
                str = obj.toString();
            } else {
                throw new RuntimeException("Hash unknown type");
            }
            sb.append(str);
            sb.append('|');
        }
        return sb.toString();
    }

    public static String getSha1ForFieldList(OutputEntity entity, List<EntityBaseRule> fieldsRules) throws Exception {
        Object[] uniqueFieldValues = new Object[fieldsRules.size()];
        for (int i = 0; i < fieldsRules.size(); i++) {
            EntityBaseRule uniqueFieldRule = fieldsRules.get(i);
            uniqueFieldValues[i] = ReflectionUtil.getFieldValue(entity, uniqueFieldRule.getName());
        }
        return getHashForSha1(uniqueFieldValues);
    }

    public static int getIntHashForFieldList(OutputEntity entity, List<EntityBaseRule> fieldsRules) throws Exception {
        Object[] fieldValues = new Object[fieldsRules.size()];
        for (int i = 0; i < fieldsRules.size(); i++) {
            EntityBaseRule uniqueFieldRule = fieldsRules.get(i);
            fieldValues[i] = ReflectionUtil.getFieldValue(entity, uniqueFieldRule.getName());
        }
        String str = objectsToString(fieldValues);

        return str == null ? 0 : getCrcHash(str);
    }

    public static int getCrcHash(String text) {
        Adler32 crc = new Adler32();
        crc.update(text.getBytes());
        return (int) crc.getValue();
    }

    public static String getSha1ForFieldListForUniqueConstraint(OutputEntity entity, List<TableBaseRule> fieldsRules) throws Exception {
        Object[] uniqueFieldValues = new Object[fieldsRules.size()];
        for (int i = 0; i < fieldsRules.size(); i++) {
            TableBaseRule uniqueFieldRule = fieldsRules.get(i);
            uniqueFieldValues[i] = ReflectionUtil.getFieldValue(entity, uniqueFieldRule.getName());
        }
        return getHashForSha1(uniqueFieldValues);
    }

}
