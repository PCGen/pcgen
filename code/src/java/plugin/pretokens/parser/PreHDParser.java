/*
 * PreHitDiceParser.java
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

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * @author wardc
 *
 */
public class PreHDParser extends AbstractPrerequisiteParser implements
		PrerequisiteParserInterface
{
	public String[] kindsHandled()
	{
		return new String[]{"HD"};
	}

	@Override
	public Prerequisite parse(String kind, String formula,
		boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq =
				super.parse(kind, formula, invertResult, overrideQualify);

		int plusLoc = formula.indexOf('+');
		if (plusLoc == -1)
		{
			int minusLoc = formula.indexOf('-');
			if (minusLoc == -1)
			{
				throw new PersistenceLayerException(
					"PREHD must be either 'x+' or 'x-y' where 'x' and 'y' are integers. '"
						+ formula
						+ "' is not valid: It does not have either a + or a -");
			}
			if (minusLoc != formula.lastIndexOf('-'))
			{
				throw new PersistenceLayerException(
					"PREHD must be either 'x+' or 'x-y' where 'x' and 'y' are integers. '"
						+ formula + "' is not valid: It has more than one -");
			}
			String hd1String = formula.substring(0, minusLoc);
			String hd2String = formula.substring(minusLoc + 1);
			try
			{
				Integer.parseInt(hd1String);
			}
			catch (NumberFormatException nfe)
			{
				throw new PersistenceLayerException(
					"PREHD must be either 'x+' or 'x-y' where 'x' and 'y' are integers. '"
						+ formula + "' is not valid: " + hd1String
						+ " is not an integer");
			}
			try
			{
				Integer.parseInt(hd2String);
			}
			catch (NumberFormatException nfe)
			{
				throw new PersistenceLayerException(
					"PREHD must be either 'x+' or 'x-y' where 'x' and 'y' are integers. '"
						+ formula + "' is not valid: " + hd2String
						+ " is not an integer");
			}

			Prerequisite minPrereq = new Prerequisite();
			minPrereq.setKind("hd");
			minPrereq.setOperator(PrerequisiteOperator.GTEQ);
			minPrereq.setOperand(hd1String);

			Prerequisite maxPrereq = new Prerequisite();
			maxPrereq.setKind("hd");
			maxPrereq.setOperator(PrerequisiteOperator.LTEQ);
			maxPrereq.setOperand(hd2String);

			prereq.setKind(null); // PREMULT
			prereq.setOperand("2");
			prereq.addPrerequisite(minPrereq);
			prereq.addPrerequisite(maxPrereq);
		}
		else
		{
			if (plusLoc != formula.length() - 1)
			{
				throw new PersistenceLayerException(
					"PREHD must be either 'x+' or 'x-y' where 'x' and 'y' are integers. '"
						+ formula
						+ "' has a + but does not end with it: It is not valid");
			}
			String hdString = formula.substring(0, plusLoc);
			try
			{
				int min = Integer.parseInt(hdString);
				prereq.setOperand(Integer.toString(min));
				prereq.setOperator(PrerequisiteOperator.GTEQ);
			}
			catch (NumberFormatException nfe)
			{
				throw new PersistenceLayerException(
					"PREHD must be either 'x+' or 'x-y' where 'x' and 'y' are integers. '"
						+ formula + "' is not valid: " + hdString
						+ " is not an integer");
			}
		}

		if (invertResult)
		{
			prereq.setOperator(prereq.getOperator().invert());
		}
		return prereq;
	}
}
