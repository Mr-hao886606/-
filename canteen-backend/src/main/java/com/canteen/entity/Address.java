package com.canteen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 收货地址实体
 */
@Data
@TableName("address")
public class Address {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String addressDetail;
    private Integer isDefault;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
