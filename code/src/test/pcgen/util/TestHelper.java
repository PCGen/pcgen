/**
 * Copyright 2005 (c) Andrew Wilson <nuance@sourceforge.net>
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * <p>
 * Current Version: $Revision$
 */

package pcgen.util;

import org.apache.commons.lang3.StringUtils;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.ChronicleEntry;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Kit;
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
import pcgen.core.spell.Spell;
import pcgen.io.ExportHandler;
import pcgen.persistence.GameModeFileLoader;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.GenericLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.rules.context.LoadContext;
import pcgen.system.Main;
import plugin.lsttokens.testsupport.BuildUtilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Helps Junit tests
 */
@SuppressWarnings("nls")
public final class TestHelper
{
	private static final Logger LOG = Logger.getLogger(TestHelper.class.getName());

	static
	{
		Main.createLoadPluginTask().run();
	}

	private static final LstObjectFileLoader<Equipment> eqLoader = new GenericLoader<>(Equipment.class);

	private TestHelper()
	{
	}

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
			GameModeFileLoader.addDefaultTabInfo(gamemode);
		}
		SettingsHandler.setGame("3.5");
		int count = 0;
		while (aTok.hasMoreTokens())
		{
			SizeAdjustment sa = BuildUtilities.createSize(aTok.nextToken(), count++);
			Globals.getContext().getReferenceContext().importObject(sa);
		}
		Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(SizeAdjustment.class, "M").put(
						ObjectKey.IS_DEFAULT_SIZE, true);
	}

	/**
	 * Make some equipment
	 * @param input Equipment source line to be parsed
	 * @return true if OK
	 */
	public static boolean makeEquipment(final String input)
	{
		try
		{
			final CampaignSourceEntry source = createSource(TestHelper.class);
			eqLoader.parseLine(Globals.getContext(), null, input, source);
			return true;
		} catch (Exception e)
		{
			// TODO Deal with Exception?
		}
		return false;
	}

	/**
	 * Create a new CampaignSourceEntry for the class.
	 * @param cls The class the try is for.
	 * @return The CampaignSourceEntry.
	 */
	public static CampaignSourceEntry createSource(Class<?> cls)
	{
		final CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + cls.getName() + ".java"));
		} catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}
		return source;
	}

	/**
	 * No-op: plugin loading runs once via the static initializer. The method
	 * is retained because many test classes invoke it explicitly to document
	 * their dependency on plugins being loaded.
	 */
	public static void loadPlugins()
	{
		// Intentionally empty. Calling this method is enough — the call
		// references TestHelper, which triggers the class's static
		// initializer (where the actual plugin load happens).
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
				for (final Field f : clazz.getDeclaredFields())
				{
					if (f.getName().equals(fieldName))
					{
						f.setAccessible(true);
						return f;
					}
				}
				if (clazz != Object.class)
				{
					clazz = clazz.getSuperclass();
				} else
				{
					break;
				}
			}

		} catch (SecurityException e)
		{
			LOG.log(Level.SEVERE, "SecurityException is thrown in findField", e);
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
	public static Skill makeSkill(
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
		CDOMDirectSingleRef<PCStat> statRef = CDOMDirectSingleRef.getRef(stat);
		aSkill.put(ObjectKey.KEY_STAT, statRef);
		aSkill.put(ObjectKey.USE_UNTRAINED, untrained);
		aSkill.put(ObjectKey.ARMOR_CHECK, armorCheck);
		Globals.getContext().getReferenceContext().importObject(aSkill);
		return aSkill;
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
		AbilityCategory useCat = Globals.getContext().getReferenceContext()
				.constructNowIfNecessary(AbilityCategory.class, cat);
		final Ability anAbility = new Ability();
		anAbility.setName(name);
		anAbility.put(StringKey.KEY_NAME, ("KEY_" + name));
		anAbility.setCDOMCategory(useCat);
		addType(anAbility, type);
		Globals.getContext().getReferenceContext().importObject(anAbility);
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
		Globals.getContext().getReferenceContext().importObject(anAbility);
		return anAbility;
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

		LoadContext context = Globals.getContext();
		final BonusObj bon = Bonus.newBonus(context, "FEAT|POOL|1");
		aRace.addToListFor(ListKey.BONUS, bon);

		context.getReferenceContext().importObject(aRace);
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

		Globals.getContext().getReferenceContext().importObject(aClass);
		return aClass;
	}

	/**
	 * Set the important info about a Domain
	 * @param name The domain name
	 * @return The domain (which has also been added to global storage)
	 */
	public static Domain makeDomain(final String name)
	{
		final Domain domain = new Domain();
		domain.setName(name);
		domain.put(StringKey.KEY_NAME, (name));

		Globals.getContext().getReferenceContext().importObject(domain);
		return domain;
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

		Globals.getContext().getReferenceContext().importObject(aSpell);
		return aSpell;
	}

	/**
	 * Set the important info about a Kit. Note the key of the kit created will
	 * be the provided name with KEY_ added at the front. e.g. KEY_name
	 * @param name The kit name
	 * @return The kit (which has also been added to global storage)
	 */
	public static Kit makeKit(final String name)
	{
		final Kit aKit = new Kit();
		aKit.setName(name);
		aKit.put(StringKey.KEY_NAME, ("KEY_" + name));

		Globals.getContext().getReferenceContext().importObject(aKit);
		return aKit;
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

		Globals.getContext().getReferenceContext().importObject(aTemplate);
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
		return (AbilityCategory) ability.getCDOMCategory();
	}

	public static void addType(CDOMObject cdo, String string)
	{
		String[] stringList = string.split("\\.");
		for (String s : stringList)
		{
			cdo.addToListFor(ListKey.TYPE, Type.getConstant(s));
		}
	}

	/**
	 * Checks to see if this PC has the weapon proficiency key aKey
	 *
	 * @param pc the PlayerCharacter to check
	 * @param aKey the WeaponProf key to look up
	 * @return boolean
	 */
	public static boolean hasWeaponProfKeyed(PlayerCharacter pc,
											 final String aKey)
	{
		WeaponProf wp = Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(WeaponProf.class, aKey);
		return wp != null && pc.hasWeaponProf(wp);
	}

	/**
	 * Locate the data folder which contains the primary set of LST data. This defaults to the data folder under the
	 * current directory but can be customized in the config.ini folder.
	 * @return The path of the data folder.
	 */
	public static String findDataFolder()
	{
		// Set the pcc location to "data"
		String pccLoc = "data";

		// Read in config.ini and override the pcc location if it exists
		try (var lines = Files.lines(Path.of("config.ini")))
		{
			return lines
					.filter(line -> line.startsWith("pccFilesPath="))
					.map(line -> line.substring(13))
					.findFirst()
					.orElse(pccLoc);
		} catch (IOException e)
		{
			// Ignore, see method comment
		}
		return pccLoc;
	}

	/**
	 * Write a settings/config file for use by unit tests.
	 * @param configFileName The name of the new config file.
	 * @param configFolder The folder in which other settings files will be saved.
	 * @param pccLoc The location of the data folder.
	 * @throws IOException If the file cannot be written.
	 */
	public static void createDummySettingsFile(String configFileName,
											   String configFolder, String pccLoc) throws IOException
	{
		File configFile = new File(configFileName);
		configFile.deleteOnExit();
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				configFile), StandardCharsets.UTF_8)))
		{
			bw.write("settingsPath=" + configFolder + "\r\n");
			if (pccLoc != null)
			{
				LOG.log(Level.INFO, "Using PCC Location of ''{0}''.", pccLoc);
				bw.write("pccFilesPath=" + pccLoc + "\r\n");
			}
			bw.write("customPath=testsuite\\\\customdata\r\n");
		}
	}

	public static ChronicleEntry buildChronicleEntry(boolean visible, String campaign, String date,
													 String gm, String party, String adventure, int xp,
													 String chronicle)
	{
		ChronicleEntry chronEntry = new ChronicleEntry();
		chronEntry.setOutputEntry(visible);
		chronEntry.setCampaign(campaign);
		chronEntry.setDate(date);
		chronEntry.setGmField(gm);
		chronEntry.setParty(party);
		chronEntry.setAdventure(adventure);
		chronEntry.setXpField(xp);
		chronEntry.setChronicle(chronicle);
		return chronEntry;
	}

	public static PCClass parsePCClassText(String classPCCText,
										   CampaignSourceEntry source)
	{
		PCClassLoader pcClassLoader = new PCClassLoader();
		PCClass reconstClass = null;
		StringTokenizer tok = new StringTokenizer(classPCCText, "\n");
		while (tok.hasMoreTokens())
		{
			String line = tok.nextToken();
			if (!StringUtils.isBlank(line))
			{
				LOG.log(Level.INFO, "Processing line:''{0}''.", line);
				reconstClass =
						pcClassLoader.parseLine(Globals.getContext(),
								reconstClass, line, source);
			}
		}
		return reconstClass;
	}

	/**
	 * Evaluate a token, used in several "export" tests. By default, the token encoding is ignored.
	 * If encoded value is required, use @see pcgen.io.FileAccess#setCurrentOutputFilter(java.lang.String) before
	 * calling this static method.
	 *
	 * @param token the token to evaluate (e.g., any token from @see plugin.exporttokens such as "PORTRAIT")
	 * @param pc    the pc or a PlayerCharacter object
	 * @return the string containing the evaluated token
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String evaluateToken(String token, PlayerCharacter pc)
			throws IOException
	{
		StringWriter retWriter = new StringWriter();
		try (BufferedWriter bufWriter = new BufferedWriter(retWriter))
		{
			ExportHandler export = ExportHandler.createExportHandler(new File(""));
			export.replaceTokenSkipMath(pc, token, bufWriter);
		}

		return retWriter.toString();
	}
}
