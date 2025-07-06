package com.campus.trade.service;

import com.campus.trade.dto.AddressDTO;
import com.campus.trade.entity.UserAddress;
import java.util.List;

public interface AddressService {
    UserAddress addAddress(Long userId, AddressDTO addressDTO);
    UserAddress updateAddress(Long addressId, Long userId, AddressDTO addressDTO);
    void deleteAddress(Long addressId, Long userId);
    List<UserAddress> getUserAddresses(Long userId);
    void setDefaultAddress(Long addressId, Long userId);
}
