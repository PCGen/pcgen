/*
 * LevelAbilitySpecial.java
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
 * Created on July 24, 2001, 11:24 PM
 */
package pcgen.core.levelability;

import pcgen.core.*;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.util.chooser.ChooserInterface;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a special ability that a character gets when gaining a level
 * (an ADD:SPECIAL entry in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision$
 */
final class LevelAbilitySpecial extends LevelAbility
{
	LevelAbilitySpecial(final PObject aowner, final int aLevel, final String aList)
	{
		super(aowner, aLevel, aList);
	}

	List getChoicesList(String choices, final PlayerCharacter aPC)
	{
		choices = choices.substring(choices.lastIndexOf('(') + 1);

		if (choices.lastIndexOf(")") >= (choices.length() - 2))
		{
			choices = choices.substring(0, choices.lastIndexOf(")"));
		}

		final List aList = super.getChoicesList(choices, aPC);
		Collections.sort(aList);

		return aList;
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
		c.setTitle("Special Ability Choice");

		return rawTagData;
	}

	/**
	 * Process the choice selected by the user.
	 * @param aArrayList
	 * @param selectedList
	 * @param aPC
	 * @param pcLevelInfo
	 */
	public boolean processChoice(
			final List            aArrayList,
			final List            selectedList,
			final PlayerCharacter aPC,
			final PCLevelInfo     pcLevelInfo)
	{
		if (selectedList.size() == 0)
		{
			return true;
		}

		for (int i = 0; i < selectedList.size(); ++i)
		{
			String selection = selectedList.get(i).toString();

			if ("Free Feat".equals(selection))
			{
				aPC.adjustFeats(1);
				continue;
			}

			final SpecialAbility sa;

			if (this.owner instanceof PCClass)
			{
				String source = "PCCLASS|" + this.owner.getName() + "|" + level();
				sa = new SpecialAbility(selection, source);
			}
			else
			{
				sa = new SpecialAbility(selection);
			}

			this.owner.addSpecialAbilityToList(sa);
			this.owner.addSave(selection);
		}
		return true;
	}

	/**
	 * Processes a single token in the comma-separated list of the ADD:
	 * field and adds the choices to be shown in the list to anArrayList.
	 *
	 * @param  aToken the token to be processed.
	 * @param  anArrayList the list to add the choice to.
	 * @param  aPC the PC this Level ability is adding to.
	 */
	void processToken(
			final String          aToken,
			final List            anArrayList,
			final PlayerCharacter aPC)
	{

		if ("FEATLIST".equals(aToken))
		{
			for (Iterator it = Globals.getAbilityKeyIterator("FEAT"); it.hasNext(); ) {
				final Ability anAbility = (Ability) it.next();

				if (aPC.canSelectAbility(anAbility))
				{
					anArrayList.add(anAbility.getKeyName());
				}
			}
		}
		else if ("Free Feat".equals(aToken))
		{
			anArrayList.add(aToken);
		}
		else if (aToken.startsWith("FEATTYPE=") || aToken.startsWith("FEATTYPE."))
		{
			final String featType = aToken.substring(9);
			anArrayList.addAll(aPC.getAvailableFeatNames(featType));
		}
		else if (aToken.startsWith("FEAT=") || aToken.startsWith("FEAT."))
		{
			String anAbilityName  = aToken.substring(5);
			final Ability anAbility = Globals.getAbilityNamed("FEAT", anAbilityName);

			if (aPC.canSelectAbility(anAbility))
			{
				anArrayList.add(anAbility.getKeyName());
			}
		}
		else
		{
			final List aList = aPC.getSpecialAbilityListStrings();

			if (!aList.contains(aToken))
			{
				anArrayList.add(aToken);
			}
		}
	}
}
