package com.acuity.visualisations.web.auth;

import java.io.Serializable;
import java.security.Principal;

public class PrincipalInfo implements Principal, Serializable {

    private static final long serialVersionUID = 310L;

    private String userName;
    private String displayName;

    public PrincipalInfo(String userName, String displayName) {
        this.userName = userName;
        this.displayName = displayName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getName() {
        return userName;
    }

    @Override
    public String toString() {
        return userName;
    }
}
