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

package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.mapping.entity.FileSection;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.ProjectRule;
import com.acuity.visualisations.mapping.entity.StudyRule;
import com.acuity.visualisations.web.workflow.ClinicalStudyWorkflow;
import com.acuity.visualisations.web.workflow.DrugProgramWorkflow;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ControllerUtils {

    public static final String SESSION_ATTR_PROGRAMME_WORKFLOW = "programme_workflow";
    public static final String SESSION_ATTR_STUDY_WORKFLOW = "study_workflow";
    public static final String SESSION_ATTR_PROGRAMME_SEARCH_RESULT = "programmes";
    public static final String SESSION_ATTR_FILE_SECTIONS = "fileSections";
    public static final String SESSION_ATTR_STUDIES_SEARCH_RESULT = "studies";
    public static final String SESSION_ATTR_PROGRAMME_GROUP = "programme-group";
    public static final String SESSION_ATTR_PROGRAMMES_COUNT = "programmes-count";
    public static final String SESSION_ATTR_STUDIES_COUNT = "studies-count";
    public static final String SESSION_ATTR_ACUITY_PROGRAMME_RESULTS = "acuity-results";
    public static final String SESSION_ATTR_CDBP_PROGRAMME_RESULTS = "cdbp-results";

    public static final String SESSION_ATTR_ACUITY_STUDY_RESULTS = "acuity-study-results";
    public static final String SESSION_ATTR_CDBP_STUDY_RESULTS = "cdbp-study-results";

    public static final String SESSION_ATTR_SEARCH_QUERY_ID = "queryId";
    private static final Supplier<ClinicalStudyWorkflow> EMPTY_STUDY_WORKFLOW = ClinicalStudyWorkflow::new;
    private static final Supplier<DrugProgramWorkflow> EMPTY_DRUG_PROGRAM_WORKFLOW = DrugProgramWorkflow::new;

    private ControllerUtils() {
    }

    public static DrugProgramWorkflow getProgrammeWorkflow(HttpSession session, String browserTabId) {
        Object attribute = session.getAttribute(SESSION_ATTR_PROGRAMME_WORKFLOW);
        if (attribute == null) {
            return EMPTY_DRUG_PROGRAM_WORKFLOW.get();
        } else {
            return ((Map<String, DrugProgramWorkflow>) attribute).getOrDefault(browserTabId, EMPTY_DRUG_PROGRAM_WORKFLOW.get());
        }
    }

    public static ClinicalStudyWorkflow getClinicalStudyWorkflow(HttpSession session, String browserTabId) {
        Object attribute = session.getAttribute(SESSION_ATTR_STUDY_WORKFLOW);
        if (attribute == null) {
            return EMPTY_STUDY_WORKFLOW.get();
        } else {
            return ((Map<String, ClinicalStudyWorkflow>) attribute).getOrDefault(browserTabId, EMPTY_STUDY_WORKFLOW.get());
        }
    }

    public static void clearProgrammeWorkflow(HttpSession session, String browserTabId) {
        Object attribute = session.getAttribute(SESSION_ATTR_PROGRAMME_WORKFLOW);
        if (attribute == null) {
            Map<String, DrugProgramWorkflow> studyWorkflowMap = new HashMap<>();
            studyWorkflowMap.put(browserTabId, EMPTY_DRUG_PROGRAM_WORKFLOW.get());
            session.setAttribute(SESSION_ATTR_PROGRAMME_WORKFLOW, studyWorkflowMap);
        } else {
            ((Map<String, DrugProgramWorkflow>) attribute).put(browserTabId, EMPTY_DRUG_PROGRAM_WORKFLOW.get());
            session.setAttribute(SESSION_ATTR_PROGRAMME_WORKFLOW, attribute);
        }
    }

    public static void clearClinicalStudyWorkflow(HttpSession session, String browserTabId) {
        Object attribute = session.getAttribute(SESSION_ATTR_STUDY_WORKFLOW);
        if (attribute == null) {
            Map<String, ClinicalStudyWorkflow> studyWorkflowMap = new HashMap<>();
            studyWorkflowMap.put(browserTabId, EMPTY_STUDY_WORKFLOW.get());
            session.setAttribute(SESSION_ATTR_STUDY_WORKFLOW, studyWorkflowMap);
        } else {
            ((Map<String, ClinicalStudyWorkflow>) attribute).put(browserTabId, EMPTY_STUDY_WORKFLOW.get());
            session.setAttribute(SESSION_ATTR_STUDY_WORKFLOW, attribute);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, ProjectRule> getProgrammesSearchResult(HttpSession session) {
        return (Map<String, ProjectRule>) session.getAttribute(SESSION_ATTR_PROGRAMME_SEARCH_RESULT);
    }

    public static void setProgrammesSearchResult(final HttpSession session, Map<String, ProjectRule> programmes) {
        session.setAttribute(SESSION_ATTR_PROGRAMME_SEARCH_RESULT, programmes);
    }

    public static void setFileSections(HttpSession session, List<FileSection> fileSections) {
        session.setAttribute(SESSION_ATTR_FILE_SECTIONS, fileSections);
    }

    public static List<FileSection> getFileSections(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<FileSection> result = (List<FileSection>) session.getAttribute(SESSION_ATTR_FILE_SECTIONS);
        return result;
    }

    public static void setStudySearchResult(HttpSession session, Map<String, StudyRule> studies) {
        session.setAttribute(SESSION_ATTR_STUDIES_SEARCH_RESULT, studies);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, StudyRule> getStudySearchResult(HttpSession session) {
        return (Map<String, StudyRule>) session.getAttribute(SESSION_ATTR_STUDIES_SEARCH_RESULT);
    }

    public static GroupRuleBase getProgrammesEditGroup(HttpSession session) {
        return (GroupRuleBase) session.getAttribute(SESSION_ATTR_PROGRAMME_GROUP);
    }

    public static void setProgrammesEditGroup(final HttpSession session, GroupRuleBase group) {
        session.setAttribute(SESSION_ATTR_PROGRAMME_GROUP, group);
    }

    public static Integer getTotalProgrammesCount(HttpSession session) {
        return (Integer) session.getAttribute(SESSION_ATTR_PROGRAMMES_COUNT);
    }

    public static void setTotalProgrammesCount(final HttpSession session, Integer value) {
        session.setAttribute(SESSION_ATTR_PROGRAMMES_COUNT, value);
    }

    public static void decreaseTotalProgrammesCount(final HttpSession session) {
        Integer value = getTotalProgrammesCount(session);
        if (value != null && value > 0) {
            value--;
            setTotalProgrammesCount(session, value);
        }

    }

    public static void increaseTotalProgrammesCount(final HttpSession session) {
        Integer value = getTotalProgrammesCount(session);
        if (value != null) {
            value++;
            setTotalProgrammesCount(session, value);
        }
    }

    public static void decreaseTotalStudiesCount(final HttpSession session) {
        Integer value = getTotalStudiesCount(session);
        if (value != null && value > 0) {
            value--;
            setTotalStudiesCount(session, value);
        }

    }

    public static void increaseTotalStudiesCount(final HttpSession session) {
        Integer value = getTotalStudiesCount(session);
        if (value != null) {
            value++;
            setTotalStudiesCount(session, value);
        }
    }

    public static Integer getTotalStudiesCount(HttpSession session) {
        return (Integer) session.getAttribute(SESSION_ATTR_STUDIES_COUNT);
    }

    public static void setTotalStudiesCount(final HttpSession session, Integer value) {
        session.setAttribute(SESSION_ATTR_STUDIES_COUNT, value);
    }

    public static String getStackTraceString(Throwable exception) {
        if (exception != null && exception.getStackTrace() != null) {
            StringWriter sw;
            PrintWriter pw = null;
            try {
                sw = new StringWriter();
                pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                String trace = sw.toString();
                return escapeHtmlTags(trace);
            } catch (Exception ignore) {

            } finally {
                pw.close();
            }
        }
        return "";
    }

    public static void setQueryId(HttpSession session, String queryId) {
        session.setAttribute(SESSION_ATTR_SEARCH_QUERY_ID, queryId);
    }

    public static String getQueryId(HttpSession session) {
        return (String) session.getAttribute(SESSION_ATTR_SEARCH_QUERY_ID);
    }


    public static void setACUITYResults(HttpSession session, List<ProjectRule> acuityResults) {
        session.setAttribute(SESSION_ATTR_ACUITY_PROGRAMME_RESULTS, acuityResults);
    }

    @SuppressWarnings("unchecked")
    public static List<ProjectRule> getACUITYResults(HttpSession session) {
        return (List<ProjectRule>) session.getAttribute(SESSION_ATTR_ACUITY_PROGRAMME_RESULTS);
    }

    public static void setACUITYStudyResults(HttpSession session, List<StudyRule> acuityResults) {
        session.setAttribute(SESSION_ATTR_ACUITY_STUDY_RESULTS, acuityResults);
    }

    @SuppressWarnings("unchecked")
    public static List<StudyRule> getACUITYStudyResults(HttpSession session) {
        return (List<StudyRule>) session.getAttribute(SESSION_ATTR_ACUITY_STUDY_RESULTS);
    }

    public static void setCDBPResults(HttpSession session, List<ProjectRule> cdbpResults) {
        session.setAttribute(SESSION_ATTR_CDBP_PROGRAMME_RESULTS, cdbpResults);
    }

    @SuppressWarnings("unchecked")
    public static List<ProjectRule> getCDBPResults(HttpSession session) {
        return (List<ProjectRule>) session.getAttribute(SESSION_ATTR_CDBP_PROGRAMME_RESULTS);
    }

    public static void setCDBPStudyResults(HttpSession session, List<StudyRule> cdbpResults) {
        session.setAttribute(SESSION_ATTR_CDBP_STUDY_RESULTS, cdbpResults);
    }

    @SuppressWarnings("unchecked")
    public static List<StudyRule> getCDBPStudyResults(HttpSession session) {
        return (List<StudyRule>) session.getAttribute(SESSION_ATTR_CDBP_STUDY_RESULTS);
    }

    public static String escapeHtmlTags(String s) {
        return StringUtils.replaceEach(s, new String[]{"<", ">"}, new String[]{"&lt;", "&gt;"});
    }
}
