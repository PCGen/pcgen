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
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.util.CControl;
import pcgen.core.Deity;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.output.channel.ChannelUtilities;
import pcgen.output.channel.compat.AlignmentCompat;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

public class PreAlignTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

	@Override
	public int passes(final Prerequisite prereq, final Equipment equipment, final PlayerCharacter pc)
		throws PrerequisiteException
	{
		if (pc == null)
		{
			return 0;
		}
		return passes(prereq, pc, equipment);
	}

	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter pc, CDOMObject source)
		throws PrerequisiteException
	{
		//
		// If game mode doesn't support alignment, then pass the prereq
		//
		int runningTotal = 0;

		if (!pc.isFeatureEnabled(CControl.ALIGNMENTFEATURE))
		{
			runningTotal = 1;
		}
		else
		{
			String desiredAlignment = prereq.getKey();
			final PCAlignment charAlignment = AlignmentCompat.getCurrentAlignment(pc.getCharID());

			if (prereq.getOperator().equals(PrerequisiteOperator.EQ))
			{
				if (alignMatches(pc.getDisplay(), desiredAlignment, charAlignment))
				{
					runningTotal++;
				}
			}
			else if (prereq.getOperator().equals(PrerequisiteOperator.NEQ))
			{
				if (!alignMatches(pc.getDisplay(), desiredAlignment, charAlignment))
				{
					runningTotal++;
				}
			}
			else
			{
				throw new PrerequisiteException(LanguageBundle.getFormattedString(
					"PreAlign.error.invalidComparison", //$NON-NLS-1$
					prereq.getOperator().toString(), prereq.toString()));
			}
		}

		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Check if the character's alignment matches the requirement.
	 * 
	 * @param display The character to test
	 * @param desiredAlignment The alignment to be found
	 * @param charAlignment The character's alignment
	 * @return true if the alignment matches, false if not.
	 */
	private static boolean alignMatches(final CharacterDisplay display, String desiredAlignment,
		final PCAlignment charAlignment)
	{
		PCAlignment al = getPCAlignment(desiredAlignment);
		if (al.equals(charAlignment))
		{
			return true;
		}
		else if (desiredAlignment.equalsIgnoreCase("Deity"))
		{
			Deity deity = (Deity) ChannelUtilities.readControlledChannel(
				display.getCharID(), CControl.DEITYINPUT);
			if (deity != null)
			{
				CDOMSingleRef<PCAlignment> deityAlign = deity.get(ObjectKey.ALIGNMENT);
				return (deityAlign != null) && charAlignment.equals(deityAlign.get());
			}
		}
		return false;
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	@Override
	public String kindHandled()
	{
		return "align"; //$NON-NLS-1$
	}

	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		String alignment = prereq.getKey();
		PCAlignment al = getPCAlignment(alignment);
		return LanguageBundle.getFormattedString(
			"PreAlign.toHtml", prereq.getOperator().toDisplayString(), //$NON-NLS-1$
			al.getKeyName());
	}

	private static PCAlignment getPCAlignment(String desiredAlignIdentifier)
	{
		PCAlignment desiredAlign = Globals.getContext().getReferenceContext()
			.silentlyGetConstructedCDOMObject(PCAlignment.class, desiredAlignIdentifier);
		if (desiredAlign == null)
		{
			Logging.errorPrint("Unable to find alignment that matches: " + desiredAlignIdentifier);
		}
		return desiredAlign;
	}
}
