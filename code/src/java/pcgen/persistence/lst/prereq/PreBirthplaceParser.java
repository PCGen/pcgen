/*
 * PreBirthplaceParser.java
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
 * Current Ver: $Revision: 1.6 $
 *
 * Last Editor: $Author: byngl $
 *
 * Last Edited: $Date: 2005/10/03 13:54:50 $
 *
 */
package pcgen.persistence.lst.prereq;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;

/**
 * @author wardc
 *
 */
public class PreBirthplaceParser extends AbstractPrerequisiteParser implements PrerequisiteParserInterface
{
	public String[] kindsHandled()
	{
		return new String[]{ "BIRTHPLACE" };
	}

	public Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{
		Prerequisite prereq = super.parse(kind, formula, invertResult, overrideQualify);
		prereq.setKey(formula);
		prereq.setOperator(PrerequisiteOperator.EQ);
		
		if (invertResult) {
			prereq.setOperator( prereq.getOperator().invert());
		}
		return prereq;
	}
}
