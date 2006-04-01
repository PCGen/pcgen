/*
 * ChooserUtilities.java
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
 * Current Version: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */

package pcgen.core.chooser;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.Delta;
import pcgen.util.Logging;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * The guts of chooser moved from PObject
 *
 * @author   Andrew Wilson
 * @version  $Revision$
 */

public class ChooserUtilities
{
	private static Map     classLookup    = null;
	private static boolean mapconstructed = false;

	/**
	 * Construct the choices for a SPELLLEVEL chooser
	 * @param  availList
	 * @param  uniqueList
	 * @param  aPC
	 * @param  elements
	 */
	static public final void buildSpellTypeChoices(
		final List            availList,
		final List            uniqueList,
		final PlayerCharacter aPC,
		Enumeration           elements)
	{
		elements.nextElement(); // should be SPELLLEVEL

		while (elements.hasMoreElements())
		{
			String aString = (String) elements.nextElement();

			while (
				!aString.startsWith("CLASS=") &&
				!aString.startsWith("CLASS.") &&
				!aString.startsWith("TYPE=") &&
				!aString.startsWith("TYPE.") &&
				elements.hasMoreElements())
			{
				aString = (String) elements.nextElement();
			}

			if (!elements.hasMoreElements())
			{
				break;
			}

			boolean endIsUnique = false;

			int minLevel = 1;

			try
			{
				minLevel = Integer.parseInt((String) elements.nextElement());
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Badly formed minLevel token: " + aString);
			}

			String mString = (String) elements.nextElement();

			if (mString.endsWith(".A"))
			{
				endIsUnique = true;
				mString     = mString.substring(0, mString.lastIndexOf(".A"));
			}

			int maxLevel = minLevel;

			if (aString.startsWith("CLASS=") || aString.startsWith("CLASS."))
			{
				final PCClass aClass = aPC.getClassKeyed(aString.substring(6));
				int           i      = 0;

				while (i < mString.length())
				{
					if (
						(mString.length() > (7 + i)) &&
						"MAXLEVEL".equals(mString.substring(i, i + 8)))
					{
						int       j      = -1;
						final int aLevel = aClass.getLevel() - 1;

						if (aLevel >= 0)
						{ // some classes, like "Domain" are level 0, so this index would
						  // be -1

							final String          tempString = aClass
								.getCastStringForLevel(aLevel);
							final StringTokenizer bTok       = new StringTokenizer(
									tempString,
									",");
							j = bTok.countTokens() - 1;
						}

						String bString = "";

						if (mString.length() > (i + 8))
						{
							bString = mString.substring(i + 8);
						}

						// mString = mString.substring(0, i) + new Integer(j).toString()
						// + bString;
						mString = mString.substring(0, i) + String.valueOf(j) + bString;
						--i; // back up one since we just did a replacement
					}

					++i;
				}

				maxLevel = aPC.getVariableValue(mString, "").intValue();

				if (aClass != null)
				{
					final String prefix = aClass.getName() + " ";

					for (int j = minLevel; j <= maxLevel; ++j)
					{
						final String bString = prefix + j;

						if (!availList.contains(bString))
						{
							availList.add(bString);
						}

						if ((j == maxLevel) && endIsUnique)
						{
							uniqueList.add(bString);
						}
					}
				}
			}

			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				aString = aString.substring(5);

				for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
				{
					final PCClass aClass = (PCClass) e.next();

					if (aClass.getSpellType().equals(aString))
					{
						if (mString.startsWith("MAXLEVEL"))
						{
							int aLevel = aClass.getLevel();

							aLevel += (int) aPC.getTotalBonusTo(
									"PCLEVEL",
									aClass.getName());
							aLevel += (int) aPC.getTotalBonusTo(
									"PCLEVEL",
									"TYPE." + aString);

							String bString = "0";

							if (aLevel >= 0) // some classes, like "Domain" are level 0,
											 // so this index would be -1
							{
								bString = aClass.getCastStringForLevel(aLevel);
							}

							if ("0".equals(bString))
							{
								maxLevel = -1;
							}
							else
							{
								final StringTokenizer bTok = new StringTokenizer(
										bString,
										",");
								maxLevel = bTok.countTokens() - 1;
							}

							if (mString.length() > 8)
							{
								mString = mString.substring(8);
								maxLevel += Delta.decode(mString).intValue();
							}
						}

						final String prefix = aClass.getName() + " ";

						for (int i = minLevel; i <= maxLevel; ++i)
						{
							final String bString = prefix + i;

							if (!availList.contains(bString))
							{
								availList.add(bString);
							}

							if ((i == maxLevel) && endIsUnique)
							{
								uniqueList.add(bString);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Creates a list of choices based on aChoice, or if aChoice is blank, the
	 * choiceString property of aPObject.  If process is true, a chooser will be
	 * presented to the user.  Otherwise, availableList will be populated.
	 *
	 * @param  aPObject
	 * @param  aChoice
	 * @param  availableList
	 * @param  selectedList
	 * @param  aPC
	 */
	public static void getChoices(
		final PObject         aPObject,
		String                aChoice,
		final List            availableList,
		final List            selectedList,
		final PlayerCharacter aPC)
	{
		String choiceString = aPObject.getChoiceString();

		if (
			!choiceString.startsWith("FEAT|")      &&
			!choiceString.startsWith("ARMORPROF")  &&
			!choiceString.startsWith("SPELLLEVEL") &&
			!aChoice.startsWith("SPELLLEVEL")      &&
			!aChoice.startsWith("WEAPONPROF")      &&
			!aChoice.startsWith("SHIELDPROF"))
		{
			return;
		}

		ChoiceManagerList aMan = getChoiceManager(aPObject, aChoice, aPC);

		aMan.getChoices(aPC, availableList, selectedList);

		if (availableList.size() + selectedList.size() == 0)
		{
			return;
		}

		final List newSelections = aMan.doChooser(aPC,
				                                  availableList,
				                                  selectedList);

		aMan.applyChoices(aPC, newSelections);
	}
	/**
	 * Make a mapping so that we can look up the name of the class that
	 * implements a given ChoiceManager for  specific type of Chooser.
	 *
	 */
	private static void constructMap()
	{
		classLookup = new HashMap();
		classLookup.put("ARMORTYPE",            ArmorTypeChoiceManager.class.getName());
		classLookup.put("CSKILLS",              ClassSkillsChoiceManager.class.getName());
		classLookup.put("DOMAIN",               DomainChoiceManager.class.getName());
		classLookup.put("EQUIPTYPE",            EquipmentTypeChoiceManager.class.getName());
		classLookup.put("FEATADD",              FeatAddChoiceManager.class.getName());
		classLookup.put("FEAT",                 FeatChoiceManager.class.getName());
		classLookup.put("FEATLIST",             FeatListChoiceManager.class.getName());
		classLookup.put("FEATSELECT",           FeatSelectChoiceManager.class.getName());
		classLookup.put("HP",                   HPChoiceManager.class.getName());
		classLookup.put("RACE",                 RaceChoiceManager.class.getName());
		classLookup.put("SALIST",               SAListChoiceManager.class.getName());
		classLookup.put("SCHOOLS",              SchoolsChoiceManager.class.getName());
		classLookup.put("SKILLIST",             SkillListChoiceManager.class.getName());
		classLookup.put("CCSKILLIST",           SkillListCrossClassChoiceManager.class.getName());
		classLookup.put("MISC",                 MiscChoiceManager.class.getName());
		classLookup.put("NONCLASSSKILLLIST",    SkillListNonClassChoiceManager.class.getName());
		classLookup.put("SKILLS",               SkillsChoiceManager.class.getName());
		classLookup.put("SKILLSNAMED",          SkillsNamedChoiceManager.class.getName());
		classLookup.put("SKILLSNAMEDTOCCSKILL", SkillsNamedToCCSkillChoiceManager.class.getName());
		classLookup.put("SKILLSNAMEDTOCSKILL",  SkillsNamedToCSkillChoiceManager.class.getName());
		classLookup.put("SPELLCLASSES",         SpellClassesChoiceManager.class.getName());
		classLookup.put("SPELLLEVEL",           SpellLevelChoiceManager.class.getName());
		classLookup.put("SPELLLIST",            SpellListChoiceManager.class.getName());
		classLookup.put("SPELLS",               SpellsChoiceManager.class.getName());
		classLookup.put("STAT",                 StatChoiceManager.class.getName());
		classLookup.put("WEAPONFOCUS",          WeaponFocusChoiceManager.class.getName());
		classLookup.put("WEAPONPROFS",          WeaponProfChoiceManager.class.getName());
		classLookup.put("WEAPONPROFTYPE",       WeaponProfTypeChoiceManager.class.getName());

		classLookup.put("ARMORPROF",            SimpleArmorProfChoiceManager.class.getName());
		classLookup.put("FEAT",                 SimpleFeatChoiceManager.class.getName());
		classLookup.put("SHIELDPROF",           SimpleShieldProfChoiceManager.class.getName());
		classLookup.put("SPELLLEVEL",           SimpleSpellLevelChoiceManager.class.getName());
		classLookup.put("WEAPONPROF",           SimpleWeaponProfChoiceManager.class.getName());
		mapconstructed = true;
	}

	/**
	 * Make a ChoiceManager Object for the chooser appropriate for
	 * aPObject.getChoiceString();
	 *
	 * @param aPObject
	 * @param theChoices
	 * @param aPC
	 *
	 * @return an initialised ChoiceManager
	 */
	public static ChoiceManagerList getChoiceManager (
			PObject         aPObject,
			String          theChoices,
			PlayerCharacter aPC)
	{
		if (!mapconstructed)
		{
			constructMap();
		}

		final String choiceString;
		if(theChoices!= null && theChoices.length() > 0)
		{
			choiceString = theChoices;
		}
		else
		{
			choiceString = aPObject.getChoiceString();
			theChoices   = "";
		}

		List mainList = Arrays.asList(choiceString.split("[|]"));

		/* Find the first element of the array that does not contain an
		 * equals sign, this is the type of chooser.
		 */
		int i = 0;
		while (i <= mainList.size() - 1 && ((String) mainList.get(i)).indexOf("=") > 0)
		{
			i++;
		}

		/* Use the name of the chooser to look up the full canonical
		 * class name of the ChoiceManager that handles that type of chooser
		 */
		String type      = (i >= mainList.size()) ? "MISC" : (String) mainList.get(i++);
		String className = (String) classLookup.get(type);

		if (className == null)
		{
			if (Globals.weaponTypesContains(type))
			{
				type      = "WEAPONPROF";
				className = (String) classLookup.get(type);
			}
			else
			{
				type      = "MISC";
				className = (String) classLookup.get(type);
			}
		}

		/* Construct and return the ChoiceManager */
		try {
			Class    aClass     = Class.forName(className);
			Class[]  argsClass  = {
					PObject.class,
					choiceString.getClass(),
					aPC.getClass()
					};
			Object[] argsObject = {aPObject, choiceString, aPC};

			Constructor constructor  = aClass.getConstructor(argsClass);
			ChoiceManagerList cm = (ChoiceManagerList) constructor.newInstance(argsObject);
			return cm;
		}
		catch (ClassNotFoundException e) {
			Logging.errorPrint("Can't create Choice Manager: " + type + " Class not found");
		}
		catch (InstantiationException e) {
			Logging.errorPrint("Can't create Choice Manager: " + type + " Can't instantiate class");
		}
		catch (IllegalAccessException e) {
			Logging.errorPrint("Can't create Choice Manager: " + type + " Illegal access");
		}
		catch (NoSuchMethodException e)  {
			Logging.errorPrint("Can't create Choice Manager: " + type + " no constructor found");
		}
		catch (InvocationTargetException e) {
			Logging.errorPrint("Can't create Choice Manager: " + type + " class threw an error");
			Logging.errorPrint(e.getCause().toString());
		}
		return null;
	}
}
