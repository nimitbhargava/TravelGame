<template>
  <div class="container">

    <div>
      <button class="btn btn-primary mx-1" @click="tabControls.manual=!tabControls.manual">
        Kuidas lisada?
      </button>
      <button class="btn btn-primary mx-1" @click="tabControls.adding=!tabControls.adding">
        Lisama
      </button>
    </div>

    <div v-if="tabControls.manual" class="my-2">
      <p>Lause lisamiseks sisesta tekst, vali lause keerukus ning lindista lause.</p>
      <div>
        <ul class="list-group" style="list-style-position: inside;">
          <li>Lause keerukus on subjektiivne, kuid peaks lähtuma sõnade lugemiskeerukusest.</li>
          <li>Püüa lugeda sõnu ühes tempos ning selgelt.</li>
          <li>Lindistatud lause saad üle kuulata ja vajadusel uuesti lindistada.</li>
          <li>Lause salvestamist serverisse ei pea ära ootama vaid võid juba järgmise lisamist alustada.</li>
          <li>Lisatud laused leiad 'Halda' akna alt.</li>
          <li>Lause muutmisel keerukus ja lindistus kustuvad.</li>
        </ul>
      </div>
    </div>

    <div class="row" v-if="tabControls.adding">
      <!--     COLUMN 1  -->
      <div class="col-md-6">
        <div class="form-group">
          <label for="inputSentence">Sisesta lause</label>
          <textarea class="form-control" :disabled="sending" maxlength="500" v-on:input="resetData" v-model="sentence.text"
                    id="inputSentence"
                    name="inputSentence" rows="2"
                    cols="30">
          </textarea>
        </div>

        <div>
          Segmenteerimise tulemus:
          <div v-if="sending && !sendingDone">
            <div>
              <SpinnerLoader :color="'#54f1d2'"/>
              <br>
              <p>Segmenteerimise ja silbitamise teenused laevad ~15 sekundit, olenevalt lause pikkusest</p>
            </div>
          </div>
          <div v-else-if="sendingDone">
            <a class="btn btn-success mx-1" v-bind:href="getAudioDownloadLink" download>Lae alla lindistus</a>
            <a class="btn btn-success mx-1" v-bind:href="getTextGridDownloadLink" download>Lae alla textgrid</a>
            <br><span>Silbitatud lause:</span><br>
            <p>{{ results.syllables }}</p>
          </div>
          <div>

          </div>
        </div>

      </div>
      <div class="col-md-6">
        <!--     COLUMN 2  -->
        <div>
          <button class="btn btn-primary mx-1" ref="start_record" v-if="!isRecording" :disabled="sending"
                  @click="startRecord">Lindista
          </button>
          <button class="btn btn-danger" ref="stop_record" v-else @click="stopRecord">Lõpeta</button>
        </div>

        <div>
          <template v-if="audioData.audioSource">
            <audio :src="audioData.audioSource" controls title="admin.wav" type="audio/wav"></audio>
          </template>
        </div>

        <div>
          <span>Lause keerukus</span><br>
          <select :disabled="sending" v-model="sentence.difficultyId">
            <option :value="null" disabled style="display:none">Valige keerukus</option>
            <option v-for="option in difficulties" :key="option.id" v-bind:value="option.id">
              {{ option.name }}
            </option>
          </select>
        </div>

        <div>
          <div v-if="!sending">
            <button class="btn btn-primary mx-1" :disabled="isRecording" @click="sendToServer">Salvesta</button>
          </div>
          <div v-else>
            <p>Saadetud</p>
            <p v-if="!sendingDone">Saatmise tulemust ei pea ära ootama ja võite juba järgmise lisamist alustada</p>
            <button class="btn btn-primary" @click="resetComponentData">Lisa järgmine</button>
          </div>
        </div>
      </div>

    </div>
    <div class="container" v-if="errors.length">
      <div class="row">
        <div class="align-self-center">

          <ul class="list-group">
            <li class="list-group-item list-group-item-action list-group-item-danger" v-for="(error) in errors"
                :key="error.id">{{ error }}
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!--    <vue-dictaphone-spectrum-analyser/>-->
  </div>
</template>

<script>
import axios from "axios";
import SpinnerLoader from '@bit/joshk.vue-spinners-css.spinner-loader';
import {Endpoints} from "@/general/endpoints";
import {MediaRecorder, register} from "extendable-media-recorder";
import {connect} from "extendable-media-recorder-wav-encoder";


export default {
  name: "AdminAddSentence",
  components: {
    SpinnerLoader
  },
  data() {
    return {
      tabControls: {
        adding: false,
        manual: false
      },
      errors: [],
      sending: false,
      sendingDone: false,
      isRecording: false,
      recording: false,
      sentence: {
        text: null,
        difficultyId: null,
        audioBlob: null
      },
      results: {
        syllables: null,
        sentenceId: null
      },
      difficulties: [
        {id: 0, name: "Lihtne, 5-6 aastastele"},
        {id: 1, name: "Keskmine, 7-10 aastastele"},
        {id: 2, name: "Raske, 10+ aastastele"}
      ],
      audioData: {
        audioSource: null,
        stream: null,
        recordedChunks: null,
        mediaRecorder: null
      },
    }
  },
  computed: {
    getTextGridDownloadLink: function () {
      return Endpoints.SENTENCE_GET_TEXTGRID + this.results.sentenceId
    },
    getAudioDownloadLink: function () {
      return Endpoints.SENTENCE_GET_RECORDING + this.results.sentenceId
    },
    getToken() {
      return this.$localStorage.token
    }
  },
  methods: {
    async startRecord() {
      this.isRecording = true
      if(this.audioData.stream != null) {
        this.audioData.stream.getTracks().forEach(track => track.stop())
        this.audioData.stream = null;
      }
      this.audioData.recordedChunks = [];

      var options = {mimeType: "audio/wav"};
      try {
        await register(await connect())
      } catch (err) {
        console.log("Already exists")
      }

      this.audioData.stream = await navigator.mediaDevices.getUserMedia({audio: true});

      this.audioData.mediaRecorder = new MediaRecorder(this.audioData.stream, options);

      this.audioData.mediaRecorder.ondataavailable = ((e) => this.handleDataAvailable(e));
      this.audioData.mediaRecorder.start();
    },
    stopRecord() {
      this.audioData.mediaRecorder.stop();
      this.isRecording = false;
    },
    handleDataAvailable(event) {
      if (event.data.size > 0) {
        this.audioData.recordedChunks.push(event.data);
        this.setAudioSrc();
      } else {
        console.log("data size 0")
      }
    },
    setAudioSrc() {
      var blob = new Blob(this.audioData.recordedChunks, {
        type: "audio/wav"
      });
      this.sentence.audioBlob = blob
      this.audioData.audioSource = URL.createObjectURL(blob);
      this.audioData.stream.getTracks().forEach(track => track.stop())
    },
    sendToServer() {
      this.errors = []

      if (this.sentence.text === null || this.sentence.text === "") {
        this.errors.push("Lause ei tohi olla tühi")
      }
      if (this.sentence.difficultyId === null) {
        this.errors.push("Palun valige lause raskusaste")
      }
      if (this.sentence.audioBlob === null) {
        this.errors.push("Palun lindistage lause")
      }

      if (this.errors.length === 0) {
        this.sending = true;

        let data = new FormData()
        data.append('audio', this.sentence.audioBlob)
        data.append("text", this.sentence.text)
        data.append("userToken", this.getToken)
        data.append("difficulty", this.sentence.difficultyId)

        let config = {
          header: {
            "Config-Type": 'multipart/form-data'
          }
        }

        axios.post(Endpoints.SENTENCE_ADD, data, config)
            .then((response) => {
              console.log(response)
              this.results.sentenceId = response.data.id
              this.results.syllables = response.data.textAsSyllables
              this.sendingDone = true
            }).catch((error) => {
          console.log(error)
          this.error = "Serveri viga, palun proovige hiljem uuesti."
          try {
            if (error.response && "message" in error.response.data) {
              this.error = error.response.data.message
            } else {
              this.error = "Serveri viga, palun proovige hiljem uuesti."
            }
          } catch (e) {
            this.error = "Serveri viga, palun proovige hiljem uuesti."
          }
        });
      }
    },
    handleRecording({blob, src}) {
      this.audioSource = src;
      console.log(src)

      console.log(blob)
      var reader = new window.FileReader();
      reader.readAsDataURL(blob);
      this.sentence.audioBlob = blob
    },
    resetData() {
      console.log('reset data')
      this.results.syllables = null
      this.results.textgrid = null
      this.sentence.difficultyId = null
      this.audioData.audioSource = null
      this.sentence.audioBlob = null
    },
    resetComponentData() {
      this.audioData.stream.getTracks().forEach(track => track.stop())
      Object.assign(this.$data, this.$options.data.apply(this))
      this.tabControls.adding = true
    },
    downloadTextgrid() {

    }
  },
  beforeDestroy() {
    if(this.audioData.stream != null) {
      this.audioData.stream.getTracks().forEach(track => track.stop())
    }
  }
}
</script>