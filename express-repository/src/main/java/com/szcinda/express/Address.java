package com.szcinda.express;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@Data
@Embeddable
public class Address implements Serializable {
    private String district;
    private String province;
    private String city;
    private String street;
    private String fullAddress;
    private String contactMan;
    private String contactPhone;
    private String company;//单位

    public Address() {
    }

    public Address(String district, String province, String city, String street, String fullAddress, String contactMan, String contactPhone, String company) {
        this.district = district;
        this.province = province;
        this.city = city;
        this.street = street;
        this.fullAddress = fullAddress;
        this.contactMan = contactMan;
        this.contactPhone = contactPhone;
        this.company = company;
    }
}
