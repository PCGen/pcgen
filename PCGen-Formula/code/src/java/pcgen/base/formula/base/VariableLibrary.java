/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.FormatManager;

/**
 * VariableLibrary performs the management of legal variable names within a
 * LegalScope. This ensures that when a VariableID is built, it is in an
 * appropriate structure to be evaluated.
 */
public class VariableLibrary
{

	/**
	 * The LegalScopeLibrary that supports to be used to determine "child"
	 * scopes from any LegalScope (in order to avoid variable name conflicts
	 * between different but non disjoint scopes).
	 */
	private final LegalScopeLibrary library;

	/**
	 * Constructs a new VariableLibrary, which uses the given LegalScopeLibrary
	 * to ensure variables are legal within a given scope.
	 * 
	 * @param vsLibrary
	 *            The LegalScopeLibrary used to to ensure variables are legal
	 *            within a given scope
	 * @throws IllegalArgumentException
	 *             if the given library is null
	 */
	public VariableLibrary(LegalScopeLibrary vsLibrary)
	{
		if (vsLibrary == null)
		{
			throw new IllegalArgumentException(
				"LegalScopeLibrary cannot be null");
		}
		library = vsLibrary;
	}

	/**
	 * Holds a map from variable names and LegalScope objects to the format for
	 * that variable.
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	private DoubleKeyMap<String, LegalScope, FormatManager<?>> variableDefs =
			new DoubleKeyMap<>(CaseInsensitiveMap.class, HashMap.class);

	/**
	 * Asserts the given variable name is valid within the given LegalScope. It
	 * will be managed by the given FormatManager.
	 * 
	 * If no previous definition for the given variable name was encountered,
	 * then the assertion automatically passes, and the given LegalScope and
	 * FormatManager are stored as the definition for the given variable name.
	 * 
	 * If a previous FormatManager exists for the given variable name, then this
	 * will return true if and only if the given LegalScope is equal to the
	 * already stored LegalScope.
	 * 
	 * @param varName
	 *            The variable name for which the given FormatManager and
	 *            LegalScope is being asserted as valid
	 * @param legalScope
	 *            The asserted LegalScope for the given variable name
	 * @param formatManager
	 *            The FormatManager for the given variable
	 * 
	 * @return true if the assertion of this being a valid LegalScope for the
	 *         given variable FormatManager and name passes; false otherwise
	 * @throws IllegalArgumentException
	 *             if any argument is null of if the variable name is otherwise
	 *             illegal (is empty or starts/ends with whitespace)
	 */
	public boolean assertLegalVariableID(String varName, LegalScope legalScope,
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
		checkLegalVarName(varName);
		if (!variableDefs.containsKey(varName))
		{
			//Can't be a conflict
			addLegalVariable(varName, legalScope, formatManager);
			return true;
		}
		FormatManager<?> currentFormat = variableDefs.get(varName, legalScope);
		if (currentFormat != null)
		{
			//Asserted Format Already there
			return formatManager.equals(currentFormat);
		}
		//Now, need to check for conflicts
		LegalScope parent = legalScope.getParentScope();
		while (parent != null)
		{
			if (variableDefs.containsKey(varName, parent))
			{
				//Conflict with a higher level scope
				return false;
			}
			parent = parent.getParentScope();
		}
		boolean hasChildConflict =
				hasChildConflict(varName, legalScope, formatManager);
		if (!hasChildConflict)
		{
			addLegalVariable(varName, legalScope, formatManager);
		}
		return !hasChildConflict;
	}

	/**
	 * Adds a variable to this Library, including the necessary side effect of
	 * registering the LegalScope to ensure we know children as well as parent
	 * scopes.
	 */
	private void addLegalVariable(String varName, LegalScope legalScope,
		FormatManager<?> formatManager)
	{
		library.registerScope(legalScope);
		variableDefs.put(varName, legalScope, formatManager);
	}

	/**
	 * Returns true if there is a conflict the a child Scope for the given
	 * variable name.
	 */
	private boolean hasChildConflict(String varName, LegalScope legalScope,
		FormatManager<?> formatManager)
	{
		List<LegalScope> children = library.getChildScopes(legalScope);
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

	/**
	 * Returns true if the given LegalScope and variable name are a legal
	 * combination, knowing previous assertions of a FormatManager for the given
	 * LegalScope and variable name.
	 * 
	 * If no previous FormatManager was stored via assertLegalScope for the
	 * given LegalScope and variable name, then this will unconditionally return
	 * false.
	 * 
	 * If a FormatManager was stored via assertLegalScope for a LegalScope and
	 * variable name, then this will return true if the given LegalScope is
	 * compatible with the stored LegalScope.
	 * 
	 * @param legalScope
	 *            The LegalScope to be used to determine if the given
	 *            combination is legal
	 * @param varName
	 *            The variable name to be used to determine if the given
	 *            combination is legal
	 * 
	 * @return true if the given LegalScope and variable name are a legal
	 *         combination; false otherwise
	 * @throws IllegalArgumentException
	 *             if the given LegalScope is null
	 */
	public boolean isLegalVariableID(LegalScope legalScope, String varName)
	{
		if (legalScope == null)
		{
			throw new IllegalArgumentException("LegalScope cannot be null");
		}
		if (variableDefs.containsKey(varName, legalScope))
		{
			return true;
		}
		LegalScope parent = legalScope.getParentScope();
		return (parent != null) && isLegalVariableID(parent, varName);
	}

	/**
	 * Returns the FormatManager for the given LegalScope and variable name,
	 * knowing previous assertions of a FormatManager for the given LegalScope
	 * and variable name.
	 * 
	 * If no previous FormatManager was stored via assertLegalScope for the
	 * given LegalScope and variable name, then this will unconditionally return
	 * null.
	 * 
	 * @param legalScope
	 *            The LegalScope to be used to determine the FormatManager for
	 *            the given variable name
	 * @param varName
	 *            The variable name to be used to determine the FormatManager
	 * 
	 * @return The FormatManager for the given LegalScope and variable name
	 * @throws IllegalArgumentException
	 *             if the given LegalScope is null
	 */
	public FormatManager<?> getVariableFormat(LegalScope legalScope,
		String varName)
	{
		if (legalScope == null)
		{
			throw new IllegalArgumentException("LegalScope cannot be null");
		}
		FormatManager<?> format = variableDefs.get(varName, legalScope);
		if (format == null)
		{
			LegalScope parent = legalScope.getParentScope();
			if (parent != null)
			{
				return getVariableFormat(parent, varName);
			}
		}
		return format;
	}

	/**
	 * Returns a non-null set of known LegalScope objects for the given
	 * FormatManager and variable name.
	 * 
	 * This is typically used for debugging (e.g. to list potential conflicts)
	 * 
	 * Ownership of the returned set is transferred to the calling object and no
	 * reference to it is maintained by VariableLibrary. Changing the returned
	 * set will not alter the VariableLibrary.
	 * 
	 * @param varName
	 *            The Variable name for which the relevant LegalScope objects
	 *            should be returned
	 * 
	 * @return The Set of LegalScope objects asserted for the given variable
	 *         name
	 * @throws IllegalArgumentException
	 *             if the FormatManager is null or if the given variable name is
	 *             null, empty, or has leading/trailing whitespace
	 */
	public Set<LegalScope> getKnownLegalScopes(String varName)
	{
		checkLegalVarName(varName);
		return variableDefs.getSecondaryKeySet(varName);
	}

	/**
	 * Returns a VariableID for the given ScopeInstance and variable name, if
	 * legal.
	 * 
	 * The rules for legality are defined in the isLegalVariableID method
	 * description.
	 * 
	 * If isLegalVariableID returns false, then this method will throw an
	 * exception. isLegalVariableID should be called first to determine if
	 * calling this method is safe.
	 * 
	 * @param scopeInst
	 *            The ScopeInstance used to determine if the ScopeInstance and
	 *            name are a legal combination
	 * @param varName
	 *            The variable name used to determine if the ScopeInstance and
	 *            name are a legal combination
	 * @return A VariableID of the given ScopeInstance and variable name if they
	 *         are are a legal combination
	 * @throws IllegalArgumentException
	 *             if the given ScopeInstance is null, the name is invalid, or
	 *             if the ScopeInstance and variable name are not a legal
	 *             combination
	 */
	public VariableID<?> getVariableID(ScopeInstance scopeInst, String varName)
	{
		return getVarIDMessaged(scopeInst, varName, scopeInst);
	}

	/**
	 * Returns a VariableID for the given name that is valid in the given
	 * ScopeInstance (or any parent ScopeInstance - recursively).
	 */
	private VariableID<?> getVarIDMessaged(ScopeInstance scopeInst,
		String varName, ScopeInstance messageScope)
	{
		if (scopeInst == null)
		{
			throw new IllegalArgumentException(
				"Cannot get VariableID " + varName + " for "
					+ messageScope.getLegalScope().getName() + " scope");
		}
		checkLegalVarName(varName);
		FormatManager<?> formatManager =
				variableDefs.get(varName, scopeInst.getLegalScope());
		if (formatManager != null)
		{
			return new VariableID<>(scopeInst, formatManager, varName);
		}
		return getVarIDMessaged(scopeInst.getParentScope(), varName,
			messageScope);
	}

	/**
	 * Ensure a name is not null, zero length, or whitespace padded.
	 */
	private void checkLegalVarName(String varName)
	{
		if (varName == null)
		{
			throw new IllegalArgumentException("Variable Name cannot be null");
		}
		if (varName.length() == 0)
		{
			throw new IllegalArgumentException("Variable Name cannot be empty");
		}
		String trimmed = varName.trim();
		if (!varName.equals(trimmed))
		{
			throw new IllegalArgumentException(
				"Variable Name cannot start/end with whitespace");
		}
	}
}
