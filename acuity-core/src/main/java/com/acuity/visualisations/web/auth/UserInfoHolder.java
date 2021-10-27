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
