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

import java.util.Optional;

/**
 * A ScopeInstanceFactory is a factory used to instantiate ScopeInstance objects given a
 * parent ScopeInstance and LegalScope in which to instantiate the ScopeInstance.
 */
public interface ScopeInstanceFactory
{
	/**
	 * Returns the "global" ScopeInstance object for the given LegalScope.
	 * 
	 * @param scopeName
	 *            The name of the LegalScope for which the "global"
	 *            ScopeInstance should be returned.
	 * @return The "global" ScopeInstance object for the given LegalScope
	 */
	public ScopeInstance getGlobalInstance(String scopeName);

	/**
	 * Returns the ScopeInstance within the given LegalScope and considering the given
	 * VarScoped object. If the LegalScope is not the scope for the given VarScoped
	 * object, then ancestors of the VarScoped object will be checked until one matches
	 * the given LegalScope. A new ScopeInstance will be created if one does not already
	 * exist.
	 * 
	 * @param scopeName
	 *            The name of the LegalScope for which the ScopeInstance should be
	 *            returned
	 * @param obj
	 *            The (Optional) Object where analysis should start in order to determine
	 *            the appropriate ScopeInstance to be returned.
	 * @return The ScopeInstance within the given LegalScope and considering the given
	 *         VarScoped object
	 * @throws IllegalArgumentException
	 *             if the given LegalScope is not a scope for the given VarScoped object
	 *             or an ancestor of the VarScoped object (as determined by
	 *             getVariableParent())
	 */
	public ScopeInstance get(String scopeName, Optional<VarScoped> obj);

	/**
	 * Returns the LegalScope for the given legal scope name, using the LegalScopeManager
	 * underlying this SimpleScopeInstanceFactory to resolve the name.
	 * 
	 * @param s
	 *            The scope name to be used to find the LegalScope in the
	 *            LegalScopeManager underlying this SimpleScopeInstanceFactory
	 * @return The LegalScope for the given legal scope name
	 */
	public LegalScope getScope(String s);
}
