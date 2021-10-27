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
