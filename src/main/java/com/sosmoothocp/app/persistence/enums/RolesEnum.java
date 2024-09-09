package com.sosmoothocp.app.persistence.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public enum RolesEnum {
    USER("User"),
    ADMIN("Admin");

    private final String role;

    RolesEnum(String role){
        this.role = role;
    }

    public static RolesEnum fromRole(String userRole){
        for (RolesEnum roleEnum : RolesEnum.values()){
            if (roleEnum.getRole().equalsIgnoreCase(userRole)){
                return roleEnum;
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("No such role (%s) exists within the system", userRole));
    }

}

