package com.canteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.canteen.common.LoginUser;
import com.canteen.common.R;
import com.canteen.entity.Review;
import com.canteen.entity.User;
import com.canteen.mapper.UserMapper;
import com.canteen.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 评价Controller
 */
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取某菜品的评价列表
     */
    @GetMapping("/list/{dishId}")
    public R list(@PathVariable Long dishId) {
        List<Review> reviews = reviewService.list(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getDishId, dishId)
                        .orderByDesc(Review::getCreateTime)
        );
        // 填充用户名和头像
        for (Review review : reviews) {
            User user = userMapper.selectById(review.getUserId());
            if (user != null) {
                review.setUsername(user.getRealname() != null ? user.getRealname() : user.getUsername());
                review.setAvatar(user.getAvatar());
            }
        }
        return R.ok().put("data", reviews);
    }

    /**
     * 添加评价
     */
    @PostMapping("/add")
    public R add(@RequestBody Review review, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        review.setUserId(loginUser.getUserId());
        reviewService.save(review);
        return R.ok("评价成功");
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id) {
        reviewService.removeById(id);
        return R.ok("删除成功");
    }
}
