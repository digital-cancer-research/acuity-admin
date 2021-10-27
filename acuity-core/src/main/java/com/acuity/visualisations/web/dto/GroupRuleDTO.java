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

package com.acuity.visualisations.web.dto;


import com.acuity.visualisations.mapping.entity.GroupRuleBase;

public class GroupRuleDTO {
    private GroupRuleBase group;
    private String message;

    public GroupRuleDTO(GroupRuleBase group, String message) {
        this.group = group;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GroupRuleBase getGroup() {
        return group;
    }

    public void setGroup(GroupRuleBase group) {
        this.group = group;
    }


}
