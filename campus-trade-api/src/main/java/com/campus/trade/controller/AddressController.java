package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.AddressDTO;
import com.campus.trade.entity.UserAddress;
import com.campus.trade.security.AuthenticatedUser;
import com.campus.trade.service.AddressService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/me/addresses")
@PreAuthorize("isAuthenticated()")
public class AddressController {
    private final AddressService addressService;
    public AddressController(AddressService addressService) { this.addressService = addressService; }

    @PostMapping
    public Result<UserAddress> add(@RequestBody AddressDTO addressDTO, @AuthenticationPrincipal AuthenticatedUser user) {
        return Result.success(addressService.addAddress(Long.valueOf(user.getUserId()), addressDTO));
    }
    @GetMapping
    public Result<List<UserAddress>> list(@AuthenticationPrincipal AuthenticatedUser user) {
        return Result.success(addressService.getUserAddresses(Long.valueOf(user.getUserId())));
    }
    @PutMapping("/{id}")
    public Result<UserAddress> update(@PathVariable String id, @RequestBody AddressDTO addressDTO, @AuthenticationPrincipal AuthenticatedUser user) {
        return Result.success(addressService.updateAddress(Long.valueOf(id), Long.valueOf(user.getUserId()), addressDTO));
    }
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id, @AuthenticationPrincipal AuthenticatedUser user) {
        addressService.deleteAddress(Long.valueOf(id), Long.valueOf(user.getUserId()));
        return Result.success();
    }
    @PostMapping("/{id}/set-default")
    public Result<Void> setDefault(@PathVariable String id, @AuthenticationPrincipal AuthenticatedUser user) {
        addressService.setDefaultAddress(Long.valueOf(id), Long.valueOf(user.getUserId()));
        return Result.success();
    }
}
