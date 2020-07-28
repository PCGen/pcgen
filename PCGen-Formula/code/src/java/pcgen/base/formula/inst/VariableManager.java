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
package pcgen.base.formula.inst;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.exception.LegalVariableException;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.FormatManager;
import pcgen.base.util.ValueStore;

/**
 * VariableManager performs the management of legal variable names within an ImplementedScope.
 * This ensures that when a VariableID is built, it is in an appropriate structure to be
 * evaluated.
 */
public class VariableManager implements VariableLibrary
{

	/**
	 * The ScopeManager that supports to be used to determine "child" scopes from any
	 * ImplementedScope (in order to avoid variable name conflicts between different but
	 * non disjoint scopes).
	 */
	private final ScopeManager scopeManager;

	/**
	 * The ValueStore that knows what defaults exist (and thus what variables would be
	 * legal).
	 */
	private final ValueStore defaultStore;

	/**
	 * Holds a map from variable names and ImplementedScope objects to the format for that
	 * variable.
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	private final DoubleKeyMap<Object, ImplementedScope, FormatManager<?>> variableDefs =
			new DoubleKeyMap<>(CaseInsensitiveMap.class, HashMap.class);

	/**
	 * Constructs a new VariableManager, which uses the given ScopeManager to ensure
	 * variables are legal within a given scope.
	 * 
	 * @param scopeManager
	 *            The ScopeManager used to to ensure variables are legal within a
	 *            given scope
	 * @param defaultStore
	 *            The ValueStore used to get the default variable for a variable Format
	 */
	public VariableManager(ScopeManager scopeManager, ValueStore defaultStore)
	{
		this.scopeManager = Objects.requireNonNull(scopeManager);
		this.defaultStore = Objects.requireNonNull(defaultStore);
	}

	@Override
	public void assertLegalVariableID(String varName, ImplementedScope scope,
		FormatManager<?> formatManager)
	{
		Objects.requireNonNull(varName);
		Objects.requireNonNull(scope);
		Objects.requireNonNull(formatManager);
		if (!scopeManager.recognizesScope(scope))
		{
			throw new IllegalArgumentException("ImplementedScope " + scope.getName()
				+ " was not registered with ScopeManager");
		}
		VariableID.checkLegalVarName(varName);
		if (!variableDefs.containsKey(varName))
		{
			//Can't be a conflict
			variableDefs.put(varName, scope, formatManager);
			return;
		}
		FormatManager<?> currentFormat = variableDefs.get(varName, scope);
		//Asserted Format Already there
		if ((currentFormat != null) && !formatManager.equals(currentFormat))
		{
			throw new LegalVariableException(varName + " was asserted in scope: "
				+ ImplementedScope.getFullName(scope) + " with format "
				+ formatManager.getIdentifierType() + " but was previously asserted as a "
				+ currentFormat.getIdentifierType());
		}
		//Now, need to check for conflicts
		if (hasConflict(varName, scope))
		{
			throw new LegalVariableException(variableDefs.getSecondaryKeySet(varName)
				.stream().map(ls -> ImplementedScope.getFullName(ls))
				.collect(Collectors.joining(", ",
					"A Variable was asserted in incompatible variable scopes: " + varName
						+ " was requested in " + ImplementedScope.getFullName(scope)
						+ " but was previously in ",
					"")));
		}
		else
		{
			variableDefs.put(varName, scope, formatManager);
		}
	}

	/**
	 * Returns true if there is a conflict with a related Scope for the given variable name.
	 */
	private boolean hasConflict(String varName, ImplementedScope scope)
	{
		return variableDefs.getSecondaryKeySet(varName).stream()
			.filter(otherScope -> scopeManager.isRelated(otherScope, scope))
			.anyMatch(otherScope -> !otherScope.equals(scope));
	}

	@Override
	public boolean isLegalVariableID(ImplementedScope scope, String varName)
	{
		Objects.requireNonNull(scope);
		if (variableDefs.containsKey(varName, scope))
		{
			return true;
		}
		//Recursively check parent
		Optional<? extends ImplementedScope> potentialParent = scope.getParentScope();
		return potentialParent.isPresent()
			&& isLegalVariableID(potentialParent.get(), varName);
	}

	@Override
	public FormatManager<?> getVariableFormat(ImplementedScope scope, String varName)
	{
		Objects.requireNonNull(scope);
		FormatManager<?> format = variableDefs.get(varName, scope);
		if (format == null)
		{
			Optional<? extends ImplementedScope> potentialParent = scope.getParentScope();
			//Recursively check parent, if possible
			if (potentialParent.isPresent())
			{
				return getVariableFormat(potentialParent.get(), varName);
			}
		}
		return format;
	}

	@Override
	public VariableID<?> getVariableID(ScopeInstance scopeInst, String varName)
	{
		return getVarIDMessaged(scopeInst, varName, scopeInst);
	}

	/**
	 * Returns a VariableID for the given name that is valid in the given ScopeInstance
	 * (or any parent ScopeInstance - recursively).
	 */
	private VariableID<?> getVarIDMessaged(ScopeInstance scopeInst, String varName,
		ScopeInstance messageScope)
	{
		Objects.requireNonNull(scopeInst);
		VariableID.checkLegalVarName(varName);
		FormatManager<?> formatManager =
				variableDefs.get(varName, scopeInst.getImplementedScope());
		if (formatManager != null)
		{
			return new VariableID<>(scopeInst, formatManager, varName);
		}
		//Recursively check parent scope
		return getVarIDMessaged(scopeInst.getParentScope().get(), varName, messageScope);
	}

	@Override
	public List<FormatManager<?>> getInvalidFormats()
	{
		Set<FormatManager<?>> formats = new HashSet<FormatManager<?>>();
		variableDefs.getKeySet().stream()
			.map(Object::toString)
			.map(variableDefs::values)
			.forEach(formats::addAll);
		return formats.stream()
			.filter(formatManager -> Objects
				.isNull(formatManager.initializeFrom(defaultStore)))
			.collect(Collectors.toList());
	}
}
