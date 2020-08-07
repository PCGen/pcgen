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
	 * Returns the scope name for this VarScoped object.
	 * 
	 * @return The scope name for this VarScoped object
	 */
	public Optional<String> getScopeName();

	/**
	 * Returns the object that is the parent of this VarScoped object.
	 * 
	 * @return The object that is the parent of this VarScoped object
	 */
	public Optional<VarScoped> getVariableParent();

}
