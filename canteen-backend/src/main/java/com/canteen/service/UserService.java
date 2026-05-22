package com.canteen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.common.R;
import com.canteen.entity.User;

public interface UserService extends IService<User> {
    R login(String username, String password, String role);
    R register(User user);
    User getByUsername(String username);
}
