package com.szcinda.express.persistence;

import com.szcinda.express.Address;
import com.szcinda.express.BaseEntity;
import com.szcinda.express.FeeDeclareStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class FeeDeclare extends BaseEntity {
    private String clientName;
    private String deliveryNo;
    private LocalDateTime deliveryDate;//发货日期
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "district", column = @Column(name = "from_district")),
            @AttributeOverride(name = "province", column = @Column(name = "from_province")),
            @AttributeOverride(name = "city", column = @Column(name = "from_city")),
            @AttributeOverride(name = "street", column = @Column(name = "from_street")),
            @AttributeOverride(name = "fullAddress", column = @Column(name = "from_full_address")),
            @AttributeOverride(name = "contactMan", column = @Column(name = "from_contact_man")),
            @AttributeOverride(name = "contactPhone", column = @Column(name = "from_contact_phone")),
            @AttributeOverride(name = "company", column = @Column(name = "from_company"))
    })
    private Address from;//始发地址
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "district", column = @Column(name = "to_district")),
            @AttributeOverride(name = "province", column = @Column(name = "to_province")),
            @AttributeOverride(name = "city", column = @Column(name = "to_city")),
            @AttributeOverride(name = "street", column = @Column(name = "to_street")),
            @AttributeOverride(name = "fullAddress", column = @Column(name = "to_full_address")),
            @AttributeOverride(name = "contactMan", column = @Column(name = "to_contact_man")),
            @AttributeOverride(name = "contactPhone", column = @Column(name = "to_contact_phone")),
            @AttributeOverride(name = "company", column = @Column(name = "to_company"))
    })
    private Address to;//收货地址
    private String vehicleType;//车型 4.2/7.6/9.6/13.5/16.5/17.5
    private Double inCome;
    private Double exceptionFee; // 异常费用
    private Double inFeeCount; // 应收总和
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
