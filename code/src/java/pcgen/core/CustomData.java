/*
 * CustomData.java
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
 * Created on November 23, 2002, 12:53 AM
 *
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceManager;
import pcgen.persistence.lst.CampaignOutput;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.util.Logging;

import java.io.*;
import java.util.*;

/**
 * <code>CustomData</code>
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class CustomData
{
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
		if (!PersistenceManager.getInstance().isCustomItemsLoaded())
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
		File customDir = new File(SettingsHandler.getPcgenCustomDir().getAbsolutePath() + File.separator + SettingsHandler.getGame().getName());
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
		if (!PersistenceManager.getInstance().isCustomItemsLoaded())
		{
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
			bw.write("#This file auto-generated by PCGen. Do not edit manually.");
			bw.newLine();

			for (Iterator e = EquipmentList.getEquipmentListIterator(); e.hasNext(); ) {
				final Map.Entry entry = (Map.Entry)e.next();
				final Equipment aEq = (Equipment) entry.getValue();

				if (aEq.isType(Constants.s_CUSTOM) && !aEq.isType("AUTO_GEN"))
				{
					aEq.save(bw);
				}
			}
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
				Logging.errorPrint("Error in writeCustomItems while closing", ex);
			}
		}
	}

	/**
	 * Write custom purchase mode config
	 */
	public static void writePurchaseModeConfiguration()
	{
		final BufferedWriter bw = getPurchaseModeWriter();
		final SortedMap pbStatCosts = SettingsHandler.getGame().getPointBuyStatCostMap();

		if (bw == null || pbStatCosts == null)
		{
			return;
		}

		try
		{
			bw.write("#");
			bw.newLine();
			bw.write("#This file auto-generated by PCGen. Do not edit manually.");
			bw.newLine();
			bw.write("#");
			bw.newLine();
			bw.write("# Point-buy ability score costs");
			bw.newLine();
			bw.write("#");
			bw.newLine();

			if (pbStatCosts.size() > 0)
			{
				for (Iterator e = pbStatCosts.keySet().iterator(); e.hasNext();)
				{
					final Integer statValue = (Integer) e.next();
					final PointBuyCost pbc = ((PointBuyCost) pbStatCosts.get(statValue));
					bw.write("STAT:" + statValue.toString() + "\t\tCOST:" + Integer.toString(pbc.getStatCost()));
					final int iCount = pbc.getPreReqCount();
					if (iCount != 0)
					{
						final StringWriter writer = new StringWriter();
						for (int i = 0; i < iCount; ++i)
						{
							final Prerequisite prereq = pbc.getPreReq(i);
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

			for (int i = 0, x = SettingsHandler.getGame().getPurchaseMethodCount(); i < x; ++i)
			{
				final PointBuyMethod pbm = SettingsHandler.getGame().getPurchaseMethod(i);
				bw.write("METHOD:" + pbm.getMethodName() + "\t\tPOINTS:" + pbm.getPointFormula());
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
				Logging.errorPrint("Error in writePurchaseModeConfiguration while closing", ex);
			}
		}
	}

	private static BufferedWriter getCustomEquipmentWriter()
	{
		return getWriter(customEquipmentFilePath());
	}

	private static String getCustomPath(final String type, final boolean usePath)
	{
		String aString = "";

		if (usePath)
		{
			aString = SettingsHandler.getPcgenCustomDir().getAbsolutePath();
			aString += File.separator + SettingsHandler.getGame().getName();
		}
		return aString + File.separator + "custom" + type + Constants.s_PCGEN_LIST_EXTENSION;
	}

	private static BufferedWriter getPurchaseModeWriter()
	{
		final String modeFile = SettingsHandler.getPcgenSystemDir() + File.separator + "gameModes" +
				File.separator + SettingsHandler.getGame().getName() + File.separator + "pointbuymethods.lst";

		return getWriter(modeFile);
	}

	private static BufferedReader getReader(final String path)
	{
		try
		{
			//return new BufferedReader(new FileReader(path));
			return new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		}
		catch (IOException e)
		{
			Logging.debugPrint("Could not get a reader to read from " + path, e);
			return null;
		}
	}

	private static BufferedWriter getWriter(final String path)
	{
		try
		{
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
		}
		catch (IOException e)
		{
			Logging.errorPrint("Could not get a writer to write to " + path + " \nThis means that your custom files won't be written. Please check the path.", e);
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
			bw.write("#This file auto-generated by PCGen. Do not edit manually.");
			bw.newLine();
			bw.write("#");
			bw.newLine();

			for (Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext();)
			{
				final PObject pobj = (PObject) it.next();

				if (pobj.isType(Constants.s_CUSTOM))
				{
					String region = pobj.getRegionString();

					if (region == null)
					{
						region = Constants.s_NONE;
					}

					final String key = pobj.getKeyName();
					bw.write(Globals.getBioSet().getRacePCCText(region, key));
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
				Logging.errorPrint("Error in writeCustomBioSet while closing", ex);
			}
		}
	}

	private static void writeCustomClasses()
	{
		writeCustomPObjects(customClassFilePath(true), Globals.getClassList().iterator());
	}

	private static void writeCustomDeities()
	{
		writeCustomPObjects(customDeityFilePath(true), Globals.getDeityList().iterator());
	}

	private static void writeCustomDomains()
	{
		writeCustomPObjects(customDomainFilePath(true), Globals.getDomainList().iterator());
	}

	private static void writeCustomAbilities()
	{
		writeCustomPObjects(customAbilityFilePath(true), Globals.getAbilityKeyIterator("ALL"));
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
		bw.write("#This file auto-generated by PCGen. Do not edit manually.");
		bw.newLine();
		bw.write("#");
		bw.newLine();
		/* Trying to correct bug 1454563. When writing custom files, it should tabs/newlines to
		 * to separate the tokens
		  bw.write("SOURCELONG:Custom|SOURCESHORT:Custom");
		bw.newLine();
		 */
		bw.write("SOURCELONG:Custom");
		bw.newLine();
		bw.write("SOURCESHORT:Custom");
		bw.newLine();	}

	private static void writeCustomLanguages()
	{
		writeCustomPObjects(customLanguageFilePath(true), Globals.getLanguageList().iterator());
	}

	private static void writeCustomPObjects(final String filename, final Iterator it)
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
				final PObject pobj = (PObject) it.next();

				if (pobj.isType(Constants.s_CUSTOM))
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
				Logging.errorPrint("Error in writeCustomPObjects while closing", ex);
			}
		}
	}

	private static void writeCustomRaces()
	{
		writeCustomPObjects(customRaceFilePath(true), Globals.getRaceMap().values().iterator());
	}

	private static void writeCustomSkills()
	{
		writeCustomPObjects(customSkillFilePath(true), Globals.getSkillList().iterator());
	}

	private static void writeCustomSources()
	{
		for (Iterator i = Globals.getCampaignList().iterator(); i.hasNext();)
		{
			final Campaign c = (Campaign) i.next();

			if (c.getDestination().length() > 0)
			{
				CampaignOutput.output(c);
			}
		}
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

			final Iterator e = Globals.getSpellMap().values().iterator();

			while (e.hasNext())
			{
				final Object obj = e.next();

				if (obj instanceof ArrayList)
				{
					for (Iterator e2 = ((ArrayList) obj).iterator(); e2.hasNext();)
					{
						final Spell aSpell = (Spell) e2.next();

						if (aSpell.isType(Constants.s_CUSTOM))
						{
							bw.write(aSpell.getPCCText());
							bw.newLine();
						}
					}
				}
				else
				{
					if (((Spell) obj).isType(Constants.s_CUSTOM))
					{
						bw.write(((Spell) obj).getPCCText());
						bw.newLine();
					}
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
				Logging.errorPrint("Error in writeCustomSpells while closing", ex);
			}
		}
	}

	private static void writeCustomTemplates()
	{
		writeCustomPObjects(customTemplateFilePath(true), Globals.getTemplateList().iterator());
	}
}
