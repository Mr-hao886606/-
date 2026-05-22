package com.canteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.common.LoginUser;
import com.canteen.common.PageUtils;
import com.canteen.common.R;
import com.canteen.entity.Dish;
import com.canteen.entity.FoodCategory;
import com.canteen.entity.Restaurant;
import com.canteen.service.DishService;
import com.canteen.service.FavoriteService;
import com.canteen.service.FoodCategoryService;
import com.canteen.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 菜品Controller
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private FoodCategoryService foodCategoryService;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private FavoriteService favoriteService;

    /**
     * 前台 - 菜品列表（带搜索和筛选）
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        long current = params.get("page") != null ? Long.parseLong(params.get("page").toString()) : 1;
        long size = params.get("limit") != null ? Long.parseLong(params.get("limit").toString()) : 10;
        String keyword = (String) params.get("keyword");
        String taste = (String) params.get("taste");
        String categoryId = (String) params.get("categoryId");
        String restaurantId = (String) params.get("restaurantId");
        String minPrice = (String) params.get("minPrice");
        String maxPrice = (String) params.get("maxPrice");

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getStatus, 1); // 只显示上架的菜品

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Dish::getName, keyword)
                             .or().like(Dish::getDescription, keyword));
        }
        if (taste != null && !taste.isEmpty()) {
            wrapper.eq(Dish::getTaste, taste);
        }
        if (categoryId != null && !categoryId.isEmpty()) {
            wrapper.eq(Dish::getCategoryId, Long.parseLong(categoryId));
        }
        if (restaurantId != null && !restaurantId.isEmpty()) {
            wrapper.eq(Dish::getRestaurantId, Long.parseLong(restaurantId));
        }
        if (minPrice != null && !minPrice.isEmpty()) {
            wrapper.ge(Dish::getPrice, new java.math.BigDecimal(minPrice));
        }
        if (maxPrice != null && !maxPrice.isEmpty()) {
            wrapper.le(Dish::getPrice, new java.math.BigDecimal(maxPrice));
        }

        wrapper.orderByDesc(Dish::getClickCount);

        IPage<Dish> page = dishService.page(new Page<>(current, size), wrapper);

        // 填充关联数据
        for (Dish dish : page.getRecords()) {
            if (dish.getCategoryId() != null) {
                FoodCategory cat = foodCategoryService.getById(dish.getCategoryId());
                dish.setCategoryName(cat != null ? cat.getName() : "");
            }
            if (dish.getRestaurantId() != null) {
                Restaurant rest = restaurantService.getById(dish.getRestaurantId());
                dish.setRestaurantName(rest != null ? rest.getName() : "");
            }
        }

        return R.ok().put("data", new PageUtils(page));
    }

    /**
     * 前台 - 菜品详情
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable Long id, HttpServletRequest request) {
        Dish dish = dishService.getById(id);
        if (dish == null || dish.getStatus() == 0) {
            return R.error("菜品不存在或已下架");
        }

        // 增加点击量
        dish.setClickCount(dish.getClickCount() + 1);
        dishService.updateById(dish);

        // 填充关联信息
        if (dish.getCategoryId() != null) {
            FoodCategory cat = foodCategoryService.getById(dish.getCategoryId());
            dish.setCategoryName(cat != null ? cat.getName() : "");
        }
        if (dish.getRestaurantId() != null) {
            Restaurant rest = restaurantService.getById(dish.getRestaurantId());
            dish.setRestaurantName(rest != null ? rest.getName() : "");
        }

        // 判断是否已收藏
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        if (loginUser != null) {
            dish.setIsFavorite(favoriteService.isFavorite(loginUser.getUserId(), id));
        }

        return R.ok().put("data", dish);
    }

    // ==================== 管理员接口 ====================

    /**
     * 后台 - 菜品分页列表
     */
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params) {
        long current = params.get("page") != null ? Long.parseLong(params.get("page").toString()) : 1;
        long size = params.get("limit") != null ? Long.parseLong(params.get("limit").toString()) : 10;
        String name = (String) params.get("name");

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Dish::getName, name);
        }
        wrapper.orderByDesc(Dish::getCreateTime);

        IPage<Dish> page = dishService.page(new Page<>(current, size), wrapper);
        for (Dish dish : page.getRecords()) {
            if (dish.getCategoryId() != null) {
                FoodCategory cat = foodCategoryService.getById(dish.getCategoryId());
                dish.setCategoryName(cat != null ? cat.getName() : "");
            }
            if (dish.getRestaurantId() != null) {
                Restaurant rest = restaurantService.getById(dish.getRestaurantId());
                dish.setRestaurantName(rest != null ? rest.getName() : "");
            }
        }

        return R.ok().put("data", new PageUtils(page));
    }

    /**
     * 后台 - 保存/更新菜品
     */
    @PostMapping("/save")
    public R save(@RequestBody Dish dish) {
        dishService.saveOrUpdate(dish);
        return R.ok("保存成功");
    }

    /**
     * 后台 - 删除菜品
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        for (Long id : ids) {
            dishService.removeById(id);
        }
        return R.ok("删除成功");
    }

    /**
     * 获取热门菜品（按销量排序）
     */
    @GetMapping("/hot")
    public R hot() {
        List<Dish> list = dishService.list(
                new LambdaQueryWrapper<Dish>()
                        .eq(Dish::getStatus, 1)
                        .orderByDesc(Dish::getSalesCount)
                        .last("LIMIT 8")
        );
        return R.ok().put("data", list);
    }
}
