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

package com.acuity.visualisations.mapping.entity;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
public class StudySubjectGrouping {
    private long studyId;

    private Map<String, List<SubjectGrouping>> groupingsType = new HashMap<>();

    public long getStudyId() {
        return studyId;
    }

    public void setStudyId(long studyId) {
        this.studyId = studyId;
    }

    public Map<String, List<SubjectGrouping>> getGroupingsType() {
        return groupingsType;
    }

    public void setGroupingsTypes(Map<String, List<SubjectGrouping>> groupingsType) {
        this.groupingsType = groupingsType;
    }

    public void addGroupings(List<SubjectGrouping> groupings) {
        groupings.stream()
                .peek((g) -> {
                    if (Objects.isNull(g.getType())) {
                        g.setType("NONE");
                    }
                })
                .collect(Collectors.groupingBy(SubjectGrouping::getType))
                .forEach((type, list) -> groupingsType.merge(type, list, (l1, l2) ->
                        Stream.concat(l1.stream(), l2.stream()).collect(Collectors.toList())
                ));
    }

    public Optional<SubjectGrouping> getByType(String type) {
        return groupingsType.get(type).stream().filter(Objects::nonNull).findAny();
    }
}
