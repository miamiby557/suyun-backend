package com.szcinda.express.controller;

import com.szcinda.express.CarrierService;
import com.szcinda.express.ClientService;
import com.szcinda.express.OrderService;
import com.szcinda.express.UserService;
import com.szcinda.express.configuration.UserLoginToken;
import com.szcinda.express.controller.dto.Result;
import com.szcinda.express.dto.DashboardDto;
import com.szcinda.express.dto.UserIdentity;
import com.szcinda.express.persistence.Carrier;
import com.szcinda.express.persistence.Client;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/home")
public class HomeController {

    private final UserService userService;
    private final OrderService orderService;
    private final CarrierService carrierService;
    private final ClientService clientService;

    public HomeController(UserService userService, OrderService orderService, CarrierService carrierService, ClientService clientService) {
        this.userService = userService;
        this.orderService = orderService;
        this.carrierService = carrierService;
        this.clientService = clientService;
    }

    @GetMapping("/")
    public String hello() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @GetMapping("/dashboard")
    public Result<DashboardDto> dashboardData() {
        DashboardDto dashboardDto = orderService.generateDayCount();
        return Result.success(dashboardDto);
    }

    @GetMapping("/generateDayFee")
    public Result<DashboardDto> generateDayFee() {
        DashboardDto dashboardDto = orderService.generateDayFee();
        return Result.success(dashboardDto);
    }

    @GetMapping("/monthOrderCount")
    public Result<DashboardDto> monthOrderCount() {
        DashboardDto dashboardDto = orderService.monthOrderCount();
        return Result.success(dashboardDto);
    }

    @UserLoginToken
    @GetMapping("/clientList")
    public List<String> clientList() {
        return clientService.getAll().stream().map(Client::getNickName).collect(Collectors.toList());
    }

    @UserLoginToken
    @GetMapping("/carrierList")
    public List<String> carrierList() {
        return carrierService.getAll().stream().map(Carrier::getNickName).collect(Collectors.toList());
    }

    @PostMapping("/authenticate")
    public Result<UserIdentity> login(@RequestParam("account") String account, @RequestParam("password") String password) {
        UserIdentity identity = userService.getUserIdentity(account, password);
        String token = userService.getToken(identity.getId(), identity.getPassword());
        identity.setToken(token);
        return Result.success(identity);
    }


}
