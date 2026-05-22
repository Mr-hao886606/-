import request from './request'

// ========== 用户相关 ==========
export const login = (username, password, role = 'user') =>
  request.post('/user/login', null, { params: { username, password, role } })

export const register = (data) => request.post('/user/register', data)

export const resetPass = (username) => request.post('/user/resetPass', null, { params: { username } })

export const getUserInfo = () => request.get('/user/info')

export const updateUser = (data) => request.put('/user/update', data)

export const updatePassword = (oldPassword, newPassword) =>
  request.put('/user/updatePassword', null, { params: { oldPassword, newPassword } })

export const getUserPage = (params) => request.get('/user/page', { params })
export const saveUser = (data) => request.post('/user/save', data)
export const deleteUsers = (ids) => request.delete('/user/delete', { data: ids })

// ========== 菜品相关 ==========
export const getDishList = (params) => request.get('/dish/list', { params })
export const getDishInfo = (id) => request.get(`/dish/info/${id}`)
export const getHotDishes = () => request.get('/dish/hot')
export const getDishPage = (params) => request.get('/dish/page', { params })
export const saveDish = (data) => request.post('/dish/save', data)
export const deleteDishes = (ids) => request.delete('/dish/delete', { data: ids })

// ========== 分类相关 ==========
export const getCategoryList = () => request.get('/foodCategory/list')
export const getCategoryPage = (params) => request.get('/foodCategory/page', { params })
export const saveCategory = (data) => request.post('/foodCategory/save', data)
export const deleteCategories = (ids) => request.delete('/foodCategory/delete', { data: ids })

// ========== 餐厅相关 ==========
export const getRestaurantList = () => request.get('/restaurant/list')
export const getRestaurantPage = (params) => request.get('/restaurant/page', { params })
export const saveRestaurant = (data) => request.post('/restaurant/save', data)
export const deleteRestaurants = (ids) => request.delete('/restaurant/delete', { data: ids })

// ========== 购物车相关 ==========
export const getCartList = () => request.get('/cart/list')
export const addToCart = (dishId, quantity = 1) => request.post('/cart/add', null, { params: { dishId, quantity } })
export const updateCartQty = (id, quantity) => request.put(`/cart/update/${id}`, null, { params: { quantity } })
export const removeFromCart = (id) => request.delete(`/cart/remove/${id}`)
export const clearCart = () => request.delete('/cart/clear')
export const getCartCount = () => request.get('/cart/count')

// ========== 订单相关 ==========
export const createOrder = (data) => request.post('/order/create', data)
export const cancelOrder = (id) => request.put(`/order/cancel/${id}`)
export const getMyOrders = (params) => request.get('/order/myOrders', { params })
export const getOrderInfo = (id) => request.get(`/order/info/${id}`)
export const getOrderPage = (params) => request.get('/order/page', { params })
export const updateOrderStatus = (id, status) => request.put(`/order/updateStatus/${id}`, null, { params: { status } })
export const deleteOrders = (ids) => request.delete('/order/delete', { data: ids })

// ========== 优惠券相关 ==========
export const getCouponList = () => request.get('/coupon/list')
export const receiveCoupon = (id) => request.post(`/coupon/receive/${id}`)
export const getMyCoupons = () => request.get('/coupon/myCoupons')
export const getCouponPage = (params) => request.get('/coupon/page', { params })
export const saveCoupon = (data) => request.post('/coupon/save', data)
export const deleteCoupons = (ids) => request.delete('/coupon/delete', { data: ids })

// ========== 地址相关 ==========
export const getAddressList = () => request.get('/address/list')
export const getAddressInfo = (id) => request.get(`/address/info/${id}`)
export const saveAddress = (data) => request.post('/address/save', data)
export const deleteAddress = (id) => request.delete(`/address/delete/${id}`)

// ========== 收藏相关 ==========
export const toggleFavorite = (dishId) => request.post(`/favorite/toggle/${dishId}`)
export const getFavorites = () => request.get('/favorite/list')

// ========== 轮播图相关 ==========
export const getBannerList = () => request.get('/banner/list')
export const getBannerPage = (params) => request.get('/banner/page', { params })
export const saveBanner = (data) => request.post('/banner/save', data)
export const deleteBanners = (ids) => request.delete('/banner/delete', { data: ids })

// ========== 资讯相关 ==========
export const getNewsList = (params) => request.get('/news/list', { params })
export const getNewsInfo = (id) => request.get(`/news/info/${id}`)
export const getNewsPage = (params) => request.get('/news/page', { params })
export const saveNews = (data) => request.post('/news/save', data)
export const deleteNews = (ids) => request.delete('/news/delete', { data: ids })

// ========== 评价相关 ==========
export const getReviewList = (dishId) => request.get(`/review/list/${dishId}`)
export const addReview = (data) => request.post('/review/add', data)
export const deleteReview = (id) => request.delete(`/review/delete/${id}`)

// ========== 文件上传 ==========
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
