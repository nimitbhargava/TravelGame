<template>
  <div class="container">
    <div class="row">
      <div class="col"></div>
      <div class="col-auto bg-light rounded m-3 p-3">
        <p>Lugemise karaoke on loodud IAPB bakalaureusetöö raames laste lugemisoskuse treenimiseks
          lugemisharjutustega.</p>
        <p>Harjutustega alustamiseks palun logige sisse või <router-link to="/register">registreerige ennast kasutajaks.</router-link></p>
        <p>Harjutades püüa lugeda punaselt märgitud sõna etteantud kiirusega.</p>
        <hr>
        <h3 class="normal">Loe <span class="highlighted">punaselt</span> märgitud sõna</h3>
        <hr>
        <br>
        <p>Kui tekst on liiga aeglane või kiire saad kiirust muuta.<br>
        Kui leiad, et laused on liiga lihtsad vali järgmine tase.<br>
        Pärast lause lugemist võid kuulata lause salvestust.</p>
        <p>'Tulemused' vaate all näed infot oma eelnevate kordade kohta.</p>
        <h3>Head harjutamist!</h3>
      </div>
      <div class="col"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Instructions"
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

.fade-enter, .fade-leave-to {
  opacity: 0;
}

</style>