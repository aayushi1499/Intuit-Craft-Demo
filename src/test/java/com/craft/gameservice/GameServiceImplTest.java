package com.craft.gameservice;

import com.craft.gameservice.entity.PlayerScore;
import com.craft.gameservice.exceptions.InitializationException;
import com.craft.gameservice.services.GameServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameServiceImplTest {

	private GameServiceImpl gameService;

	@BeforeEach
	public void setUp() {
		gameService = new GameServiceImpl();
	}

	@Test
	public void testInitialize() throws InitializationException {
		List<PlayerScore> dataSet = Arrays.asList(
				new PlayerScore("player1", 500),
				new PlayerScore("player2", 400),
				new PlayerScore("player3", 300)
		);

		assertThrows(InitializationException.class, () -> {
			gameService.initialize(5, dataSet);
		});

		gameService.initialize(3, dataSet);
	}

	@Test
	public void testGetTopNplayers() throws InitializationException {
		List<PlayerScore> dataSet = Arrays.asList(
				new PlayerScore("player1", 500),
				new PlayerScore("player2", 400),
				new PlayerScore("player3", 300)
		);

		gameService.initialize(3, dataSet);
		List<PlayerScore> topPlayers = gameService.getTopPlayers();
		assertEquals(3, topPlayers.size());
		assertEquals("player1", topPlayers.get(0).getPlayerId());
		assertEquals("player2", topPlayers.get(1).getPlayerId());
		assertEquals("player3", topPlayers.get(2).getPlayerId());
	}

	@Test
	public void testUpdatePlayerScore() throws InitializationException {
		List<PlayerScore> dataSet = Arrays.asList(
				new PlayerScore("player1", 500),
				new PlayerScore("player2", 400),
				new PlayerScore("player3", 300)
		);

		gameService.initialize(3, dataSet);
		gameService.updatePlayer(new PlayerScore("player2", 600));
		List<PlayerScore> topPlayers = gameService.getTopPlayers();
		assertEquals(3, topPlayers.size());
		assertEquals("player2", topPlayers.get(0).getPlayerId());
		assertEquals(600, topPlayers.get(0).getScore());

		assertThrows(IllegalArgumentException.class, () -> {
			gameService.updatePlayer(new PlayerScore("player4", 700));
		});
	}
}
