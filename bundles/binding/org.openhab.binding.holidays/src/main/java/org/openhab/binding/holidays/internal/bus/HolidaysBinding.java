/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.holidays.internal.bus;

import java.util.Dictionary;
import java.util.Map;

import org.openhab.binding.holidays.HolidaysBindingProvider;
import org.openhab.binding.holidays.internal.Holidays;
import org.openhab.binding.holidays.internal.impl.HolidaysImpl;
import org.openhab.binding.holidays.internal.job.HolidaysScheduler;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.binding.BindingProvider;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the holidays binding.
 * 
 * @author Martin Renner
 * @since 1.7.0
 */
public class HolidaysBinding extends AbstractActiveBinding<HolidaysBindingProvider> implements ManagedService {

	private static final Logger logger = LoggerFactory.getLogger(HolidaysBinding.class);

	/** Interval for AbstractActiveBinding (in millis). */
	private long refreshInterval = 1 * 60 * 1000;

	private HolidaysScheduler scheduler = new HolidaysScheduler();
	private Holidays holidays;

	/**
	 * Called by the SCR to activate the component with its configuration read
	 * from CAS
	 * 
	 * @param bundleContext
	 *            BundleContext of the Bundle that defines this component
	 * @param configuration
	 *            Configuration properties for this component obtained from the
	 *            ConfigAdmin service
	 */
	public void activate(final BundleContext bundleContext, final Map<String, Object> configuration) {
		logger.debug("activate(BundleContext,Map) is called");

		// "configuration" may be null, because "configuration-policy" in
		// "binding.xml" is not set to "require".

		// Start with a new instance
		holidays = new HolidaysImpl(eventPublisher, providers);
		// Start the scheduler.
		scheduler.start(holidays);
	}

	/**
	 * Called by the SCR when the configuration of a binding has been changed
	 * through the ConfigAdmin service.
	 * 
	 * @param configuration
	 *            Updated configuration properties
	 */
	public void modified(final Map<String, Object> configuration) {
		logger.debug("modified(Map) is called");
		// update the internal configuration accordingly
	}

	/**
	 * Called by the SCR to deactivate the component when either the
	 * configuration is removed or mandatory references are no longer satisfied
	 * or the component has simply been stopped.
	 * 
	 * @param reason
	 *            Reason code for the deactivation:<br>
	 *            <ul>
	 *            <li>0 – Unspecified
	 *            <li>1 – The component was disabled
	 *            <li>2 – A reference became unsatisfied
	 *            <li>3 – A configuration was changed
	 *            <li>4 – A configuration was deleted
	 *            <li>5 – The component was disposed
	 *            <li>6 – The bundle was stopped
	 *            </ul>
	 */
	public void deactivate(final int reason) {
		logger.debug("deactivate({}) is called", reason);

		// Stop the scheduler and deallocate resources.
		scheduler.stop();
		holidays = null;
	}

	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}

	@Override
	protected String getName() {
		return "Holidays Refresh Service";
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Checks the holiday configuration file for new holidays.
	 */
	@Override
	protected void execute() {
		logger.debug("execute() is called, holidays={}", holidays);
		if (holidays != null) {
			try {
				holidays.readVacationFile();
			} catch (Exception e) {
				logger.warn("Exception while reading vacation file", e);
			}
		}
	}

	@Override
	public void allBindingsChanged(BindingProvider provider) {
		logger.debug("allBindingsChanged");
		setProperlyConfigured(true);
	}

	@Override
	public void bindingChanged(BindingProvider provider, String itemName) {
		logger.debug("bindingsChanged");
	}

	public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
		logger.debug("updated is called (properties={})", properties);
	}
}
