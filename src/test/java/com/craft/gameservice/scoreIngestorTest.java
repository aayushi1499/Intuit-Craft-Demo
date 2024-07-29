package com.craft.gameservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.craft.gameservice.entity.playerScore;
import com.craft.gameservice.exceptions.DatabaseStorageException;
import com.craft.gameservice.ingester.PlayerScoreIngester;
import com.craft.gameservice.services.LeaderBoardService;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.craft.gameservice.exceptions.InitializationException;
import com.craft.gameservice.exceptions.LeaderboardNotInitializedException;
import com.craft.gameservice.repository.PlayerScoreRepository;

@SpringBootTest(classes = GameApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class scoreIngestorTest {
	@Autowired
	LeaderBoardService leaderBoard;
	@Autowired
	PlayerScoreIngester playerScoreIngester;
	@Autowired
    PlayerScoreRepository scoreRepository;
	
	@Test
	public void test() {
		try {
			try {
				try {
					playerScoreIngester.publishToRedis(new playerScore("OP", 700));
				} catch (DatabaseStorageException e) {
					fail(e.getMessage());
				}
			} catch (Exception e) {
				fail(e.getMessage());
			}
			for (playerScore p : leaderBoard.getTopPlayers())
				assertEquals(p, new playerScore("OP", 700));
			try {
				leaderBoard.createBoard(3);
			} catch (InitializationException e) {
				fail(e.getMessage());
			}
			try {
				try {
					playerScoreIngester.publishToRedis(new playerScore("OP", 600));
					playerScoreIngester.publishToRedis(new playerScore("GB", 700));
					playerScore[] outputList = { new playerScore("OP", 700), new playerScore("GB", 700), new playerScore("IS", 500)};
					int i = 0;
					for (playerScore p : leaderBoard.getTopPlayers()) {
						assertEquals(p, outputList[i++]);
					}
				} catch (DatabaseStorageException e) {
					fail(e.getMessage());
				}
			} catch (Exception e) {
				fail(e.getMessage());
			}
			
		
			for (playerScore p : leaderBoard.getTopPlayers())
				System.out.println(p);
		} catch (LeaderboardNotInitializedException e) {
			fail(e.getMessage());
		}	
	}
	
	@After
	public void tearDown() {
		scoreRepository.deleteAll();
	}

}
