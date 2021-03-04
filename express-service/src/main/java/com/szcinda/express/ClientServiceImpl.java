package com.szcinda.express;

import com.szcinda.express.dto.CreateClientDto;
import com.szcinda.express.dto.ModifyClientDto;
import com.szcinda.express.persistence.Client;
import com.szcinda.express.persistence.ClientRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {


    private final ClientRepository clientRepository;
    private final SnowFlakeFactory snowFlakeFactory;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        this.snowFlakeFactory = SnowFlakeFactory.getInstance();
    }

    @Override
    public List<Client> getAll() {
        return clientRepository.findAll();
    }

    @Override
    public void create(CreateClientDto clientDto) {
        List<Client> clients = clientRepository.findByNameIn(Collections.singleton(clientDto.getName()));
        Assert.isTrue(clients.isEmpty(),"存在此名称["+clientDto.getName()+"]的客户");
        Client client = new Client();
        BeanUtils.copyProperties(clientDto, client);
        client.setId(snowFlakeFactory.nextId());
        if(StringUtils.isEmpty(client.getNickName())){
            client.setNickName(client.getName());
        }
        clientRepository.save(client);
    }

    @Override
    public void modify(ModifyClientDto clientDto) {
        Client client = clientRepository.findFirstById(clientDto.getId());
        Assert.notNull(client, String.format("没有找到ID[%s]的客户记录", clientDto.getId()));
        client.setEmail(clientDto.getEmail());
        client.setNickName(clientDto.getNickName());
        if(StringUtils.isEmpty(client.getNickName())){
            client.setNickName(client.getName());
        }
        clientRepository.save(client);
    }

    @Override
    public Client getById(String id) {
        return clientRepository.findFirstById(id);
    }

    @Override
    public void delete(String id) {
        clientRepository.deleteById(id);
    }
}
