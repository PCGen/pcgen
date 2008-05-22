/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.rules.persistence.TokenUtilities;

public class LevelCommandFactory extends ConcretePrereqObject implements
		Comparable<LevelCommandFactory>, LSTWriteable
{

	private final CDOMSingleRef<PCClass> pcClass;

	private final Formula levels;

	public LevelCommandFactory(CDOMSingleRef<PCClass> cl, Formula lvls)
	{
		pcClass = cl;
		levels = lvls;
	}

	public Formula getLevelCount()
	{
		return levels;
	}

	public PCClass getPCClass()
	{
		return pcClass.resolvesTo();
	}

	public String getLSTformat()
	{
		return pcClass.getLSTformat();
	}

	@Override
	public int hashCode()
	{
		return pcClass.hashCode() * 29 + levels.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof LevelCommandFactory))
		{
			return false;
		}
		LevelCommandFactory lcf = (LevelCommandFactory) o;
		return levels.equals(lcf.levels) && pcClass.equals(lcf.pcClass);
	}

	public int compareTo(LevelCommandFactory arg0)
	{
		int i = TokenUtilities.REFERENCE_SORTER.compare(pcClass, arg0.pcClass);
		if (i == 0)
		{
			if (levels.equals(arg0.levels))
			{
				return 0;
			}
			if (levels == arg0.levels)
			{
				return 0;
			}
			return levels.toString().compareTo(arg0.levels.toString());
		}
		return i;
	}

	public void add(PlayerCharacter pc)
	{
		apply(pc, levels.resolve(pc, "").intValue());
	}

	public void remove(PlayerCharacter pc)
	{
		apply(pc, -levels.resolve(pc, "").intValue());
	}

	private void apply(PlayerCharacter pc, int lvls)
	{
		boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
		SettingsHandler.setShowHPDialogAtLevelUp(false);
		boolean tempFeatDlg = SettingsHandler.getShowFeatDialogAtLevelUp();
		SettingsHandler.setShowFeatDialogAtLevelUp(false);
		int tempChoicePref = SettingsHandler.getSingleChoicePreference();
		SettingsHandler
				.setSingleChoicePreference(Constants.CHOOSER_SINGLECHOICEMETHOD_SELECTEXIT);

		pc.incrementClassLevel(lvls, pcClass.resolvesTo(), true, true);

		SettingsHandler.setSingleChoicePreference(tempChoicePref);
		SettingsHandler.setShowFeatDialogAtLevelUp(tempFeatDlg);
		SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
	}
}
