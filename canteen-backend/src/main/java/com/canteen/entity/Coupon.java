package com.canteen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券实体
 */
@Data
@TableName("coupon")
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;
    private BigDecimal minAmount;
    private BigDecimal discountAmount;
    private Integer totalCount;
    private Integer remainCount;
    private Date startTime;
    private Date endTime;
    private Long restaurantId;
    private String remark;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
