/*
 * 
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package actor.choose;

import pcgen.cdom.base.Chooser;
import pcgen.core.WeaponProf;
import plugin.lsttokens.choose.WeaponProficiencyToken;

import actor.testsupport.AbstractPersistentCDOMChoiceActorTestCase;

public class WeaponProficiencyTokenTest extends
		AbstractPersistentCDOMChoiceActorTestCase<WeaponProf>
{

	private static final WeaponProficiencyToken PCA = new WeaponProficiencyToken();

	@Override
	public Chooser<WeaponProf> getActor()
	{
		return PCA;
	}

	@Override
	public Class<WeaponProf> getCDOMClass()
	{
		return WeaponProf.class;
	}
}
