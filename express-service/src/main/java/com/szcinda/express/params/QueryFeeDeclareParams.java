package com.szcinda.express.params;

import com.szcinda.express.FeeDeclareStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class QueryFeeDeclareParams extends PageParams {
    private String cindaNo;
    private LocalDate createTimeStart;
    private LocalDate createTimeEnd;
    private LocalDate deliveryDateStart;
    private LocalDate deliveryDateEnd;
    private FeeDeclareStatus status;
}
