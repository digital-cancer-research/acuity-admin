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

/**
 * Created by knml167 on 05/05/14.
 */
public class ResourceInfoFile extends ResourceInfo {

    public ResourceInfoFile(String resourceName, FileStandard fileStandard) {
        super(resourceName, fileStandard);
        this.resourceNameFact = resourceName;
    }

    private String resourceNameFact;

    @Override
    public String getResourceNameFact() {
        return resourceNameFact;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }


    @Override
    public String toString() {
        return resourceName;
    }
}
