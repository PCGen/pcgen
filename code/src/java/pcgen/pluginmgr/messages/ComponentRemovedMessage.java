/*
 * ComponentRemovedMessage.java
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
 * Created on 22/02/2014 3:48:31 pm
 *
 * $Id$
 */
package pcgen.pluginmgr.messages;

import pcgen.pluginmgr.PCGenMessage;
import pcgen.pluginmgr.PCGenMessageHandler;

/**
 * The Class {@code ComponentRemovedMessage} indicates that a plugin has
 * been removed from the system. 
 *
 * <br>
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 */
public class ComponentRemovedMessage extends PCGenMessage
{
	private final PCGenMessageHandler plugin;

	/**
	 * Create a new instance of ComponentRemovedMessage
	 * @param source The source of the message.
	 * @param plugin The plugin that was removed.
	 */
	public ComponentRemovedMessage(Object source, PCGenMessageHandler plugin)
	{
		super(source);
		this.plugin = plugin;
	}

	public PCGenMessageHandler getPlugin()
	{
		return plugin;
	}
}
