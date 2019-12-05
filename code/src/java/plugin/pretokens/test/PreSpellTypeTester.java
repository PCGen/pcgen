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

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * This is used to check the characters spellcasting ability.
 */
public class PreSpellTypeTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

    @Override
    public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
    {
        final String castingType = prereq.getKey();
        int requiredLevel;
        try
        {
            requiredLevel = Integer.parseInt(prereq.getOperand());
        } catch (NumberFormatException e)
        {
            requiredLevel = 1;
            Logging.errorPrintLocalised("PreSpellType.Badly_formed_spell_type", //$NON-NLS-1$
                    prereq.getOperand(), prereq.toString());
        }

        int count = character.countSpellCastTypeLevel(castingType, requiredLevel);

        final int runningTotal = prereq.getOperator().compare(count, 1);
        return countedTotal(prereq, runningTotal);
    }

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String kindHandled()
    {
        return "SPELLTYPE"; //$NON-NLS-1$
    }

    @Override
    public String toHtmlString(final Prerequisite prereq)
    {
        return LanguageBundle.getFormattedString("PreSpellType.toHtmlSingle", //$NON-NLS-1$
                prereq.getOperator().toDisplayString(), 1, prereq.getKey(), prereq.getOperand());
    }

}
