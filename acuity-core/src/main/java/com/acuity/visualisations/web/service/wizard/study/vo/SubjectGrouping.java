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

package com.acuity.visualisations.web.service.wizard.study.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("SubjectGrouping")
public class SubjectGrouping {
    private Long id;
    private String name;
    private SubjectGroupingType type = SubjectGroupingType.NONE;
    @Getter
    @Setter
    private List<SubjectGroup> groups = new ArrayList<>();

    public SubjectGrouping(String name) {
        this.name = name;
    }

    public SubjectGrouping(Long id, String name, SubjectGroupingType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.groups = new ArrayList<>();
    }

}

