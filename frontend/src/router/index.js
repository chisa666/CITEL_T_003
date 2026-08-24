import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/query/age'
  },
  {
    path: '/query/age',
    name: 'AgeQuery',
    component: () => import('../views/AgeQuery.vue')
  },
  {
    path: '/query/mileage',
    name: 'MileageQuery',
    component: () => import('../views/MileageQuery.vue')
  },
  {
    path: '/query/time',
    name: 'TimeQuery',
    component: () => import('../views/TimeQuery.vue')
  },
  {
    path: '/config',
    name: 'ConfigManage',
    component: () => import('../views/ConfigManage.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
