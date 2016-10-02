/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.base.formula.inst.ScopeInstanceFactory;

/**
 * A DynamicDependency is a formula dependency that can change the absolute variable upon
 * which the item is dependent.
 * 
 * The most common scenario that can be described is the situation of a local variable
 * (localvar). In the situation where you don't know which object to pull the local
 * variable from until the equation is resolved, that is a dynamic dependency. (Because
 * you have to resolve one variable to know the absolute location of the variable to draw
 * upon).
 */
public class DynamicDependency
{

	/**
	 * The VariableID that contains the variable that controls which local variable scope
	 * is used to resolve the source variable name.
	 */
	private final VariableID<?> controlVar;

	/**
	 * The source variable name for the dynamic dependency. Stored as the variable name
	 * since the actual VariableID is relative to the value of controlVar.
	 */
	private final String sourceVarName;

	/**
	 * The source scope name for the dynamic dependency.
	 */
	private final String sourceScopeName;

	/**
	 * The VariableLibrary used to return VariableID objects.
	 */
	private final VariableLibrary varLibrary;

	/**
	 * Constructs a new DynamicDependency with the given VariableLibrary, Control
	 * Variable, and source scope name and variable name.
	 * 
	 * @param varLibrary
	 *            The VariableLibrary used to return VariableID objects
	 * @param controlVar
	 *            The VariableID that contains the variable that controls which local
	 *            variable scope is used to resolve the source variable name
	 * @param sourceScopeName
	 *            The source scope name for the dynamic dependency
	 * @param sourceVarName
	 *            The source variable name for the dynamic dependency
	 */
	public DynamicDependency(VariableLibrary varLibrary, VariableID<?> controlVar,
		String sourceScopeName, String sourceVarName)
	{
		this.varLibrary = Objects.requireNonNull(varLibrary);
		this.controlVar = Objects.requireNonNull(controlVar);
		this.sourceScopeName = Objects.requireNonNull(sourceScopeName);
		this.sourceVarName = Objects.requireNonNull(sourceVarName);
	}

	/**
	 * Returns the VariableID of the source of the dynamic dependency, given the given
	 * source object. The given ScopeInstanceFactory is used to resolve the ScopeInstance
	 * for the given source object.
	 * 
	 * @return the VariableID of the source of the dynamic dependency, given the given
	 *         source object
	 */
	public VariableID<?> generateSourceVarID(ScopeInstanceFactory siFactory,
		VarScoped sourceObject)
	{
		ScopeInstance scopeInst = siFactory.get(sourceScopeName, sourceObject);
		return varLibrary.getVariableID(scopeInst, sourceVarName);
	}

	/**
	 * Returns the VariableID that controls which local variable scope is used to resolve
	 * the source variable name.
	 * 
	 * @return the VariableID that controls which local variable scope is used to resolve
	 *         the source variable name
	 */
	public VariableID<?> getControlVar()
	{
		return controlVar;
	}
}
