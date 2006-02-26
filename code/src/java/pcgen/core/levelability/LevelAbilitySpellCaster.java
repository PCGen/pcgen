/*
 * LevelAbilitySpellCaster.java
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
 * Current Version: $Revision: 1.13 $
 * Last Editor: $Author: nuance $
 * Last Edited: $Date: 2005/10/27 00:41:05 $
 *
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */

package pcgen.core.levelability;

import pcgen.core.*;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserInterface;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;



/**
 * Class for adding a level of a spellcasting class to a character as part of
 * levelling up.
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision: 1.13 $
 */

public class LevelAbilitySpellCaster extends LevelAbility
{
	/**
	 * Creates a new LevelAbilitySpellCaster object.
	 *
	 * @param  aOwner
	 * @param  aLevel
	 * @param  tagData
	 */
	LevelAbilitySpellCaster(PObject aOwner, int aLevel, String tagData)
	{
		super(aOwner, aLevel, tagData);
	}

	/**
	 * Performs the initial setup of a chooser.
	 *
	 * @param   chooser
	 * @param aPC
	 *
	 * @return  String
	 */

	String prepareChooser(final ChooserInterface chooser, PlayerCharacter aPC)
	{
		setNumberofChoices(chooser, aPC);

		chooser.setTitle("Spell Caster Class Choice");

		return rawTagData;
	}

	/**
	 * Set the type property of this LevelAbility
	 *
	 * @param  aPC
	 */

	protected void setType(final PlayerCharacter aPC)
	{
		type = SPELLCASTER;
	}

	/**
	 * Process the choice selected by the user.
	 *
	 * @param  anArrayList
	 * @param  selectedList
	 * @param  aPC
	 * @param  pcLevelInfo
	 */

	public boolean processChoice(
		final List            anArrayList,
		final List            selectedList,
		final PlayerCharacter aPC,
		final PCLevelInfo     pcLevelInfo)
	{
		if (selectedList.size() > 0)
		{
			final String className = selectedList.get(0).toString();
			PCClass      theClass  = aPC.getClassNamed(className);

			if (theClass == null)
			{
				theClass = Globals.getClassNamed(className);

				if (theClass == null)
				{
					Logging.errorPrint(
						"ERROR:Expected PC to have a class named " + className);

					return true;
				}

				aPC.incrementClassLevel(0, theClass);
				theClass = aPC.getClassNamed(className);
			}

			owner.addBonusList("0|PCLEVEL|" + theClass.getKeyName() + "|1");

			// owner.addSave("BONUS|0|PCLEVEL|" + bClass.getKeyName() + "|1");
			// to force spellbook update
			// for divine spellcasters

			theClass.setLevel(theClass.getLevel(), aPC);

			addAllToAssociated(selectedList);
		}
		return true;
	}

	/**
	 * Processes a single token in the comma-separated list of the ADD: field
	 * and adds the choices to be shown to the ArrayList.
	 *
	 * @param  aToken        the token to be processed.
	 * @param  anArrayList   the list to add the choice to.
	 * @param  aPC           the PC this Level ability is adding to.
	 */

	void processToken(
		final String          aToken,
		final List            anArrayList,
		final PlayerCharacter aPC)
	{
		String token = aToken;
		final String  casterName = token.substring(
				token.lastIndexOf('(') + 1,
				token.lastIndexOf(')'));

		// If the SPELLCASTER bit is still there, strip it off
		if (aToken.startsWith("SPELLCASTER("))
		{
			token = casterName;
		}

		final PCClass namedClass = Globals.getClassNamed(casterName);

		if (namedClass != null)
		{
			token = namedClass.getName();
		}

		PCClass aClass;

		for (Iterator e1 = aPC.getClassList().iterator(); e1.hasNext();)
		{
			aClass = (PCClass) e1.next();

			if ("Domain".equals(aClass.getKeyName()))
			{
				continue; // cannot include domain class
			}

			// if the class has a valid spelltype and the class is not the owning
			// class
			else if (
				!"".equals(aClass.getSpellType()) &&
				!(Constants.s_NONE.equals(aClass.getSpellType())) &&
				!aClass.getKeyName().equals(owner.getKeyName()))
			{
				// if the string is ANY or if the string matches the class' spell
				// type
				if (
					(token.equals("ANY")) ||
					(token.equals(aClass.getSpellType())))
				{
					anArrayList.add(aClass.getKeyName());
				}
			}
		}

		if (token.startsWith("SPELLCASTER("))
		{
			token = token.substring(12, token.length() - 1);
		}

		final StringTokenizer aTok = new StringTokenizer(token, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();

			if (anArrayList.contains(aString))
			{
				continue;
			}

			if (
				aString.startsWith("EXCLUDE=") &&
				anArrayList.contains(aString.substring(8)))
			{
				anArrayList.remove(aString.substring(8));
			}

			aClass = Globals.getClassNamed(aString);

			if (aClass != null)
			{
				anArrayList.add(aClass.getKeyName());
			}
		}
	}
}
