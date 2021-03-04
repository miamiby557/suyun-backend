package com.szcinda.express;

import com.szcinda.express.dto.CreateCarrierDto;
import com.szcinda.express.dto.CreateClientDto;
import com.szcinda.express.dto.ModifyCarrierDto;
import com.szcinda.express.dto.ModifyClientDto;
import com.szcinda.express.persistence.Carrier;
import com.szcinda.express.persistence.Client;

import java.util.List;

public interface CarrierService {

    List<Carrier> getAll();

    void create(CreateCarrierDto clientDto);

    void modify(ModifyCarrierDto clientDto);

    Carrier getById(String id);

    void delete(String id);
}
