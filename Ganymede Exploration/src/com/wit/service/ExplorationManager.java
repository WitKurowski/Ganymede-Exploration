package com.wit.service;

import java.io.IOException;
import java.util.Map;

import com.wit.exception.ServerException;
import com.wit.model.CommandResult;
import com.wit.model.Drone;
import com.wit.model.ReportDetails;
import com.wit.model.ReportResponse;
import com.wit.model.Room;
import com.wit.model.command.CommandContents;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Used to start the exploration.
 */
public class ExplorationManager extends Manager {
	private interface ExplorationManagerRetrofitCore {
		@POST("/drone/{id}/commands")
		Call<Map<String, CommandResult>> execute(@Path("id") String droneId,
				@Body Map<String, CommandContents> commandIdCommandContents);

		@GET("/start")
		Call<Room> get();

		@POST("/report")
		Call<ReportResponse> report(@Body ReportDetails reportDetails);
	}

	/**
	 * The singleton instance of {@link ExplorationManager}.
	 */
	private static final ExplorationManager EXPLORATION_MANAGER = new ExplorationManager();

	/**
	 * The maximum number of commands that any given {@link Drone} should be given.
	 */
	public static final int MAXIMUM_COMMAND_BATCH_SIZE = 5;

	/**
	 * Returns the singleton instance of {@link ExplorationManager}.
	 *
	 * @return The singleton instance of {@link ExplorationManager}.
	 */
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
	 * Executes commands using a particular {@link Drone}.
	 *
	 * @param droneId
	 *            The ID of the {@link Drone} used to execute the commands.
	 * @param commandIdCommandContents
	 *            The unique command IDs and their associated contents to execute.
	 * @return The unique command IDs and their associated {@link CommandResult}s.
	 * @throws IOException
	 *             A network error occurred.
	 * @throws ServerException
	 *             The server returned an error.
	 */
	public Map<String, CommandResult> execute(final String droneId,
			final Map<String, CommandContents> commandIdCommandContents)
					throws IOException, ServerException {
		final Map<String, CommandResult> commandIdCommandResults;

		if (commandIdCommandContents.size() > 5) {
			throw new IllegalArgumentException(
					String.format("The maximum number of commands that can be batched is %s",
							ExplorationManager.MAXIMUM_COMMAND_BATCH_SIZE));
		} else {
			final Call<Map<String, CommandResult>> call = this.explorationManagerRetrofitCore
					.execute(droneId, commandIdCommandContents);
			final Response<Map<String, CommandResult>> response = call.execute();
			final boolean successful = response.isSuccessful();

			if (successful) {
				commandIdCommandResults = response.body();
			} else {
				final String message = response.message();

				throw new ServerException(message);
			}
		}

		return commandIdCommandResults;
	}

	/**
	 * Sends the {@link ReportDetails} uncovered as part of the exploration.
	 *
	 * @return The {@link ReportResponse} received.
	 * @throws IOException
	 *             A network error occurred.
	 * @throws ServerException
	 *             The server returned an error.
	 */
	public ReportResponse report(final ReportDetails reportDetails)
			throws IOException, ServerException {
		final Call<ReportResponse> call = this.explorationManagerRetrofitCore.report(reportDetails);
		final Response<ReportResponse> response = call.execute();
		final boolean successful = response.isSuccessful();
		final ReportResponse reportResponse;

		if (successful) {
			reportResponse = response.body();
		} else {
			final String message = response.message();

			throw new ServerException(message);
		}

		return reportResponse;
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