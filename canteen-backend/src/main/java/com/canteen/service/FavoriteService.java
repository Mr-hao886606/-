package com.canteen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.canteen.common.R;
import com.canteen.entity.Favorite;

public interface FavoriteService extends IService<Favorite> {
    R toggleFavorite(Long userId, Long dishId);
    R getUserFavorites(Long userId);
    boolean isFavorite(Long userId, Long dishId);
}
