package com.wit.model;

import com.google.gson.annotations.SerializedName;

/**
 * The results to report back.
 */
public class ReportDetails {
	/**
	 * The message uncovered from the exploration.
	 */
	@SerializedName("message")
	private final String message;

	/**
	 * Creates a new {@link ReportDetails}.
	 *
	 * @param message
	 *            The message uncovered from the exploration.
	 */
	public ReportDetails(final String message) {
		this.message = message;
	}
}