<template>
  <div>
    <h2>Registreeri</h2>
    <p>Kui sul on juba kasutaja olemas <router-link to="/login">logi sisse siit.</router-link></p>
    <p>Uue kasutaja registreerimiseks täida vorm.</p>
    <RegisterNewAccount />
  </div>
</template>

<script>
import RegisterNewAccount from "@/components/RegisterNewAccount";

export default {
  name: 'Register',
  components: {
    RegisterNewAccount
  }
}
</script>


