package com.canteen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.common.R;
import com.canteen.entity.OrderInfo;

import java.util.List;
import java.util.Map;

public interface OrderService extends IService<OrderInfo> {
    R createOrder(Long userId, Long addressId, Long couponId, String remark);
    R cancelOrder(Long orderId, Long userId);
    R updateOrderStatus(Long orderId, String status);
}
