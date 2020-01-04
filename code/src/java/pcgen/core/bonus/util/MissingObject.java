/*
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 */
package pcgen.core.bonus.util;

/**
 * Used for any Bonus class that can't find the proper object
 */
public class MissingObject
{
	private String objectName;

	/**
	 * Constructor
	 * @param aName
	 */
	public MissingObject(final String aName)
	{
		objectName = aName;
	}

	/**
	 * Get the Object Name that is missing
	 * @return the Object Name that is missing
	 */
	public String getObjectName()
	{
		return objectName;
	}
}
