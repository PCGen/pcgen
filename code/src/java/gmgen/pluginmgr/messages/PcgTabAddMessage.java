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

import java.awt.Component;

/**
 * Send this message to the GMBus is you want the main interface to add a tab
 * That contains the JPanel you send.
 *
 *@author     Soulcatcher
 *@since    May 23, 2003
 */
public class PcgTabAddMessage extends GMBMessage
{
	private Component pane;
	private String name;

	/**
	 *  Constructor for the TabAddMessage object
	 *
	 *@param  comp  Component sending the message
	 *@param  name  Name of the tab to create
	 *@param  pane  JPanel to populate the tab with
	 */
	public PcgTabAddMessage(GMBComponent comp, String name, Component pane)
	{
		super(comp);
		this.name = name;
		this.pane = pane;
	}

	/**
	 *  returns the desired name of the tab
	 *
	 *@return    The name value
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  returns the desired JPanel to put in the tab
	 *
	 *@return    The pane value
	 */
	public Component getPane()
	{
		return pane;
	}
}
