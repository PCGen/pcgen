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
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

/**
 * Prerequisite tester, test for the presence of text.
 */
public class PreTextTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

    @Override
    public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
    {
        // PRETEXT: is a flavor text string that needs
        // to be displayed in the GUI, but the PC
        // should always be qualified to take it, as
        // it's a DM call/issue
        if (prereq.getOperator().equals(PrerequisiteOperator.NEQ))
        {
            return countedTotal(prereq, 0);
        }
        return countedTotal(prereq, 1);
    }

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String kindHandled()
    {
        return "TEXT"; //$NON-NLS-1$
    }

    /**
     * Convert PreReq to an HTML string
     *
     * @param prereq
     * @return html String representation of the PreReq
     */
    @Override
    public String toHtmlString(final Prerequisite prereq)
    {
        return LanguageBundle.getFormattedString("AbstractPrerequisiteTest.toHtml",
                prereq.getOperator().toDisplayString(), "", "", prereq.getKey());
    }

}
