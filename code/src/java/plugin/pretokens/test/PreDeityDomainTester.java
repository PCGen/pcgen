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

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.output.channel.compat.DeityCompat;
import pcgen.system.LanguageBundle;

public class PreDeityDomainTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

	@Override
	public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
	{
		int runningTotal = 0;

		Deity deity = DeityCompat.getCurrentDeity(display.getCharID());
		if (deity != null)
		{
			if (deity.hasObjectOnList(Deity.DOMAINLIST, Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(Domain.class, prereq.getKey())))
			{
				runningTotal++;
			}
		}
		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	@Override
	public String kindHandled()
	{
		return "DEITYDOMAIN"; //$NON-NLS-1$
	}

	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		return LanguageBundle.getFormattedString(
			"PreDeityDomain.toHtml", prereq.getOperator().toDisplayString(), //$NON-NLS-1$
			prereq.getKey());
	}

}
