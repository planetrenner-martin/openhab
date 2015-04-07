package org.openhab.binding.holidays.internal;

/**
 * Central interface used to glue all information and functionality together.
 * 
 * @author Martin Renner
 * 
 */
public interface Holidays {

	/**
	 * Deletes all calculated data and then calls {@link #calculateData()}.
	 */
	void reset();

	/**
	 * Fire events if "today" is a holiday.
	 */
	void fireEvents();

	/**
	 * Reads the vacations from the vacation file, if the content has changed.
	 */
	void readVacationFile();
}
