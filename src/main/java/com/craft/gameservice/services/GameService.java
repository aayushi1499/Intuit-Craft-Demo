package com.craft.gameservice.services;

import java.util.List;

import com.craft.gameservice.exceptions.InitializationException;


public interface GameService<T> {
	public void initialize(int topN, List<T> data) throws InitializationException;
	public List<T> getTopNplayers();
}
