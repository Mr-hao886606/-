package com.canteen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.common.R;
import com.canteen.entity.Favorite;
import com.canteen.mapper.FavoriteMapper;
import com.canteen.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Override
    public R toggleFavorite(Long userId, Long dishId) {
        Favorite exist = baseMapper.selectOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getDishId, dishId)
        );
        if (exist != null) {
            baseMapper.deleteById(exist.getId());
            return R.ok("已取消收藏");
        } else {
            Favorite fav = new Favorite();
            fav.setUserId(userId);
            fav.setDishId(dishId);
            baseMapper.insert(fav);
            return R.ok("收藏成功");
        }
    }

    @Override
    public R getUserFavorites(Long userId) {
        List<Favorite> favorites = baseMapper.selectList(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreateTime)
        );
        List<Long> dishIds = favorites.stream().map(Favorite::getDishId).collect(Collectors.toList());
        return R.ok().put("data", dishIds);
    }

    @Override
    public boolean isFavorite(Long userId, Long dishId) {
        if (userId == null) return false;
        return baseMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getDishId, dishId)
        ) > 0;
    }
}
