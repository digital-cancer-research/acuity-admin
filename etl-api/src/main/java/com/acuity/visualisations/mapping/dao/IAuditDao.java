package com.acuity.visualisations.mapping.dao;

import com.acuity.visualisations.mapping.AuditAction;
import com.acuity.visualisations.mapping.entity.Audit;
import com.acuity.visualisations.mapping.entity.AuditEntity;

import java.util.List;

/**
 * Created by knml167 on 20/06/2014.
 */
public interface IAuditDao {
    void logAction(String user, AuditAction action, AuditEntity entity, Long objectId, String objectName, String comment);

    List<Audit> getActions(int limit, int offset, Audit.Field sortBy, boolean sortReverse);

    List<Audit> searchActions(int offset, int limit, Audit audit);

    int getTotalActions();

    int getTotalActions(Audit audit);
}
