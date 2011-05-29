/*
 *  RPGeneration - A role playing utility generate interesting things
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
 * VariableException.java
 *
 * Created on November 1, 2002, 1:22 PM
 */
package plugin.doomsdaybook.util;

/**
 * <code>VariableException</code> is an exception raised when a problem
 * occurs when processing a variable.
 *
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 04:36:01 +0000 (Sun, 17 Dec 2006) $
 *
 * @author Devon D Jones
 * @version $Revision: 1777 $
 */
public class VariableException extends Exception
{
	/**
	 * Creates a new instance of <code>VariableException</code> without detail message.
	 */
	public VariableException()
	{
		// Empty Constructor
	}

	/**
	 * Constructs an instance of <code>VariableException</code> with the specified detail message.
	 * @param msg the detail message.
	 */
	public VariableException(String msg)
	{
		super(msg);
	}
}
