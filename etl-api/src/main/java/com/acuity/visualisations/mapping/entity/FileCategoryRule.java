package com.acuity.visualisations.mapping.entity;

import java.util.List;

public class FileCategoryRule extends MappingEntity implements StaticEntity {
    private String name;
    private List<FileDescription> fileDescriptions;

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
