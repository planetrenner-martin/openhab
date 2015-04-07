package org.openhab.binding.holidays.internal.impl;

/**
 * This exception is thrown if the binding cannot be initialized.
 * 
 * @author Martin Renner
 * 
 */
public class HolidaysInitializationException extends RuntimeException {

	private static final long serialVersionUID = -8656046515106178819L;

	public HolidaysInitializationException(String message) {
		super(message);
	}

	public HolidaysInitializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
