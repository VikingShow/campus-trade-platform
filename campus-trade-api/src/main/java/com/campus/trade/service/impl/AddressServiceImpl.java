package com.campus.trade.service.impl;

import com.campus.trade.dto.AddressDTO;
import com.campus.trade.entity.UserAddress;
import com.campus.trade.mapper.AddressMapper;
import com.campus.trade.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    private final AddressMapper addressMapper;

    @Autowired
    public AddressServiceImpl(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    @Override
    @Transactional
    public UserAddress addAddress(Long userId, AddressDTO addressDTO) {
        // 如果新地址要设为默认，先将该用户的所有其他地址取消默认
        if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
            addressMapper.clearDefaultAddress(userId);
        }

        UserAddress address = new UserAddress();
        // 【最终修正】将 DTO 的属性完整地复制到实体对象中
        address.setUserId(userId);
        address.setRecipientName(addressDTO.getRecipientName());
        address.setPhone(addressDTO.getPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetailedAddress(addressDTO.getDetailedAddress());
        address.setIsDefault(Boolean.TRUE.equals(addressDTO.getIsDefault()));

        addressMapper.insert(address);
        return address;
    }

    @Override
    @Transactional
    public UserAddress updateAddress(Long addressId, Long userId, AddressDTO addressDTO) {
        if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
            addressMapper.clearDefaultAddress(userId);
        }

        UserAddress address = new UserAddress();
        address.setId(addressId);
        address.setUserId(userId);
        address.setRecipientName(addressDTO.getRecipientName());
        address.setPhone(addressDTO.getPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetailedAddress(addressDTO.getDetailedAddress());
        address.setIsDefault(Boolean.TRUE.equals(addressDTO.getIsDefault()));

        addressMapper.update(address);
        return address;
    }

    @Override
    public void deleteAddress(Long addressId, Long userId) {
        addressMapper.delete(addressId, userId);
    }

    @Override
    public List<UserAddress> getUserAddresses(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long addressId, Long userId) {
        addressMapper.clearDefaultAddress(userId);
        addressMapper.setDefaultAddress(addressId, userId);
    }
}
