package com.acuity.visualisations.data;

import java.io.InputStream;

public interface Data {

    InputStream stream();

    boolean exists();

    boolean isDirectory();

    long timestamp();

    long length();

    default boolean isLocal() {
        return false;
    }

}
