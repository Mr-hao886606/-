package com.canteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.common.LoginUser;
import com.canteen.common.PageUtils;
import com.canteen.common.R;
import com.canteen.entity.*;
import com.canteen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订单Controller
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private DishService dishService;
    @Autowired
    private UserService userService;

    /**
     * 创建订单（下单）
     */
    @PostMapping("/create")
    public R create(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        Long addressId = params.get("addressId") != null ? Long.parseLong(params.get("addressId").toString()) : null;
        Long couponId = params.get("couponId") != null ? Long.parseLong(params.get("couponId").toString()) : null;
        String remark = (String) params.get("remark");
        return orderService.createOrder(loginUser.getUserId(), addressId, couponId, remark);
    }

    /**
     * 用户 - 取消订单
     */
    @PutMapping("/cancel/{id}")
    public R cancel(@PathVariable Long id, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        return orderService.cancelOrder(id, loginUser.getUserId());
    }

    /**
     * 用户 - 我的订单列表
     */
    @GetMapping("/myOrders")
    public R myOrders(@RequestParam(defaultValue = "1") long page,
                      @RequestParam(defaultValue = "10") long limit,
                      @RequestParam(required = false) String status,
                      HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");

        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getUserId, loginUser.getUserId());
        if (status != null && !status.isEmpty()) {
            wrapper.eq(OrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(OrderInfo::getCreateTime);

        IPage<OrderInfo> p = orderService.page(new Page<>(page, limit), wrapper);

        // 填充订单明细
        for (OrderInfo order : p.getRecords()) {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
            );
            order.setItems(items);
        }

        return R.ok().put("data", new PageUtils(p));
    }

    /**
     * 订单详情
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable Long id) {
        OrderInfo order = orderService.getById(id);
        if (order != null) {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
            );
            order.setItems(items);
        }
        return R.ok().put("data", order);
    }

    // ==================== 管理员接口 ====================

    /**
     * 管理员 - 所有订单列表
     */
    @GetMapping("/page")
    public R page(@RequestParam(defaultValue = "1") long page,
                  @RequestParam(defaultValue = "10") long limit,
                  @RequestParam(required = false) String orderNo,
                  @RequestParam(required = false) String status) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(OrderInfo::getOrderNo, orderNo);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(OrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(OrderInfo::getCreateTime);

        IPage<OrderInfo> p = orderService.page(new Page<>(page, limit), wrapper);

        // 填充订单明细和用户名
        for (OrderInfo order : p.getRecords()) {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
            );
            order.setItems(items);
            User user = userService.getById(order.getUserId());
            order.setUsername(user != null ? user.getUsername() : "未知");
        }

        return R.ok().put("data", new PageUtils(p));
    }

    /**
     * 管理员 - 更新订单状态
     */
    @PutMapping("/updateStatus/{id}")
    public R updateStatus(@PathVariable Long id, @RequestParam String status) {
        return orderService.updateOrderStatus(id, status);
    }

    /**
     * 管理员 - 删除订单
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        for (Long id : ids) {
            // 先删除订单明细
            orderItemMapper.delete(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
            orderService.removeById(id);
        }
        return R.ok("删除成功");
    }
}
