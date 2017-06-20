/*
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 *
 *
 */
package pcgen.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.SortedMap;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.CampaignOutput;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

/**
 * {@code CustomData}
 *
 */
public final class CustomData
{
	private static final String AUTO_GEN_WARN_LINE_1 = "#This file auto-generated by PCGen. Manual editing not recommended.";
	private static final String AUTO_GEN_WARN_LINE_2 = "#Ensure PCGen is not running before editing. Make backups as errors may result in data loss.";
	private static boolean customItemsLoaded = false;

	/**
	 * As it only contains static methods, disable instance creation with a private constructor.
	 */
	private CustomData()
	{
		// Empty Constructor
	}

	/**
	 * Get custom equipment reader
	 * @return custom equipment reade
	 */
	public static BufferedReader getCustomEquipmentReader()
	{
		return getReader(customEquipmentFilePath());
	}

	/**
	 * Get custom bio file set path
	 * @param usePath
	 * @return  custom bio file set path
	 */
	public static String customBioSetFilePath(final boolean usePath)
	{
		return getCustomPath("BioSet", usePath);
	}

	/**
	 * Get customClassFilePath
	 * @param usePath
	 * @return customClassFilePath
	 */
	public static String customClassFilePath(final boolean usePath)
	{
		return getCustomPath("Classes", usePath);
	}

	/**
	 * Get customDeityFilePath
	 * @param usePath
	 * @return customDeityFilePath
	 */
	public static String customDeityFilePath(final boolean usePath)
	{
		return getCustomPath("Deities", usePath);
	}

	/**
	 * Get customDomainFilePath
	 * @param usePath
	 * @return customDomainFilePath
	 */
	public static String customDomainFilePath(final boolean usePath)
	{
		return getCustomPath("Domains", usePath);
	}

	/**
	 * Get customAbilityFilePath
	 * @param usePath
	 * @return customAbilityFilePath
	 */
	public static String customAbilityFilePath(final boolean usePath)
	{
		return getCustomPath("Abilities", usePath);
	}

	/**
	 * Get customFeatFilePath
	 * @param usePath
	 * @return customFeatFilePath
	 */
	public static String customFeatFilePath(final boolean usePath)
	{
		return getCustomPath("Feats", usePath);
	}

	/**
	 * GEt customLanguageFilePath
	 * @param usePath
	 * @return customLanguageFilePath
	 */
	public static String customLanguageFilePath(final boolean usePath)
	{
		return getCustomPath("Languages", usePath);
	}

	/**
	 * Get custom purchase mode path
	 * @param usePath Should the game mode path be used
	 * @param gmName The name of the game mode to get the path for 
	 * @return  custom purchase mode file set path
	 */
	public static String customPurchaseModeFilePath(final boolean usePath, String gmName)
	{
		return getCustomPath("PointBuyMethods", usePath, gmName);
	}

	/**
	 * Get customRaceFilePath
	 * @param usePath
	 * @return customRaceFilePath
	 */
	public static String customRaceFilePath(final boolean usePath)
	{
		return getCustomPath("Races", usePath);
	}

	/**
	 * Get customSkillFilePath
	 * @param usePath
	 * @return customSkillFilePath
	 */
	public static String customSkillFilePath(final boolean usePath)
	{
		return getCustomPath("Skills", usePath);
	}

	/**
	 * Get customSpellFilePath
	 * @param usePath
	 * @return customSpellFilePath
	 */
	public static String customSpellFilePath(final boolean usePath)
	{
		return getCustomPath("Spells", usePath);
	}

	/**
	 * GEt customTemplateFilePath
	 * @param usePath
	 * @return customTemplateFilePath
	 */
	public static String customTemplateFilePath(final boolean usePath)
	{
		return getCustomPath("Templates", usePath);
	}

	/**
	 * Write custom files out
	 */
	public static void writeCustomFiles()
	{
		//
		// Make sure the custom directory exists
		//
		ensureCustomDirExists();

		writePurchaseModeConfiguration();

		// Don't trash the file if user exits before loading custom items
		if (!customItemsLoaded)
		{
			return;
		}

		writeCustomBioSet();
		writeCustomClasses();
		writeCustomDeities();
		writeCustomDomains();
		writeCustomAbilities();
		writeCustomFeats();
		writeCustomItems();
		writeCustomLanguages();
		writeCustomRaces();
		writeCustomSkills();
		writeCustomSpells();
		writeCustomTemplates();
		writeCustomSources();
	}

	/**
	 * This method will check for the system specific custom directory
	 * and will create it if it exists.
	 */
	private static void ensureCustomDirExists()
	{
		File customDir =
				new File(PCGenSettings.getCustomDir() + File.separator
					+ SettingsHandler.getGame().getName());
		if (!customDir.exists())
		{
			try
			{
				customDir.mkdirs();
			}
			catch (SecurityException se)
			{
				Logging.errorPrint("Unable to create custom data directory '"
					+ customDir.getPath() + "' due the following error.", se);
			}
		}
	}

	/**
	 * Write custom items out
	 */
	public static void writeCustomItems()
	{

		//check if custom equipment has been loaded.  If not, just return without erasing customequipment.lst file
		if (!customItemsLoaded && new File(customEquipmentFilePath()).exists())
		{
			if (Logging.isLoggable(Logging.WARNING))
			{
				Logging
					.log(Logging.WARNING,
						"Custom items had not been loaded, so we won't save them this time."); //$NON-NLS-1$
			}
			return;
		}

		ensureCustomDirExists();

		final BufferedWriter bw = getCustomEquipmentWriter();

		if (bw == null)
		{
			return;
		}

		try
		{
			bw.write(AUTO_GEN_WARN_LINE_1);
			bw.newLine();
			bw.write(AUTO_GEN_WARN_LINE_2);
			bw.newLine();

			Globals.getContext()
			       .getReferenceContext()
			       .getConstructedCDOMObjects(Equipment.class)
			       .stream()
			       .filter(aEq -> aEq.isType(Constants.TYPE_CUSTOM) && !aEq.isType("AUTO_GEN"))
			       .forEach(aEq -> aEq.save(bw));
		}
		catch (IOException e)
		{
			Logging.errorPrint("Error in writeCustomItems", e);
		}
		finally
		{
			try
			{
				bw.close();
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Error in writeCustomItems while closing",
					ex);
			}
		}
	}

	/**
	 * Write custom purchase mode config
	 */
	public static void writePurchaseModeConfiguration()
	{
		ensureCustomDirExists();
		final BufferedWriter bw = getPurchaseModeWriter();
		final SortedMap<Integer, PointBuyCost> pbStatCosts = SettingsHandler.getGame().getPointBuyStatCostMap();

		if (bw == null || pbStatCosts == null)
		{
			return;
		}

		try
		{
			bw.write("#");
			bw.newLine();
			bw.write(AUTO_GEN_WARN_LINE_1);
			bw.newLine();
			bw.write(AUTO_GEN_WARN_LINE_2);
			bw.newLine();
			bw.write("#");
			bw.newLine();
			bw.write("# Point-buy ability score costs");
			bw.newLine();
			bw.write("#");
			bw.newLine();

			if (!pbStatCosts.isEmpty())
			{
				for ( Integer statValue : pbStatCosts.keySet() )
				{
					final PointBuyCost pbc = pbStatCosts.get(statValue);
					bw.write("STAT:" + statValue.toString() + "\t\tCOST:" + Integer.toString(pbc.getBuyCost()));
					final int iCount = pbc.getPrerequisiteCount();
					if (iCount != 0)
					{
						final StringWriter writer = new StringWriter();
						for (Prerequisite prereq : pbc.getPrerequisiteList())
						{
							final PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
							try
							{
								writer.write("\t");
								prereqWriter.write(writer, prereq);
							}
							catch (Exception e1)
							{
								e1.printStackTrace();
							}
						}
						bw.write(writer.toString());
					}
					bw.newLine();
				}
			}

			bw.write("#");
			bw.newLine();
			bw.write("# Point-buy methods");
			bw.newLine();
			bw.write("#");
			bw.newLine();

			for (PointBuyMethod pbm : SettingsHandler.getGame()
					.getModeContext().getReferenceContext()
					.getConstructedCDOMObjects(PointBuyMethod.class))
			{
				bw.write("METHOD:" + pbm.getDisplayName() + "\t\tPOINTS:"
					+ pbm.getPointFormula());
				bw.newLine();
			}
		}
		catch (IOException e)
		{
			Logging.errorPrint("Error in writePurchaseModeConfiguration", e);
		}
		finally
		{
			try
			{
				bw.close();
			}
			catch (IOException ex)
			{
				Logging
					.errorPrint(
						"Error in writePurchaseModeConfiguration while closing",
						ex);
			}
		}
	}

	private static BufferedWriter getCustomEquipmentWriter()
	{
		return getWriter(customEquipmentFilePath());
	}

	private static String getCustomPath(final String type,
		final boolean usePath, String gmName)
	{
		String aString = "";

		if (usePath)
		{
			aString = PCGenSettings.getCustomDir();
			aString += File.separator + gmName;
		}
		return aString + File.separator + "custom" + type + Constants.EXTENSION_LIST_FILE;
	}

	private static String getCustomPath(final String type, final boolean usePath)
	{
		return getCustomPath(type, usePath, SettingsHandler.getGame().getName());
	}

	private static BufferedWriter getPurchaseModeWriter()
	{
		return getWriter(customPurchaseModeFilePath(true, SettingsHandler.getGame().getName()));
	}

	private static BufferedReader getReader(final String path)
	{
		try
		{
			//return new BufferedReader(new FileReader(path));
			return new BufferedReader(new InputStreamReader(
				new FileInputStream(path), "UTF-8"));
		}
		catch (IOException e)
		{
			Logging
				.debugPrint("Could not get a reader to read from " + path, e);
			return null;
		}
	}

	private static BufferedWriter getWriter(final String path)
	{
		try
		{
			return new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(path), "UTF-8"));
		}
		catch (IOException e)
		{
			Logging
				.errorPrint(
					"Could not get a writer to write to "
						+ path
						+ " \nThis means that your custom files won't be written. Please check the path.",
					e);
			return null;
		}
	}

	private static String customEquipmentFilePath()
	{
		return getCustomPath("Equipment", true);
	}

	private static void writeCustomBioSet()
	{
		final BufferedWriter bw = getWriter(customBioSetFilePath(true));

		if (bw == null)
		{
			return;
		}

		try
		{
			bw.write("#");
			bw.newLine();
			bw.write(AUTO_GEN_WARN_LINE_1);
			bw.newLine();
			bw.write(AUTO_GEN_WARN_LINE_2);
			bw.newLine();
			bw.write("#");
			bw.newLine();

			for ( final Race race : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Race.class) )
			{
				if (race.isType(Constants.TYPE_CUSTOM))
				{
					String region;
					String[] unp = Globals.getContext().unparseSubtoken(race, "REGION");

					if (unp == null)
					{
						region = Constants.NONE;
					}
					else
					{
						region = unp[0];
					}

					final String key = race.getKeyName();
					bw.write(SettingsHandler.getGame().getBioSet().getRacePCCText(region, key));
					bw.newLine();
				}
			}

			// We are grouping the custom bio sets under the region of custom,
			// rather than types which are used elsewhere.
			// This done as type is not supported for bio sets.
			//bw.write(BioSet.getRegionPCCText("Custom"));
			bw.newLine();
		}
		catch (IOException e)
		{
			Logging.errorPrint("Error in writeCustomBioSet", e);
		}
		finally
		{
			try
			{
				bw.close();
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Error in writeCustomBioSet while closing",
					ex);
			}
		}
	}

	private static void writeCustomClasses()
	{
		writeCustomPObjects(customClassFilePath(true), Globals.getContext().getReferenceContext()
				.getConstructedCDOMObjects(PCClass.class).iterator());
	}

	private static void writeCustomDeities()
	{
		writeCustomPObjects(customDeityFilePath(true), Globals.getContext().getReferenceContext()
				.getConstructedCDOMObjects(Deity.class).iterator());
	}

	private static void writeCustomDomains()
	{
		writeCustomPObjects(customDomainFilePath(true),
				Globals.getContext().getReferenceContext()
						.getConstructedCDOMObjects(Domain.class).iterator());
	}

	private static void writeCustomAbilities()
	{
		for (AbilityCategory ac : SettingsHandler.getGame().getAllAbilityCategories())
		{
			writeCustomPObjects(customAbilityFilePath(true), Globals
					.getContext().getReferenceContext().getManufacturer(Ability.class, ac)
					.getAllObjects().iterator());
		}
	}

	private static void writeCustomFeats()
	{
		File temp = new File(customFeatFilePath(true));
		temp.delete();
	}

	private static void writeCustomHeader(final BufferedWriter bw)
		throws IOException
	{
		bw.write("#");
		bw.newLine();
		bw.write(AUTO_GEN_WARN_LINE_1);
		bw.newLine();
		bw.write(AUTO_GEN_WARN_LINE_2);
		bw.newLine();
		bw.write("#");
		bw.newLine();
		bw.write("SOURCELONG:Custom\tSOURCESHORT:Custom");
		bw.newLine();	
	}

	private static void writeCustomLanguages()
	{
		writeCustomPObjects(customLanguageFilePath(true),
				Globals.getContext().getReferenceContext().getConstructedCDOMObjects(
						Language.class).iterator());
	}

	private static void writeCustomPObjects(final String filename, final Iterator<? extends PObject> it)
	{
		final BufferedWriter bw = getWriter(filename);

		if (bw == null)
		{
			return;
		}

		try
		{
			writeCustomHeader(bw);

			while (it.hasNext())
			{
				final PObject pobj = it.next();

				if (pobj.isType(Constants.TYPE_CUSTOM))
				{
					bw.write(pobj.getPCCText());
					bw.newLine();
				}
			}
		}
		catch (IOException e)
		{
			Logging.errorPrint("Error in writeCustomPObjects", e);
		}
		finally
		{
			try
			{
				bw.close();
			}
			catch (IOException ex)
			{
				Logging.errorPrint(
					"Error in writeCustomPObjects while closing", ex);
			}
		}
	}

	private static void writeCustomRaces()
	{
		writeCustomPObjects(customRaceFilePath(true), Globals.getContext().getReferenceContext()
				.getConstructedCDOMObjects(Race.class).iterator());
	}

	private static void writeCustomSkills()
	{
		writeCustomPObjects(customSkillFilePath(true), Globals.getContext().getReferenceContext()
				.getConstructedCDOMObjects(Skill.class).iterator());
	}

	private static void writeCustomSources()
	{
		Globals.getCampaignList()
		       .stream()
		       .filter(c -> !c.getSafe(StringKey.DESTINATION).isEmpty())
		       .forEach(c -> CampaignOutput.output(Globals.getContext(), c));
	}

	private static void writeCustomSpells()
	{
		final BufferedWriter bw = getWriter(customSpellFilePath(true));

		if (bw == null)
		{
			return;
		}

		try
		{
			writeCustomHeader(bw);

			for(Spell spell : Globals.getContext().getReferenceContext()
					.getConstructedCDOMObjects(Spell.class))
			{
				if (spell.isType(Constants.TYPE_CUSTOM))
				{
					bw.write(spell.getPCCText());
					bw.newLine();
				}
			}
		}
		catch (IOException e)
		{
			Logging.errorPrint("Error in writeCustomSpells", e);
		}
		finally
		{
			try
			{
				bw.close();
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Error in writeCustomSpells while closing",
					ex);
			}
		}
	}

	private static void writeCustomTemplates()
	{
		writeCustomPObjects(customTemplateFilePath(true),
				Globals.getContext().getReferenceContext().getConstructedCDOMObjects(
						PCTemplate.class).iterator());
	}

	/**
	 * @param loaded Have the custom items ben loaded?
	 */
	public static void setCustomItemsLoaded(boolean loaded)
	{
		customItemsLoaded  = loaded;
	}
}
