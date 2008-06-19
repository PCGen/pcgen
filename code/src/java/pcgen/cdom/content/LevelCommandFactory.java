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
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.rules.persistence.TokenUtilities;

/**
 * A LevelCommandFactory is used to identify a PCClass which is to be applied
 * with a given number of levels (as defiend by a Formula) to a PlayerCharacter.
 */
public class LevelCommandFactory extends ConcretePrereqObject implements
		Comparable<LevelCommandFactory>
{

	/**
	 * The PCClass to be applied to the PlayerCharacter when this
	 * LevelCommandFactory is executed.
	 */
	private final CDOMSingleRef<PCClass> pcClass;

	/**
	 * A Formula indicating the number of levels to be applied to the
	 * PlayerCharacter when this LevelCommandFactory is executed.
	 */
	private final Formula levels;

	/**
	 * Constructs a new LevelCommandFactory for the given PCClass and number of
	 * levels
	 * 
	 * @param cl
	 *            A Reference to the PCClass to be applied to the
	 *            PlayerCharacter when this LevelCommandFactory is executed.
	 *            This reference must be resolved before the LevelCommandFactory
	 *            can be executed.
	 * @param lvls
	 *            A Formula indicating the number of levels to be applied to the
	 *            PlayerCharacter when this LevelCommandFactory is executed.
	 */
	public LevelCommandFactory(CDOMSingleRef<PCClass> cl, Formula lvls)
	{
		pcClass = cl;
		levels = lvls;
	}

	/**
	 * Returns the Formula indicating the number of levels to be applied to the
	 * PlayerCharacter when this LevelCommandFactory is executed.
	 * 
	 * @return The Formula indicating the number of levels to be applied to the
	 *         PlayerCharacter when this LevelCommandFactory is executed.
	 */
	public Formula getLevelCount()
	{
		return levels;
	}

	/**
	 * Returns a Reference to the PCClass to be applied to the PlayerCharacter
	 * when this LevelCommandFactory is executed.
	 * 
	 * @return A Reference to the PCClass to be applied to the PlayerCharacter
	 *         when this LevelCommandFactory is executed.
	 */
	public PCClass getPCClass()
	{
		return pcClass.resolvesTo();
	}

	/**
	 * Returns a representation of this LevelCommandFactory, suitable for
	 * storing in an LST file.
	 */
	public String getLSTformat()
	{
		return pcClass.getLSTformat();
	}

	/**
	 * Returns the consistent-with-equals hashCode for this LevelCommandFactory
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return pcClass.hashCode() * 29 + levels.hashCode();
	}

	/**
	 * Returns true if this LevelCommandFactory is equal to the given Object.
	 * Equality is defined as being another LevelCommandFactory object with
	 * equal PCClass to be added and equal level Formula.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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

	/**
	 * Compares this LevelCommandFactory to another LevelCommandFactory.
	 * 
	 * @param other
	 *            The LevelCommandFactory to be compared to this
	 *            LevelCommandFactory.
	 * @return 0 if this LevelCommandFactory is equal to the given
	 *         LevelCommandFactory; -1 if this LevelCommandFactory has a PCClass
	 *         and level formula that sorts before the given
	 *         LevelCommandFactory; +1 if this LevelCommandFactory has a PCClass
	 *         and level formula that sorts before the given LevelCommandFactory
	 */
	public int compareTo(LevelCommandFactory other)
	{
		int i = TokenUtilities.REFERENCE_SORTER.compare(pcClass, other.pcClass);
		if (i == 0)
		{
			if (levels.equals(other.levels))
			{
				return 0;
			}
			if (levels == other.levels)
			{
				return 0;
			}
			return levels.toString().compareTo(other.levels.toString());
		}
		return i;
	}

	/**
	 * Adds levels of the PCClass in this LevelCommandFactory to the given
	 * PlayerCharacter.
	 * 
	 * The number of levels added is defined by the level formula in this
	 * LevelCommandFactory, and the PCClass is defined by the CDOMReference
	 * provided when this LevelCommandFactory was constructed.
	 * 
	 * NOTE: It is important that the CDOMReference provided during construction
	 * of this LevelCommandFactory is resolved before this method is called.
	 * 
	 * @param pc
	 *            The PlayerCharacter to which the levels of the PCClass in this
	 *            LevelCommandFactory will be added.
	 */
	public void add(PlayerCharacter pc)
	{
		apply(pc, levels.resolve(pc, "").intValue());
	}

	/**
	 * Removes levels of the PCClass in this LevelCommandFactory to the given
	 * PlayerCharacter.
	 * 
	 * The number of levels removed is defined by the level formula in this
	 * LevelCommandFactory, and the PCClass is defined by the CDOMReference
	 * provided when this LevelCommandFactory was constructed.
	 * 
	 * NOTE: It is important that the CDOMReference provided during construction
	 * of this LevelCommandFactory is resolved before this method is called.
	 * 
	 * @param pc
	 *            The PlayerCharacter from which the levels of the PCClass in
	 *            this LevelCommandFactory will be removed.
	 */
	public void remove(PlayerCharacter pc)
	{
		apply(pc, -levels.resolve(pc, "").intValue());
	}

	/**
	 * Applies a change in level of the PCClass in this LevelCommandFactory to
	 * the given PlayerCharacter. The change is provided as an argument to this
	 * method. If the number of levels is greater than zero, then levels are
	 * added to the given PlayerCharacter, if less than zero, levels are removed
	 * from the given PlayerCharacter
	 * 
	 * NOTE: It is important that the CDOMReference provided during construction
	 * of this LevelCommandFactory is resolved before this method is called.
	 * 
	 * @param pc
	 *            The PlayerCharacter from which the levels of the PCClass in
	 *            this LevelCommandFactory will be removed.
	 * @param lvls
	 *            The number of levels to apply to the PlayerCharacter
	 */
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
