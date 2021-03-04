package com.szcinda.express.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class QueryOrderParams extends PageParams {
    private String clientName;
    private LocalDate deliveryDateStart;
    private LocalDate deliveryDateEnd;
    private LocalDate createTimeStart;
    private LocalDate createTimeEnd;
    private String deliveryNo;
    private String consignNo;
    private String transportNo;
    private String transportChannel;
    private String departureWarning;
    private String deliveryWarning;
    private String timeoutWarning;
}
