import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/harjutus',
    name: 'Harjutus',
    component: () => import('../views/Karaoke')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login')
  },
  {
    path: '/kasutaja',
    name: 'Kasutaja',
    component: () => import('../views/AccountPage')
  },
  {
    path: '/register',
    name: 'Registreerimine',
    component: () => import('../views/Register')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/Admin')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
