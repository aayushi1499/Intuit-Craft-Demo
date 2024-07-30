package com.craft.gameservice.repository;

import com.craft.gameservice.entity.playerScore;
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

    public List<playerScore> findAll() {
        List<playerScore> playerScores = new ArrayList<>();
        // Scan options with a pattern if needed
        try {
            ScanOptions scanOptions = ScanOptions.scanOptions().build();
            Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions);
            while (cursor.hasNext()) {
                // Convert the key to a string
                String keyStr = new String(cursor.next());
                // Fetch value for the key
                Long value = new Long((int) redisTemplate.opsForValue().get(keyStr));
                // Create playerScore and add to list
                playerScore newPlayerScore = new playerScore(keyStr, value);
                playerScores.add(newPlayerScore);
            }
        } finally {}

        return playerScores;
    }

    public void deleteAll() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    public void save(playerScore newScore) {
        redisTemplate.opsForValue().set(newScore.getPlayerId(), newScore.getScore());
    }

    public Optional<playerScore> findById(String playerId) {
        Object score = redisTemplate.opsForValue().get(playerId);
        if (score instanceof Long) {
            return Optional.of(new playerScore(playerId, (Long) score));
        } else if (score instanceof Integer) {
            return Optional.of(new playerScore(playerId, ((Integer) score).longValue()));
        } else {
            return Optional.empty();
        }
    }
}