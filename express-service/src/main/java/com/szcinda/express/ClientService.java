package com.szcinda.express;

import com.szcinda.express.dto.CreateClientDto;
import com.szcinda.express.dto.ModifyClientDto;
import com.szcinda.express.persistence.Client;

import java.util.List;

public interface ClientService {

    List<Client> getAll();

    void create(CreateClientDto clientDto);

    void modify(ModifyClientDto clientDto);

    Client getById(String id);

    void delete(String id);
}
