package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdatePasswordDto implements Serializable {
    private String userId;
    private String oldPassword;
    private String newPassword;
}
