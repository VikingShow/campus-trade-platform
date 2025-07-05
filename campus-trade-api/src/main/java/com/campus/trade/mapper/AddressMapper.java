package com.campus.trade.mapper;

import com.campus.trade.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AddressMapper {
    void insert(UserAddress address);
    void update(UserAddress address);
    void delete(@Param("addressId") String addressId, @Param("userId") String userId);
    List<UserAddress> findByUserId(String userId);
    void clearDefaultAddress(String userId);
    void setDefaultAddress(@Param("addressId") String addressId, @Param("userId") String userId);
}
