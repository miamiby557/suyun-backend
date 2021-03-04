package com.szcinda.express.controller;

import com.szcinda.express.CarrierService;
import com.szcinda.express.configuration.UserLoginToken;
import com.szcinda.express.controller.dto.Result;
import com.szcinda.express.dto.CreateCarrierDto;
import com.szcinda.express.dto.ModifyCarrierDto;
import com.szcinda.express.persistence.Carrier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carrier")
public class CarrierController {

    private final CarrierService carrierService;

    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @GetMapping("/query")
    public List<Carrier> getAll() {
        return carrierService.getAll();
    }

    @PostMapping("/create")
    public Result create(@RequestBody CreateCarrierDto carrierDto) {
        carrierService.create(carrierDto);
        return Result.success();
    }

    @PostMapping("/modify")
    public Result modify(@RequestBody ModifyCarrierDto carrierDto) {
        carrierService.modify(carrierDto);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Carrier> getById(@PathVariable String id) {
        return Result.success(carrierService.getById(id));
    }

    @GetMapping("/delete/{id}")
    public Result delete(@PathVariable String id) {
        carrierService.delete(id);
        return Result.success();
    }
}
