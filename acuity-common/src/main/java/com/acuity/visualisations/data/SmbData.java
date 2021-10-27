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
