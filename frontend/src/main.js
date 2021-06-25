import Vue from 'vue'
import App from './App.vue'
import router from './router'
import VueDictaphone from "vue-dictaphone"; //sound wave
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'


// import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'


Vue.use(VueDictaphone);
Vue.config.productionTip = false
Vue.use(BootstrapVue)
Vue.use(IconsPlugin)


Vue.mixin({
  methods: {
    checkLogin: function () {
    },
  },
})

Vue.prototype.$localStorage = new Vue({
  data: {
    token: window.localStorage.getItem('token'),
    username: window.localStorage.getItem('username'),
    age: window.localStorage.getItem('age'),
    admin: window.localStorage.getItem('admin'),
    validUntil: window.localStorage.getItem('admin')
  },
  watch: {
    '$route': function(to, from) {
      console.log(to)
      console.log(from)
      console.log("cakelog")
    },
    token(value) {
      window.localStorage.setItem('token', value)
    },
    username(value) {
      window.localStorage.setItem('username', value)
    },
    age(value) {
      window.localStorage.setItem('age', value)
    },
    admin(value) {
      window.localStorage.setItem('admin', value)
    },
    validUntil(value) {
      window.localStorage.setItem('validUntil', value)
    },
  }
})

new Vue({
  router,
  render: h => h(App),
  computed: {
    getToken() {
      return this.$localStorage.token
    },
    getDate() {
      return this.$localStorage.validUntil
    }
  },
  watch: {
    '$route': function(from) {
      let expireDate = this.getDate
      if((new Date() > expireDate) && this.$localStorage.token && this.$localStorage.token !== '' && expireDate.getFullYear() > 2020) {
        this.$localStorage.token = '';
        this.$localStorage.initializeTime = '';
        if(from.fullPath !== "/") {
          router.push("/")
        }
      }
    }
  }
}).$mount('#app')
