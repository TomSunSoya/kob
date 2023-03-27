<template>
  <NavBar />
  <router-view/>
</template>

<script>
import NavBar from './components/NavBar';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap';

export default {
  components: {
    NavBar
  },
  setup() {

    
  }
}

</script>

<style>
</style>
