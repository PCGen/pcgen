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
import pcgen.base.formula.base.ImplementedScopeManager;
import pcgen.base.formula.base.RelationshipManager;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.ScopeInstanceFactory;
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
	 * The RelationshipManager that supports to be used to determine "child" scopes from
	 * any ImplementedScope (in order to avoid variable name conflicts between different
	 * but non disjoint scopes).
	 */
	private final RelationshipManager relationshipManager;

	/**
	 * The ImplementedScopeManager that supports to be used to determine legal
	 * ImplementedScope objects.
	 */
	private final ImplementedScopeManager scopeManager;

	
	private final ScopeInstanceFactory siFactory;

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
	 * Constructs a new VariableManager, which uses the RelationshipManager and
	 * ImplementedScopeManager parameters to ensure variables are legal within a given
	 * scope, and the ValueStore for default values of the variables.
	 * 
	 * @param relationshipManager
	 *            The RelationshipManager used to determine if two scopes are related
	 * @param scopeManager
	 *            The ImplementedScopeManager used to to ensure variables are legal within
	 *            a given scope
	 * @param siFactory
	 *            The ScopeInstanceFactory used by this VariableManager
	 * @param defaultStore
	 *            The ValueStore used to get the default variable for a variable Format
	 */
	public VariableManager(RelationshipManager relationshipManager,
		ImplementedScopeManager scopeManager, ScopeInstanceFactory siFactory,
		ValueStore defaultStore)
	{
		this.relationshipManager = Objects.requireNonNull(relationshipManager);
		this.scopeManager = Objects.requireNonNull(scopeManager);
		this.siFactory = Objects.requireNonNull(siFactory);
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
		if (variableDefs.containsKey(varName))
		{
			FormatManager<?> currentFormat =
					variableDefs.get(varName, scope);
			//Asserted Format Already there
			if ((currentFormat != null) && !formatManager.equals(currentFormat))
			{
				throw new LegalVariableException(
					varName + " was asserted in scope: "
						+ ImplementedScope.getFullName(scope)
						+ " with format " + formatManager.getIdentifierType()
						+ " but was previously asserted as a "
						+ currentFormat.getIdentifierType());
			}
			//Now, need to check for conflicts
			if (hasConflict(varName, scope))
			{
				throw new LegalVariableException(variableDefs
					.getSecondaryKeySet(varName).stream()
					.map(ls -> ImplementedScope.getFullName(ls))
					.collect(Collectors.joining(", ",
						"A Variable was asserted in incompatible variable scopes: "
							+ varName + " was requested in "
							+ ImplementedScope.getFullName(scope)
							+ " but was previously in ",
						"")));
			}
		}
		variableDefs.put(varName, scope, formatManager);
	}

	/**
	 * Returns true if there is a conflict with a related Scope for the given variable name.
	 */
	private boolean hasConflict(String varName, ImplementedScope scope)
	{
		return variableDefs.getSecondaryKeySet(varName).stream()
			.filter(otherScope -> relationshipManager.isRelated(otherScope, scope))
			.anyMatch(otherScope -> !otherScope.equals(scope));
	}

	@Override
	public boolean isLegalVariableID(ImplementedScope implScope,
		String varName)
	{
		return internalGetImplementedScope(implScope, varName).isPresent();
	}

	@Override
	public Optional<FormatManager<?>> getVariableFormat(ImplementedScope scope, String varName)
	{
		Objects.requireNonNull(scope);
		Optional<FormatManager<?>> format =
				Optional.ofNullable(variableDefs.get(varName, scope));
		if (format.isEmpty())
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
		Objects.requireNonNull(scopeInst);
		VariableID.checkLegalVarName(varName);
		ImplementedScope implScope =
				getScopeImpl(scopeInst.getImplementedScope(), varName);
		FormatManager<?> formatManager = variableDefs.get(varName, implScope);
		ScopeInstance activeInst = getActiveInst(implScope, scopeInst);
		return new VariableID<>(activeInst, formatManager, varName);
	}

	private ScopeInstance getActiveInst(ImplementedScope implScope,
		ScopeInstance scopeInst)
	{
		if (scopeInst.getImplementedScope().equals(implScope))
		{
			return scopeInst;
		}
		return siFactory.get(ImplementedScope.getFullName(implScope),
			Optional.of(scopeInst.getOwningObject()));
	}

	private ImplementedScope getScopeImpl(ImplementedScope implScope,
		String varName)
	{
		Optional<ImplementedScope> scope =
				internalGetImplementedScope(implScope, varName);
		return scope.orElseThrow(
			() -> new IllegalArgumentException("Unable to find Variable "
				+ varName + " when starting from Implemented Scope "
				+ ImplementedScope.getFullName(implScope)));
	}

	private Optional<ImplementedScope> internalGetImplementedScope(ImplementedScope implScope,
		String varName)
	{
		FormatManager<?> formatManager = variableDefs.get(varName, implScope);
		if (formatManager != null)
		{
			return Optional.of(implScope);
		}
		for (ImplementedScope from : implScope.drawsFrom())
		{
			if (variableDefs.containsKey(varName, from))
			{
				return Optional.of(from);
			}
		}
		return Optional.empty();
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

	@Override
	public <T> T getDefault(FormatManager<T> formatManager)
	{
		return formatManager.initializeFrom(defaultStore);
	}
}
