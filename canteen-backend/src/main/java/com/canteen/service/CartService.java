package com.canteen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.common.R;
import com.canteen.entity.Cart;

public interface CartService extends IService<Cart> {
    R addToCart(Long userId, Long dishId, Integer quantity);
    R updateQuantity(Long cartId, Integer quantity, Long userId);
    R removeFromCart(Long cartId, Long userId);
    R clearCart(Long userId);
}
