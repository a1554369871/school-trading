package com.trading.service.impl;

import com.trading.common.BusinessException;
import com.trading.entity.Address;
import com.trading.mapper.AddressMapper;
import com.trading.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public List<Address> getByUserId(Integer userId) {
        return addressMapper.findByUserId(userId);
    }

    @Override
    public Address getById(Integer id) {
        Address address = addressMapper.findById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        return address;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Address create(Integer userId, Address address) {
        address.setUserId(userId);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefault(userId);
        }
        addressMapper.insert(address);
        return address;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Address update(Integer userId, Integer addressId, Address address) {
        Address existing = addressMapper.findById(addressId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权修改");
        }
        address.setId(addressId);
        address.setUserId(userId);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefault(userId);
        }
        addressMapper.update(address);
        return addressMapper.findById(addressId);
    }

    @Override
    public void delete(Integer userId, Integer addressId) {
        Address existing = addressMapper.findById(addressId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在或无权删除");
        }
        addressMapper.deleteById(addressId);
    }
}
