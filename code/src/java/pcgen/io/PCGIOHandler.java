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

import pcgen.core.*;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PObjectLoader;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>PCGIOHandler</code><br>
 * Reading and Writing PlayerCharacters in PCGen's own format (PCG).
 *
 * @author Thomas Behr 11-03-02
 * @version $Revision$
 */
public final class PCGIOHandler extends IOHandler
{
	private final List errors = new ArrayList();
	private final List warnings = new ArrayList();
	private PlayerCharacter aPC;

	/**
	 * Selector
	 * <p/>
	 * <br>author: Thomas Behr 18-03-02
	 *
	 * @return a list of error messages
	 */
	public List getErrors()
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
	public List getMessages()
	{
		final List messages = new ArrayList();

		for (Iterator it = errors.iterator(); it.hasNext();)
		{
			messages.add("Error: " + it.next());
		}

		for (Iterator it = warnings.iterator(); it.hasNext();)
		{
			messages.add("Warning: " + it.next());
		}

		return messages;
	}

	/**
	 * Selector
	 * <p/>
	 * <br>author: Thomas Behr 15-03-02
	 *
	 * @return a list of warning messages
	 */
	public List getWarnings()
	{
		return warnings;
	}

	public static void buildSALIST(String aChoice, List aAvailable, List aBonus, final PlayerCharacter currentPC)
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

		final List saNames = new ArrayList();
		final StringTokenizer aTok = new StringTokenizer(aString, ",");

		while (aTok.hasMoreTokens())
		{
			saNames.add(aTok.nextToken());
		}

		final List aSAList = currentPC.getSpecialAbilityList();

		//
		// Add special abilities due to templates
		//
		final List aTemplateList = currentPC.getTemplateList();

		for (Iterator e1 = aTemplateList.iterator(); e1.hasNext();)
		{
			final PCTemplate aTempl = (PCTemplate) e1.next();
			final List SAs = aTempl.getSpecialAbilityList(currentPC.getTotalLevels(), currentPC.totalHitDice());

			if ((SAs == null) || SAs.isEmpty()) // null pointer/empty check
			{
				continue;
			}

			for (Iterator e2 = SAs.iterator(); e2.hasNext();)
			{
				final String aSA = (String) e2.next();

				if (!aSAList.contains(aSA))
				{
					aSAList.add(aSA);
				}
			}
		}

		for (Iterator e2 = saNames.iterator(); e2.hasNext();)
		{
			aString = (String) e2.next();

			for (Iterator e1 = aSAList.iterator(); e1.hasNext();)
			{
				String aSA = ((SpecialAbility) (e1.next())).getKeyName();

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
							aVar = aPost.substring(0, iOffs) + aVar + aPost.substring(iOffs + 1);
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

		final List lines = new ArrayList();

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
				Logging.errorPrint("Couldn't close file in PCGIOHandler.read", e);
			}
		}

		pcToBeRead.setImporting(true);

		// If not validating, disable user preference for loading the campaign.
		// Otherwise, previewing will throw up a license for the campaign.
		final boolean loadCampaignsWithPC
				= SettingsHandler.isLoadCampaignsWithPC();

		if (!validate)
		{
			SettingsHandler.setLoadCampaignsWithPC(false);
		}

		final String[] pcgLines
				= (String[]) lines.toArray(new String[lines.size()]);

		final PCGParser parser;

		if (isPCGVersion2)
		{
			parser = new PCGVer2Parser(pcToBeRead);
		}
		else
		{
			parser = new PCGVer0Parser(pcToBeRead);
		}

		try
		{
			// parse it all
			parser.parsePCG(pcgLines);
		}
		catch (PCGParseException pcgex)
		{
			errors.add(pcgex.getMessage() + Constants.s_LINE_SEP + "Method: " + pcgex.getMethod() + '\n' + "Line: "
				+ pcgex.getLine());
		}

		warnings.addAll(parser.getWarnings());

		// we are now all done with the import parsing, so turn off
		// the Importing flag and then do some sanity checks
		pcToBeRead.setImporting(false);
		// Restore the original user preference
		SettingsHandler.setLoadCampaignsWithPC(loadCampaignsWithPC);

		try
		{
			sanityChecks(pcToBeRead);
		}
		catch (NumberFormatException ex)
		{
			errors.add(ex.getMessage() + Constants.s_LINE_SEP + "Method: sanityChecks");
		}

		pcToBeRead.setDirty(false);
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
				Logging.errorPrint("Couldn't close file in PCGIOHandler.write", e);
			}
		}
	}

	/*
	 * ###############################################################
	 * private helper methods
	 * ###############################################################
	 */
	private void sanityChecks(PlayerCharacter currentPC)
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

		final Race aRace = aPC.getRace();

		if (aRace.hitDice(aPC) != 0)
		{
			iSides = aRace.getHitDiceSize(aPC);

			//
			// If pcg was saved with "Use Default Monsters off" and
			// now loading with "Use Default Monsters on"
			// generate some random hit points
			//
			if (aRace.getHitPointMapSize() < 1)
			{
				for (int i = 0; i <= aRace.hitDice(aPC); i++)
				{
					final int roll = RollingMethods.roll(iSides);
					aRace.setHitPoint(i, new Integer(roll));
				}

				warnings.add(
					"Character was saved with \"Use Default Monsters\" off. Random hit points added for race hit dice.");
				bFixMade = true;
			}

			for (int i = 0; i <= aRace.hitDice(aPC); i++)
			{
				iRoll = aRace.getHitPoint(i).intValue();

				if (iRoll > iSides)
				{
					aRace.setHitPoint(i, new Integer(iSides));
					bFixMade = true;
				}
			}
		}

		Ability aFeat;

		for (Iterator it = aPC.getRealFeatsIterator(); it.hasNext();)
		{
			aFeat = (Ability) it.next();

			if (aFeat.getChoiceString().startsWith("SALIST|"))
			{
				List aAvailable = new ArrayList();
				List aBonus = new ArrayList();
				buildSALIST(aFeat.getChoiceString(), aAvailable, aBonus, currentPC);

				for (int i = 0; i < aFeat.getAssociatedCount(); i++)
				{
					String aString = aFeat.getAssociated(i);
					final String prefix = aString + "|";
					boolean bLoop = true;

					while (true)
					{
						int x;

						for (x = 0; x < aBonus.size(); x++)
						{
							final String bString = (String) aBonus.get(x);

							if (bString.startsWith(prefix))
							{
								String tmp = bString.substring(bString.indexOf('|') + 1);
								aFeat.addBonusList(tmp);

								break;
							}
						}

						if ((x < aBonus.size()) || !bLoop)
						{
							break;
						}

						bLoop = false; // Avoid infinite loops at all costs!

						// Do direct replacement if only 1 choice
						if (aBonus.size() == 1)
						{
							aString = (String) aBonus.get(0);
							aString = aString.substring(0, aString.indexOf('|'));
						}
						else
						{
							/*
							 * need to come up with a method that will allow to
							 * remove the necessity of swing to be used here
							 *
							 * author: Thomas Behr 15-03-02
							 */
							while (true)
							{
								final String message = aFeat.getDisplayName() + " has been modified and PCGen is unable to "
									+ "determine your previous selection(s)." + Constants.s_LINE_SEP
									+ Constants.s_LINE_SEP + "This box will pop up once for each time you "
									+ "have taken the feat.";

								InputInterface ii = InputFactory.getInputInstance();
								Object selectedValue = ii.showInputDialog(null, message, Constants.s_APPNAME,
										MessageType.INFORMATION, aAvailable.toArray(), aAvailable.get(0));

								if (selectedValue != null)
								{
									aString = (String) selectedValue;

									break;
								}

								ShowMessageDelegate.showMessageDialog("You MUST make a selection", Constants.s_APPNAME, MessageType.INFORMATION);
							}
						}

						aFeat.setAssociated(i, aString);
					}
				}
			}
			else if (aFeat.getChoiceString().startsWith("NONCLASSSKILLLIST|"))
			{
				//
				// Byngl July 12, 2002
				//
				for (int it3 = 0; it3 < aFeat.getAssociatedCount(); it3++)
				{
					final String skillString = aFeat.getAssociated(it3);
					try
					{
						PObjectLoader.parseTag(aFeat, "CSKILL:" + skillString);
					} catch (PersistenceLayerException e)
					{
						e.printStackTrace();
					}
				}
			}

			if (aFeat.isMultiples() && (aFeat.getAssociatedCount() == 0))
			{
				aFeat.addAssociated("PLEASE MAKE APPROPRIATE SELECTION");
				warnings.add("Multiple selection feat found with no selections (" + aFeat.getDisplayName()
					+ "). Correct on Feat tab.");
			}
		}

		PCClass aClass;

		// Get templates - give it the biggest HD
		// sk4p 11 Dec 2002

		//PCTemplate aTemplate = null;
		if (aPC.getClassList() != null)
		{
			for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
			{
				aClass = (PCClass) it.next();

				// Ignore if no levels
				if (aClass.getLevel() < 1)
				{
					continue;
				}

				// Walk through the levels for this class

				for (int i = 0; i <= aClass.getLevel(); i++)
				{
					int baseSides = aClass.getLevelHitDie(currentPC, i + 1);
					iRoll = aClass.getHitPoint(i).intValue();
					iSides = baseSides + (int) aClass.getBonusTo("HD", "MAX", i + 1, aPC);

					if (iRoll > iSides)
					{
						aClass.setHitPoint(i, new Integer(iSides));
						bFixMade = true;
					}
				}
			}
		}

		if (bFixMade)
		{
			final String message = "Fixed illegal value in hit points. " + "Current character hit points: "
				+ aPC.hitPoints() + " not " + oldHp;
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
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			aClass = (PCClass) it.next();
			aClass.setLevel(aClass.getLevel(), currentPC);
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
