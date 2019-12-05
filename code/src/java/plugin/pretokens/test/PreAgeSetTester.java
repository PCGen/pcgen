/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the GNU
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
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

public class PreAgeSetTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

    @Override
    public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
            throws PrerequisiteException
    {
        final int ageset = display.getAgeSetIndex();

        int runningTotal = -1;
        int anInt;

        try
        {
            anInt = Integer.parseInt(prereq.getKey());
        } catch (NumberFormatException exc)
        {
            anInt = display.getBioSet().getAgeSetNamed(prereq.getKey());
        } catch (Exception e)
        {
            throw new PrerequisiteException(
                    LanguageBundle.getFormattedString(
                            "PreAgeSet.error.badly_formed_attribute", prereq.getOperand()), e); //$NON-NLS-1$
        }

        if (anInt == -1)
        { //String was not recognized
            throw new PrerequisiteException(
                    LanguageBundle.getFormattedString(
                            "PreAgeSet.error.badly_formed_attribute", prereq.getOperand())); //$NON-NLS-1$
        }

        runningTotal = prereq.getOperator().compare(ageset, anInt);

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
        return "AGESET"; //$NON-NLS-1$
    }

}
