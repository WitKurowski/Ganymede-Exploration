package com.wit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.wit.exception.ServerException;
import com.wit.model.CommandResult;
import com.wit.model.Drone;
import com.wit.model.Room;
import com.wit.model.command.CommandContents;
import com.wit.model.command.ExploreCommandContents;
import com.wit.model.command.ReadCommandContents;
import com.wit.service.ExplorationManager;

/**
 * The core class where the exploration is managed.
 */
public class GanymedeExploration {
	/**
	 * Performs any work necessary after a {@link Drone} has completed exploring.
	 */
	private final class DroneOnCommandsCompletedListener
			implements Drone.OnCommandsCompletedListener {
		/**
		 * The {@link Drone} that we are waiting on the completion for.
		 */
		private final Drone drone;

		/**
		 * The current {@link State} of the exploration.
		 */
		private final State state;

		/**
		 * Creates a new {@link DroneOnCommandsCompletedListener}.
		 * 
		 * @param drone
		 *            The {@link Drone} that we are waiting on the completion for.
		 * @param state
		 *            The current {@link State} of the exploration.
		 */
		public DroneOnCommandsCompletedListener(final Drone drone, final State state) {
			this.drone = drone;
			this.state = state;
		}

		@Override
		public void onCompleted() {
			synchronized (GanymedeExploration.this) {
				this.state.busyDrones.remove(this.drone);
				this.state.pendingDrones.add(this.drone);

				GanymedeExploration.this.notify();
			}
		}
	}

	/**
	 * The current state of the exploration.
	 */
	private static final class State {
		/**
		 * The {@link Drone}s available to receive new commands.
		 */
		public final List<Drone> availableDrones = new ArrayList<>();

		/**
		 * The {@link Drone}s currently executing commands.
		 */
		public final List<Drone> busyDrones = new ArrayList<>();

		/**
		 * The {@link Drone}s that have completed executing commands and that are carrying the
		 * results of those commands.
		 */
		public final List<Drone> pendingDrones = new ArrayList<>();

		/**
		 * The IDs of the {@link Room}s that have been previously explored.
		 */
		public final List<String> exploredRoomIds = new ArrayList<>();

		/**
		 * The IDs of the {@link Room}s that have had their writing read already.
		 */
		public final List<String> readRoomIds = new ArrayList<>();

		/**
		 * The IDs of the {@link Room}s that have not been "explore"d yet.
		 */
		public final List<String> unexploredRoomIds = new ArrayList<>();

		/**
		 * The IDs of the {@link Room}s that have not been "read" yet.
		 */
		public final List<String> unreadRoomIds = new ArrayList<>();

		/**
		 * The indices and associated writings that have been found within the explored labyrinth.
		 */
		public final Map<Integer, String> indexedWritings = new HashMap<>();
	}

	/**
	 * The entry-point for the application.
	 *
	 * @param args
	 *            Any arguments that need to be passed in to run the application.
	 */
	public static void main(final String[] args) {
		final GanymedeExploration ganymedeExploration = new GanymedeExploration();

		ganymedeExploration.performExploration();
	}

	/**
	 * The current state of the exploration.
	 */
	private final State state = new State();

	/**
	 * Performs the exploration from start to finish.
	 */
	public void performExploration() {
		final ExplorationManager mainService = ExplorationManager.getInstance();

		try {
			final Room startingRoom = mainService.start();
			final String roomId = startingRoom.getId();

			System.out.println("Room ID: " + roomId);

			this.state.unexploredRoomIds.add(roomId);
			this.state.unreadRoomIds.add(roomId);

			final List<String> droneIds = startingRoom.getDroneIds();

			System.out.println("Drone IDs: " + droneIds);

			for (final String droneId : droneIds) {
				final Drone drone = new Drone(droneId);
				final DroneOnCommandsCompletedListener droneOnCommandsCompletedListener = new DroneOnCommandsCompletedListener(
						drone, this.state);

				drone.setOnCommandsCompletedListener(droneOnCommandsCompletedListener);

				this.state.availableDrones.add(drone);
			}

			synchronized (this) {
				while (!this.state.unexploredRoomIds.isEmpty()
						|| !this.state.unreadRoomIds.isEmpty() || !this.state.busyDrones.isEmpty()
						|| !this.state.pendingDrones.isEmpty()) {
					final boolean pendingDronesExist = !this.state.pendingDrones.isEmpty();

					if (pendingDronesExist) {
						for (final Drone pendingDrone : this.state.pendingDrones) {
							final Map<String, CommandResult> commandIdCommandResults = pendingDrone
									.getCommandIdCommandResults();
							final Collection<CommandResult> commandResults = commandIdCommandResults
									.values();

							for (final CommandResult commandResult : commandResults) {
								final List<String> connectedRoomIds = commandResult
										.getConnectedRoomIds();
								final Integer order = commandResult.getOrder();

								if ((connectedRoomIds == null) && (order == null)) {
									final String error = commandResult.getError();
									final String message = String.format(
											"Failed to execute command \"%s\": %s", commandResult,
											error);

									throw new ServerException(message);
								} else {
									if (connectedRoomIds != null) {
										for (final String connectedRoomId : connectedRoomIds) {
											if (!this.state.exploredRoomIds
													.contains(connectedRoomId)) {
												this.state.unexploredRoomIds.add(connectedRoomId);
											}

											if (!this.state.readRoomIds.contains(connectedRoomId)) {
												this.state.unreadRoomIds.add(connectedRoomId);
											}
										}
									}

									if ((order != null) && (order != -1)) {
										final String writing = commandResult.getWriting();
										this.state.indexedWritings.put(order, writing);
									}
								}
							}
						}

						this.state.availableDrones.addAll(this.state.pendingDrones);
						this.state.pendingDrones.clear();
					}

					final boolean availableDronesExist = !this.state.availableDrones.isEmpty();
					final boolean unexploredRoomsExist = !this.state.unexploredRoomIds.isEmpty();
					final boolean unreadRoomsExist = !this.state.unreadRoomIds.isEmpty();

					if (availableDronesExist && (unexploredRoomsExist || unreadRoomsExist)) {
						for (final Drone availableDrone : new ArrayList<>(
								this.state.availableDrones)) {
							final Map<String, CommandContents> commandIdCommandContents = new HashMap<>();

							for (final String unreadRoomId : new ArrayList<>(
									this.state.unreadRoomIds)) {
								if (commandIdCommandContents
										.size() == ExplorationManager.MAXIMUM_COMMAND_BATCH_SIZE) {
									break;
								} else {
									final String commandId = UUID.randomUUID().toString();
									final CommandContents commandContents = new ReadCommandContents(
											unreadRoomId);

									commandIdCommandContents.put(commandId, commandContents);

									this.state.unreadRoomIds.remove(unreadRoomId);
									this.state.readRoomIds.add(unreadRoomId);
								}
							}

							for (final String unexploredRoomId : new ArrayList<>(
									this.state.unexploredRoomIds)) {
								if (commandIdCommandContents
										.size() == ExplorationManager.MAXIMUM_COMMAND_BATCH_SIZE) {
									break;
								} else {
									final String commandId = UUID.randomUUID().toString();
									final CommandContents commandContents = new ExploreCommandContents(
											unexploredRoomId);

									commandIdCommandContents.put(commandId, commandContents);

									this.state.unexploredRoomIds.remove(unexploredRoomId);
									this.state.exploredRoomIds.add(unexploredRoomId);
								}
							}

							if (commandIdCommandContents.size() > 0) {
								this.state.availableDrones.remove(availableDrone);
								this.state.busyDrones.add(availableDrone);

								availableDrone.execute(commandIdCommandContents);
							}
						}
					} else {
						final boolean busyDronesExist = !this.state.busyDrones.isEmpty();

						if (busyDronesExist) {
							this.wait();
						}
					}
				}
			}

			System.out.println(this.state.indexedWritings);
		} catch (final IOException ioException) {
			ioException.printStackTrace();
		} catch (final ServerException serverException) {
			serverException.printStackTrace();
		} catch (final InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}
}