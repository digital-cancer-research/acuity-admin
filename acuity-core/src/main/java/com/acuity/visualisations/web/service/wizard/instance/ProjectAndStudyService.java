package com.acuity.visualisations.web.service.wizard.instance;

import com.acuity.visualisations.mapping.dao.IProjectRuleDao;
import com.acuity.visualisations.mapping.dao.IStudyRuleDao;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class ProjectAndStudyService {

    @Autowired
    private IProjectRuleDao projectRuleDao;

    @Autowired
    private IStudyRuleDao studyRuleDao;

    public List<ProjectRule> searchProject(String query) {
        return projectRuleDao.search("%" + query + "%");
    }

    public List<StudyRule> searchStudy(ProjectRule project) {
        return studyRuleDao.getByProjectId(project.getId());
    }

    public List<StudyRule> searchStudiesByProjectId(Long id) {
        return studyRuleDao.getByProjectId(id);
    }

    /**
     * Load main information in a single place
     *
     * @return
     */
    public List<ProjectRule> getAllProjectRulesWithStudyRules() {
        List<ProjectRule> projectRules = projectRuleDao.getAllProjectRules();
        List<StudyRule> studyRules = studyRuleDao.getAllStudyRules();

        for (ProjectRule projectRule : projectRules) {
            projectRule.setStudyRules(new ArrayList<>());

            for (StudyRule studyRule : studyRules) {
                if (studyRule.getProjectId().equals(projectRule.getId())) {
                    projectRule.getStudyRules().add(studyRule);
                }
            }
        }
        return projectRules;
    }
}
