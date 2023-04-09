package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import lombok.var;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private final Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private final ReentrantLock lock = new ReentrantLock();
    private String status = "playing";          // playing -> finished
    private String loser = "";                  // All -> A -> B
    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";
    public static final int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    public Game(
            Integer rows,
            Integer cols,
            Integer inner_walls_count,
            Integer idA,
            Bot botA,
            Integer idB,
            Bot botB,
            String usernameA,
            String usernameB
    ) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if (botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if (botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, usernameA, new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, usernameB, new ArrayList<>());
    }

    public int[][] getG() {
        return g;
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    private boolean checkConnectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; ++i) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0)
                if (checkConnectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
        }
        g[sx][sy] = 0;
        return false;
    }

    private boolean draw() {
        for (int i = 0; i < this.rows; ++i)
            Arrays.fill(g[i], 0);

        for (int r = 0; r < this.rows; ++r)
            g[r][0] = g[r][this.cols - 1] = 1;
        for (int c = 0; c < this.cols; ++c)
            g[0][c] = g[this.rows - 1][c] = 1;

        Random random = new Random();
        for (int i = 0; i < this.inner_walls_count / 2; ++i) {
            for (int j = 0; j < 1000; ++j) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) continue;
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }
        return checkConnectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for (int i = 0; i < 1000; ++i)
            if (draw()) break;
    }

    @Contract(pure = true)
    private @NotNull String getInput(Player player) {
        Player me, you;
        if (playerA.getId().equals(player.getId())) {
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }
        return getMapString() + "#" + me.getSx() + "#" + me.getSy() + "#(" + me.getStepsString() + ")#" +
                you.getSx() + "#" + you.getSy() + "#(" + you.getStepsString() + ")";
    }

    private void sendBotCode(@NotNull Player player) {
        if (player.getBotId() == -1) return;
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));

        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    private boolean nextStep() {        // 定义两名玩家的下一步操作
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for (int i = 0; i < 50; ++i) {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void sendAllMessage(String message) {
        if (WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if (WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private @NotNull String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; ++i)
            for (int j = 0; j < cols; ++j)
                res.append(g[i][j]);
        return res.toString();
    }

    private void updateUserRating(Player player, Integer rating) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    private void saveToDatabase() {
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();

        if ("A".equals(loser)) {
            ratingA -= 2;
            ratingB += 5;
        } else if ("B".equals(loser)) {
            ratingA += 5;
            ratingB -= 2;
        }

        updateUserRating(playerA, ratingA);
        updateUserRating(playerB, ratingB);

        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult() {             // 向两名玩家广播结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    private boolean checkValid(List<Cell> cellsA, List<Cell> cellsB) {
        var n = cellsA.size();
        Cell cell = cellsA.get(n-1);
        if (g[cell.getX()][cell.getY()] == 1) return false;

        for (int i = 0; i < n-1; ++i)
            if (cellsA.get(i).getX() == cell.getX() && cellsA.get(i).getY() == cell.getY())
                return false;

        for (int i = 0; i < n-1; ++i)
            if (cellsB.get(i).getX() == cell.getX() && cellsB.get(i).getY() == cell.getY())
                return false;

        return true;
    }

    private void judge() {                  // 判断两名玩家是否合法
         List<Cell> cellsA = playerA.getCells();
         List<Cell> cellsB = playerB.getCells();

         boolean validA = checkValid(cellsA, cellsB);
         boolean validB = checkValid(cellsB, cellsA);
         if (!validA || !validB) {
             status = "finished";

             if (!validA && !validB)
                 loser = "all";
             else if (!validA)
                 loser = "A";
             else
                 loser = "B";
         }
    }

    private void sendMove() {               // 向两名玩家传递移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            System.out.println("a_dir" + nextStepA + "   b_dir" + nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; ++i) {
            if (nextStep()) {               // 是否获取了两条蛇的下一步操作
                judge();

                if (status.equals("playing")) {
                    sendMove();
                } else {
                    sendResult();
                    break;
                }
            } else {
                status = "finished";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null)
                        loser = "all";
                    else if (nextStepA == null)
                        loser = "A";
                    else
                        loser = "B";
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
