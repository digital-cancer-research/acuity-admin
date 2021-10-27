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

import com.acuity.visualisations.mapping.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
    @Getter private Date timestamp;
    @Getter private AuditAction action;
    @Getter private Long resourceId;
    @Getter private String resourceName;
    @Getter private AuditEntity resourceType;
    @Getter private String comment;
    @Getter private String username;

    public enum Field {
        action("MAU_ACTION"),
        timestamp("MAU_TIMESTAMP"),
        resourceId("MAU_OBJECT_ID"),
        resourceName("MAU_OBJECT_NAME"),
        resourceType("MAU_ENTITY"),
        username("MAU_USER"),
        comment("MAU_COMMENT");

        private String column;

        Field(String column) {
            this.column = column;
        }

        public String getColumn() {
            return column;
        }
    }
}
