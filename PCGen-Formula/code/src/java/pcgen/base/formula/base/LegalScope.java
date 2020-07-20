/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
import java.util.List;
import java.util.Optional;

/**
 * LegalScope identifies a scope in which a particular part of a formula
 * (usually a variable) is valid.
 * 
 * This effectively provides a concept similar to a local variable scope in many
 * programming languages.
 * 
 * LegalScope provides the ability to not only distinguish the different scopes,
 * but also to define the relationship between the scopes (each scope can
 * identify its parent scope - if you need to know child scopes, see
 * LegalScopeManager).
 * 
 * It is perhaps important to recognize that a LegalScope is simply an
 * identifier. This means it does not contain information about what the scope
 * contains (it does not, for example, have a list of legal variables for the
 * scope).
 * 
 * LegalScope also just a template of sorts for actual ScopeInstance objects.
 * You cannot actually instantiate a VariableID in a LegalScope. (That would be
 * like asking to instantiate a non-static variable on just a class [not an
 * instance of that class] in Java). You need to instantiate the LegalScope (see
 * ScopeInstanceFactory) and in that ScopeInstance, the variables can be
 * processed (this is like creating a new instance of a class in Java, where the
 * local variables and methods can then be processed).
 * 
 * In general a VariableID HAS-A ScopeInstance, not a LegalScope or
 * ScopeInstance HAS-A list of VariableIDs. (The exact reasoning for this
 * relates to how VariableIDs are constructed, as well as the fact that they can
 * be "destroyed" by losing a reference to the VariableID, in which case we do
 * not want the contractual requirement on a developer to have to call back to
 * the scope in order to clean up a collection).
 */
public interface LegalScope
{
	/**
	 * Returns the LegalScope that serves as a "parent" for this LegalScope.
	 * 
	 * Null is a legal return value for a "master" scope.
	 * 
	 * @return The LegalScope that serves as a "parent" for this LegalScope
	 */
	public Optional<? extends LegalScope> getParentScope();

	/**
	 * Returns the name of this LegalScope.
	 * 
	 * @return the name of the LegalScope
	 */
	public String getName();

	/**
	 * Returns the full name (including parent names) for the given LegalScope.
	 * 
	 * @param legalScope
	 *            The LegalScope for which the full name should be returned
	 * @return The full name (including parent names) for the given LegalScope
	 */
	public static String getFullName(LegalScope legalScope)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(legalScope.getName());
		Optional<? extends LegalScope> current = legalScope.getParentScope();
		while (current.isPresent())
		{
			sb.insert(0, '.');
			sb.insert(0, current.get().getName());
			current = current.get().getParentScope();
		}
		return sb.toString();
	}

	public static List<String> getHierarchy(LegalScope legalScope)
	{
		List<String> list = new ArrayList<>(6);
		list.add(legalScope.getName());
		Optional<? extends LegalScope> current = legalScope.getParentScope();
		while (current.isPresent())
		{
			list.add(0, current.get().getName());
			current = current.get().getParentScope();
		}
		return list;
	}

}
