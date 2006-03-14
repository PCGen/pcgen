/*
 * PObjectHelper.java
 * Copyright 2004 (C) Bryan McRoberts merton_monk@yahoo.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2004, 2:15 PM
 *
 * $Id$
 */
package pcgen.persistence.lst.utils;

import pcgen.core.PObject;

/**
 * @author ???
 * @version $Revision$
 */
public final class PObjectHelper
{
	private PObject object;
	private String tag;
	private int level;

	public PObjectHelper(PObject obj, String aTag, int anInt)
	{
		object = obj;
		tag = aTag;
		level = anInt;
	}

	public PObject getObject()
	{
		return object;
	}

	public String getTag()
	{
		return tag;
	}

	public int getInt()
	{
		return level;
	}
}
