/*
 * Copyright 2014-16 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.base.util.TypedKey;
import pcgen.base.util.MappedDeque;

/**
 * A DependencyManager is a class to capture Formula dependencies.
 * 
 * In order to capture specific dependencies, a specific dependency should be
 * loaded into this DependencyManager.
 */
public class DependencyManager extends MappedDeque
{

	private static final TypedKey<ArrayList<VariableID<?>>> VARIABLES =
			new TypedKey<ArrayList<VariableID<?>>>();

	/**
	 * A TypedKey used for storing the FormulaManager contained in this
	 * DependencyManager
	 */
	public static final TypedKey<FormulaManager> FMANAGER =
			new TypedKey<FormulaManager>();

	/**
	 * A TypedKey used for storing the ScopeInstance contained in this
	 * DependencyManager
	 */
	public static final TypedKey<ScopeInstance> INSTANCE =
			new TypedKey<ScopeInstance>();

	/**
	 * A TypedKey used for storing the Format currently asserted for the formula
	 * served by this DependencyManager
	 */
	public static final TypedKey<Class<?>> ASSERTED = new TypedKey<Class<?>>();

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
		ArrayList<VariableID<?>> vars = peek(VARIABLES);
		if (vars == null)
		{
			vars = new ArrayList<>();
			set(VARIABLES, vars);
		}
		vars.add(varID);
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
		List<VariableID<?>> vars = peek(VARIABLES);
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
	 * Generates an initialized DependencyManager with the given arguments.
	 * 
	 * @param formulaManager
	 *            The FormulaManager to be contained in the DependencyManager
	 * @param scopeInst
	 *            The ScopeInstance to be contained in the DependencyManager
	 * @param assertedFormat
	 *            The format currently asserted for the formula served by the
	 *            DependencyManager
	 * @return An initialized DependencyManager with the given arguments
	 */
	public static DependencyManager generate(FormulaManager formulaManager,
		ScopeInstance scopeInst, Class<?> assertedFormat)
	{
		DependencyManager fdm = new DependencyManager();
		fdm.set(DependencyManager.FMANAGER, formulaManager);
		fdm.set(DependencyManager.INSTANCE, scopeInst);
		fdm.set(DependencyManager.ASSERTED, assertedFormat);
		return fdm;
	}
}
