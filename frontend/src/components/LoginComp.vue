<template>
  <div id="loginform">
    <div v-if="success">
      <p>Sisselogimine edukas!</p>
      <p>Teid suunatakse edasi harjutuste lehele.</p>
      <SpinnerLoader :color="'#54f1d2'"/>
    </div>
    <div v-else>
      <h2>Logi sisse</h2>
      <p>Sisselogimiseks sisesta kasutajanimi ja parool.</p>
      <p>Kui kasutaja puudub siis <router-link to="/register">Registreerimiseks vajuta siia.</router-link></p>
      <form @submit.prevent="initLogin()">
        <p><input type="text" placeholder="kasutaja nimi" v-model="user"></p>

        <p><input type="password" placeholder="parool" v-model="password"></p>

        <p><button type="submit" class="btn btn-primary">Logi sisse</button></p>
        <p class="error">{{ error }}</p>
      </form>
    </div>

  </div>
</template>


<script>
import axios from "axios";
import {Endpoints} from "@/general/endpoints";
import SpinnerLoader from '@bit/joshk.vue-spinners-css.spinner-loader';

export default {
  name: 'Login',
  components: {
    SpinnerLoader
  },
  data() {
    return {
      loginError: false,
      user: '',
      password: '',
      error: null,
      success: false
    }
  },
  computed: {
    getToken() {
      return this.$localStorage.token
    },
    getDate() {
      return this.$localStorage.validUntil
    }
  },
  methods: {
    initLogin() {
      let router = this.$router;
      this.errors = [];
      axios.post(Endpoints.LOGIN, {username: this.user, password: this.password})
          .then(response => {
            // console.log(response.data)
            if("token" in response.data.session) {
              this.$localStorage.token = response.data.session.token
              this.$localStorage.username = response.data.user.username
              this.$localStorage.age = response.data.user.age
              this.$localStorage.admin = response.data.user.admin
              this.$localStorage.validUntil = new Date(response.data.session.validUntil)

              this.success = true;
            } else {
              this.error = "Serveri viga. Palun värskendage lehte ja proovige uuesti."
              return;
            }
            setTimeout(function () {
              router.push({name:"Harjutus"})
            }, 1500);
          })
          .catch((error) => {
            console.log(error)
            try {
              if (error.response && "message" in error.response.data) {
                this.error = error.response.data.message
              } else {
                this.error = "Sisselogimine ebaõnnestus, palun värskendage lehte ja proovige uuesti."
              }
            } catch (e) {
              this.error = "Sisselogimine ebaõnnestus, palun värskendage lehte ja proovige uuesti."
            }
          })
    }
  }
};
</script>

<style scoped>

</style>