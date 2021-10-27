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

package com.acuity.visualisations.sdtm.entity;

public class SdtmKey {
    private final String studyId;
    private final String subjectId;

    public SdtmKey(String subjectId) {
        this.subjectId = subjectId;
        this.studyId = null;
    }

    public SdtmKey(String studyId, String subjectId) {
        this.studyId = studyId;
        this.subjectId = subjectId;
    }

    public String getStudyId() {
        return studyId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SdtmKey)) {
            return false;
        }

        SdtmKey sdtmKey = (SdtmKey) o;

        return subjectId.equals(sdtmKey.subjectId);

    }

    @Override
    public int hashCode() {
        return subjectId.hashCode();
    }
}
