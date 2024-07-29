package com.craft.gameservice.ingester;

import com.craft.gameservice.constants.Constants;
import com.craft.gameservice.entity.playerScore;
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
public class PlayerScoreIngester implements Ingester<playerScore> {
    List<LeaderBoardService> leaderBoards = new ArrayList<LeaderBoardService>();

    @Autowired
    PlayerScoreRepository scoreRepository;
    Logger logger = LoggerFactory.getLogger(PlayerScoreIngester.class);

    @Override
    @Scheduled(fixedRate = 5000)
    public void ingest() {
        try (FileReader reader = new FileReader(Constants.CSV_FILE_PATH)) {
            List<playerScore> scores = new CsvToBeanBuilder<playerScore>(reader)
                    .withType(playerScore.class)
                    .build()
                    .parse();

            for (playerScore score : scores) {
                publishToRedis(score);
                logger.debug("Published " + score);
            }
        } catch (IOException e) {
            logger.error("Could not read CSV file - " + e.getMessage());
        } catch (Exception e) {
            logger.error("Could not publish new score - " + e.getMessage(),e);
        }
    }

    public void publishToRedis(playerScore newScore) throws DatabaseStorageException {
        try {
            scoreRepository.save(newScore);
        } catch (Exception e) {
            logger.error("message " + e.getMessage());
            throw new DatabaseStorageException("Could not publish data to storage");
        }

    }

    public void createLeaderBoard(LeaderBoardService leaderBoard) {
        leaderBoards.add(leaderBoard);
    }

}


