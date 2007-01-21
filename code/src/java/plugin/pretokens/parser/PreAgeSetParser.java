/*
 * PreAgeSetParser.java
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
 * Created on December 30, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.pretokens.parser;

import pcgen.core.Globals;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * @author perchrh
 *
 */
public class PreAgeSetParser extends AbstractPrerequisiteListParser
implements PrerequisiteParserInterface
{

	public String[] kindsHandled() {
		return new String[]{"AGESET", "AGESETEQ", "AGESETGT", "AGESETGTEQ", "AGESETLT", 
				"AGESETLTEQ", "AGESETNEQ"}; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrereqParserInterface#parse(java.lang.String)
	 */
	@Override
	public Prerequisite parse(String kind, String formula,
			boolean invertResult, boolean overrideQualify)
	throws PersistenceLayerException
	{
		
		Prerequisite prereq =
			super.parse(kind, formula, invertResult, overrideQualify);

		//Operand should be either an integer or a recognizable String
		try{
			Integer.parseInt(formula);
		}
		catch (NumberFormatException exc){
			prereq.setOperand(formula); //assume recognizable String for now
		}

		return prereq;
	
	}

}