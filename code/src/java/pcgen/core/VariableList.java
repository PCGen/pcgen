/*
 *  VariableList.java
 *  Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * @author Scott Ellsworth
 */
package pcgen.core;

import java.util.*;

/**
 * <code>VariableList</code> encapsulates a list of Variable objects 
 * along with various methods to aid in the efficient processing of 
 * the variables. 
 *
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006/01/29 00:02:13 $
 *
 * @author Scott Ellsworth
 * @version $Revision: 1.17 $
 */
public class VariableList implements Cloneable
{
	/** The list of variables. */
	private ArrayList list = new ArrayList();
	/** A read-only copy of the variable list for external use. */
	private List unmodifiableList = Collections.unmodifiableList(list);
	/** The set of the names in the variable list. */
	private Set nameSet = null;

	/**
	 * Retrieve the definition of the variable at the specified 
	 * location in the list.
	 * 
	 * @param i The index of the location to be retrieved.
	 * @return The definition of the variable. 
	 */
	public final String getDefinition(final int i)
	{
		final Variable v = (Variable) list.get(i);

		return v.getDefinition();
	}

	/**
	 * Retrieve the variable at the specified location in the list.
	 * 
	 * @param i The index of the location to be retrieved.
	 * @return The variable. 
	 */
	public final Variable getVariable(final int i)
	{
		final Variable v = (Variable) list.get(i);

		return v;
	}

	/**
	 * Retrieve a read-only set of the variable names contained in the 
	 * list.
	 * 
	 * @return The set of names.
	 */
	public final Set getVariableNamesAsUnmodifiableSet()
	{
		if (nameSet == null)
		{
			cacheNames();
		}

		return Collections.unmodifiableSet(nameSet);
	}

	/**
	 * Add a new Variable to the list.
	 * 
	 * @param level The level at which the variable should be applied, 0 if always.
	 * @param variableName The name of the variable.
	 * @param defaultFormula The formula which should be used to calculate its value.
	 */
	public final void add(final int level, final String variableName, final String defaultFormula)
	{
		final Variable v = new Variable(level, variableName, defaultFormula);
		list.add(v);
		clearNameCache();
	}

	/**
	 * Remove all entries from the list of variables.
	 */
	public void clear()
	{
		list.clear();
		nameSet = null;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException
	{
		final VariableList retVal = (VariableList) super.clone();
		retVal.list = (ArrayList) list.clone();

		return retVal;
	}

	/**
	 * Check if a named variable is held.
	 *  
	 * @param variableName The name to be checked for.
	 * @return True if the variable is present, false otherwise.
	 */
	public final boolean hasVariableNamed(final String variableName)
	{
		final String upperName = variableName.toUpperCase();

		if (nameSet == null)
		{
			cacheNames();
		}

		return nameSet.contains(upperName);
	}

	/**
	 * Retrieve an iterator over a read-only copy of the variable list.
	 * 
	 * @return The iterator.
	 */
	public final Iterator iterator()
	{
		return unmodifiableList.iterator();
	}

	/**
	 * Set a specific index in the list to a new Variable.
	 * 
	 * @param idx The index to be set.
	 * @param level The level at which the variable should be applied, 0 if always.
	 * @param variableName The name of the variable.
	 * @param defaultFormula The formula which should be used to calculate its value.
	 */
	public final void set(final int idx, final int level, final String variableName, final String defaultFormula)
	{
		final Variable v = new Variable(level, variableName, defaultFormula);
		list.set(idx, v);
		clearNameCache();
	}

	/**
	 * Retrieve the number of entries in the list.
	 * @return The size of the list.
	 */
	public final int size()
	{
		return list.size();
	}

	/**
	 * Add all entries from another VariableList to this VariableList. Note the 
	 * added entries will be shared with both lists.
	 * @param vOther The list of Variables to be added.
	 */
	protected final void addAll(final VariableList vOther)
	{
		list.addAll(vOther.list);
		clearNameCache();
	}

	/**
	 * Populate nameSet, the set of the names in the variable list.    
	 */
	private final void cacheNames()
	{
		nameSet = new HashSet();

		for (Iterator i = list.iterator(); i.hasNext();)
		{
			final Variable v = (Variable) i.next();
			nameSet.add(v.getUpperName());
		}
	}

	/**
	 * Clear nameSet, the set of the names in the variable list.    
	 */
	private final void clearNameCache()
	{
		nameSet = null;
	}
}
