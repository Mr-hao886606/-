package com.canteen.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 美食资讯实体
 */
@Data
@TableName("news")
public class News {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String category;
    private String coverImage;
    private String content;
    private Integer clickCount;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
