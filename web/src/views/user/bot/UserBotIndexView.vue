<template>
    <ContentField>我的BOT</ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField';
import $ from 'jquery';
import { useStore } from 'vuex';

export default {
  components: {
    ContentField,
  },
  setup() {
    const store = useStore();
    // $.ajax({
	// 	url: "http://localhost:3000/user/bot/add/",
	// 	type: "post",
	// 	data: {
	// 		title: "1qqewq1",
	// 		description: "",
	// 		content: "---",
	// 	},
	// 	headers: {
	// 		Authorization: "Bearer " + store.state.user.token,
	// 	},
	// 	success(resp) {
	// 		console.log(resp);
	// 	},
	// 	error(resp) {
	// 		console.log(resp);
	// 	}
    // })
	// $.ajax({
	// 	url: "http://localhost:3000/user/bot/remove/",
	// 	type: "post",
	// 	data: {
	// 		bot_id: 1,
	// 	},
	// 	headers: {
	// 		Authorization: "Bearer " + store.state.user.token,
	// 	},
	// 	success(resp) {
	// 		console.log(resp);
	// 	},
	// 	error(resp) {
	// 		console.log(resp);
	// 	}
    // })
	// $.ajax({
	// 	url: "http://localhost:3000/user/bot/update/",
	// 	type: "post",
	// 	data: {
	// 		bot_id: 1,
	// 		title: "new update",
	// 		description: "",
	// 		content: "Hello",
	// 	},
	// 	headers: {
	// 		Authorization: "Bearer " + store.state.user.token,
	// 	},
	// 	success(resp) {
	// 		console.log(resp);
	// 	},
	// 	error(resp) {
	// 		console.log(resp);
	// 	}
    // })
	$.ajax({
		url: "http://localhost:3000/user/bot/getlist/",
		type: "get",
		headers: {
			Authorization: "Bearer " + store.state.user.token,
		},
		success(resp) {
			console.log(resp);
		},
		error(resp) {
			console.log(resp);
		}
    })
  }
};
</script>

<style scoped>

</style>