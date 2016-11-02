/*
 * Copyright (c) 2016 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.inst;

import pcgen.base.formula.base.LegalScope;
import pcgen.cdom.formula.scope.GlobalScope;
import pcgen.cdom.helper.SpringHelper;

public final class DynamicCategory extends AbstractCategory<Dynamic> implements
		LegalScope
{
	@Override
	public Class<Dynamic> getReferenceClass()
	{
		return Dynamic.class;
	}

	@Override
	public String getReferenceDescription()
	{
		return "Dynamic Category " + getKeyName();
	}

	@Override
	public String getName()
	{
		return getDisplayName();
	}

	@Override
	public LegalScope getParentScope()
	{
		return SpringHelper.getBean(GlobalScope.class);
	}

	@Override
	public Dynamic newInstance()
	{
		return new Dynamic();
	}
}
