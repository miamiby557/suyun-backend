package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProfileUpdateDto implements Serializable {
    private String id;
    private String name;
    private String district;
    private String province;
    private String city;
    private String street;
    private String fullAddress;
    private String contactMan;
    private String contactPhone;
    private String company;
}
