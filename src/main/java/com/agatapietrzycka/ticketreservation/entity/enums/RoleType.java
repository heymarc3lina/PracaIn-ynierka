package com.agatapietrzycka.ticketreservation.entity.enums;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum RoleType {
    ADMIN,
    MANAGER,
    USER;

    public Set<String> getRolesAndPermissions() {
        Set<String> rolesAndPermissions = new HashSet<>(getPermissions());
        rolesAndPermissions.add(getRole());
        return rolesAndPermissions;
    }

    private String getRole() {
        return "ROLE_" + this.name().toUpperCase();
    }

    private Set<String> getPermissions() {
        return Collections.emptySet();
    }


}
