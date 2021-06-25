<template>
  <div>
    <div v-if="!isLoggedIn">
      <p>Tulemuste lehe vaatamiseks palun logige sisse.</p>
    </div>
    <div v-else-if="!userData">
      <SpinnerLoader :color="'#54f1d2'"/>
    </div>
<!--    <div v-else-if="userData">-->
<!--      -->
<!--    </div>-->
    <div v-else>
      <div class="container">
        <div class="row">
          <div class="col-4">
            <div class="jumbotron bg-danger rounded my-3 py-3 mt-5">
              <h2 class="h4">Keskmine lugemiskiirus:</h2>
              <h2 class="h4"><b>{{userData.averageTotalSyllables}} silpi sekundis!</b></h2>
              <br><br>
              <h2 class="h4">Keskmine raskusaste:</h2>
              <h2 class="h4"><b>{{getDifficultyString(userData.averageTotalDifficulty)}}</b></h2>
            </div>
          </div>
          <div class="col-8 bg-primary rounded-left" style="min-height: 300px">
            <h3 class="h3">Harjutamise sessioonid</h3>
            <div class="list-group">
              <table class="table table-dark">
                <thead>
                <tr>
                  <th scope="col">Kuupäev</th>
                  <th scope="col">Silpe sekundis</th>
                  <th scope="col">Keerukus</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(data) in feedbackStatistics" :key="data.id">
                  <td>{{data.sessionDate}}</td>
                  <td>{{data.averageSyllablesPerSec}}</td>
                  <td>{{data.averageDifficulty}}</td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import SpinnerLoader from "@bit/joshk.vue-spinners-css.spinner-loader";
import {Endpoints} from "@/general/endpoints";
import axios from "axios";

export default {
  name: "Progress",
  components: {
    SpinnerLoader
  },
  data() {
    return {
      userData: null,
      feedbackStatistics: null,
    }
  },
  mounted() {
    this.getUserData()
  },
  computed: {
    isLoggedIn() {
      return this.$localStorage.token && this.$localStorage.token !== '';
    },
    getToken() {
      return this.$localStorage.token
    }
  },
  methods: {
    getDifficultyString(difficultyNr) {
      if(difficultyNr <= 1) {
        return "Kerge"
      } else if(difficultyNr <= 2) {
        return "Keskmine"
      } else {
        return "Keeruline"
      }
    },
    roundToTwoDecimals(nr) {
      return Math.round(nr * 1e2) / 1e2;
    },
    getUserData: async function () {
      console.log("Fetching user data")
      const {data} = await axios.get(Endpoints.FEEDBACK_GET_ALL_FEEDBACK, {
        headers: {
          'token': this.getToken
        }
      });
      // console.log(data)
      data.averageTotalSyllables = this.roundToTwoDecimals(data.averageTotalSyllables)
      data.averageTotalDifficulty = this.getDifficultyString(data.averageTotalDifficulty)
      for (const session of data.feedbackStatistics) {
        session.sessionDate = new Date(session.sessionDate).toISOString().split('T')[0]
        session.averageDifficulty = this.getDifficultyString(session.averageDifficulty)
        session.averageSyllablesPerSec = this.roundToTwoDecimals(session.averageSyllablesPerSec)
      }

      this.userData = data
      this.feedbackStatistics = data.feedbackStatistics
    },
  },
}
</script>

<style scoped>

</style>