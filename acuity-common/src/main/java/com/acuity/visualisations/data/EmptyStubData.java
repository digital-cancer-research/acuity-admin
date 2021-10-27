package com.acuity.visualisations.data;

import java.io.InputStream;

public final class EmptyStubData implements Data {

    private static EmptyStubData instance;

    private EmptyStubData() {
    }

    /**
     * This class represents empty data. Returned in cases of missing files or exceptions
     */
    public static EmptyStubData getInstance() {
        if (instance == null) {
            instance = new EmptyStubData();
        }
        return instance;
    }

    @Override
    public InputStream stream() {
        throw new UnsupportedOperationException("Stub Data could not be read");
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long timestamp() {
        return 0;
    }

    @Override
    public long length() {
        return 0;
    }


}
