package com.szcinda.express.listener;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

@Data
public class ZhengChePriceDto extends BaseRowModel {
    @ExcelProperty(value = "始发地", index = 0)
    private String startCity;
    @ExcelProperty(value = "目的地（省）", index = 1)
    private String endProvince;
    @ExcelProperty(value = "目的地（市）", index = 2)
    private String endCity;
    @ExcelProperty(value = "车型", index = 3)
    private String vehicleType;
    @ExcelProperty(value = "价格", index = 4)
    private double money;
}
