package com.szcinda.express.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExcelOrderImportDto extends BaseRowModel {
    @ExcelProperty(value = "客户名称", index = 0)
    private String clientName;
    @ExcelProperty(value = "发货日期", format = "yyyy-MM-dd", index = 1)
    private Date deliveryDate;
    @ExcelProperty(value = "先达单号", index = 2)
    private String cindaNo;
    @ExcelProperty(value = "托运单号", index = 3)
    private String consignNo;
    @ExcelProperty(value = "销售订单号", index = 4)
    private String saleNo;
    @ExcelProperty(value = "发货单号", index = 5)
    private String deliveryNo;
    @ExcelProperty(value = "始发地（省）", index = 6)
    private String fromProvince;
    @ExcelProperty(value = "始发地（市）", index = 7)
    private String fromCity;
    @ExcelProperty(value = "目的地（省）", index = 8)
    private String toProvince;
    @ExcelProperty(value = "目的地（市）", index = 9)
    private String toCity;
    @ExcelProperty(value = "收货人", index = 10)
    private String toContactMan;
    @ExcelProperty(value = "收货人电话", index = 11)
    private String toContactPhone;
    @ExcelProperty(value = "收货地址", index = 12)
    private String toAddress;
    @ExcelProperty(value = "品号", index = 13)
    private String productCode;
    @ExcelProperty(value = "品名", index = 14)
    private String productName;
    @ExcelProperty(value = "包装方式", index = 15)
    private String packageType;
    @ExcelProperty(value = "走货渠道", index = 16)
    private String transportChannel;
    @ExcelProperty(value = "走货单号", index = 17)
    private String transportNo;
    @ExcelProperty(value = "件数", index = 18)
    private int itemCount;
    @ExcelProperty(value = "重量", index = 19)
    private double weight;
    @ExcelProperty(value = "体积", index = 20)
    private double volume;
    @ExcelProperty(value = "运输方式", index = 21)
    private String transportType;
    @ExcelProperty(value = "车型", index = 22)
    private String vehicleType;
    @ExcelProperty(value = "计费方式", index = 23)
    private String calculateType;
    @ExcelProperty(value = "备注", index = 24)
    private String remark;

    private String fromDistrict;
    private String toDistrict;
}
