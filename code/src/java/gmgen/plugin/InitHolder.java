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

public interface InitHolder
{
	/**
	 *  Gets the SystemInitiative of the InitHolder
	 *
	 *@return    The SystemInitiative value
	 */
	public SystemInitiative getInitiative();

	/**
	 *  Gets the name attribute of the InitHolder
	 *
	 *@return    The name value
	 */
	public String getName();

	/**
	 *  Gets the player attribute of the InitHolder
	 *
	 *@return    The player value
	 */
	public String getPlayer();

	/**
	 *  Gets a Vector intended for use as a row in a JTable
	 *
	 *@param  columnOrder  The current table's column order
	 *@return              The Row Vector
	 */
	public Vector<Object> getRowVector(List<String> columnOrder);

	/**
	 *  Gets an XML version of the class, appropriate for saving out to file,
	 *  and restoring later.
	 *
	 *@return An XML element appropriate for saving.
	 */
	public Element getSaveElement();

	/**
	 *  Sets the status of the InitHolder
	 *
	 *@param  status  The new status value
	 */
	public void setStatus(State status);

	/**
	 *  Gets the status of the InitHolder
	 *
	 *@return    The status value
	 */
	public State getStatus();

	/**
	 *  changes the value of a table field in the backend data set
	 *
	 *@param  columnOrder  A list of columns in order for the table
	 *@param  colNumber    What column number has been edited
	 *@param  data         The nex value for the field
	 */
	public void editRow(List<String> columnOrder, int colNumber, Object data);

	/**
	 *  Does any end of round effects
	 */
	public void endRound();
	
}
