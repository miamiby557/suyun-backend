package com.szcinda.express.persistence;

import com.szcinda.express.Address;
import com.szcinda.express.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class TransportOrder extends BaseEntity {
    private String clientName;//客户名称
    private LocalDateTime deliveryDate;//发货日期
    private String cindaNo;//先达单号
    private String consignNo;//托运单号
    private String saleNo;//销售订单号
    private String deliveryNo;//发货单号
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
    private String productCode;//产品型号
    private String productName;//产品名称
    private String packageType;//包装方式
    private String calculateType;//计费方式

    private int itemCount;//件数
    private double volume;//总体积
    private double weight;//总重量
    private double inShippingFee;//运费
    private double inDeliveryFee;//送货费
    private double inUnloadFee;//卸货费
    private double inPickupFee;//提货费
    private double inSpecialFee;//特殊费用
    private double insuranceFee;//保险费
    private double inFeeAmount;//运费合计
    private String transportType;//运输模式   零担/整车/空运
    private String vehicleType;//车型 4.2/7.6/9.6/13.5/16.5/17.5
    private String remark;
    private String transportNo;//走货单号
    private String transportChannel;//走货渠道
    private double outPickupFee;//提货费
    private double outTransportFee;//运费
    private double outShippingFee;//配送费
    private String specialFee1;//特殊费用1 格式：费用项目:金额 （英文冒号）
    private String specialFee2;//特殊费用2 格式：费用项目:金额 （英文冒号）
    private String specialFee3;//特殊费用3 格式：费用项目:金额 （英文冒号）
    private String specialFee4;//特殊费用4 格式：费用项目:金额 （英文冒号）
    private String specialFee5;//特殊费用5 格式：费用项目:金额 （英文冒号）
    private double outFeeAmount;//应付金额
    private double profit;//毛利润
    private String profitPercent;//毛利率

    private boolean calculate;//是否已经算过费用
    private LocalDateTime modifyTime = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order", orphanRemoval = true)
    private List<OrderTracking> trackingList = new ArrayList<>();

    public void setRemark(String remark) {
        if (StringUtils.isEmpty(this.remark)) {
            this.remark = remark;
        } else {
            this.remark = this.remark + ";" + remark;
        }
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }


    public void addTracking(OrderTracking tracking) {
        tracking.setOrder(this);
        this.trackingList.add(tracking);
    }

    public void refreshInFeeCount() {
        this.inFeeAmount = this.inShippingFee + this.inDeliveryFee + this.inUnloadFee + this.inPickupFee
                + this.inSpecialFee;
    }

    public void refreshOutTransportFee() {
        this.outFeeAmount = this.outPickupFee + this.outTransportFee + this.outShippingFee;
        // 特殊费用1
        if (StringUtils.hasLength(specialFee1) && specialFee1.contains(":")) {
            String[] comboList = specialFee1.split(":");
            if (comboList.length == 2) {
                this.outFeeAmount += Double.parseDouble(comboList[1]);
            }
        }
        // 特殊费用2
        if (StringUtils.hasLength(specialFee2) && specialFee2.contains(":")) {
            String[] comboList = specialFee2.split(":");
            if (comboList.length == 2) {
                this.outFeeAmount += Double.parseDouble(comboList[1]);
            }
        }
        // 特殊费用3
        if (StringUtils.hasLength(specialFee3) && specialFee3.contains(":")) {
            String[] comboList = specialFee3.split(":");
            if (comboList.length == 2) {
                this.outFeeAmount += Double.parseDouble(comboList[1]);
            }
        }
        // 特殊费用4
        if (StringUtils.hasLength(specialFee4) && specialFee4.contains(":")) {
            String[] comboList = specialFee4.split(":");
            if (comboList.length == 2) {
                this.outFeeAmount += Double.parseDouble(comboList[1]);
            }
        }
        // 特殊费用5
        if (StringUtils.hasLength(specialFee5) && specialFee5.contains(":")) {
            String[] comboList = specialFee5.split(":");
            if (comboList.length == 2) {
                this.outFeeAmount += Double.parseDouble(comboList[1]);
            }
        }
        this.profit = this.inFeeAmount - this.outFeeAmount;
        BigDecimal b1 = BigDecimal.valueOf(this.profit);
        BigDecimal b2 = BigDecimal.valueOf(this.inFeeAmount);
//        Assert.isTrue(b2.doubleValue()>0,"应收费用为0");
        if(b2.doubleValue()>0){
            double value = b1.divide(b2, BigDecimal.ROUND_HALF_DOWN).setScale(4, RoundingMode.HALF_UP).doubleValue();
            this.profitPercent = String.valueOf(value * 100);
        }
    }
}
