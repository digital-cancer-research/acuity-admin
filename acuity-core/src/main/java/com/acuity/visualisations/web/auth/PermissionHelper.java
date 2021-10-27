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

package com.acuity.visualisations.web.auth;

import com.acuity.va.security.acl.domain.AcuityObjectIdentity;
import com.acuity.va.security.acl.domain.AcuityObjectIdentityWithPermission;
import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.acuity.va.security.auth.common.ISecurityResourceClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component("permissionHelper")
public class PermissionHelper {

    @Autowired
    private ISecurityResourceClient securityClient;
    @Autowired
    private Environment environment;

    public boolean isGlobalAdmin(Authentication auth) {
        return isDeveloper(auth);
    }

    public boolean isCurrentUserDrugProgrammeAdmin(Long projectId) {
        return isDrugProgrammeAdmin(projectId, getCurrentAuth());
    }

    public boolean isDrugProgrammeAdmin(Long projectId, Authentication auth) {
        return environment.acceptsProfiles("local-no-security")
                || isGlobalAdmin(auth)
                || getUserIdentities(auth).stream()
                .filter(AcuityObjectIdentity::thisDrugProgrammeType)
                .anyMatch(d -> Objects.equals(d.getId(), projectId));
    }

    @SneakyThrows
    public List<AcuityObjectIdentityWithPermission> getUserIdentities(Authentication auth) {
        AcuitySidDetails user = (AcuitySidDetails) auth;
        return securityClient.getAclsForUser(user.getName());
    }

    public boolean canAccessSchedulerPage() {
        return isDeveloper();
    }

    public boolean canAccessServicePage() {
        return isDeveloper();
    }

    public boolean isDeveloper() {
        return UserInfoHolder.isCurrentUserDeveloper();
    }

    public boolean isDeveloper(Authentication auth) {
        return UserInfoHolder.isUserDeveloper(auth);
    }

    public boolean canViewUploadReports() {
        return isDeveloper();
    }

    private static Authentication getCurrentAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
