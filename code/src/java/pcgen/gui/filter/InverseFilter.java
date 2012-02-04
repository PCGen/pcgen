/*
 * InverseFilter.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on February 23, 2002, 9:00 PM
 */
package pcgen.gui.filter;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.system.LanguageBundle;

/**
 * <code>InverseFilter</code>
 *
 * @author Thomas Behr
 * @version $Revision$
 */
final class InverseFilter extends AbstractPObjectFilter implements CustomFilter
{
	private static final String in_not = LanguageBundle.getString("in_not");
	private PObjectFilter filter;

	InverseFilter(PObjectFilter filter)
	{
		super();
		this.filter = filter;

		setCategory(LanguageBundle.getString("in_custom"));
		setName(LanguageBundle.getString("in_not") + " " + this.filter.getName());
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		return !filter.accept(aPC, pObject);
	}

	@Override
	public String toString()
	{
		return in_not + " (" + this.filter.toString() + ')';
	}
}
