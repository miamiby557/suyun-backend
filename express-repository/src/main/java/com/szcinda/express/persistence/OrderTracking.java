package com.szcinda.express.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szcinda.express.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class OrderTracking extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TransportOrder order;

    private String operation;

    private String operateAddress;
    private String terminalAddress;

    private String operator;

    private String operateTime;
    private String remark;
}
