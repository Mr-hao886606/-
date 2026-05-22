package com.canteen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 美食分类实体
 */
@Data
@TableName("food_category")
public class FoodCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
