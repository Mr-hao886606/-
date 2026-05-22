package com.canteen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.common.R;
import com.canteen.entity.Coupon;

public interface CouponService extends IService<Coupon> {
    R receiveCoupon(Long userId, Long couponId);
    R getUserCoupons(Long userId);
}
