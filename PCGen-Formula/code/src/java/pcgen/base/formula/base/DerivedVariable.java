/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
import java.util.Objects;

import pcgen.base.util.FormatManager;

/**
 * A VariableID is a combination of a variable name and scope that is used to uniquely
 * identify a variable. This class is effectively a composite (design-pattern) object
 * designed to keep the two "primary keys" necessary to uniquely identify a variable in
 * one location.
 * 
 * The variable name is considered case-insensitive. The scope is a ScopeInstance.
 * 
 * Note that a user should not directly construct a VariableID. Since it is intended that
 * only VariableIDs in a specific scope is legal, a system should depend on
 * pcgen.base.formula.manager.VariableLibrary to provide construction services for
 * VariableID objects. Exact details of VariableID uniqueness are provided in
 * VariableIDFactory.
 * 
 * @param <T>
 *            The format of object identified by this VariableID
 */
public class DerivedVariable<T>
{

	/**
	 * The FormatManager for this VariableID.
	 */
	private final FormatManager<T> formatManager;

	/**
	 * The ScopeInstance for this VariableID.
	 */
	private final ScopeInstance scope;

	/**
	 * The source variables for the internal VariableID.
	 */
	private final List<VariableID<?>> sourceVars = new ArrayList<>();

	/*
	 * We choose not to do any enforcement of variable names beyond what is already
	 * enforced (non-null, non-empty, no leading/trailing whitespace). (In other words, we
	 * are doing a simple set of enforcement, NOT the same enforcement as done by the
	 * formula parser). This is consciously done to allow "virtual VariableIDs" that may
	 * not otherwise be within a formula, but which are necessary in a larger system.
	 */

	/**
	 * Constructs a new VariableID with the given ScopeInstance, FormatManager and name.
	 * 
	 * Note: While this is public, it is highly advised to use a VariableIDFactory to
	 * construct new instances of VariableID.
	 * 
	 * @param scopeInst
	 *            The ScopeInstance of the variable represented by this VariableID
	 * @param formatManager
	 *            The FormatManager of the variable represented by this VariableID
	 * @throws IllegalArgumentException
	 *             if the name is empty or starts/ends with whitespace
	 */
	public DerivedVariable(ScopeInstance scopeInst, FormatManager<T> formatManager)
	{
		this.formatManager = Objects.requireNonNull(formatManager);
		this.scope = Objects.requireNonNull(scopeInst);
	}

	/**
	 * Returns the ScopeInstance of the variable represented by this VariableID.
	 * Guaranteed to be non-null.
	 * 
	 * @return The ScopeInstance of the variable represented by this VariableID
	 */
	public ScopeInstance getScope()
	{
		return scope;
	}

	/**
	 * Returns the name of the variable represented by this VariableID. Guaranteed to be
	 * non-null, non-empty.
	 * 
	 * @return The name of the variable represented by this VariableID
	 */
	public String getName()
	{
		return "Derived Variable";
	}

	/**
	 * Returns the FormatManager of this VariableID.
	 * 
	 * @return The FormatManager of this VariableID
	 */
	public FormatManager<T> getFormatManager()
	{
		return formatManager;
	}

	/**
	 * Returns the format (e.g. Number.class) of this VariableID (as controlled by the
	 * FormatManager).
	 * 
	 * @return The format (e.g. Number.class) of this VariableID
	 */
	public Class<T> getVariableFormat()
	{
		return formatManager.getManagedClass();
	}

	public void addInputVar(VariableID<?> varID)
	{
		sourceVars.add(Objects.requireNonNull(varID));
	}

	public List<VariableID<?>> getInputVars()
	{
		return Collections.unmodifiableList(sourceVars);
	}
}
