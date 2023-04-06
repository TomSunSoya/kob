package com.kob.matchingsystem.service;

public interface MatchingService {
    String addPLayer(Integer userId, Integer rating, Integer botId);
    String removePlayer(Integer userId);
}
