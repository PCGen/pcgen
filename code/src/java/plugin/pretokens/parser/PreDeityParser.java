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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.pretokens.parser;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * {@code PreDeityParser} parses PREDEITY prerequisites. It handles both
 * new (PREDEITY:1,Odin) and old (PREDEITY:Odin) format syntax along with the
 * hasdeity syntax (PREDEITY:Y or PREDEITY:No).
 */
public class PreDeityParser extends AbstractPrerequisiteListParser implements PrerequisiteParserInterface
{
    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String[] kindsHandled()
    {
        return new String[]{"DEITY"};
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

        // Scan for any has deity options
        replaceHasDeityPrereqs(prereq);
        return prereq;
    }

    /**
     * Scan a predeity prerequisite and its children, converting any yes or no deity
     * entries into hasdeity prereqs.
     *
     * @param prereq The prereq to be scanned.
     */
    private static void replaceHasDeityPrereqs(Prerequisite prereq)
    {
        String key = prereq.getKey();
        if ("deity".equalsIgnoreCase(prereq.getKind()) && key != null)
        {
            char firstChar = key.charAt(0);
            if ((key.length() == 1) && (firstChar == 'y' || firstChar == 'Y' || firstChar == 'n' || firstChar == 'N')
                    || key.equalsIgnoreCase("yes") || key.equalsIgnoreCase("no"))
            {
                if (firstChar == 'y' || firstChar == 'Y')
                {
                    prereq.setKey("Y");
                } else
                {
                    prereq.setKey("N");
                }
                prereq.setKind("has.deity");
            }
        }

        for (Prerequisite subprereq : prereq.getPrerequisites())
        {
            replaceHasDeityPrereqs(subprereq);
        }
    }
}
