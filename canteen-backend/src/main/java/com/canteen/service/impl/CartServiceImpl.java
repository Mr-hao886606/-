package com.canteen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.common.R;
import com.canteen.entity.Cart;
import com.canteen.entity.Dish;
import com.canteen.mapper.CartMapper;
import com.canteen.mapper.DishMapper;
import com.canteen.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private DishMapper dishMapper;

    @Override
    public R addToCart(Long userId, Long dishId, Integer quantity) {
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null || dish.getStatus() == 0) {
            return R.error("菜品不存在或已下架");
        }

        // 检查是否已在购物车中
        Cart existCart = baseMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .eq(Cart::getDishId, dishId)
        );

        if (existCart != null) {
            // 更新数量
            existCart.setQuantity(existCart.getQuantity() + quantity);
            baseMapper.updateById(existCart);
        } else {
            // 新增
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setDishId(dishId);
            cart.setQuantity(quantity);
            baseMapper.insert(cart);
        }

        // 返回购物车总数
        Long count = baseMapper.selectCount(
                new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId)
        );
        return R.ok("已添加到购物车").put("cartCount", count);
    }

    @Override
    public R updateQuantity(Long cartId, Integer quantity, Long userId) {
        Cart cart = baseMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return R.error("购物车项不存在");
        }
        if (quantity <= 0) {
            baseMapper.deleteById(cartId);
            return R.ok("已移除");
        }
        cart.setQuantity(quantity);
        baseMapper.updateById(cart);
        return R.ok("数量已更新");
    }

    @Override
    public R removeFromCart(Long cartId, Long userId) {
        Cart cart = baseMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return R.error("购物车项不存在");
        }
        baseMapper.deleteById(cartId);
        return R.ok("已移除");
    }

    @Override
    public R clearCart(Long userId) {
        baseMapper.delete(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
        return R.ok("购物车已清空");
    }
}
