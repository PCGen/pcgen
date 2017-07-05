/*
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
 */
package gmgen.pluginmgr.messages;

import java.awt.Component;

import pcgen.pluginmgr.PCGenMessage;

/**
 * The Class {@code RequestAddTabToGMGenMessage} is a message
 * requesting that a tab be added to the GMGen user interface.
 */
@SuppressWarnings("serial")
public class RequestAddTabToGMGenMessage extends PCGenMessage
{

	private final String name;
	private final Component pane;

	/**
	 * Create a new instance of RequestAddTabToGMGenMessage.
	 * 
	 * @param source The object requesting the tab be added.
	 * @param name The name of the tab.
	 * @param pane The contents of the tab.
	 */
	public RequestAddTabToGMGenMessage(Object source,
		String name, Component pane)
	{
		super(source);
		this.name = name;
		this.pane = pane;
	}

	/**
	 * @return the localizedName
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the view
	 */
	public Component getPane()
	{
		return pane;
	}

}
