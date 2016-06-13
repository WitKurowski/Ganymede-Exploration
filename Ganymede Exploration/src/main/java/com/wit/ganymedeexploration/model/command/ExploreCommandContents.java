package com.wit.ganymedeexploration.model.command;

import com.google.gson.annotations.SerializedName;
import com.wit.ganymedeexploration.model.Room;

/**
 * The contents sent along as part of a drone "explore" command.
 */
public class ExploreCommandContents extends CommandContents {
	/**
	 * The ID of the {@link Room} to explore.
	 */
	@SerializedName("explore")
	private final String roomId;

	/**
	 * Creates a new {@link ExploreCommandContents}.
	 *
	 * @param roomId
	 *            The ID of the {@link Room} to explore.
	 */
	public ExploreCommandContents(final String roomId) {
		this.roomId = roomId;
	}

	/**
	 * Returns the ID of the {@link Room} to explore.
	 *
	 * @return The ID of the {@link Room} to explore.
	 */
	public String getRoomId() {
		return this.roomId;
	}
}