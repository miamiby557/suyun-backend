package com.szcinda.express.dto;

import com.szcinda.express.persistence.TransportOrder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MonthPayableReport implements Serializable {
    private String transportChannel;
    private String email;
    private List<OrderPayableExcelModel> datas = new ArrayList<>();

    public void addOrder(List<TransportOrder> orders) {
        orders.forEach(order -> {
            OrderPayableExcelModel model = new OrderPayableExcelModel();
            BeanUtils.copyProperties(order, model);
            model.setFromCity(order.getFrom().getCity());
            model.setCreateTime(order.getCreateTime().toLocalDate().toString());
            model.setToCity(order.getTo().getCity());
            model.setFullAddress(order.getTo().getFullAddress());
            datas.add(model);
        });
    }
}
