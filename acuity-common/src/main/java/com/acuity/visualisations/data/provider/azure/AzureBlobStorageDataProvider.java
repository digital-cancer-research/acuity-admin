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
