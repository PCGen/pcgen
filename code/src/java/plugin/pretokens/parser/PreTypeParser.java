/*
 * Created on 23-Dec-2003
 *
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
 * @author Valued Customer
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreTypeParser extends AbstractPrerequisiteParser implements
		PrerequisiteParserInterface
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{"TYPE"};
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#parse(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public Prerequisite parse(String kind, String formula,
		boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{

		int aNum = 1;
		final StringTokenizer aTok = new StringTokenizer(formula, ",");
		String aString = aTok.nextToken();
		try
		{
			aNum = Integer.parseInt(aString);
		}
		catch (NumberFormatException nfe)
		{
			throw new PersistenceLayerException(formula + " must start with a number in PRETYPE");
		}

		// Parse new style syntax
		Prerequisite prereq =
				super.parse(kind, formula, invertResult, overrideQualify);
		prereq.setOperand(Integer.toString(aNum));

		//
		// If only 1 selection, then don't make a PREMULT
		// i.e. PRETYPE:1,typename shouldn't need to be converted to PREMULT:1,[PRETYPE:1,typename]
		//
		if (aTok.countTokens() == 1)
		{
			prereq.setOperator(PrerequisiteOperator.EQ);
			prereq.setKey(aTok.nextToken());
		}
		else
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
