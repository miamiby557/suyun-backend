package com.szcinda.express.persistence;

import com.szcinda.express.BaseEntity;
import com.szcinda.express.FeeDeclareStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class FeeDeclare extends BaseEntity {
    private String clientName;
    private String deliveryNo;
    private Double inCome;
    @NotNull(message = "单号不能为空")
    private String cindaNo;//先达单号
    private String transportChannel;
    private String feeItem;//费用项目
    private Double money;//金额
    @Enumerated(EnumType.STRING)
    private FeeDeclareStatus status;//申报状态
    private String remark;//备注
    private String rejectReason;//驳回原因
    private String person;
}
