package com.craft.gameservice.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.craft.gameservice.entity.PlayerScore;
import com.craft.gameservice.exceptions.InitializationException;
import com.craft.gameservice.exceptions.LeaderboardNotInitializedException;
import com.craft.gameservice.repository.PlayerScoreRepository;
import com.craft.gameservice.ingester.PlayerScoreIngester;

@Service
public class LeaderBoardServiceImpl implements LeaderBoardService {

	@Autowired
    GameService<PlayerScore> service;

	@Autowired
	PlayerScoreRepository scoreRepository;

	@Autowired
	PlayerScoreIngester playerScoreIngester;

	boolean leaderBoardInitialized;

	Logger logger = LoggerFactory.getLogger(LeaderBoardServiceImpl.class);
	public void createBoard(int topN) throws LeaderboardNotInitializedException {
		try {
			List<PlayerScore> allScores = scoreRepository.findAll();
			service.initialize(topN, allScores);
			playerScoreIngester.createLeaderBoard(this);
			leaderBoardInitialized = true;
		} catch (InitializationException e) {
			logger.error("Leader Board Initialization Failed - " + e.getMessage());
			throw new LeaderboardNotInitializedException(e.getMessage());
		}
	}

	public List<PlayerScore> getTopPlayers() throws LeaderboardNotInitializedException {
		if (!leaderBoardInitialized) {
			logger.error("Leader Board Not Initialized - Cannot retrieve top players");
			throw new LeaderboardNotInitializedException("LeaderBoard not yet initialized");
		}
		return service.getTopPlayers();
	}

	public PlayerScore updatePlayerScore(PlayerScore updatedScore) {
		PlayerScore existingScore = scoreRepository.findById(updatedScore.getPlayerId())
				.orElseThrow(() -> new IllegalArgumentException("Player not found"));
		existingScore.setScore(updatedScore.getScore());
		scoreRepository.save(existingScore);
		if (leaderBoardInitialized) {
			service.updatePlayer(existingScore);
		}
		return existingScore;
	}

}
