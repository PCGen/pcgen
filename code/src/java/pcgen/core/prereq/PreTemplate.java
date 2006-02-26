/*
 * PreTemplate.java
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
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision: 1.11 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:37 $
 *
 */
package pcgen.core.prereq;

import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.util.PropertyFactory;

import java.util.Iterator;


/**
 * @author wardc
 *
 */
public class PreTemplate extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) throws PrerequisiteException {
		int runningTotal = 0;

        final int number;
        try {
            number = Integer.parseInt(prereq.getOperand());
        }
        catch (NumberFormatException exceptn) {
            throw new PrerequisiteException(PropertyFactory.getFormattedString("PreTemplate.error", prereq.toString())); //$NON-NLS-1$
        }

        if (!character.getTemplateList().isEmpty())
		{

			String templateName = prereq.getKey().toUpperCase();
			final int wildCard = templateName.indexOf('%');
			//handle wildcards (always assume they end the line)
			if (wildCard >= 0)
			{
				templateName = templateName.substring(0, wildCard);
				for (Iterator ti = character.getTemplateList().iterator(); ti.hasNext();)
				{
					final PCTemplate aTemplate = (PCTemplate) ti.next();
					if (aTemplate.getName().toUpperCase().startsWith(templateName))
					{
						runningTotal++;
					}
				}
			}
			else if (character.getTemplateNamed(templateName) != null)
			{
				runningTotal++;
			}
		}
        runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "TEMPLATE"; //$NON-NLS-1$
	}

}
