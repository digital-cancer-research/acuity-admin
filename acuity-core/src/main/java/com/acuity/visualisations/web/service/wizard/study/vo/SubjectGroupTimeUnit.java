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
