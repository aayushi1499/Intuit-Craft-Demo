package com.craft.gameservice;

import com.craft.gameservice.services.GameServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.craft.gameservice.entity.playerScore;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.craft.gameservice.exceptions.InitializationException;

public class GameServiceImplTest {

	private GameServiceImpl gameService;

	@BeforeEach
	public void setUp() {
		gameService = new GameServiceImpl();
	}

	@Test
	public void testInitialize() throws InitializationException {
		List<playerScore> dataSet = Arrays.asList(
				new playerScore("player1", 500),
				new playerScore("player2", 400),
				new playerScore("player3", 300)
		);

		assertThrows(InitializationException.class, () -> {
			gameService.initialize(5, dataSet);  // topN is greater than dataSet size
		});

		gameService.initialize(3, dataSet);  // No exception should be thrown
	}

	@Test
	public void testGetTopNplayers() {
		List<playerScore> dataSet = Arrays.asList(
				new playerScore("player1", 500),
				new playerScore("player2", 400),
				new playerScore("player3", 300)
		);

		// We create a scenario where initialization will fail, for instance, setting topN to a negative value.
		Exception exception = assertThrows(InitializationException.class, () -> {
			gameService.initialize(-3, dataSet);
		});

		String expectedMessage = "Failed to initialize";
		String actualMessage = exception.getMessage();

		assertEquals(expectedMessage, actualMessage);
	}
}