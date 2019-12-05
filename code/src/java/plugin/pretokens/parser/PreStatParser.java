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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.pretokens.parser;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.util.Logging;

/**
 * A prerequisite parser class that handles the parsing of pre stat tokens.
 */
public class PreStatParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String[] kindsHandled()
    {
        return new String[]{"STAT", "STATEQ", "STATGT", "STATGTEQ", "STATLT", "STATLTEQ", "STATNEQ"};
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
        Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
        try
        {
            prereq.setKind(null); // PREMULT

            // Get the comparator type STATGTEQ, STAT, STATNEQ etc.
            String compType = kind.substring(4);

            if (compType.isEmpty())
            {
                compType = "gteq";
            }

            String[] tokens = formula.split(",|\\|");
            int currToken = 0;

            // Get the minimum match count
            String aString = tokens[currToken++];

            try
            {
                prereq.setOperand(Integer.toString(Integer.parseInt(aString)));
            } catch (NumberFormatException e)
            {
                Logging.errorPrint("Badly formed PRESTAT attribute: " + aString);
                prereq.setOperand("1");
            }

            while (currToken < tokens.length)
            {
                aString = tokens[currToken++];

                final int idxEquals = aString.lastIndexOf('=');
                if (idxEquals < 3)
                {
                    throw new PersistenceLayerException(
                            "PRE" + kindsHandled()[0] + " formula '" + formula + "' is not valid.");
                }

                final String stat = aString.substring(0, Math.min(3, idxEquals));
                Prerequisite statPrereq = new Prerequisite();
                statPrereq.setKind("stat");
                statPrereq.setKey(stat);
                statPrereq.setOperator(compType);
                statPrereq.setOperand(aString.substring(idxEquals + 1));

                prereq.addPrerequisite(statPrereq);
            }

            if ((prereq.getPrerequisiteCount() == 1) && (prereq.getOperator() == PrerequisiteOperator.GTEQ)
                    && prereq.getOperand().equals("1"))
            {
                prereq = prereq.getPrerequisites().get(0);
            }

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
