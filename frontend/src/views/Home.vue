<template>
  <div class="home">
    <div class="container">
      <h1>Tere tulemast!</h1>
      <Instructions />
    </div>

  </div>
</template>

<script>
import Instructions from "@/components/Instructions";

export default {
  name: 'Home',
  components: {
    Instructions
  }
}
</script>

