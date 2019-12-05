/*
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.parser;

import java.util.StringTokenizer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * A prerequisite parser class that handles the parsing of pre type tokens.
 */
public class PreTypeParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{
    /**
     * Get the type of prerequisite handled by this token.
     *
     * @return the type of prerequisite handled by this token.
     */
    @Override
    public String[] kindsHandled()
    {
        return new String[]{"TYPE"};
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

        int aNum;
        final StringTokenizer aTok = new StringTokenizer(formula, ",");
        String aString = aTok.nextToken();
        try
        {
            aNum = Integer.parseInt(aString);
        } catch (NumberFormatException nfe)
        {
            throw new PersistenceLayerException(formula + " must start with a number in PRETYPE", nfe);
        }

        // Parse new style syntax
        Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
        prereq.setOperand(Integer.toString(aNum));

        //
        // If only 1 selection, then don't make a PREMULT
        // i.e. PRETYPE:1,typename shouldn't need to be converted to PREMULT:1,[PRETYPE:1,typename]
        //
        if (aTok.countTokens() == 1)
        {
            prereq.setOperator(PrerequisiteOperator.EQ);
            prereq.setKey(aTok.nextToken());
        } else
        {
            prereq.setKind(null); // PREMULT
            while (aTok.hasMoreTokens())
            {
                Prerequisite subreq = new Prerequisite();
                subreq.setOperator(PrerequisiteOperator.EQ);
                subreq.setKind("type");
                subreq.setKey(aTok.nextToken());
                prereq.addPrerequisite(subreq);
            }
        }

        if (invertResult)
        {
            prereq.setOperator(prereq.getOperator().invert());
        }
        //prereq.setDeprecated();
        return prereq;
    }
}
