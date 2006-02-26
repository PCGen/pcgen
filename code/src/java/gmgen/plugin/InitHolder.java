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
 *  InitHolder.java
 *
 *  Created on January 16, 2002, 1:01 PM
 */
package gmgen.plugin;

import org.jdom.Element;

import java.util.List;
import java.util.Vector;

/**
 *@author     devon
 *@since    March 20, 2003
 *@version $Revision: 1.8 $
 */
public interface InitHolder
{
	/*
	 *  History:
	 *  March 20, 2003: Cleanup for Version 1.0
	 */

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
	public Vector getRowVector(List columnOrder);

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
	public void setStatus(String status);

	/**
	 *  Gets the status of the InitHolder
	 *
	 *@return    The status value
	 */
	public String getStatus();

	/**
	 *  changes the value of a table field in the backend data set
	 *
	 *@param  columnOrder  A list of columns in order for the table
	 *@param  colNumber    What column number has been edited
	 *@param  data         The nex value for the field
	 */
	public void editRow(List columnOrder, int colNumber, Object data);

	/**
	 *  Does any end of round effects
	 *
	 */
	public void endRound();
}
