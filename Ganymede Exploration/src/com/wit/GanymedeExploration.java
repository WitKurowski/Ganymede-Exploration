package com.wit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.wit.exception.ServerException;
import com.wit.model.Drone;
import com.wit.model.Room;
import com.wit.model.command.CommandContents;
import com.wit.model.command.ReadCommandContents;
import com.wit.service.ExplorationManager;

/**
 * The core class where the exploration is managed.
 */
public class GanymedeExploration {
	public static void main(final String[] args) {
		final GanymedeExploration ganymedeExploration = new GanymedeExploration();

		ganymedeExploration.performExploration();
	}

	public void performExploration() {
		final ExplorationManager mainService = ExplorationManager.getInstance();

		try {
			final Room startingRoom = mainService.start();
			final String roomId = startingRoom.getId();

			System.out.println("Room ID: " + roomId);

			final List<String> droneIds = startingRoom.getDroneIds();

			System.out.println("Drone IDs: " + droneIds);

			final String droneId = droneIds.get(0);
			final Drone drone = new Drone(droneId);

			final Map<String, CommandContents> commandIdCommandContents = new HashMap<>();
			final String commandId = UUID.randomUUID().toString();
			final CommandContents commandContents = new ReadCommandContents(roomId);

			commandIdCommandContents.put(commandId, commandContents);

			drone.execute(commandIdCommandContents);
		} catch (final IOException ioException) {
			ioException.printStackTrace();
		} catch (final ServerException serverException) {
			serverException.printStackTrace();
		}
	}
}