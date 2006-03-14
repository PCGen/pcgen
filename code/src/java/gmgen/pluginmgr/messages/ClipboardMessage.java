/*
 *  GMBMessage.java - A GMBus message
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
package gmgen.pluginmgr.messages;

import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBMessage;

/**
 *  Send this message to inform all components that a file save has taken place.
 *
 *@author     Soulcatcher
 *@since    May 23, 2003
 */
public class ClipboardMessage extends GMBMessage
{
	/** CUT = 0 */
	public static final int CUT = 0;
	/** COPY = 1 */
	public static final int COPY = 1;
	/** PASTE = 2 */
	public static final int PASTE = 2;
	private int mode;

	/**
	 *  Constructor for the StateChangedMessage object
	 *
	 *@param  comp Component sending the state changed message
	 * @param mode
	 */
	public ClipboardMessage(GMBComponent comp, int mode)
	{
		super(comp);
		this.mode = mode;
	}

	/**
	 * Get the mode
	 * @return mode
	 */
	public int getMode()
	{
		return mode;
	}
}
