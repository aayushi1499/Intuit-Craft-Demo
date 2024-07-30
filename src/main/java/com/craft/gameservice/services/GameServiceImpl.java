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

import com.craft.gameservice.entity.playerScore;
import com.craft.gameservice.exceptions.InitializationException;

@Service
public class GameServiceImpl implements GameService<playerScore> {

	int topN;
	PriorityQueue<playerScore> minHeap;
	Map<String, playerScore> playerToScore;

	@Autowired
	PlayerScoreRepository playerScoreRepository;
	
	Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

	public void initialize(int topN, List<playerScore> dataSet) throws InitializationException {
		if (topN > dataSet.size()) {
			throw new InitializationException("TopN cannot be greater than the size of the dataset");
		}
		this.topN = topN;
		try {
			minHeap = new PriorityQueue<playerScore>();
			playerToScore = new HashMap<String, playerScore>();
			for (playerScore score : dataSet) {
				if (minHeap.size() < topN) {
					minHeap.add(score);
					playerToScore.put(score.getPlayerId(), score);
				} else {
					if (score.getScore() > minHeap.peek().getScore()) {
						playerScore removedScore = minHeap.poll();
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

	public List<playerScore> getTopPlayers() {
		List<playerScore> res = new ArrayList<playerScore>(minHeap);
		Collections.sort(res, Collections.reverseOrder());
		logger.info("Top players: " + res);
		return res;
	}

	@Override
	public void updatePlayer(playerScore player) {
		if (!playerToScore.containsKey(player.getPlayerId())) {
			addPlayer(player);
		} else {
			playerScore existingScore = playerToScore.get(player.getPlayerId());
			minHeap.remove(existingScore);
			playerToScore.put(player.getPlayerId(), player);
			addPlayer(player);
		}
	}

	private void addPlayer(playerScore player) {
		if (minHeap.size() < topN) {
			minHeap.add(player);
			playerToScore.put(player.getPlayerId(), player);
		} else {
			if (player.getScore() > minHeap.peek().getScore()) {
				playerScore removedScore = minHeap.poll();
				minHeap.add(player);
				playerToScore.remove(removedScore.getPlayerId());
				playerToScore.put(player.getPlayerId(), player);
			}
		}
	}

}
