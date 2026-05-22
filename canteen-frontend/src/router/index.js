import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  // ========== 前台页面 ==========
  {
    path: '/',
    component: () => import('../views/layout/FrontLayout.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: 'Home', component: () => import('../views/front/Home.vue'), meta: { title: '首页' } },
      { path: 'dishes', name: 'Dishes', component: () => import('../views/front/DishList.vue'), meta: { title: '菜品浏览' } },
      { path: 'dish/:id', name: 'DishDetail', component: () => import('../views/front/DishDetail.vue'), meta: { title: '菜品详情' } },
      { path: 'cart', name: 'Cart', component: () => import('../views/front/Cart.vue'), meta: { title: '购物车', needLogin: true } },
      { path: 'orders', name: 'Orders', component: () => import('../views/front/Orders.vue'), meta: { title: '我的订单', needLogin: true } },
      { path: 'favorites', name: 'Favorites', component: () => import('../views/front/Favorites.vue'), meta: { title: '我的收藏', needLogin: true } },
      { path: 'profile', name: 'Profile', component: () => import('../views/front/Profile.vue'), meta: { title: '个人中心', needLogin: true } },
      { path: 'address', name: 'Address', component: () => import('../views/front/Address.vue'), meta: { title: '收货地址', needLogin: true } },
      { path: 'coupons', name: 'Coupons', component: () => import('../views/front/Coupons.vue'), meta: { title: '我的优惠券', needLogin: true } },
      { path: 'news', name: 'News', component: () => import('../views/front/News.vue'), meta: { title: '美食资讯' } },
      { path: 'news/:id', name: 'NewsDetail', component: () => import('../views/front/NewsDetail.vue'), meta: { title: '资讯详情' } },
    ]
  },
  // ========== 登录/注册 ==========
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },

  // ========== 后台管理页面 ==========
  {
    path: '/admin',
    component: () => import('../views/layout/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { needLogin: true, needAdmin: true },
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '控制台' } },
      { path: 'users', name: 'AdminUsers', component: () => import('../views/admin/Users.vue'), meta: { title: '用户管理' } },
      { path: 'restaurants', name: 'AdminRestaurants', component: () => import('../views/admin/Restaurants.vue'), meta: { title: '餐厅管理' } },
      { path: 'categories', name: 'AdminCategories', component: () => import('../views/admin/Categories.vue'), meta: { title: '分类管理' } },
      { path: 'dishes', name: 'AdminDishes', component: () => import('../views/admin/Dishes.vue'), meta: { title: '菜品管理' } },
      { path: 'orders', name: 'AdminOrders', component: () => import('../views/admin/Orders.vue'), meta: { title: '订单管理' } },
      { path: 'coupons', name: 'AdminCoupons', component: () => import('../views/admin/Coupons.vue'), meta: { title: '优惠券管理' } },
      { path: 'banners', name: 'AdminBanners', component: () => import('../views/admin/Banners.vue'), meta: { title: '轮播图管理' } },
      { path: 'news', name: 'AdminNews', component: () => import('../views/admin/News.vue'), meta: { title: '资讯管理' } },
    ]
  },
  // ========== 404 ==========
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('../views/NotFound.vue') }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 校园食堂` : '校园食堂'

  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

  // 需要登录的页面
  if (to.meta.needLogin && !token) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  // 需要管理员权限
  if (to.meta.needAdmin && userInfo.role !== 'admin') {
    return next({ path: '/home' })
  }

  next()
})

export default router
