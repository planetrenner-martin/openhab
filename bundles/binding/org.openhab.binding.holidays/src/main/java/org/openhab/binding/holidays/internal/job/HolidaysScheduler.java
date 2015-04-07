package org.openhab.binding.holidays.internal.job;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Set;

import org.openhab.binding.holidays.internal.Holidays;
import org.openhab.binding.holidays.internal.impl.HolidaysInitializationException;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HolidaysScheduler {

	private static final Logger logger = LoggerFactory.getLogger(HolidaysScheduler.class);
	private static final String JOB_GROUP = "Holidays";

	/**
	 * @throws HolidaysInitializationException
	 */
	public void start(Holidays holidays) {
		Scheduler scheduler = getScheduler();

		// Populate job data map.
		JobDataMap jobData = new JobDataMap();
		jobData.put(DailyJob.HOLIDAYS_PROPERTY, holidays);

		final String name = DailyJob.class.getSimpleName();
		// Create JobDetail with our JobDataMap.
		JobDetail dailyJobDetail = newJob(DailyJob.class).withIdentity(name, JOB_GROUP).usingJobData(jobData).build();
		// Trigger "DailyJob" at midnight
		Trigger trigger = newTrigger().withIdentity(name + "-Trigger", JOB_GROUP).startNow()
				.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 0)).build();
		try {
			scheduler.scheduleJob(dailyJobDetail, trigger);
			logger.info("Scheduled job {} with next fire time {}", name, trigger.getNextFireTime());
		} catch (SchedulerException e) {
			logger.error("Exception while scheduling daily job", e);
			throw new HolidaysInitializationException("Exception while scheduling daily job", e);
		}
	}

	public void stop() {
		Scheduler scheduler = getScheduler();

		// Delete all of our jobs.
		logger.info("Deleting jobs");
		try {
			Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(JOB_GROUP));
			for (JobKey jobKey : jobKeys) {
				logger.debug("Deleting job {}", jobKey.getName());
				scheduler.deleteJob(jobKey);
			}
		} catch (SchedulerException e) {
			logger.error("Could not delete jobs", e);
		}
	}

	/**
	 * @throws HolidaysInitializationException
	 */
	private Scheduler getScheduler() {
		try {
			return StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			logger.error("Exception while getting quartz scheduler factory", e);
			throw new HolidaysInitializationException("Execption while getting quartz scheduler factory", e);
		}

	}
}
