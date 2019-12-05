/*
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

import java.util.StringTokenizer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * A prerequisite parser class that handles the parsing of pre PC Level tokens.
 */
public class PrePCLevelParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{

    //TODO created tests
    //TODO create writer
    //TODO create prereqparser

    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String[] kindsHandled()
    {
        return new String[]{"PCLEVEL"};
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

        if (formula.contains("MIN") || formula.contains("MAX"))
        {
            StringTokenizer tok = new StringTokenizer(formula, ",");
            Prerequisite maxPrereq = new Prerequisite();
            Prerequisite minPrereq = new Prerequisite();
            boolean hasMin = false;
            boolean hasMax = false;
            while (tok.hasMoreTokens())
            {
                String value = tok.nextToken();
                String[] vals = value.split("=");
                if (vals.length != 2)
                {
                    throw new PersistenceLayerException("PREPCLEVEL must be either 'MIN=x', 'MAX=y' or "
                            + "'MIN=x,MAX=y' where 'x' and 'y' are integers. '" + formula + "' is not valid. ");

                }
                String token = vals[0];
                String hdVal = vals[1];
                try
                {
                    Integer.parseInt(hdVal);
                } catch (NumberFormatException nfe)
                {
                    throw new PersistenceLayerException("PREPCLEVEL must be either 'MIN=x', 'MAX=y' or "
                            + "'MIN=x,MAX=y' where 'x' and 'y' are integers. '" + formula + "' is not valid: " + hdVal
                            + " is not an integer", nfe);
                }
                if (token.equals("MIN"))
                {
                    minPrereq.setKind("pclevel");
                    minPrereq.setOperator(PrerequisiteOperator.GTEQ);
                    minPrereq.setOperand(hdVal);

                    hasMin = true;

                }
                if (token.equals("MAX"))
                {
                    maxPrereq.setKind("pclevel");
                    maxPrereq.setOperator(PrerequisiteOperator.LTEQ);
                    maxPrereq.setOperand(hdVal);
                    hasMax = true;
                }
            }
            if (hasMin && hasMax)
            {
                prereq.setKind(null); // PREMULT
                prereq.setOperand("2");
                prereq.addPrerequisite(minPrereq);
                prereq.addPrerequisite(maxPrereq);
            } else if (hasMin)
            {
                prereq = minPrereq;
            } else if (hasMax)
            {
                prereq = maxPrereq;
            }

        }
        if (invertResult)
        {
            prereq.setOperator(prereq.getOperator().invert());
        }
        return prereq;
    }
}
