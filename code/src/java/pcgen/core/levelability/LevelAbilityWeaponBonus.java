/**
 * LevelAbilityWeaponBonus.java
 * Copyright 2001 (C) Dmitry Jemerov
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
 * Created on Jul 26, 2001, 10:15:09 PM
 *
 * $Id$
 */
package pcgen.core.levelability;

import pcgen.core.*;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.util.chooser.ChooserInterface;

import java.util.*;

/**
 * Represents a weapon proficiency that a character gets when gaining a level
 * (an ADD:WEAPONBONUS entry in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision$
 */
final class LevelAbilityWeaponBonus extends LevelAbility
{
	private String bonusMod;
	private String bonusTag;

	LevelAbilityWeaponBonus(final PObject aowner, final int aLevel, final String aList)
	{
		super(aowner, aLevel, aList);
	}

	/**
	 * Parses the comma-separated list of the ADD: field and returns the
	 * list of tokens to be shown in the chooser.
	 * @param bString
	 * @param aPC
	 * @return choices list
	 */
	List<String> getChoicesList(final String bString, final PlayerCharacter aPC)
	{
		final List<String> aArrayList = new ArrayList<String>();
		final StringTokenizer bTok = new StringTokenizer(bString.substring(12), "|", false);
		bonusTag = bTok.nextToken();
		bonusMod = bTok.nextToken();

		while (bTok.hasMoreTokens())
		{
			final String cString = bTok.nextToken();

			if (cString.startsWith("PCFEAT=") || cString.startsWith("PCFEAT."))
			{
				final Ability aFeat = aPC.getFeatKeyed(cString.substring(7));

				if (aFeat != null)
				{
					for (int ii = 0; ii < aFeat.getAssociatedCount(); ++ii)
					{
						if (!aArrayList.contains(aFeat.getAssociated(ii)))
						{
							aArrayList.add(aFeat.getAssociated(ii));
						}
					}
				}
			}
			else if (cString.startsWith("FEAT=") || cString.startsWith("FEAT."))
			{
				final Ability anAbility = Globals.getAbilityKeyed("FEAT", cString.substring(5));

				if (anAbility != null)
				{
					final StringTokenizer aTok = new StringTokenizer(anAbility.getKeyName(), " ", false);
					final String aName = aTok.nextToken(); // first word of name should match type of weaponprof

					for (WeaponProf wp : Globals
							.getPObjectsOfType(
									Globals.getContext().ref
											.getConstructedCDOMObjects(WeaponProf.class),
									aName))
					{
						if (!aArrayList.contains(wp.getKeyName()))
						{
							aArrayList.add(wp.getKeyName());
						}
					}
				}
			}
			else if (cString.startsWith("ALL"))
			{
				for (WeaponProf wp : Globals.getContext().ref.getConstructedCDOMObjects(WeaponProf.class))
				{
					if (!aArrayList.contains(wp.getKeyName()))
					{
						aArrayList.add(wp.getKeyName());
					}
				}
			}
			else if (cString.startsWith("PCPROFLIST"))
			{
				for ( WeaponProf wp : aPC.getWeaponProfs() )
				{
					if (!aArrayList.contains(wp.getKeyName()))
					{
						aArrayList.add(wp.getKeyName());
					}
				}
			}
			else
			{
				final WeaponProf wp = Globals.getContext().ref.silentlyGetConstructedCDOMObject(WeaponProf.class, cString);

				if ((wp != null) && !aArrayList.contains(cString))
				{
					aArrayList.add(cString);
				}
			}
		}

		return aArrayList;
	}

	/**
	 * Performs the initial setup of a chooser.
	 * @param c
	 * @param aPC
	 * @return String
	 */
	String prepareChooser(final ChooserInterface c, PlayerCharacter aPC)
	{
		super.prepareChooser(c, aPC);
		c.setTitle("Select Weapon Prof");
		bonusTag = "";
		bonusMod = "";

		return rawTagData;
	}

	/**
	 * Process the choice selected by the user.
	 * @param selectedList
	 * @param aPC
	 * @param pcLevelInfo
	 * @param aArrayList
	 */
	public boolean processChoice(final List<String> aArrayList, final List<String> selectedList, final PlayerCharacter aPC, final PCLevelInfo pcLevelInfo)
	{
		final String bonusString = '|' + bonusTag + '|' + bonusMod;

		for ( String cString : selectedList )
		{
			final String weaponProfString = "WEAPONPROF=" + cString + bonusString;

			owner.addBonusList((new StringBuffer("0|").append(weaponProfString)).toString());
			owner.addSave((new StringBuffer("BONUS|0|").append(weaponProfString)).toString());
		}
		return true;
	}
}
