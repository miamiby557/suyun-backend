package com.szcinda.express.controller;

import com.szcinda.express.FeeDeclareService;
import com.szcinda.express.FeeDeclareStatus;
import com.szcinda.express.controller.dto.Result;
import com.szcinda.express.dto.*;
import com.szcinda.express.params.QueryFeeDeclareParams;
import com.szcinda.express.persistence.FeeDeclare;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feeDeclare")
public class FeeDeclareController {

    private final FeeDeclareService declareService;

    public FeeDeclareController(FeeDeclareService declareService) {
        this.declareService = declareService;
    }

    @PostMapping("/query")
    public PageResult<FeeDeclareDto> query(@RequestBody QueryFeeDeclareParams params) {
        return declareService.query(params);
    }

    @PostMapping("/querySubmit")
    public PageResult<FeeDeclareDto> querySubmit(@RequestBody QueryFeeDeclareParams params) {
        params.setStatus(FeeDeclareStatus.SUBMITTED);
        return declareService.query(params);
    }

    @GetMapping("/{declareId}")
    public Result<FeeDeclare> getById(@PathVariable String declareId) {
        return Result.success(declareService.getById(declareId));
    }

    @PostMapping("/create")
    public Result<String> create(@RequestBody FeeDeclareCreateDto createDto) {
        declareService.createDeclare(createDto);
        return Result.success();
    }

    @PostMapping("/passed")
    public Result<String> passed(@RequestBody List<String> declareIds) {
        declareIds.forEach(declareService::passed);
        return Result.success();
    }

    @PostMapping("/modify")
    public Result<String> reApproval(@RequestBody FeeDeclareModifyDto reApprovalDto) {
        declareService.modify(reApprovalDto);
        return Result.success();
    }

    @PostMapping("/reApproval")
    public Result<String> reApproval(@RequestBody List<String> declareIds) {
        declareIds.forEach(declareService::reApproval);
        return Result.success();
    }

    @PostMapping("/reject")
    public Result<String> reject(@RequestBody FeeDeclareRejectDto rejectDto) {
        declareService.rejected(rejectDto);
        return Result.success();
    }

//    @GetMapping("/getTodayPassFee")
//    public List<FeeDeclare> getTodayPassFee(){
//        return declareService.getTodayPassRecord();
//    }
}
