/*
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
package pcgen.cdom.facet;

import java.util.List;
import java.util.ListIterator;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

public class AddLevelFacet implements DataFacetChangeListener<PCTemplate>
{

	private final Class<?> thisClass = getClass();

	public void associatePlayerCharacter(CharID id, PlayerCharacter pc)
	{
		FacetCache.set(id, thisClass, pc);
	}

	public void dataAdded(DataFacetChangeEvent<PCTemplate> dfce)
	{
		PCTemplate template = dfce.getCDOMObject();
		CharID id = dfce.getCharID();
		PlayerCharacter pc = (PlayerCharacter) FacetCache.get(id, thisClass);

		// If we are importing these levels will have been saved with the
		// character so don't apply them again.
		if (!pc.isImporting())
		{
			for (LevelCommandFactory lcf : template
					.getSafeListFor(ListKey.ADD_LEVEL))
			{
				add(lcf.getLevelCount(), lcf.getPCClass(), pc);
			}
		}
	}

	public void dataRemoved(DataFacetChangeEvent<PCTemplate> dfce)
	{
		PCTemplate template = dfce.getCDOMObject();
		CharID id = dfce.getCharID();
		PlayerCharacter pc = (PlayerCharacter) FacetCache.get(id, thisClass);

		List<LevelCommandFactory> lcfList = template
				.getSafeListFor(ListKey.ADD_LEVEL);
		for (ListIterator<LevelCommandFactory> it = lcfList
				.listIterator(lcfList.size()); it.hasPrevious();)
		{
			LevelCommandFactory lcf = it.previous();
			remove(lcf.getLevelCount(), lcf.getPCClass(), pc);
		}
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
	 * @throws NullPointerException
	 *             if the given PlayerCharacter is null
	 */
	public void add(Formula levels, PCClass cl, PlayerCharacter pc)
	{
		apply(pc, cl, levels.resolve(pc, "").intValue());
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
	 * @throws NullPointerException
	 *             if the given PlayerCharacter is null
	 */
	public void remove(Formula levels, PCClass cl, PlayerCharacter pc)
	{
		apply(pc, cl, -levels.resolve(pc, "").intValue());
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
	 * @param pcClass
	 *            Despite what the javadoc for this method claims, it actually
	 *            ignores the PCClass stored as state and instead works on the
	 *            class passed here.
	 * @param levels
	 *            The number of levels to apply to the PlayerCharacter
	 * @throws NullPointerException
	 *             if the given PlayerCharacter is null
	 */
	private void apply(PlayerCharacter pc, PCClass pcClass, int levels)
	{
		boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
		SettingsHandler.setShowHPDialogAtLevelUp(false);
		boolean tempFeatDlg = SettingsHandler.getShowFeatDialogAtLevelUp();
		SettingsHandler.setShowFeatDialogAtLevelUp(false);
		int tempChoicePref = SettingsHandler.getSingleChoicePreference();
		SettingsHandler.setSingleChoicePreference(Constants.CHOOSER_SINGLE_CHOICE_METHOD_SELECT_EXIT);

		pc.incrementClassLevel(levels, pcClass, true, true);

		SettingsHandler.setSingleChoicePreference(tempChoicePref);
		SettingsHandler.setShowFeatDialogAtLevelUp(tempFeatDlg);
		SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
	}
}
