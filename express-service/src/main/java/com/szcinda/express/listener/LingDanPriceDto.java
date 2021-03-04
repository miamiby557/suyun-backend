package com.szcinda.express.listener;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class LingDanPriceDto  extends BaseRowModel {
    @ExcelProperty(value = "始发地", index = 0)
    private String startCity;
    @ExcelProperty(value = "目的地（省）", index = 1)
    private String endProvince;
    @ExcelProperty(value = "目的地（市）", index = 2)
    private String endCity;
    @ExcelProperty(value = "干线单价(重量）", index = 3)
    private double weightPrice;
    @ExcelProperty(value = "干线单价（体积）", index = 4)
    private double volumePrice;
    @ExcelProperty(value = "配送费", index = 5)
    private double inDeliveryFee;
}
