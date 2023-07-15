package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.InfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class InfoController {
    @Resource
    private InfoService infoService;

    @GetMapping("/api/user/account/info/")
    public Map<String, String> getinfo() {
        return infoService.getinfo();
    }
}
