package com.craft.gameservice.services;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.craft.gameservice.entity.PlayerScore;
import com.craft.gameservice.exceptions.InitializationException;

@Service
public class GameServiceImpl implements GameService<PlayerScore> {

	private int topN;
	private PriorityQueue<PlayerScore> minHeap;
	private Map<String, PlayerScore> playerToScore;

	private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

	public void initialize(int topN, List<PlayerScore> dataSet) throws InitializationException {
		if (topN > dataSet.size()) {
			throw new InitializationException("TopN cannot be greater than the size of the dataset");
		}
		this.topN = topN;
		this.minHeap = new PriorityQueue<>(Comparator.comparingLong(PlayerScore::getScore));
		this.playerToScore = new HashMap<>();

		try {
			for (PlayerScore score : dataSet) {
				addPlayer(score);
			}
			logger.info("Scores in dataset: " + dataSet);
		} catch (Exception e) {
			logger.error("Failed to initialize - " + e.getMessage());
			throw new InitializationException("Failed to initialize");
		}
	}

	public List<PlayerScore> getTopPlayers() {
		List<PlayerScore> res = new ArrayList<>(minHeap);
		res.sort(Collections.reverseOrder());
		logger.info("Top players: " + res);
		return res;
	}

	@Override
	public void updatePlayer(PlayerScore player) {
		if (player == null || !playerToScore.containsKey(player.getPlayerId())) {
			throw new IllegalArgumentException("Player with id " + player.getPlayerId() + " not found");
		}

		PlayerScore existingScore = playerToScore.get(player.getPlayerId());
		if (existingScore == null) {
			throw new NoSuchElementException("Player ID not found: " + player.getPlayerId());
		}
		minHeap.remove(existingScore);
		playerToScore.put(player.getPlayerId(), player);
		addPlayer(player);
	}

	private void addPlayer(PlayerScore player) {
		if (player == null) {
			throw new IllegalArgumentException("Player cannot be null");
		}

		if (minHeap.size() < topN) {
			minHeap.add(player);
			playerToScore.put(player.getPlayerId(), player);
		} else if (player.getScore() > minHeap.peek().getScore()) {
			PlayerScore removedScore = minHeap.poll();
			minHeap.add(player);
			playerToScore.remove(removedScore.getPlayerId());
			playerToScore.put(player.getPlayerId(), player);
		}
	}


}
