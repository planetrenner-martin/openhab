/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.holidays.internal.bus;

import org.openhab.binding.holidays.HolidaysBindingConfig;
import org.openhab.binding.holidays.HolidaysBindingProvider;
import org.openhab.binding.holidays.internal.HolidayType;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author Martin Renner
 * @since 1.7.0
 */
public class HolidaysGenericBindingProvider extends AbstractGenericBindingProvider implements HolidaysBindingProvider {

	private static final Logger logger = LoggerFactory.getLogger(HolidaysGenericBindingProvider.class);

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "holidays";
	}

	/**
	 * @{inheritDoc
	 */
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
		if (!(item instanceof SwitchItem)) {
			throw new BindingConfigParseException("item '" + item.getName() + "' is of type '"
					+ item.getClass().getSimpleName() + "', only SwitchItem are allowed "
					+ "- please check your *.items configuration");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig)
			throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);

		logger.debug("Processing binding configuration '{}'", bindingConfig);

		HolidayType holidayType = HolidayType.fromConfigName(bindingConfig);
		HolidaysBindingConfig config = new HolidaysBindingConfig(holidayType);
		addBindingConfig(item, config);
	}

	/**
	 * {@inheritDoc}
	 */
	public HolidaysBindingConfig getConfig(String itemName) {
		return (HolidaysBindingConfig) bindingConfigs.get(itemName);
	}
}
