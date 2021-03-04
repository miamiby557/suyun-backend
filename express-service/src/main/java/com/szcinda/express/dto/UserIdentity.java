package com.szcinda.express.dto;

import com.szcinda.express.RoleType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class UserIdentity implements Serializable {
    private String id;
    private String account;
    private String password;
    private String token;
    private RoleType role;
    private List<String> permissions = new ArrayList<>();

    public UserIdentity(String id, String account, String password, RoleType role) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.role = role;
    }

    public void setAdminPermissions() {
        this.permissions.addAll(Arrays.asList("feeApproval","financialReport","user"));
    }
}
