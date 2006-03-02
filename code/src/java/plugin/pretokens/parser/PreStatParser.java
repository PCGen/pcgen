/*
 * PreStatLstParser.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Current Ver: $Revision: 1.12 $
 * Last Editor: $Author: byngl $
 * Last Edited: $Date: 2005/11/15 14:20:14 $
 *
 */
package plugin.pretokens.parser;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 */
public class PreStatParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{
	/**
	 *
	 */
	public PreStatParser()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrereqParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{ "STAT", "STATEQ", "STATGT", "STATGTEQ", "STATLT", "STATLTEQ", "STATNEQ" };
	}

	/**
	 * @param kind
	 * @param formula
	 * @param invertResult
	 * @param overrideQualify
	 * @return Prerequisite
	 * @throws PersistenceLayerException
	 */
	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		try
		{
			prereq.setKind(null);		// PREMULT

			// Get the comparator type STATGTEQ, STAT, STATNEQ etc.
			String compType = kind.substring(4).toLowerCase();

			if (compType.length() == 0)
			{
				compType = "gteq";
			}

			String[] tokens = formula.split(",|\\|");
			int currToken = 0;

			// Get the minimum match count
			String aString = tokens[currToken++];

			try
			{
				prereq.setOperand(Integer.toString(Integer.parseInt(aString)));
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Badly formed PRESTAT attribute: " + aString);
				prereq.setOperand("1");
			}

			while (currToken < tokens.length)
			{
				aString = tokens[currToken++];

				final int idxEquals = aString.lastIndexOf('=');
				if (idxEquals < 3)
				{
					throw new PersistenceLayerException("PRE" + kindsHandled()[0] + " formula '" + formula + "' is not valid.");
				}

				final String stat = aString.substring(0, Math.min(3, idxEquals));
				Prerequisite statPrereq = new Prerequisite();
				statPrereq.setKind("stat");
				statPrereq.setKey(stat);
				statPrereq.setOperator(compType);
				statPrereq.setOperand(aString.substring(idxEquals + 1));

				prereq.addPrerequisite(statPrereq);
			}

			if ((prereq.getPrerequisites().size() == 1) &&
				prereq.getOperator().equals(PrerequisiteOperator.GTEQ) &&
				prereq.getOperand().equals("1"))
			{
				prereq = (Prerequisite) prereq.getPrerequisites().get(0);
			}

			if (invertResult)
			{
				prereq.setOperator( prereq.getOperator().invert());
			}
		}
		catch (PrerequisiteException pe)
		{
			throw new PersistenceLayerException("Unable to parse the prerequisite :'" + kind + ":" + formula + "'. "+ pe.getLocalizedMessage());
		}
		return prereq;
	}
}
