package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderTrackingDto implements Serializable {
    private String transportNo;
    private String operation;

    private String operateAddress;
    private String terminalAddress;

    private String operator;

    private String operateTime;
    private String remark;
}
