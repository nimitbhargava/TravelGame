<template>
  <div>
    <h2>Halda</h2>
    <div v-if="!sentenceData">
      <SpinnerLoader :color="'#54f1d2'"/>
      <h4 class="h4">Andmeid laetakse..</h4>
    </div>
    <div v-else>

      <span>Lause grupp:</span><br>
      <div class="btn-group mb-4" role="group">
        <button v-on:click="setGroup(0)" :class="activeGroup === 0 ? 'active' : ''" class="btn btn-secondary">
          Kerge
        </button>
        <button v-on:click="setGroup(1)" :class="activeGroup === 1 ? 'active' : ''" class="btn btn-secondary">
          Keskmine
        </button>
        <button v-on:click="setGroup(2)" :class="activeGroup === 2 ? 'active' : ''" class="btn btn-secondary">
          Keeruline
        </button>
        <button v-on:click="setGroup(null)" :class="activeGroup === null ? 'active' : ''" class="btn btn-secondary">
          Grupeerimata
        </button>
      </div>

      <div class="accordion mb-3">
        <div class="accordion-item border-dark">
          <div class="row">
            <div class="col border">
              <div>
              <h5 class="h5">Lause</h5>
              </div>
            </div>
            <div class="col border">
              <h5 class="h5">Kasutajate lindistused</h5>
            </div>
          </div>
        </div>
        <div v-for="(item, index) in getSentenceData" :key="item.id" v-bind:value="item.id">
          <div class="accordion-item" v-if="item.difficulty === activeGroup">
            <h2 class="accordion-header">
              <button v-bind:class="['accordion-button', (item.active) ? '' : 'collapsed']"
                      @click="triggerActive(index)">
                {{ item.text }}
              </button>
            </h2>
            <div v-bind:class="['accordion-collapse', (item.active) ? 'show' : 'collapse']">
              <div class="row">
                <div class="col border">
                  <b>Autor: </b> <span>{{ item.author.username }}</span>
                  <br>
                  <b>Silbitatud tekst: </b> <span v-for="word in item.words" :key="word.id"
                                                  v-bind:value="word.id">{{ word.wordAsSyllables }} </span>
                  <br>
                  <a class="btn btn-success my-1 mx-1 btn-sm" v-bind:href="getAdminRecordedAudioLink(item.id)" download>
                    Lae alla lindistus</a>
                  <button class="btn btn-success my-1 mx-1 btn-sm" @click="playAudio(getAdminRecordedAudioLink(item.id))">Kuula lindistust</button>
                  <a class="btn btn-success my-1 mx-1 btn-sm" v-bind:href="getTextGridDlLink(item.id)" download>
                    Lae alla textgrid</a>
                  <br>
                  <div v-if="item.error">
                    <span class="text-danger">Error: {{item.error}}</span>
                  </div>
                  <div v-else-if="item.queryActive">
                    <SpinnerLoader :color="'#54f1d2'"/>
                  </div>
                  <div v-else-if="item.difficulty !== null">
                    <button class="btn btn-danger m-1 btn-sm" @click="setNewGroupForSentence(null, item.id, index)">
                      Eemalda grupist
                    </button>
                  </div>
                  <div v-else>
                    <span>Liiguta gruppi:</span>
                    <div class="btn-group mb-2" role="group">
                      <button class="btn btn-primary btn-sm" @click="setNewGroupForSentence(0, item.id, index)">
                        Kerge
                      </button>
                      <button class="btn btn-primary btn-sm" @click="setNewGroupForSentence(1, item.id, index)">
                        Keskmine
                      </button>
                      <button class="btn btn-primary btn-sm" @click="setNewGroupForSentence(2, item.id, index)">
                        Keeruline
                      </button>
                    </div>
                  </div>
                </div>
                <div class="col">
                  <h5 class="h5">Lindistused</h5>
                  <div v-for="recording in item.recordings" :key="recording.id" v-bind:value="recording.id">
                    <b>Kasutaja: </b> <span>{{ recording.user.username }}</span>
                    <br>
                    <b>Salvestatud: </b> <span>{{ recording.createDate }}</span>
                    <br>
                    <b>Silpe sekundis: </b> <span>{{ recording.syllablesPerSecond }}</span>
                    <br>
                    <button class="btn btn-success my-1 mx-1 btn-sm" @click="getRecording(recording.id)" download>
                      Lae alla lindistus</button>
                    <button class="btn btn-success my-1 mx-1 btn-sm" @click="getRecording(recording.id, false)">Kuula lindistust</button>
                    <hr/>
                  </div>
                </div>

              </div>
            </div>
          </div>
        </div>


      </div>
    </div>

  </div>
</template>


<script>
import axios from "axios";
import {Endpoints} from "@/general/endpoints";
import SpinnerLoader from "@bit/joshk.vue-spinners-css.spinner-loader";

export default {
  name: 'AdminManageSentence',
  components: {
    SpinnerLoader
  },
  computed: {
    getToken() {
      return this.$localStorage.token
    },
    getSentenceData: function () {
      return this.sentenceData;
    }
  },
  data() {
    return {
      sentenceData: null,
      activeGroup: 1
    }
  },
  mounted() {
    this.getAllSentences()
  },
  methods: {
    playAudio(src) {
      console.log(src)
      let audio = new Audio(src)
      audio.play();
    },
    getAdminRecordedAudioLink(sentenceId) {
      return Endpoints.SENTENCE_GET_RECORDING + sentenceId
    },
    getTextGridDlLink(sentenceId) {
      return Endpoints.SENTENCE_GET_TEXTGRID + sentenceId
    },
    setGroup(id) {
      this.activeGroup = id
    },
    triggerActive(sentenceIndex) {
      this.sentenceData[sentenceIndex]["active"] = !this.sentenceData[sentenceIndex]["active"]
      // Fetching all right away, makes it easier for testing. Replace with on group basis if slow on high load
      // if(this.sentenceData[sentenceIndex]["active"] && this.sentenceData[sentenceIndex]["recordings"] == null) {
      //   this.getRecordingsForSentence(this.sentenceData[sentenceIndex]["id"], sentenceIndex)
      // }
    },
    setNewGroupForSentence(groupId, sentenceId, sentenceIndex) {
      console.log("setting group")
      console.log(groupId)
      console.log(sentenceId)
      console.log(sentenceIndex)

      this.sentenceData[sentenceIndex]["queryActive"] = true

      let data = {
        id: sentenceId,
        difficulty: groupId,
        userToken: this.getToken
      }

      axios.post(Endpoints.SENTENCE_UPDATE_GROUP, data)
          .then((response) => {
            console.log(response)
            this.sentenceData[sentenceIndex]["queryActive"] = false
            this.sentenceData[sentenceIndex]["difficulty"] = groupId
          }).catch((error) => {
        this.sentenceData[sentenceIndex]["queryActive"] = false
        console.log(error)
        this.sentenceData[sentenceIndex]["error"] = "Grupi uuendamine ei õnnestunud."
      });
    },
    async getRecordingsForSentence(sentenceId) {
      const {data} = await axios.get(Endpoints.KARAOKE_GET_RECORDINGS_BY_SENTENCE + sentenceId, {
        headers: {
          'token': this.getToken
        }
      });
      for (const recording of data) {
        recording.createDate = new Date(recording.createDate).toLocaleString('en-GB')
      }
      return data
    },
    async getAllSentences() {
      console.log("get sentence")
      axios.get(Endpoints.SENTENCE_GET_ALL)
          .then(async (response) => {
            // console.log(response.data)
            for (const sentence of response.data) {
              // item.recordings = this.getRecordingsForSentence(item.id)
              sentence["recordings"] = await this.getRecordingsForSentence(sentence.id)
              sentence["active"] = false
              sentence["error"] = false
              sentence["queryActive"] = false
            }
            this.sentenceData = response.data
          })
    },
    async getRecording(recordingId, downloadFile = true) {
      const {data} = await axios.get(Endpoints.KARAOKE_GET_RECORDING_FILE_BY_ID + recordingId, {
        responseType: 'arraybuffer',
        headers: {
          'Content-Type': 'audio/wav',
          'token': this.getToken
        }
      });
      const blob = new Blob([data], {
        type: 'audio/wav'
      });

      if(downloadFile) {
        this.download(URL.createObjectURL(blob))
      } else {
        this.playAudio(URL.createObjectURL(blob))
      }
    },
    download(url) {
      var a = document.createElement("a");
      document.body.appendChild(a);
      a.style = "display: none";
      a.href = url;
      a.download = "test.wav";
      a.click();
    },
  }
};
</script>

<style scoped>

.border {
  border: 1px #2c3e50;
}

</style>