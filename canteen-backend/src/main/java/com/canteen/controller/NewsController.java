package com.canteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.common.PageUtils;
import com.canteen.common.R;
import com.canteen.entity.News;
import com.canteen.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 美食资讯Controller
 */
@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    /**
     * 前台 - 资讯列表
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") long page,
                  @RequestParam(defaultValue = "10") long limit,
                  @RequestParam(required = false) String category) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(News::getStatus, 1);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(News::getCategory, category);
        }
        wrapper.orderByDesc(News::getCreateTime);

        IPage<News> p = newsService.page(new Page<>(page, limit), wrapper);
        return R.ok().put("data", new PageUtils(p));
    }

    /**
     * 前台 - 资讯详情
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable Long id) {
        News news = newsService.getById(id);
        if (news != null) {
            news.setClickCount(news.getClickCount() + 1);
            newsService.updateById(news);
        }
        return R.ok().put("data", news);
    }

    /**
     * 后台 - 分页列表
     */
    @GetMapping("/page")
    public R page(@RequestParam(defaultValue = "1") long page,
                  @RequestParam(defaultValue = "10") long limit,
                  @RequestParam(required = false) String title) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(News::getTitle, title);
        }
        wrapper.orderByDesc(News::getCreateTime);
        IPage<News> p = newsService.page(new Page<>(page, limit), wrapper);
        return R.ok().put("data", new PageUtils(p));
    }

    /**
     * 后台 - 保存/更新
     */
    @PostMapping("/save")
    public R save(@RequestBody News news) {
        newsService.saveOrUpdate(news);
        return R.ok("保存成功");
    }

    /**
     * 后台 - 删除
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        for (Long id : ids) {
            newsService.removeById(id);
        }
        return R.ok("删除成功");
    }
}
