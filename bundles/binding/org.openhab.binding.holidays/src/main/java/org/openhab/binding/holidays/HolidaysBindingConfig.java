package org.openhab.binding.holidays;

import org.openhab.binding.holidays.internal.HolidayType;
import org.openhab.core.binding.BindingConfig;

/**
 * This is a helper class holding binding specific configuration details
 * 
 * @author Martin Renner
 * @since 1.7.0
 */
public class HolidaysBindingConfig implements BindingConfig {

	private final HolidayType holidayType;

	public HolidaysBindingConfig(HolidayType holidayType) {
		this.holidayType = holidayType;
	}

	public HolidayType getHolidayType() {
		return holidayType;
	}
}
