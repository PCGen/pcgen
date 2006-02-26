/*
 * PreAttackParser.java
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
 * Created on 17-Dec-2003
 *
 * Current Ver: $Revision: 1.5 $
 *
 * Last Editor: $Author: frugal $
 *
 * Last Edited: $Date: 2004/01/11 19:52:19 $
 *
 */
package pcgen.persistence.lst.prereq;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 */
public class PreAttackParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{
	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrereqParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{ "ATT" };
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrereqParserInterface#parse(java.lang.String, java.lang.String, boolean)
	 */
	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);

		try
		{
			prereq.setOperand(Integer.toString(Integer.parseInt(formula)));
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Badly formed PREATT attribute: '" + formula + "' assuming '1'");
			prereq.setOperand("1");
		}
		
		if (invertResult) {
			prereq.setOperator( prereq.getOperator().invert());
		}
		return prereq;
	}
}
