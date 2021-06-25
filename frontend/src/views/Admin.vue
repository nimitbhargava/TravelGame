<template>
  <div class="karaoke">
    <h1>Admin</h1>

    <AdminArea />

  </div>
</template>

<script>


import AdminArea from "@/components/AdminArea";

export default {
  name: 'Admin',
  data() {
    return {
      backendResponse: '',
      securedApiCallSuccess: false,
      errors: []
    }
  },
  components: {
    AdminArea
  }
}
</script>
