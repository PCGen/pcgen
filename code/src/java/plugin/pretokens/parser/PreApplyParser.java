/*
 * PreApplyParser.java
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
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.parser;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * A prerequisite parser class that handles the parsing of pre apply tokens.
 *
 */
public class PreApplyParser extends AbstractPrerequisiteParser implements
		PrerequisiteParserInterface
{
	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	public String[] kindsHandled()
	{
		return new String[]{"APPLY"};
	}

	/**
	 * Parse the pre req list
	 *
	 * @param kind The kind of the prerequisite (less the "PRE" prefix)
	 * @param formula The body of the prerequisite.
	 * @param invertResult Whether the prerequisite should invert the result.
	 * @param overrideQualify
	 *           if set true, this prerequisite will be enforced in spite
	 *           of any "QUALIFY" tag that may be present.
	 * @return PreReq
	 */
	@Override
	public Prerequisite parse(String kind,
	                          String formula,
	                          boolean invertResult,
	                          boolean overrideQualify)
	{

		String[] andTokens = formula.split(",");
		Prerequisite prereq = new Prerequisite();
		prereq.setOperator(PrerequisiteOperator.EQ);
		prereq.setOperand(Integer.toString(andTokens.length));
		prereq.setKind("APPLY");

		for (int i = 0; i < andTokens.length; i++)
		{
			String andToken = andTokens[i];

			Prerequisite andPrereq = new Prerequisite();
			prereq.addPrerequisite(andPrereq);

			if (andToken.indexOf(';') > -1)
			{
				String[] orTokens = andToken.split("\\;");
				andPrereq.setOperand("1");
				andPrereq.setOperator(PrerequisiteOperator.GTEQ);

				for (int j = 0; j < orTokens.length; j++)
				{
					String orToken = orTokens[j];

					Prerequisite orPrereq = new Prerequisite();
					andPrereq.addPrerequisite(orPrereq);
					orPrereq.setKind("APPLY");
					if (orToken.startsWith("["))
					{
						orPrereq.setOperand(orToken.substring(1, orToken
							.length() - 1));
						orPrereq.setOperator(PrerequisiteOperator.NEQ);
					}
					else
					{
						orPrereq.setOperand(orToken);
						orPrereq.setOperator(PrerequisiteOperator.EQ);
					}
				}
			}
			else
			{
				andPrereq.setKind("APPLY");
				if (andToken.startsWith("["))
				{
					andPrereq.setOperand(andToken.substring(1, andToken
						.length() - 1));
					andPrereq.setOperator(PrerequisiteOperator.NEQ);
				}
				else
				{
					andPrereq.setOperand(andToken);
					andPrereq.setOperator(PrerequisiteOperator.EQ);
				}
			}

		}
		if (invertResult)
		{
			prereq.setOperator(prereq.getOperator().invert());
		}
		prereq.setOverrideQualify(overrideQualify);
		return prereq;
	}

}
