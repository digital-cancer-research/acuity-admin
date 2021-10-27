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

package com.acuity.visualisations.batch.reader;

import com.acuity.visualisations.mapping.entity.FileStandard;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceInfo {

    protected String resourceName;
    protected String absolutePath;
    protected FileStandard fileStandard;

    public ResourceInfo(String resourceName) {
        this.resourceName = resourceName;
    }

    public ResourceInfo(String resourceName, FileStandard fileStandard) {
        this.resourceName = resourceName;
        this.fileStandard = fileStandard;
    }

    public FileStandard getFileStandard() {
        return fileStandard;
    }

    public void setFileStandard(FileStandard fileStandard) {
        this.fileStandard = fileStandard;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getResourceNameFact() {
        return resourceName;
    }
}
