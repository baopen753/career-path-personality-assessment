package org.swd392.universities.entity;

import java.security.Principal;

public class UserPrincipal implements Principal {

    private String userId;
    private String userRole;

    public UserPrincipal(String userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole;
    }

    @Override
    public String getName() {
        return userRole;
    }
}
