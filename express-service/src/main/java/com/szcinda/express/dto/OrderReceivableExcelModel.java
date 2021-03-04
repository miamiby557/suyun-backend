package com.szcinda.express.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderReceivableExcelModel extends BaseRowModel {
    @ExcelProperty(value = "发货日期", index = 0)
    private String deliveryDate;//发货日期
    @ExcelProperty(value = "托运单号", index = 1)
    private String consignNo;//托运单号
    @ExcelProperty(value = "销售订单号", index = 2)
    private String saleNo;//销售订单号
    @ExcelProperty(value = "发货单号", index = 3)
    private String deliveryNo;//发货单号
    @ExcelProperty(value = "始发地", index = 4)
    private String fromCity;
    @ExcelProperty(value = "目的地", index = 5)
    private String toCity;
    @ExcelProperty(value = "收货地址", index = 6)
    private String fullAddress;
    @ExcelProperty(value = "产品型号", index = 7)
    private String productCode;//产品型号
    @ExcelProperty(value = "产品名称", index = 8)
    private String productName;//产品名称
    @ExcelProperty(value = "包装方式", index = 9)
    private String packageType;//包装方式
    @ExcelProperty(value = "长", index = 10)
    private double length;//长
    @ExcelProperty(value = "宽", index = 11)
    private double width;//宽
    @ExcelProperty(value = "高", index = 12)
    private double height;//高
    @ExcelProperty(value = "件数", index = 13)
    private int itemCount;//件数
    @ExcelProperty(value = "体积", index = 14)
    private double volume;//体积
    @ExcelProperty(value = "总体积", index = 15)
    private double totalVolume;//总体积
    @ExcelProperty(value = "单价", index = 16)
    private double price;//单价
    @ExcelProperty(value = "折扣/上浮比例", index = 17)
    private String discount;//折扣/上浮比例
    @ExcelProperty(value = "运费", index = 18)
    private double inShippingFee;//运费
    @ExcelProperty(value = "送货费", index = 19)
    private double inDeliveryFee;//送货费
    @ExcelProperty(value = "卸货费", index = 20)
    private double inUnloadFee;//卸货费
    @ExcelProperty(value = "提货费", index = 21)
    private double inPickupFee;//提货费
    @ExcelProperty(value = "运费合计", index = 22)
    private double inFeeAccount;//运费合计
    @ExcelProperty(value = "运输模式", index = 23)
    private String transportType;//运输模式
}
