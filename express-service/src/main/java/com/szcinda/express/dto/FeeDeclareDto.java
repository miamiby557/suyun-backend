package com.szcinda.express.dto;

import com.szcinda.express.Address;
import com.szcinda.express.FeeDeclareStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class FeeDeclareDto implements Serializable {
    private String id;
    private String clientName;
    private String deliveryNo;
    private Address from;//始发地址
    private Address to;//收货地址
    private String vehicleType;//车型 4.2/7.6/9.6/13.5/16.5/17.5
    private Double inCome;
    private String cindaNo;//先达单号
    private String province;
    private String city;
    private String receiveMan;
    private String transportChannel;
    private String feeItem;//费用项目
    private Double money;//金额
    private FeeDeclareStatus status;//申报状态
    private String remark;//备注
    private String rejectReason;//驳回原因
    private String person;
}
