package com.szcinda.express.controller;

import com.szcinda.express.ClientService;
import com.szcinda.express.configuration.UserLoginToken;
import com.szcinda.express.controller.dto.Result;
import com.szcinda.express.dto.CreateClientDto;
import com.szcinda.express.dto.ModifyClientDto;
import com.szcinda.express.persistence.Client;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }


    @GetMapping("/query")
    public List<Client> getAll() {
        return clientService.getAll();
    }

    @PostMapping("/create")
    public Result create(@RequestBody CreateClientDto clientDto) {
        clientService.create(clientDto);
        return Result.success();
    }

    @PostMapping("/modify")
    public Result modify(@RequestBody ModifyClientDto clientDto) {
        clientService.modify(clientDto);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Client> getById(@PathVariable String id) {
        return Result.success(clientService.getById(id));
    }

    @GetMapping("/delete/{id}")
    public Result delete(@PathVariable String id) {
        clientService.delete(id);
        return Result.success();
    }
}
