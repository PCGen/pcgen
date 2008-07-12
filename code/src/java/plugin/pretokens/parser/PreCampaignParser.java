/*
 * PreCampaignParser.java
 * Copyright 2008 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 12/07/2008 12:03:09
 *
 * $Id: $
 */
package plugin.pretokens.parser;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.AbstractPrerequisiteListParser;
import pcgen.persistence.lst.prereq.PrerequisiteParserInterface;

/**
 * The Class <code>PreCampaignParser</code> is responsible for 
 * parsing a PRECAMPAIGN tag and generating a Prerequisite object 
 * based on that tag.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class PreCampaignParser extends AbstractPrerequisiteListParser implements
		PrerequisiteParserInterface
{
	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.prereq.PrerequisiteParserInterface#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{"CAMPAIGN"};
	}

	/**
	 * Parse the pre req list
	 * 
	 * @param kind 
	 * @param formula 
	 * @param invertResult 
	 * @param overrideQualify 
	 * @return PreReq 
	 * @throws PersistenceLayerException 
	 */
	@Override
	public Prerequisite parse(String kind, String formula,
		boolean invertResult, boolean overrideQualify)
		throws PersistenceLayerException
	{

		Prerequisite prereq =
				super.parse(kind, formula, invertResult, overrideQualify);
		setNoNeedForChar(prereq);
		return prereq;
	}
	

	/**
	 * Flag each Prerequisite created to indicate that no character is 
	 * required to successfully test the Prerequisite. The function is 
	 * recursive to handle a single Prerequisite that gets split out 
	 * into a premult.
	 * 
	 * @param prereq the new no need for char
	 */
	private void setNoNeedForChar(Prerequisite prereq)
	{
		if (prereq == null)
		{
			return;
		}
		prereq.setCharacterRequired(false);

		for (Prerequisite element : prereq.getPrerequisites())
		{
			setNoNeedForChar(element);
		}
	}
	
}
