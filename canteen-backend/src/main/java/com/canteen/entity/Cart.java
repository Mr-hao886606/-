package com.canteen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 购物车实体
 */
@Data
@TableName("cart")
public class Cart {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long dishId;
    private Integer quantity;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    // 非数据库字段 - 用于前端展示
    @TableField(exist = false)
    private String dishName;
    @TableField(exist = false)
    private String dishImage;
    @TableField(exist = false)
    private java.math.BigDecimal price;
}
