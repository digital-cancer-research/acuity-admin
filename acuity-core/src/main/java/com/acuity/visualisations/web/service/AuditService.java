package com.acuity.visualisations.web.service;

import java.util.List;

import com.acuity.visualisations.mapping.AuditAction;
import com.acuity.visualisations.mapping.dao.IAuditDao;
import com.acuity.visualisations.mapping.entity.Audit;
import com.acuity.visualisations.mapping.entity.AuditEntity;
import com.acuity.visualisations.web.auth.UserInfoHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by knml167 on 20/06/2014.
 */
@Service
public class AuditService {
    @Autowired
    private IAuditDao auditDao;

    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void logAction(AuditAction action, AuditEntity entity, Long objectId, String objectName, String comment) {
        String user = UserInfoHolder.getUserInfo().getDisplayName() + '/' + UserInfoHolder.getUserInfo().getName();
        auditDao.logAction(user, action, entity, objectId, objectName, comment);
    }

    @Transactional(readOnly = false, rollbackFor = Throwable.class)
    public void logAction(AuditAction action, AuditEntity entity, String objectName, Long objectId) {
        String user = UserInfoHolder.getUserInfo().getDisplayName();
        auditDao.logAction(user, action, entity, objectId, objectName, null);
    }

    public List<Audit> searchAction(int offset, int limit, Audit audit) {
        return auditDao.searchActions(offset, limit, audit);
    }

    public int getActionCount(Audit audit) {
        return auditDao.getTotalActions(audit);
    }
}
