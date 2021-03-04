package com.szcinda.express.dto;

import com.szcinda.express.persistence.TransportOrder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MonthReceivableReport implements Serializable {
    private String clientName;
    private String email;
    private List<OrderReceivableExcelModel> datas = new ArrayList<>();

    public void addOrder(List<TransportOrder> orders) {
        orders.forEach(order -> {
            OrderReceivableExcelModel model = new OrderReceivableExcelModel();
            BeanUtils.copyProperties(order, model);
            model.setToCity(order.getTo().getCity());
            model.setDeliveryDate(order.getDeliveryDate()!=null?order.getDeliveryDate().toLocalDate().toString():"");
            model.setFullAddress(order.getTo().getFullAddress());
            model.setFromCity(order.getFrom().getCity());
            datas.add(model);
        });
    }
}
