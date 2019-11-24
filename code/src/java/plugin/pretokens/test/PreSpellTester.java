/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.pretokens.test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Domain;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.spell.Spell;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

public class PreSpellTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
	{
		CharacterDisplay display = character.getDisplay();
		int requiredNumber = 0;
		try
		{
			requiredNumber = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint(
				LanguageBundle.getString("PreSpell.error.badly_formed_attribute") + prereq.toString()); //$NON-NLS-1$
		}

		// Build a list of all possible spells
		final List<Spell> aArrayList =
				character.aggregateSpellList("", "", "", 0, 20); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		//Needs to add domain spells as well
		for (Domain d : display.getDomainSet())
		{
			aArrayList
				.addAll(character.getAllSpellsInLists(Collections.singletonList(d.get(ObjectKey.DOMAIN_SPELLLIST))));
		}

		//Are there Innate Spell-like abilities?
		if (character.getAutoSpells())
		{
			Collection<CDOMReference<Spell>> mods = display.getRace().getListMods(Spell.SPELLS);
			if (mods != null)
			{
				for (CDOMReference<Spell> ref : mods)
				{
					aArrayList.addAll(ref.getContainedObjects());
				}
			}
		}

		final String spellName = prereq.getKey();
		int runningTotal = 0;

		for (Spell aSpell : aArrayList)
		{
			if (aSpell != null && aSpell.getKeyName() != null && aSpell.getKeyName().equalsIgnoreCase(spellName))
			{
				runningTotal++;
			}
		}
		runningTotal = prereq.getOperator().compare(runningTotal, requiredNumber);
		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	@Override
	public String kindHandled()
	{
		return "SPELL"; //$NON-NLS-1$
	}

	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		final Object[] args =
				{prereq.getOperator().toDisplayString(), prereq.getOperand(), prereq.getKey()};
		return LanguageBundle.getFormattedString("PreSpell.toHtml", args); //$NON-NLS-1$
	}

}
