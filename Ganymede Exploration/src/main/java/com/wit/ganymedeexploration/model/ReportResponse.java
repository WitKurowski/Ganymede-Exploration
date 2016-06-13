package com.wit.ganymedeexploration.model;

import com.google.gson.annotations.SerializedName;

/**
 * The response after reporting the message found during the exploration.
 */
public class ReportResponse {
	/**
	 * The message uncovered from the exploration.
	 */
	@SerializedName("response")
	private String message;

	/**
	 * Returns the message uncovered from the exploration.
	 * 
	 * @return The message uncovered from the exploration.
	 */
	public String getMessage() {
		return this.message;
	}
}