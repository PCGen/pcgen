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
 * A VarScoped object is an object that is designed to be an "owning object" in
 * a variable processing hierarchy (supporting local variables).
 */
public interface VarScoped
{

	/**
	 * Returns the name of this VarScoped object. Intended mainly for user
	 * interpretation (uniqueness is valuable).
	 * 
	 * @return The name of this VarScoped object
	 */
	public String getKeyName();

	/**
	 * Returns the object drawn from by this VarScoped for the given ImplementedScope.
	 * May return this if the ImplementedScope matches the scope of this object.
	 * 
	 * @param implScope
	 *            The ImplementedScope for which the relevant VarScoped object should be
	 *            returned
	 * @return The object drawn from by this ScopeInstance for the given ImplementedScope
	 */
	public VarScoped getProviderFor(ImplementedScope implScope);

}
