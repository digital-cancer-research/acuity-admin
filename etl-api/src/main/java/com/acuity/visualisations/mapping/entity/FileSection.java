package com.acuity.visualisations.mapping.entity;

import java.util.ArrayList;
import java.util.List;

public class FileSection extends MappingEntity implements StaticEntity {
    public static final Long PATIENT_INFORMATION_SECTION = Long.valueOf(1);
    private String name;
    private List<FileDescription> fileDescriptions = new ArrayList<FileDescription>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FileDescription> getFileDescriptions() {
        return fileDescriptions;
    }

    public void setFileDescriptions(List<FileDescription> fileDescriptions) {
        this.fileDescriptions = fileDescriptions;
    }
}
