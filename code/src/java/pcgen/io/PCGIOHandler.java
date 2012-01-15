/*
 * PCGIOHandler.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on March 11, 2002, 8:30 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SpecialAbility;
import pcgen.util.Logging;

/**
 * <code>PCGIOHandler</code><br>
 * Reading and Writing PlayerCharacters in PCGen's own format (PCG).
 *
 * @author Thomas Behr 11-03-02
 * @version $Revision$
 */
public final class PCGIOHandler extends IOHandler
{
	private final List<String> errors = new ArrayList<String>();
	private final List<String> warnings = new ArrayList<String>();
	private PlayerCharacter aPC;

	/**
	 * Selector
	 * <p/>
	 * <br>author: Thomas Behr 18-03-02
	 *
	 * @return a list of error messages
	 */
	public List<String> getErrors()
	{
		return errors;
	}

	/**
	 * Convenience Method
	 * <p/>
	 * <br>author: Thomas Behr 18-03-02
	 *
	 * @return a list of messages
	 */
	public List<String> getMessages()
	{
		final List<String> messages = new ArrayList<String>();

		messages.addAll(errors);
		messages.addAll(warnings);

		return messages;
	}

	/**
	 * Selector
	 * <p/>
	 * <br>author: Thomas Behr 15-03-02
	 *
	 * @return a list of warning messages
	 */
	public List<String> getWarnings()
	{
		return warnings;
	}

	public static void buildSALIST(String aChoice, List<String> aAvailable,
		List<String> aBonus, final PlayerCharacter currentPC)
	{
		// SALIST:Smite|VAR|%|1
		// SALIST:Turn ,Rebuke|VAR|%|1
		String aString;
		String aPost = "";
		int iOffs = aChoice.indexOf('|', 7);

		if (iOffs < 0)
		{
			aString = aChoice;
		}
		else
		{
			aString = aChoice.substring(7, iOffs);
			aPost = aChoice.substring(iOffs + 1);
		}

		final List<String> saNames = new ArrayList<String>();
		final StringTokenizer aTok = new StringTokenizer(aString, ",");

		while (aTok.hasMoreTokens())
		{
			saNames.add(aTok.nextToken());
		}

		final List<SpecialAbility> aSAList = currentPC.getSpecialAbilityList();

		for (String name : saNames)
		{
			for (SpecialAbility sa : aSAList)
			{
				String aSA = sa.getKeyName();

				if (aSA.startsWith(aString))
				{
					String aVar = "";

					//
					// Trim off variable portion of SA, and save variable name
					// (eg. "Smite Evil %/day|SmiteEvil" --> aSA = "Smite Evil", aVar = "SmiteEvil")
					//
					iOffs = aSA.indexOf('|');

					if (iOffs >= 0)
					{
						aVar = aSA.substring(iOffs + 1);
						iOffs = aSA.indexOf('%');

						if (iOffs >= 0)
						{
							aSA = aSA.substring(0, iOffs).trim();
						}
					}

					if (!aAvailable.contains(aSA))
					{
						aAvailable.add(aSA);

						//
						// Check for variable substitution
						//
						iOffs = aPost.indexOf('%');

						if (iOffs >= 0)
						{
							aVar =
									aPost.substring(0, iOffs) + aVar
										+ aPost.substring(iOffs + 1);
						}

						aBonus.add(aSA + "|" + aVar);
					}
				}
			}
		}
	}

	/**
	 * Reads the contents of the given PlayerCharacter from a stream
	 * <p/>
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param pcToBeRead the PlayerCharacter to store the read data
	 * @param in         the stream to be read from
	 * @param validate
	 */
	public void read(PlayerCharacter pcToBeRead, InputStream in,
		final boolean validate)
	{
		this.aPC = pcToBeRead;

		warnings.clear();

		final List<String> lines = new ArrayList<String>();

		boolean isPCGVersion2 = false;

		// try reading in all the lines in the .pcg file
		BufferedReader br = null;

		try
		{
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			String aLine;

			while ((aLine = br.readLine()) != null)
			{
				lines.add(aLine);
				isPCGVersion2 |= aLine.startsWith(IOConstants.TAG_PCGVERSION);
			}
		}
		catch (IOException ioe)
		{
			Logging.errorPrint("Exception in PCGIOHandler::read", ioe);
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				Logging.errorPrint("Couldn't close file in PCGIOHandler.read",
					e);
			}
		}

		pcToBeRead.setImporting(true);

		// If not validating, disable user preference for loading the campaign.
		// Otherwise, previewing will throw up a license for the campaign.
		final boolean loadCampaignsWithPC =
				SettingsHandler.isLoadCampaignsWithPC();

		if (!validate)
		{
			SettingsHandler.setLoadCampaignsWithPC(false);
		}

		final String[] pcgLines = lines.toArray(new String[lines.size()]);
		if (isPCGVersion2)
		{
			final PCGParser parser = new PCGVer2Parser(pcToBeRead);
			try
			{
				// parse it all
				parser.parsePCG(pcgLines);
			}
			catch (PCGParseException pcgex)
			{
				errors.add(pcgex.getMessage() + Constants.LINE_SEPARATOR + "Method: "
					+ pcgex.getMethod() + '\n' + "Line: " + pcgex.getLine());
			}

			warnings.addAll(parser.getWarnings());

			// we are now all done with the import parsing, so turn off
			// the Importing flag and then do some sanity checks
			pcToBeRead.setImporting(false);
			// Restore the original user preference
			SettingsHandler.setLoadCampaignsWithPC(loadCampaignsWithPC);

			try
			{
				sanityChecks(pcToBeRead, parser);
			}
			catch (NumberFormatException ex)
			{
				errors.add(ex.getMessage() + Constants.LINE_SEPARATOR
					+ "Method: sanityChecks");
			}

			pcToBeRead.setDirty(false);
		}
		else
		{
			errors.add("Cannot open PCG file");
		}
	}

	/**
	 * Writes the contents of the given PlayerCharacter to a stream
	 * <p/>
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param pcToBeWritten the PlayerCharacter to write
	 * @param out           the stream to be written to
	 */
	public void write(PlayerCharacter pcToBeWritten, OutputStream out)
	{
		this.aPC = pcToBeWritten;

		final String pcgString;
		pcgString = (new PCGVer2Creator(pcToBeWritten)).createPCGString();

		BufferedWriter bw = null;

		try
		{
			bw = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
			bw.write(pcgString);
			bw.flush();

			pcToBeWritten.setDirty(false);
		}
		catch (IOException ioe)
		{
			Logging.errorPrint("Exception in PCGIOHandler::write", ioe);
		}
		finally
		{
			try
			{
				bw.close();
			}
			catch (IOException e)
			{
				Logging.errorPrint("Couldn't close file in PCGIOHandler.write",
					e);
			}
		}
	}

	/*
	 * ###############################################################
	 * private helper methods
	 * ###############################################################
	 */
	private void sanityChecks(PlayerCharacter currentPC, PCGParser parser)
	{
		// Hit point sanity check
		boolean bFixMade = false;

		// First make sure the "working" equipment list
		// is in effect for all the bonuses it may add
		aPC.setCalcEquipmentList();

		// make sure the bonuses from companions are applied
		aPC.setCalcFollowerBonus(currentPC);

		// pre-calculate all the bonuses
		aPC.calcActiveBonuses();

		int iSides;
		int iRoll;
		final int oldHp = aPC.hitPoints();

		// Recalc the feat pool if required
		if (parser.isCalcFeatPoolAfterLoad())
		{
			double baseFeatPool = parser.getBaseFeatPool();
			double featPoolBonus = aPC.getRemainingFeatPoints(true);
			baseFeatPool -= featPoolBonus;
			aPC.setFeats(baseFeatPool);
		}

		for (Ability aFeat : aPC.getRealAbilitiesList(AbilityCategory.FEAT))
		{
			if (aFeat.getSafe(ObjectKey.MULTIPLE_ALLOWED) && !currentPC.hasAssociations(aFeat))
			{
				currentPC.addAssociation(aFeat, "PLEASE MAKE APPROPRIATE SELECTION");
				warnings
					.add("Multiple selection feat found with no selections ("
						+ aFeat.getDisplayName() + "). Correct on Feat tab.");
			}
		}

		// Get templates - give it the biggest HD
		// sk4p 11 Dec 2002

		//PCTemplate aTemplate = null;
		if (aPC.hasClass())
		{
			for (PCClass pcClass : aPC.getClassSet())
			{
				// Ignore if no levels
				if (aPC.getLevel(pcClass) < 1)
				{
					continue;
				}

				// Walk through the levels for this class

				for (int i = 0; i <= aPC.getLevel(pcClass); i++)
				{
					int baseSides = currentPC.getLevelHitDie(pcClass, i + 1).getDie();
					PCClassLevel pcl = aPC.getActiveClassLevel(pcClass, i);
					Integer hp = currentPC.getHP(pcl);
					iRoll = hp == null ? 0 : hp;
					iSides =
							baseSides
								+ (int) pcClass.getBonusTo("HD", "MAX", i + 1,
									aPC);

					if (iRoll > iSides)
					{
						PCClassLevel classLevel = aPC.getActiveClassLevel(pcClass, i);
						aPC.setHP(classLevel, Integer.valueOf(iSides));
						bFixMade = true;
					}
				}
			}
		}

		if (bFixMade)
		{
			final String message =
					"Fixed illegal value in hit points. "
						+ "Current character hit points: " + aPC.hitPoints()
						+ " not " + oldHp;
			warnings.add(message);
		}

		//
		// Sometimes another class, feat, item, whatever can affect
		// what spells or whatever would have been available for a
		// class, so this simply lets the level advancement routine
		// take into account all the details known about a character
		// now that the import is completed. The level isn't affected.
		//  merton_monk@yahoo.com 2/15/2002
		//
		for (PCClass pcClass : aPC.getClassSet())
		{
			pcClass.setLevel(aPC.getLevel(pcClass), currentPC);
		}

		//
		// need to calc the movement rates
		aPC.adjustMoveRates();

		// re-calculate all the bonuses
		aPC.calcActiveBonuses();

		// make sure we are not dirty
		aPC.setDirty(false);
	}
}
