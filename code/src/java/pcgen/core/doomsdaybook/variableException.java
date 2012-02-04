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
 * variableException.java
 *
 * Created on November 1, 2002, 1:22 PM
 */
package pcgen.core.doomsdaybook;

/**
 * <code>variableException</code> is an exception raised when a problem 
 * occurs when processing a variable.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author Devon D Jones
 * @version $Revision$
 */
public class variableException extends Exception
{
	/**
	 * Creates a new instance of <code>variableException</code> without detail message.
	 */
	public variableException()
	{
		// Empty Constructor
	}

	/**
	 * Constructs an instance of <code>variableException</code> with the specified detail message.
	 * @param msg the detail message.
	 */
	public variableException(String msg)
	{
		super(msg);
	}
}
