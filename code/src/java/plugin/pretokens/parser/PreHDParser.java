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
public class PreHDParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{
	public String[] kindsHandled()
	{
		return new String[]{ "HD" };
	}

	@Override
	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);

		/*
		 * either PREHD:xxx+ or PREHD:xxx-yyy
		 * with xxx being the minimum requirement
		 * and yyy being the maximum requirement
		 *
		 * author: Thomas Behr 13-03-02
		 */
		String[] tokens = formula.split("\\+|-");

		try
		{
			if ((tokens.length == 1) || ((tokens.length == 2) && tokens[1].equals("")))
			{
				int min = Integer.parseInt(tokens[0]);

				prereq.setOperand(Integer.toString(min));
				prereq.setOperator(PrerequisiteOperator.GTEQ);
			}
			else if (tokens.length == 2)
			{
				int min = Integer.parseInt(tokens[0]);
				int max = Integer.parseInt(tokens[1]);

				Prerequisite minPrereq = new Prerequisite();
				minPrereq.setKind("hd");
				minPrereq.setOperator(PrerequisiteOperator.GTEQ);
				minPrereq.setOperand(Integer.toString(min));

				Prerequisite maxPrereq = new Prerequisite();
				maxPrereq.setKind("hd");
				maxPrereq.setOperator(PrerequisiteOperator.LTEQ);
				maxPrereq.setOperand(Integer.toString(max));

				prereq.setKind(null);		// PREMULT
				prereq.setOperand("2");
				prereq.addPrerequisite(minPrereq);
				prereq.addPrerequisite(maxPrereq);
			}
			else
			{
				throw new PersistenceLayerException(
				    "PREHD must be either 'x+' or 'x-y' where 'x' and 'y' are integers. '" + formula
				    + "' is not valid ");
			}
		}
		catch (NumberFormatException nfe)
		{
			throw new PersistenceLayerException("PREHD must be either 'x+' or 'x-y' where 'x' and 'y' are integers. '"
			    + formula + "' is not valid ");
		}
		if (invertResult) {
			prereq.setOperator( prereq.getOperator().invert());
		}
		return prereq;
	}
}
