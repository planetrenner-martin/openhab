/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.holidays;

import org.openhab.core.binding.BindingProvider;

/**
 * @author Martin Renner
 * @since 1.7.0
 */
public interface HolidaysBindingProvider extends BindingProvider {

	/**
	 * Returns the binding configuration for a given item name.
	 * 
	 * @return {@code null} if there is no binding config for this item name.
	 */
	public HolidaysBindingConfig getConfig(String itemName);
}
