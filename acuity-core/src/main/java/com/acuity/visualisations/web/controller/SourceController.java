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

import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.mapping.entity.ColumnRule;
import com.acuity.visualisations.mapping.entity.FieldRule;
import com.acuity.visualisations.mapping.entity.FileRule;
import com.acuity.visualisations.mapping.entity.MappingRule;
import com.acuity.visualisations.web.dto.PrimitiveObjWrapper;
import com.acuity.visualisations.web.service.SourceService;
import com.acuity.visualisations.web.workflow.ClinicalStudyWorkflow;
import com.epam.parso.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/api/source")
public class SourceController {
    private static final Long CNTLIN_FILE_DESCRIPTION_ID = 18L;
    private static final String CNTLIN_ENTITY_ID = "22";
    private static final String CNTLIN_NAME_FIELD = "Code group name";
    private static final String TAB_ID = "TabId";

    @Autowired
    private DataProvider provider;

    @Autowired
    private SourceService sourceService;

    @RequestMapping("/check_directory_exists")
    @ResponseBody
    public PrimitiveObjWrapper<Boolean> checkDirectoryExists(@RequestParam(value = "path") String path) {
        try {
            return new PrimitiveObjWrapper<>(provider.get(path).isDirectory());
        } catch (Exception e) {
            return new PrimitiveObjWrapper<>(Boolean.FALSE);
        }
    }

    @RequestMapping("/check_file_exists")
    @ResponseBody
    public PrimitiveObjWrapper<Boolean> checkFileExists(@RequestParam(value = "path") String path) {
        try {
            return new PrimitiveObjWrapper<>(provider.get(path).exists());
        } catch (Exception e) {
            return new PrimitiveObjWrapper<>(Boolean.FALSE);
        }
    }

    @RequestMapping("/get_column_names")
    @ResponseBody
    public List<String> getColumnNames(@RequestParam(value = "path") String path) {
        try {
            return sourceService.getColumnNames(path);
        } catch (Exception e) {
            return null;
        }
    }

    @RequestMapping("/get_column_metadata")
    @ResponseBody
    public Map<String, Column> getColumnMetadata(@RequestParam(value = "path") String path) {
        try {
            return sourceService.getColumnMetadata(path);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Return list of keys from cntlin files
     * @param request
     * @return
     */
    @RequestMapping("/get_decoding_values")
    @ResponseBody
    public Collection<String> getDecodingValues(HttpServletRequest request) {
        String browserTabId = WebUtils.getCookie(request, TAB_ID).getValue();
        ClinicalStudyWorkflow workflow = ControllerUtils.getClinicalStudyWorkflow(request.getSession(), browserTabId);
        if (workflow == null) {
            return Collections.emptyList();
        }
        List<FileRule> fileRules = workflow.getSelectedStudy().getFileRules();

        Set<String> values = new LinkedHashSet<>();

        for (FileRule fileRule : fileRules) {
            if (!StringUtils.isEmpty(fileRule.getName())
                    && CNTLIN_FILE_DESCRIPTION_ID.equals(fileRule.getDescriptionId())) {
                for (MappingRule mappingRule : fileRule.getMappingRules()) {
                    for (FieldRule fieldRule : mappingRule.getFieldRules()) {
                        if (!StringUtils.isEmpty(fieldRule.getName())
                                && CNTLIN_ENTITY_ID.equals(fieldRule.getEntityRuleId())
                                && CNTLIN_NAME_FIELD.equals(fieldRule.getDescription().getText())) {
                            List<ColumnRule> columnRules = mappingRule.getColumnRules();
                            if (!columnRules.isEmpty()) {
                                values.addAll(sourceService.getDecodingValues(fileRule.getName(), columnRules.get(0).getName()));
                            }
                        }
                    }
                }
            }
        }
        return values;
    }
}
