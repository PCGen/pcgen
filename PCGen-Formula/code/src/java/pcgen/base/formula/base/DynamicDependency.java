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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
public class DynamicDependency implements DependencyStrategy
{

	/**
	 * The VarIDResolver used to determine VariableID objects.
	 */
	private final VarIDResolver varResolver;

	/**
	 * The VariableID that contains the variable that controls which local variable scope
	 * is used to resolve the source variable name.
	 */
	private final VariableID<?> controlVar;

	/**
	 * The source variable names for the dynamic dependency. Stored as the variable names
	 * since the actual VariableID is relative to the value of controlVar.
	 */
	private final List<String> sourceVarNames = new ArrayList<>();

	/**
	 * The source scope name for the dynamic dependency.
	 */
	private final String sourceScopeName;

	/**
	 * Constructs a new DynamicDependency with the given VariableLibrary, Control
	 * Variable, and source scope name and variable name.
	 * 
	 * @param varResolver
	 *            The VariableLibrary used to resolve variables
	 * @param controlVar
	 *            The VariableID that contains the variable that controls which local
	 *            variable scope is used to resolve the source variable name
	 * @param sourceScopeName
	 *            The source scope name for the dynamic dependency
	 */
	public DynamicDependency(VarIDResolver varResolver,
		VariableID<?> controlVar, String sourceScopeName)
	{
		if (!VarScoped.class
			.isAssignableFrom(controlVar.getFormatManager().getManagedClass()))
		{
			throw new IllegalArgumentException(
				"Request to add Dynamic Dependency to Solver based on " + controlVar
					+ " but that variable cannot be VarScoped");
		}
		this.varResolver = Objects.requireNonNull(varResolver);
		this.controlVar = Objects.requireNonNull(controlVar);
		this.sourceScopeName = Objects.requireNonNull(sourceScopeName);
	}

	/**
	 * Returns a list of VariableIDs of the sources of the dynamic dependency, for the
	 * given source object.
	 * 
	 * @param sourceObject
	 *            The source VarScoped for the variables
	 * @return A list of the VariableIDs of the source of the dynamic dependency, for the
	 *         given source object
	 */
	public List<VariableID<?>> generateSources(VarScoped sourceObject)
	{
		return sourceVarNames.stream()
			.map(varName -> generateSource(sourceObject, varName))
			.collect(Collectors.toList());
	}

	/**
	 * Returns a source VariableID for a specific source variable name and other parameters.
	 * 
	 * @param sourceObject
	 *            The source VarScoped for the variable
	 * @param sourceVarName
	 *            The source variable name
	 * @return A source VariableID for a specific source variable name based on the other
	 *         given parameters
	 */
	public VariableID<?> generateSource(VarScoped sourceObject, String sourceVarName)
	{
		return varResolver.resolve(sourceScopeName, sourceObject, sourceVarName);
	}

	/**
	 * Adds a source variable name to this DynamicDependency.
	 * 
	 * @param varName The source variable name to be added to this DynamicDependency
	 */
	@Override
	public void addVariable(DependencyManager depManager, String varName)
	{
		sourceVarNames.add(varName);
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
