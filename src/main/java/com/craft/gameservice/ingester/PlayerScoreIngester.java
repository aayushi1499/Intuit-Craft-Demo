package com.craft.gameservice.ingester;

import com.craft.gameservice.constants.Constants;
import com.craft.gameservice.entity.PlayerScore;
import com.craft.gameservice.exceptions.DatabaseStorageException;
import com.craft.gameservice.repository.PlayerScoreRepository;
import com.craft.gameservice.services.LeaderBoardService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerScoreIngester implements Ingester<PlayerScore> {

    @Autowired
    PlayerScoreRepository scoreRepository;
    Logger logger = LoggerFactory.getLogger(PlayerScoreIngester.class);

    @Override
    @Scheduled(fixedRate = 600000)
    public void ingest() {
        try (FileReader reader = new FileReader(Constants.CSV_FILE_PATH)) {
            List<PlayerScore> scores = new CsvToBeanBuilder<PlayerScore>(reader)
                    .withType(PlayerScore.class)
                    .build()
                    .parse();

            for (PlayerScore score : scores) {
                publishToRedis(score);
                logger.debug("Published " + score);
            }
        } catch (IOException e) {
            logger.error("Could not read CSV file - " + e.getMessage());
        } catch (Exception e) {
            logger.error("Could not publish new score - " + e.getMessage(),e);
        }
    }

    public void publishToRedis(PlayerScore newScore) throws DatabaseStorageException {
        try {
            scoreRepository.save(newScore);
        } catch (Exception e) {
            logger.error("message " + e.getMessage());
            throw new DatabaseStorageException("Could not publish data to storage");
        }
    }

}


