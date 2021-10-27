package com.acuity.visualisations.data;

import java.io.InputStream;
import jcifs.smb.SmbFile;
import lombok.SneakyThrows;

public class SmbData implements Data {

    private SmbFile file;

    public SmbData(SmbFile file) {
        this.file = file;
    }

    @SneakyThrows
    @Override
    public InputStream stream() {
        return file.getInputStream();
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
}
