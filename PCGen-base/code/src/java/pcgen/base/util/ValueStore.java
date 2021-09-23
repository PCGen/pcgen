/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

/**
 * A ValueStore is an object that can return the default value of a format (provided as
 * an Identifier).
 */
public interface ValueStore
{
	/**
	 * Returns the default value for a given Format (provided as an Identifier).
	 * 
	 * @param identifier
	 *            The Identifier indicating the Format for which the default value should
	 *            be returned
	 * @return The default value for the Format with the given Identifier
	 */
	public Object getValueFor(String identifier);
}
