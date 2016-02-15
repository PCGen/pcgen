/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.analysis;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formula.base.VariableID;

/**
 * A VariableDependencyManager is a class to capture Formula dependencies on
 * other VariableIDs.
 */
public class VariableDependencyManager
{
	/**
	 * The internal list of VariableIDs upon which the formula this
	 * VariableDependencyManager represents is dependent.
	 */
	private final List<VariableID<?>> dependentVars =
			new ArrayList<VariableID<?>>();

	/**
	 * Adds a Variable (identified by the VariableID) to the list of
	 * dependencies for a Formula.
	 * 
	 * @param varID
	 *            The VariableID to be added as a dependency of the Formula this
	 *            VariableDependencyManager represents
	 * @throws IllegalArgumentException
	 *             if the given VariableID is null
	 */
	public void addVariable(VariableID<?> varID)
	{
		if (varID == null)
		{
			throw new IllegalArgumentException("VariableID may not be null");
		}
		dependentVars.add(varID);
	}

	/**
	 * Returns a non-null list of VariableID objects that identify the list of
	 * dependencies of the Formula this VariableDependencyManager represents.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. The
	 * contents of the List will not be modified as a result of the
	 * VariableDependencyManager maintaining or otherwise transferring a
	 * reference to the List to another object (and the
	 * VariableDependencyManager cannot be modified if the returned list is
	 * modified).
	 * 
	 * @return A non-null list of VariableID objects that identify the list of
	 *         dependencies of the Formula this VariableDependencyManager
	 *         represents
	 */
	public List<VariableID<?>> getVariables()
	{
		return new ArrayList<VariableID<?>>(dependentVars);
	}

	/**
	 * Returns true if this VariableDependencyManager has an empty list of
	 * VariableIDs upon which the formula this VariableDependencyManager
	 * represents is dependent.
	 * 
	 * @return if this VariableDependencyManager has no variable dependencies;
	 *         false otherwise
	 */
	public boolean isEmpty()
	{
		return dependentVars.isEmpty();
	}

}
