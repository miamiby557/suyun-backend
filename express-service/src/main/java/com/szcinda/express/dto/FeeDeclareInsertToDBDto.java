package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FeeDeclareInsertToDBDto implements Serializable {
    private String financialReportId;//报表ID
    private String transportChannel;
    private double upstairsFee;//上楼费
    private double daofuProcedureFee;//倒付手续费
    private double terminalFee;//末端费用
    private String remark;//备注
}
