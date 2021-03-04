package com.szcinda.express.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class DashboardDto implements Serializable {
    private long todayOrderCount;
    private double todayIncome;
    private double todayPayment;
    private long departureWarningCount;
    private long deliveryWarningCount;
    private long timeoutWarningCount;

    private long submitFeeCount;//未审核的费用

    private List<OrderItem> orderItems = new ArrayList<>();
    private List<FeeDto> feeItems = new ArrayList<>();

    private List<MonthClientOrderCount> clientOrderCounts = new ArrayList<>();
    private List<MonthCarrierOrderCount> carrierOrderCounts = new ArrayList<>();

    @Data
    public static class OrderItem implements Serializable {
        private String day;//MM月dd日
        private long orderCount;
    }

    @Data
    public static class FeeDto implements Serializable {
        private String day;
        private double income;
        private double payment;
        private double profit;

        public void updateProfit() {
            this.profit = BigDecimal.valueOf(this.income).subtract(BigDecimal.valueOf(this.payment)).doubleValue();
        }
    }

    //当月客户单量占比
    @Data
    public static class MonthClientOrderCount implements Serializable {
        private String name;
        private int count;
    }

    //当月承运商单量占比
    @Data
    public static class MonthCarrierOrderCount implements Serializable {
        private String name;
        private int count;
    }
}
