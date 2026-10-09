package com.trading.service;

import com.trading.entity.Address;
import java.util.List;

public interface AddressService {
    List<Address> getByUserId(Integer userId);
    Address getById(Integer id);
    Address create(Integer userId, Address address);
    Address update(Integer userId, Integer addressId, Address address);
    void delete(Integer userId, Integer addressId);
}
