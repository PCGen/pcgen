/*
 * Copyright 2015 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.formula.scope.PCGenScope;


/**
 * An ObjectGrouping is a simplified collection of objects, capable of simply
 * identifying the class of object it contains and whether it contains a given
 * object
 * 
 */
public interface ObjectGrouping
{
	/**
	 * Returns true if this ObjectGrouping contains the given item.
	 * 
	 * @param item
	 *            The item to be checked if this ObjectGrouping contains the
	 *            item
	 * @return true if this ObjectGrouping contains the given item; false
	 *         otherwise
	 */
	public boolean contains(VarScoped item);

	/**
	 * Returns the Scope of objects contained by this ObjectGrouping.
	 * 
	 * @return the Scope of objects contained by this ObjectGrouping
	 */
	public PCGenScope getScope();

	/**
	 * Returns the Identifier of objects contained by this ObjectGrouping.
	 * 
	 * @return the Identifier of objects contained by this ObjectGrouping
	 */
	public String getIdentifier();

}
