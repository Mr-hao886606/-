package com.canteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.canteen.common.LoginUser;
import com.canteen.common.R;
import com.canteen.entity.Cart;
import com.canteen.entity.Dish;
import com.canteen.mapper.DishMapper;
import com.canteen.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 购物车Controller
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 获取购物车列表
     */
    @GetMapping("/list")
    public R list(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        List<Cart> cartList = cartService.list(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, loginUser.getUserId())
                        .orderByDesc(Cart::getCreateTime)
        );

        // 填充菜品信息
        for (Cart cart : cartList) {
            Dish dish = dishMapper.selectById(cart.getDishId());
            if (dish != null) {
                cart.setDishName(dish.getName());
                cart.setDishImage(dish.getImage());
                cart.setPrice(dish.getPrice());
            }
        }

        return R.ok().put("data", cartList);
    }

    /**
     * 添加到购物车
     */
    @PostMapping("/add")
    public R add(@RequestParam Long dishId,
                 @RequestParam(defaultValue = "1") Integer quantity,
                 HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        return cartService.addToCart(loginUser.getUserId(), dishId, quantity);
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update/{id}")
    public R update(@PathVariable Long id,
                    @RequestParam Integer quantity,
                    HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        return cartService.updateQuantity(id, quantity, loginUser.getUserId());
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/remove/{id}")
    public R remove(@PathVariable Long id, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        return cartService.removeFromCart(id, loginUser.getUserId());
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public R clear(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        return cartService.clearCart(loginUser.getUserId());
    }

    /**
     * 获取购物车数量
     */
    @GetMapping("/count")
    public R count(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        long count = cartService.count(
                new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, loginUser.getUserId())
        );
        return R.ok().put("data", count);
    }
}
