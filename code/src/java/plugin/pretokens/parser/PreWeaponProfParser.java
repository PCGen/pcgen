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

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser;

/**
 * A prerequisite parser class that handles the parsing of pre weaponprof tokens.
 */
public class PreWeaponProfParser extends AbstractPrerequisiteListParser
{
    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String[] kindsHandled()
    {
        return new String[]{"WEAPONPROF"};
    }

    /**
     * This operation performs the actual parsing.
     *
     * @param kind            the kind of the prerequisite (less the "PRE" prefix)
     * @param formula         The body of the prerequisite;
     * @param invertResult    If the prerequisite should invert the result
     *                        before it is returned
     * @param overrideQualify if set true, this prerequisite will be enforced in spite
     *                        of any "QUALIFY" tag that may be present.
     * @return a object for testing the prerequisite
     * @throws PersistenceLayerException
     */
    @Override
    public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
            throws PersistenceLayerException
    {
        Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);

        doTypeInvertFixUp(prereq);

        return prereq;
    }

    private static void doTypeInvertFixUp(Prerequisite prereq)
    {
        if ("weaponprof".equalsIgnoreCase(prereq.getKind()))
        {
            if (prereq.getKey().startsWith("TYPE"))
            {
                prereq.setCountMultiples(true);
            } else if (prereq.getKey().startsWith("["))
            {
                final int length = prereq.getKey().length() - 1;
                final int rBracket = prereq.getKey().lastIndexOf(']');
                final int endIndex = Math.max(length, rBracket);

                final String key = prereq.getKey().substring(1, endIndex);

                prereq.setKey(key);
                prereq.setOperator(prereq.getOperator().invert());
            }
        }

        /*
         * In case of PREMULT (e.g 'PREWEAPONPROF:1,TYPE.Martial,Chain (Spiked)',
         * need to check all sub-prereqs
         */
        for (Prerequisite subReq : prereq.getPrerequisites())
        {
            doTypeInvertFixUp(subReq);
        }
    }

    @Override
    protected boolean allowsNegate()
    {
        return true;
    }
}
