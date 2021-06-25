<template>
  <div class="container">
      <b-tabs align="center">

        <b-tab title="Mis see on?">
          <h3>Admin keskkonna ülevaade</h3>
          <p>Lugemiskaraoke admin keskkonnas saate lisada lauseid mis harjutustes ette antakse, neid hallata ning kuulata lausete salvestusi.</p>
        </b-tab>

        <b-tab title="Lisa">
          <h2>Lausete lisamine</h2>
          <AdminAddSentence />
        </b-tab>

        <b-tab title="Halda" @click="getAllSentences">
          <AdminManageSentence ref="manageSentences"/>
        </b-tab>
      </b-tabs>
  </div>
</template>

<script>

import AdminAddSentence from "@/components/AdminAddSentence";
import AdminManageSentence from "@/components/AdminManageSentence";

export default {
  name: "AdminArea",
  data() {
    return {
      activeTab: "add",
    }
  },
  methods: {
    getAllSentences() {
      this.$refs.manageSentences.getAllSentences()
    }
  },
  components: {
    AdminAddSentence,
    AdminManageSentence
  }
}
</script>
