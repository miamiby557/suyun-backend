package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateDto implements Serializable {
    private String userId;
    private String name;
    private String email;
}
