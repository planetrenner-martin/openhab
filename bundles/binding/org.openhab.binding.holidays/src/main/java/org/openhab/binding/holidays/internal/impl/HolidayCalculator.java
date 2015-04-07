package org.openhab.binding.holidays.internal.impl;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HolidayCalculator {

	private static final Logger logger = LoggerFactory.getLogger(HolidayCalculator.class);

	/**
	 * Calculates the holidays for the given year.
	 * 
	 * @return A map with the holidays as key and the corresponding name as
	 *         value.
	 */
	public Map<LocalDate, String> calculateHolidays(int year) {
		LocalDate easter = calculateEaster(year);
		LocalDate whitsun = easter.plusDays(49);

		Map<LocalDate, String> holidays = new HashMap<LocalDate, String>();
		holidays.put(date(year, 1, 1), "Neujahr");
		holidays.put(date(year, 1, 6), "Heilige 3 Könige");
		holidays.put(easter.minusDays(2), "Karfreitag");
		holidays.put(easter, "Ostersonntag");
		holidays.put(easter.plusDays(1), "Ostermontag");
		holidays.put(date(year, 5, 1), "Tag der Arbeit");
		holidays.put(whitsun.minusDays(10), "Christi Himmelfahrt");
		holidays.put(whitsun, "Pfingstsonntag");
		holidays.put(whitsun.plusDays(1), "Pfingstmontag");
		holidays.put(whitsun.plusDays(11), "Fronleichnam");
		holidays.put(date(year, 10, 3), "Deutsche Einheit");
		holidays.put(date(year, 11, 1), "Allerheiligen");
		holidays.put(date(year, 12, 25), "1. Weihnachtstag");
		holidays.put(date(year, 12, 26), "2. Weichnachtstag");

		return holidays;
	}

	/**
	 * Calculates the date of Easter according to an optimized formula of
	 * Lichtenberg.
	 * 
	 * @param year
	 *            The year the calculation should be made for.
	 */
	private LocalDate calculateEaster(int year) {
		// http://de.wikipedia.org/wiki/Gau%C3%9Fsche_Osterformel#Eine_erg.C3.A4nzte_Osterformel
		int k = year / 100;
		int m = 15 + (3 * k + 3) / 4 - (8 * k + 13) / 25;
		int s = 2 - (3 * k + 3) / 4;
		int a = year % 19;
		int d = (19 * a + m) % 30;
		int r = (d + a / 11) / 29;
		int og = 21 + d + r;
		int sz = 7 - (year + year / 4 + s) % 7;
		int oe = 7 - (og - sz) % 7;
		int os = og + oe;

		// Now, "os" contains the day of Easter relative to the 1st of march.
		// "4" means "4th mars", "32" means "1st april" and so on.

		LocalDate firstOfMarch = date(year, DateTimeConstants.MARCH, 1);
		LocalDate easter = firstOfMarch.plusDays(os - 1);
		logger.debug("Easter is on {}", easter);
		return easter;
	}

	private LocalDate date(int year, int month, int day) {
		return new LocalDate(year, month, day);
	}
}
