/*
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 */
package plugin.pretokens.parser;

import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * A prerequisite parser class that handles the parsing of pre reach tokens.
 */
public class PreReachParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{
    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String[] kindsHandled()
    {
        return new String[]{"REACH", "REACHEQ", "REACHGT", "REACHGTEQ", "REACHLT", "REACHLTEQ", "REACHNEQ"};
    }

    /**
     * Parse the pre req list
     *
     * @param kind            The kind of the prerequisite (less the "PRE" prefix)
     * @param formula         The body of the prerequisite.
     * @param invertResult    Whether the prerequisite should invert the result.
     * @param overrideQualify if set true, this prerequisite will be enforced in spite
     *                        of any "QUALIFY" tag that may be present.
     * @return PreReq
     * @throws PersistenceLayerException
     */
    @Override
    public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
            throws PersistenceLayerException
    {
        if (ControlUtilities.hasControlToken(Globals.getContext(), CControl.PCREACH))
        {
            throw new PersistenceLayerException("PREREACH is disabled when CREATEUREREACH control is used");
        }
        Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
        try
        {
            prereq.setKind("reach");

            // Get the comparator type SIZEGTEQ, BSIZE, SIZENEQ etc.
            String compType = kind.substring(5);
            if (compType.isEmpty())
            {
                compType = "gteq";
            }
            prereq.setOperator(compType);

            prereq.setOperand(formula);
            if (invertResult)
            {
                prereq.setOperator(prereq.getOperator().invert());
            }
        } catch (PrerequisiteException pe)
        {
            throw new PersistenceLayerException(
                    "Unable to parse the prerequisite :'" + kind + ':' + formula + "'. " + pe.getLocalizedMessage(), pe);
        }
        return prereq;
    }
}
