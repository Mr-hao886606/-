package com.canteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.common.PageUtils;
import com.canteen.common.R;
import com.canteen.entity.Restaurant;
import com.canteen.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 餐厅Controller
 */
@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    /**
     * 前台 - 餐厅列表
     */
    @GetMapping("/list")
    public R list() {
        List<Restaurant> list = restaurantService.list(
                new LambdaQueryWrapper<Restaurant>().eq(Restaurant::getStatus, 1)
        );
        return R.ok().put("data", list);
    }

    /**
     * 后台 - 分页列表
     */
    @GetMapping("/page")
    public R page(@RequestParam(defaultValue = "1") long page,
                  @RequestParam(defaultValue = "10") long limit,
                  @RequestParam(required = false) String name) {
        LambdaQueryWrapper<Restaurant> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Restaurant::getName, name);
        }
        wrapper.orderByDesc(Restaurant::getCreateTime);

        IPage<Restaurant> p = restaurantService.page(new Page<>(page, limit), wrapper);
        return R.ok().put("data", new PageUtils(p));
    }

    /**
     * 后台 - 保存/更新
     */
    @PostMapping("/save")
    public R save(@RequestBody Restaurant restaurant) {
        restaurantService.saveOrUpdate(restaurant);
        return R.ok("保存成功");
    }

    /**
     * 后台 - 删除
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        for (Long id : ids) {
            restaurantService.removeById(id);
        }
        return R.ok("删除成功");
    }
}
