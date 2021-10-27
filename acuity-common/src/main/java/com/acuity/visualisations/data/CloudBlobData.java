package com.acuity.visualisations.data;

import com.microsoft.azure.storage.blob.CloudBlockBlob;
import java.io.InputStream;
import java.util.Date;
import lombok.SneakyThrows;

public class CloudBlobData implements Data {

    private CloudBlockBlob blob;

    public CloudBlobData(CloudBlockBlob blob) {
        this.blob = blob;
    }

    @Override
    @SneakyThrows
    public InputStream stream() {
        return blob.openInputStream();
    }

    @SneakyThrows
    public boolean exists() {
        return blob.exists();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @SneakyThrows
    @Override
    public long timestamp() {
        blob.downloadAttributes();
        Date lastModified = blob.getProperties().getLastModified();

        if (lastModified != null) {
            return lastModified.getTime();
        }

        return 0;
    }

    @SneakyThrows
    @Override
    public long length() {
        blob.downloadAttributes();
        return blob.getProperties().getLength();
    }
}