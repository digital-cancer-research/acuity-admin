package com.acuity.visualisations.web.auth;


import com.acuity.va.security.acl.domain.AcuitySidDetails;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class UserInfoHolder {

    private UserInfoHolder() {
    }

    public static PrincipalInfo getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication object not found");
        }
        PrincipalInfo info;
        if (auth instanceof PrincipalInfo) {
            info = (PrincipalInfo) auth;
        } else if (auth instanceof AcuitySidDetails) {
            AcuitySidDetails user = (AcuitySidDetails) auth;
            info = new PrincipalInfo(user.getSidAsString(), user.getFullName());
        } else {
            throw new AuthenticationCredentialsNotFoundException("Authentication object is of unknown type");
        }
        return info;
    }

    public static boolean isCurrentUserDeveloper() {
        return isUserDeveloper(SecurityContextHolder.getContext().getAuthentication());
    }

    public static boolean isUserDeveloper(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication object not found");
        }
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("DEVELOPMENT_TEAM"));
    }

}
