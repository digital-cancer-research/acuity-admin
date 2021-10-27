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
