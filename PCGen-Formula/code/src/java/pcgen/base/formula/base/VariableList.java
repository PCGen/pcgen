/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A VariableList is a contained list of VariableID objects. It does lazy instantiation
 * and provides ownership protection to the underlying list.
 */
public class VariableList
{

	/**
	 * The underlying List of this Variable List.
	 */
	private List<VariableID<?>> vars;

	/**
	 * Returns a non-null list of VariableID objects that this VariableList contains.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. The contents
	 * of the returned List will not be modified as a result of the VariableList
	 * maintaining or otherwise transferring a reference to the List to another object
	 * (Thus, subsequent calls to add on VariableList will not cause the returned List to
	 * change. Also, the VariableList cannot be modified if the returned list is
	 * modified.)
	 * 
	 * @return A non-null list of VariableID objects that identify the list of
	 *         dependencies of the Formula this VariableList contains
	 */
	public List<VariableID<?>> getVariables()
	{
		if (vars == null)
		{
			vars = Collections.emptyList();
		}
		else
		{
			vars = new ArrayList<>(vars);
		}
		return vars;
	}

	/**
	 * Adds a new VariableID to this VariableList.
	 * 
	 * @param varID
	 *            The VariableID to be added to this VariableList
	 */
	public void add(VariableID<?> varID)
	{
		if (vars == null)
		{
			vars = new ArrayList<>();
		}
		vars.add(varID);
	}

}
