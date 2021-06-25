<template>
  <div id="registerForm">
    <form id="app"
          @submit="registerNewAcc"
    >

      <p>
        <label for="username">Kasutaja nimi</label>
        <input
            id="username"
            v-model="username"
            type="text"
            name="name"
        >
      </p>

      <p>
        <label for="password">Parool</label>
        <input
            id="password"
            v-model="password"
            type="password"
            name="name"
        >
      </p>

      <p>
        <label for="age">Vanus</label>
        <input
            id="age"
            v-model="age"
            type="number"
            name="age"
            min="0"
            max="100">
      </p>

      <div>
        <p>
        <input
            type="checkbox"
            v-model="termsAgreed">
        Nõustun, et minu häälenäited salvestatakse ja kasutatakse teadustöö eesmärgil.</p>
      </div>

      <p v-if="newUserErrors.length">
        <b>Viga:</b>
      <ul style="list-style-type: none">
        <li v-for="(error) in newUserErrors" :key="error.id">{{ error }}</li>
      </ul>
      </p>



      <div v-if="success">
        <p>Kasutaja registreeritud!</p>
        <button class="btn btn-primary"> <router-link style="color: black" to="/login">Logi sisse</router-link></button>
      </div>
      <div v-else>
        <p>
          <button class="btn btn-primary" type="submit">Registreeri</button>
        </p>
      </div>

    </form>
  </div>
</template>


<script>

import axios from "axios";
import {Endpoints} from "@/general/endpoints";

export default {
  name: 'RegisterNewAccount',
  data() {
    return {
      loginError: false,
      username: null,
      password: null,
      age: null,
      newUserErrors: [],
      success: false,
      termsAgreed: false
    }
  },
  methods: {
    registerNewAcc: function (e) {
      this.newUserErrors = []

      if(!this.termsAgreed) {
        this.newUserErrors.push("Palun andke nõusolek hääle salvestamiseks.")
      }

      if (!this.username) {
        this.newUserErrors.push("Palun sisesta kasutajanimi")
      } else if (this.username.length < 4) {
        this.newUserErrors.push("Kasutajanimes peab olema vähemalt 4 märki.")
      }

      if (!this.age) {
        this.newUserErrors.push("Palun sisesta vanus")
      }

      if (!this.password) {
        this.newUserErrors.push("Palun sisesta parool.")
      } else if (this.password.length < 4) {
        this.newUserErrors.push("Paroolis peab olema vähemalt 4 märki.")
      }

      if (this.newUserErrors.length === 0) {
        axios.post(Endpoints.REGISTER, {username: this.username, password: this.password, age: this.age})
            .then(response => {
              console.log(response)
              this.success = true;
            })
            .catch(error => {
              this.success = false;
              console.log("err")
              console.log(error)
              if (error.response.data.message) {
                console.log(error.response.data);
                this.newUserErrors.push(error.response.data.message)
              } else {
                this.newUserErrors.push("Registreerimine ebaõnnestus, palun proovige hiljem uuesti.")
              }
            })
      }

      e.preventDefault();
    }
  }
};
</script>

<style scoped>

</style>