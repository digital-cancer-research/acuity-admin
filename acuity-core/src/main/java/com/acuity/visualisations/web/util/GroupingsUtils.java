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

package com.acuity.visualisations.web.util;

import au.com.bytecode.opencsv.CSVReader;
import com.acuity.visualisations.data.provider.DataProvider;
import com.acuity.visualisations.mapping.entity.GroupRuleBase;
import com.acuity.visualisations.mapping.entity.GroupValueBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;

public final class GroupingsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupingsUtils.class);

    private GroupingsUtils() {
    }

    public static String readGroupingValues(GroupRuleBase group, InputStream stream) {

        try (CSVReader reader = new CSVReader(new InputStreamReader(stream))) {
            String[] nextLine;
            if (group.isHeaderRow()) {
                reader.readNext();
            }

            Set<GroupValueBase> namedValues = new LinkedHashSet<>();

            while ((nextLine = reader.readNext()) != null) {
                GroupValueBase val = group.getValueBaseInstance();
                val.setGroupId(group.getId());
                val.setName(nextLine[0]);
                val.setValues(nextLine);
                namedValues.add(val);
            }

            group.getValues().addAll(namedValues);

            if (group.getType() != GroupRuleBase.ProjectGroupType.ae
                    && group.getValues().stream().map(GroupValueBase::getNameAndUniqueField).distinct().count()
                    != group.getValues().stream().map(GroupValueBase::getUniqueField).distinct().count()) {
                return Consts.MULTI_GROUP;
            }
        } catch (Exception e) {
            LOGGER.error("Error reading AE/Lab group from stream!", e);
            return Consts.BAD_FILE;
        }

        return Consts.EMPTY_STRING;
    }

    public static String readGroupingValuesFromSharedLocation(GroupRuleBase group, String path, DataProvider provider) {
        InputStream source = provider.get(path).stream();
        String message = readGroupingValues(group, source);
        if (StringUtils.hasText(message)) {
            return message;
        }
        return Consts.EMPTY_STRING;
    }

}
