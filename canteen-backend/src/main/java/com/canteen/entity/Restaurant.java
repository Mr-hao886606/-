package com.canteen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 餐厅实体
 */
@Data
@TableName("restaurant")
public class Restaurant {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String image;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private String licenseImage;
    private String description;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
