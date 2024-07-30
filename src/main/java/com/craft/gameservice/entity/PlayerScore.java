package com.craft.gameservice.entity;

public class PlayerScore implements Comparable<PlayerScore> {

	String playerId;
	Long score;
	
	public PlayerScore() {
	}

	public PlayerScore(String playerId, long score) {
		this.playerId = playerId;
		this.score = score;
	}
	public long getScore() {
		return this.score;
	}
	public String getPlayerId() {
		return playerId;
	}
	
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public void setScore(Long score) {
		this.score = score;
	}

	public int compareTo(PlayerScore p) {
		if (this.score == p.getScore()) {
			return this.playerId.compareTo(p.getPlayerId());
		}
		return Long.compare(this.score, p.getScore());
	}
	
	@Override
	public String toString() {
		return "{" + playerId + " " + score + "}";
	}
	
	@Override
	public boolean equals(Object o) {
		return this.playerId.equals(((PlayerScore)o).getPlayerId())
				&& this.score == ((PlayerScore)o).getScore();
	}
}
