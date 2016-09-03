/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.formula.base;

/**
 * A DefaultStore is an object that can return the default value of a format
 * (provided as a Class).
 */
public interface DefaultStore
{
	/**
	 * Returns the default value for a given Format (provided as a Class).
	 * 
	 * @param <T>
	 *            The format (class) of object for which the default value
	 *            should be returned
	 * @param varFormat
	 *            The Class (data format) for which the default value should be
	 *            returned
	 * @return The default value for the given Format
	 */
	public <T> T getDefault(Class<T> varFormat);
}
