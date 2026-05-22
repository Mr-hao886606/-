package com.canteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.common.PageUtils;
import com.canteen.common.R;
import com.canteen.entity.FoodCategory;
import com.canteen.service.FoodCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 美食分类Controller
 */
@RestController
@RequestMapping("/foodCategory")
public class FoodCategoryController {

    @Autowired
    private FoodCategoryService foodCategoryService;

    /**
     * 前台/后台 - 分类列表
     */
    @GetMapping("/list")
    public R list() {
        List<FoodCategory> list = foodCategoryService.list(
                new LambdaQueryWrapper<FoodCategory>().orderByAsc(FoodCategory::getSortOrder)
        );
        return R.ok().put("data", list);
    }

    /**
     * 后台 - 分页列表
     */
    @GetMapping("/page")
    public R page(@RequestParam(defaultValue = "1") long page,
                  @RequestParam(defaultValue = "10") long limit) {
        IPage<FoodCategory> p = foodCategoryService.page(
                new Page<>(page, limit),
                new LambdaQueryWrapper<FoodCategory>().orderByAsc(FoodCategory::getSortOrder)
        );
        return R.ok().put("data", new PageUtils(p));
    }

    /**
     * 后台 - 保存/更新
     */
    @PostMapping("/save")
    public R save(@RequestBody FoodCategory category) {
        foodCategoryService.saveOrUpdate(category);
        return R.ok("保存成功");
    }

    /**
     * 后台 - 删除
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        for (Long id : ids) {
            foodCategoryService.removeById(id);
        }
        return R.ok("删除成功");
    }
}
