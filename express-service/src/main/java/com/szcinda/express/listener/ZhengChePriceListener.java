package com.szcinda.express.listener;

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
public class ZhengChePriceListener extends AnalysisEventListener {
    private List<ZhengChePriceDto> importDatas = new ArrayList<>();

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        ZhengChePriceDto importDto = (ZhengChePriceDto) o;
        if (StringUtils.hasLength(importDto.getStartCity())) {
            importDatas.add(importDto);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
