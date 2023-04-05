package com.kob.matchingsystem.service;

public interface MatchingService {
    String addPLayer(Integer userId, Integer rating);
    String removePlayer(Integer userId);
}
