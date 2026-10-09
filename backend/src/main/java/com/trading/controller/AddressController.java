package com.trading.controller;

import com.trading.common.Result;
import com.trading.entity.Address;
import com.trading.service.impl.AddressServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressServiceImpl addressService;

    /**
     * 我的收货地址列表 — 个人中心查看当前用户的所有收货地址，默认地址排在前面
     * → AddressServiceImpl.getByUserId() → AddressMapper.xml: findByUserId
     */
    @GetMapping
    public Result<List<Address>> myAddresses(Authentication auth) {
        return Result.success(addressService.getByUserId((Integer) auth.getPrincipal()));
    }

    /**
     * 地址详情 — 查看指定收货地址的详细信息（收货人、手机号、地址等），仅允许访问自己的地址
     * → AddressServiceImpl.getById() → AddressMapper.xml: findById
     */
    @GetMapping("/{id}")
    public Result<Address> getById(Authentication auth, @PathVariable Integer id) {
        Address address = addressService.getById(id);
        // 校验地址归属
        if (!address.getUserId().equals((Integer) auth.getPrincipal())) {
            return Result.error(403, "无权访问此地址");
        }
        return Result.success(address);
    }

    /**
     * 新增收货地址 — 用户添加新的收货地址，支持设置为默认地址（设为默认时清除其他默认）
     * → AddressServiceImpl.create() → AddressMapper.xml: clearDefault(设默认时) + insert
     */
    @PostMapping
    public Result<Address> create(Authentication auth, @RequestBody Address address) {
        return Result.success(addressService.create((Integer) auth.getPrincipal(), address));
    }

    /**
     * 修改收货地址 — 用户编辑已有地址信息，支持修改默认地址状态
     * → AddressServiceImpl.update() → AddressMapper.xml: findById + clearDefault(设默认时) + update
     */
    @PutMapping("/{id}")
    public Result<Address> update(Authentication auth,
                                  @PathVariable Integer id,
                                  @RequestBody Address address) {
        return Result.success(addressService.update((Integer) auth.getPrincipal(), id, address));
    }

    /**
     * 删除收货地址 — 用户删除不需要的收货地址
     * → AddressServiceImpl.delete() → AddressMapper.xml: findById + deleteById
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(Authentication auth, @PathVariable Integer id) {
        addressService.delete((Integer) auth.getPrincipal(), id);
        return Result.success();
    }
}
