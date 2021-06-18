package com.szcinda.express.dto;

import com.szcinda.express.Address;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FeeDeclareCreateDto implements Serializable {
    private String clientName;
    private String deliveryNo;// 发货单号
    private LocalDateTime deliveryDate;//发货日期
    private Address from;//始发地址
    private Address to;//收货地址
    private String vehicleType;//车型 4.2/7.6/9.6/13.5/16.5/17.5
    private double inCome;//应收
    private double exceptionFee; // 异常费用
    private String cindaNo;//先达单号
    private String transportChannel;
    private String feeItem;
    private double money;
    private String remark;//备注
    private String person;
}
