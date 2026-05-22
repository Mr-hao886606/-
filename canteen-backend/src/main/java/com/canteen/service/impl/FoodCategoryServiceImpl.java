package com.canteen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.entity.FoodCategory;
import com.canteen.mapper.FoodCategoryMapper;
import com.canteen.service.FoodCategoryService;
import org.springframework.stereotype.Service;

@Service
public class FoodCategoryServiceImpl extends ServiceImpl<FoodCategoryMapper, FoodCategory> implements FoodCategoryService {
}
