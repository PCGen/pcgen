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

/**
 */
public class Effect extends Event
{

	/**
	 *  Creates new Spell
	 * @param player
	 * @param description
	 * @param duration
	 * @param alert
	 */
	public Effect(String player, String description, int duration, boolean alert)
	{
		super("", player, description, duration, 0, alert);
	}

	@Override
	public String getEndText()
	{
		return "Effect " + getName() + " Completed or Occurred";
	}

	/**
	 *  builds a vector that is intended to be turned into a table row that
	 *  contains all of this object's informaiton
	 *
	 *@param  columnOrder  The current table's column order
	 *@return              The Row Vector
	 */
	@SuppressWarnings({"UseOfObsoleteCollectionType", "PMD.ReplaceVectorWithList"})
	@Override
	public Vector<Object> getRowVector(List<String> columnOrder)
	{
		Vector<Object> rowVector = new Vector<>();

		for (final String columnName : columnOrder)
		{
			switch (columnName)
			{
				case "Name": // Event's name
					rowVector.add("");
					break;
				case "Player": // Player's Name who cast the spell
					rowVector.add("Owner: " + getPlayer());
					break;
				case "Status": // Event's Status
					rowVector.add(getStatus());
					break;
				case "Init": // Event's Initiative
					rowVector.add("");
					break;
				case "Dur": // Event's Duration
					rowVector.add(String.valueOf(getDuration()));
					break;
				case "+": // Ignored
				case "#": // Ignored
				case "HP": // Ignored
				case "HP Max": // Ignored
				case "Type": //PC, Enemy, Ally, -
					rowVector.add("");
					break;
				default:
					//Case not caught, should this cause an error?
					break;
			}
		}

		return rowVector;
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

		switch (columnName)
		{
			case "Name": // Spell's Name
				setName(strData);
				break;
			case "Player": // Name of the player who cast the spell
				setPlayer(strData);
				break;
			case "Status": // SPell's status
				setStatus(State.getState(strData));
				break;
			case "Dur": // Spell's duration
				Integer intData = Integer.valueOf(strData);
				setDuration(intData);
				break;
			default:
				//Case not caught, should this cause an error?
				break;
		}
	}
}
