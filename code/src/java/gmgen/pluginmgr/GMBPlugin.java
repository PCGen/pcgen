/*
 *  GMBPlugin.java - An GMBus plugin
 *  :noTabs=false:
 *
 *  Copyright (C) 2003 Devon Jones
 *  Derived from jEdit by Slava Pestov Copyright (C) 1999
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

import javax.swing.filechooser.FileFilter;

/**
 *  Plugins extending this class are automatically added to the GMBus.
 *
 *@author     Soulcatcher
 *@since        GMGen 3.3
 */
public abstract class GMBPlugin extends Plugin implements GMBComponent
{
	// protected members

	/**
	 *  Constructor for the GMBPlugin object
	 *@since        GMGen 3.3
	 */
	protected GMBPlugin()
	{
	    // Empty Constructor
	}

	/**
	 * Get the file types
	 * @return file types
	 */
	public abstract FileFilter[] getFileTypes();

	/**
	 *  Handles a message sent on the GMBus. The default implementation ignores the
	 *  message.
	 *
	 *@param  message  the message recieved
	 *@since        GMGen 3.3
	 */
	public void handleMessage(GMBMessage message)
	{
	    // TODO This method currently does nothing?
	}
}
