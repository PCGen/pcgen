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
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.util.Logging;

public class PreDomainTester extends AbstractDisplayPrereqTest
{

    private static final Class<Domain> DOMAIN_CLASS = Domain.class;

    @Override
    public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
    {
        int runningTotal;
        int number = 0;
        try
        {
            number = Integer.parseInt(prereq.getOperand());
        } catch (NumberFormatException e)
        {
            Logging.errorPrintLocalised("PreDomain.error.bad_operand", prereq.toString()); //$NON-NLS-1$
        }

        if (prereq.getKey().equalsIgnoreCase("ANY"))
        {
            runningTotal = display.getDomainCount();
        } else
        {
            Domain domain = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(DOMAIN_CLASS,
                    prereq.getKey());
            final boolean hasDomain = domain != null && display.hasDomain(domain);
            runningTotal = hasDomain ? 1 : 0;
        }

        runningTotal = prereq.getOperator().compare(runningTotal, number);
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
        return "DOMAIN"; //$NON-NLS-1$
    }

}
