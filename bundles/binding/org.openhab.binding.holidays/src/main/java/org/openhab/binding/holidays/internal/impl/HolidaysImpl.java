package org.openhab.binding.holidays.internal.impl;

import static org.openhab.core.library.types.OnOffType.OFF;
import static org.openhab.core.library.types.OnOffType.ON;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.joda.time.LocalDate;
import org.openhab.binding.holidays.HolidaysBindingConfig;
import org.openhab.binding.holidays.HolidaysBindingProvider;
import org.openhab.binding.holidays.internal.HolidayType;
import org.openhab.binding.holidays.internal.Holidays;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds the holidays for the current year.
 * <p>
 * {@link #calculateData()} has to be called regularly. Ideally, each day at
 * midnight.
 * <p>
 * The class is thread safe.
 * 
 * @author Martin Renner
 * 
 */
public class HolidaysImpl implements Holidays {
	private static final Logger logger = LoggerFactory.getLogger(HolidaysImpl.class);

	private final EventPublisher eventPublisher;
	private final Collection<HolidaysBindingProvider> providers;
	private int holidaysForYear = -1;
	private Map<LocalDate, String> holidays = Collections.emptyMap();
	private Set<LocalDate> vacations = Collections.emptySet();
	private final VacationReader vacationReader;

	/**
	 * Constructs a new instance and calls {@link #reset()}.
	 */
	public HolidaysImpl(EventPublisher eventPublisher, Collection<HolidaysBindingProvider> providers) {
		this.eventPublisher = eventPublisher;
		this.providers = providers;
		this.vacationReader = new VacationReader();
		reset();
	}

	/**
	 * {@inheritDoc}
	 */
	public void reset() {
		synchronized (this) {
			// clear data.
			holidaysForYear = -1;
			holidays.clear();
			// recalculate.
			calculateData();
			readVacationFile();
		}

		fireEvents();
	}

	/**
	 * Calculates holidays data for the current year, if not already done.
	 */
	private void calculateData() {
		final LocalDate today = new LocalDate();
		final int currentYear = today.getYear();

		synchronized (this) {
			if (currentYear != holidaysForYear) {
				logger.info("No data for year {}. Calculating holidays for this year.", currentYear);
				HolidayCalculator calculator = new HolidayCalculator();

				holidays = new TreeMap<LocalDate, String>(calculator.calculateHolidays(currentYear));
				holidaysForYear = currentYear;

				// Print out the next holiday. We can iterate because the keys
				// in the map are sorted.
				for (Iterator<LocalDate> it = holidays.keySet().iterator(); it.hasNext();) {
					LocalDate holiday = it.next();
					if (holiday.isAfter(today)) {
						logger.info("Next holiday is on {}: {}", holiday, holidays.get(holiday));
						break;
					}
				}
			}
		}
	}

	/**
	 * Returns if a given date is a holiday.
	 */
	private boolean isHoliday(LocalDate date) {
		synchronized (this) {
			return holidays.containsKey(date);
		}
	}

	/**
	 * Returns if a given date is vacation.
	 */
	private boolean isVacation(LocalDate date) {
		synchronized (this) {
			return vacations.contains(date);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void fireEvents() {
		calculateData();

		LocalDate today = new LocalDate();

		// Holidays
		State holidayState = isHoliday(today) ? ON : OFF;
		for (HolidaysBindingProvider provider : providers) {
			for (String itemName : provider.getItemNames()) {
				HolidaysBindingConfig config = provider.getConfig(itemName);
				if (HolidayType.PUBLIC_HOLIDAY.equals(config.getHolidayType())) {
					if (ON.equals(holidayState)) {
						logger.info("Setting item {} to {}", itemName, holidayState);
					}
					eventPublisher.postUpdate(itemName, holidayState);
				}
			}
		}

		// Vacation
		State vacationState = isVacation(today) ? ON : OFF;
		for (HolidaysBindingProvider provider : providers) {
			for (String itemName : provider.getItemNames()) {
				HolidaysBindingConfig config = provider.getConfig(itemName);
				if (HolidayType.SCHOOL_HOLIDAY.equals(config.getHolidayType())) {
					if (ON.equals(holidayState)) {
						logger.info("Setting item {} to {}", itemName, holidayState);
					}
					eventPublisher.postUpdate(itemName, vacationState);
				}
			}
		}
	}

	/**
	 * Reads the vacation file if it has changed.
	 */
	public void readVacationFile() {
		if (vacationReader.hasChanged()) {
			synchronized (this) {
				vacations = vacationReader.readFile();
			}
			fireEvents();
		}
	}
}
