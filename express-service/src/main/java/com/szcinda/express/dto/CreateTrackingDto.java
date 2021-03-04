package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateTrackingDto implements Serializable {
    private String orderId;
    private String operation;
    private String operateAddress;
    private String terminalAddress;
    private String operator;
    private String operateTime;
    private String remark;

}
