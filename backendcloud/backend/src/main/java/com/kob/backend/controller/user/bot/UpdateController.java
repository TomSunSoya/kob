package com.kob.backend.controller.user.bot;

import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class UpdateController {
    @Resource
    private UpdateService updateService;

    @PostMapping("/api/user/bot/update/")
    public Map<String, String> update(@RequestParam Map<String, String> data) {
        return updateService.update(data);
    }
}
