package com.szcinda.express.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class LingDanPriceListener extends AnalysisEventListener {
    private List<LingDanPriceDto> importDatas = new ArrayList<>();

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        LingDanPriceDto importDto = (LingDanPriceDto) o;
        if (StringUtils.hasLength(importDto.getStartCity())) {
            importDatas.add(importDto);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
