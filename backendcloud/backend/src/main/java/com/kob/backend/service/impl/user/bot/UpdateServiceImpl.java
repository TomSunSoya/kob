package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.bot.UpdateService;
import com.kob.backend.utils.GetUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> update(Map<String, String> data) {
        User user = GetUserUtil.getUser();
        int bot_id;
        try {
            bot_id = Integer.parseInt(data.get("bot_id"));
        } catch (NumberFormatException e) {
            bot_id = -1;
        }

        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();

        if (title == null || title.length() == 0) {
            map.put("error_message", "标题不能为空");
            return map;
        }
        if (title.length() > 100) {
            map.put("error_message", "标题长度不能大于100");
            return map;
        }

        if (description == null || description.length() == 0)
            description = "这个用户很懒，什么也没留下~";
        if (description.length() > 300) {
            map.put("error_message", "描述长度不能大于300");
            return map;
        }

        if (content == null || content.length() == 0) {
            map.put("error_message", "代码长度不能为空");
            return map;
        }
        if (content.length() > 10000) {
            map.put("error_message", "代码长度不能大于10000");
            return map;
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

        Bot new_bot = new Bot(
                bot_id,
                user.getId(),
                title,
                description,
                content,
                bot.getCreatetime(),
                new Date()
        );
        botMapper.updateById(new_bot);

        map.put("error_message", "success");
        return map;
    }
}
