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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;


/**
 * The guts of chooser moved from PObject
 *
 * @author   Andrew Wilson
 * @version  $Revision$
 */

public class ChooserUtilities
{
	private static Map<String, String>     classLookup    = null;
	private static boolean mapconstructed = false;

	/**
	 * Construct the choices for a SPELLLEVEL chooser
	 * @param  availList
	 * @param  uniqueList
	 * @param  aPC
	 * @param  elements
	 */
	static public final void buildSpellTypeChoices(
		final List<String>            availList,
		final List<String>            uniqueList,
		final PlayerCharacter aPC,
		Enumeration<String>           elements)
	{
		elements.nextElement(); // should be SPELLLEVEL

		while (elements.hasMoreElements())
		{
			String aString = elements.nextElement();

			while (
				!aString.startsWith("CLASS=") &&
				!aString.startsWith("CLASS.") &&
				!aString.startsWith("TYPE=") &&
				!aString.startsWith("TYPE.") &&
				elements.hasMoreElements())
			{
				aString = elements.nextElement();
			}

			if (!elements.hasMoreElements())
			{
				break;
			}

			boolean endIsUnique = false;

			int minLevel = 1;

			try
			{
				minLevel = Integer.parseInt(elements.nextElement());
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Badly formed minLevel token: " + aString);
			}

			String mString = elements.nextElement();

			if (mString.endsWith(".A"))
			{
				endIsUnique = true;
				mString     = mString.substring(0, mString.lastIndexOf(".A"));
			}

			int maxLevel = minLevel;

			if (aString.startsWith("CLASS=") || aString.startsWith("CLASS."))
			{
				final PCClass aClass = aPC.getClassKeyed(aString.substring(6));

				if (mString.indexOf("MAXLEVEL") >= 0)
				{
					int maxLevelVal = calcMaxSpellLevel(aClass);
					mString = mString.replaceAll("MAXLEVEL", String
						.valueOf(maxLevelVal));
				}
				maxLevel = aPC.getVariableValue(mString, "").intValue();

				if (aClass != null)
				{
					// TODO check this
					final String prefix = aClass.getKeyName() + " ";

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
						if (mString.indexOf("MAXLEVEL") >= 0)
						{
							int maxLevelVal = calcMaxSpellLevel(aClass,
								aString, aPC);
							mString = mString.replaceAll("MAXLEVEL", String
								.valueOf(maxLevelVal));
						}
						maxLevel = aPC.getVariableValue(mString, "").intValue();

						// TODO check this
						final String prefix = aClass.getKeyName() + " ";

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
	 * Calculate the maximum level of spell that is castable by the
	 * PC for the supplied Class. If the class is not restricted in
	 * what spells can be cast, its limits on known spells will be checked instead.
	 *
	 * @param aClass The class to be checked.
	 * @return The highest level spell castable
	 */
	private static int calcMaxSpellLevel(final PCClass aClass)
	{
		if (aClass == null)
		{
			return 0;
		}

		int j = -1;
		final int aLevel = aClass.getLevel();

		if (aLevel >= 0)
		{ // some classes, like "Domain" are level 0, so this index would
			// be -1

			j = aClass.getCastListForLevel(aLevel).size() - 1;
			if (j == -1)
			{
				j = aClass.getKnownListForLevel(aLevel).size() - 1;
			}
		}
		return j;
	}

	/**
	 * Calculate the maximum level of spell that is castable by the
	 * PC for the supplied Class. If the class is not restricted in
	 * what spells can be cast, its limits on known spells will be checked instead.
	 *
	 * @param aClass The class to be checked.
	 * @param aType The class type to be checked.
	 * @param aPC The character to be checked.
	 * @return The highest level spell castable
	 */
	private static int calcMaxSpellLevel(final PCClass aClass,
		final String aType, PlayerCharacter aPC)
	{
		if (aClass == null)
		{
			return 0;
		}

		int maxLevel = -1;
		int aLevel = aClass.getLevel();
		aLevel += (int) aPC.getTotalBonusTo("PCLEVEL", aClass.getKeyName());
		aLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + aType);

		if (aLevel >= 0)
		{  // some classes, like "Domain" are level 0,
			// so this index would be -1
			maxLevel = aClass.getCastListForLevel(aLevel).size() - 1;
			if (maxLevel == -1)
			{
				maxLevel = aClass.getKnownListForLevel(aLevel).size() - 1;
			}
		}

		return maxLevel;
	}

	/**
	 * Deal with CHOOSE tag processing
	 *
	 * @param aPObject
	 * @param availableList
	 * @param selectedList
	 * @param process
	 * @param aPC
	 * @param addIt
	 *
	 * @return true if aPObject was modified
	 */
	public static final boolean modChoices(
			final PObject         aPObject,
				  List            availableList,
			final List            selectedList,
			final boolean         process,
			final PlayerCharacter aPC,
			final boolean         addIt)
	{
		availableList.clear();
		selectedList.clear();

		ChoiceManagerList aMan = getChoiceManager(aPObject, "", aPC);

		if (aMan == null) {return false;}

		aMan.getChoices(aPC, availableList, selectedList);

		if (!process) {return false;}

		if (availableList.size() > 0 || selectedList.size() > 0)
		{
			if (addIt)
			{
				final List newSelections = aMan.doChooser(aPC, availableList, selectedList);
				aMan.applyChoices(aPC, newSelections);
			}
			else
			{
				aMan.doChooserRemove(aPC, availableList, selectedList);
			}
			return true;
		}
		return false;
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
		classLookup = new HashMap<String, String>();
		classLookup.put("ARMORTYPE",            ArmorTypeChoiceManager.class.getName());
		classLookup.put("CSKILLS",              ClassSkillsChoiceManager.class.getName());
		classLookup.put("DOMAIN",               DomainChoiceManager.class.getName());
		classLookup.put("EQUIPTYPE",            EquipmentTypeChoiceManager.class.getName());
		classLookup.put("FEATADD",              FeatAddChoiceManager.class.getName());
		classLookup.put("SINGLEFEAT",           FeatChoiceManager.class.getName());
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
		classLookup.put("PROFICIENCY",          ProficiencyChoiceManager.class.getName());
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
		//classLookup.put("SPELLLEVEL",           SimpleSpellLevelChoiceManager.class.getName());
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
		if(theChoices != null && theChoices.length() > 0)
		{
			choiceString = theChoices;
		}
		else
		{
			choiceString = aPObject.getChoiceString();
		}

		if (choiceString == null || choiceString.length() == 0)
		{
			return null;
		}

		List mainList = Arrays.asList(choiceString.split("[|]"));

		/* Find the first element of the array that does not contain an
		 * equals sign, this is the type of chooser.
		 */
		int i = 0;
		while (i <= mainList.size() - 1 &&
				((String) mainList.get(i)).indexOf("=") > 0 &&
				!(((String) mainList.get(i)).startsWith("FEAT=") ||
						((String) mainList.get(i)).startsWith("FEAT.")))
		{
			i++;
		}

		/* Use the name of the chooser to look up the full canonical
		 * class name of the ChoiceManager that handles that type of chooser
		 */
		String type = (i >= mainList.size()) ? "MISC" : (String) mainList.get(i);

		if (type.startsWith("FEAT=") || type.startsWith("FEAT."))
		{
			type = "SINGLEFEAT";
		}

		String className = classLookup.get(type);

		if (className == null)
		{
			if (Globals.weaponTypesContains(type))
			{
				type      = "WEAPONPROF";
				className = classLookup.get(type);
			}
			else
			{
				type      = "MISC";
				className = classLookup.get(type);
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
			Logging.errorPrint("Can't create Choice Manager: " + type + " Class not found", e);
		}
		catch (InstantiationException e) {
			Logging.errorPrint("Can't create Choice Manager: " + type + " Can't instantiate class", e);
		}
		catch (IllegalAccessException e) {
			Logging.errorPrint("Can't create Choice Manager: " + type + " Illegal access", e);
		}
		catch (NoSuchMethodException e)  {
			Logging.errorPrint("Can't create Choice Manager: " + type + " no constructor found", e);
		}
		catch (InvocationTargetException e) {
			Logging.errorPrint("Can't create Choice Manager: " + type + " class threw an error", e);
		}
		return null;
	}
}
