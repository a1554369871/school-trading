package com.trading.mapper;

import com.trading.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {

    List<Address> findByUserId(@Param("userId") Integer userId);

    Address findById(@Param("id") Integer id);

    int insert(Address address);

    int update(Address address);

    int deleteById(@Param("id") Integer id);

    int clearDefault(@Param("userId") Integer userId);
}
