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

import pcgen.cdom.base.Constants;
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

	/**
	 * Default Constructor.
	 * 
	 * <p>Creates an empty Filter that is not usable.
	 */
	protected AbstractPObjectFilter()
	{
		this(Constants.EMPTY_STRING, Constants.EMPTY_STRING, Constants.EMPTY_STRING);
	}

	/**
	 * Create a filter with just a name.
	 * 
	 * @param argName The name for the filter.
	 */
	protected AbstractPObjectFilter(String argName)
	{
		this(Constants.EMPTY_STRING, argName, Constants.EMPTY_STRING);
	}

	/**
	 * Create a filter with a name and category.
	 * 
	 * @param argCategory The category for the filter.
	 * @param argName The name of the filter.
	 */
	protected AbstractPObjectFilter(String argCategory, String argName)
	{
		this(argCategory, argName, Constants.EMPTY_STRING);
	}

	private AbstractPObjectFilter(String argCategory, String argName, String argDescription)
	{
		setCategory(argCategory);
		setName(argName);
		setDescription(argDescription);
	}

	/**
     * Get the category
     * @return category 
	 */
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

    /**
     * Get the description
     * @return description
     */
	public String getDescription()
	{
		return description;
	}

	/**
     * Get description given a PC
     * 
     * @param aPC
     * @return description 
	 */
    public String getDescription(@SuppressWarnings("unused")PlayerCharacter aPC)
	{
		return description;
	}

    /**
     * Get name
     * @return name
     */
	public String getName()
	{
		return name;
	}
	
	/**
     * Get name given a PC
     * 
     * @param aPC
     * @return name 
	 */
    public String getName(@SuppressWarnings("unused")PlayerCharacter aPC)
	{
		return name;
	}

    /**
     * equals method, returns true if filter is equal
     * 
     * @param object
     * @return true if equal
     */
	@Override
	public final boolean equals(Object object)
	{
		if (object instanceof PObjectFilter)
		{
			return equals((PObjectFilter) object);
		}

		return super.equals(object);
	}

    /**
     * Hashcode
     * 
     * @return hashcode of this filter
     */
	@Override
	public final int hashCode()
	{
		return toString().hashCode();
	}

	/**
     * Abstract method, returns true if filter accepts the PC and Object
     * 
	 * @param aPC 
	 * @param pObject 
	 * @return true or false 
	 */
    public abstract boolean accept(PlayerCharacter aPC, PObject pObject);

	/**
     * toString - category + name or name
     * @return category + name or name
	 */
    @Override
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
