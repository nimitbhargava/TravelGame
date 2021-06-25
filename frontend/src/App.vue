<template>
  <div id="app" class="demo-wrap" style="height: 100%;">

    <b-navbar class="bg-light mb-4" toggleable="lg" type="light" variant="info">
      <b-navbar-brand class="m-1 mx-auto" href="#">Lugemise karaoke</b-navbar-brand>

      <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

      <b-collapse class="justify-content-center" id="nav-collapse" is-nav>
        <b-navbar-nav class="justify-content-center">
          <router-link class="nav-link text-primary" to="/">Esileht</router-link>
          <router-link class="nav-link text-primary" to="/harjutus">Harjutama</router-link>
          <router-link v-if="isAdmin && isLoggedIn" class="nav-link text-primary" to="/admin">Admin</router-link>
          <router-link v-if="isLoggedIn" class="nav-link text-primary" to="/kasutaja">Tulemused</router-link>
          <a v-if="isLoggedIn" class="nav-link text-primary" target="_blank" href="https://docs.google.com/forms/d/e/1FAIpQLSfS06E9U6poX7PR2s3EiFs5ARUL5RARTFMkHGAC5ErWR-hIkA/viewform">Tagasiside</a>
          <span v-if="isLoggedIn" class="nav-link text-primary"  style="cursor: pointer;" @click="logout">Logi välja</span>
          <router-link v-else class="nav-link text-primary" to="/login">Logi sisse</router-link>
        </b-navbar-nav>

        <b-navbar-nav>
          <b-navbar-nav v-if="isLoggedIn">
            <b-nav-text class="text-dark mx-lg-2">Head harjutamist, {{ getUsername }}!</b-nav-text>
          </b-navbar-nav>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <router-view/>
  </div>
</template>

<script>


export default {
  name: 'RegisterNewAccount',
  computed:{
    isLoggedIn(){
      return this.$localStorage.token && this.$localStorage.token !== '' ;
    },
    getUsername() {
      return this.$localStorage.username
    },
    isAdmin() {
      return this.$localStorage.admin
    }
  },
  methods:{
    logout(){
      let router = this.$router;
      this.$localStorage.token = '';
      router.push("/")
    }
  }
}
</script>

<style>
@import "./assets/bootstrap.min.css";

li a {
  text-decoration: none;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#username {
  font-weight: bold;
}

#logout {
  font-weight: bold;
  color: #2c3e50;
  text-decoration: underline;
  cursor: pointer;
}

body,html {
  /*background: url("https://images.pexels.com/photos/743986/pexels-photo-743986.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260") no-repeat center center fixed;*/
  background: linear-gradient( rgba(0, 0, 0, 0.005), rgba(0, 0, 0, 0.005) ), url('./assets/pexels-jess-bailey-designs-743986.jpg') no-repeat center center fixed;
  -webkit-background-size: cover;
  -moz-background-size: cover;
  -o-background-size: cover;
  background-size: cover;
}

/*@media only screen and (max-width: 767px) {*/
/*  body {*/
/*    background-image: url("https://images.pexels.com/photos/743986/pexels-photo-743986.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");*/
/*  }*/
/*}*/

/*body{*/
/*  height: 100vh;*/
/*  margin: 0;*/
/*  padding: 0;*/
/*}*/

/*#nav a.router-link-exact-active {*/
/*  color: #42b983;*/
/*}*/
</style>
