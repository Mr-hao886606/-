package com.canteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.common.LoginUser;
import com.canteen.common.PageUtils;
import com.canteen.common.R;
import com.canteen.entity.Coupon;
import com.canteen.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 优惠券Controller
 */
@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * 用户 - 领取优惠券
     */
    @PostMapping("/receive/{id}")
    public R receive(@PathVariable Long id, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        return couponService.receiveCoupon(loginUser.getUserId(), id);
    }

    /**
     * 用户 - 我的优惠券
     */
    @GetMapping("/myCoupons")
    public R myCoupons(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        return couponService.getUserCoupons(loginUser.getUserId());
    }

    /**
     * 前台 - 可领取的优惠券列表
     */
    @GetMapping("/list")
    public R list() {
        return R.ok().put("data", couponService.list(
                new LambdaQueryWrapper<Coupon>()
                        .eq(Coupon::getStatus, 1)
                        .gt(Coupon::getRemainCount, 0)
                        .orderByDesc(Coupon::getCreateTime)
        ));
    }

    // ==================== 管理员接口 ====================

    /**
     * 后台 - 分页列表
     */
    @GetMapping("/page")
    public R page(@RequestParam(defaultValue = "1") long page,
                  @RequestParam(defaultValue = "10") long limit,
                  @RequestParam(required = false) String name) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Coupon::getName, name);
        }
        wrapper.orderByDesc(Coupon::getCreateTime);
        IPage<Coupon> p = couponService.page(new Page<>(page, limit), wrapper);
        return R.ok().put("data", new PageUtils(p));
    }

    /**
     * 后台 - 保存/更新
     */
    @PostMapping("/save")
    public R save(@RequestBody Coupon coupon) {
        couponService.saveOrUpdate(coupon);
        return R.ok("保存成功");
    }

    /**
     * 后台 - 删除
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        for (Long id : ids) {
            couponService.removeById(id);
        }
        return R.ok("删除成功");
    }
}
