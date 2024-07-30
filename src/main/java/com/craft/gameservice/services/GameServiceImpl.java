package com.craft.gameservice.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.craft.gameservice.repository.PlayerScoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.craft.gameservice.entity.PlayerScore;
import com.craft.gameservice.exceptions.InitializationException;

@Service
public class GameServiceImpl implements GameService<PlayerScore> {

	int topN;
	PriorityQueue<PlayerScore> minHeap;
	Map<String, PlayerScore> playerToScore;

	@Autowired
	PlayerScoreRepository playerScoreRepository;
	
	Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

	public void initialize(int topN, List<PlayerScore> dataSet) throws InitializationException {
		if (topN > dataSet.size()) {
			throw new InitializationException("TopN cannot be greater than the size of the dataset");
		}
		this.topN = topN;
		try {
			minHeap = new PriorityQueue<PlayerScore>();
			playerToScore = new HashMap<String, PlayerScore>();
			for (PlayerScore score : dataSet) {
				if (minHeap.size() < topN) {
					minHeap.add(score);
					playerToScore.put(score.getPlayerId(), score);
				} else {
					if (score.getScore() > minHeap.peek().getScore()) {
						PlayerScore removedScore = minHeap.poll();
						minHeap.add(score);
						playerToScore.remove(removedScore.getPlayerId());
						playerToScore.put(score.getPlayerId(), score);
					}
				}
			}
			logger.info("Scores in dataset: " + dataSet);
		} catch (Exception e) {
			logger.error("Failed to initialize - " + e.getMessage());
			throw new InitializationException("Failed to initialize");
		}
	}

	public List<PlayerScore> getTopPlayers() {
		List<PlayerScore> res = new ArrayList<PlayerScore>(minHeap);
		Collections.sort(res, Collections.reverseOrder());
		logger.info("Top players: " + res);
		return res;
	}

	@Override
	public void updatePlayer(PlayerScore player) {
		if (!playerToScore.containsKey(player.getPlayerId())) {
			addPlayer(player);
		} else {
			PlayerScore existingScore = playerToScore.get(player.getPlayerId());
			minHeap.remove(existingScore);
			playerToScore.put(player.getPlayerId(), player);
			addPlayer(player);
		}
	}

	private void addPlayer(PlayerScore player) {
		if (minHeap.size() < topN) {
			minHeap.add(player);
			playerToScore.put(player.getPlayerId(), player);
		} else {
			if (player.getScore() > minHeap.peek().getScore()) {
				PlayerScore removedScore = minHeap.poll();
				minHeap.add(player);
				playerToScore.remove(removedScore.getPlayerId());
				playerToScore.put(player.getPlayerId(), player);
			}
		}
	}

}
