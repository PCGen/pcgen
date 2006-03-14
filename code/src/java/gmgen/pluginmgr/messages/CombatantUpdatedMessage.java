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

import gmgen.plugin.Combatant;
import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBMessage;

/**
 *  Informs the bus that a component has been added.
 *
 *@author     Soulcatcher
 *@since    May 23, 2003
 */
public class CombatantUpdatedMessage extends GMBMessage
{
	protected Combatant cbt;
	/**
	 *  Constructor for the ComponentAddedMessage object
	 *
	 *@param  comp  Component added
	 * @param cbt
	 */
	public CombatantUpdatedMessage(GMBComponent comp, Combatant cbt)
	{
		super(comp);
		this.cbt = cbt;
	}

	/**
	 * @return Returns the cbt.
	 */
	public Combatant getCombatant() {
		return cbt;
	}

	/**
	 * @param cbt The cbt to set.
	 */
	public void setCombatant(Combatant cbt) {
		this.cbt = cbt;
	}
}
