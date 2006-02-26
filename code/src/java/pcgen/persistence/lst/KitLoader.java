/*
 * KitLoader.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 23, 2002, 1:39 PM
 *
 * $Id: KitLoader.java,v 1.51 2006/02/17 02:50:07 boomer70 Exp $
 */
package pcgen.persistence.lst;

import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.kit.KitAbilities;
import pcgen.core.kit.KitAlignment;
import pcgen.core.kit.KitClass;
import pcgen.core.kit.KitDeity;
import pcgen.core.kit.KitFunds;
import pcgen.core.kit.KitGear;
import pcgen.core.kit.KitKit;
import pcgen.core.kit.KitProf;
import pcgen.core.kit.KitRace;
import pcgen.core.kit.KitSkill;
import pcgen.core.kit.KitSpells;
import pcgen.core.kit.KitStat;
import pcgen.core.kit.KitTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;
import pcgen.core.kit.KitSelect;
import pcgen.core.kit.BaseKit;
import pcgen.core.kit.KitLevelAbility;
import pcgen.core.kit.KitBio;

/**
 *
 * ???
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.51 $
 */
final class KitLoader
{
	/** Creates a new instance of KitLoader */
	private KitLoader()
	{
		// Empty Constructor
	}

	/**
	 * parse the Kit in the data file
	 * @param obj
	 * @param inputLine
	 * @param sourceURL
	 * @param lineNum
	 * @throws PersistenceLayerException
	 */
	public static void parseLine(Kit obj, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		if (inputLine.startsWith("STARTPACK:"))
		{
			parseNameLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("ABILITY:"))
		{
			parseAbilityLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("NAME:")
			  || inputLine.startsWith("GENDER"))
		{
			parseBioLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("FEAT:"))
		{
			parseAbilityLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("FUNDS:"))
		{
			parseFundsLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("GEAR:"))
		{
			parseGearLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("LANGAUTO:"))
		{
			parseNameLine(obj, inputLine, sourceURL, lineNum);		// will get handled by PObjectLoader
		}
		else if (inputLine.startsWith("PROF:"))
		{
			parseProfLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("SKILL:"))
		{
			parseSkillLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("SPELLS:"))
		{
			parseSpellsLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("TEMPLATE:"))
		{
			parseTemplateLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("CLASS:"))
		{
			parseClassLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("STAT:"))
		{
			parseStatLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("ROLLSTATS:"))
		{
			parseRollLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("ALIGN:"))
		{
			parseAlignmentLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("RACE:"))
		{
			parseRaceLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("DEITY:"))
		{
			parseDeityLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("KIT:"))
		{
			parseKitLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("SELECT:"))
		{
			// For now only accept SELECT:formula
			// Future enhancement SELECT:[named var]|formula
			parseSelectLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("LEVELABILITY:"))
		{
			parseLevelAbilityLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("TABLE:"))
		{
			parseTableLine(obj, inputLine, sourceURL, lineNum);
		}
		else
		{
			Logging.errorPrint("Unknown kit info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \""
				+ inputLine + "\"");
		}
	}

	private static boolean parseCommonTags(BaseKit obj, final String tag, URL sourceURL, int lineNum)
	{
		if (tag.startsWith("PRE") || tag.startsWith("!PRE"))
		{
			obj.addPreReq(tag);
			return true;
		}
		else if (tag.startsWith("OPTION:"))
		{
			String optString = tag.substring(7);
			StringTokenizer tok = new StringTokenizer(optString, "|");
			while (tok.hasMoreTokens())
			{
				String val = tok.nextToken();
				int ind = -1;
				String lowVal;
				String highVal;
				if ((ind = val.indexOf(",")) != -1)
				{
					lowVal = val.substring(0, ind);
					highVal = val.substring(ind + 1);
				}
				else
				{
					lowVal = highVal = val;
				}
				obj.addOptionRange(lowVal, highVal);
			}
			return true;
		}
		else if (tag.startsWith("LOOKUP:"))
		{
			obj.addLookup(tag.substring(7));
			return true;
		}

		return false;
	}

	private static void parseAbilityLine(Kit aKit, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		KitAbilities kAbilities  = null;

		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();

			if (colString.startsWith("ABILITY:") || colString.startsWith("FEAT:"))
			{
				if (kAbilities == null)
				{
					if (colString.startsWith("ABILITY:"))
					{
						/* No default Category and the category is unlocked. This will
						 * throw an error if the first item is not CATEGORY= */
						kAbilities = new KitAbilities(colString.substring(8), "", false);
					}
					else
					{
						/* Default category FEAT, this is locked, so attemts to change it
						 * will generate an error */
						kAbilities = new KitAbilities(colString.substring(5), "FEAT", true);
					}
				}
				else
				{
					Logging.errorPrint("Ignoring second FEAT or ABILITY tag \"" +
							colString + "\" in Kit.parseAbilityLine");
				}
			}
			else
			{
				if (kAbilities == null)
				{
					/* this can't ever be called since this routine is only called for
					 * lines that start with FEAT: or ABILITY: */
					Logging.errorPrint("Cannot process tag, missing FEAT or ABILITY tag." +
							Constants.s_LINE_SEP + colString);

					continue;
				}

				if (colString.startsWith("FREE:"))
				{
					kAbilities.setFree(colString.charAt(5) == 'Y');
				}
				else if (colString.startsWith("COUNT:"))
				{
					kAbilities.setChoiceCount(colString.substring(6));
				}
				else
				{
					if (parseCommonTags(kAbilities, colString, sourceURL, lineNum) == false)
					{
						throw new PersistenceLayerException(
							"Unknown KitAbilities info " +
							sourceURL.toString() + ":"
							+ Integer.toString(lineNum) +
							" \"" + colString + "\"");
					}
				}
			}
		}

		aKit.addObject(kAbilities);
	}

	private static void parseAlignmentLine(Kit obj, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);
		KitAlignment kAlign = null;

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("ALIGN:"))
			{
				final String alignStr = colString.substring(6);
				if (kAlign == null)
				{
					kAlign = new KitAlignment(alignStr);
				}
				else
				{
					Logging.errorPrint("Ignoring second ALIGN tag \"" +
							colString + "\" in Kit.parseAlignmentLine at "
							 + sourceURL.toString() + ":"
							 + Integer.toString(lineNum));
				}
			}
			else
			{
				if (parseCommonTags(kAlign, colString, sourceURL, lineNum) == false)
				{
					throw new PersistenceLayerException(
						"Unknown KitAlign info " +
						sourceURL.toString() + ":" + Integer.toString(lineNum) +
						" \"" + colString + "\"");
				}
			}
		}
		obj.addObject(kAlign);
	}

	private static void parseBioLine(Kit kit, String inputLine, URL sourceURL, int lineNum)
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		KitBio kBio = null;
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("NAME:"))
			{
				if (kBio == null)
				{
					kBio = new KitBio();
				}
				kBio.setCharacterName(colString.substring(5));
			}
			else if (colString.startsWith("GENDER:"))
			{
				if (kBio == null)
				{
					kBio = new KitBio();
				}
				kBio.setGender(colString.substring(7));
			}
		}
		if (kBio != null)
		{
			kit.addObject(kBio);
		}
	}

	private static void parseFundsLine(Kit obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		KitFunds kFund = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();

			if (colString.startsWith("FUNDS:"))
			{
				if (kFund == null)
				{
					kFund = new KitFunds(colString.substring(6));
				}
				else
				{
					Logging.errorPrint("Ignoring second FUNDS tag \"" + colString + "\" in Kit.parseFundsLine");
				}
			}
			else
			{
				if (kFund == null)
				{
					Logging.errorPrint("Cannot process tag, missing FUNDS tag." + Constants.s_LINE_SEP + colString);

					continue;
				}
				else if (colString.startsWith("QTY:"))
				{
					kFund.setQty(colString.substring(4));
				}
				else
				{
					if (parseCommonTags(kFund, colString, sourceURL, lineNum) == false)
					{
						throw new PersistenceLayerException(
							"Unknown KitFunds info " + sourceURL.toString()
							+ ":"
							+ Integer.toString(lineNum) + " \"" + colString
							+ "\"");
					}
				}
			}
		}
		obj.addObject(kFund);
	}

	private static void parseGearLine(Kit obj, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		KitGear kGear = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();

			if (colString.startsWith("GEAR:"))
			{
				if (kGear == null)
				{
					kGear = new KitGear(colString.substring(5));
				}
				else
				{
					Logging.errorPrint("Ignoring second GEAR tag \"" + colString + "\" in Kit.parseGearLine");
				}
			}
			else
			{
				if (kGear == null)
				{
					Logging.errorPrint("Cannot process tag, missing GEAR tag." + Constants.s_LINE_SEP + colString);

					continue;
				}

				if (colString.startsWith("EQMOD:"))
				{
					kGear.addEqMod(colString.substring(6));
				}
				else if (colString.startsWith("QTY:"))
				{
					kGear.setQty(colString.substring(4));
				}
				else if (colString.startsWith("MAXCOST:"))
				{
					kGear.setMaxCost(colString.substring(8));
				}
				else if (colString.startsWith("SIZE:"))
				{
					kGear.setSize(colString.substring(5));
				}
				else if (colString.startsWith("LOCATION:"))
				{
					kGear.setLocation(colString.substring(9));
				}
				else if (colString.startsWith("SPROP:") || colString.startsWith("LEVEL:"))
				{
					Logging.errorPrint("unhandled parsed object in KitLoader.parseGearLine: " + colString);
				}
				else
				{
					if (parseCommonTags(kGear, colString, sourceURL, lineNum) == false)
					{
						throw new PersistenceLayerException(
							"Unknown KitGear info " + sourceURL.toString()
							+ ":"
							+ Integer.toString(lineNum) + " \"" + colString
							+ "\"");
					}
				}
			}
		}

		obj.addObject(kGear);
	}

	private static void parseNameLine(Kit obj, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		if (obj == null)
		{
			return;
		}

		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();

			if (PObjectLoader.parseTag(obj, colString))
			{
				// Here if PObjectLoader has processed tag--nothing else to do
			}
			else if (colString.startsWith("STARTPACK:"))
			{
				obj.setName(colString.substring(10));
			}
			else if (colString.startsWith("EQUIPBUY:"))
			{
				obj.setBuyRate(colString.substring(9));
			}
			else if (colString.startsWith("EQUIPSELL:"))
			{
				obj.setSellRate(colString.substring(10));
			}
			else if (colString.startsWith("VISIBLE:"))
			{
				obj.setVisible(colString.substring(8));
			}
			else if (colString.startsWith("APPLY:"))
			{
				obj.setApplyMode(colString.substring(6));
			}
			else
			{
				throw new PersistenceLayerException("Unknown KitPack info " + sourceURL.toString() + ":"
					+ Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
	}

	private static void parseProfLine(Kit obj, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		KitProf kProf = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();

			if (colString.startsWith("PROF:"))
			{
				if (kProf == null)
				{
					kProf = new KitProf(colString.substring(5));
				}
				else
				{
					Logging.errorPrint("Ignoring second PROF tag \"" + colString + "\" in Kit.parseProfLine");
				}
			}
			else
			{
				if (kProf == null)
				{
					Logging.errorPrint("Cannot process tag, missing PROF tag." + Constants.s_LINE_SEP + colString);

					continue;
				}

				if (colString.startsWith("COUNT:"))
				{
					kProf.setChoiceCount(colString.substring(6));
				}
				else if (colString.startsWith("RACIAL:"))
				{
					kProf.setRacialProf(colString.charAt(7) == 'Y');
				}
				else
				{
					if (parseCommonTags(kProf, colString, sourceURL, lineNum) == false)
					{
						throw new PersistenceLayerException(
							"Unknown KitProf info " + sourceURL.toString()
							+ ":"
							+ Integer.toString(lineNum) + " \"" + colString
							+ "\"");
					}
				}
			}
		}

		obj.addObject(kProf);
	}

	private static void parseSkillLine(Kit obj, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		KitSkill kSkill = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();

			if (colString.startsWith("SKILL:"))
			{
				if (kSkill == null)
				{
					kSkill = new KitSkill(colString.substring(6));
				}
				else
				{
					Logging.errorPrint("Ignoring second SKILL tag \"" + colString + "\" in Kit.parseSkillLine");
				}
			}
			else
			{
				if (kSkill == null)
				{
					Logging.errorPrint("Cannot process tag, missing SKILL tag." + Constants.s_LINE_SEP + colString);

					continue;
				}

				if (colString.startsWith("RANK:"))
				{
					kSkill.setRank(colString.substring(5));
				}
				else if (colString.startsWith("FREE:"))
				{
					kSkill.setFree(colString.substring(5).startsWith("Y"));
				}
				else if (colString.startsWith("CLASS:"))
				{
					kSkill.setClassName(colString.substring(6));
				}
				else if (colString.startsWith("COUNT:"))
				{
					kSkill.setChoiceCount(colString.substring(6));
				}
				else
				{
					if (parseCommonTags(kSkill, colString, sourceURL, lineNum) == false)
					{
						throw new PersistenceLayerException(
							"Unknown KitSkill info " + sourceURL.toString()
							+ ":"
							+ Integer.toString(lineNum) + " \"" + colString
							+ "\"");
					}
				}
			}
		}

		obj.addObject(kSkill);
	}

	private static void parseSpellsLine(Kit obj, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		final KitSpells kSpells = new KitSpells();
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();

			if (colString.startsWith("SPELLS:"))
			{
				final StringTokenizer aTok = new StringTokenizer(colString.substring(7), "|");

				String spellbook = Globals.getDefaultSpellBook();
				String castingClass = null;
				while (aTok.hasMoreTokens())
				{
					String field = aTok.nextToken();
					if (field.startsWith("SPELLBOOK="))
					{
						spellbook = field.substring(10);
					}
					else if (field.startsWith("CLASS="))
					{
						castingClass = field.substring(6);
					}
					else
					{
						String countStr = null;
						if (field.indexOf("=") != -1)
						{
							countStr = field.substring(field.indexOf("=")+1);
							field = field.substring(0,field.indexOf("="));
						}
						final StringTokenizer subTok = new StringTokenizer(field, "[]");
						final String spell = subTok.nextToken();
						ArrayList featList = new ArrayList();
						while (subTok.hasMoreTokens())
						{
							featList.add(subTok.nextToken());
						}
						kSpells.addSpell(castingClass, spellbook, spell, featList, countStr);
					}
				}
			}
			else if (colString.startsWith("COUNT:"))
			{
				kSpells.setCountFormula(colString.substring(6));
			}
			else
			{
				if (parseCommonTags(kSpells, colString, sourceURL, lineNum) == false)
				{
					throw new PersistenceLayerException(
						"Unknown KitSpells info " + sourceURL.toString() + ":"
						+ Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
		}

		obj.addObject(kSpells);
	}

	private static void parseTemplateLine(Kit obj, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		KitTemplate kTemplate = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("TEMPLATE:"))
			{
				if (kTemplate == null)
				{
					kTemplate = new KitTemplate(colString.substring(9));
				}
				else
				{
					Logging.errorPrint("Ignoring second TEMPLATE tag \"" + colString +
									   "\" in Kit.parseTemplateLine ("
									   + sourceURL.toString() + ": " + lineNum + ")");
				}
			}
			else
			{
				if (parseCommonTags(kTemplate, colString, sourceURL, lineNum) == false)
				{
					throw new PersistenceLayerException(
						"Unknown KitTemplate info " + sourceURL.toString()
						+ ":"
						+ Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
		}
		obj.addObject(kTemplate);
	}

	private static void parseClassLine(Kit kit, String inputLine, URL sourceURL, int lineNum)
	{
		KitClass kClass = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();

			if (colString.startsWith("CLASS:"))
			{
				if (kClass == null)
				{
					kClass = new KitClass(colString.substring(6));
				}
				else
				{
					Logging.errorPrint("Ignoring second CLASS tag \"" + colString +
									   "\" in Kit.parseClassLine ("
									   + sourceURL.toString() + ": " + lineNum + ")");
				}
			}
			else if (colString.startsWith("SUBCLASS:"))
			{
				kClass.setSubClass(colString.substring(9));
			}
			else if (colString.startsWith("LEVEL:"))
			{
				kClass.setLevel(colString.substring(6));
			}
			else
			{
				if (parseCommonTags(kClass, colString, sourceURL, lineNum) == false)
				{
					Logging.errorPrint("Unknown tag in CLASS line tag \""
									   + colString +
									   "\" in Kit.parseClassLine ("
									   + sourceURL.toString() + ": " + lineNum
									   + ")");
				}
			}
		}
		kit.addObject(kClass);
	}

	private static void parseStatLine(Kit kit, String inputLine, URL sourceURL, int lineNum)
	{
		KitStat stats = null;
		// Remove the STAT:
		final StringTokenizer aTok = new StringTokenizer(inputLine.substring(5),
			"|");

		while (aTok.hasMoreTokens())
		{
			final String statStr = aTok.nextToken();
			// STAT:value
			final int equalInd = statStr.indexOf("=");
			if (equalInd < 0)
			{
				Logging.errorPrint("Invalid STAT tag \"" + statStr + "\" in " +
								   sourceURL.toString() + ": " + lineNum);
				continue;
			}
			final String statType = statStr.substring(0, equalInd);
			final String statVal = statStr.substring(equalInd + 1);
			stats = new KitStat(statType, statVal);
			kit.addStat(stats);
		}
	}

	private static void parseRaceLine(Kit kit, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		KitRace kRace = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("RACE:"))
			{
				if (kRace == null)
				{
					kRace = new KitRace(colString.substring(5));
				}
				else
				{
					Logging.errorPrint("Ignoring second RACE tag \"" + colString +
									   "\" in Kit.parseRaceLine ("
									   + sourceURL.toString() + ": " + lineNum + ")");
				}
			}
			else
			{
				if (parseCommonTags(kRace, colString, sourceURL, lineNum) == false)
				{
					throw new PersistenceLayerException("Unknown KitRace info "
						+ sourceURL.toString() + ":"
						+ Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
		}
		kit.addObject(kRace);
	}

	private static void parseDeityLine(Kit kit, String inputLine, URL sourceURL, int lineNum)
	{
		KitDeity kDeity = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			String colString = colToken.nextToken();

			if (colString.startsWith("DEITY:"))
			{
				if (kDeity == null)
				{
					kDeity = new KitDeity(colString.substring(6));
				}
				else
				{
					Logging.errorPrint("Ignoring second DEITY tag \"" + colString +
									   "\" in Kit.parseDeityLine ("
									   + sourceURL.toString() + ": " + lineNum + ")");
				}
			}
			else if (colString.startsWith("DOMAIN:"))
			{
				colString = colString.substring(7);
				final StringTokenizer pTok = new StringTokenizer(colString, "|");
				while (pTok.hasMoreTokens())
				{
					final String domain = pTok.nextToken();
					kDeity.addDomain(domain);
				}
			}
			else if (colString.startsWith("COUNT:"))
			{
				kDeity.setCountFormula(colString.substring(6));
			}
			else
			{
				if (parseCommonTags(kDeity, colString, sourceURL, lineNum) == false)
				{
					Logging.errorPrint("Invalid DEITY tag \"" + colString
									   + "\" in " +
									   sourceURL.toString() + ": " + lineNum);
				}
			}
		}
		kit.addObject(kDeity);
	}

	private static void parseKitLine(Kit kit, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		KitKit kKit = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("KIT:"))
			{
				if (kKit == null)
				{
					kKit = new KitKit(colString.substring(4));
				}
				else
				{
					Logging.errorPrint("Ignoring second KIT tag \"" + colString +
									   "\" in Kit.parseKitLine ("
									   + sourceURL.toString() + ": " + lineNum + ")");
				}
			}
			else
			{
				if (parseCommonTags(kKit, colString, sourceURL, lineNum) == false)
				{
					throw new PersistenceLayerException("Unknown KitKit info "
						+ sourceURL.toString() + ":"
						+ Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
		}
		kit.addObject(kKit);
	}

	private static void parseSelectLine(Kit kit, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		KitSelect kSelect = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("SELECT:"))
			{
				if (kSelect == null)
				{
					kSelect = new KitSelect(colString.substring(7));
				}
				else
				{
					Logging.errorPrint("Ignoring second SELECT tag \"" + colString +
				   "\" in Kit.parseSelectLine ("
				   + sourceURL.toString() + ": " + lineNum + ")");
				}
			}
			else
			{
				if (parseCommonTags(kSelect, colString, sourceURL, lineNum) == false)
				{
					throw new PersistenceLayerException(
						"Unknown KitSelect info " + sourceURL.toString() + ":"
						+ Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
		}
		kit.addObject(kSelect);
	}

	private static void parseLevelAbilityLine(Kit kit, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		KitLevelAbility kLA = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("LEVELABILITY:"))
			{
				if (kLA == null)
				{
					kLA = new KitLevelAbility();
				}
				else
				{
					Logging.errorPrint("Ignoring second SELECT tag \"" + colString +
				   "\" in Kit.parseSelectLine ("
				   + sourceURL.toString() + ": " + lineNum + ")");
				}

				String classInfo = colString.substring(13);
				int levelInd = classInfo.indexOf("=");
				if (levelInd < 0)
				{
					throw new PersistenceLayerException(
						"Invalid level in KitLevelAbility info " + sourceURL.toString() + ":"
						+ Integer.toString(lineNum) + " \"" + colString + "\"");
				}
				kLA.setClass(classInfo.substring(0, levelInd));
				try
				{
					kLA.setLevel(Integer.parseInt(classInfo.substring(levelInd+1)));
				}
				catch (NumberFormatException e)
				{
					throw new PersistenceLayerException(
						"Invalid level in KitLevelAbility info " + sourceURL.toString() + ":"
						+ Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
			else if (colString.startsWith("ABILITY:"))
			{
				StringTokenizer pipeTok = new StringTokenizer(colString.substring(8), "|");
				String ability = pipeTok.nextToken();
				ArrayList choices = new ArrayList();
				while (pipeTok.hasMoreTokens())
				{
					choices.add(pipeTok.nextToken());
				}
				if (choices.size() < 1)
				{
					throw new PersistenceLayerException(
						"Missing choice in KitLevelAbility info " + sourceURL.toString() + ":"
						+ Integer.toString(lineNum) + " \"" + colString + "\"");
				}
				kLA.addAbility(ability, choices);
			}
			else
			{
				if (parseCommonTags(kLA, colString, sourceURL, lineNum) == false)
				{
					throw new PersistenceLayerException(
						"Unknown KitLevelAbility info " + sourceURL.toString() + ":"
						+ Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
		}
		kit.setDoLevelAbilities(false);
		kit.addObject(kLA);
	}

	private static void parseRollLine(Kit kit, String inputLine, URL sourceURL, int lineNum)
	{
		// TODO Do Nothing
	}

	private static void parseTableLine(Kit kit, String inputLine, URL sourceURL, int lineNum)
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);
		String tableName = "";
		while (colToken.hasMoreTokens())
		{
			String colString = colToken.nextToken();
			if (colString.startsWith("TABLE:"))
			{
				tableName = colString.substring(6);
				kit.addLookupTable(tableName);
			}
			else if (colString.startsWith("VALUES:"))
			{
				colString = colString.substring(7);
				final StringTokenizer fieldToken = new StringTokenizer(colString, "|");
				while (fieldToken.hasMoreTokens())
				{
					String value = fieldToken.nextToken();
					String range = fieldToken.nextToken();
					int ind = -1;
					String lowVal;
					String highVal;
					if ((ind = range.indexOf(",")) != -1)
					{
						lowVal = range.substring(0, ind);
						highVal = range.substring(ind + 1);
					}
					else
					{
						lowVal = highVal = range;
					}
					kit.addLookupValue(tableName, value, lowVal, highVal);
				}
			}
		}
	}
}
