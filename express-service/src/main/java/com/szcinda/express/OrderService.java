package com.szcinda.express;

import com.szcinda.express.dto.*;
import com.szcinda.express.params.QueryOrderParams;
import com.szcinda.express.persistence.OrderTracking;
import com.szcinda.express.persistence.TransportOrder;

import java.util.List;

public interface OrderService {
    PageResult<TransportOrder> query(QueryOrderParams params);

    List<TransportOrder> queryAll(QueryOrderParams params);

    List<TransportOrder> queryAll(ExportExcelDto exportExcelDto);

    void create(OrderCreateDto order);

    void batchCreate(List<ExcelOrderImportDto> importDtos);

    void update(OrderUpdateDto updateDto);

    TransportOrder getById(String orderId);

    void insertFeeDeclare(FeeDeclareInsertToDBDto insertToDBDto);

    void insertByRPA(RPAInsertToDBDto rpaInsertToDBDto);

    void deleteAll();

    List<String> getClientList();

    List<String> getCarrierList();

    List<TransportOrder> getTodayOrder(String pageNumber);

    List<OrderTrackingDto> getTodayOrderTracking();

    void addTracking(CreateTrackingDto createTrackingDto);

    List<OrderTracking> getTrackingById(String orderId);

    List<MonthReceivableReport> generateMonthReceivableReports();

    List<MonthPayableReport> generateMonthPayableReports();

    List<OrderReceivableExcelModel> getClientMonthOrders(String clientName, String month);

    List<OrderPayableExcelModel> getCarrierMonthOrders(String transportChannel, String month);

    DashboardDto generateDayCount();

    DashboardDto generateDayFee();

    DashboardDto monthOrderCount();
}
