package com.acuity.visualisations.mapping.dao.impl;

import com.acuity.visualisations.dal.ACUITYDaoSupport;
import com.acuity.visualisations.mapping.dao.IDynamicFieldRuleDao;
import com.acuity.visualisations.mapping.entity.FieldDescription;
import com.acuity.visualisations.mapping.entity.FieldRule;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class DynamicFieldRuleDao extends ACUITYDaoSupport implements IDynamicFieldRuleDao {

    @Override
    public FieldRule insertDynamicField(long mappingRuleId, String name) {
        getJdbcTemplate().update("insert into MAP_DYNAMIC_FIELD(MDFI_NAME, MDFI_MMR_ID) VALUES(?, ?)", name, mappingRuleId);
        return getDynamicFieldByMappingRuleId(mappingRuleId);
    }

    @Override
    public FieldRule getDynamicFieldByMappingRuleId(long mappingRuleId) {
        List<FieldRule> fieldRules = getJdbcTemplate().query("select * from MAP_DYNAMIC_FIELD where MDFI_MMR_ID = ?", ROW_MAPPER, mappingRuleId);
        if (fieldRules.isEmpty()) {
            return null;
        }
        return fieldRules.get(0);
    }

    @Override
    public void deleteDynamicField(long mappingRuleId) {
        getJdbcTemplate().update("delete from MAP_DYNAMIC_FIELD where MDFI_MMR_ID = ?", mappingRuleId);
    }

    @Override
    public List<FieldRule> selectAll() {
        return getJdbcTemplate().query("select * from MAP_DYNAMIC_FIELD", ROW_MAPPER);
    }

    private static final RowMapper<FieldRule> ROW_MAPPER = (rs, rowNum) -> {
        FieldRule fieldRule = new FieldRule();
        fieldRule.setId(rs.getLong("MDFI_ID"));
        fieldRule.setName(rs.getString("MDFI_NAME"));
        fieldRule.setType("String");
        fieldRule.setMandatory(false);
        FieldDescription fieldDescription = new FieldDescription();
        fieldDescription.setText(rs.getString("MDFI_NAME"));
        fieldDescription.setId(rs.getLong("MDFI_ID"));
        fieldRule.setDescription(fieldDescription);
        fieldRule.setEntityProcessOrder(999);
        fieldRule.setOrder(new BigDecimal(999));
        fieldRule.setDynamic(true);
        return fieldRule;
    };
}
