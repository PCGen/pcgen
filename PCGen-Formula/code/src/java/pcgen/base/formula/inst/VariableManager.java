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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.exception.LegalVariableException;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.FormatManager;

/**
 * VariableManager performs the management of legal variable names within a LegalScope.
 * This ensures that when a VariableID is built, it is in an appropriate structure to be
 * evaluated.
 */
public class VariableManager implements VariableLibrary
{

	/**
	 * The LegalScopeManager that supports to be used to determine "child" scopes from any
	 * LegalScope (in order to avoid variable name conflicts between different but non
	 * disjoint scopes).
	 */
	private final LegalScopeManager legalScopeManager;

	/**
	 * Constructs a new VariableManager, which uses the given LegalScopeManager to ensure
	 * variables are legal within a given scope.
	 * 
	 * @param legalScopeManager
	 *            The LegalScopeManager used to to ensure variables are legal within a
	 *            given scope
	 */
	public VariableManager(LegalScopeManager legalScopeManager)
	{
		this.legalScopeManager = Objects.requireNonNull(legalScopeManager);
	}

	/**
	 * Holds a map from variable names and LegalScope objects to the format for that
	 * variable.
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	private DoubleKeyMap<String, LegalScope, FormatManager<?>> variableDefs =
			new DoubleKeyMap<>(CaseInsensitiveMap.class, HashMap.class);

	@Override
	public void assertLegalVariableID(String varName, LegalScope legalScope,
		FormatManager<?> formatManager)
	{
		if (formatManager == null)
		{
			throw new IllegalArgumentException("FormatManager cannot be null");
		}
		if (legalScope == null)
		{
			throw new IllegalArgumentException("LegalScope cannot be null");
		}
		if (!legalScopeManager.recognizesScope(legalScope))
		{
			throw new IllegalArgumentException("LegalScope " + legalScope.getName()
				+ " was not registered with LegalScopeManager");
		}
		VariableID.checkLegalVarName(varName);
		if (!variableDefs.containsKey(varName))
		{
			//Can't be a conflict
			variableDefs.put(varName, legalScope, formatManager);
		}
		FormatManager<?> currentFormat = variableDefs.get(varName, legalScope);
		if (currentFormat != null)
		{
			//Asserted Format Already there
			if (!formatManager.equals(currentFormat))
			{
				throw new LegalVariableException(varName + " was asserted in scope: "
					+ LegalScope.getFullName(legalScope) + " with format "
					+ formatManager.getIdentifierType()
					+ " but was previously asserted as a "
					+ currentFormat.getIdentifierType());
			}
		}
		//Now, need to check for conflicts
		boolean hasConflict = hasParentConflict(varName, legalScope)
			|| hasChildConflict(varName, legalScope, formatManager);
		if (hasConflict)
		{
			throw new LegalVariableException(variableDefs.getSecondaryKeySet(varName)
				.stream().map(ls -> LegalScope.getFullName(ls))
				.collect(Collectors.joining(", ",
					"A Variable was asserted in incompatible variable scopes: " + varName
						+ " was requested in " + LegalScope.getFullName(legalScope)
						+ " but was previously in ",
					"")));
		}
		else
		{
			variableDefs.put(varName, legalScope, formatManager);
		}
	}

	/**
	 * Returns true if there is a conflict the a parent Scope for the given variable name.
	 */
	private boolean hasParentConflict(String varName, LegalScope legalScope)
	{
		LegalScope parent = legalScope.getParentScope();
		while (parent != null)
		{
			if (variableDefs.containsKey(varName, parent))
			{
				//Conflict with a higher level scope
				return true;
			}
			parent = parent.getParentScope();
		}
		return false;
	}

	/**
	 * Returns true if there is a conflict the a child Scope for the given variable name.
	 */
	private boolean hasChildConflict(String varName, LegalScope legalScope,
		FormatManager<?> formatManager)
	{
		List<? extends LegalScope> children =
				legalScopeManager.getChildScopes(legalScope);
		if (children == null)
		{
			return false;
		}
		for (LegalScope childScope : children)
		{
			if (variableDefs.containsKey(varName, childScope)
				|| hasChildConflict(varName, childScope, formatManager))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isLegalVariableID(LegalScope legalScope, String varName)
	{
		if (variableDefs.containsKey(varName, Objects.requireNonNull(legalScope)))
		{
			return true;
		}
		//Recursively check parent
		LegalScope parent = legalScope.getParentScope();
		return (parent != null) && isLegalVariableID(parent, varName);
	}

	@Override
	public FormatManager<?> getVariableFormat(LegalScope legalScope, String varName)
	{
		FormatManager<?> format =
				variableDefs.get(varName, Objects.requireNonNull(legalScope));
		if (format == null)
		{
			LegalScope parent = legalScope.getParentScope();
			//Recursively check parent, if possible
			if (parent != null)
			{
				return getVariableFormat(parent, varName);
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
		if (scopeInst == null)
		{
			throw new IllegalArgumentException("Cannot get VariableID " + varName
				+ " for " + messageScope.getLegalScope().getName() + " scope");
		}
		VariableID.checkLegalVarName(varName);
		FormatManager<?> formatManager =
				variableDefs.get(varName, scopeInst.getLegalScope());
		if (formatManager != null)
		{
			return new VariableID<>(scopeInst, formatManager, varName);
		}
		//Recursively check parent scope
		return getVarIDMessaged(scopeInst.getParentScope(), varName, messageScope);
	}
}
