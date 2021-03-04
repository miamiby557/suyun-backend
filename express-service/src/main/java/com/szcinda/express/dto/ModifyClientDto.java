package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ModifyClientDto implements Serializable {
    private String id;
    private String nickName;
    private String email;
}
