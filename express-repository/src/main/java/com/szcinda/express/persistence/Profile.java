package com.szcinda.express.persistence;

import com.szcinda.express.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Profile extends BaseEntity {
    @NotNull(message = "名称不能为空")
    private String name;
    @NotNull(message = "行政区域不能为空")
    private String district;
    private String province;
    private String city;
    private String street;
    private String fullAddress;
    private String contactMan;
    private String contactPhone;
    private String company;
}
