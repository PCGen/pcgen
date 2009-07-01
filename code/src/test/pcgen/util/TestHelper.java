/**
 * TestHelper.java
 * Copyright 2005 (c) Andrew Wilson <nuance@sourceforge.net>
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
 * Last Editor:     $Author$
 * Last Edited:     $Date$
 *
 */

package pcgen.util;

import gmgen.pluginmgr.PluginLoader;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.SystemCollections;
import pcgen.core.WeaponProf;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbilityLoader;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.GenericLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * Helps Junit tests
 */
@SuppressWarnings("nls")
public class TestHelper
{
	private static boolean loaded = false;
	private static LstObjectFileLoader<Equipment> eqLoader = new GenericLoader<Equipment>(Equipment.class);
	private static LstObjectFileLoader<Ability>   abLoader = new AbilityLoader();
	private static CampaignSourceEntry source = null;

	/**
	 * Make some size adjustments
	 */
	public static void makeSizeAdjustments()
	{
		final String sizes =
				"Fine|Diminutive|Tiny|Small|Medium|Large|Huge|Gargantuan|Colossal";
		final StringTokenizer aTok = new StringTokenizer(sizes, "|");
		GameMode gamemode = SystemCollections.getGameModeNamed("3.5");
		if (gamemode == null)
		{
			gamemode = new GameMode("3.5");
			SystemCollections.addToGameModeList(gamemode);
		}
		SettingsHandler.setGame("3.5");
		while (aTok.hasMoreTokens())
		{
			final String name = aTok.nextToken();
			final String abb  = name.substring(0, 1);

			final SizeAdjustment sa = new SizeAdjustment();

			sa.setName(name);
			sa.put(StringKey.ABB, abb);

			gamemode.addToSizeAdjustmentList(sa);
			Globals.getContext().ref.importObject(sa);
			Globals.getContext().ref.registerAbbreviation(sa, sa.getAbbreviation());
		}
		gamemode.getSizeAdjustmentNamed("Medium").put(ObjectKey.IS_DEFAULT_SIZE, true);
	}
	
	/**
	 * Make some equipment
	 * @param input Equipment source line to be parsed
	 * @return true if OK
	 */
	public static boolean makeEquipment(final String input)
	{
		if (!loaded)
		{
			loadPlugins();
		}
		try
		{
			final CampaignSourceEntry source;
			try
			{
				source = new CampaignSourceEntry(new Campaign(),
						new URI("file:/" + TestHelper.class.getName() + ".java"));
			}
			catch (URISyntaxException e)
			{
				throw new UnreachableError(e);
			}
			eqLoader.parseLine(Globals.getContext(), null, input, source);
			return true;
		}
		catch (Exception e)
		{
			// TODO Deal with Exception?
		}
		return false;
	}

	/**
	 * Load the plugins
	 */
	public static void loadPlugins()
	{
		final PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
		loaded = true;
	}

	/**
	 * Load and initialise the properties, plugins and GameModes
	 */
	public static void loadAll()
	{
		SettingsHandler.readOptionsProperties();
		SettingsHandler.getOptionsFromProperties(null);

		loadPlugins();
		SettingsHandler.initGameModes();
		SettingsHandler.getGame().clearLoadContext();
	}

	/**
	 * Get the field related to a name
	 * @param aClass The class to search for the field
	 * @param fieldName the field to search for
	 * @return the field related to a name in the class
	 */
    public static Object findField(final Class<?> aClass, final String fieldName)
	{
		try
		{
			Class<?> clazz = aClass;
			while (true)
			{
				for (final Field f : Arrays.asList(clazz.getDeclaredFields()))
				{
					if (f.getName().equals(fieldName))
					{
						f.setAccessible(true);
						return f;
					}
				}
				if (!"Object".equals(clazz.getName()))
				{
					clazz = clazz.getSuperclass();
				}
				else
				{
					break;
				}
			}

		}
		catch (SecurityException e)
		{
			System.out.println(e);
		}
		return null;
	}

	/**
	 * Set the important info about a Skill
	 * @param name The skill name
	 * @param type The type info ("." separated)
	 * @param stat The key stat
	 * @param untrained Can this be used untrained
	 * @param armorCheck should an armor check penalty be applied
	 */
	public static void makeSkill(
            final String name,
            final String type,
            final PCStat stat,
		    final boolean untrained,
            final SkillArmorCheck armorCheck)
	{
		final Skill aSkill = new Skill();
		aSkill.setName(name);
		aSkill.put(StringKey.KEY_NAME, ("KEY_" + name));
		addType(aSkill, type);
		aSkill.put(ObjectKey.KEY_STAT, stat);
		aSkill.put(ObjectKey.USE_UNTRAINED, untrained);
		aSkill.put(ObjectKey.ARMOR_CHECK, armorCheck);
		Globals.getContext().ref.importObject(aSkill);
	}

	/**
	 * Set the important info about a Skill
	 * @param name The skill name
	 * @param cat the category of this Ability
	 * @param type The type info ("." separated)
	 * @return The ability (which has also been added to global storage
	 */
	public static Ability makeAbility(final String name, final String cat, final String type)
	{
		AbilityCategory useCat = SettingsHandler.getGame()
				.silentlyGetAbilityCategory(cat);
		if (useCat == null)
		{
			useCat = new AbilityCategory(cat);
			SettingsHandler.getGame().addAbilityCategory(useCat);
		}
		final Ability anAbility = new Ability();
		anAbility.setName(name);
		anAbility.put(StringKey.KEY_NAME, ("KEY_" + name));
		anAbility.setCDOMCategory(useCat);
		addType(anAbility, type);
		Globals.addAbility(anAbility);
		return anAbility;
	}

	/**
	 * Set the important info about a Skill
	 * @param name The skill name
	 * @param cat the category of this Ability
	 * @param type The type info ("." separated)
	 * @return The ability (which has also been added to global storage
	 */
	public static Ability makeAbility(final String name, final AbilityCategory cat, final String type)
	{
		final Ability anAbility = new Ability();
		anAbility.setName(name);
		anAbility.put(StringKey.KEY_NAME, ("KEY_" + name));
		anAbility.setCDOMCategory(cat);
		addType(anAbility, type);
		Globals.addAbility(anAbility);
		Globals.getContext().ref.importObject(anAbility);
		return anAbility;
	}

	/**
	 * Make an ability
     *
	 * @param input the Ability source string to parse and create the ability from
	 * @return true if OK
	 */
	public static boolean makeAbilityFromString(final String input)
	{
		if (!loaded)
		{
			loadPlugins();
		}

		try
		{
			if (null == source)
			{
				try
				{
					source = new CampaignSourceEntry(new Campaign(),
							new URI("file:/" + TestHelper.class.getName() + ".java"));
				}
				catch (URISyntaxException e)
				{
					throw new UnreachableError(e);
				}
			}

			abLoader.parseLine(Globals.getContext(), null, input, source);
			return true;
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getLocalizedMessage());
		}
		return false;
	}

	
	
	/**
	 * Set the important info about a WeaponProf
	 * @param name The weaponprof name
	 * @param type The type info ("." separated)
	 * @return The weapon prof (which has also been added to global storage
	 */
	public static WeaponProf makeWeaponProf(final String name, final String type)
	{
		final WeaponProf aWpnProf = new WeaponProf();
		aWpnProf.setName(name);
		aWpnProf.put(StringKey.KEY_NAME, ("KEY_" + name));
		addType(aWpnProf, type);
		Globals.getContext().ref.importObject(aWpnProf);
		return aWpnProf;
	}

	/**
	 * Set the important info about a Race
	 * @param name The race name
	 * @return The race (which has also been added to global storage)
	 */
	public static Race makeRace(final String name)
	{
		final Race aRace = new Race();
		aRace.setName(name);
		aRace.put(StringKey.KEY_NAME, ("KEY_" + name));

		try
		{
			final BonusObj bon = Bonus.newBonus("FEAT|POOL|1");
			final PreParserFactory factory = PreParserFactory.getInstance();
			final Prerequisite prereq = factory.parse("PREDEFAULTMONSTER:N");
			bon.addPrerequisite(prereq);
			bon.setCreatorObject(aRace);

			aRace.addToListFor(ListKey.BONUS, bon);
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("Caught " + e);
		}

		Globals.getContext().ref.importObject(aRace);
		return aRace;
	}

	/**
	 * Set the important info about a Class
	 * @param name The race name
	 * @return The race (which has also been added to global storage)
	 */
	public static PCClass makeClass(final String name)
	{
		final PCClass aClass = new PCClass();
		aClass.setName(name);
		aClass.put(StringKey.KEY_NAME, ("KEY_" + name));

		Globals.getContext().ref.importObject(aClass);
		return aClass;
	}

	/**
	 * Set the important info about a Spell
	 * @param name The spell name
	 * @return The spell (which has also been added to global storage)
	 */
	public static Spell makeSpell(final String name)
	{
		final Spell aSpell = new Spell();
		aSpell.setName(name);
		aSpell.put(StringKey.KEY_NAME, ("KEY_" + name));

		Globals.getContext().ref.importObject(aSpell);
		return aSpell;
	}

	/**
	 * Set the important info about a Template
	 * @param name The template name
	 * @return The template (which has also been added to global storage)
	 */
	public static PCTemplate makeTemplate(final String name)
	{
		final PCTemplate aTemplate = new PCTemplate();
		aTemplate.setName(name);
		aTemplate.put(StringKey.KEY_NAME, ("KEY_" + name));

		Globals.getContext().ref.importObject(aTemplate);
		return aTemplate;
	}
	
	/**
     * Get the Ability Category of the Ability object passed in.  If it does
     * not exist in the game mode, a new object wil be created and added to
     * the game mode
     *
	 * @param ability an ability in the AbilityCategory we want to retrieve
	 * @return the AbilityCategory
	 */
	public static AbilityCategory getAbilityCategory(final Ability ability)
	{
		AbilityCategory aCategory =
				SettingsHandler.getGame().getAbilityCategory(ability.getCategory());
		if (aCategory == null)
		{
			aCategory = new AbilityCategory(ability.getCategory());
			SettingsHandler.getGame().addAbilityCategory(aCategory);
		}
		return aCategory;
	}

	public static void addType(CDOMObject cdo, String string)
	{
		List<String> stringList = Arrays.asList(string.split("\\."));
		for (String s : stringList)
		{
			cdo.addToListFor(ListKey.TYPE, Type.getConstant(s));
		}
	}

	public static void setAlignment(PlayerCharacter pc, final int index, final boolean bLoading)
	{
		PCAlignment al = SettingsHandler.getGame().getAlignmentAtIndex(index);
		pc.setAlignment(al, bLoading, false);
	}

	public static void setAlignment(PlayerCharacter pc, final int index, final boolean bLoading,
			final boolean bForce)
	{
		PCAlignment al = SettingsHandler.getGame().getAlignmentAtIndex(index);
		pc.setAlignment(al, bLoading, bForce);
	}

	/**
	 * Checks to see if this PC has the weapon proficiency key aKey
	 * 
	 * @param aKey
	 * @return boolean
	 */
	public static boolean hasWeaponProfKeyed(PlayerCharacter pc,
			final String aKey)
	{
		WeaponProf wp = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(WeaponProf.class, aKey);
		return wp != null && pc.hasWeaponProf(wp);
	}

}
