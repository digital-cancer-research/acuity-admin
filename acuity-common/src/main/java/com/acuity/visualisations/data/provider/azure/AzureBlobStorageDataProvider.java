package com.acuity.visualisations.data.provider.azure;

import com.acuity.visualisations.data.CloudBlobData;
import com.acuity.visualisations.data.Data;
import com.acuity.visualisations.data.provider.DataProvider;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

public class AzureBlobStorageDataProvider implements DataProvider {

    private static final String AZURE_BLOB_PROTO_PREFIX = "azure-blob://";

    private CloudBlobClient client;

    @Autowired
    public AzureBlobStorageDataProvider(CloudBlobClient client) {
        this.client = client;
    }

    @Override
    public boolean match(@NonNull String location) {
        return location.startsWith(AZURE_BLOB_PROTO_PREFIX);
    }

    private String trim(String location) {
        return location.substring(AZURE_BLOB_PROTO_PREFIX.length());
    }

    @Override
    @SneakyThrows
    public Data get(@NonNull String location) {
        String[] tokens = trim(location).split("/");

        if (tokens.length != 2) {
            throw new IllegalArgumentException("Illegal path to BLOB");
        }

        CloudBlobContainer container = client.getContainerReference(tokens[0]);
        CloudBlockBlob blob = container.getBlockBlobReference(tokens[1]);

        return new CloudBlobData(blob);
    }
}
