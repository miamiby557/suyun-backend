package com.szcinda.express.controller.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.szcinda.express.dto.ExcelOrderImportDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExcelListener extends AnalysisEventListener {
    private List<ExcelOrderImportDto> importDatas = new ArrayList<>();

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        ExcelOrderImportDto importDto = (ExcelOrderImportDto) o;
        if (StringUtils.hasLength(importDto.getClientName())) {
            importDatas.add(importDto);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
