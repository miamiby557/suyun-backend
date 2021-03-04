package com.szcinda.express.controller.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProfileByNameDto implements Serializable {
    private String name;
}
