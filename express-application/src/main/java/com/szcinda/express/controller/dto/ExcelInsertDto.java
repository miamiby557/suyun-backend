package com.szcinda.express.controller.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExcelInsertDto implements Serializable {
    private String transportNo;
    private String consignNo;//托运单号
    //应收计算
    private String clientName;//客户
    private String fromCity;
    private String toCity;
    private double volume;//总体积
    private double weight;//总重量
    private double inPrice;//单价
    private double inShippingFee;//总运费
    private double inDeliveryFee;//送货费
    private String transportType;//运输模式
    private String vehicleType;//车型
    //应付计算
    private String transportChannel;//走货渠道
    private double outPrice;//体积单价
    private double outTransportFee;//总费用
    private double outMainlineFee;//干线费
    private double outShippingFee;//配送费
    private double receiptProcedureFee;//回单手续费
    private double childOrderCreateFee;//子单开单费
    private double insuranceFee;//保险费
    private double specialFee;//特殊费用
}
