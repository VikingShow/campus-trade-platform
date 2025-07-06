package com.campus.trade.mapper;

import com.campus.trade.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AddressMapper {
    void insert(UserAddress address);
    void update(UserAddress address);
    void delete(@Param("addressId") Long addressId, @Param("userId") Long userId);
    List<UserAddress> findByUserId(Long userId);
    void clearDefaultAddress(Long userId);
    void setDefaultAddress(@Param("addressId") Long addressId, @Param("userId") Long userId);
}
