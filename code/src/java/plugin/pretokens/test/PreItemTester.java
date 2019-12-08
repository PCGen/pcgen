/*
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.pretokens.test;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.Equipment;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.core.utils.CoreUtility;
import pcgen.system.LanguageBundle;

/**
 * Sets requirements for items a character must possess.
 */
public class PreItemTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

	// TODO Refactor this with all the equipment tests.
	@SuppressWarnings("PMD.OneDeclarationPerLine")
	@Override
	public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
		throws PrerequisiteException
	{
		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException e)
		{
			throw new PrerequisiteException(
				LanguageBundle.getFormattedString("PreItem.error.bad_operand", prereq.toString()), e); //$NON-NLS-1$
		}

		int runningTotal = 0;

		if (display.hasEquipment())
		{
			// Work out exactlywhat we are going to test.
			final String aString = prereq.getKey();
			List<String> typeList = null;
			if (aString.startsWith(Constants.LST_TYPE_EQUAL) || aString.startsWith(Constants.LST_TYPE_DOT))
			{
				String stripped = aString.substring(Constants.SUBSTRING_LENGTH_FIVE);
				typeList = CoreUtility.split(stripped, '.');
			}

			for (Equipment eq : display.getEquipmentSet())
			{
				if (typeList != null)
				{
					// Check to see if the equipment matches
					// all of the types in the requested list;
					boolean bMatches = true;
                    for (String s : typeList)
                    {
                        if (!eq.isType(s))
                        {
                            bMatches = false;
                            break;
                        }
                    }
					if (bMatches)
					{
						runningTotal++;
					}
				}
				else
				{ //not a TYPE string
					final String eqName = eq.getName().toUpperCase();

					if (aString.indexOf('%') >= 0)
					{
						//handle wildcards (always assume
						// they end the line)
						final int percentPos = aString.indexOf('%');
						final String substring = aString.substring(0, percentPos).toUpperCase();
						if ((eqName.startsWith(substring)))
						{
							++runningTotal;
							break;
						}
					}
					else if (eqName.equalsIgnoreCase(aString))
					{
						//just a straight String compare
						++runningTotal;
						break;
					}
				}
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	@Override
	public String kindHandled()
	{
		return "ITEM"; //$NON-NLS-1$
	}

}
