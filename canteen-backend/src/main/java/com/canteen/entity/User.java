package com.canteen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 用户实体
 */
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String realname;
    private String phone;
    private String email;
    private String avatar;
    private String role;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
