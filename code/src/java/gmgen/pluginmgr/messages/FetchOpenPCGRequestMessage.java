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
 *  Send this message to inform all components that a a call has taken
 *  place asking the current tab to open a file.
 *
 *@author     Soulcatcher
 *@since    May 23, 2003
 */
public class FetchOpenPCGRequestMessage extends GMBMessage
{

	/**
	 *  Constructor for the FetchOpenPCGRequestMessage object
	 *
	 *@param  comp Component sends a message requestiong PCGen to send all open PCGs
	 */
	public FetchOpenPCGRequestMessage(GMBComponent comp)
	{
		super(comp);
	}
}
