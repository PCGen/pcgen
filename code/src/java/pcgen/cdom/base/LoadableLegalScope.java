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

import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.VarScoped;

/**
 * A LoadableLegalScope is an interface to be used by plugins that can identify
 * where variables can be used. This adds to LegalScope the information
 * necessary for them to be plugins for PCGen.
 * 
 * @param <T>
 *            The Class of object that will contain variables for this
 *            LoadableLegalScope
 */
public interface LoadableLegalScope<T extends CDOMObject & VarScoped> extends
		LegalScope
{
	/**
	 * Returns the Class of object that will contain variables for this
	 * LoadableLegalScope.
	 * 
	 * @return the Class of object that will contain variables for this
	 *         LoadableLegalScope
	 */
	public Class<T> getLocalClass();
}
