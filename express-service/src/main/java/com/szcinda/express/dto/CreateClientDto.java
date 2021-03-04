package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateClientDto implements Serializable {
    private String name;
    private String nickName;
    private String email;
}
