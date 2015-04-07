package org.openhab.binding.holidays.internal;

public enum HolidayType {

	PUBLIC_HOLIDAY("publicHoliday"), SCHOOL_HOLIDAY("schoolHoliday");

	private final String configName;

	private HolidayType(String configName) {
		this.configName = configName;
	}

	/**
	 * @throws IllegalArgumentException
	 *             If the given configuration name is not known.
	 */
	public static HolidayType fromConfigName(String configName) {
		for (HolidayType holidayType : values()) {
			if (holidayType.configName.equals(configName)) {
				return holidayType;
			}
		}
		throw new IllegalArgumentException("Unknown configuration name " + configName);
	}
}
