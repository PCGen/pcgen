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

/**
 * A ScopeInstanceFactory is a factory used to instantiate ScopeInstance objects given a
 * parent Scope name and the VarScoped for which the ScopeInstance is being instantiated.
 */
public interface ScopeInstanceFactory
{
	/**
	 * Returns the ScopeInstance within the ImplementedScope of the given name, and
	 * considering the given VarScoped object. If the ImplementedScope is not the scope
	 * for the given VarScoped object, the VarScoped object will provide the appropriate
	 * VarScoped it draws upon for the ImplementedScope. A new ScopeInstance will be
	 * created if one does not already exist.
	 * 
	 * @param scopeName
	 *            The name of the ImplementedScope for which the ScopeInstance should be
	 *            returned
	 * @param varScoped
	 *            The Object where analysis should start in order to determine the
	 *            appropriate ScopeInstance to be returned.
	 * @return The ScopeInstance within the ImplementedScope of the given name and
	 *         considering the given VarScoped object
	 * @throws IllegalArgumentException
	 *             if the given ImplementedScope is not a scope for the given VarScoped
	 *             object or an ancestor of the VarScoped object (as determined by
	 *             getProviderFor())
	 */
	public ScopeInstance get(String scopeName, VarScoped varScoped);
}
