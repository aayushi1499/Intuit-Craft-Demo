package com.craft.gameservice.services;

import java.util.List;

import com.craft.gameservice.entity.PlayerScore;
import com.craft.gameservice.exceptions.InitializationException;
import com.craft.gameservice.exceptions.LeaderboardNotInitializedException;

public interface LeaderBoardService {
	public void createBoard(int topN) throws InitializationException, LeaderboardNotInitializedException;
	public List<PlayerScore> getTopPlayers() throws LeaderboardNotInitializedException;
	PlayerScore updatePlayerScore(PlayerScore updatedScore) throws LeaderboardNotInitializedException;


}
