/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  Spell.java
 *
 *  Created on January 16, 2002, 12:27 PM
 */
package gmgen.plugin;

import org.jdom.Element;
import pcgen.util.Logging;

import java.util.List;
import java.util.Vector;

/**
 *@author     devon
 *@since    March 20, 2003
 *@version $Revision$
 */
public class Spell extends Event
{
	protected String anEffect;
	protected String aName;
	protected String aPlayer;
	protected String aStatus = "Active";
	protected boolean anAlert;
	protected int aDuration;

	/**
	 *  Creates new Spell
	 *
	 *@param  name      Description of the Parameter
	 *@param  player    Description of the Parameter
	 * @param effect
	 *@param  duration  Description of the Parameter
	 *@param  init      Description of the Parameter
	 * @param alert
	 */
	public Spell(String name, String player, String effect, int duration, int init, boolean alert)
	{
		super(name, player, effect, duration, init, alert);
	}

	public Spell(Element spell)
	{
		try
		{
			String theName = spell.getAttribute("name").getValue();
			String thePlayer = spell.getAttribute("player").getValue();
			String theStatus = spell.getAttribute("status").getValue();
			String theEffect = spell.getAttribute("effect").getValue();
			int theDuration = spell.getChild("Initiative").getAttribute("duration").getIntValue();
			int anInit = spell.getChild("Initiative").getAttribute("initiative").getIntValue();
			boolean theAlert = spell.getChild("Initiative").getAttribute("alert").getBooleanValue();
			setValues(theName, thePlayer, theStatus, theEffect, theDuration, anInit, theAlert);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Initiative", e);
		}
	}

    @Override
	public String getEndText()
	{
		return "Spell " + getName() + "'s Duration Expired";
	}

	/**
	 *  builds a vector that is intended to be turned into a table row that
	 *  contains all of this object's informaiton
	 *
	 *@param  columnOrder  The current table's column order
	 *@return              The Row Vector
	 */
    @Override
	public Vector<String> getRowVector(List<String> columnOrder)
	{
		Vector<String> rowVector = new Vector<String>();

		for ( String columnName : columnOrder )
		{
			if (columnName.equals("Name"))
			{ // Spell's name
				rowVector.add("Spell: " + getName());
			}
			else if (columnName.equals("Player"))
			{ // Player's Name who cast the spell
				rowVector.add("Owner: " + getPlayer());
			}
			else if (columnName.equals("Status"))
			{ // Spell's Status
				rowVector.add(getStatus());
			}
			else if (columnName.equals("+"))
			{ // Ignored
				rowVector.add("");
			}
			else if (columnName.equals("Init"))
			{ // Spell's Initiative
				rowVector.add("" + init.getCurrentInitiative());
			}
			else if (columnName.equals("Dur"))
			{ // Spell's Duration
				rowVector.add("" + getDuration());
			}
			else if (columnName.equals("#"))
			{ // Ignored
				rowVector.add("");
			}
			else if (columnName.equals("HP"))
			{ // Ignored
				rowVector.add("");
			}
			else if (columnName.equals("HP Max"))
			{ // Ignored
				rowVector.add("");
			}
			else if (columnName.equals("Type"))
			{ //PC, Enemy, Ally, -
				rowVector.add("-");
			}
		}

		return rowVector;
	}

    @Override
	public Element getSaveElement()
	{
		Element retElement = new Element("Spell");
		Element initiative = new Element("Initiative");

		initiative.setAttribute("initiative", init.getCurrentInitiative() + "");
		initiative.setAttribute("duration", getDuration() + "");
		initiative.setAttribute("alert", isAlert() + "");
		retElement.addContent(initiative);

		retElement.setAttribute("name", getName());
		retElement.setAttribute("player", getPlayer());
		retElement.setAttribute("status", getStatus());
		retElement.setAttribute("effect", getEffect());

		return retElement;
	}
}
