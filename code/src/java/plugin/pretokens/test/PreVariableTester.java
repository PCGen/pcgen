/*
 *
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
import pcgen.cdom.base.Constants;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.prereq.PrerequisiteTestFactory;
import pcgen.core.utils.CoreUtility;

/**
 * Prerequisite test for the presence of a variable.
 */
public class PreVariableTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String kindHandled()
    {
        return "VAR"; //$NON-NLS-1$
    }

    @Override
    public int passes(final Prerequisite prereq, final Equipment equipment, PlayerCharacter aPC)
            throws PrerequisiteException
    {
        if (aPC == null)
        {
            return 0;
        }
        final String eqVar = "EQ:" + equipment.getNonHeadedName(); //$NON-NLS-1$
        final float aVar = equipment.getVariableValue(prereq.getKey(), eqVar, aPC);
        final float aTarget = equipment.getVariableValue(prereq.getOperand(), eqVar, aPC);

        float runningTotal = prereq.getOperator().compare(aVar, aTarget);
        if (CoreUtility.doublesEqual(runningTotal, 0.0))
        {
            return 0;
        }
        for (Prerequisite element : prereq.getPrerequisites())
        {
            final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();
            final PrerequisiteTest test = factory.getTest(element.getKind());
            if (test != null)
            {
                // all of the tests must pass, so just
                // assign the value here, don't add
                runningTotal = test.passes(element, equipment, aPC);
                if (CoreUtility.doublesEqual(runningTotal, 0.0))
                {
                    return 0;
                }
            }
        }
        // The test has passed, but only pass 1 rather than the runningTotal
        // as the total could be negative, and isn't running...
        return countedTotal(prereq, 1);
    }

    @Override
    public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
            throws PrerequisiteException
    {
        String src = (source == null) ? Constants.EMPTY_STRING : source.getQualifiedKey();
        final float aVar = character.getVariableValue(prereq.getKey(), src);
        final float aTarget = character.getVariableValue(prereq.getOperand(), src);

        float runningTotal = prereq.getOperator().compare(aVar, aTarget);
        if (CoreUtility.doublesEqual(runningTotal, 0.0))
        {
            return 0;
        }
        for (Prerequisite element : prereq.getPrerequisites())
        {
            final PrerequisiteTestFactory factory = PrerequisiteTestFactory.getInstance();
            final PrerequisiteTest test = factory.getTest(element.getKind());

            if (test != null)
            {
                // all of the tests must pass, so just
                // assign the value here, don't add
                runningTotal = test.passes(element, character, source);
                if (CoreUtility.doublesEqual(runningTotal, 0.0))
                {
                    return 0;
                }
            }
        }

        // The test has passed, but only pass 1 rather than the runningTotal
        // as the total could be negative, and isn't running...
        return countedTotal(prereq, 1);
    }

}
