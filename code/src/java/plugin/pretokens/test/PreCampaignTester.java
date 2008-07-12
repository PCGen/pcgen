/*
 * PreCampaignTester.java
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
 * Created on 12/07/2008 12:38:47
 *
 * $Id: $
 */
package plugin.pretokens.test;

import java.net.URI;
import java.util.List;

import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.persistence.PersistenceManager;
import pcgen.util.PropertyFactory;

/**
 * The Class <code>PreCampaignTester</code> is responsible for testing if the 
 * currently loaded sources satisfy the campaign prerequisite.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class PreCampaignTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		int runningTotal = 0;
		Campaign campaign = Globals.getCampaignKeyed(prereq.getKey());
		if (campaign != null)
		{
			if (campaign.getKeyName().equals(prereq.getKey())
				&& campaign.isLoaded())
			{
				++runningTotal;
			}
			else
			{
				List<URI> selCampaigns =
						PersistenceManager.getInstance()
							.getChosenCampaignSourcefiles();
				for (URI element : selCampaigns)
				{
					final Campaign aCampaign =
							Globals.getCampaignByURI(element);

					if (campaign.equals(aCampaign))
					{
						++runningTotal;
					}
				}
			}
		}
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "CAMPAIGN"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		final String foo = PropertyFactory.getFormattedString(
				"PreCampaign.toHtml", //$NON-NLS-1$
				new Object[] { prereq.getOperator().toDisplayString(),
						prereq.getOperand(), prereq.getKey() });
		return foo;
	}

}
