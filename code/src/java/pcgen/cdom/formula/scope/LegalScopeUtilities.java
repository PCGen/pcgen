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
package pcgen.cdom.formula.scope;

import pcgen.base.formula.inst.ImplementedScopeLibrary;
import pcgen.cdom.helper.SpringHelper;

public final class LegalScopeUtilities
{

	private LegalScopeUtilities()
	{
		//Do not construct utility class
	}

	public static void loadLegalScopeLibrary(ImplementedScopeLibrary library)
	{
		library.addScope(SpringHelper.getBean(GlobalPCScope.class));
		library.addScope(SpringHelper.getBean(EquipmentScope.class));
		library.addScope(SpringHelper.getBean(EquipmentPartScope.class));
		library.addScope(SpringHelper.getBean(RaceScope.class));
		library.addScope(SpringHelper.getBean(SaveScope.class));
		library.addScope(SpringHelper.getBean(SizeScope.class));
		library.addScope(SpringHelper.getBean(SkillScope.class));
		library.addScope(SpringHelper.getBean(StatScope.class));
	}
}
