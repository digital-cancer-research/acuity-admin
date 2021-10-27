package com.acuity.visualisations.web.service.wizard.study.vo;

public enum SubjectGroupTimeUnit {
    None("-", "-"),
    Minute("minute", "minutes"),
    Hour("hour", "hours"),
    Day("day", "days"),
    Week("week", "weeks"),
    Year("year", "years"),
    Cycle("cycle", "cycles");

    private String single;
    private String plural;

    SubjectGroupTimeUnit(String single, String plural) {
        this.single = single;
        this.plural = plural;
    }

    public String toHumanString(int count) {
        return count == 1 ? single : plural;
    }
}
