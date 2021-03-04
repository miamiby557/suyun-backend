package com.szcinda.express.controller;

import com.szcinda.express.ProfileService;
import com.szcinda.express.configuration.UserLoginToken;
import com.szcinda.express.controller.dto.ProfileByNameDto;
import com.szcinda.express.controller.dto.Result;
import com.szcinda.express.dto.PageResult;
import com.szcinda.express.dto.ProfileCreateDto;
import com.szcinda.express.dto.ProfileUpdateDto;
import com.szcinda.express.params.QueryProfileParams;
import com.szcinda.express.persistence.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @UserLoginToken
    @PostMapping("/query")
    public PageResult<Profile> query(@RequestBody QueryProfileParams params) {
        return profileService.query(params);
    }

    @UserLoginToken
    @PostMapping("/create")
    public Result createOrder(@RequestBody ProfileCreateDto createDto) {
        profileService.create(createDto);
        return Result.success();
    }

    @UserLoginToken
    @PostMapping("/update")
    public Result update(@RequestBody ProfileUpdateDto updateDto) {
        profileService.update(updateDto);
        return Result.success();
    }

    @UserLoginToken
    @GetMapping("/delete/{id}")
    public Result delete(@PathVariable String id) {
        profileService.delete(id);
        return Result.success();
    }

    @UserLoginToken
    @GetMapping("/{id}")
    public Result<Profile> getById(@PathVariable String id) {
        return Result.success(profileService.getById(id));
    }

    @UserLoginToken
    @PostMapping("/findByName")
    public Result findByName(@RequestBody ProfileByNameDto dto) {
        return Result.success(profileService.findByName(dto.getName()));
    }
}

