/*
 *  GMBus.java - The GMBus
 *  :noTabs=false:
 *
 *  Copyright (C) 2003 Devon Jones
 *  Derived from jEdit Copyright (C) 1999 Slava Pestov
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gmgen.pluginmgr;

import gmgen.pluginmgr.messages.ComponentAddedMessage;
import gmgen.pluginmgr.messages.ComponentRemovedMessage;
import pcgen.util.Logging;

import java.util.Vector;

/**
 *  The EditBus is GMGen's global event notification mechanism. A number of
 *  messages are sent by GMGen; they are all instances of the classes found in
 *  the <code>gmgen.pluginmgr.messages</code> package. Plugins can also send their
 *  own messages.
 *
 *@author     Soulcatcher
 *@since        GMGen 3.3
 */
public class GMBus
{
	// private members
	private static Vector components = new Vector();
	private static GMBComponent[] copyComponents;

	//private static Hashtable listVectors = new Hashtable();
	//private static Hashtable listArrays = new Hashtable();
	// can't create new instances
	private GMBus()
	{
	    // Empty Constructor
	}

	/**
	 *  Returns an array of all components connected to the bus.
	 *
	 *@return    Array of components
	 *@since        GMGen 3.3
	 */
	public static GMBComponent[] getComponents()
	{
		synchronized (components)
		{
			if (copyComponents == null)
			{
				copyComponents = new GMBComponent[components.size()];
				components.copyInto(copyComponents);
			}

			return copyComponents;
		}
	}

	/**
	 *  Adds a component to the bus. It will receive all messages sent on the bus.
	 *
	 *@param  comp  The component to add
	 *@since        GMGen 3.3
	 */
	public static void addToBus(GMBComponent comp)
	{
		synchronized (components)
		{
			components.addElement(comp);
			send(new ComponentAddedMessage(comp));
			copyComponents = null;
		}
	}

	/**
	 *  Removes a component from the bus.
	 *
	 *@param  comp  The component to remove
	 *@since        GMGen 3.3
	 */
	public static void removeFromBus(GMBComponent comp)
	{
		synchronized (components)
		{
			components.removeElement(comp);
			send(new ComponentRemovedMessage(comp));
			copyComponents = null;
		}
	}

	/**
	 *  Sends a message to all components on the bus. The message will be sent to
	 *  all components in turn, with the original sender receiving it last.
	 *
	 *@param  message  The message to send
	 *@since        GMGen 3.3
	 */
	public static void send(GMBMessage message)
	{
		Logging.debugPrint(message.toString());

		// To avoid any problems if components are added or removed
		// while the message is being sent
		GMBComponent[] comps = getComponents();

		for (int i = 0; i < comps.length; i++)
		{
			try
			{
				//Do everyone else first
				if (comps[i] != message.getSource())
				{
					comps[i].handleMessage(message);

					if (message.isVetoed())
					{
						break;
					}
				}
			}
			catch (Throwable t)
			{
				Logging.errorPrint("Exception" + " while sending message on GMBus:" + t.getMessage(), t);
			}
		}

		//Then the sender
		message.getSource().handleMessage(message);
	}
}
