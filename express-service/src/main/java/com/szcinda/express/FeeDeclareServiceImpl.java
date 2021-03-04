package com.szcinda.express;

import com.szcinda.express.dto.*;
import com.szcinda.express.params.QueryFeeDeclareParams;
import com.szcinda.express.persistence.*;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeeDeclareServiceImpl implements FeeDeclareService {

    private final FeeDeclareRepository declareRepository;
    private final SnowFlakeFactory snowFlakeFactory;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public FeeDeclareServiceImpl(FeeDeclareRepository declareRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.declareRepository = declareRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.snowFlakeFactory = SnowFlakeFactory.getInstance();
    }

    @Override
    public PageResult<FeeDeclareDto> query(QueryFeeDeclareParams params) {
        Specification<FeeDeclare> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasLength(params.getCindaNo())) {
                Predicate likeName = criteriaBuilder.like(root.get("cindaNo").as(String.class), params.getCindaNo().trim() + "%");
                predicates.add(likeName);
            }
            if (params.getCreateTimeStart() != null && params.getCreateTimeEnd() != null) {
                Predicate start = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), params.getCreateTimeStart().atStartOfDay());
                predicates.add(start);
                Predicate end = criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), params.getCreateTimeEnd().atTime(23, 59, 59));
                predicates.add(end);
            }
            if (params.getStatus() != null) {
                Predicate equal = criteriaBuilder.equal(root.get("status").as(FeeDeclareStatus.class), params.getStatus());
                predicates.add(equal);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createTime");
        Pageable pageable = new PageRequest(params.getPage() - 1, params.getPageSize(), new Sort(order));
        Page<FeeDeclare> transportOrderPage = declareRepository.findAll(specification, pageable);
        List<FeeDeclareDto> declareDtos = new ArrayList<>();
        if (transportOrderPage.getContent() != null && transportOrderPage.getContent().size() > 0) {
            List<FeeDeclare> feeDeclares = transportOrderPage.getContent();
            List<String> cindaNos = feeDeclares.stream().map(FeeDeclare::getCindaNo).collect(Collectors.toList());
            List<TransportOrder> transportOrders = orderRepository.findByCindaNoIn(cindaNos);
            feeDeclares.forEach(feeDeclare -> {
                FeeDeclareDto declareDto = new FeeDeclareDto();
                BeanUtils.copyProperties(feeDeclare, declareDto);
                transportOrders.stream().filter(o -> o.getCindaNo().equals(feeDeclare.getCindaNo()))
                        .findFirst()
                        .ifPresent(o -> {
                            declareDto.setProvince(o.getTo().getProvince());
                            declareDto.setCity(o.getTo().getCity());
                            declareDto.setReceiveMan(o.getTo().getContactMan());
                        });
                declareDtos.add(declareDto);
            });
        }
        return PageResult.of(declareDtos, params.getPage(), params.getPageSize(), transportOrderPage.getTotalElements());
    }

    @Override
    public void createDeclare(FeeDeclareCreateDto createDto) {
        TransportOrder order = orderRepository.findFirstByCindaNo(createDto.getCindaNo());
        Assert.isTrue(order != null, String.format("没有找到先达单号[%s]的订单信息", createDto.getCindaNo()));
        /*boolean canAdd = StringUtils.isEmpty(order.getSpecialFee1()) || StringUtils.isEmpty(order.getSpecialFee2()) || StringUtils.isEmpty(order.getSpecialFee3()) ||
                StringUtils.isEmpty(order.getSpecialFee4()) || StringUtils.isEmpty(order.getSpecialFee5());
        Assert.isTrue(canAdd, "申报记录已经超过5,不允许再申报");*/
        FeeDeclare declare = new FeeDeclare();
        BeanUtils.copyProperties(createDto, declare);
        declare.setVehicleType(order.getVehicleType());
        declare.setFrom(order.getFrom());
        declare.setTo(order.getTo());
        declare.setId(snowFlakeFactory.nextId());
        declare.setCreateTime(LocalDateTime.now());
        declare.setStatus(FeeDeclareStatus.SUBMITTED);
        User user = userRepository.findFirstByAccount(createDto.getPerson());
        declare.setPerson(user.getName());
        declareRepository.save(declare);
    }

    @Override
    public FeeDeclare getById(String id) {
        return declareRepository.findFirstById(id);
    }

    @Override
    public void reApproval(String declareId) {
        Assert.hasText(declareId, "ID不能为空");
        FeeDeclare declare = declareRepository.findFirstById(declareId);
        Assert.isTrue(FeeDeclareStatus.REJECTED.equals(declare.getStatus()), "请提交被驳回的费用申请记录");
        declare.setStatus(FeeDeclareStatus.SUBMITTED);//重新审批
        declareRepository.save(declare);
    }

    @Override
    public void passed(String declareId) {
        FeeDeclare declare = declareRepository.findFirstById(declareId);
        Assert.isTrue(FeeDeclareStatus.SUBMITTED.equals(declare.getStatus()), "请选择提交状态的费用记录进行审批");
        declare.setStatus(FeeDeclareStatus.PASSED);
        declareRepository.save(declare);
    }

    @Override
    public void rejected(FeeDeclareRejectDto rejectDto) {
        Assert.hasText(rejectDto.getDeclareId(), "ID不能为空");
        FeeDeclare declare = declareRepository.findFirstById(rejectDto.getDeclareId());
        declare.setStatus(FeeDeclareStatus.REJECTED);
        declare.setRejectReason(rejectDto.getRejectReason());
        declareRepository.save(declare);
    }

    @Override
    public void modify(FeeDeclareModifyDto modifyDto) {
        Assert.hasText(modifyDto.getId(), "ID不能为空");
        FeeDeclare declare = declareRepository.findFirstById(modifyDto.getId());
//        Assert.isTrue(FeeDeclareStatus.SUBMITED.equals(declare.getStatus()),"只能修改已提交状态的申请记录");
        TransportOrder order = orderRepository.findFirstByCindaNo(declare.getCindaNo());
        Assert.isTrue(order != null, String.format("没有找到先达单号[%s]的订单信息", declare.getCindaNo()));
        BeanUtils.copyProperties(modifyDto, declare, "id", "version");
        declare.setTransportChannel(modifyDto.getTransportChannel());
        declare.setStatus(FeeDeclareStatus.SUBMITTED);
        declareRepository.save(declare);
    }
}
