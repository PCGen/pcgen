/*
 * LevelAbilityList.java
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
 * Created on July 24, 2001, 12:36 PM
 */
package pcgen.core.levelability;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.util.chooser.ChooserInterface;

import java.util.*;

/**
 * Represents an option list that a character gets when gaining a level
 * (an ADD:LIST entry in the LST file).
 *
 * @author Dmitry Jemerov <yole@spb.cityline.ru>
 * @version $Revision$
 */
final class LevelAbilityList extends LevelAbility
{
	private List aBonusList;
	private List aChoiceList;
	private int cnt = 0;

	LevelAbilityList(final PObject aowner, final int aLevel, final String aList)
	{
		super(aowner, aLevel, aList);
	}

	List getChoicesList(final String bString, final PlayerCharacter aPC)
	{
		aChoiceList = new ArrayList();
		aBonusList = new ArrayList();

		return super.getChoicesList(bString.substring(5), aPC);
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
		c.setTitle("Option List");

		return rawTagData;
	}

	/**
	 * Process the choice selected by the user.
	 * @param selectedList
	 * @param aPC
	 * @param pcLevelInfo
	 * @param aArrayList
	 */
	public boolean processChoice(
			final List            aArrayList,
			final List            selectedList,
			final PlayerCharacter aPC,
			final PCLevelInfo     pcLevelInfo)
	{
		for (int index = 0; index < selectedList.size(); ++index)
		{
			cnt = aArrayList.indexOf(selectedList.get(index).toString());

			String theChoice = null;
			String theBonus;
			final List selectedBonusList = new ArrayList();
			final String prefix = cnt + "|";

			for (Iterator e = aBonusList.iterator(); e.hasNext();)
			{
				theBonus = (String) e.next();

				if (theBonus.startsWith(prefix))
				{
					theBonus = theBonus.substring((cnt / 10) + 2);
					selectedBonusList.add(theBonus);
				}
			}

			for (Iterator e = aChoiceList.iterator(); e.hasNext();)
			{
				theChoice = (String) e.next();

				if (theChoice.startsWith(prefix))
				{
					theChoice = theChoice.substring((cnt / 10) + 9);

					break;
				}

				theChoice = "";
			}

			if ((theChoice != null) && (theChoice.length() > 0))
			{
				owner.getChoices(theChoice, selectedBonusList, aPC);
			}
			else if (selectedBonusList.size() > 0)
			{
				for (Iterator e1 = selectedBonusList.iterator(); e1.hasNext();)
				{
					owner.applyBonus((String) e1.next(), "", aPC);
				}
			}
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
		final StringTokenizer cTok = new StringTokenizer(aToken, "[]", false);

		try
		{
			anArrayList.add(cTok.nextToken());
		}
		catch (NoSuchElementException e)
		{
			//do nothing, TODO add debug logger and log condition
		}

		while (cTok.hasMoreTokens())
		{
			final String bTokString = cTok.nextToken();
			final String aString = new StringBuffer(cnt).append(String.valueOf(cnt)).append("|").append(bTokString)
				.toString();

			if (bTokString.startsWith("CHOOSE:"))
			{
				aChoiceList.add(aString);
			}

			if (bTokString.startsWith("BONUS:"))
			{
				aBonusList.add(aString);
			}
		}

		cnt++;
	}
}
