/*
 *  PCLoadedMessage.java - A GMBus message
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
import pcgen.core.PlayerCharacter;

import java.util.ArrayList;
import java.util.List;

/**
 *  Message sending out a loaded PC
 *
 *@author     Soulcatcher
 *@since    May 23, 2003
 */
public class PCLoadedMessage extends GMBMessage
{
	private List ignoreList = new ArrayList();
	private PlayerCharacter pc;

	/**
	 *  Constructor for the PCLoadedMessage object
	 *
	 *@param  comp  Component requesting the list
	 *@param  pc
	 */
	public PCLoadedMessage(GMBComponent comp, PlayerCharacter pc)
	{
		super(comp);
		this.pc = pc;
	}

	public boolean isIgnored(GMBComponent comp)
	{
		for (int i = 0; i < ignoreList.size(); i++)
		{
			if (comp == ignoreList.get(i))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 *  Gets the combatantList that has been sent in this message
	 *
	 *@return    The Combatant List
	 */
	public PlayerCharacter getPC()
	{
		return pc;
	}

	public void addIgnore(GMBComponent comp)
	{
		ignoreList.add(comp);
	}
}
