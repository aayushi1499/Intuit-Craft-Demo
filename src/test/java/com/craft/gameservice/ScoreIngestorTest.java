package com.craft.gameservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.craft.gameservice.entity.playerScore;
import com.craft.gameservice.exceptions.DatabaseStorageException;
import com.craft.gameservice.ingester.PlayerScoreIngester;
import com.craft.gameservice.services.LeaderBoardService;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.craft.gameservice.exceptions.InitializationException;
import com.craft.gameservice.exceptions.LeaderboardNotInitializedException;
import com.craft.gameservice.repository.PlayerScoreRepository;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = GameApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ScoreIngestorTest {

	@Autowired
	LeaderBoardService leaderBoard;

	@Autowired
	PlayerScoreIngester playerScoreIngester;

	@Autowired
	PlayerScoreRepository scoreRepository;

	@Test
	public void test() {
		try {
			// Clear any existing scores
			scoreRepository.deleteAll();

			// Insert test data into the repository
			List<playerScore> testScores = Arrays.asList(
					new playerScore("Aayushi", 100),
					new playerScore("Priyanka", 200),
					new playerScore("Ravi", 300)
			);

			for (playerScore score : testScores) {
				scoreRepository.save(score);
			}

			// Initialize leaderboard
			leaderBoard.createBoard(3);

			// Publish scores to Redis
			playerScoreIngester.publishToRedis(new playerScore("Aayushi", 100));
			playerScoreIngester.publishToRedis(new playerScore("Priyanka", 200));
			playerScoreIngester.publishToRedis(new playerScore("Ravi", 300));

			// Allow some time for Redis to process the scores
			Thread.sleep(2000); // Adjust this as needed for your environment

			// Refresh leaderboard with the latest scores from Redis
//			leaderBoard.updateLeaderboard();

			// Print scores for debugging
			System.out.println("Published Scores to Redis:");
			for (playerScore p : testScores) {
				System.out.println(p);
			}

			List<playerScore> actualScores = leaderBoard.getTopPlayers();

			// Print actual scores for debugging
			System.out.println("Actual Scores from LeaderBoard:");
			for (playerScore p : actualScores) {
				System.out.println(p);
			}

			// Define expected output
			List<playerScore> expectedScores = Arrays.asList(
					new playerScore("Ravi", 300),
					new playerScore("Priyanka", 200),
					new playerScore("Aayushi", 100)
			);

			// Verify top players
			assertEquals(expectedScores.size(), actualScores.size());

			for (int i = 0; i < expectedScores.size(); i++) {
				assertEquals(expectedScores.get(i), actualScores.get(i));
			}

		} catch (DatabaseStorageException | InitializationException | LeaderboardNotInitializedException | InterruptedException e) {
			fail(e.getMessage());
		}
	}

	@After
	public void tearDown() {
		scoreRepository.deleteAll();
	}
}
