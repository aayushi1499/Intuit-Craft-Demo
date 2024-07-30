package com.craft.gameservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.craft.gameservice.entity.playerScore;
import com.craft.gameservice.exceptions.LeaderboardNotInitializedException;
import com.craft.gameservice.services.LeaderBoardService;

@RestController
public class GameController {
	
	@Autowired
	LeaderBoardService leaderBoard;
	
	Logger logger = LoggerFactory.getLogger(GameController.class);

	@GetMapping("/getTopScorers/{N}")
	public List<playerScore> getTopScorers(@PathVariable String N) {
		int n;
		try {
			n = Integer.parseInt(N);
		} catch (NumberFormatException e) {
			logger.error("Invalid number format for N: " + N);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid number format for N");
		}

		try {
			leaderBoard.createBoard(n);
			return leaderBoard.getTopPlayers();
		} catch (LeaderboardNotInitializedException e) {
			logger.error("Leaderboard is not initialized - " + e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please first create a LeaderBoard");
		} catch (Exception e) {
			logger.error("Couldn't get top scores - " + e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@PutMapping("/updatePlayerScore")
	public playerScore updatePlayerScore(@RequestBody playerScore updatedScore) {
		try {
			return leaderBoard.updatePlayerScore(updatedScore);
		} catch (IllegalArgumentException e) {
			logger.error("Player not found - " + e.getMessage());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
		} catch (Exception e) {
			logger.error("Couldn't update player score - " + e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating player score");
		}
	}


	@ExceptionHandler(ResponseStatusException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleResponseStatusException(ResponseStatusException ex) {
		return new ErrorResponse(ex.getStatus().value(), ex.getReason());
	}

	public static class ErrorResponse {
		private int status;
		private String error;

		public ErrorResponse(int status, String error) {
			this.status = status;
			this.error = error;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}
	}
}