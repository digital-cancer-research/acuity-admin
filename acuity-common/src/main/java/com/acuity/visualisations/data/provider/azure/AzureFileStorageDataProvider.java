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

import com.acuity.visualisations.data.CloudFileData;
import com.acuity.visualisations.data.Data;
import com.acuity.visualisations.data.provider.DataProvider;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.ListFileItem;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

public class AzureFileStorageDataProvider implements DataProvider {

    private static final String AZURE_FILE_PROTO_PREFIX = "azure-file://";

    private CloudFileClient client;

    @Autowired
    public AzureFileStorageDataProvider(CloudFileClient client) {
        this.client = client;
    }

    @Override
    public boolean match(@NonNull String location) {
        return location.startsWith(AZURE_FILE_PROTO_PREFIX);
    }

    private String trim(String location) {
        return location.substring(AZURE_FILE_PROTO_PREFIX.length());
    }

    @Override
    @SneakyThrows
    public Data get(@NonNull String location) {
        String[] tokens = trim(location).split("/");

        CloudFileShare share = client.getShareReference(tokens[0]);
        CloudFileDirectory root = share.getRootDirectoryReference();

        CloudFile file = (CloudFile) walk(root, tokens, 1);
        //remove root directory from path
        String path = trim(location).replace(share.getName() + "/", "");
        return new CloudFileData(file, root.getDirectoryReference(path).exists());
    }

    @SneakyThrows
    private ListFileItem walk(CloudFileDirectory directory, String[] tokens, int index) {
        if (index < tokens.length - 1) {
            return walk(directory.getDirectoryReference(tokens[index]), tokens, index + 1);
        } else {
            return directory.getFileReference(tokens[index]);
        }
    }
}
