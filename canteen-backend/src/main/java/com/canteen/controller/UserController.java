package com.canteen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.canteen.common.LoginUser;
import com.canteen.common.PageUtils;
import com.canteen.common.R;
import com.canteen.entity.User;
import com.canteen.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户Controller - 处理登录/注册/用户管理
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public R login(@RequestParam String username,
                   @RequestParam String password,
                   @RequestParam(defaultValue = "user") String role) {
        return userService.login(username, password, role);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public R register(@RequestBody User user) {
        return userService.register(user);
    }

    /**
     * 密码重置
     */
    @PostMapping("/resetPass")
    public R resetPass(@RequestParam String username) {
        User user = userService.getByUsername(username);
        if (user == null) {
            return R.error("账号不存在");
        }
        user.setPassword("123456");
        userService.updateById(user);
        return R.ok("密码已重置为：123456");
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public R logout() {
        return R.ok("退出成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public R getCurrentUser(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        User user = userService.getById(loginUser.getUserId());
        user.setPassword(null);
        return R.ok().put("data", user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public R update(@RequestBody User user, HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        user.setId(loginUser.getUserId());
        user.setPassword(null); // 不允许通过此接口修改密码
        user.setRole(null);     // 不允许修改角色
        userService.updateById(user);
        return R.ok("更新成功");
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public R updatePassword(@RequestParam String oldPassword,
                            @RequestParam String newPassword,
                            HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        User user = userService.getById(loginUser.getUserId());
        if (!oldPassword.equals(user.getPassword())) {
            return R.error("原密码不正确");
        }
        user.setPassword(newPassword);
        userService.updateById(user);
        return R.ok("密码修改成功");
    }

    // ==================== 管理员接口 ====================

    /**
     * 用户列表（分页）
     */
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params) {
        long current = params.get("page") != null ? Long.parseLong(params.get("page").toString()) : 1;
        long size = params.get("limit") != null ? Long.parseLong(params.get("limit").toString()) : 10;
        String keyword = (String) params.get("keyword");

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                   .or().like(User::getRealname, keyword)
                   .or().like(User::getPhone, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);

        IPage<User> page = userService.page(new Page<>(current, size), wrapper);
        page.getRecords().forEach(u -> u.setPassword(null)); // 隐藏密码
        return R.ok().put("data", new PageUtils(page));
    }

    /**
     * 管理员保存/更新用户
     */
    @PostMapping("/save")
    public R save(@RequestBody User user) {
        if (user.getId() == null) {
            User exist = userService.getByUsername(user.getUsername());
            if (exist != null) {
                return R.error("用户名已存在");
            }
            userService.save(user);
        } else {
            user.setPassword(null); // 不更新密码
            userService.updateById(user);
        }
        return R.ok("保存成功");
    }

    /**
     * 管理员删除用户
     */
    @DeleteMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        for (Long id : ids) {
            userService.removeById(id);
        }
        return R.ok("删除成功");
    }
}
