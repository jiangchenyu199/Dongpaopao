package com.cy.controller.app;

import com.alibaba.fastjson2.JSONObject;
import com.cy.common.R;
import com.cy.service.app.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/add")
    public R addAddress(@RequestBody JSONObject params) {
        return addressService.addAddress(params);
    }

    @DeleteMapping("/delete")
    public R deleteAddress(@RequestBody JSONObject params) {
        return addressService.deleteAddress(params);
    }

    @PutMapping("/update")
    public R updateAddress(@RequestBody JSONObject params) {
        return addressService.updateAddress(params);
    }

    @GetMapping("/list")
    public R list(@RequestParam("uid") String uid) {
        return addressService.list(uid);
    }
}
