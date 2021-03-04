package com.szcinda.express.dto;

import com.szcinda.express.Address;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderUpdateDto implements Serializable {
    private String id;
    private String clientName;//客户名称
    private LocalDateTime deliveryDate;//发货日期
    private String cindaNo;//先达单号
    private String consignNo;//托运单号
    private String saleNo;//销售订单号
    private String deliveryNo;//发货单号
    private Address from;//始发地址
    private Address to;//收货地址
    private String productCode;//产品型号
    private String productName;//产品名称
    private String packageType;//包装方式
    private String calculateType;//计费方式

    private int itemCount;//件数
    private double volume;//总体积
    private double weight;//总重量
    private String transportType;//运输模式   零担/整车/空运
    private String vehicleType;//车型 4.2/7.6/9.6/13.5/16.5/17.5
    private String remark;
    private String transportNo;//走货单号
    private String transportChannel;//走货渠道
}
