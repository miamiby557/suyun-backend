package com.szcinda.express.persistence;

import com.szcinda.express.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Carrier extends BaseEntity {
    private String name;
    private String nickName;
    private String email;
}
