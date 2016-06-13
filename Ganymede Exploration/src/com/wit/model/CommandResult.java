package com.wit.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * The results of a command.
 */
public class CommandResult {
	/**
	 * The IDs of the {@link Room}s that are connected to the explored {@link Room}.
	 */
	@SerializedName("connections")
	private List<String> connectedRoomIds;

	/**
	 * The writing found within a {@link Room}, if any.
	 */
	@SerializedName("writing")
	private String writing;

	/**
	 * The order of the writing found within a {@link Room}, or -1 if no meaningful writing was
	 * found.
	 */
	@SerializedName("order")
	private Integer order;

	/**
	 * A description of the error encountered when attempting to execute the command.
	 */
	@SerializedName("error")
	private String error;

	/**
	 * Returns the IDs of the {@link Room}s that are connected to the explored {@link Room}.
	 *
	 * @return The IDs of the {@link Room}s that are connected to the explored {@link Room}.
	 */
	public List<String> getConnectedRoomIds() {
		return this.connectedRoomIds;
	}

	/**
	 * Returns a description of the error encountered when attempting to execute the command.
	 *
	 * @return A description of the error encountered when attempting to execute the command.
	 */
	public String getError() {
		return this.error;
	}

	/**
	 * Returns the order of the writing found within a {@link Room}, or -1 if no meaningful writing
	 * was found.
	 *
	 * @return The order of the writing found within a {@link Room}, or -1 if no meaningful writing
	 *         was found.
	 */
	public Integer getOrder() {
		return this.order;
	}

	/**
	 * Returns the writing found within a {@link Room}, if any.
	 *
	 * @return The writing found within a {@link Room}, if any.
	 */
	public String getWriting() {
		return this.writing;
	}
}