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

import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.exception.LegalVariableException;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.FormatManager;
import pcgen.base.util.ValueStore;

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
	 * The ValueStore that knows what defaults exist (and thus what variables would be
	 * legal).
	 */
	private final ValueStore defaultStore;

	/**
	 * Holds a map from variable names and LegalScope objects to the format for that
	 * variable.
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	private final DoubleKeyMap<Object, LegalScope, FormatManager<?>> variableDefs =
			new DoubleKeyMap<>(CaseInsensitiveMap.class, HashMap.class);

	/**
	 * Constructs a new VariableManager, which uses the given LegalScopeManager to ensure
	 * variables are legal within a given scope.
	 * 
	 * @param legalScopeManager
	 *            The LegalScopeManager used to to ensure variables are legal within a
	 *            given scope
	 * @param defaultStore
	 *            The ValueStore used to get the default variable for a variable Format
	 */
	public VariableManager(LegalScopeManager legalScopeManager, ValueStore defaultStore)
	{
		this.legalScopeManager = Objects.requireNonNull(legalScopeManager);
		this.defaultStore = Objects.requireNonNull(defaultStore);
	}

	@Override
	public void assertLegalVariableID(String varName, LegalScope legalScope,
		FormatManager<?> formatManager)
	{
		Objects.requireNonNull(varName);
		Objects.requireNonNull(legalScope);
		Objects.requireNonNull(formatManager);
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
			return;
		}
		FormatManager<?> currentFormat = variableDefs.get(varName, legalScope);
		//Asserted Format Already there
		if ((currentFormat != null) && !formatManager.equals(currentFormat))
		{
			throw new LegalVariableException(varName + " was asserted in scope: "
				+ LegalScope.getFullName(legalScope) + " with format "
				+ formatManager.getIdentifierType() + " but was previously asserted as a "
				+ currentFormat.getIdentifierType());
		}
		//Now, need to check for conflicts
		if (hasConflict(varName, legalScope))
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
	 * Returns true if there is a conflict with a related Scope for the given variable name.
	 */
	private boolean hasConflict(String varName, LegalScope legalScope)
	{
		return variableDefs.getSecondaryKeySet(varName).stream()
			.filter(otherScope -> legalScopeManager.isRelated(otherScope, legalScope))
			.anyMatch(otherScope -> !otherScope.equals(legalScope));
	}

	@Override
	public boolean isLegalVariableID(LegalScope legalScope, String varName)
	{
		Objects.requireNonNull(legalScope);
		if (variableDefs.containsKey(varName, legalScope))
		{
			return true;
		}
		//Recursively check parent
		Optional<? extends LegalScope> potentialParent = legalScope.getParentScope();
		return potentialParent.isPresent()
			&& isLegalVariableID(potentialParent.get(), varName);
	}

	@Override
	public FormatManager<?> getVariableFormat(LegalScope legalScope, String varName)
	{
		Objects.requireNonNull(legalScope);
		FormatManager<?> format = variableDefs.get(varName, legalScope);
		if (format == null)
		{
			Optional<? extends LegalScope> potentialParent = legalScope.getParentScope();
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
				variableDefs.get(varName, scopeInst.getLegalScope());
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
