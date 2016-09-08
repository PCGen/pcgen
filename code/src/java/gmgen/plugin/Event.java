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
 *
 *  Spell.java
 *
 *  Created on January 16, 2002, 12:27 PM
 */
package gmgen.plugin;

import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;

import pcgen.util.Logging;

import org.jdom.Element;

/**
 *@author     devon
 */
public class Event implements InitHolder
{
	/**  Description of the Field */
	public SystemInitiative init;
	protected String effect;
	protected String name;
	protected String player;
	protected State status = State.Active;
	protected boolean alert;
	protected int duration;

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
	public Event(String name, String player, String effect, int duration, int init, boolean alert)
	{
		setValues(name, player, status, effect, duration, init, alert);
	}

	/**
	 * Constructor
	 * @param name
	 * @param player
	 * @param status
	 * @param effect
	 * @param duration
	 * @param init
	 * @param alert
	 */
	public Event(String name, String player, State status, String effect, int duration, int init, boolean alert)
	{
		setValues(name, player, status, effect, duration, init, alert);
	}

	/**
	 * Constructor
	 */
	public Event()
	{
		// Empty Constructor
	}

	/**
	 * Constructor
	 * @param event
	 */
	public Event(Element event)
	{
		try
		{
			String aName = event.getAttribute("name").getValue();
			String aPlayer = event.getAttribute("player").getValue();
			State aStatus = State.getState(event.getAttribute("status").getValue());
			String anEffect = event.getAttribute("effect").getValue();
			int aDuration = event.getChild("Initiative").getAttribute("duration").getIntValue();
			int anInit = event.getChild("Initiative").getAttribute("initiative").getIntValue();
			boolean anAlert = event.getChild("Initiative").getAttribute("alert").getBooleanValue();
			setValues(aName, aPlayer, aStatus, anEffect, aDuration, anInit, anAlert);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Initiative", e);
		}
	}

	/**
	 * Return true if alert
	 * @return true if alert
	 */
	public boolean isAlert()
	{
		return alert;
	}

	/**
	 *  Sets the duration attribute of the Spell object
	 *
	 *@param  duration  The new duration value
	 */
	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	/**
	 *  Gets the duration attribute of the Spell object
	 *
	 *@return    The duration value
	 */
	public int getDuration()
	{
		return duration;
	}

	/**
	 * Set effect
	 * @param effect
	 */
	public void setEffect(String effect)
	{
		this.effect = effect;
	}

	/**
	 * Get effect
	 * @return effect
	 */
	public String getEffect()
	{
		return effect;
	}

	/**
	 * Get end text
	 * @return end text
	 */
	public String getEndText()
	{
		return MessageFormat.format("Event {0} Completed or Occurred", name);
	}

	/**
	 *  Gets the initiative object of the Spell object
	 *
	 *@return    The initiative object
	 */
    @Override
	public SystemInitiative getInitiative()
	{
		return init;
	}

	/**
	 *  Sets the name attribute of the Spell object
	 *
	 *@param  name  The new name value
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Gets the name attribute of the Spell object
	 *
	 *@return    The name value
	 */
    @Override
	public String getName()
	{
		return name;
	}

	/**
	 *  Sets the player attribute of the Spell object
	 *
	 *@param  player  The new player value
	 */
	public void setPlayer(String player)
	{
		this.player = player;
	}

	/**
	 *  Gets the player attribute of the Spell object
	 *
	 *@return    The player value
	 */
    @Override
	public String getPlayer()
	{
		return player;
	}

	/**
	 *  builds a vector that is intended to be turned into a table row that
	 *  contains all of this object's information
	 *
	 *@param  columnOrder  The current table's column order
	 *@return              The Row Vector
	 */
    @Override
	public Vector<Object> getRowVector(List<String> columnOrder)
	{
		Vector<Object> rowVector = new Vector<>();

		// Event's name
		// Player's Name who cast the spell
		// Event's Status
		// Ignored
		// Event's Initiative
		// Event's Duration
		// Ignored
		//PC, Enemy, Ally, -
		columnOrder.forEach(columnName ->
		{
			if (columnName.equals("Name"))
			{ // Event's name
				rowVector.add(name);
			}
			else if (columnName.equals("Player"))
			{ // Player's Name who cast the spell
				rowVector.add("Owner: " + player);
			}
			else if (columnName.equals("Status"))
			{ // Event's Status
				rowVector.add(status);
			}
			else if (columnName.equals("+"))
			{ // Ignored
				rowVector.add("");
			}
			else if (columnName.equals("Init"))
			{ // Event's Initiative
				rowVector.add(String.valueOf(init.getCurrentInitiative()));
			}
			else if (columnName.equals("Dur"))
			{ // Event's Duration
				rowVector.add(String.valueOf(duration));
			}
			else if (columnName.equals("#") || columnName.equals("HP") || columnName.equals("HP Max"))
			{ // Ignored
				rowVector.add("");
			}
			else if (columnName.equals("Type"))
			{ //PC, Enemy, Ally, -
				rowVector.add("-");
			}
		});

		return rowVector;
	}

    @Override
	public Element getSaveElement()
	{
		Element retElement = new Element("Event");
		Element initiative = new Element("Initiative");

		initiative.setAttribute("initiative", String.valueOf(init.getCurrentInitiative()));
		initiative.setAttribute("duration", String.valueOf(duration));
		initiative.setAttribute("alert", String.valueOf(alert));
		retElement.addContent(initiative);

		retElement.setAttribute("name", name);
		retElement.setAttribute("player", player);
		retElement.setAttribute("status", status.name());
		retElement.setAttribute("effect", effect);

		return retElement;
	}

	/**
	 *  Sets the status attribute of the Spell object
	 *
	 *@param  status  The new status value
	 */
    @Override
	public void setStatus(State status)
	{
		this.status = status;
	}

	/**
	 *  Gets the status attribute of the Spell object
	 *
	 *@return    The status value
	 */
    @Override
	public State getStatus()
	{
		return status;
	}

	/**
	 *  Decrements the duration
	 *
	 *@return    new duration
	 */
	public int decDuration()
	{
		duration--;

		return duration;
	}

	/**
	 *  changes the value of a table field in the backend data set
	 *
	 *@param  columnOrder  A list of columns in order for the table
	 *@param  colNumber    What column number has been edited
	 *@param  data         The new value for the field
	 */
    @Override
	public void editRow(List<String> columnOrder, int colNumber, Object data)
	{
		String columnName = columnOrder.get(colNumber);
		String strData = String.valueOf(data);

		if (columnName.equals("Name"))
		{ // Spell's Name
			this.name = strData;
		}
		else if (columnName.equals("Player"))
		{ // Name of the player who cast the spell
			this.player = strData;
		}
		else if (columnName.equals("Status"))
		{ // SPell's status
			this.status = State.getStateLocalised(strData);
		}
		else if (columnName.equals("Init"))
		{ // Spell's Initiative

			Integer intData = Integer.valueOf(strData);
			init.setCurrentInitiative(intData);
		}
		else if (columnName.equals("Dur"))
		{ // Spell's duration

			Integer intData = Integer.valueOf(strData);
			this.duration = intData;
		}
	}

    @Override
	public void endRound()
	{
		// TODO This method currently does nothing?
	}

	/**
	 * Constructor helper method.
	 * Made it final as it is called from constructor.
	 * @param name
	 * @param player
	 * @param status
	 * @param effect
	 * @param duration
	 * @param init
	 * @param alert
	 */
	final void setValues(String name, String player, State status, String effect, int duration, int init,
	                     boolean alert)
	{
		this.name = name;
		this.player = player;
		this.effect = effect;
		this.duration = duration;
		this.init = new SystemInitiative(0);
		this.init.setCurrentInitiative(init);
		this.alert = alert;
		this.status = status;
	}
}
