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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.acuity.visualisations.mapping.dao.ICommonStaticDao;
import com.acuity.visualisations.web.auth.UserInfoHolder;
import com.acuity.visualisations.web.dto.EntityRuleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/data")
public class DataController {
    @Autowired
    private ICommonStaticDao dao;

    @RequestMapping("/get-current-user")
    @ResponseBody
    public Map<String, String> getCurrentUser() {
        Map<String, String> res = new HashMap<>();
        res.put("id", UserInfoHolder.getUserInfo().getName());
        res.put("value", UserInfoHolder.getUserInfo().getDisplayName());
        return res;
    }

    @RequestMapping("/get_entities")
    @ResponseBody
    public List<EntityRuleDTO> getEntities() {
        return dao.getStaticData()
                .stream()
                .filter(entityRule -> !"Test".equals(entityRule.getName()))
                .map(entityRule -> {
                    EntityRuleDTO entityRuleDTO = new EntityRuleDTO(entityRule.getId(), entityRule.getName());
                    entityRuleDTO.setFieldRules(entityRule.getFieldRules());
                    return entityRuleDTO;
                })
                .collect(Collectors.toList());
    }
}
