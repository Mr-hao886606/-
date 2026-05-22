package com.canteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.canteen.common.LoginUser;
import com.canteen.common.R;
import com.canteen.entity.Address;
import com.canteen.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 收货地址Controller
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取用户的所有地址
     */
    @GetMapping("/list")
    public R list(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        List<Address> list = addressService.list(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, loginUser.getUserId())
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getCreateTime)
        );
        return R.ok().put("data", list);
    }

    /**
     * 获取单个地址
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable Long id) {
        return R.ok().put("data", addressService.getById(id));
    }

    /**
     * 新增/更新地址
     */
    @PostMapping("/save")
    public R save(@RequestBody Address address, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        address.setUserId(loginUser.getUserId());

        // 如果设为默认，取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            Address updateDefault = new Address();
            updateDefault.setIsDefault(0);
            addressService.update(updateDefault,
                    new LambdaQueryWrapper<Address>()
                            .eq(Address::getUserId, loginUser.getUserId())
                            .eq(Address::getIsDefault, 1)
            );
        }

        addressService.saveOrUpdate(address);
        return R.ok("保存成功");
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id) {
        addressService.removeById(id);
        return R.ok("删除成功");
    }
}
