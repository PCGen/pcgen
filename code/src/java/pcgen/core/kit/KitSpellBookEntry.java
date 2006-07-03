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
 * $Id$
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
	// TODO This variable is never used
	private String className;
	private String bookName;
	private String theName = null;
	private List<String> theModifierList = null;
	private int theCount = 1;

	private PCClass theClass = null;

	/**
	 *
	 * @param aClassName
	 * @param aBookName
	 * @param aName
	 * @param modifiers
	 */
	public KitSpellBookEntry(final String aClassName, final String aBookName,
							 final String aName, final List<String> modifiers)
	{
		className = aClassName;
		bookName = aBookName;
		theName = aName;
		if (modifiers != null && modifiers.size() > 0)
		{
			theModifierList = new ArrayList<String>();
			theModifierList.addAll(modifiers);
		}
	}

	/**
	 * Get the spell book name
	 * @return the spell book name
	 */
	public String getBookName()
	{
		return bookName;
	}

	/**
	 * Get the name
	 * @return name
	 */
	public String getName()
	{
		return theName;
	}

	/**
	 * Get the modifiers
	 * @return the modifiers
	 */
	public List<String> getModifiers()
	{
		List<String> ret = theModifierList;
		if (ret == null)
		{
			ret = new ArrayList<String>();
		}
		return Collections.unmodifiableList(ret);
	}

	/**
	 * Get the number of copies
	 * @return the number of copies
	 */
	public int getCopies()
	{
		return theCount;
	}

	/**
	 * Add copies
	 * @param numCopies
	 * @return the updated number of copies
	 */
	public int addCopies(final int numCopies)
	{
		return theCount += numCopies;
	}

	/**
	 * Set the PC Class
	 * @param aClass
	 */
	public void setPCClass(final PCClass aClass)
	{
		theClass = aClass;
	}

	/**
	 * Get the class of the PC
	 * @return the class of the PC
	 */
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
