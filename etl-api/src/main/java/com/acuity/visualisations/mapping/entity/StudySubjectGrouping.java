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
