package com.canteen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 菜品实体
 */
@Data
@TableName("dish")
public class Dish {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String image;
    private Long categoryId;
    private String taste;
    private Long restaurantId;
    private BigDecimal price;
    private Integer stock;
    private Integer limitPerOrder;
    private Integer clickCount;
    private Integer salesCount;
    private String description;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    // 非数据库字段 - 用于关联查询展示
    @TableField(exist = false)
    private String categoryName;
    @TableField(exist = false)
    private String restaurantName;
    @TableField(exist = false)
    private Boolean isFavorite;
}
