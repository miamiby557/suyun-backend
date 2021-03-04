package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FeeDeclareCreateDto implements Serializable {
    private String clientName;
    private String deliveryNo;
    private Double inCome;//应收
    private String cindaNo;//先达单号
    private String transportChannel;
    private String feeItem;
    private double money;
    private String remark;//备注
    private String person;
}
