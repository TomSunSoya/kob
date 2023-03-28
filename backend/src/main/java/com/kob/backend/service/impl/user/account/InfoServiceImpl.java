package com.kob.backend.service.impl.user.account;

import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.InfoService;
import com.kob.backend.utils.GetUserUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InfoServiceImpl implements InfoService {
    @Override
    public Map<String, String> getinfo() {
        User user = GetUserUtil.getUser();

        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("username", user.getUsername());
        map.put("photo", user.getPhoto());
        return map;
    }
}
