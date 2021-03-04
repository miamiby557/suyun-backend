package com.szcinda.express.controller.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReportParams implements Serializable {
    private String clientName;
    private String month;//yyyy年M月
    private String transportChannel;
}
