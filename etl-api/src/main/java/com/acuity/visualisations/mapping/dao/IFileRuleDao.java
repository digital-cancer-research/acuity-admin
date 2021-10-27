package com.acuity.visualisations.mapping.dao;

import java.util.List;

import com.acuity.visualisations.mapping.entity.FileRule;

public interface IFileRuleDao extends IBasicDynamicEntityDao<FileRule> {

	List<FileRule> getFileRulesByStudy(long studyId);

	void delete(FileRule fileRule);

    FileRule getFileRule(long fileRuleId);

    List<String> getFileRuleNamesByFileDescriptionId(long fileDescriptionId);
}
