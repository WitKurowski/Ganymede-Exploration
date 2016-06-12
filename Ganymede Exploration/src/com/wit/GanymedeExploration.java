package com.wit;

import java.io.IOException;

import com.wit.exception.ServerException;
import com.wit.model.Room;
import com.wit.service.ExplorationManager;

/**
 * The core class where the exploration is managed.
 */
public class GanymedeExploration {
	public static void main(final String[] args) {
		final ExplorationManager mainService = ExplorationManager.getInstance();

		try {
			final Room startingRoom = mainService.start();
			final String roomId = startingRoom.getId();

			System.out.println("Room ID: " + roomId);
			System.out.println("Drone IDs: " + startingRoom.getDroneIds());
		} catch (final IOException ioException) {
			ioException.printStackTrace();
		} catch (final ServerException serverException) {
			serverException.printStackTrace();
		}
	}
}