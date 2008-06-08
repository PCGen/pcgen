/*
 * LevelAbilityClassSkills.java
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
 * Created on Jul 27, 2001, 12:13:37 AM
 *
 * $Id$
 */
package pcgen.core.levelability;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.*;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Represents class skills the character gains when going up a level (an
 * ADD:CLASSSKILLS line in the LST file).
 *
 * @author   Dmitry Jemerov <yole@spb.cityline.ru>
 * @version  $Revision$
 */
final class LevelAbilityClassSkills extends LevelAbility
{
	private static final int CHOICETYPE_ANY          = -1;
	private static final int CHOICETYPE_NONE         = 0;
	private static final int CHOICETYPE_UNTRAINED    = 1;
	private static final int CHOICETYPE_TRAINED      = 2;
	private static final int CHOICETYPE_EXCLUSIVE    = 3;
	private static final int CHOICETYPE_NONEXCLUSIVE = 4;
	private static final int CHOICETYPE_CROSSCLASS   = 5;
	private static final int CHOICETYPE_BYTYPE       = 6;
	private int              autoRank                = 0;

	LevelAbilityClassSkills(final PObject aOwner, final int aLevel, final String aList)
	{
		/*
		 * TODO Why does this allow something other than PCClass as an owner, when 
		 * it will create an artificial PCClass later on if the owner is not a PCClass?
		 * That seems like it will only introduce bugs later on in the system...
		 */
		super(aOwner, aLevel, aList);
	}

	/**
	 * Generates the list of tokens to be shown in the chooser from the list of
	 * skills of given type.
	 *
	 * @param   bString
	 * @param   aPC
	 *
	 * @return  list of choices
	 */
	List<String> getChoicesList(final String bString, final PlayerCharacter aPC)
	{
		final List<String> aArrayList = new ArrayList<String>();

		final StringTokenizer aTok       = new StringTokenizer(
				rawTagData.substring(
					rawTagData.indexOf('(') + 1,
					rawTagData.lastIndexOf(')')),
				",",
				false);
		int                   choiceType;
		String                skillType  = "";
		Skill                 aSkill;
		autoRank = 0;

		final PCClass theClass;

		if (owner instanceof PCClass)
		{
			theClass = (PCClass) owner;
		}
		else
		{
			theClass = new PCClass();
		}

		while (aTok.hasMoreTokens())
		{
			choiceType = CHOICETYPE_NONE;

			final String toAdd = aTok.nextToken();

			if ("UNTRAINED".equals(toAdd))
			{
				choiceType = CHOICETYPE_UNTRAINED;
			}
			else if ("TRAINED".equals(toAdd))
			{
				choiceType = CHOICETYPE_TRAINED;
			}
			else if ("EXCLUSIVE".equals(toAdd))
			{
				choiceType = CHOICETYPE_EXCLUSIVE;
			}
			else if ("NONEXCLUSIVE".equals(toAdd))
			{
				choiceType = CHOICETYPE_NONEXCLUSIVE;
			}
			else if ("CROSSCLASSSKILLS".equals(toAdd))
			{
				choiceType = CHOICETYPE_CROSSCLASS;
			}
			else if ("ANY".equals(toAdd))
			{
				choiceType = CHOICETYPE_ANY;
			}
			else if (toAdd.startsWith("TYPE=") || toAdd.startsWith("TYPE."))
			{
				skillType  = toAdd.substring(5);
				choiceType = CHOICETYPE_BYTYPE;
			}
			else if (toAdd.startsWith("AUTORANK=") || toAdd.startsWith("AUTORANK."))
			{
				try
				{
					autoRank = Integer.parseInt(toAdd.substring(9));
				}
				catch (NumberFormatException exc)
				{
					Logging.errorPrint("Will use default for autoRank: " + autoRank, exc);
				}
			}
			else
			{
				aSkill = Globals.getContext().ref.silentlyGetConstructedCDOMObject(Skill.class, toAdd);

				if ((aSkill != null) && !aSkill.isClassSkill(theClass, aPC))
				{
					aArrayList.add(aSkill.getKeyName());
				}
			}

			if (choiceType == CHOICETYPE_NONE)
			{
				continue;
			}

			for ( Skill skill : Globals.getContext().ref.getConstructedCDOMObjects(Skill.class) )
			{
				//
				// Already a class skill--no point in making it one again
				//
				if (skill.isClassSkill(theClass, aPC))
				{
					continue;
				}

				switch (choiceType)
				{
					case CHOICETYPE_UNTRAINED:

						if (skill.getSafe(ObjectKey.USE_UNTRAINED))
						{
							break;
						}

						continue;

					case CHOICETYPE_TRAINED:

						if (!skill.getSafe(ObjectKey.USE_UNTRAINED))
						{
							break;
						}

						continue;

					case CHOICETYPE_EXCLUSIVE:

						if (skill.getSafe(ObjectKey.EXCLUSIVE))
						{
							break;
						}

						continue;

					case CHOICETYPE_NONEXCLUSIVE:

						if (!skill.getSafe(ObjectKey.EXCLUSIVE))
						{
							break;
						}

						continue;

					case CHOICETYPE_CROSSCLASS:

						if (!skill.getSafe(ObjectKey.EXCLUSIVE))
						{
							break;
						}

						continue;

					case CHOICETYPE_ANY:
						break;

					case CHOICETYPE_BYTYPE:

						if (skill.isType(skillType))
						{
							break;
						}

						continue;

					default:
						Logging.errorPrint(
							"Impossible choice in LevelAbilityClassSkills." +
							"getChoicesList() :" +
							choiceType);

						break;
				}

				aArrayList.add(skill.getKeyName());
			}
		}

		return aArrayList;
	}

	/**
	 * Performs the initial setup of a chooser.
	 *
	 * @param   chooser
	 * @param   aPC
	 *
	 * @return  String
	 */
	String prepareChooser(final ChooserInterface chooser, PlayerCharacter aPC)
	{
		super.prepareChooser(chooser, aPC);

		final StringTokenizer aTok = new StringTokenizer(rawTagData, "|", false);

		try
		{
			chooser.setTitle(aTok.nextToken());
		}
		catch (NoSuchElementException ignore)
		{
			// do nothing since the hasMoreTokens will return false
		}

		if (aTok.hasMoreTokens())
		{
			final String s = aTok.nextToken();

			if (s.equalsIgnoreCase("ALL"))
			{
				chooser.setPickAll(true);
			}
			else
			{
				chooser.setTotalChoicesAvail(Integer.parseInt(s));
			}
		}

		return rawTagData;
	}

	/**
	 * Process the choice selected by the user.
	 * @param  selectedList
	 * @param  aPC
	 * @param  pcLevelInfo
	 * @param  aArrayList
	 */
	public boolean processChoice(
		final List<String>           aArrayList,
		final List<String>            selectedList,
		final PlayerCharacter aPC,
		final PCLevelInfo     pcLevelInfo)
	{
		for ( String nString : selectedList )
		{
			if ((owner instanceof PCClass) && !((PCClass) owner).hasSkill(nString))
			{
				((PCClass) owner).addSkillToList(nString);

				if (autoRank != 0)
				{
					Skill aSkill = Globals.getContext().ref.silentlyGetConstructedCDOMObject(Skill.class, nString);

					if (aSkill != null)
					{
						if (aPC != null)
						{
							aSkill = aPC.addSkill(aSkill);
							aSkill.modRanks(1.0, (PCClass) owner, true, aPC);
						}
					}
				}
			}
		}

		addAllToAssociated(selectedList);
		return true;
	}
}
