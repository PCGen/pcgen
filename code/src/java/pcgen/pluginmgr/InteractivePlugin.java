/*
 * Copyright James Dempsey, 2014
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 */
package pcgen.pluginmgr;

import java.io.File;

/**
 * {@code InteractivePlugin} defines the interface that must be provided by
 * any user interactive plugin to PCGen. These plugins are most often defined by 
 * the GMGen module, thus allowing it to be extended.   
 *
 * 
 */
public interface InteractivePlugin extends PCGenMessageHandler
{

	/**
	 * Retrieve the priority of the plugin. This is used to sort which items are 
	 * loaded first, lower priority is first.
	 * @return The priority of the plugin.
	 */
    int getPriority();

	/**
	 * @return The displayable name of the plugin.
	 */
    String getPluginName();

	/**
	 * Advises the plugin that it has been started. The plugin may do any 
	 * required startup and allocation in response.
	 * @param postbox The primary message handler, can be stored and used to send future messages out.
	 */
    void start(PCGenMessageHandler postbox);

	/**
	 * Advises the plugin that it has been stopped. Intended to allow deallocation 
	 * of any resources.
	 */
    void stop();

	/**
	 * Retrieves the folder in which configuration data for the plugin should be 
	 * stored.
	 * @return The directory for config data storage.
	 */
    File getDataDirectory();
}
