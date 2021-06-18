package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FeeDeclareModifyDto implements Serializable {
    private String id;
    private String clientName;
    private String deliveryNo;
    private Double inCome;
    private double exceptionFee; // 异常费用
    private String transportChannel;
    private String feeItem;
    private double upstairsFee;//上楼费
    private double daofuProcedureFee;//倒付手续费
    private double terminalFee;//末端费用
    private double money;//费用
    private String remark;//备注
}
