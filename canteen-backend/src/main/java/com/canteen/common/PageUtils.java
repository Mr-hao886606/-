package com.canteen.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.HashMap;
import java.util.List;

/**
 * 分页工具类
 */
public class PageUtils extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public PageUtils(IPage<?> page) {
        this.put("records", page.getRecords());
        this.put("total", page.getTotal());
        this.put("size", page.getSize());
        this.put("current", page.getCurrent());
        this.put("pages", page.getPages());
    }

    public PageUtils(List<?> list, long total, long size, long current) {
        this.put("records", list);
        this.put("total", total);
        this.put("size", size);
        this.put("current", current);
        this.put("pages", (total + size - 1) / size);
    }
}
