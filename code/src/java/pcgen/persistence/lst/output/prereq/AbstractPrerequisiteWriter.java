/*
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 *
 *
 */

package pcgen.persistence.lst.output.prereq;

import java.io.IOException;
import java.io.Writer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;

/**
 * This class handles writing pre reqs for LST Tokens
 */
public class AbstractPrerequisiteWriter
{

    protected void checkValidOperator(Prerequisite prereq, PrerequisiteOperator[] comparators)
            throws PersistenceLayerException
    {
        StringBuilder comparatorString = new StringBuilder(25);
        for (int i = 0;i < comparators.length;i++)
        {
            PrerequisiteOperator comparator = comparators[i];
            if (prereq.getOperator().equals(comparators[i]))
            {
                return;
            }
            if (i > 0)
            {
                comparatorString.append(", ");
            }
            comparatorString.append(comparator);
        }

        String kind = prereq.getKind();
        if (kind == null)
        {
            kind = "<NULL>";
        }
        throw new PersistenceLayerException("Cannot write token: LST syntax only supports "
                + comparatorString.toString() + " operators for PRE" + kind.toUpperCase() + ": " + prereq.toString());
    }

    /**
     * Meant to be over-ridden
     * TODO  Does this and its overriding methods need to throw IOException?
     *
     * @param writer
     * @param prereq
     * @return false if not over ridden
     * @throws IOException if IO errors occur
     */
    public boolean specialCase(Writer writer, Prerequisite prereq) throws IOException
    {
        return false;
    }

    protected PrerequisiteOperator getConsolidateMethod(String handled, Prerequisite prereq, boolean ranked)
    {
        // If this is NOT a PREMULT... fail
        if (prereq.getKind() != null)
        {
            return null;
        }
        PrerequisiteOperator oper = null;
        for (Prerequisite p : prereq.getPrerequisites())
        {
            //
            // ...testing one item...
            //
            if (!ranked && !"1".equals(p.getOperand()))
            {
                return null;
            }
            //
            // ...with all PREARMORTYPE entries...
            //
            if (!handled.equalsIgnoreCase(p.getKind()))
            {
                return null;
            }
            //
            // ...and the same operator...
            //
            if (oper == null)
            {
                oper = p.getOperator();
            } else
            {
                if (!oper.equals(p.getOperator()))
                {
                    return null;
                }
            }
        }
        String count = prereq.getOperand();
        if (PrerequisiteOperator.LT.equals(oper))
        {
            try
            {
                int i = Integer.parseInt(count);
                if (prereq.getPrerequisiteCount() != i)
                {
                    return null;
                }
            } catch (NumberFormatException e)
            {
                return null;
            }
        } else if (!PrerequisiteOperator.GTEQ.equals(oper))
        {
            // TODO Not sure whether these can be consolidated...
            return null;
        }
        return oper;
    }
}
