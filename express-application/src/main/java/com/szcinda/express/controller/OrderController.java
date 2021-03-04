package com.szcinda.express.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.szcinda.express.Address;
import com.szcinda.express.OrderService;
import com.szcinda.express.SchedulerService;
import com.szcinda.express.configuration.UserLoginToken;
import com.szcinda.express.controller.dto.ExcelInsertDto;
import com.szcinda.express.controller.dto.ReportParams;
import com.szcinda.express.controller.dto.Result;
import com.szcinda.express.controller.utils.ExcelListener;
import com.szcinda.express.dto.*;
import com.szcinda.express.params.QueryOrderParams;
import com.szcinda.express.persistence.OrderTracking;
import com.szcinda.express.persistence.TransportOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @UserLoginToken
    @PostMapping("/query")
    public PageResult<TransportOrder> query(@RequestBody QueryOrderParams params) {
        return orderService.query(params);
    }

    @UserLoginToken
    @PostMapping("/create")
    public Result<String> createOrder(@RequestBody OrderCreateDto createDto) {
        orderService.create(createDto);
        return Result.success();
    }

    @UserLoginToken
    @GetMapping("/{orderId}")
    public Result<TransportOrder> getById(@PathVariable String orderId) {
        return Result.success(orderService.getById(orderId));
    }

    @UserLoginToken
    @PostMapping("/update")
    public Result<String> update(@RequestBody OrderUpdateDto updateDto) {
        orderService.update(updateDto);
        return Result.success();
    }

    @UserLoginToken
    @PostMapping("/insertFeeDeclare")
    public Result<String> insertFeeDeclare(@RequestBody FeeDeclareInsertToDBDto insertToDBDto) {
        orderService.insertFeeDeclare(insertToDBDto);
        return Result.success();
    }

    @GetMapping("/getTodayOrderTrackings")
    public List<OrderTrackingDto> getTodayOrderTrackings() {
        return orderService.getTodayOrderTracking();
    }

    @UserLoginToken
    @PostMapping("/insertByRPA")
    public Result<String> insertByRPA(@RequestBody RPAInsertToDBDto rpaInsertToDBDto) {
        orderService.insertByRPA(rpaInsertToDBDto);
        return Result.success();
    }

    @PostMapping("/orderImport")
    public Result<String> orderImport(@RequestParam("file") MultipartFile file) throws Exception {
        // 解析每行结果在listener中处理
        InputStream inputStream = file.getInputStream();
        try {
            ExcelListener listener = new ExcelListener();
            System.out.println("导入...");
            EasyExcelFactory.readBySax(inputStream, new Sheet(1, 1, ExcelOrderImportDto.class), listener);
            orderService.batchCreate(listener.getImportDatas());
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //下载应收对账
    @PostMapping("/exportReceivable")
    public void exportReceivable(@RequestParam String createTimeStartString,
                                 @RequestParam String createTimeEndString,
                                 @RequestParam String clientName,
                                 HttpServletResponse response) throws Exception {
        List<TransportOrder> transportOrders = queryAll(createTimeStartString, createTimeEndString, clientName, null);
        Map<String, List<TransportOrder>> orderMap = transportOrders.stream()
                .collect(Collectors.groupingBy(TransportOrder::getClientName));
        OutputStream out = response.getOutputStream();
        AtomicInteger sheetNo = new AtomicInteger(1);
        try {
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            orderMap.forEach((client, orderList) -> {
                List<OrderReceivableExcelModel> receivableExcelModels = orderList.stream().map(order -> {
                    OrderReceivableExcelModel model = new OrderReceivableExcelModel();
                    BeanUtils.copyProperties(order, model);
                    model.setDeliveryDate(order.getDeliveryDate() != null ? order.getDeliveryDate().toLocalDate().toString() : "");
                    model.setFromCity(order.getFrom().getCity());
                    model.setToCity(order.getTo().getCity());
                    model.setFullAddress(order.getTo().getFullAddress());
                    return model;
                }).collect(Collectors.toList());
                Sheet sheet = new Sheet(sheetNo.getAndIncrement(), 0, OrderReceivableExcelModel.class);
                sheet.setSheetName(client);
                writer.write(receivableExcelModels, sheet);
            });
            // 下载EXCEL
            response.setHeader("Content-Disposition", "attachment;filename=receivable.xlsx");
            writer.finish();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/search")
    public List<TransportOrder> search(@RequestBody ExportExcelDto exportExcelDto) {
        return orderService.queryAll(exportExcelDto);
    }

    private List<TransportOrder> queryAll(String createTimeStartString, String createTimeEndString, String clientName, String carrierName) {
        Assert.isTrue(createTimeStartString.length() == 10, "日期格式必须是YYYY-MM-DD");
        Assert.isTrue(createTimeEndString.length() == 10, "日期格式必须是YYYY-MM-DD");
        QueryOrderParams params = new QueryOrderParams();
        params.setClientName(clientName);
        params.setTransportChannel(carrierName);
        params.setCreateTimeStart(LocalDate.parse(createTimeStartString));
        params.setCreateTimeEnd(LocalDate.parse(createTimeEndString));
        return orderService.queryAll(params);
    }

    //下载应付对账
    @PostMapping("/exportExcel")
    public Result<String> exportExcel(@RequestBody ExportExcelDto exportExcelDto){
        Assert.isTrue(exportExcelDto.getDeliveryDateStart().isBefore(exportExcelDto.getDeliveryDateEnd()),"开始时间必须小于结束时间");
        SchedulerService.queue.add(exportExcelDto);
        return Result.success();
    }


    //下载应付对账
    @PostMapping("/exportPayable")
    public void exportPayable(@RequestParam String createTimeStartString,
                              @RequestParam String createTimeEndString,
                              @RequestParam String transportChannel,
                              HttpServletResponse response) throws Exception {
        List<TransportOrder> transportOrders = queryAll(createTimeStartString, createTimeEndString, null, transportChannel);
        Map<String, List<TransportOrder>> orderMap = transportOrders.stream()
                .collect(Collectors.groupingBy(TransportOrder::getTransportChannel));
        OutputStream out = response.getOutputStream();
        AtomicInteger sheetNo = new AtomicInteger(1);

        try {
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            orderMap.forEach((carrier, orderList) -> {
                List<OrderPayableExcelModel> receivableExcelModels = orderList.stream().map(order -> {
                    OrderPayableExcelModel model = new OrderPayableExcelModel();
                    BeanUtils.copyProperties(order, model);
                    model.setCreateTime(order.getCreateTime().toLocalDate().toString());
                    model.setFromCity(order.getFrom().getCity());
                    model.setToCity(order.getTo().getCity());
                    model.setFullAddress(order.getTo().getFullAddress());
                    return model;
                }).collect(Collectors.toList());
                Sheet sheet = new Sheet(sheetNo.getAndIncrement(), 0, OrderPayableExcelModel.class);
                sheet.setSheetName(carrier);
                writer.write(receivableExcelModels, sheet);
            });
            // 下载EXCEL
            response.setHeader("Content-Disposition", "attachment;filename=payable.xlsx");
            writer.finish();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/initOrder")
    public Result<String> initOrder() {
        Address from = new Address("440305", "广东省", "深圳市", "南山区", "南山西丽茶光路", "黄先生", "13112274760", "先达速运");
        Address to1 = new Address("3401", "重庆市", "重庆市", "", "安徽合肥", "黄先生", "13112274760", "收货公司");
        Address to3 = new Address("3404", "重庆市", "重庆市", "", "安徽淮南", "黄先生", "13112274760", "收货公司");
        List<Address> toAddressList = new ArrayList<>();
        toAddressList.add(to1);
        toAddressList.add(to3);
        orderService.deleteAll();
        for (int i = 1; i <= 2; i++) {
            OrderCreateDto createDto = new OrderCreateDto();
            createDto.setClientName("科信");
            createDto.setDeliveryDate(LocalDateTime.now());
            createDto.setDeliveryNo("FH" + String.format("%08d", i));
            createDto.setSaleNo("XS" + String.format("%08d", i));
            createDto.setFrom(from);
            createDto.setTo(toAddressList.get(i % 2));
            createDto.setProductCode("CODE" + i);
            createDto.setProductName("机器" + i);
            createDto.setTransportType("零担");
            createDto.setPackageType("纸箱");
            createDto.setConsignNo("TY" + String.format("%08d", i));
            createDto.setTransportChannel("恒路");
            createDto.setTransportNo("TN" + String.format("%08d", i));
            createDto.setItemCount(100);
            createDto.setWeight(300.5);
            orderService.create(createDto);
        }
        return Result.success();
    }

    @GetMapping("/getTodayOrder/{pageNumber}")
    public List<ExcelInsertDto> getTodayOrder(@PathVariable String pageNumber) {
        List<TransportOrder> transportOrders = orderService.getTodayOrder(pageNumber);
        List<ExcelInsertDto> excelInsertDtos = new ArrayList<>();
        if (transportOrders != null && transportOrders.size() > 0) {
            transportOrders.forEach(order -> {
                ExcelInsertDto dto = new ExcelInsertDto();
                BeanUtils.copyProperties(order, dto);
                dto.setFromCity(order.getFrom().getCity());
                dto.setToCity(order.getTo().getCity());
                dto.setWeight(order.getWeight());
                excelInsertDtos.add(dto);
            });
        }
        return excelInsertDtos;
    }

    @PostMapping("/addTracking")
    public Result<String> addTracking(@RequestBody CreateTrackingDto createTrackingDto) {
        orderService.addTracking(createTrackingDto);
        return Result.success();
    }

    @GetMapping("/getTrackingList/{orderId}")
    public List<OrderTracking> getTrackingList(@PathVariable String orderId) {
        return orderService.getTrackingById(orderId);
    }

    @GetMapping("/generateMonthReceivableReports")
    public List<MonthReceivableReport> generateMonthReceivableReports() {
        return orderService.generateMonthReceivableReports();
    }

    @GetMapping("/generateMonthPayableReports")
    public List<MonthPayableReport> generateMonthPayableReports() {
        return orderService.generateMonthPayableReports();
    }

    @PostMapping("/getClientMonthOrders")
    public List<OrderReceivableExcelModel> getClientMonthOrders(@RequestBody ReportParams reportParams) {
        return orderService.getClientMonthOrders(reportParams.getClientName(), reportParams.getMonth());
    }

    @PostMapping("/getCarrierMonthOrders")
    public List<OrderPayableExcelModel> getCarrierMonthOrders(@RequestBody ReportParams reportParams) {
        return orderService.getCarrierMonthOrders(reportParams.getTransportChannel(), reportParams.getMonth());
    }
}
