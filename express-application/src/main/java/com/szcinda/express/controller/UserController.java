package com.szcinda.express.controller;

import com.szcinda.express.RoleType;
import com.szcinda.express.UserService;
import com.szcinda.express.configuration.UserLoginToken;
import com.szcinda.express.controller.dto.Result;
import com.szcinda.express.dto.PageResult;
import com.szcinda.express.dto.UpdatePasswordDto;
import com.szcinda.express.dto.UserCreateDto;
import com.szcinda.express.dto.UserUpdateDto;
import com.szcinda.express.params.QueryUserParams;
import com.szcinda.express.persistence.User;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @UserLoginToken
    @PostMapping("/query")
    public PageResult<User> query(@RequestBody QueryUserParams params) {
        return userService.query(params);
    }

    @UserLoginToken
    @PostMapping("/create")
    public Result create(@RequestBody UserCreateDto createDto) {
        userService.createUser(createDto);
        return Result.success();
    }

    @UserLoginToken
    @PostMapping("/update")
    public Result update(@RequestBody UserUpdateDto updateDto) {
        userService.updateUser(updateDto);
        return Result.success();
    }

    @GetMapping("/initAdmin")
    public Result initAdmin() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setAccount("admin");
        createDto.setEmail("test@szcinda.com");
        createDto.setPassword("admin");
        createDto.setName("超级管理员");
        createDto.setRole(RoleType.BOSS);
        userService.createUser(createDto);
        return Result.success();
    }

    @GetMapping("getEncodePassword/{password}")
    public Result getEncodePassword(@PathVariable String password) {
        return Result.success(userService.generatePwd(password));
    }

    @UserLoginToken
    @PostMapping("/modifyPassword")
    public Result modifyPassword(@RequestBody UpdatePasswordDto updatePasswordDto) {
        userService.updatePassword(updatePasswordDto);
        return Result.success();
    }

    @UserLoginToken
    @GetMapping("/{userId}")
    public Result getUser(@PathVariable String userId) {
        User user = userService.findUserById(userId);
        return Result.success(user);
    }

    @UserLoginToken
    @GetMapping("/delete/{userId}")
    public Result delete(@PathVariable String userId) {
        userService.delete(userId);
        return Result.success();
    }
}
