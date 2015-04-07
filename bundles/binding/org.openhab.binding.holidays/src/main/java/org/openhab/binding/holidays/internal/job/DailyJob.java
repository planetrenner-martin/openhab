package org.openhab.binding.holidays.internal.job;

import org.openhab.binding.holidays.internal.Holidays;
import org.openhab.binding.holidays.internal.impl.HolidaysInitializationException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Daily job to update the openhab items and to calculate the holidays.
 * 
 * @author Martin Renner
 * @since 1.7.0
 */
public class DailyJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(DailyJob.class);
	public static final String HOLIDAYS_PROPERTY = "holidays";
	private Holidays holidays;

	/**
	 * Setter called by Quartz with the input of the JobDataMap.
	 */
	public void setHolidays(Holidays holidays) {
		this.holidays = holidays;
	}

	public void execute(JobExecutionContext jobContext) throws JobExecutionException {
		logger.info("DailyJob is executing");
		if (holidays == null) {
			throw new HolidaysInitializationException("Property 'holidays' must not be null");
		}

		// Fire events if necessary.
		holidays.fireEvents();
	}
}
