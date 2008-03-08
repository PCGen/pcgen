/*
 * PreLevelParser.java
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
 *
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.parser;

import java.util.StringTokenizer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.util.Logging;

/**
 * @author wardc
 * 
 */
public class PreLevelParser extends AbstractPrerequisiteParser implements
        PrerequisiteParserInterface
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]
		{ "LEVEL" };
	}

	@Override
	public Prerequisite parse(String kind, String formula,
	        boolean invertResult, boolean overrideQualify)
	        throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult,
		        overrideQualify);

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
					throw new PersistenceLayerException(
					        "PRELEVEL must be either 'MIN=x', 'MAX=y' or 'MIN=x,MAX=y' where 'x' and 'y' are integers. '"
					                + formula + "' is not valid. ");

				}
				String token = vals[0];
				String hdVal = vals[1];
				try
				{
					Integer.parseInt(hdVal);
				}
				catch (NumberFormatException nfe)
				{
					throw new PersistenceLayerException(
					        "PRELEVEL must be either 'MIN=x', 'MAX=y' or 'MIN=x,MAX=y' where 'x' and 'y' are integers. '"
					                + formula
					                + "' is not valid: "
					                + hdVal
					                + " is not an integer");
				}
				if (token.equals("MIN"))
				{
					minPrereq.setKind("level");
					minPrereq.setOperator(PrerequisiteOperator.GTEQ);
					minPrereq.setOperand(hdVal);

					hasMin = true;

				}
				if (token.equals("MAX"))
				{
					maxPrereq.setKind("level");
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
			}
			else if (hasMin)
			{
				prereq = minPrereq;
			}
			else if (hasMax)
			{
				prereq = maxPrereq;
			}

		}
		else
		{
			processOldSyntax(formula, prereq);
		}

		if (invertResult)
		{
			prereq.setOperator(prereq.getOperator().invert());
		}
		return prereq;
	}

	/**
	 * @param formula
	 * @param prereq
	 * @throws PersistenceLayerException
	 */
	private void processOldSyntax(String formula, Prerequisite prereq)
	        throws PersistenceLayerException
	{

		Logging.deprecationPrint("Deprecated use of PRELEVEL found: ");
		Logging
		        .deprecationPrint("The PRELEVEL:x syntax is no longer supported. "
		                + "The new format is  'MIN=x', 'MAX=y', or 'MIN=x,MAX=y' where x and y are integers. "
		                + "Passed formala was: " + formula);

		try
		{
			int min = Integer.parseInt(formula);
			prereq.setKind("level");
			prereq.setOperand(Integer.toString(min));
			prereq.setOperator(PrerequisiteOperator.GTEQ);
		}
		catch (NumberFormatException nfe)
		{
			throw new PersistenceLayerException(
			        "PRELEVEL must be 'x' where 'x' is an integer. '" + formula
			                + "' is not valid: " + formula
			                + " is not an integer");
		}
	}
}
