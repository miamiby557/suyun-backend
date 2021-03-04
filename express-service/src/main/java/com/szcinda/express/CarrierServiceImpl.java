package com.szcinda.express;

import com.szcinda.express.dto.CreateCarrierDto;
import com.szcinda.express.dto.ModifyCarrierDto;
import com.szcinda.express.persistence.Carrier;
import com.szcinda.express.persistence.CarrierRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class CarrierServiceImpl implements CarrierService {

    private final CarrierRepository carrierRepository;
    private final SnowFlakeFactory snowFlakeFactory;

    public CarrierServiceImpl(CarrierRepository carrierRepository) {
        this.carrierRepository = carrierRepository;
        this.snowFlakeFactory = SnowFlakeFactory.getInstance();
    }

    @Override
    public List<Carrier> getAll() {
        return carrierRepository.findAll();
    }

    @Override
    public void create(CreateCarrierDto carrierDto) {
        List<Carrier> carriers = carrierRepository.findByNameIn(Collections.singleton(carrierDto.getName()));
        Assert.isTrue(carriers.isEmpty(),"存在此名称["+carrierDto.getName()+"]的承运商");
        Carrier carrier = new Carrier();
        BeanUtils.copyProperties(carrierDto, carrier);
        carrier.setId(snowFlakeFactory.nextId());
        if (StringUtils.isEmpty(carrier.getNickName())) {
            carrier.setNickName(carrier.getName());
        }
        carrierRepository.save(carrier);
    }

    @Override
    public void modify(ModifyCarrierDto carrierDto) {
        Carrier carrier = carrierRepository.findFirstById(carrierDto.getId());
        Assert.notNull(carrier, String.format("没有找到ID[%s]的承运商记录", carrierDto.getId()));
        carrier.setEmail(carrierDto.getEmail());
        carrier.setNickName(carrierDto.getNickName());
        if (StringUtils.isEmpty(carrier.getNickName())) {
            carrier.setNickName(carrier.getName());
        }
        carrierRepository.save(carrier);
    }

    @Override
    public Carrier getById(String id) {
        return carrierRepository.findFirstById(id);
    }

    @Override
    public void delete(String id) {
        carrierRepository.deleteById(id);
    }
}
