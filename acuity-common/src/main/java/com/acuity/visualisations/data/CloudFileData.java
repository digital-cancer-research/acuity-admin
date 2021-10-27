package com.acuity.visualisations.data;

import com.microsoft.azure.storage.file.CloudFile;
import java.io.InputStream;
import java.util.Date;
import lombok.SneakyThrows;

public class CloudFileData implements Data {

    private CloudFile file;
    private boolean isDirectory;

    public CloudFileData(CloudFile file, boolean isDirectory) {
        this.isDirectory = isDirectory;
        this.file = file;
    }

    @Override
    @SneakyThrows
    public InputStream stream() {
        return file.openRead();
    }

    @SneakyThrows
    public boolean exists() {
        return file.exists();
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @SneakyThrows
    @Override
    public long timestamp() {
        file.downloadAttributes();
        Date lastModified = file.getProperties().getLastModified();

        if (lastModified != null) {
            return lastModified.getTime();
        }

        return 0;
    }

    @SneakyThrows
    @Override
    public long length() {
        file.downloadAttributes();
        return file.getProperties().getLength();
    }
}
