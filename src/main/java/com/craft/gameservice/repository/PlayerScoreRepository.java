package com.craft.gameservice.repository;

import com.craft.gameservice.entity.PlayerScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PlayerScoreRepository {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public List<PlayerScore> findAll() {
        List<PlayerScore> playerScores = new ArrayList<>();
        try {
            ScanOptions scanOptions = ScanOptions.scanOptions().build();
            Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions);
            while (cursor.hasNext()) {
                String keyStr = new String(cursor.next());
                Long value = new Long((int) redisTemplate.opsForValue().get(keyStr));
                PlayerScore newPlayerScore = new PlayerScore(keyStr, value);
                playerScores.add(newPlayerScore);
            }
        } finally {}

        return playerScores;
    }

    public void deleteAll() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    public void save(PlayerScore newScore) {
        redisTemplate.opsForValue().set(newScore.getPlayerId(), newScore.getScore());
    }

    public Optional<PlayerScore> findById(String playerId) {
        Object score = redisTemplate.opsForValue().get(playerId);
        if (score instanceof Long) {
            return Optional.of(new PlayerScore(playerId, (Long) score));
        } else if (score instanceof Integer) {
            return Optional.of(new PlayerScore(playerId, ((Integer) score).longValue()));
        } else {
            return Optional.empty();
        }
    }
}