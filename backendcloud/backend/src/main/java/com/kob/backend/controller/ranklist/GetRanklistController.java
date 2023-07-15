package com.kob.backend.controller.ranklist;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.ranklist.GetRanklistService;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class GetRanklistController {
    @Resource
    private GetRanklistService getRanklistService;

    @GetMapping("/api/ranklist/getlist/")
    public JSONObject getList(@RequestParam @NotNull Map<String, String> data) {
        Integer page = Integer.parseInt(data.get("page"));
        return getRanklistService.getList(page);
    }
}
