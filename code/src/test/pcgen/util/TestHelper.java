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
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.core.Skill;
import pcgen.core.SystemCollections;
import pcgen.core.WeaponProf;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbilityLoader;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.EquipmentLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * Helps Junit tests
 */
@SuppressWarnings("nls")
public class TestHelper
{
	private static boolean loaded = false;
	private static LstObjectFileLoader<Equipment> eqLoader = new EquipmentLoader();
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
			sa.setAbbreviation(abb);

			gamemode.addToSizeAdjustmentList(sa);
		}
		gamemode.getSizeAdjustmentNamed("Medium").setIsDefaultSize(true);
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
		final Equipment eq = new Equipment();
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
			eqLoader.parseLine(Globals.getContext(), eq, input, source);
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
            final String stat,
		    final boolean untrained,
            final SkillArmorCheck armorCheck)
	{
		GameMode gamemode = SettingsHandler.getGame();
		List<PCStat> statList = gamemode.getUnmodifiableStatList();
		int intLoc = gamemode.getStatFromAbbrev(stat);
		PCStat intStat = statList.get(intLoc);
		final Skill aSkill = new Skill();
		aSkill.setName(name);
		aSkill.setKeyName("KEY_" + name);
		aSkill.setTypeInfo(type);
		aSkill.put(ObjectKey.KEY_STAT, intStat);
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
		final Ability anAbility = new Ability();
		anAbility.setName(name);
		anAbility.setKeyName("KEY_" + name);
		anAbility.setCategory(cat);
		anAbility.setTypeInfo(type);
		Globals.addAbility(anAbility);
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
		aWpnProf.setKeyName("KEY_" + name);
		aWpnProf.setTypeInfo(type);
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
		aRace.setKeyName("KEY_" + name);

		try
		{
			final BonusObj bon = Bonus.newBonus("FEAT|POOL|1");
			final PreParserFactory factory = PreParserFactory.getInstance();
			final Prerequisite prereq = factory.parse("PREDEFAULTMONSTER:N");
			bon.addPreReq(prereq);
			bon.setCreatorObject(aRace);

			aRace.setBonusInitialFeats(bon);
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("Caught " + e);
		}

		Globals.getContext().ref.importObject(aRace);
		return aRace;
	}

	/**
	 * Set the important info about a Race
	 * @param name The race name
	 * @return The race (which has also been added to global storage)
	 */
	public static PCClass makeClass(final String name)
	{
		final PCClass aClass = new PCClass();
		aClass.setName(name);
		aClass.setKeyName("KEY_" + name);

		Globals.getContext().ref.importObject(aClass);
		return aClass;
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

}
