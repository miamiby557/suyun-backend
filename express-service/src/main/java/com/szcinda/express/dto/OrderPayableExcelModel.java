package com.szcinda.express.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderPayableExcelModel extends BaseRowModel {
    @ExcelProperty(value = "走货日期", index = 0)
    private String createTime;
    @ExcelProperty(value = "托运单号", index = 1)
    private String consignNo;//托运单号
    @ExcelProperty(value = "走货单号", index = 2)
    private String transportNo;//走货单号
    @ExcelProperty(value = "走货渠道", index = 3)
    private String transportChannel;//走货渠道
    @ExcelProperty(value = "始发地", index = 4)
    private String fromCity;
    @ExcelProperty(value = "目的地", index = 5)
    private String toCity;
    @ExcelProperty(value = "收货地址", index = 6)
    private String fullAddress;
    @ExcelProperty(value = "长", index = 7)
    private double length;//长
    @ExcelProperty(value = "宽", index = 8)
    private double width;//宽
    @ExcelProperty(value = "高", index = 9)
    private double height;//高
    @ExcelProperty(value = "件数", index = 10)
    private int itemCount;//件数
    @ExcelProperty(value = "体积", index = 11)
    private double volume;//体积
    @ExcelProperty(value = "总体积", index = 12)
    private double totalVolume;
    @ExcelProperty(value = "提货费", index = 13)
    private double outPickupFee;//提货费
    @ExcelProperty(value = "燃油费", index = 14)
    private double outFuelFee;//燃油费
    @ExcelProperty(value = "运费单价", index = 15)
    private double outPrice;//运费单价
    @ExcelProperty(value = "干线费", index = 16)
    private double outMainlineFee;//干线费
    @ExcelProperty(value = "配送费", index = 17)
    private double outShippingFee;//配送费
    @ExcelProperty(value = "回单手续费", index = 18)
    private double receiptProcedureFee;//回单手续费
    @ExcelProperty(value = "子单开单费", index = 19)
    private double childOrderCreateFee;//子单开单费
    @ExcelProperty(value = "保险费", index = 20)
    private double insuranceFee;//保险费
    @ExcelProperty(value = "上楼费", index = 21)
    private double upstairsFee;//上楼费
    @ExcelProperty(value = "倒付手续费", index = 22)
    private double daofuProcedureFee;//倒付手续费
    @ExcelProperty(value = "特殊费用", index = 23)
    private double specialFee;//特殊费用
    @ExcelProperty(value = "应付金额", index = 24)
    private double outFeeAccount;//应付金额
    @ExcelProperty(value = "末端费用", index = 25)
    private double terminalFee;//末端费用
    @ExcelProperty(value = "应付总金额", index = 26)
    private double outTotalFeeAccount;//应付总金额
}
