package com.szcinda.express.dto;

import com.szcinda.express.FeeDeclareStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class FeeDeclareDto implements Serializable {
    private String id;
    private String clientName;
    private String deliveryNo;
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
