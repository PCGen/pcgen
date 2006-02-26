/*
 * KitSpellBook.java
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on September 26, 2005
 *
 * $Id: KitSpellBookEntry.java,v 1.9 2006/02/16 13:54:41 karianna Exp $
 */
package pcgen.core.kit;

import pcgen.core.PCClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Deals with a SpellBook Entry for Kits
 */
public class KitSpellBookEntry
{
	private String className;
	private String bookName;
	private String theName = null;
	private List theModifierList = null;
	private int theCount = 1;

	private PCClass theClass = null;

	public KitSpellBookEntry(final String aClassName, final String aBookName,
							 final String aName, final List modifiers)
	{
		className = aClassName;
		bookName = aBookName;
		theName = aName;
		if (modifiers != null && modifiers.size() > 0)
		{
			theModifierList = new ArrayList();
			theModifierList.addAll(modifiers);
		}
	}

	/**
	 * @return classname
	 * @deprecated Unused - remove 5.9.5
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * Get the spell book name
	 * @return the spell book name
	 */
	public String getBookName()
	{
		return bookName;
	}

	public String getName()
	{
		return theName;
	}

	public List getModifiers()
	{
		List ret = theModifierList;
		if (ret == null)
		{
			ret = new ArrayList();
		}
		return Collections.unmodifiableList(ret);
	}

	public int getCopies()
	{
		return theCount;
	}

	public int addCopies(final int numCopies)
	{
		return theCount += numCopies;
	}

	public void setPCClass(final PCClass aClass)
	{
		theClass = aClass;
	}

	public PCClass getPCClass()
	{
		return theClass;
	}

	/** TODO Fix this
	 * @return String*/
	public String toString()
	{
		return theName;
	}
}
