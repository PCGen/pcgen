/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 *
 */
package pcgen.core;

/**
 * Interface to describe an object that has variables associated with it.  A
 * variable is a named object that has a value.  Currently this interface is
 * small however it will be extended over time as the variable handling routines
 * are made common.
 */
public interface VariableContainer
{
	/**
	 * Get the value of a variable passed as aString.
	 *
	 * @param varName The name of the variable to look up
	 * @param src The context in which to look up the variable
	 * @param aPC The current PC
	 *
	 * @return the value of the variable
	 */
	public Float getVariableValue(String varName, String src, PlayerCharacter aPC);
}
