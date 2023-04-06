package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Integer id;
    private Integer botId;      // -1表示人工
    private String botCode;
    private Integer sx;
    private Integer sy;
    private String username;
    private List<Integer> steps;

     private boolean checkTailIncreasing(int step) {
         if (step <= 10) return true;
         return step % 3 == 1;
     }

    public List<Cell> getCells() {
        List<Cell> res = new ArrayList<>();

        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for (int d : steps) {
            x += Game.dx[d];
            y += Game.dy[d];
            res.add(new Cell(x, y));
            if (!checkTailIncreasing(++step))
                res.remove(0);
        }
        return res;
    }

    public String getStepsString() {
         StringBuilder res = new StringBuilder();
         for (int d : steps)
             res.append(d);
         return res.toString();
    }
}
