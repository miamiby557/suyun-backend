package com.szcinda.express;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.szcinda.express.dto.ExportExcelDto;
import com.szcinda.express.listener.LingDanPriceDto;
import com.szcinda.express.listener.LingDanPriceListener;
import com.szcinda.express.listener.ZhengChePriceDto;
import com.szcinda.express.listener.ZhengChePriceListener;
import com.szcinda.express.persistence.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Transactional
public class SchedulerService {

    @Value("${fee.file.path}")
    private String filePath;

    public static final ConcurrentLinkedQueue<ExportExcelDto> queue = new ConcurrentLinkedQueue<>();

    private final OrderRepository orderRepository;
    private final FeeDeclareRepository declareRepository;
    private final UserRepository userRepository;

    public SchedulerService(OrderRepository orderRepository, FeeDeclareRepository feeDeclareRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.declareRepository = feeDeclareRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0/30 * * * ? ") // 30分钟 生成应收应付费用
    private void generateFee() {
        System.out.println("生成应收应付费用开始.......");
        // 取出今天未生成费用的订单列表
        Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("modifyTime"), LocalDateTime.now().toLocalDate().atStartOfDay());
            Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("modifyTime"), LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay());
            Predicate notCalculateFee = criteriaBuilder.equal(root.get("calculate"), Boolean.FALSE);
            predicates.add(createTimeStart);
            predicates.add(createTimeEnd);
            predicates.add(notCalculateFee);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        List<TransportOrder> transportOrders = orderRepository.findAll(specification);
        transportOrders.forEach(order -> {
            String clientName = order.getClientName();
            String transportChannel = order.getTransportChannel();
            String transportType = order.getTransportType();
            String fromCity = order.getFrom().getCity();
            String toCity = order.getTo().getCity();
            // 应收
            String receivableFilePath = filePath + File.separator + clientName + File.separator + transportType + File.separator + "价格表.xlsx";
            File receiveFile = new File(receivableFilePath);
            if (receiveFile.exists()) {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(receiveFile);
                    if ("零担".equals(transportType)) {
                        LingDanPriceListener lingDanPriceListener = new LingDanPriceListener();
                        EasyExcelFactory.readBySax(inputStream, new Sheet(1, 1, LingDanPriceDto.class), lingDanPriceListener);
                        List<LingDanPriceDto> priceDtos = lingDanPriceListener.getImportDatas();
                        String calculateType = order.getCalculateType();
                        priceDtos.stream().filter(priceDto -> priceDto.getStartCity().equals(fromCity) && priceDto.getEndCity().equals(toCity))
                                .findFirst()
                                .ifPresent(lingDanPriceDto -> {
                                    order.setInDeliveryFee(lingDanPriceDto.getInDeliveryFee());
                                    if ("体积".equals(calculateType)) {
                                        try {
                                            double volume = order.getVolume();
                                            order.setInShippingFee(volume * lingDanPriceDto.getVolumePrice());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else if ("重量".equals(calculateType)) {
                                        try {
                                            double weight = order.getWeight();
                                            order.setInShippingFee(weight * lingDanPriceDto.getWeightPrice());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    order.refreshInFeeCount();
                                    // 标志 ：已处理
                                    order.setCalculate(true);
                                });

                    } else if ("整车".equals(transportType)) {
                        String vehicleType = order.getVehicleType();
                        ZhengChePriceListener zhengChePriceListener = new ZhengChePriceListener();
                        EasyExcelFactory.readBySax(inputStream, new Sheet(1, 1, ZhengChePriceDto.class), zhengChePriceListener);
                        List<ZhengChePriceDto> priceDtos = zhengChePriceListener.getImportDatas();
                        priceDtos.stream().filter(priceDto -> priceDto.getStartCity().equals(fromCity) && priceDto.getEndCity().equals(toCity) && priceDto.getVehicleType().equals(vehicleType))
                                .findFirst()
                                .ifPresent(zhengChePriceDto -> {
                                    order.setInShippingFee(zhengChePriceDto.getMoney());
                                    order.refreshInFeeCount();
                                    // 标志 ：已处理
                                    order.setCalculate(true);
                                });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            // 应付
            String payableFilePath = filePath + File.separator + transportChannel + File.separator + transportType + File.separator + "价格表.xlsx";
            File payableFile = new File(payableFilePath);
            if (payableFile.exists()) {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(payableFile);
                    if ("零担".equals(transportType)) {
                        LingDanPriceListener lingDanPriceListener = new LingDanPriceListener();
                        EasyExcelFactory.readBySax(inputStream, new Sheet(1, 1, LingDanPriceDto.class), lingDanPriceListener);
                        List<LingDanPriceDto> priceDtos = lingDanPriceListener.getImportDatas();
                        String calculateType = order.getCalculateType();
                        priceDtos.stream().filter(priceDto -> priceDto.getStartCity().equals(fromCity) && priceDto.getEndCity().equals(toCity))
                                .findFirst()
                                .ifPresent(lingDanPriceDto -> {
                                    order.setOutShippingFee(lingDanPriceDto.getInDeliveryFee());
                                    if ("体积".equals(calculateType)) {
                                        try {
                                            double volume = order.getVolume();
                                            order.setOutTransportFee(volume * lingDanPriceDto.getVolumePrice());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else if ("重量".equals(calculateType)) {
                                        try {
                                            double weight = order.getWeight();
                                            order.setOutTransportFee(weight * lingDanPriceDto.getWeightPrice());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    order.refreshOutTransportFee();
                                    // 标志 ：已处理
                                    order.setCalculate(true);
                                });
                    } else if ("整车".equals(transportType)) {
                        String vehicleType = order.getVehicleType();
                        ZhengChePriceListener zhengChePriceListener = new ZhengChePriceListener();
                        EasyExcelFactory.readBySax(inputStream, new Sheet(1, 1, ZhengChePriceDto.class), zhengChePriceListener);
                        List<ZhengChePriceDto> priceDtos = zhengChePriceListener.getImportDatas();
                        priceDtos.stream().filter(priceDto -> priceDto.getStartCity().equals(fromCity) && priceDto.getEndCity().equals(toCity) && priceDto.getVehicleType().equals(vehicleType))
                                .findFirst()
                                .ifPresent(zhengChePriceDto -> {
                                    order.setOutTransportFee(zhengChePriceDto.getMoney());
                                    order.refreshOutTransportFee();
                                    // 标志 ：已处理
                                    order.setCalculate(true);
                                });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        orderRepository.save(transportOrders);
        System.out.println("生成应收应付费用结束.......");
    }

    @Scheduled(cron = "0 0/1 * * * ? ") // 1分钟 审批
    private void approvalFee() {
        System.out.println("操作审批费用开始.......");
        Specification<FeeDeclare> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
//            Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), LocalDateTime.now().toLocalDate().atStartOfDay());
//            Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay());
            Predicate statusEq = criteriaBuilder.equal(root.get("status").as(FeeDeclareStatus.class), FeeDeclareStatus.PASSED);
//            predicates.add(createTimeStart);
//            predicates.add(createTimeEnd);
            predicates.add(statusEq);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        List<FeeDeclare> feeDeclares = declareRepository.findAll(specification);
        feeDeclares.forEach(feeDeclare -> {
            String cindaNo = feeDeclare.getCindaNo();
            TransportOrder order = orderRepository.findFirstByCindaNo(cindaNo);
            if (order != null) {
                // 更新应收费用
                if (feeDeclare.getInCome() != null && feeDeclare.getInCome() > 0) {
                    order.setInFeeAmount(feeDeclare.getInCome());
                }
                // 更新应付费用
                order.setOutFeeAmount(feeDeclare.getMoney());
                // 更新销售费用
                order.setSalesFee((order.getInFeeAmount() * 0.91 - order.getOutFeeAmount()) * 0.6);
                // 更新利润
                order.setProfit((order.getInFeeAmount() - order.getOutFeeAmount() / 0.89) / 1.09 - order.getSalesFee());
//                boolean add = false;
                /*if (!StringUtils.hasText(order.getSpecialFee1())) {
                    order.setSpecialFee1(feeDeclare.getFeeItem() + ":" + feeDeclare.getMoney());
                    add = true;
                } else if (!StringUtils.hasText(order.getSpecialFee2())) {
                    order.setSpecialFee2(feeDeclare.getFeeItem() + ":" + feeDeclare.getMoney());
                    add = true;
                } else if (!StringUtils.hasText(order.getSpecialFee3())) {
                    order.setSpecialFee3(feeDeclare.getFeeItem() + ":" + feeDeclare.getMoney());
                    add = true;
                } else if (!StringUtils.hasText(order.getSpecialFee4())) {
                    order.setSpecialFee4(feeDeclare.getFeeItem() + ":" + feeDeclare.getMoney());
                    add = true;
                } else if (!StringUtils.hasText(order.getSpecialFee5())) {
                    order.setSpecialFee5(feeDeclare.getFeeItem() + ":" + feeDeclare.getMoney());
                    add = true;
                }*/
//                order.refreshOutTransportFee();
                orderRepository.save(order);
                feeDeclare.setStatus(FeeDeclareStatus.DONE);
                /*if (!add) {
                    feeDeclare.setStatus(FeeDeclareStatus.REJECTED);
                    if (StringUtils.hasText(feeDeclare.getRejectReason())) {
                        feeDeclare.setRejectReason(feeDeclare.getRejectReason() + ";申报记录已经超过5条");
                    } else {
                        feeDeclare.setRejectReason("申报记录已经超过5条");
                    }
                } else {
                    feeDeclare.setStatus(FeeDeclareStatus.DONE);
                }*/
            } else {
                feeDeclare.setStatus(FeeDeclareStatus.REJECTED);
                if (StringUtils.hasText(feeDeclare.getRejectReason())) {
                    feeDeclare.setRejectReason(feeDeclare.getRejectReason() + ";根据托运单号没有找到订单");
                } else {
                    feeDeclare.setRejectReason("根据托运单号没有找到订单");
                }
            }
        });
        declareRepository.save(feeDeclares);
        System.out.println("操作审批费用结束.......");
    }


    @Scheduled(cron = "0 0/2 * * * ? ") // 15分钟 审批
    private void exportExcel() throws Exception{
        ExportExcelDto poll = queue.poll();
        if (poll != null) {
            System.out.println("start python");
            User user = userRepository.findFirstByAccount(poll.getUserAccount());
            String email = user.getEmail();
            String[] args1 = new String[] { "C:\\Users\\Administrator\\AppData\\Local\\Programs\\Python\\Python37\\python.exe", "C:\\export.py", poll.getDeliveryDateStart().toString(), poll.getDeliveryDateEnd().toString(), email};
            Runtime.getRuntime().exec(args1);
            System.out.println("end python");
            /*Specification<TransportOrder> specification = ((root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                Predicate createTimeStart = criteriaBuilder.greaterThanOrEqualTo(root.get("deliveryDate"), poll.getDeliveryDateStart().atStartOfDay());
                Predicate createTimeEnd = criteriaBuilder.lessThanOrEqualTo(root.get("deliveryDate"), poll.getDeliveryDateEnd().plusDays(1).atStartOfDay());
                predicates.add(createTimeStart);
                predicates.add(createTimeEnd);
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            });
            List<TransportOrder> transportOrders = orderRepository.findAll(specification);*/
        }
    }
}
