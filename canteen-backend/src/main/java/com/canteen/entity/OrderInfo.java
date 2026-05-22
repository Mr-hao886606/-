package com.canteen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单实体
 */
@Data
@TableName("order_info")
public class OrderInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalPrice;
    private Long couponId;
    private BigDecimal discountAmount;
    private BigDecimal actualPrice;
    private String payType;
    private String status;
    private Long addressId;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    private Date updateTime;

    // 非数据库字段
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private java.util.List<OrderItem> items;
}
