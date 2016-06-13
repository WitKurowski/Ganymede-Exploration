package com.wit.ganymedeexploration.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * A room within the labyrinths on Ganymede.
 */
public class Room {
	/**
	 * The ID of this {@link Room}.
	 */
	@SerializedName("roomId")
	private String id;

	/**
	 * The IDs of the drones usable for exploration.
	 */
	@SerializedName("drones")
	private List<String> droneIds;

	/**
	 * Returns the IDs of the drones usable for exploration.
	 * 
	 * @return The IDs of the drones usable for exploration.
	 */
	public List<String> getDroneIds() {
		return this.droneIds;
	}

	/**
	 * Returns the ID of this {@link Room}.
	 * 
	 * @return The ID of this {@link Room}.
	 */
	public String getId() {
		return this.id;
	}
}