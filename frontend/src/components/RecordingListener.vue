<template>
  <div id="loginform">
    <form @submit.prevent="initLogin()">
      <input type="text" placeholder="kasutaja nimi" v-model="user">
      <input type="password" placeholder="parool" v-model="password">
      <button type="submit" class="btn btn-primary">Logi sisse</button>
      <p class="error">{{ error }}</p>
    </form>
    <p v-if="success">Sisselogimine edukas!</p>
  </div>
</template>


<script>
import axios from "axios";

export default {
  name: 'RecordingListener',
  data() {
    return {
      loginError: false,
      user: '',
      password: '',
      error: null,
      success: false
    }
  },
  methods: {
    initLogin() {
      let router = this.$router;
      this.errors = [];
      axios.post("api/user/login", {username: this.user, password: this.password})
          .then(response => {
            if("token" in response.data) {
              localStorage.token = response.data.token
              localStorage.username = this.user
              localStorage.admin = this.isAdmin
              this.success = true;
            } else {
              this.error = "Serveri viga, palun proovige hiljem uuesti."
              return;
            }
            setTimeout(function () {
              router.push({name:"Harjutus"})
            }, 3000);
          })
          .catch((error) => {
            console.log(error)
            if (error.response && "message" in error.response.data) {
              this.error = error.response.data.message
            } else {
              this.error = "Sisselogimine ebaõnnestus, palun proovige hiljem uuesti."
            }
          })
    }
  }
};
</script>

<style scoped>

</style>