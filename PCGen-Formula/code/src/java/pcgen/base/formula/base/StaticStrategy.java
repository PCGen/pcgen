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

import java.util.Objects;
import java.util.Optional;

/**
 * StaticStrategy is a DependencyStrategy used for static variables (Those where the
 * instance and name are known at time of formula parse).
 */
public class StaticStrategy implements DependencyStrategy
{
	@Override
	public void addVariable(DependencyManager depManager, String varName)
	{
		Optional<VariableList> vars = depManager.get(DependencyManager.VARIABLES);
		vars.get().add(Objects.requireNonNull(getVariableID(depManager, varName)));
	}

	/**
	 * Returns the VariableID for the given variable name based on characteristics from
	 * the given DependencyManager.
	 * 
	 * @param depManager
	 *            The DependencyManager used to determine information about the Formula
	 *            being analyzed
	 * @param varName
	 *            The variable name for which the VariableID should be returned
	 * @return The VariableID for the given variable name
	 */
	public static VariableID<?> getVariableID(DependencyManager depManager, String varName)
	{
		VariableLibrary varLib = depManager.get(DependencyManager.VARLIB);
		return varLib.getVariableID(depManager.get(DependencyManager.INSTANCE), varName);
	}
}
