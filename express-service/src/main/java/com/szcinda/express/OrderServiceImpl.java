package com.szcinda.express;

import com.szcinda.express.dto.*;
import com.szcinda.express.params.QueryOrderParams;
import com.szcinda.express.persistence.*;
import com.szcinda.express.utils.District;
import com.szcinda.express.utils.DistrictUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final SnowFlakeFactory snowFlakeFactory;
    private final OrderTrackingRepository trackingRepository;
    private final ClientRepository clientRepository;
    private final CarrierRepository carrierRepository;
    private final DistrictUtil districtUtil;

    public OrderServiceImpl(OrderRepository orderRepository, OrderTrackingRepository trackingRepository, ClientRepository clientRepository, CarrierRepository carrierRepository, DistrictUtil districtUtil) {
        this.orderRepository = orderRepository;
        this.trackingRepository = trackingRepository;
        this.clientRepository = clientRepository;
        this.carrierRepository = carrierRepository;
        this.districtUtil = districtUtil;
        this.snowFlakeFactory = SnowFlakeFactory.getInstance();
    }

    @Override
    public PageResult<TransportOrder> query(QueryOrderParams params) {
        Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(params.getDeliveryNo())) {
                Predicate likeName = criteriaBuilder.like(root.get("deliveryNo").as(String.class), params.getDeliveryNo().trim() + "%");
                predicates.add(likeName);
            }
            if (!StringUtils.isEmpty(params.getCindaNo())) {
                Predicate likeName = criteriaBuilder.like(root.get("cindaNo").as(String.class), params.getCindaNo().trim() + "%");
                predicates.add(likeName);
            }
            if (!StringUtils.isEmpty(params.getTransportNo())) {
                Predicate likeName = criteriaBuilder.like(root.get("transportNo").as(String.class), params.getTransportNo().trim() + "%");
                predicates.add(likeName);
            }
            if (!StringUtils.isEmpty(params.getClientName())) {
                Predicate likeName = criteriaBuilder.like(root.get("clientName").as(String.class), params.getClientName().trim() + "%");
                predicates.add(likeName);
            }
            if (!StringUtils.isEmpty(params.getDepartureWarning())) {
                Predicate equal = criteriaBuilder.equal(root.get("departureWarning").as(String.class), params.getDepartureWarning());
                predicates.add(equal);
            }
            if (!StringUtils.isEmpty(params.getDeliveryWarning())) {
                Predicate equal = criteriaBuilder.equal(root.get("deliveryWarning").as(String.class), params.getDeliveryWarning().trim());
                predicates.add(equal);
            }
            if (!StringUtils.isEmpty(params.getTimeoutWarning())) {
                Predicate equal = criteriaBuilder.equal(root.get("timeoutWarning").as(String.class), params.getTimeoutWarning().trim());
                predicates.add(equal);
            }
            if (params.getDeliveryDateStart() != null && params.getDeliveryDateEnd() != null) {
                Predicate start = criteriaBuilder.greaterThanOrEqualTo(root.get("deliveryDate"), params.getDeliveryDateStart().atStartOfDay());
                predicates.add(start);
                Predicate end = criteriaBuilder.lessThanOrEqualTo(root.get("deliveryDate"), params.getDeliveryDateEnd().atTime(23, 59, 59));
                predicates.add(end);
            }
            if (params.getCreateTimeStart() != null && params.getCreateTimeEnd() != null) {
                Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), params.getCreateTimeStart().atStartOfDay());
                predicates.add(createTimeStart);
                Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), params.getCreateTimeEnd().atTime(23, 59, 59));
                predicates.add(createTimeEnd);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        Sort.Order order = new Sort.Order(Sort.Direction.DESC,"createTime");
        Pageable pageable = new PageRequest(params.getPage() - 1, params.getPageSize(), new Sort(order));
        Page<TransportOrder> transportOrderPage = orderRepository.findAll(specification, pageable);
        return PageResult.of(transportOrderPage.getContent(), params.getPage(), params.getPageSize(), transportOrderPage.getTotalElements());
    }

    @Override
    public List<TransportOrder> queryAll(QueryOrderParams params) {
        Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), params.getCreateTimeStart().atStartOfDay());
            Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), params.getCreateTimeEnd().atTime(23, 59, 59));
            if (StringUtils.hasLength(params.getClientName())) {
                Predicate clientName = criteriaBuilder.equal(root.get("clientName"), params.getClientName());
                predicates.add(clientName);
            }
            if (StringUtils.hasLength(params.getTransportChannel())) {
                Predicate transportChannel = criteriaBuilder.equal(root.get("transportChannel"), params.getTransportChannel());
                predicates.add(transportChannel);
            }
            predicates.add(createTimeStart);
            predicates.add(createTimeEnd);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        return orderRepository.findAll(specification, sort);
    }

    @Override
    public List<TransportOrder> queryAll(ExportExcelDto exportExcelDto) {
        Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("deliveryDate"), exportExcelDto.getDeliveryDateStart().atStartOfDay());
            Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("deliveryDate"), exportExcelDto.getDeliveryDateEnd().plusDays(1).atStartOfDay());
            predicates.add(createTimeStart);
            predicates.add(createTimeEnd);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        return orderRepository.findAll(specification);
    }

    @Override
    public void create(OrderCreateDto order) {
        TransportOrder createOrder = new TransportOrder();
        BeanUtils.copyProperties(order, createOrder);
        createOrder.setId(snowFlakeFactory.nextId());
        createOrder.setCreateTime(LocalDateTime.now());
        orderRepository.save(createOrder);
    }

    @Override
    public void batchCreate(List<ExcelOrderImportDto> importDtos) {
        // 判断数据是否无误
        List<String> messages = new ArrayList<>();
        importDtos.forEach(dto -> {
            if (StringUtils.isEmpty(dto.getClientName())) {
                messages.add("导入文件客户名称不能为空");
            }
            if (dto.getDeliveryDate() == null) {
                messages.add("导入文件发货日期不能为空");
            }
            if (StringUtils.isEmpty(dto.getConsignNo())) {
                messages.add("导入文件托运单号不能为空");
            }
            if (StringUtils.isEmpty(dto.getFromProvince())) {
                messages.add("导入文件始发省份不能为空");
            }
            if (StringUtils.isEmpty(dto.getFromCity())) {
                messages.add("导入文件始发城市不能为空");
            }
            if (StringUtils.isEmpty(dto.getToProvince())) {
                messages.add("导入文件目的省份不能为空");
            }
            if (StringUtils.isEmpty(dto.getToCity())) {
                messages.add("导入文件目的城市不能为空");
            }
            if (StringUtils.isEmpty(dto.getTransportType())) {
                messages.add("导入文件运输方式不能为空");
            } else {
                if ("整车".equals(dto.getTransportType())) {
                    if (StringUtils.isEmpty(dto.getVehicleType())) {
                        messages.add("运输方式为整车时车型不能为空");
                    }
                }
            }
        });
        List<District> districts = districtUtil.convertDistrict();
        if (districts != null && districts.size() > 0) {
            importDtos.forEach(dto -> {
                String fromProvince = dto.getFromProvince();
                String fromCity = dto.getFromCity();
                String toProvince = dto.getToProvince();
                String toCity = dto.getToCity();
                districts.stream().filter(district -> district.getLabel().equals(fromProvince))
                        .findFirst()
                        .ifPresent(district -> {
                            List<District> childrens = district.getChildren();
                            childrens.stream().filter(children -> children.getLabel().equals(fromCity))
                                    .findFirst()
                                    .ifPresent(children -> dto.setFromDistrict(children.getValue()));
                        });
                districts.stream().filter(district -> district.getLabel().equals(toProvince))
                        .findFirst()
                        .ifPresent(district -> {
                            List<District> childrens = district.getChildren();
                            childrens.stream().filter(children -> children.getLabel().equals(toCity))
                                    .findFirst()
                                    .ifPresent(children -> dto.setToDistrict(children.getValue()));
                        });
            });
        }
        importDtos.forEach(dto -> {
            if (StringUtils.isEmpty(dto.getFromDistrict())) {
                messages.add(String.format("根据始发城市[%s]没有找到地区编码", dto.getFromCity()));
            }
            if (StringUtils.isEmpty(dto.getToDistrict())) {
                messages.add(String.format("根据目的城市[%s]没有找到地区编码", dto.getToCity()));
            }
        });
        Assert.isTrue(messages.isEmpty(), String.join(";", messages));
        if (importDtos.size() > 0) {
            List<TransportOrder> orders = new ArrayList<>();
            importDtos.forEach(importDto -> {
                TransportOrder order = new TransportOrder();
                BeanUtils.copyProperties(importDto, order);
                order.setId(snowFlakeFactory.nextId());
                Instant instant = importDto.getDeliveryDate().toInstant();
                ZoneId zone = ZoneId.systemDefault();
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
                order.setDeliveryDate(localDateTime);
                order.setItemCount(importDto.getItemCount());
                order.setVolume(importDto.getVolume());
                order.setWeight(importDto.getWeight());
                order.setTransportChannel(importDto.getTransportChannel());
                Address from = new Address();
                from.setDistrict(importDto.getFromDistrict());
                from.setCity(importDto.getFromCity());
                order.setFrom(from);
                Address to = new Address();
                to.setDistrict(importDto.getToDistrict());
                to.setProvince(importDto.getToProvince());
                to.setCity(importDto.getToCity());
                to.setFullAddress(importDto.getToAddress());
                order.setTo(to);
                order.setCreateTime(LocalDateTime.now());
                orders.add(order);
            });
            orderRepository.save(orders);
        }
    }

    @Override
    public void update(OrderUpdateDto updateDto) {
        Assert.hasText(updateDto.getId(), "ID不能为空");
        TransportOrder order = orderRepository.findFirstById(updateDto.getId());
        BeanUtils.copyProperties(updateDto, order, "id", "version");
        // 重新计算费用
        order.setModifyTime(LocalDateTime.now());
        order.setCalculate(false);
        orderRepository.save(order);
    }

    @Override
    public TransportOrder getById(String orderId) {
        Assert.hasText(orderId, "订单ID不能为空");
        return orderRepository.findFirstById(orderId);
    }

    @Override
    public void insertFeeDeclare(FeeDeclareInsertToDBDto insertToDBDto) {
        Assert.hasText(insertToDBDto.getFinancialReportId(), "报表ID不能为空");
        TransportOrder order = orderRepository.findFirstById(insertToDBDto.getFinancialReportId());
        order.setTransportChannel(insertToDBDto.getTransportChannel());
        order.setRemark(insertToDBDto.getRemark());
        order.refreshOutTransportFee();
        orderRepository.save(order);
    }

    @Override
    public void insertByRPA(RPAInsertToDBDto rpaInsertToDBDto) {
        Assert.hasText(rpaInsertToDBDto.getFinancialReportId(), "报表ID不能为空");
        TransportOrder order = orderRepository.findFirstById(rpaInsertToDBDto.getFinancialReportId());
        BeanUtils.copyProperties(rpaInsertToDBDto, order, "id", "version");
        order.refreshInFeeCount();
        order.refreshOutTransportFee();
        orderRepository.save(order);
    }

    @Override
    public void deleteAll() {
        orderRepository.deleteAll();
    }

    @Override
    public List<String> getCarrierList() {
        return orderRepository.findDistinctTransportChannel();
    }

    @Override
    public List<String> getClientList() {
        return orderRepository.findDistinctClientName();
    }

    @Override
    public List<TransportOrder> getTodayOrder(String pageNumber) {
        Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), LocalDateTime.now().toLocalDate().atStartOfDay());
            Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay());
            predicates.add(createTimeStart);
            predicates.add(createTimeEnd);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        Pageable pageable = new PageRequest(Integer.parseInt(pageNumber) - 1, 50);
        Page<TransportOrder> transportOrderPage = orderRepository.findAll(specification, pageable);
        return transportOrderPage.getContent() != null ? transportOrderPage.getContent() : new ArrayList<>();
    }

    @Override
    public List<OrderTrackingDto> getTodayOrderTracking() {
        Specification<OrderTracking> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), LocalDateTime.now().toLocalDate().atStartOfDay());
            predicates.add(createTimeStart);
            Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay());
            predicates.add(createTimeEnd);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        List<OrderTracking> trackings = trackingRepository.findAll(specification);
        return trackings.stream().map(tracking -> {
            OrderTrackingDto dto = new OrderTrackingDto();
            BeanUtils.copyProperties(tracking, dto);
            dto.setTransportNo(tracking.getOrder().getTransportNo());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void addTracking(CreateTrackingDto createTrackingDto) {
        Assert.hasText(createTrackingDto.getOrderId(), "订单ID不能为空");
        TransportOrder order = orderRepository.findFirstById(createTrackingDto.getOrderId());
        OrderTracking tracking = new OrderTracking();
        BeanUtils.copyProperties(createTrackingDto, tracking);
        tracking.setId(snowFlakeFactory.nextId());
        tracking.setCreateTime(LocalDateTime.now());
        order.addTracking(tracking);
        orderRepository.save(order);
    }

    @Override
    public List<OrderTracking> getTrackingById(String orderId) {
        TransportOrder order = orderRepository.findFirstById(orderId);
        return order.getTrackingList();
    }

    @Override
    public List<MonthReceivableReport> generateMonthReceivableReports() {
        Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            LocalDate today = LocalDate.now();
            Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay());
            predicates.add(createTimeStart);
            Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), today.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).atStartOfDay());
            predicates.add(createTimeEnd);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        List<TransportOrder> transportOrders = orderRepository.findAll(specification);
        Set<String> clientNames = transportOrders.stream().map(TransportOrder::getClientName).collect(Collectors.toSet());
        List<Client> clients = clientRepository.findByNameIn(clientNames);
        Map<String, List<TransportOrder>> clientGroup = transportOrders.stream().collect(Collectors.groupingBy(TransportOrder::getClientName));
        List<MonthReceivableReport> monthReceivableReports = new ArrayList<>();
        clientGroup.forEach((clientName, orders) -> {
            if (orders.size() > 0) {
                MonthReceivableReport report = new MonthReceivableReport();
                report.setClientName(clientName);
                clients.stream().filter(client -> client.getName().equals(clientName))
                        .findFirst()
                        .ifPresent(client -> report.setEmail(client.getEmail()));
                report.addOrder(orders);
                monthReceivableReports.add(report);
            }
        });
        return monthReceivableReports;
    }

    @Override
    public List<MonthPayableReport> generateMonthPayableReports() {
        Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            LocalDate today = LocalDate.now();
            Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay());
            Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), today.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).atStartOfDay());
            predicates.add(createTimeStart);
            predicates.add(createTimeEnd);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        List<TransportOrder> orders = orderRepository.findAll(specification);
        Set<String> carrierNames = orders.stream().filter(order -> !StringUtils.isEmpty(order.getTransportChannel())).map(TransportOrder::getTransportChannel)
                .collect(Collectors.toSet());
        List<Carrier> carriers = carrierRepository.findByNameIn(carrierNames);
        orders = orders.stream().filter(order -> !StringUtils.isEmpty(order.getTransportChannel())).collect(Collectors.toList());
        Map<String, List<TransportOrder>> clientGroup = orders.stream().collect(Collectors.groupingBy(TransportOrder::getTransportChannel));
        List<MonthPayableReport> monthPayableReports = new ArrayList<>();
        clientGroup.forEach((transportChannel, transportOrders) -> {
            if (transportOrders.size() > 0) {
                MonthPayableReport report = new MonthPayableReport();
                report.setTransportChannel(transportChannel);
                carriers.stream().filter(carrier -> carrier.getName().equals(transportChannel))
                        .findFirst()
                        .ifPresent(carrier -> report.setEmail(carrier.getEmail()));
                report.addOrder(transportOrders);
                monthPayableReports.add(report);
            }
        });
        return monthPayableReports;
    }

    @Override
    public List<OrderReceivableExcelModel> getClientMonthOrders(String clientName, String month) {//格式 2019年10月
        try {
            Date dates = new SimpleDateFormat("yyyy年M月").parse(month);
            Instant instant = dates.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDate localDate = instant.atZone(zoneId).toLocalDate();
            Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), localDate.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay());
                predicates.add(createTimeStart);
                Predicate equalName = criteriaBuilder.equal(root.get("clientName").as(String.class), clientName);
                predicates.add(equalName);
                Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), localDate.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).atStartOfDay());
                predicates.add(createTimeEnd);
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            });
            List<TransportOrder> transportOrders = orderRepository.findAll(specification);
            List<OrderReceivableExcelModel> models = new ArrayList<>();
            if (transportOrders.size() > 0) {
                transportOrders.forEach(order -> {
                    OrderReceivableExcelModel model = new OrderReceivableExcelModel();
                    BeanUtils.copyProperties(order, model);
                    model.setFromCity(order.getFrom().getCity());
                    model.setFullAddress(order.getTo().getFullAddress());
                    model.setToCity(order.getTo().getCity());
                    model.setDeliveryDate(order.getDeliveryDate() != null ? order.getDeliveryDate().toLocalDate().toString() : "");
                    models.add(model);
                });
            }
            return models;
        } catch (ParseException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<OrderPayableExcelModel> getCarrierMonthOrders(String transportChannel, String month) {//格式 2019年10月
        try {
            Date dates = new SimpleDateFormat("yyyy年M月").parse(month);
            Instant instant = dates.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDate localDate = instant.atZone(zoneId).toLocalDate();
            Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), localDate.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay());
                predicates.add(createTimeStart);
                Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), localDate.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).atStartOfDay());
                predicates.add(createTimeEnd);
                Predicate equalName = criteriaBuilder.equal(root.get("transportChannel").as(String.class), transportChannel);
                predicates.add(equalName);
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            });
            List<TransportOrder> transportOrders = orderRepository.findAll(specification);
            List<OrderPayableExcelModel> models = new ArrayList<>();
            if (transportOrders.size() > 0) {
                transportOrders.forEach(order -> {
                    OrderPayableExcelModel model = new OrderPayableExcelModel();
                    BeanUtils.copyProperties(order, model);
                    model.setFromCity(order.getFrom().getCity());
                    model.setCreateTime(order.getCreateTime() != null ? order.getCreateTime().toLocalDate().toString() : "");
                    model.setFullAddress(order.getTo().getFullAddress());
                    model.setToCity(order.getTo().getCity());
                    models.add(model);
                });
            }
            return models;
        } catch (ParseException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public DashboardDto generateDayCount() {
        DashboardDto dashboardDto = new DashboardDto();
        Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), LocalDateTime.now().toLocalDate().atStartOfDay());
            Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());
            predicates.add(createTimeStart);
            predicates.add(createTimeEnd);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        List<TransportOrder> todayOrders = orderRepository.findAll(specification);
        dashboardDto.setTodayOrderCount(todayOrders.size());
        //统计应收
        double todayIncome = todayOrders.stream().mapToDouble(TransportOrder::getInFeeAmount).sum();
        dashboardDto.setTodayIncome(todayIncome);
//        //统计应付
//        double totalPayment = todayOrders.stream().mapToDouble(TransportOrder::getOutTotalFeeAccount).sum();
//        dashboardDto.setTodayPayment(totalPayment);
//        //出港预警
//        long departureWarnings = todayOrders.stream().filter(order -> StringUtils.hasLength(order.getDepartureWarning())).count();
//        dashboardDto.setDepartureWarningCount(departureWarnings);
//        //配送预警
//        long deliveryWarnings = todayOrders.stream().filter(order -> StringUtils.hasLength(order.getDeliveryWarning())).count();
//        dashboardDto.setDeliveryWarningCount(deliveryWarnings);
//        //超时预警
//        long timeoutWarnings = todayOrders.stream().filter(order -> StringUtils.hasLength(order.getTimeoutWarning())).count();
//        dashboardDto.setTimeoutWarningCount(timeoutWarnings);
        return dashboardDto;
    }

    @Override
    public DashboardDto generateDayFee() {
        DashboardDto dashboardDto = new DashboardDto();
        //近15天的单量曲线图
        LocalDate localDate = LocalDate.now();
        LocalDate before14Day = localDate.minusDays(14);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM月dd日");
        for (int i = 1; i <= 15; i++) {
            LocalDate finalBefore14Day = before14Day;
            Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), finalBefore14Day.atStartOfDay());
                Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), finalBefore14Day.plusDays(1).atStartOfDay());
                predicates.add(createTimeStart);
                predicates.add(createTimeEnd);
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            });
            List<TransportOrder> todayOrders = orderRepository.findAll(specification);
            String day = before14Day.format(df);
            //每天的单量
            DashboardDto.OrderItem item = new DashboardDto.OrderItem();
            item.setDay(day);
            item.setOrderCount(todayOrders.size());
            dashboardDto.getOrderItems().add(item);

            //每天的营收情况
            DashboardDto.FeeDto feeDto = new DashboardDto.FeeDto();
            feeDto.setDay(day);
            double totalIncome = todayOrders.stream().mapToDouble(TransportOrder::getInFeeAmount).sum();
//            double totalPayment = todayOrders.stream().mapToDouble(TransportOrder::getOutTotalFeeAccount).sum();
            feeDto.setIncome(BigDecimal.valueOf(totalIncome).setScale(2, RoundingMode.HALF_UP).doubleValue());
//            feeDto.setPayment(BigDecimal.valueOf(totalPayment).setScale(2, RoundingMode.HALF_UP).doubleValue());
            feeDto.updateProfit();
            dashboardDto.getFeeItems().add(feeDto);
            //加1天
            before14Day = before14Day.plusDays(1);
        }
        return dashboardDto;
    }

    @Override
    public DashboardDto monthOrderCount() {
        DashboardDto dashboardDto = new DashboardDto();
        LocalDate now = LocalDate.now();
        Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), now.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay());
            Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), now.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).atStartOfDay());
            predicates.add(createTimeStart);
            predicates.add(createTimeEnd);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        //单月的单量
        List<TransportOrder> transportOrders = orderRepository.findAll(specification);
        Map<String, Long> clientOrderCount = transportOrders.stream().collect(Collectors.groupingBy(TransportOrder::getClientName, Collectors.counting()));
        Map<String, Long> carrierOrderCount = transportOrders.stream().filter(order -> StringUtils.hasLength(order.getTransportChannel()))
                .collect(Collectors.groupingBy(TransportOrder::getTransportChannel, Collectors.counting()));
        clientOrderCount.forEach((clientName, count) -> {
            DashboardDto.MonthClientOrderCount orderCount = new DashboardDto.MonthClientOrderCount();
            orderCount.setName(clientName);
            orderCount.setCount(count.intValue());
            dashboardDto.getClientOrderCounts().add(orderCount);
        });
        carrierOrderCount.forEach((carrier, count) -> {
            DashboardDto.MonthCarrierOrderCount orderCount = new DashboardDto.MonthCarrierOrderCount();
            orderCount.setName(carrier);
            orderCount.setCount(count.intValue());
            dashboardDto.getCarrierOrderCounts().add(orderCount);
        });
        return dashboardDto;
    }


    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        LocalDate next3Day = now.plusDays(3);
        Period period = Period.between(now, next3Day);
        System.out.println("相差：" + period.getDays());
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        System.out.println("第一天：" + firstDayOfThisMonth);
        LocalDate lastDayOfThisMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println("最后一天：" + lastDayOfThisMonth.plusDays(1));
        try {
            Date dates = new SimpleDateFormat("yyyy年M月").parse("2019年10月");
            Instant instant = dates.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDate localDate = instant.atZone(zoneId).toLocalDate();
            System.out.println("localDate:" + localDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
