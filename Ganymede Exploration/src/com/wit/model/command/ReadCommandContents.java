package com.wit.model.command;

import com.google.gson.annotations.SerializedName;
import com.wit.model.Room;

/**
 * The contents sent along as part of a drone "read" command.
 */
public class ReadCommandContents extends CommandContents {
	/**
	 * The ID of the {@link Room} to read the writing of.
	 */
	@SerializedName("read")
	private final String roomId;

	/**
	 * Creates a new {@link ReadCommandContents}.
	 *
	 * @param roomId
	 *            The ID of the {@link Room} to read the writing of.
	 */
	public ReadCommandContents(final String roomId) {
		this.roomId = roomId;
	}

	/**
	 * Returns the ID of the {@link Room} to read the writing of.
	 * 
	 * @return The ID of the {@link Room} to read the writing of.
	 */
	public String getRoomId() {
		return this.roomId;
	}
}