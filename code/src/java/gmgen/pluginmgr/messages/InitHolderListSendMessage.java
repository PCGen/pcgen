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

import gmgen.plugin.InitHolderList;
import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBMessage;

/**
 *  Message sending out the list of combatants.
 *
 *@author     Soulcatcher
 *@since    May 23, 2003
 */
public class InitHolderListSendMessage extends GMBMessage
{
	private InitHolderList combatantList;

	/**
	 *  Constructor for the InitHolderListSendMessage object
	 *
	 *@param  comp  Component requesting the list
	 *@param  cl    Combatant List to be sent
	 */
	public InitHolderListSendMessage(GMBComponent comp, InitHolderList cl)
	{
		super(comp);
		combatantList = cl;
	}

	/**
	 *  Gets the combatantList that has been sent in this message
	 *
	 *@return    The Combatant List
	 */
	public InitHolderList getInitHolderList()
	{
		return combatantList;
	}
}
