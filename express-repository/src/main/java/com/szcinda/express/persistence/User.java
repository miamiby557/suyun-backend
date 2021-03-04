package com.szcinda.express.persistence;

import com.szcinda.express.BaseEntity;
import com.szcinda.express.RoleType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class User extends BaseEntity {
    @NotNull(message = "登录账号不能为空")
    private String account;
    @NotNull(message = "密码不能为空")
    private String password;
    @NotNull(message = "名称不能为空")
    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "角色不能为空")
    private RoleType role;
}
