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

package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.mapping.dao.ICommonStaticDao;
import com.acuity.visualisations.mapping.entity.EntityRule;
import com.acuity.visualisations.mapping.entity.FieldDescription;
import com.acuity.visualisations.mapping.entity.FieldRule;
import com.acuity.visualisations.mapping.entity.FileDescription;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommonStaticDao extends ACUITYDaoSupport implements ICommonStaticDao {

	public List<EntityRule> getStaticData() {
		return getJdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(getSelectQuery());
            return ps;
        }, (ResultSet rs) -> {
            Map<EntityRule, EntityRule> entityRules = new HashMap<>();
            while (rs.next()) {
                String entityName = rs.getString("MEN_NAME");
                EntityRule entityRule = new EntityRule();
                entityRule.setId(rs.getLong("MEN_ID"));
                entityRule.setName(entityName);
                entityRule.setStoreInCache(rs.getBoolean("MEN_STORE_IN_CACHE"));
                entityRule.setGlobalFunction(rs.getString("MEN_GLOBAL_FUNCTION"));
                if (!entityRules.containsKey(entityRule)) {
                    entityRules.put(entityRule, entityRule);
                }
                entityRule = entityRules.get(entityRule);
                FileDescription fileDescription = new FileDescription();
                fileDescription.setDescription(rs.getString("MFD_NAME"));
                fileDescription.setId(rs.getLong("MFD_ID"));
                entityRule.getDescriptions().put(fileDescription, fileDescription);
                FieldRule fieldRule = new FieldRule();
                fieldRule.setId(rs.getLong("MFI_ID"));
                fieldRule.setName(rs.getString("MFI_NAME"));
                fieldRule.setType(rs.getString("MFI_TYPE"));
                FieldDescription fieldDescription = new FieldDescription();
                fieldDescription.setId(rs.getLong("MFID_ID"));
                fieldDescription.setText(rs.getString("MFID_TEXT"));
                fieldRule.setDescription(fieldDescription);
                fieldRule.setOrder(rs.getBigDecimal("MFI_ORDER"));
                fieldRule.setEntityProcessOrder(rs.getInt("MDE_PROCESS_ORDER"));
                entityRule.getFieldRules().add(fieldRule);
            }
            return new ArrayList<>(entityRules.values());
        });
	}

	protected String getSelectQuery() {
		return "select * from map_file_description, map_description_entity, map_entity, map_field, map_field_description "
				+ "where map_file_description.mfd_id = map_description_entity.mde_mfd_id "
				+ "and map_entity.men_id = map_description_entity.mde_men_id " + "and map_entity.men_id = map_field.mfi_men_id "
				+ "and map_field.mfi_mfid_id = map_field_description.mfid_id ORDER BY map_field.MFI_ORDER";
	}

	public List<FieldRule> getFieldsWithFileId() {
		return getJdbcTemplate()
				.query("select * from MAP_FIELD "
                                + "left join MAP_FIELD_DESCRIPTION on MFI_MFID_ID = MFID_ID "
                                + "inner join MAP_DESCRIPTION_ENTITY on MFI_MEN_ID = MDE_MEN_ID "
                                + "inner join MAP_FILE_DESCRIPTION on MDE_MFD_ID = MFD_ID",
						(rs, rowNum) -> {
                            FieldRule fieldRule = new FieldRule();
                            fieldRule.setId(rs.getLong("MFI_ID"));
                            fieldRule.setName(rs.getString("MFI_NAME"));
                            fieldRule.setType(rs.getString("MFI_TYPE"));
                            fieldRule.setEntityRuleId(rs.getString("MFI_MEN_ID"));
                            fieldRule.setOrder(rs.getBigDecimal("MFI_ORDER"));
                            fieldRule.setEntityProcessOrder(rs.getInt("MDE_PROCESS_ORDER"));
                            fieldRule.setDescription(new FieldDescription(rs.getString("MFID_TEXT"), rs.getLong("MFID_ID")));
                            fieldRule.setFileDescriptionId(rs.getLong("MFD_ID"));
                            fieldRule.setMandatory(rs.getInt("MFI_MANDATORY") == 1);
                            return fieldRule;
                        });
	}

	public List<Map.Entry<Long, Long>> getEntityDescription() {
		return getJdbcTemplate().query("select MDE_MEN_ID, MDE_MFD_ID from MAP_DESCRIPTION_ENTITY",
                (rs, rowNum) -> new AbstractMap.SimpleEntry<>(rs.getLong("MDE_MEN_ID"), rs.getLong("MDE_MFD_ID")));
	}
}
