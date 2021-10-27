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
