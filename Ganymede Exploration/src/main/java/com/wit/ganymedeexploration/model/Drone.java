package com.wit.ganymedeexploration.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.wit.ganymedeexploration.exception.ServerException;
import com.wit.ganymedeexploration.model.command.CommandContents;
import com.wit.ganymedeexploration.service.ExplorationManager;

/**
 * An unmanned aerial vehicle used for exploration.
 */
public class Drone {
	private static final class CurrentExplorationOnCompletedListener
			implements ExplorationThread.OnCompletedListener {
		private final Drone drone;
		private final OnCommandsCompletedListener onCommandsCompletedListener;

		public CurrentExplorationOnCompletedListener(final Drone drone) {
			this(drone, null);
		}

		public CurrentExplorationOnCompletedListener(final Drone drone,
				final OnCommandsCompletedListener onCommandsCompletedListener) {
			this.drone = drone;
			this.onCommandsCompletedListener = onCommandsCompletedListener;
		}

		@Override
		public void onCompleted(final Map<String, CommandResult> commandIdCommandResults) {
			this.drone.getCommandIdCommandResults().putAll(commandIdCommandResults);
			this.drone.setRunning(false);

			if (this.onCommandsCompletedListener != null) {
				this.onCommandsCompletedListener.onCompleted();
			}
		}
	}

	/**
	 * Used to asynchronously execute commands given to the {@link Drone}.
	 */
	private static final class ExplorationThread extends Thread {
		/**
		 * Allows other classes to listen to the completion of the {@link ExplorationThread}.
		 */
		public static interface OnCompletedListener {
			/**
			 * Called once the exploration commands have been executed.
			 *
			 * @param commandIdCommandResults
			 *            The results of executing the exploration commands.
			 */
			void onCompleted(final Map<String, CommandResult> commandIdCommandResults);
		}

		/**
		 * The unique command IDs and their associated contents.
		 */
		private final Map<String, CommandContents> commandIdCommandContents;

		/**
		 * The {@link Drone} executing the commands.
		 */
		private final Drone drone;

		/**
		 * The {@link OnCompletedListener} to call once the commands have been executed.
		 */
		private final OnCompletedListener onCompletedListener;

		/**
		 * Creates a new {@link ExplorationThread}.
		 *
		 * @param commandIdCommandContents
		 *            The unique command IDs and their associated contents.
		 * @param drone
		 *            The {@link Drone} executing the commands.
		 * @param onCompletedListener
		 *            The {@link OnCompletedListener} to call once the commands have been executed.
		 */
		public ExplorationThread(final Map<String, CommandContents> commandIdCommandContents,
				final Drone drone, final OnCompletedListener onCompletedListener) {
			this.commandIdCommandContents = commandIdCommandContents;
			this.drone = drone;
			this.onCompletedListener = onCompletedListener;
		}

		@Override
		public void run() {
			super.run();

			final ExplorationManager explorationManager = ExplorationManager.getInstance();
			final String droneId = this.drone.getId();

			try {
				final Map<String, CommandResult> commandIdCommandResults = explorationManager
						.execute(droneId, this.commandIdCommandContents);

				this.onCompletedListener.onCompleted(commandIdCommandResults);
			} catch (final IOException ioException) {
				final String message = String
						.format("Failed to execute commands from drone with ID \"%s\".", droneId);

				System.err.println(message);
				ioException.printStackTrace();
			} catch (final ServerException serverException) {
				final String message = String
						.format("Failed to execute commands from drone with ID \"%s\".", droneId);

				System.err.println(message);
				serverException.printStackTrace();
			}
		}
	}

	/**
	 * Used to listen to the completion of the latest batch of commands that a {@link Drone} was
	 * requested to execute.
	 */
	public static interface OnCommandsCompletedListener {
		/**
		 * Called once the latest batch of commands has finished being executed.
		 */
		void onCompleted();
	}

	/**
	 * The unique command IDs and their associated results.
	 */
	private final Map<String, CommandResult> commandIdCommandResults = new HashMap<>();

	/**
	 * The ID of this {@link Drone}.
	 */
	private final String id;

	/**
	 * The listener used to listen to the completion of execution of the latest batch of commands.
	 */
	private OnCommandsCompletedListener onCommandsCompletedListener;

	/**
	 * Whether this {@link Drone} is currently executing commands.
	 */
	private boolean running = false;

	/**
	 * Creates a new {@link Drone}.
	 *
	 * @param id
	 *            The ID of this {@link Drone}.
	 */
	public Drone(final String id) {
		this.id = id;
	}

	/**
	 * Clears any state of this {@link Drone}.
	 */
	public void clear() {
		this.commandIdCommandResults.clear();
	}

	/**
	 * Executes the given commands.
	 *
	 * @param commandIdCommandContents
	 *            The unique command IDs and their associated contents.
	 */
	public void execute(final Map<String, CommandContents> commandIdCommandContents) {
		if (this.running) {
			throw new IllegalStateException(
					"Unable to send commands to a currently running drone.");
		} else {
			this.running = true;

			final CurrentExplorationOnCompletedListener currentExplorationOnCompletedListener;

			if (this.onCommandsCompletedListener == null) {
				currentExplorationOnCompletedListener = new CurrentExplorationOnCompletedListener(
						this);
			} else {
				currentExplorationOnCompletedListener = new CurrentExplorationOnCompletedListener(
						this, this.onCommandsCompletedListener);
			}

			final ExplorationThread explorationThread = new ExplorationThread(
					commandIdCommandContents, this, currentExplorationOnCompletedListener);

			explorationThread.start();
		}
	}

	/**
	 * Returns the unique command IDs and their associated results.
	 *
	 * @return The unique command IDs and their associated results.
	 */
	public Map<String, CommandResult> getCommandIdCommandResults() {
		return this.commandIdCommandResults;
	}

	/**
	 * Returns the ID of this {@link Drone}.
	 *
	 * @return The ID of this {@link Drone}.
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Returns whether this {@link Drone} is currently executing commands.
	 *
	 * @return Whether this {@link Drone} is currently executing commands.
	 */
	public boolean isRunning() {
		return this.running;
	}

	/**
	 * Sets the listener used to listen to the completion of execution of the latest batch of
	 * commands.
	 *
	 * @param onCommandsCompletedListener
	 *            The listener used to listen to the completion of execution of the latest batch of
	 *            commands.
	 */
	public void setOnCommandsCompletedListener(
			final OnCommandsCompletedListener onCommandsCompletedListener) {
		this.onCommandsCompletedListener = onCommandsCompletedListener;
	}

	/**
	 * Sets whether this {@link Drone} is currently executing commands.
	 *
	 * @param running
	 *            Whether this {@link Drone} is currently executing commands.
	 */
	public void setRunning(final boolean running) {
		this.running = running;
	}
}