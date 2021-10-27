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

package com.acuity.visualisations.mapping;

import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Class for compact storage of the hex-string in the memory.
 * For example, it uses just 20 bytes for a string like this "0f03a2be762a715e33c7da3259cc4084a9b9b077" (instead of 80 bytes)
 */
public class OctetString implements Serializable {

    private static final long serialVersionUID = -2995247962892546041L;
    private final byte[] bytes;

    public OctetString(byte[] bytes) {
        this.bytes = bytes;
    }

    public OctetString(String hexString) {
        this.bytes = DatatypeConverter.parseHexBinary(hexString);
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return bytesToHexString(bytes);
    }

    public static String bytesToHexString(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OctetString that = (OctetString) o;

        return Arrays.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
