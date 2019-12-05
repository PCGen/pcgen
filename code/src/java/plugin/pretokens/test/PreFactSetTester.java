/*
 * Copyright 2014-15 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.pretokens.test;

import java.util.List;

import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Reducible;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.output.publish.OutputDB;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code PreFactTester} is responsible for testing FACT values on an object.
 */
public class PreFactSetTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

    @Override
    public int passes(final Prerequisite prereq, final PlayerCharacter aPC, CDOMObject source)
            throws PrerequisiteException
    {

        final int number;
        try
        {
            number = Integer.parseInt(prereq.getOperand());
        } catch (NumberFormatException exceptn)
        {
            throw new PrerequisiteException(
                    LanguageBundle.getFormattedString("PreFactSet.error", prereq.toString()), exceptn); //$NON-NLS-1$
        }

        String location = prereq.getCategoryName();
        String[] locationElements = location.split("\\.");
        Iterable<Reducible> objModel = (Iterable<Reducible>) OutputDB.getIterable(aPC.getCharID(), locationElements);
        if (objModel == null)
        {
            throw new PrerequisiteException("Output System does not have model for: " + location);
        }

        String test = prereq.getKey();
        String[] factinfo = test.split("=");
        String factid = factinfo[0];
        String factval = factinfo[1];
        FactSetKey<?> fk = FactSetKey.valueOf(factid);

        int runningTotal = getRunningTotal(prereq, number, objModel, factval, fk);
        return countedTotal(prereq, runningTotal);
    }

    private static <T> int getRunningTotal(final Prerequisite prereq, final int number, Iterable<Reducible> objModel,
            String factval, FactSetKey<T> fk)
    {
        T targetVal = fk.getFormatManager().convert(factval);
        int runningTotal = 0;
        CDO:
        for (Reducible r : objModel)
        {
            List<Indirect<T>> sets = r.getCDOMObject().getSetFor(fk);
            for (Indirect<T> indirect : sets)
            {
                if (indirect.get().equals(targetVal))
                {
                    runningTotal++;
                    continue CDO;
                }
            }
        }

        runningTotal = prereq.getOperator().compare(runningTotal, number);
        return runningTotal;
    }

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String kindHandled()
    {
        return "FACTSET"; //$NON-NLS-1$
    }

    @Override
    public String toHtmlString(final Prerequisite prereq)
    {
        // Simplify the output when requiring a single source
        if (prereq.getOperator() == PrerequisiteOperator.GTEQ && ("1".equals(prereq.getOperand())))
        {
            return prereq.getKey();
        }

        final String foo = LanguageBundle.getFormattedString("PreFactSet.toHtml", //$NON-NLS-1$
                prereq.getOperator().toDisplayString(), prereq.getOperand(), prereq.getKey());
        return foo;
    }

}
