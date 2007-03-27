/*
 * PreSpellTypeParser.java
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
import pcgen.util.Logging;

import java.util.StringTokenizer;

/**
 * @author wardc
 *
 */
public class PreSpellTypeParser extends AbstractPrerequisiteParser implements
		PrerequisiteParserInterface
{
	private final static String prereqKind = "spell.type";

	public String[] kindsHandled()
	{
		return new String[]{"SPELLTYPE"};
	}

	@Override
	public Prerequisite parse(String kind, String formula,
		boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq =
				super.parse(kind, formula, invertResult, overrideQualify);
		prereq.setKind(prereqKind);

		// old-style: PRESPELLTYPE:<name of spell type>,<number of spells required>,<minimum spell level>
		// new-style: PRESPELLTYPE:<number of spells required>,<name of spell type>=<minimum spell level>[,<name of spell type>=<minimum spell level>,...]
		//
		boolean bError = false;
		final StringTokenizer aTok = new StringTokenizer(formula, ",");
		String aString = aTok.nextToken();
		try
		{
			Integer.parseInt(aString); // to test if number

			if (aTok.hasMoreTokens())
			{
				Prerequisite subreq;
				prereq.setOperand(aString);
				final int totalTokens = aTok.countTokens();
				if (totalTokens > 1)
				{
					prereq.setKind(null); // PREMULT
				}
				while (aTok.hasMoreTokens())
				{
					aString = aTok.nextToken();
					final int eqIdx = aString.indexOf('=');
					if (eqIdx < 0)
					{
						bError = true;
						break;
					}

					if (totalTokens == 1)
					{
						subreq = prereq;
					}
					else
					{
						subreq = new Prerequisite();
						prereq.addPrerequisite(subreq);
						subreq.setKind(prereqKind);
						subreq.setOperand("1");
						subreq.setCountMultiples(true);
					}
					subreq.setKey(aString.substring(0, eqIdx));
					subreq.setSubKey(aString.substring(eqIdx + 1));
					subreq.setOperator(PrerequisiteOperator.GTEQ);
				}
			}
			else
			{
				bError = true;
			}
		}
		catch (NumberFormatException nfe)
		{
			//
			// Must be exactly 3 tokens. Old-style did not support multiple options
			//
			Logging.errorPrint("Deprecated use of PRESPELLTYPE found: "
					+ formula);
			Logging.errorPrint("The new format is "
					+ "<number of spells required>,<name of spell type>"
					+ "=<minimum spell level>"
					+ "[,<name of spell type>=<minimum spell level>,...]");
			if (aTok.countTokens() == 2)
			{
				String[] types = aString.split("\\|");
				String operand = aTok.nextToken();
				String subKey = aTok.nextToken();

				if (types.length == 1)
				{
					prereq.setKey(aString);
					prereq.setSubKey(subKey);
					prereq.setOperator(PrerequisiteOperator.GTEQ);
					prereq.setOperand(operand);
				}
				else
				{
					prereq.setKind(null);

					for (int i = 0; i < types.length; i++)
					{
						Prerequisite subreq = new Prerequisite();
						prereq.addPrerequisite(subreq);
						subreq.setKind("spell.type");
						subreq.setKey(types[i]);
						subreq.setSubKey(subKey);
						subreq.setOperator(PrerequisiteOperator.GTEQ);
						subreq.setOperand(operand);
					}
				}
				
			}
			else
			{
				bError = true;
			}
		}
		if (bError)
		{
			throw new PersistenceLayerException("PRE" + kindsHandled()[0]
				+ " formula '" + formula + "' is not valid.");
		}

		if (invertResult)
		{
			prereq.setOperator(prereq.getOperator().invert());
		}
		return prereq;
	}
}
