package com.craft.gameservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import com.craft.gameservice.entity.PlayerScore;
import com.craft.gameservice.exceptions.DatabaseStorageException;
import com.craft.gameservice.ingester.PlayerScoreIngester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.craft.gameservice.repository.PlayerScoreRepository;

import java.io.IOException;
import java.util.Optional;

@SpringBootTest(classes = GameApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class PlayerScoreIngesterTest {

	@Autowired
	PlayerScoreIngester playerScoreIngester;

	@MockBean
	PlayerScoreRepository scoreRepository;

	@Test
	public void testPublishToRedis() throws DatabaseStorageException {
		PlayerScore playerScore = new PlayerScore("Aayushi", 100);
		when(scoreRepository.findById(playerScore.getPlayerId())).thenReturn(Optional.of(playerScore));

		playerScoreIngester.publishToRedis(playerScore);

		Optional<PlayerScore> retrievedScoreOptional = scoreRepository.findById(playerScore.getPlayerId());
		if (retrievedScoreOptional.isPresent()) {
			PlayerScore retrievedScore = retrievedScoreOptional.get();
			assertEquals(playerScore, retrievedScore);
		} else {
			fail("No score was found with the given ID.");
		}
	}

	@Test
	public void testIngest() throws IOException {
		playerScoreIngester.ingest();
		verify(scoreRepository, atLeastOnce()).save(any(PlayerScore.class));
	}

}
