package com.wit.service;

import java.io.IOException;

import com.wit.exception.ServerException;
import com.wit.model.Room;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;

/**
 * Used to start the exploration.
 */
public class ExplorationManager extends Manager {
	private interface ExplorationManagerRetrofitCore {
		@GET("/start")
		Call<Room> get();
	}

	private static final ExplorationManager EXPLORATION_MANAGER = new ExplorationManager();

	public static ExplorationManager getInstance() {
		return ExplorationManager.EXPLORATION_MANAGER;
	}

	private final ExplorationManagerRetrofitCore explorationManagerRetrofitCore;

	private ExplorationManager() {
		super();

		this.explorationManagerRetrofitCore = this
				.create(ExplorationManager.ExplorationManagerRetrofitCore.class);
	}

	/**
	 * Starts the exploration process.
	 *
	 * @return The starting room of the exploration.
	 * @throws IOException
	 *             A network error occurred.
	 * @throws ServerException
	 *             The server returned an error.
	 */
	public Room start() throws IOException, ServerException {
		final Call<Room> call = this.explorationManagerRetrofitCore.get();
		final Response<Room> response = call.execute();
		final boolean successful = response.isSuccessful();
		final Room room;

		if (successful) {
			room = response.body();
		} else {
			final String message = response.message();

			throw new ServerException(message);
		}

		return room;
	}
}