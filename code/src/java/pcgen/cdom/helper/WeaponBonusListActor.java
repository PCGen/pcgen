/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;

public class WeaponBonusListActor implements
		PersistentChoiceActor<WeaponProf>
{

	@Override
	public boolean allow(WeaponProf choice, PlayerCharacter pc,
			boolean allowStack)
	{
		return true;
	}

	@Override
	public void applyChoice(CDOMObject owner, WeaponProf choice,
			PlayerCharacter pc)
	{
		pc.addWeaponBonus(owner, choice);
	}

	@Override
	public List<? extends WeaponProf> getCurrentlySelected(CDOMObject owner,
			PlayerCharacter pc)
	{
		return pc.getBonusWeaponProfs(owner);
	}

	@Override
	public WeaponProf decodeChoice(String s)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				WeaponProf.class, s);
	}

	@Override
	public String encodeChoice(WeaponProf choice)
	{
		return choice.getKeyName();
	}

	@Override
	public void removeChoice(PlayerCharacter pc, CDOMObject owner,
			WeaponProf choice)
	{
		pc.removeWeaponBonus(owner, choice);
	}

	@Override
	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
			WeaponProf choice)
	{
		pc.addWeaponBonus(owner, choice);
	}
}