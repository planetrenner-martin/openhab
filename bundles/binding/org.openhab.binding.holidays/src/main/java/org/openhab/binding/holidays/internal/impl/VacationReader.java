package org.openhab.binding.holidays.internal.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for reading the vacation file.
 * 
 * @author Martin Renner
 */
public class VacationReader {

	private static final Logger logger = LoggerFactory.getLogger(VacationReader.class);

	private File vacationFile = new File("etc", "vacations.txt");
	private long lastRead = -1;

	/**
	 * Has the vacation file changed since we read it? Should be called before
	 * calling {@link #readFile()}.
	 */
	public boolean hasChanged() {
		if (!vacationFile.exists()) {
			logger.debug("Vacation file does not exist");
			return false;
		}

		final long modificationTime = vacationFile.lastModified();
		return modificationTime > lastRead;
	}

	/**
	 * Reads the vacation file and returns the vacation days in a set.
	 * 
	 * @return The vacation days or an empty list. If the file contains ranges,
	 *         each day of the range is included in the set.
	 */
	public Set<LocalDate> readFile() {
		SortedSet<LocalDate> vacations = new TreeSet<>();
		DateTimeFormatter df = DateTimeFormat.forPattern("dd.MM.yyyy");

		try (LineNumberReader lnr = new LineNumberReader(new FileReader(vacationFile))) {
			String line;
			while ((line = lnr.readLine()) != null) {
				if (line.indexOf('#') >= 0) {
					// Remove comment sign and anything behind it.
					line = line.substring(0, line.indexOf('#'));
				}
				if (line.matches("^[ \t]*$")) {
					// A comment line: continue.
					continue;
				}
				line = line.trim();
				if (line.contains("-")) {
					// A range of dates.
					String[] dates = line.split("-");
					if (dates.length != 2) {
						logger.warn("Invalid date range in line {}: {}", lnr.getLineNumber(), line);
						continue;
					}
					LocalDate start;
					LocalDate end;
					try {
						start = df.parseLocalDate(dates[0].trim());
						end = df.parseLocalDate(dates[1].trim());
					} catch (IllegalArgumentException iae) {
						logger.warn("Invalid date format in line {}: {}", lnr.getLineNumber(), line);
						continue;
					}
					if (start.isAfter(end)) {
						logger.warn("Start has to be before end in line {}: {}", lnr.getLineNumber(), line);
						continue;
					}
					Duration duration = new Duration(start.toDateTimeAtStartOfDay(), end.toDateTimeAtStartOfDay());
					if (duration.getStandardDays() > 100) {
						logger.warn("More than 100 days between start and end in line {}: {}. "
								+ "Split into several entries.", lnr.getLineNumber(), line);
						continue;
					}
					// Put every date into the set.
					logger.debug("Adding vacation range: {} to {}", start, end);
					LocalDate current = new LocalDate(start);
					while (!current.isAfter(end)) {
						vacations.add(current);
						current = current.plusDays(1);
					}
				} else {
					// A single entry.
					LocalDate date;
					try {
						date = df.parseLocalDate(line);
					} catch (IllegalArgumentException iae) {
						logger.warn("Invalid date format in line {}: {}", lnr.getLineNumber(), line);
						continue;
					}
					logger.debug("Adding vacation: {}", date);
					vacations.add(date);
				}
			}

			logger.info("Parsed vacations: {} distinct days", vacations.size());
			lastRead = System.currentTimeMillis();
			return vacations;
		} catch (FileNotFoundException e) {
			// almost not possible because we check for existence
			logger.warn("Vacation file disappeared.");
			return Collections.emptySet();
		} catch (IOException ioe) {
			logger.error("IOException while reading fomr vacation file", ioe);
			return Collections.emptySet();
		}
	}
}
