package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class ExportExcelDto implements Serializable {
    private LocalDate deliveryDateEnd;
    private LocalDate deliveryDateStart;
    private String userAccount;
}
