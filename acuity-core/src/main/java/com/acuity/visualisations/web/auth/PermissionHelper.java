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
