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
