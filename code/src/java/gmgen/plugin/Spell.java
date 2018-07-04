/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package gmgen.plugin;

import java.util.List;
import java.util.Vector;

import org.jdom2.Element;

import pcgen.util.Logging;

public class Spell extends Event
{
	protected String aName;

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
			State theStatus = State.getState(spell.getAttribute("status").getValue());
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
	public Vector<Object> getRowVector(List<String> columnOrder)
	{
		Vector<Object> rowVector = new Vector<>();

		for (String columnName : columnOrder)
		{
			switch (columnName)
			{
				case "Name": // Spell's name
					rowVector.add("Spell: " + getName());
					break;
				case "Player": // Player's Name who cast the spell
					rowVector.add("Owner: " + getPlayer());
					break;
				case "Status": // Spell's Status
					rowVector.add(getStatus());
					break;
				case "+": // Ignored
					rowVector.add("");
					break;
				case "Init": // Spell's Initiative
					rowVector.add(String.valueOf(init.getCurrentInitiative()));
					break;
				case "Dur": // Spell's Duration
					rowVector.add(String.valueOf(getDuration()));
					break;
				case "#": // Ignored
					rowVector.add("");
					break;
				case "HP": // Ignored
					rowVector.add("");
					break;
				case "HP Max": // Ignored
					rowVector.add("");
					break;
				case "Type": //PC, Enemy, Ally, -
					rowVector.add("-");
					break;
				default:
					//Case not caught, should this cause an error?
					break;
			}
		}

		return rowVector;
	}

	@Override
	public Element getSaveElement()
	{
		Element retElement = new Element("Spell");
		Element initiative = new Element("Initiative");

		initiative.setAttribute("initiative", String.valueOf(init.getCurrentInitiative()));
		initiative.setAttribute("duration", String.valueOf(getDuration()));
		initiative.setAttribute("alert", String.valueOf(isAlert()));
		retElement.addContent(initiative);

		retElement.setAttribute("name", getName());
		retElement.setAttribute("player", getPlayer());
		retElement.setAttribute("status", getStatus().name());
		retElement.setAttribute("effect", getEffect());

		return retElement;
	}
}
