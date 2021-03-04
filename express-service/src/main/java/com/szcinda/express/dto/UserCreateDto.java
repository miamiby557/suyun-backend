package com.szcinda.express.dto;

import com.szcinda.express.RoleType;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserCreateDto implements Serializable {
    private String account;
    private String password;
    private String name;
    private String email;
    private RoleType role;
}
