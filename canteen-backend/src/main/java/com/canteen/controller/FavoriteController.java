package com.canteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.common.LoginUser;
import com.canteen.common.R;
import com.canteen.entity.Dish;
import com.canteen.entity.Favorite;
import com.canteen.service.DishService;
import com.canteen.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏Controller
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private DishService dishService;

    /**
     * 切换收藏状态
     */
    @PostMapping("/toggle/{dishId}")
    public R toggle(@PathVariable Long dishId, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        return favoriteService.toggleFavorite(loginUser.getUserId(), dishId);
    }

    /**
     * 我的收藏列表
     */
    @GetMapping("/list")
    public R list(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        List<Favorite> favorites = favoriteService.list(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, loginUser.getUserId())
                        .orderByDesc(Favorite::getCreateTime)
        );
        List<Long> dishIds = favorites.stream().map(Favorite::getDishId).collect(Collectors.toList());
        if (dishIds.isEmpty()) {
            return R.ok().put("data", java.util.Collections.emptyList());
        }
        List<Dish> dishes = dishService.listByIds(dishIds);
        return R.ok().put("data", dishes);
    }
}
