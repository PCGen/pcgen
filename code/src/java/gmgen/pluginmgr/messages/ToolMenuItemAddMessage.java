/*
 *  ToolMenuItemAddMessage.java - A GMBus message
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

import javax.swing.JMenuItem;

/**
 * Send this message to the GMBus is you want the main interface to add an item to the Tools menu.
 *
 *@author     Soulcatcher
 *@since    May 23, 2003
 */
public class ToolMenuItemAddMessage extends GMBMessage
{
	private JMenuItem item;

	/**
	 *  Constructor for the TabAddMessage object
	 *
	 *@param  comp  Component sending the message
	 *@param  item  JMenuItem to use
	 */
	public ToolMenuItemAddMessage(GMBComponent comp, JMenuItem item)
	{
		super(comp);
		this.item = item;
	}

	/**
	 *  returns the desired JMenuItem to put in the tab
	 *
	 *@return    The item value
	 */
	public JMenuItem getMenuItem()
	{
		return item;
	}
}
