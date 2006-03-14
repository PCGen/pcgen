/*
 * AbstractPObjectFilter.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on February 9, 2002, 2:30 PM
 */
package pcgen.gui.filter;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * <code>AbstractPObjectFilter</code>
 * Abstract PObject filter class<br>
 *
 * @author Thomas Behr
 * @version $Revision$
 */
public abstract class AbstractPObjectFilter implements PObjectFilter
{
	private String category;
	private String description;
	private String name;

	protected AbstractPObjectFilter()
	{
		this("", "", "");
	}

	protected AbstractPObjectFilter(String argName)
	{
		this("", argName, "");
	}

	protected AbstractPObjectFilter(String argCategory, String argName)
	{
		this(argCategory, argName, "");
	}

	private AbstractPObjectFilter(String argCategory, String argName, String argDescription)
	{
		setCategory(argCategory);
		setName(argName);
		setDescription(argDescription);
	}

	public String getCategory()
	{
		return category;
	}

	/**
	 * Sets Description
	 * @param d
	 */
	public void setDescription(String d)
	{
		description = normalize(d);
	}

	public String getDescription()
	{
		return description;
	}

	public String getDescription(PlayerCharacter aPC)
	{
		return description;
	}


	public String getName()
	{
		return name;
	}
	
	public String getName(PlayerCharacter aPC)
	{
		return name;
	}

	public final boolean equals(Object object)
	{
		if (object instanceof PObjectFilter)
		{
			return equals((PObjectFilter) object);
		}

		return super.equals(object);
	}

	public final int hashCode()
	{
		return toString().hashCode();
	}

	public abstract boolean accept(PlayerCharacter aPC, PObject pObject);

	public String toString()
	{
		return (category.length() > 0) ? (getCategory() + SEPARATOR + name) : name;
	}

	final void setCategory(String c)
	{
		category = normalize(c);
	}

	void setName(String n)
	{
		name = normalize(n);
	}

	private final boolean equals(PObjectFilter filter)
	{
		return filter.toString().equals(toString());
	}

	private static String normalize(String s)
	{
		final int sLen = s.length();
		StringBuffer work = new StringBuffer(sLen);
		work.append(s);

		for (int i = 0; i < sLen; i++)
		{
			final char current = work.charAt(i);

			if (current == '|')
			{
				work.setCharAt(i, '-');
			}
			else if (current == '[')
			{
				work.setCharAt(i, '(');
			}
			else if (current == ']')
			{
				work.setCharAt(i, ')');
			}
		}

		return work.toString();
	}
}
