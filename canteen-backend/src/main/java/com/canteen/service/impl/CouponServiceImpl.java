package com.canteen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.common.R;
import com.canteen.entity.Coupon;
import com.canteen.entity.UserCoupon;
import com.canteen.mapper.CouponMapper;
import com.canteen.mapper.UserCouponMapper;
import com.canteen.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Override
    public R receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = baseMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() == 0) {
            return R.error("优惠券不存在或已下架");
        }
        if (coupon.getRemainCount() != null && coupon.getRemainCount() <= 0) {
            return R.error("优惠券已被领完");
        }
        // 检查是否重复领取
        Long count = userCouponMapper.selectCount(
                new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId)
                        .eq(UserCoupon::getCouponId, couponId)
        );
        if (count > 0) {
            return R.error("已领取过该优惠券");
        }

        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        uc.setStatus(1);
        userCouponMapper.insert(uc);

        // 更新库存
        coupon.setRemainCount(coupon.getRemainCount() - 1);
        baseMapper.updateById(coupon);

        return R.ok("领取成功");
    }

    @Override
    public R getUserCoupons(Long userId) {
        List<UserCoupon> ucList = userCouponMapper.selectList(
                new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId)
                        .eq(UserCoupon::getStatus, 1)
        );
        List<Coupon> coupons = ucList.stream().map(uc -> {
            Coupon c = baseMapper.selectById(uc.getCouponId());
            if (c != null) {
                // 检查是否过期
                if (c.getEndTime() != null && c.getEndTime().before(new Date())) {
                    uc.setStatus(2);
                    userCouponMapper.updateById(uc);
                    return null;
                }
            }
            return c;
        }).filter(c -> c != null).collect(Collectors.toList());

        return R.ok().put("data", coupons);
    }
}
