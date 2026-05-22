package com.canteen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.common.R;
import com.canteen.config.TokenUtils;
import com.canteen.entity.User;
import com.canteen.mapper.UserMapper;
import com.canteen.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private TokenUtils tokenUtils;

    @Override
    public R login(String username, String password, String role) {
        User user = baseMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        if (user == null) {
            return R.error("账号不存在");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return R.error("账号已被禁用");
        }
        if (!role.equals(user.getRole())) {
            return R.error("角色权限不正确");
        }
        if (!password.equals(user.getPassword())) {
            return R.error("密码不正确");
        }
        String token = tokenUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        user.setPassword(null); // 不返回密码
        return R.ok("登录成功").put("token", token).put("data", user);
    }

    @Override
    public R register(User user) {
        User existUser = baseMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername())
        );
        if (existUser != null) {
            return R.error("用户名已存在");
        }
        user.setRole("user"); // 默认注册为普通用户
        user.setStatus(1);
        baseMapper.insert(user);
        return R.ok("注册成功");
    }

    @Override
    public User getByUsername(String username) {
        return baseMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
    }
}
