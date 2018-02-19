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
 * A LegalScopeLibrary is a storage location for LegalScope used to track the
 * children of LegalScope objects.
 */
public interface LegalScopeLibrary
{
	/**
	 * Returns the LegalScope with the given name. If there was no LegalScope
	 * with the given name registered with this LegalScopeLibrary, then null
	 * will be returned.
	 * 
	 * @param name
	 *            The name of the LegalScope that should be returned
	 * @return The LegalScope with the given name
	 */
	public LegalScope getScope(String name);
}
