package com.craft.gameservice;

import com.craft.gameservice.entity.PlayerScore;
import com.craft.gameservice.exceptions.InitializationException;
import com.craft.gameservice.services.GameServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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
			gameService.initialize(5, dataSet);  // topN is greater than dataSet size
		});

		gameService.initialize(3, dataSet);  // No exception should be thrown
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

		// Update player2's score to 600
		gameService.updatePlayer(new PlayerScore("player2", 600));
		List<PlayerScore> topPlayers = gameService.getTopPlayers();
		assertEquals(3, topPlayers.size());
		assertEquals("player2", topPlayers.get(0).getPlayerId());
		assertEquals(600, topPlayers.get(0).getScore());
		assertEquals("player1", topPlayers.get(1).getPlayerId());
		assertEquals("player3", topPlayers.get(2).getPlayerId());

		// Update player4's score to 700 (new player)
		gameService.updatePlayer(new PlayerScore("player4", 700));
		topPlayers = gameService.getTopPlayers();
		assertEquals(3, topPlayers.size());
		assertEquals("player4", topPlayers.get(0).getPlayerId());
		assertEquals(700, topPlayers.get(0).getScore());
		assertEquals("player2", topPlayers.get(1).getPlayerId());
		assertEquals(600, topPlayers.get(1).getScore());
		assertEquals("player1", topPlayers.get(2).getPlayerId());
	}
}
