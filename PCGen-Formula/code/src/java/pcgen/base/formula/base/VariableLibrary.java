/*
 * Copyright 2014-20 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.List;

import pcgen.base.formula.exception.LegalVariableException;
import pcgen.base.util.FormatManager;

/**
 * VariableLibrary performs the management of legal variable names within a LegalScope.
 * This ensures that when a VariableID is built, it is in an appropriate structure to be
 * evaluated.
 * 
 * A VariableLibrary should ensure uniqueness of variable names when in a given
 * scope. @see LegalScopeManager to understand how scopes can be related.
 */
public interface VariableLibrary
{

	/**
	 * Asserts the given variable name is valid within the given LegalScope. It will be
	 * managed by the given FormatManager.
	 * 
	 * If no previous definition for the given variable name was encountered, then the
	 * assertion automatically passes, and the given LegalScope and FormatManager are
	 * stored as the definition for the given variable name.
	 * 
	 * If a previous FormatManager exists for the given variable name, then this will pass
	 * if and only if the given LegalScope can allow a duplicate variable name to any
	 * already stored LegalScope. In effect, if the scopes are at all related, then this
	 * will fail.
	 * 
	 * @param varName
	 *            The variable name for which the given FormatManager and LegalScope is
	 *            being asserted as valid
	 * @param legalScope
	 *            The asserted LegalScope for the given variable name
	 * @param formatManager
	 *            The FormatManager for the given variable
	 * 
	 * @throws IllegalArgumentException
	 *             if any argument is null of if the variable name is otherwise illegal
	 *             (is empty or starts/ends with whitespace)
	 * @throws LegalVariableException
	 *             if a variable of that name exists in a conflicting scope or in the same
	 *             scope with a different format
	 */
	public void assertLegalVariableID(String varName, LegalScope legalScope,
		FormatManager<?> formatManager);

	/**
	 * Returns true if the given LegalScope and variable name are a legal combination,
	 * knowing previous assertions of a FormatManager for the given LegalScope and
	 * variable name.
	 * 
	 * If no previous FormatManager was stored via assertLegalScope for the given
	 * LegalScope and variable name, then this will unconditionally return false.
	 * 
	 * If a FormatManager was stored via assertLegalScope for a LegalScope and variable
	 * name, then this will return true if the given LegalScope is compatible with the
	 * stored LegalScope.
	 * 
	 * @param legalScope
	 *            The LegalScope to be used to determine if the given combination is legal
	 * @param varName
	 *            The variable name to be used to determine if the given combination is
	 *            legal
	 * 
	 * @return true if the given LegalScope and variable name are a legal combination;
	 *         false otherwise
	 */
	public boolean isLegalVariableID(LegalScope legalScope, String varName);

	/**
	 * Returns the FormatManager for the given LegalScope and variable name, knowing
	 * previous assertions of a FormatManager for the given LegalScope and variable name.
	 * 
	 * If no previous FormatManager was stored via assertLegalScope for the given
	 * LegalScope and variable name, then this will unconditionally return null.
	 * 
	 * @param legalScope
	 *            The LegalScope to be used to determine the FormatManager for the given
	 *            variable name
	 * @param varName
	 *            The variable name to be used to determine the FormatManager
	 * 
	 * @return The FormatManager for the given LegalScope and variable name
	 */
	public FormatManager<?> getVariableFormat(LegalScope legalScope, String varName);

	/**
	 * Returns a VariableID for the given ScopeInstance and variable name, if legal.
	 * 
	 * The rules for legality are defined in the isLegalVariableID method description.
	 * 
	 * If isLegalVariableID returns false, then this method will throw an exception.
	 * isLegalVariableID should be called first to determine if calling this method is
	 * safe.
	 * 
	 * @param scopeInst
	 *            The ScopeInstance used to determine if the ScopeInstance and name are a
	 *            legal combination
	 * @param varName
	 *            The variable name used to determine if the ScopeInstance and name are a
	 *            legal combination
	 * @return A VariableID of the given ScopeInstance and variable name if they are are a
	 *         legal combination
	 * @throws IllegalArgumentException
	 *             if the name is invalid, or if the ScopeInstance and variable name are
	 *             not a legal combination
	 */
	public VariableID<?> getVariableID(ScopeInstance scopeInst, String varName);
	
	/**
	 * Returns a List of FormatManager objects indicating Formats for which a legal
	 * VariableID was asserted, but for which the VariableLibrary cannot construct a
	 * default value. Returns an empty list if all asserted Variables have a valid default
	 * value.
	 * 
	 * @return A List of FormatManager objects indicating Formats for which a legal
	 *         VariableID was asserted, but for which the VariableLibrary cannot construct
	 *         a default value
	 */
	public List<FormatManager<?>> getInvalidFormats();
}
