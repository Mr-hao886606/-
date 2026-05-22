package com.canteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.common.PageUtils;
import com.canteen.common.R;
import com.canteen.entity.Banner;
import com.canteen.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 轮播图Controller
 */
@RestController
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 前台 - 轮播图列表
     */
    @GetMapping("/list")
    public R list() {
        List<Banner> list = bannerService.list(
                new LambdaQueryWrapper<Banner>()
                        .eq(Banner::getStatus, 1)
                        .orderByAsc(Banner::getSortOrder)
        );
        return R.ok().put("data", list);
    }

    /**
     * 后台 - 分页列表
     */
    @GetMapping("/page")
    public R page(@RequestParam(defaultValue = "1") long page,
                  @RequestParam(defaultValue = "10") long limit) {
        IPage<Banner> p = bannerService.page(
                new Page<>(page, limit),
                new LambdaQueryWrapper<Banner>().orderByAsc(Banner::getSortOrder)
        );
        return R.ok().put("data", new PageUtils(p));
    }

    /**
     * 后台 - 保存/更新
     */
    @PostMapping("/save")
    public R save(@RequestBody Banner banner) {
        bannerService.saveOrUpdate(banner);
        return R.ok("保存成功");
    }

    /**
     * 后台 - 删除
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        for (Long id : ids) {
            bannerService.removeById(id);
        }
        return R.ok("删除成功");
    }
}
