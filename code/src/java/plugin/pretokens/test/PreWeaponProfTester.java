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
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.util.CControl;
import pcgen.core.Deity;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.WeaponProf;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.output.channel.ChannelUtilities;
import pcgen.system.LanguageBundle;

public class PreWeaponProfTester extends AbstractDisplayPrereqTest
{

	/**
	 * <b>Tag Name</b>: {@code PREWEAPONPROF:x,y,y}<br>
	 * &nbsp; <b>Variables Used (x)</b>: <i>Number</i> 
	 * (The number of proficiencies that must match the specified requirements). <br>
	 * &nbsp; <b>Variables Used (y)</b>: <i>Text</i> (The name of a weapon proficiency). <br>
	 * &nbsp; <b>Variables Used (y)</b>: {@code TYPE.}<i>Text</i> (The name of a weaponprof type). <br>
	 * &nbsp; <b>Variables Used (y)</b>: {@code DEITYWEAPON} (The favored weapon of the character's deity). <br>
	 * <p>
	 * <b>What it does:</b><br>
	 * &nbsp; Sets weapon proficiency requirements.
	 * <p>
	 * <b>Examples</b>: <br>
	 * &nbsp; {@code PREWEAPONPROF:2,Kama,Katana}<br>
	 * &nbsp; &nbsp; Character must have both "Kama" and "Katana".
	 * <p>
	 * &nbsp; {@code PREWEAPONPROF:1,TYPE.Exotic} <br>
	 * &nbsp; &nbsp; Character must have proficiency with any one exotic weaponprof type.
	 * <p>
	 * &nbsp; {@code PREWEAPONPROF:1,TYPE.Martial,Chain (Spiked)} <br>
	 * &nbsp; &nbsp; Character must have proficiency with either the Chain (Spiked) or any martial weapon.
	 * <p>
	 * &nbsp; {@code PREWEAPONPROF:1,DEITYWEAPON} <br>
	 * &nbsp; &nbsp; Weapon Prof in question must be one of the chosen deity's favored weapons.
	 */
	@Override
	public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
		throws PrerequisiteException
	{
		int runningTotal = 0;

		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(
				LanguageBundle.getFormattedString("PreFeat.error", prereq.toString()), exceptn); //$NON-NLS-1$
		}

		final String aString = prereq.getKey();
		Deity deity = (Deity) ChannelUtilities
			.readControlledChannel(display.getCharID(), CControl.DEITYINPUT);
		if ("DEITYWEAPON".equals(aString) && (deity != null)) //$NON-NLS-1$
		{
			List<CDOMReference<WeaponProf>> dwp = deity.getSafeListFor(ListKey.DEITYWEAPON);
			DEITYWPN: for (CDOMReference<WeaponProf> ref : dwp)
			{
				for (WeaponProf wp : ref.getContainedObjects())
				{
					if (display.hasWeaponProf(wp))
					{
						runningTotal++;
						break DEITYWPN;
					}
				}
			}
		}
		else if (aString.startsWith("TYPE.") || aString.startsWith("TYPE=")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			final String requiredType = aString.substring(5);
			for (WeaponProf wp : display.getWeaponProfSet())
			{
				if (wp.isType(requiredType))
				{
					runningTotal++;
				}
				else
				{
					final Equipment eq = Globals.getContext().getReferenceContext()
						.silentlyGetConstructedCDOMObject(Equipment.class, wp.getKeyName());
					if (eq != null)
					{
						if (eq.isType(requiredType))
						{
							runningTotal++;
						}
					}
				}
			}
		}
		else
		{
			WeaponProf wp = Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(WeaponProf.class, aString);
			if ((wp != null && display.hasWeaponProf(wp)))
			{
				runningTotal++;
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

	@Override
	public String kindHandled()
	{
		return "WEAPONPROF"; //$NON-NLS-1$
	}

}
