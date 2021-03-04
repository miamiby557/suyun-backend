package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RPAInsertToDBDto implements Serializable {
    private String financialReportId;//报表ID
    private double volume;//体积
    private double inShippingFee;//运费
    private double inDeliveryFee;//送货费
    private double inUnloadFee;//卸货费
    private double inPickupFee;//提货费
    private double inSpecialFee;//特殊费用
    private double insuranceFee;//保险费

    private double outPickupFee;//提货费
    private double outTransportFee;//运费
    private double outShippingFee;//配送费

    public static void main(String[] args) {
        double d1 = 89.67;
        double d2 = 56.98;
        System.out.println(d1 + d2);
    }
}
