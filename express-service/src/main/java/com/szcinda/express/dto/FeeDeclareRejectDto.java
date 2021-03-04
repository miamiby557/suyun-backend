package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FeeDeclareRejectDto implements Serializable {
    private String declareId;
    private String rejectReason;//驳回原因
}
