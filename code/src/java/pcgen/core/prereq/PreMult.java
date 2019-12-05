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
package pcgen.core.prereq;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

public class PreMult extends AbstractPrerequisiteTest implements PrerequisiteTest
{

    @Override
    public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
            throws PrerequisiteException
    {
        int runningTotal = 0;
        final int targetNumber = Integer.parseInt(prereq.getOperand());

        for (Prerequisite element : prereq.getPrerequisites())
        {
            final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();
            final PrerequisiteTest test = factory.getTest(element.getKind());
            if (test != null)
            {
                runningTotal += test.passes(element, character, source);
            } else
            {
                Logging.errorPrintLocalised("PreMult.cannot_find_subtest", element.getKind()); //$NON-NLS-1$
            }
        }

        runningTotal = prereq.getOperator().compare(runningTotal, targetNumber);

        return countedTotal(prereq, runningTotal);
    }

    @Override
    public String kindHandled()
    {
        return "MULT"; //$NON-NLS-1$
    }

    @Override
    public int passes(final Prerequisite prereq, final Equipment equipment, PlayerCharacter aPC)
            throws PrerequisiteException
    {
        int runningTotal = 0;
        final int targetNumber = Integer.parseInt(prereq.getOperand());

        for (Prerequisite element : prereq.getPrerequisites())
        {
            final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();
            final PrerequisiteTest test = factory.getTest(element.getKind());
            runningTotal += test.passes(element, equipment, aPC);
        }

        runningTotal = prereq.getOperator().compare(runningTotal, targetNumber);
        return countedTotal(prereq, runningTotal);
    }

    @Override
    public String toHtmlString(final Prerequisite prereq)
    {
        final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();

        StringBuilder str = new StringBuilder(250);
        String delimiter = ""; //$NON-NLS-1$
        for (Prerequisite element : prereq.getPrerequisites())
        {
            final PrerequisiteTest test = factory.getTest(element.getKind());
            if (test == null)
            {
                Logging.errorPrintLocalised("PreMult.cannot_find_subformatter", element.getKind()); //$NON-NLS-1$
            } else
            {
                str.append(delimiter);
                if (test instanceof PreMult && !delimiter.equals(""))
                {
                    str.append("##BR##");
                }
                str.append(test.toHtmlString(element));
                delimiter = LanguageBundle.getString("PreMult.html_delimiter"); //$NON-NLS-1$
            }
        }

        // Handle some special cases - all required, one required or none required
        int numRequired = -1;
        if (StringUtils.isNumeric(prereq.getOperand()))
        {
            numRequired = Integer.parseInt(prereq.getOperand());
        }
        if ((prereq.getOperator() == PrerequisiteOperator.GTEQ || prereq.getOperator() == PrerequisiteOperator.GT
                || prereq.getOperator() == PrerequisiteOperator.EQ) && numRequired == prereq.getPrerequisites().size())
        {
            return LanguageBundle.getFormattedString("PreMult.toHtmlAllOf", //$NON-NLS-1$
                    str.toString());
        }
        if ((prereq.getOperator() == PrerequisiteOperator.GTEQ || prereq.getOperator() == PrerequisiteOperator.EQ)
                && numRequired == 1)
        {
            return LanguageBundle.getFormattedString("PreMult.toHtmlEither", //$NON-NLS-1$
                    str.toString());
        }
        if ((prereq.getOperator() == PrerequisiteOperator.LT && numRequired == 1)
                || ((prereq.getOperator() == PrerequisiteOperator.EQ || prereq.getOperator() == PrerequisiteOperator.LTEQ)
                && numRequired == 0))
        {
            return LanguageBundle.getFormattedString("PreMult.toHtmlNone", //$NON-NLS-1$
                    str.toString());
        }

        return LanguageBundle.getFormattedString("PreMult.toHtml", //$NON-NLS-1$
                prereq.getOperator().toDisplayString(), prereq.getOperand(), str.toString());

    }

}
