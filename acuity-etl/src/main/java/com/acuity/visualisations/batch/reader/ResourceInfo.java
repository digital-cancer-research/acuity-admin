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
