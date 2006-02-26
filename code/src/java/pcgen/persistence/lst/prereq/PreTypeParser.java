/*
 * Created on 23-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.persistence.lst.prereq;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;

import java.util.StringTokenizer;

/**
 * @author Valued Customer
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreTypeParser
	extends AbstractPrerequisiteParser
	implements PrerequisiteParserInterface {

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"TYPE"};
	}



	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#parse(java.lang.String, java.lang.String, boolean)
	 */
	public Prerequisite parse( String kind, String formula, boolean invertResult, boolean overrideQualify) throws PersistenceLayerException
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
			// First token was not a number,
			// must be old style syntax.
			return parseOldPreType(kind, formula, invertResult);
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
		}
		else
		{
			prereq.setKind(null);				// PREMULT
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
		return prereq;
	}

	protected Prerequisite parseOldPreType(String kind, String formula,	boolean invertResult)
	{
		// This one uses:
		//  PRETYPE:type1,type2|type3,[type4]

		/*
<prereq operator="eq" operand="3" >
 <prereq kind="type" operator="eq" operand="type1" ></prereq>
 <prereq operator="eq" operand="2" >
  <prereq kind="type" operator="eq" operand="type2" ></prereq>
  <prereq kind="type" operator="eq" operand="type3" ></prereq>
 </prereq>
 <prereq kind="type" operator="neq" operand="type4" ></prereq>
</prereq>
		 */

		String[] andTokens = formula.split(",");
		Prerequisite prereq = new Prerequisite();
		prereq.setKind(null);		// PREMULT
		prereq.setOperand( Integer.toString(andTokens.length) );

		// e.g.
		// PRETYPE:type1,type2|type3,[type4]
		// andTokens[] = "type1", "type2|type3", "[type4]"
		//

		for (int i = 0; i < andTokens.length; ++i)
		{
			final String andToken = andTokens[i];

			final int idxPipe = andToken.indexOf('|');

			Prerequisite andPrereq = new Prerequisite();
			prereq.addPrerequisite(andPrereq);

			if (idxPipe >= 0)
			{
				andPrereq.setKind(null);		// PREMULT

				String[] orTokens = andToken.split("\\|");
				andPrereq.setOperand("1");
				andPrereq.setOperator(PrerequisiteOperator.GTEQ);

				for (int j = 0; j < orTokens.length; ++j)
				{
					final String orToken = orTokens[j];

					final Prerequisite orPrereq = new Prerequisite();
					andPrereq.addPrerequisite(orPrereq);
					orPrereq.setKind("type");
					orPrereq.setOperand("1");
					if (orToken.startsWith("["))
					{
						orPrereq.setKey(orToken.substring(1, orToken.length()-1));
						orPrereq.setOperator(PrerequisiteOperator.NEQ);
					}
					else
					{
						orPrereq.setKey(orToken);
						orPrereq.setOperator(PrerequisiteOperator.EQ);
					}
				}
			}
			else
			{
				andPrereq.setKind("type");
				andPrereq.setOperand("1");
				if (andToken.startsWith("["))
				{
					andPrereq.setKey(andToken.substring(1, andToken.length()-1));
					andPrereq.setOperator(PrerequisiteOperator.NEQ);
				}
				else
				{
					andPrereq.setKey(andToken);
					andPrereq.setOperator(PrerequisiteOperator.EQ);
				}
			}

		}
		if (invertResult)
		{
			prereq.setOperator(prereq.getOperator().invert());
		}
		return prereq;
	}

}
