<template>
    <div class="result-board">
        <div class="result-board-text" v-if="$store.state.pk.loser === 'all'" >
            DRAW
        </div>
        <div class="result-board-text" v-else-if="$store.state.pk.loser === 'A' && $store.state.user.username === $store.state.pk.a_username" >
            LOSE
        </div>
        <div class="result-board-text" v-else-if="$store.state.pk.loser === 'B' && $store.state.user.username === $store.state.pk.b_username" >
            LOSE
        </div>
        <div class="result-board-text" v-else>
            WIN
        </div>

        <div class="result-board-btn">
            <button @click="restart" type="button" class="btn btn-info btn-large">
                重新匹配
            </button>
        </div>
    </div>
</template>

<script>
import { useStore } from 'vuex';

export default {
    setup() {
        const store = useStore();
        const restart = () => {
            store.commit("updateStatus", "matching"); 
            store.commit("updateLoser", "none");
            store.commit("updateOpponent", {
				username: "我的对手",
				photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
			});
        };

        return {
            restart
        };
    }
}

</script>

<style scoped>
div.result-board {
    height: 30vh;
    width: 30vw;
    background-color: rgba(44, 43, 43, 0.959);
    position: absolute;
    top: 30vh;
    left: 35vw;
    border-radius: 5%;
}

div.result-board-text {
    text-align: center;
    color: white;
    font-size: 50px;
    font-weight: 600;
    font-style: italic;
    padding-top: 5vh;
}

div.result-board-btn {
    padding-top: 7vh;
    text-align: center;
}
</style>