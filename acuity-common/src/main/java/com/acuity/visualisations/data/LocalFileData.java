package com.acuity.visualisations.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import lombok.SneakyThrows;

public class LocalFileData implements Data {

    private File file;

    public LocalFileData(File file) {
        this.file = file;
    }

    @SneakyThrows
    @Override
    public InputStream stream() {
        return new FileInputStream(file);
    }

    @SneakyThrows
    public boolean exists() {
        return file.exists();
    }

    @SneakyThrows
    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @SneakyThrows
    @Override
    public long timestamp() {
        return file.lastModified();
    }

    @SneakyThrows
    @Override
    public long length() {
        return file.length();
    }

    @Override
    public boolean isLocal() {
        return true;
    }
}
