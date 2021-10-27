package com.acuity.visualisations.mapping.entity;

import java.util.ArrayList;
import java.util.List;

public class FileRule extends MappingEntity implements DynamicEntity {

    private String name;
    private List<FileDescription> descriptions = new ArrayList<FileDescription>();
    private FileType fileType;
    private List<MappingRule> mappingRules = new ArrayList<MappingRule>();
    private boolean acuityEnabled;
    private boolean updateEnabled;
    private Long fileTypeId;

    private StudyRule studyRule;
    private FileStandard fileStandard;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FileDescription> getDescriptions() {
        return descriptions;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public StudyRule getStudyRule() {
        return studyRule;
    }

    public void setStudyRule(StudyRule studyRule) {
        this.studyRule = studyRule;
    }

    public List<MappingRule> getMappingRules() {
        return mappingRules;
    }

    public void setMappingRules(List<MappingRule> mappingRules) {
        this.mappingRules = new ArrayList<MappingRule>(mappingRules);
    }

    public FileStandard getFileStandard() {
        return fileStandard;
    }

    public void setFileStandard(FileStandard fileStandard) {
        this.fileStandard = fileStandard;
    }

    public MappingRule getMappingRule(Long id) {
        for (MappingRule rule : getMappingRules()) {
            if (rule.getId().equals(id)) {
                return rule;
            }

        }
        throw new IllegalArgumentException("Wrong ID");
    }

    public boolean isAcuityEnabled() {
        return acuityEnabled;
    }

    public void setAcuityEnabled(boolean acuityEnabled) {
        this.acuityEnabled = acuityEnabled;
    }

    public boolean isUpdateEnabled() {
        return updateEnabled;
    }

    public void setUpdateEnabled(boolean updateEnabled) {
        this.updateEnabled = updateEnabled;
    }

    public Long getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(Long filetypeId) {
        this.fileTypeId = filetypeId;
    }

    public boolean validate() {
        for (MappingRule mapping : mappingRules) {
            if (!mapping.validate()) {
                return false;
            }
        }
        return true;

    }

    /**
     * Actually file rule has only one description
     * @return
     */
    public Long getDescriptionId() {
        return descriptions.get(0).getId();
    }

}
