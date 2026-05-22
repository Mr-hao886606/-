package com.canteen.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.common.R;
import com.canteen.entity.*;
import com.canteen.mapper.*;
import com.canteen.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private UserCouponMapper userCouponMapper;

    @Override
    @Transactional
    public R createOrder(Long userId, Long addressId, Long couponId, String remark) {
        // 1. 获取购物车中的所有商品
        List<Cart> cartList = cartMapper.selectList(
                new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId)
        );
        if (cartList.isEmpty()) {
            return R.error("购物车为空，请先添加菜品");
        }

        // 2. 计算总金额并生成订单明细
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Cart cart : cartList) {
            Dish dish = dishMapper.selectById(cart.getDishId());
            if (dish == null || dish.getStatus() == 0) {
                return R.error("菜品【" + (dish != null ? dish.getName() : "未知") + "】已下架");
            }
            BigDecimal subtotal = dish.getPrice().multiply(new BigDecimal(cart.getQuantity()));
            totalPrice = totalPrice.add(subtotal);
        }

        // 3. 处理优惠券
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponId != null) {
            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon == null) {
                return R.error("优惠券不存在");
            }
            if (coupon.getRemainCount() != null && coupon.getRemainCount() <= 0) {
                return R.error("优惠券已被领完");
            }
            if (coupon.getMinAmount() != null && totalPrice.compareTo(coupon.getMinAmount()) < 0) {
                return R.error("未达到优惠券使用门槛（满" + coupon.getMinAmount() + "元可用）");
            }
            discountAmount = coupon.getDiscountAmount();
            // 更新优惠券剩余数量
            coupon.setRemainCount(coupon.getRemainCount() - 1);
            couponMapper.updateById(coupon);
        }

        BigDecimal actualPrice = totalPrice.subtract(discountAmount);
        if (actualPrice.compareTo(BigDecimal.ZERO) < 0) {
            actualPrice = BigDecimal.ZERO;
        }

        // 4. 创建订单
        OrderInfo order = new OrderInfo();
        order.setOrderNo(IdUtil.fastSimpleUUID().substring(0, 20).toUpperCase());
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setCouponId(couponId);
        order.setDiscountAmount(discountAmount);
        order.setActualPrice(actualPrice);
        order.setPayType("offline");
        order.setStatus("pending");
        order.setAddressId(addressId);
        order.setRemark(remark);
        order.setCreateTime(new Date());
        baseMapper.insert(order);

        // 5. 创建订单明细
        for (Cart cart : cartList) {
            Dish dish = dishMapper.selectById(cart.getDishId());
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setDishId(cart.getDishId());
            item.setDishName(dish.getName());
            item.setDishImage(dish.getImage());
            item.setPrice(dish.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(dish.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            orderItemMapper.insert(item);
        }

        // 6. 清空购物车
        cartMapper.delete(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));

        return R.ok("下单成功").put("data", order);
    }

    @Override
    @Transactional
    public R cancelOrder(Long orderId, Long userId) {
        OrderInfo order = baseMapper.selectById(orderId);
        if (order == null) {
            return R.error("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return R.error("无权操作此订单");
        }
        if (!"pending".equals(order.getStatus())) {
            return R.error("只有待处理状态的订单才能取消");
        }
        order.setStatus("cancelled");
        order.setUpdateTime(new Date());
        baseMapper.updateById(order);
        return R.ok("订单已取消");
    }

    @Override
    public R updateOrderStatus(Long orderId, String status) {
        OrderInfo order = baseMapper.selectById(orderId);
        if (order == null) {
            return R.error("订单不存在");
        }
        order.setStatus(status);
        order.setUpdateTime(new Date());
        baseMapper.updateById(order);
        return R.ok("状态更新成功");
    }
}
