package com.campus.trade.service;

import com.campus.trade.dto.AddressDTO;
import com.campus.trade.entity.UserAddress;
import java.util.List;

public interface AddressService {
    UserAddress addAddress(String userId, AddressDTO addressDTO);
    UserAddress updateAddress(String addressId, String userId, AddressDTO addressDTO);
    void deleteAddress(String addressId, String userId);
    List<UserAddress> getUserAddresses(String userId);
    void setDefaultAddress(String addressId, String userId);
}
