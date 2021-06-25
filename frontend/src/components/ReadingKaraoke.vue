<template>
  <div>
    <div v-if="isLoggedIn">
      <div class="btn-group" role="group">
        <button v-on:click="changeDifficulty(0)" :class="difficulty === 0 ? 'active' : ''" class="btn btn-secondary">
          Kerge
        </button>
        <button v-on:click="changeDifficulty(1)" :class="difficulty === 1 ? 'active' : ''" class="btn btn-secondary">
          Keskmine
        </button>
        <button v-on:click="changeDifficulty(2)" :class="difficulty === 2 ? 'active' : ''" class="btn btn-secondary">
          Keeruline
        </button>
      </div>
      <br>
      <br>
      <div class="btn-group" role="group">
        <button class="btn btn-secondary" v-on:click="changeSpeed(+0.2)">Aeglasemaks</button>
        <button class="btn btn-secondary" v-on:click="resetSpeed()">Taasta</button>
        <button class="btn btn-secondary" v-on:click="changeSpeed(-0.2)">Kiiremaks</button>
      </div>
      <button :class="capitalize ? 'active' : ''" class="btn btn-secondary m-lg-2" v-on:click="capitalize=!capitalize">Trükitähed</button>
      <br>
      <transition v-if="speedVisible">
        <span>Teksti kiirus: {{ timeMultiplier }}x</span>
      </transition>
      <br>
      <p v-if="errorMsg">{{errorMsg}}</p>

      <div v-if="sentenceDone && started">
        <button @click="playAudio(getRecordedAudio)" class="btn btn-secondary m-1">Kuula lindistust</button>
        <button @click="playAudio(audioData.audioSource)" class="btn btn-secondary m-1">Kuula loetud teksti</button>
        <br>
      </div>

      <br>
      <div v-if="started">
        <span v-for="item in sentence" :key="item.id">
          <span :style="[capitalize ? {'text-transform': 'uppercase'} : {}]" :class="item.css">{{ item.word }} </span>
        </span>
      </div>
      <br>

      <div>
        <button v-if="sentenceDone"
                class="btn btn-primary"
                :class="{active:started}"
                @click="startKaraoke">
          {{ started ? 'Järgmine' : 'Alusta' }}
        </button>
      </div>

      <br>
      <vue-speedometer
          :value="syllablesPerSec"
          :minValue="0"
          :maxValue="10"
          :segments="100"
          :maxSegmentLabels="5"
          currentValueText="Silpi sekundis: ${value}"
      />

      <!--      <vue-dictaphone-spectrum-analyser/>-->


      <Karaoke/>
    </div>
    <div v-else>
      <p>Harjutustega alustamise palun
        <router-link to="/login">logige sisse</router-link>
      </p>
    </div>
  </div>
</template>

<script>

import Karaoke from "@/components/Karaoke";
import VueSpeedometer from "vue-speedometer"
import axios from "axios";
import {Endpoints} from "@/general/endpoints";
import {AgeSpeedMultiplier, KaraokeOptions} from "@/general/options";
import {MediaRecorder, register} from "extendable-media-recorder";
import {connect} from "extendable-media-recorder-wav-encoder";

export default {
  name: "ReadingKaraoke.vue",
  components: {Karaoke, VueSpeedometer},
  data() {
    return {
      errorMsg: null,
      sylPerSec: 0,
      started: false,
      sentenceDone: true,
      sentenceObj: null,
      sentence: null,
      currentWord: 0,
      difficulty: 0,
      timeMultiplier: 1,
      syllableCounter: 0,
      startTime: null,
      speedVisible: false,
      capitalize: false,

      audioData: {
        audioBlob: null,
        audioSource: null,
        stream: null,
        recordedChunks: null,
        mediaRecorder: null,
        options: {
          mimeType: "audio/wav"
        }
      },
    }
  },
  computed: {
    getRecordedAudio() {
      return Endpoints.SENTENCE_GET_RECORDING + this.sentenceObj.id
    },
    getUserAudio() {
      return this.audioData.audioSource
    },
    isLoggedIn() {
      return this.$localStorage.token && this.$localStorage.token !== '';
    },
    getToken() {
      return this.$localStorage.token
    },
    getAge() {
      return this.$localStorage.age;
    },
    syllablesPerSec() {
      if (this.startTime != null && this.started && this.syllableCounter !== 0) {
        // A little bit of random just to make the graphic less static since there's no silence control to end sentence early.
        var meterTest = (Math.random() * (0.2 - 0.02) + 0.02)
        return Math.round(((this.syllableCounter / ((new Date().getTime() / 1000) - this.startTime)) + meterTest) * 1e2) / 1e2;
      } else {
        return 0
      }
    }
  },
  mounted() {
    if (this.getAge <= 6) {
      this.changeDifficulty(0)
      this.capitalize = true
    } else if (this.getAge <= 9) {
      this.changeDifficulty(1)
    } else {
      this.changeDifficulty(2)
    }
    this.resetSpeed()
  },
  methods: {
    playAudio(src) {
      let audio = new Audio(src)
      audio.play();
    },
    changeDifficulty(difficultyId) {
      this.difficulty = difficultyId
    },
    resetSpeed() {
      if (this.getAge <= 6) {
        this.timeMultiplier = AgeSpeedMultiplier.toSix
      } else if (this.getAge <= 9) {
        this.timeMultiplier = AgeSpeedMultiplier.toNine
      } else {
        this.timeMultiplier = AgeSpeedMultiplier.overNine
      }
    },
    hideSpeedDisplay() {
      this.speedVisible = false;
    },
    changeSpeed(value) {
      this.speedVisible = true;
      setTimeout(() => this.hideSpeedDisplay(), 2500)
      this.timeMultiplier = Math.round((this.timeMultiplier + value) * 1e2) / 1e2
      if (this.timeMultiplier < 0.1) {
        this.timeMultiplier = 0.1
      } else if (this.timeMultiplier > 10) {
        this.timeMultiplier = 10
      }
    },
    resetKaraokeData() {
      this.audioData.audioSource = null;
      this.started = true;
      this.currentWord = 0;
      this.sentenceDone = false;
      this.syllableCounter = 0;
      this.startTime = new Date().getTime() / 1000;
    },
    async startKaraoke() {
      this.errorMsg = null;
      this.getRandomSentenceFromCurrentGroup()

      this.resetKaraokeData()
      // console.log(this.sentence)
      try {
        await register(await connect())
      } catch (err) {
        console.log("Already exists")
      }

      this.audioData.recordedChunks = [];
      this.audioData.stream = await navigator.mediaDevices.getUserMedia({audio: true});
      this.audioData.mediaRecorder = new MediaRecorder(this.audioData.stream, this.audioData.options);

      this.audioData.mediaRecorder.ondataavailable = ((e) => this.handleRecordedChunk(e));
      this.audioData.mediaRecorder.start();

      setTimeout(() => this.highlightWord(), KaraokeOptions.startDelay)
    },
    handleRecordedChunk(event) {
      if (event.data.size > 0) {
        this.audioData.recordedChunks.push(event.data);
        this.setAudioSrc();
        this.sendToServer();
      } else {
        console.log("Data size 0 passed")
      }
    },
    highlightWord() {
      console.log(this.syllableCounter)
      if ((this.currentWord) in this.sentence) {
        this.syllableCounter += this.sentence[this.currentWord].syllableCount
        // var meterTest = (Math.random() * (0.2 - 0.02) + 0.02).toFixed(2)
        // this.sylPerSec = Math.round(((1000 / this.sentence[this.currentWord].time * this.sentence[this.currentWord].syllableCount + meterTest)) * 1e2) / 1e2
        this.sentence[this.currentWord].css = "highlighted h4"
        if (this.currentWord > 0) {
          this.sentence[this.currentWord - 1].css = "normal h4"
        }
        setTimeout(() => this.highlightWord(),
            this.sentence[this.currentWord].time * this.timeMultiplier + KaraokeOptions.extraTimeAtEachWord)
        this.currentWord += 1
      } else {
        setTimeout(() => {
          this.audioData.mediaRecorder.stop();
          this.sentenceDone = true;
          for (let id in this.sentence) {
            this.sentence[id].css = "h4"
          }
        }, KaraokeOptions.endDelay)
      }
    },
    getRandomSentenceFromCurrentGroup() {
      console.log(this.difficulty)

      if(this.$route.query.sentence != null) {
        console.log("Defaulting to sentence selected by path variable")
        axios.get(Endpoints.KARAOKE_GET_BY_SENTENCE_ID + this.$route.query.sentence)
            .then((response) => {
              this.sentenceObj = response.data;
              var counter = 0;
              this.sentence = {};
              this.sentenceObj.words.forEach(item => {
                this.sentence[counter] = {
                  word: item.word,
                  time: item.time,
                  css: "normal h4",
                  syllableCount: item.nrOfSyllables
                }
                counter += 1
              });
            }).catch(error => {
              console.log(error)
          this.errorMsg = "Selle ID-ga (" +this.$route.query.sentence + ") lauset ei leitud";
        })
      } else {
        axios.get(Endpoints.KARAOKE_GET_RANDOM_BY_GROUP + this.difficulty)
            .then((response) => {
              this.sentenceObj = response.data;
              var counter = 0;
              this.sentence = {};
              this.sentenceObj.words.forEach(item => {
                this.sentence[counter] = {
                  word: item.word,
                  time: item.time,
                  css: "normal h4",
                  syllableCount: item.nrOfSyllables
                }
                counter += 1
              });
            }).catch(() => {
            this.errorMsg = "Sellest grupist lauset ei leitud."
        })
      }
    },
    setAudioSrc() {
      var blob = new Blob(this.audioData.recordedChunks, {
        type: "audio/wav"
      });
      this.audioData.audioBlob = blob;
      this.audioData.audioSource = URL.createObjectURL(blob);
      this.audioData.stream.getTracks().forEach(track => track.stop())
    },
    sendToServer() {
      let data = new FormData()
      data.append('audioFile', this.audioData.audioBlob)
      data.append("sentenceId", this.sentenceObj.id)
      data.append("sessionToken", this.getToken)
      data.append("syllablesPerSecond", this.syllablesPerSec)

      let config = {
        header: {
          "Config-Type": 'multipart/form-data'
        }
      }

      axios.post(Endpoints.KARAOKE_ADD, data, config)
          .then((response) => {
            console.log(response)
          }).catch((error) => {
        console.log(error)
      });
    }
  },
  beforeDestroy() {
    if(this.audioData.stream != null) {
      this.audioData.stream.getTracks().forEach(track => track.stop())
    }
  }
}
</script>

<style scoped>

.normal {
  color: grey;
}

.highlighted {
  color: red;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity .5s;
}

.fade-enter, .fade-leave-to
{
  opacity: 0;
}

</style>