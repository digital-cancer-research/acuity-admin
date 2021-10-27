package com.acuity.visualisations.util;

import junit.framework.TestCase;

public class HashUtilTest extends TestCase {

    public void testGetSha1ForFieldList() throws Exception {
        Object[] array = {"", null, "E0002010", "", "D4510C00005", "STDY8021"};
        String hash = HashUtil.getHashForSha1(array);
        System.out.println(hash);
    }
}
