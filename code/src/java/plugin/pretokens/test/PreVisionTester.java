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
import pcgen.core.Vision;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.enumeration.VisionType;

/**
 * Checks a characters vision..
 */
public class PreVisionTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

    @Override
    public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
    {
        String range = prereq.getOperand();
        VisionType requiredVisionType = VisionType.getVisionType(prereq.getKey());
        int runningTotal = 0;
        if (range.equals("ANY"))
        {
            Vision v = display.getVision(requiredVisionType);
            if (v == null)
            {
                runningTotal += prereq.getOperator().compare(0, 1);
            } else
            {
                runningTotal += prereq.getOperator().compare(1, 0);
            }
        } else
        {
            int requiredRange = Integer.parseInt(range);
            Vision v = display.getVision(requiredVisionType);
            if (v == null)
            {
                runningTotal += prereq.getOperator().compare(0, requiredRange);
            } else
            {
                int visionRange = Integer.parseInt(v.getDistance().toString());
                runningTotal += prereq.getOperator().compare(visionRange, requiredRange);
            }
        }

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
        return "VISION"; //$NON-NLS-1$
    }

}
