package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.bot.RemoveService;
import com.kob.backend.utils.GetUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoveServiceImpl implements RemoveService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> remove(Map<String, String> data) {
        User user = GetUserUtil.getUser();
        int bot_id;
        Map<String, String> map = new HashMap<>();
        try {
            bot_id = Integer.parseInt(data.get("bot_id"));
        } catch (NumberFormatException e) {
            bot_id = -1;
        }
        Bot bot = botMapper.selectById(bot_id);

        if (bot == null) {
            map.put("error_message", "该BOT不存在或已被删除");
            return map;
        }

        if (!bot.getUserId().equals(user.getId())) {
            map.put("error_message", "没有权限删除该BOT");
            return map;
        }

        botMapper.deleteById(bot_id);
        map.put("error_message", "success");
        return map;
    }
}
