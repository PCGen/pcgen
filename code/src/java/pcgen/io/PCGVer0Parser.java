/*
 * PCGVer0Parser.java
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
 * Created on March 15, 2002, 4:30 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.core.Campaign;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Globals;
import pcgen.core.NoteItem;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.SpecialProperty;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellInfo;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.io.parsers.CharacterDomainParser;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * <code>PCGVer0Parser</code>
 * @author Thomas Behr
 * @version $Revision$
 **/
final class PCGVer0Parser implements PCGParser
{
	private List<String> warnings = new ArrayList<String>();
	private PlayerCharacter aPC;

	/**
	 * Used only for legacy (pre 2.3.0) Domain class pcg files
	 **/
	private int ignoreDomainClassLine = 0;

	/**
	 * Version of file being read values can be
	 * 0 - oh oh
	 * 1 - hit points are no longer written with the CON modifier
	 * 2 - skills are saved by class
	 */
	private int pcgVersion;

	/**
	 * Constructor
	 * @param aPC
	 **/
	PCGVer0Parser(PlayerCharacter aPC)
	{
		this.aPC = aPC;
	}

	/**
	 * Selector
	 *
	 * @return a list of warning messages
	 **/
	public List<String> getWarnings()
	{
		return warnings;
	}

	/**
	 * parse a String in PCG format
	 *
	 * @param lines   the String to parse
	 * @throws PCGParseException
	 **/
	public void parsePCG(String[] lines) throws PCGParseException
	{
		int i = 0;

		if (lines.length == 0)
		{
			return;
		}

		if (checkCampaignLine(lines[i]))
		{
			i++;
		}

		pcgVersion = parseVersionLine(lines[i]);

		/*
		 * pcgVersion < 0:
		 *   no version tag
		 * pcgVersion > -1:
		 *   ignore second line (version line)
		 */
		if (pcgVersion > -1)
		{
			i++;
		}

		parseNameLine(lines[i++]);
		parseStatsLine(lines[i++]);
		parseClassesLine(lines[i++]);
		parseFeatsLine(lines[i++]);

		//Note, the following order is neccessary, for historical reasons...
		parseRaceLine(lines[i + 2]);
		parseSkillsLine(lines[i++]);
		parseDeityLine(lines[i++]);
		i++;

		// this loads the old version of spells
		// where all the spells were on a single line
		// new version (>254) has each spell on seperate line
		// spells were revamped again with 272, so support
		// for previous version of spells has been dropped
		// assuming enough time has passed
		//
		// first, get the auto known spells line
		i = parseAutoSpellsLine(lines, i);

		// then parse all the spell lines (if any)
		if (pcgVersion < 272)
		{
			i = parseOldSpellLine(lines, i);
		}
		else
		{
			i = parseSpellLine(lines, i);
		}

		parseLanguagesLine(lines[i++]);

		final int weaponProfLine = i++;

		//		parseWeaponProfLine(lines[i++]);
		parseUnusedPointsLine(lines[i++]);
		parseMiscLine(lines[i++]);
		parseEquipmentLine(lines[i++]);

		if (pcgVersion > 254)
		{
			parsePortraitLine(lines[i++]);
		}

		i = parseGoldBioDescriptionLine(lines, i);

		int dx = 0;

		for (Iterator it = aPC.getClassList().iterator(); it.hasNext(); it
			.next())
		{
			if (++dx == ignoreDomainClassLine)
			{
				i++;
			}

			parseClassesSkillLine(lines[i++]);
		}

		if (++dx == ignoreDomainClassLine)
		{
			i++;
		}

		i = parseExperienceAndMiscLine(lines, i);
		i = parseClassSpecialtyAndSaveLines(lines, i);

		if (i < lines.length)
		{
			parseTemplateLine(lines[i++]);
		}

		i = parseEquipSetLine(lines, i);
		i = parseFollowerLine(lines, i);
		i = parseNoteLine(lines, i);

		//
		// This needs to be called after the templates are loaded
		//
		parseWeaponProfLine(lines[weaponProfLine]);
	}

	private static boolean checkCampaignLine(String line)
		throws PCGParseException
	{
		if (line.startsWith("CAMPAIGNS:"))
		{
			loadCampaignsForPC(line);

			if (!Globals.displayListsHappy())
			{
				Logging
					.errorPrint("Insufficient campaign information to load character file.");
				throw new PCGParseException("checkCampaignLine", line,
					"Insufficient campaign information to load character file.");
			}

			return true;
		}

		return false;
	}

	private static void loadCampaignsForPC(String line)
		throws PCGParseException
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":", false);
		final String sCamp = aTok.nextToken();

		//if the pcg file starts with CAMPAIGNS data then lets process it
		if ("CAMPAIGNS".equals(sCamp))
		{
			if (SettingsHandler.isLoadCampaignsWithPC())
			{
				final List<Campaign> campList = new ArrayList<Campaign>();

				while (aTok.hasMoreTokens())
				{
					Campaign aCamp = Globals.getCampaignKeyed(aTok.nextToken());

					if (aCamp != null
						&& aCamp
							.isGameMode(SettingsHandler.getGame().getName()))
					{
						if (!aCamp.isLoaded())
						{
							campList.add(aCamp);
						}
					}
				}

				if (campList.size() > 0)
				{
					try
					{
						//PersistenceObserver observer = new PersistenceObserver();
						PersistenceManager pManager =
								PersistenceManager.getInstance();
						//pManager.addObserver( observer );
						pManager.loadCampaigns(campList);
						//pManager.deleteObserver( observer );
					}
					catch (PersistenceLayerException e)
					{
						throw new PCGParseException("loadCampaignsForPC", line,
							e.getMessage());
					}

					if (Globals.getUseGUI())
					{
						pcgen.gui.PCGen_Frame1.getInst().getMainSource()
							.updateLoadedCampaignsUI();
					}
				}
			}
		}
	}

	private static Float parseCarried(Float qty, String aName)
	{
		float carried;

		if ("Y".equals(aName))
		{
			carried = qty.floatValue();
		}
		else if ("N".equals(aName))
		{
			carried = 0.0F;
		}
		else
		{
			try
			{
				carried = Float.parseFloat(aName);
			}
			catch (NumberFormatException e)
			{
				carried = 0.0F;
			}
		}

		return new Float(carried);
	}

	/**
	 * parseVersionLine should return 220 if string is 2.2.0
	 * @param line
	 * @return int
	 * @throws PCGParseException
	 **/
	private static int parseVersionLine(String line) throws PCGParseException
	{
		int version = -1;
		StringTokenizer aTok = new StringTokenizer(line, ":");
		final String tag = (aTok.hasMoreTokens()) ? aTok.nextToken() : "";
		final String ver = (aTok.hasMoreTokens()) ? aTok.nextToken() : "";

		// if the pcg file starts with VERSION data then lets process it
		try
		{
			if ("VERSION".equals(tag))
			{
				aTok = new StringTokenizer(ver, ".");
				version = 0;

				while (aTok.hasMoreTokens())
				{
					version =
							(version * 10) + Integer.parseInt(aTok.nextToken());
				}
			}
		}
		catch (NumberFormatException ex)
		{
			throw new PCGParseException("parseVersionLine", line, ex
				.getMessage());
		}

		return version;
	}

	/**
	 * see if this character should get auto known spells like a monkey
	 * @param lines
	 * @param i
	 * @return int
	 **/
	private int parseAutoSpellsLine(String[] lines, int i)
	{
		if (i >= lines.length)
		{
			return i;
		}

		if (lines[i].startsWith("AUTOSPELLS:NO"))
		{
			aPC.setAutoSpells(false);
			i++;
		}

		if (lines[i].startsWith("AUTOSPELLS:YES"))
		{
			aPC.setAutoSpells(true);
			i++;
		}

		return i;
	}

	private int parseClassSpecialtyAndSaveLines(String[] lines, int start)
		throws PCGParseException
	{
		int current = start;

		try
		{
			String line;
			String token;
			StringTokenizer aTok;

			ListKey<String> saveKey = ListKey.SAVE;

			for (int i = 0; i < aPC.getClassList().size(); i++)
			{
				line = lines[current++];

				if (line == null)
				{
					return current;
				}

				aTok = new StringTokenizer(line, ":", true);

				String bString = aTok.nextToken();
				PCClass aClass = aPC.getClassKeyed(bString);

				if ((aClass == null) || "Domain".equals(aClass.getKeyName()))
				{
					continue;
				}

				while (aTok.hasMoreTokens())
				{
					token = aTok.nextToken();

					if (token.startsWith("SPECIAL"))
					{
						aClass.addSpecialty(token.substring(7));
					}
					else
					{
						/**
						 * This no longer needs to be saved in the PCG file.
						 * Need to strip it from older versions of
						 * save files. Gets handled differently in class
						 */
						if ("Smite Evil".equals(token) || ":".equals(token))
						{
							continue;
						}

						if (token.startsWith("BONUS"))
						{
							aClass.addBonusList(token.substring(6));

							if (token.lastIndexOf("|PCLEVEL|") > -1)
							{
								String tmp =
										token.substring(token
											.lastIndexOf("PCLEVEL"));
								StringTokenizer cTok =
										new StringTokenizer(tmp, "|");
								cTok.nextToken(); // should be PCLEVEL

								if (cTok.hasMoreTokens())
								{
									SpecialAbility sa =
											new SpecialAbility(
												"Bonus Caster Level for "
													+ cTok.nextToken());

									if ((pcgVersion > 270)
										&& cTok.hasMoreTokens())
									{
										sa.setSASource("PCCLASS|"
											+ aClass.getKeyName() + "|"
											+ cTok.nextToken());
									}

									aClass.addSpecialAbilityToList(sa);
								}
							}
						}
						else if (!aPC.hasSpecialAbility(token))
						{
							SpecialAbility sa = new SpecialAbility(token);
							String src = "";

							if ((pcgVersion > 270) && aTok.hasMoreTokens())
							{
								src = aTok.nextToken();
							}

							if (":".equals(src))
							{
								src = "";
							}

							//sa.setSource(src);
							aClass.addSpecialAbilityToList(sa);
						}

						if (!aClass.containsInList(saveKey, token)
							|| token.startsWith("BONUS"))
						{
							aClass.addSave(token);
						}
					}
				}
			}
		}
		catch (NumberFormatException ex)
		{
			throw new PCGParseException("parseClassSpecialtyAndSaveLines",
				lines[current], ex.getMessage());
		}

		return current;
	}

	private void parseClassesLine(String line) throws PCGParseException
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");
		String aName;
		boolean getNext = true;
		String aString = "";
		int x = 0;

		while (aTok.hasMoreTokens())
		{
			if (getNext)
			{
				x++;
				aName = aTok.nextToken();
			}
			else
			{
				aName = aString;
			}

			getNext = true;

			if (!aTok.hasMoreTokens())
			{
				break;
			}

			boolean needCopy = true;
			PCClass aClass = aPC.getClassKeyed(aName);

			if (aClass == null)
			{
				aClass = Globals.getClassKeyed(aName);
			}
			else
			{
				needCopy = false;
			}

			if ((aClass == null) && aName.equalsIgnoreCase("Domain"))
			{
				Logging
					.errorPrint("Domain class found and ignored. "
						+ "Please check character to verify conversion is successful.");
				ignoreDomainClassLine = x;
			}
			else if (aClass == null)
			{
				final String msg =
						PropertyFactory.getFormattedString(
							"Exceptions.PCGenParser.ClassNotFound", //$NON-NLS-1$
							aName);
				throw new PCGParseException("parseClassesLine", line, msg); //$NON-NLS-1$
			}

			// ClassName:SubClassName:ProhibitedString:Level:[hp1:[hp2:...[hpn:]]]skillPool:SpellBaseStat:
			// If the class wasn't found we will parse through the data anyway, but just toss it
			String subClassKey = aTok.nextToken().trim();
			String prohibitedString = aTok.nextToken().trim();
			int k = Integer.parseInt(aTok.nextToken());

			if (aClass != null)
			{
				if (needCopy)
				{
					aClass = (PCClass) aClass.clone();
					aPC.getClassList().add(aClass);
				}

				aClass.setSubClassKey(subClassKey);
				PCClassLstToken token =
						(PCClassLstToken) TokenStore.inst().getTokenMap(
							PCClassLstToken.class).get("PROHIBITED");
				token.parse(aClass, prohibitedString, -9);
			}

			//
			// NOTE: race is not yet set here, so skillpool calculated in addLevel will be out by
			// racial intelligence adjustment and BonusSkillsPerLevel, but we're just going to trash
			// the calculated value in the next step anyway
			//
			for (int i = 0; i < k; ++i)
			{
				int iHp = Integer.parseInt(aTok.nextToken());

				if (aClass != null)
				{
					PCLevelInfo levelInfo =
							aPC.saveLevelInfo(aClass.getKeyName());
					aClass.addLevel(levelInfo, false, aPC);
					aClass.setHitPoint(i, Integer.valueOf(iHp));
					aPC.saveLevelInfo(aClass.getKeyName());
				}
			}

			int skillPool = Integer.parseInt(aTok.nextToken());

			if (aClass != null)
			{
				aClass.setSkillPool(skillPool);
			}

			if (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();

				if ((SettingsHandler.getGame().getStatFromAbbrev(
					aString.toUpperCase()) > -1)
					|| aString.equalsIgnoreCase(Constants.s_NONE)
					|| "Any".equalsIgnoreCase(aString)
					|| "SPELL".equalsIgnoreCase(aString))
				{
					if (aClass != null)
					{
						aClass.setSpellBaseStat(aString);
					}
				}
				else
				{
					getNext = false;
				}
			}
		}

		aPC.setCurrentHP(aPC.hitPoints());
	}

	private void parseClassesSkillLine(String line)
	{
		// don't do anything because we don't store the class
		// skills in the .pcg anymore, they are in the class.lst file
	}

	private void parseDeityLine(String line)
	{
		final StringTokenizer deityTokenizer = new StringTokenizer(line, ":");
		String token;

		for (int i = 0; deityTokenizer.hasMoreElements(); i++)
		{
			token = deityTokenizer.nextToken();

			switch (i)
			{
				case 0:

					boolean deityFound = false;
					Deity aDeity;

					for (Iterator it = Globals.getDeityList().iterator(); it
						.hasNext();)
					{
						aDeity = (Deity) it.next();

						if (aDeity.toString().equals(token))
						{
							aPC.setDeity(aDeity);
							deityFound = true;

							break;
						}
					}

					if (!deityFound && !token.equals(Constants.s_NONE))
					{
						final String msg =
								PropertyFactory.getFormattedString(
									"Warnings.PCGenParser.DeityNotFound", //$NON-NLS-1$
									token);
						warnings.add(msg);
					}

					break;

				default:

					int j = aPC.indexOfFirstEmptyCharacterDomain();

					if (j == -1)
					{
						CharacterDomain aCD = new CharacterDomain();
						aPC.addCharacterDomain(aCD);
						j = aPC.getCharacterDomainList().size() - 1;
					}

					if (j >= 0)
					{
						final StringTokenizer cdTok =
								new StringTokenizer(token, "=", false);
						final String domainName = cdTok.nextToken();
						CharacterDomain aCD =
								aPC.getCharacterDomainList().get(j);
						Domain aDomain = Globals.getDomainKeyed(domainName);

						if (aDomain != null)
						{
							//aDomain = (Domain)aDomain.clone();	// gets cloned by setDomain, so commented it out
							aDomain = aCD.setDomain(aDomain, aPC);

							while (cdTok.hasMoreTokens())
							{
								String sSource = cdTok.nextToken();

								if (sSource.startsWith("LIST|"))
								{
									aDomain.addAllToAssociated(CoreUtility
										.split(sSource.substring(5), '|'));
								}
								else
								{
									CharacterDomainParser parser =
											new CharacterDomainParser();
									parser.setDomainSource(aCD, sSource);
								}
							}

							aDomain.setIsLocked(true, aPC);
						}
						else
						{
							if (!domainName.equals(Constants.s_NONE))
							{
								final String msg =
										PropertyFactory
											.getFormattedString(
												"Warnings.PCGenParser.DomainNotFound", //$NON-NLS-1$
												token);
								warnings.add(msg);
							}
						}
					}
			}
		}
	}

	private int parseEquipSetLine(String[] lines, int i)
	{
		if (i >= lines.length)
		{
			return i;
		}

		String aString = lines[i];

		while (aString.startsWith("EQUIPSET:"))
		{
			EquipSet aSet;
			Equipment eqT = null;
			Equipment eqI;
			Equipment eq;
			StringTokenizer aTok =
					new StringTokenizer(aString.substring(9), ":", false);
			String id = aTok.nextToken();
			String name = aTok.nextToken();
			aSet = new EquipSet(id, name);

			if (aTok.hasMoreTokens())
			{
				String value = aTok.nextToken();
				aSet.setValue(value);
				eqI = EquipmentList.getEquipmentNamed(value);

				if (eqI == null)
				{
					final String message =
							"parseEquipSetLine: equipment not found: " + value;
					warnings.add(message);
				}
				else
				{
					eq = (Equipment) eqI.clone();

					final StringTokenizer iTok =
							new StringTokenizer(id, ".", false);

					// see if the Quantity is set
					if (aTok.hasMoreTokens())
					{
						final float fNum = Float.parseFloat(aTok.nextToken());
						final Float num = new Float(fNum);
						aSet.setQty(num);
						eq.setQty(num);
						eq.setNumberCarried(num);
					}

					// if the idPath is longer than 3
					// it's inside a container
					if (iTok.countTokens() > 3)
					{
						// get parent EquipSet
						EquipSet es =
								aPC.getEquipSetByIdPath(aSet.getParentIdPath());

						// get the container
						if (es != null)
						{
							eqT = es.getItem();
						}

						// add the child to container
						if (eqT != null)
						{
							eqT.insertChild(aPC, eq);
							eq.setParent(eqT);
						}
					}

					aSet.setItem(eq);
				}
			}

			if (aSet != null)
			{
				aPC.addEquipSet(aSet);
			}

			i++;

			if (i >= lines.length)
			{
				return i;
			}

			aString = lines[i];
		}

		return i;
	}

	private void parseEquipmentLine(String line)
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");
		String aName;
		Equipment eq;
		final Map<String, String> containers = new HashMap<String, String>();
		boolean bFound;
		final List<Equipment> headerChildren = new ArrayList<Equipment>();

		while (aTok.hasMoreTokens())
		{
			aName = aTok.nextToken().trim();

			String customName = "";

			if ((aName.indexOf(";NAME=") > -1)
				|| (aName.indexOf(";SIZE=") > -1)
				|| (aName.indexOf(";EQMOD=") > -1)
				|| (aName.indexOf(";ALTEQMOD=") > -1)
				|| (aName.indexOf(";SPROP=") > -1)
				|| (aName.indexOf(";COSTMOD=") > -1)
				|| (aName.indexOf(";WEIGHTMOD=") > -1))
			{
				final int idx = aName.indexOf(';');
				final String baseItemKey = aName.substring(0, idx);
				String aLine = aName.substring(idx + 1);

				//
				// Get base item (must have to modify)
				//
				final Equipment aEq =
						EquipmentList.getEquipmentKeyed(baseItemKey);

				if (aEq != null)
				{
					eq = (Equipment) aEq.clone();
					eq.load(aLine, ";", "=", aPC);
					if (!aEq.isType(Constants.s_CUSTOM))
					{
						aEq.addMyType(Constants.s_CUSTOM);
					}
					EquipmentList.addEquipment((Equipment) eq.clone());
					bFound = true;
				}
				else
				{
					// dummy container to stuff equipment info into
					eq = new Equipment();
					bFound = false;
				}
			}
			else
			{
				final StringTokenizer anTok = new StringTokenizer(aName, ";");
				String sized = "";
				String head1 = "";
				String head2 = "";
				String customProp = "";
				int tokenCount = anTok.countTokens();

				if ((tokenCount >= 4) && (tokenCount <= 6))
				{
					//
					// baseName;size;head1;head2
					// name;baseName;size;head1;head2
					// name;baseName;size;head1;head2;sprop
					//
					if (tokenCount >= 5)
					{
						customName = anTok.nextToken();
					}

					String baseName = anTok.nextToken();
					sized = anTok.nextToken();
					head1 = anTok.nextToken();
					head2 = anTok.nextToken();
					aName = baseName;

					if (tokenCount == 6)
					{
						customProp = anTok.nextToken();
					}
				}

				Equipment aEq = EquipmentList.getEquipmentKeyed(aName);

				if (aEq == null)
				{
					// Try to strip the modifiers off item
					aEq = EquipmentList.getEquipmentFromName(aName, aPC);
				}

				bFound = true;

				if (aEq == null)
				{
					// dummy container to stuff equipment info into
					eq = new Equipment();
					bFound = false;
				}
				else
				{
					eq = (Equipment) aEq.clone();

					if ((customName.length() == 0)
						&& ((eq.getEqModifierList(true).size() + eq
							.getEqModifierList(false).size()) != 0))
					{
						customName = aName;
					}
				}

				if (customProp.length() != 0)
				{
					eq.addSpecialProperty(SpecialProperty
						.createFromLst(customProp));
				}

				eq.addEqModifiers(head1, true);
				eq.addEqModifiers(head2, false);

				if (((sized.length() != 0) && !eq.getSize().equals(sized))
					|| ((eq.getEqModifierList(true).size() + eq
						.getEqModifierList(false).size()) != 0)
					|| (customProp.length() != 0))
				{
					if (sized.length() == 0)
					{
						sized = eq.getSize();
					}

					eq.resizeItem(aPC, sized);
					eq.nameItemFromModifiers(aPC);
				}

				//
				// If item doesn't exist, add it
				// to the global equipment list
				//
				if (bFound)
				{
					if (customName.length() > 0)
					{
						eq.setName(customName);
					}

					EquipmentList.addEquipment((Equipment) eq.clone());
				}
			}

			eq.setQty(aTok.nextToken());

			// Output index determines the order things appear on a character sheet, it was added in v 2.6.9
			if (pcgVersion >= 269)
			{
				eq.setOutputIndex(Integer.parseInt(aTok.nextToken()));
			}

			aTok.nextToken();

			final StringTokenizer bTok =
					new StringTokenizer(aTok.nextToken(), "@", false);
			eq.setCarried(parseCarried(new Float(eq.qty()), bTok.nextToken()));

			if (bTok.hasMoreTokens())
			{
				containers.put(eq.getKeyName(), bTok.nextToken());
			}

			eq.setLocation(Equipment.getLocationNum(aTok.nextToken()));

			if (eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS)
			{
				eq.setNumberEquipped(Integer.parseInt(aTok.nextToken()));
			}

			if (bFound)
			{
				aPC.addEquipment(eq);
				aPC.equipmentListAddAll(headerChildren);
			}
			else
			{
				//
				// Only show message if not natural weapon
				//
				if (aName.indexOf("Natural/") < 0)
				{
					final String msg =
							PropertyFactory.getFormattedString(
								"Warnings.PCGenParser.EquipmentNotFound", //$NON-NLS-1$
								aName);
					warnings.add(msg);
				}
			}
		}

		//now insert parent/child relationships
		Equipment aParent;

		for (Iterator<String> it = containers.keySet().iterator(); it.hasNext();)
		{
			aName = it.next();
			eq = aPC.getEquipmentNamed(aName);

			if (eq != null)
			{
				final String containerName = containers.get(aName);
				aParent = aPC.getEquipmentNamed(containerName);

				if (aParent != null)
				{
					aParent.insertChild(aPC, eq);
				}
				else
				{
					Logging.errorPrint("Container \"" + containerName
						+ "\" not found for \"" + aName + "\"");
				}
			}
		}
	}

	private int parseExperienceAndMiscLine(String[] lines, int start)
		throws PCGParseException
	{
		int current = start;

		try
		{
			int i = 0;
			boolean nextLine = true;
			String line = "";
			String cString = "";

			while (i < 6)
			{
				if (nextLine)
				{
					line = lines[current++];
				}

				int k = line.indexOf(':');

				while ((k > 0) && (line.charAt(k - 1) == '\\'))
				{
					k = line.indexOf(':', k + 1);
				}

				if ((k < 0) || (line.charAt(k - 1) == '\\'))
				{
					k = -1;
				}

				if (k == -1)
				{
					cString = cString.concat(line);
					cString = cString.concat(Constants.s_LINE_SEP);
					nextLine = true;

					//EOL so don't try 4 or 5, it'll break old PCG files
					if (i > 3)
					{
						break;
					}
				}
				else
				{
					k = line.indexOf(':');

					while (line.charAt(k - 1) == '\\')
					{
						k = line.indexOf(':', k + 1);
					}

					cString = cString.concat(line.substring(0, k));

					switch (i)
					{
						case 0:
							aPC.setXP(Integer.parseInt(cString));

							break;

						case 1:
						case 2:
						case 3:

							String tempStr = "";

							for (int j = 0; j < cString.length(); j++)
							{
								if (cString.charAt(j) != '\\')
								{
									tempStr += cString.charAt(j);
								}
								else
								{
									if (((j + 1) < cString.length())
										&& (cString.charAt(j + 1) != ':'))
									{
										tempStr += "\\";
									}
								}
							}

							aPC.getMiscList().set(i - 1, tempStr.trim());

							break;

						default:
							Logging
								.errorPrint("In PCGVer0Parser.parseExperienceAndMiscLine the i value "
									+ i + " is not handled.");

							break;
					}

					if (i < 6)
					{
						line = line.substring(k + 1);
					}

					cString = "";
					nextLine = false;
					i++;
				}
			}
		}
		catch (NumberFormatException ex)
		{
			throw new PCGParseException("parseExperienceAndMiscLine",
				lines[current], ex.getMessage());
		}

		return current;
	}

	private void parseFeatsLine(String line) throws PCGParseException
	{
		try
		{
			String aName;
			String aString;
			final StringTokenizer aTok = new StringTokenizer(line, ":");

			while (aTok.hasMoreTokens())
			{
				aName = aTok.nextToken().trim();

				if (aName.length() == 0)
				{
					continue;
				}

				final int k = Integer.parseInt(aTok.nextToken());
				final StringTokenizer bTok = new StringTokenizer(aName, "[]");
				aName = bTok.nextToken();

				Ability anAbility = Globals.getAbilityKeyed("FEAT", aName);

				if ((anAbility != null) /*&& !aPC.hasFeatAutomatic(aName)*/)
				{
					anAbility = anAbility.clone();
					AbilityUtilities.modFeat(aPC, null, anAbility.getKeyName(),
						true, !anAbility.isMultiples());

					if (anAbility.isMultiples()
						&& (anAbility.getAssociatedCount() == 0)
						&& (aPC.getFeatKeyed(anAbility.getKeyName()) == null))
					{
						aPC.addFeat(anAbility, null);
					}

					anAbility = aPC.getFeatKeyed(anAbility.getKeyName());

					while (bTok.hasMoreTokens())
					{
						aString = bTok.nextToken();

						if (aString.startsWith("BONUS")
							&& (aString.length() > 6))
						{
							anAbility.addBonusList(aString.substring(6));
						}

						anAbility.addSave(aString);
					}
				}
				else
				{
					anAbility = new Ability();
				}

				for (int j = 0; j < k; j++)
				{
					aString = aTok.nextToken();

					if (aName.endsWith("Weapon Proficiency"))
					{
						aPC.addWeaponProf(aString);
					}
					else if ((anAbility.isMultiples() && anAbility.isStacks())
						|| !anAbility.containsAssociated(aString))
					{
						anAbility.addAssociated(aString);
					}
				}
			}
		}
		catch (NumberFormatException ex)
		{
			throw new PCGParseException("parseFeatsLine", line, ex.getMessage());
		}
	}

	/**
	 * Parses the FOLLOWER or MASTER lines of the .pcg file
	 * and either adds the follower to an array
	 * or set's the master
	 * @param lines
	 * @param i
	 * @return int
	 **/
	private int parseFollowerLine(String[] lines, int i)
	{
		if (i >= lines.length)
		{
			return i;
		}

		String aString = lines[i];

		while (aString.startsWith("FOLLOWER") || aString.startsWith("MASTER"))
		{
			StringTokenizer aTok = new StringTokenizer(aString, "|", false);
			String who = aTok.nextToken();
			String fName = aTok.nextToken();
			String aName = aTok.nextToken();
			String aType = aTok.nextToken();
			int usedHD = Integer.parseInt(aTok.nextToken());
			Follower aF = new Follower(fName, aName, aType);
			aF.setUsedHD(usedHD);

			if ("FOLLOWER".equals(who))
			{
				aPC.addFollower(aF);
			}
			else if ("MASTER".equals(who))
			{
				aPC.setMaster(aF);
			}

			i++;

			if (i >= lines.length)
			{
				return i;
			}

			aString = lines[i];
		}

		return i;
	}

	private int parseGoldBioDescriptionLine(String[] lines, int start)
		throws PCGParseException
	{
		int current = start;
		int i = 0;

		try
		{
			boolean nextLine = true;
			String line = "";
			String cString = "";

			while (i < 3)
			{
				if (nextLine)
				{
					line = lines[current++];
				}

				int k = line.indexOf(':');

				while ((k > 0) && (line.charAt(k - 1) == '\\'))
				{
					k = line.indexOf(':', k + 1);
				}

				if ((k < 0) || ((k > 0) && (line.charAt(k - 1) == '\\')))
				{
					k = -1;
				}

				if (k == -1)
				{
					cString = cString.concat(line);
					cString = cString.concat(Constants.s_LINE_SEP);
					nextLine = true;
				}
				else
				{
					k = line.indexOf(':');

					while ((k > 0) && (line.charAt(k - 1) == '\\'))
					{
						k = line.indexOf(':', k + 1);
					}

					cString = cString.concat(line.substring(0, k));

					String tempStr = "";

					for (int j = 0; j < cString.length(); j++)
					{
						if (cString.charAt(j) != '\\')
						{
							tempStr += cString.charAt(j);
						}
						else
						{
							if (((j + 1) < cString.length())
								&& (cString.charAt(j + 1) != ':'))
							{
								tempStr += "\\";
							}
						}
					}

					switch (i)
					{
						case 0:
							aPC.setGold(tempStr);

							break;

						case 1:
							aPC.setBio(tempStr);

							break;

						case 2:
							aPC.setDescription(tempStr);

							break;

						default:
							Logging
								.errorPrint("In PCGVer0Parser.parseGoldBioValue the i value "
									+ i + " is not handled.");

							break;
					}

					if (i < 3)
					{
						line = line.substring(k + 1);
					}

					cString = "";
					nextLine = false;
					i++;
				}
			}
		}
		catch (NumberFormatException ex)
		{
			throw new PCGParseException("parseGoldBioDescriptionLine", Integer
				.toString(i)
				+ ":" + lines[current], ex.getMessage());
		}

		return current;
	}

	private void parseLanguagesLine(String line)
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");

		while (aTok.hasMoreTokens())
		{
			aPC.addLanguageKeyed(aTok.nextToken());
		}
	}

	private void parseMiscLine(String line)
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");
		String token;

		for (int i = 0; aTok.hasMoreTokens(); i++)
		{
			token = CoreUtility.unEscapeColons2(aTok.nextToken().trim());

			switch (i)
			{
				case 0:
					aPC.setEyeColor(token);

					break;

				case 1:
					aPC.setSkinColor(token);

					break;

				case 2:
					aPC.setHairColor(token);

					break;

				case 3:
					aPC.setHairStyle(token);

					break;

				case 4:
					aPC.setSpeechTendency(token);

					break;

				case 5:
					aPC.setPhobias(token);

					break;

				case 6:
					aPC.setInterests(token);

					break;

				case 7:
					aPC.setTrait1(token);

					break;

				case 8:
					aPC.setTrait2(token);

					break;

				case 9:
					aPC.setCatchPhrase(token);

					break;

				case 10:
					aPC.setLocation(token);

					break;

				case 11:
					aPC.setResidence(token);

					break;

				default:
					Logging
						.errorPrint("In PCGVer0Parser.parseMiscLine the i value "
							+ i + " is not handled.");

					break;
			}
		}
	}

	private void parseNameLine(String line) throws PCGParseException
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");

		if (aTok.hasMoreTokens())
		{
			aPC.setName(CoreUtility.unEscapeColons2(aTok.nextToken()));
		}
		else
		{
			throw new PCGParseException("parseNameLine", line,
				"No character name found.");
		}

		if (aTok.hasMoreTokens() && (aTok.countTokens() > 1))
		{
			aPC.setTabName(CoreUtility.unEscapeColons2(aTok.nextToken()));
		}

		if (aTok.hasMoreTokens())
		{
			aPC.setPlayersName(CoreUtility.unEscapeColons2(aTok.nextToken()));
		}
	}

	private int parseNoteLine(String[] lines, int i)
	{
		if (i >= lines.length)
		{
			return i;
		}

		String lastLineParsed = lines[i];
		boolean flag = lastLineParsed.startsWith("NOTES:");
		NoteItem anItem = null;

		while (flag)
		{
			if (lastLineParsed.startsWith("NOTES:"))
			{
				final StringTokenizer aTok =
						new StringTokenizer(lastLineParsed.substring(6), ":",
							false);
				final int id_value = Integer.parseInt(aTok.nextToken());
				final int id_parent = Integer.parseInt(aTok.nextToken());
				final String id_name = aTok.nextToken();
				String id_text = "";

				if (aTok.hasMoreTokens())
				{
					id_text = aTok.nextToken();
				}

				anItem = new NoteItem(id_value, id_parent, id_name, id_text);
				aPC.addNotesItem(anItem);
			}
			else
			{
				if (anItem != null)
				{
					anItem.setValue(anItem.getValue() + Constants.s_LINE_SEP
						+ lastLineParsed);
				}
			}

			i++;

			if (i >= lines.length)
			{
				return i;
			}

			lastLineParsed = lines[i];
			flag =
					((lastLineParsed != null) && !":ENDNOTES:"
						.equals(lastLineParsed));
		}

		return i;
	}

	/**
	 * This parses the new style spell line
	 * each spell is on it's own line
	 * line could look like this:
	 * SPELL:SpellName:3:Fire:Wizard:Known Spells:One Feat:Two Feat
	 * @param lines
	 * @param i
	 * @return int
	 **/
	private int parseOldSpellLine(String[] lines, int i)
	{
		// line could look like this:
		// SPELL:SpellName:3:Fire:Wizard:Known Spells:One Feat:Two Feat
		if (i >= lines.length)
		{
			return i;
		}

		String aString = lines[i];

		while (aString.startsWith("SPELL:"))
		{
			aString = aString.substring(6);

			StringTokenizer aTok = new StringTokenizer(aString, ":", false);
			i++;
			aString = lines[i];

			String spellKey = aTok.nextToken();
			Spell aSpell = Globals.getSpellKeyed(spellKey);

			if (aSpell == null)
			{
				final String message =
						"Unable to find spell keyed: " + spellKey;
				warnings.add(message);

				continue;
			}

			final int times = Integer.parseInt(aTok.nextToken());
			final String domainKey = aTok.nextToken();
			final String classKey = aTok.nextToken();
			final String book = aTok.nextToken();
			final PCClass aClass = aPC.getClassKeyed(classKey);
			PObject aObject;

			if (aClass == null)
			{
				final String message =
						"Bad spell info - no class keyed " + classKey;
				warnings.add(message);

				continue;
			}

			// first, let's see if the spell is a domain spell
			aObject = aPC.getCharacterDomainKeyed(domainKey);

			// if it's not a domain spell, check to see if
			// it's a class spell,  ie: (bard == bard)
			if (aObject == null)
			{
				aObject = aClass;
			}

			if (aObject == null)
			{
				final String message =
						"Bad spell info - no class or domain named "
							+ domainKey;
				warnings.add(message);

				continue;
			}

			int sLevel = aSpell.getFirstLevelForKey(aObject.getSpellKey(), aPC);

			if (sLevel == -1)
			{
				final String message =
						"Bad spell info -" + aSpell.getKeyName()
							+ " doesn't have valid level info for " + domainKey;
				warnings.add(message);

				continue;
			}

			// do not load auto knownspells into default spellbook
			if (book.equals(Globals.getDefaultSpellBook())
				&& aClass.isAutoKnownSpell(aSpell.getKeyName(), sLevel, aPC))
			{
				continue;
			}

			CharacterSpell cs =
					aClass.getSpellSupport().getCharacterSpellForSpell(aSpell,
						aClass);

			if (cs == null)
			{
				cs = new CharacterSpell(aClass, aSpell);
				cs.addInfo(sLevel, 1, Globals.getDefaultSpellBook());
				aClass.getSpellSupport().addCharacterSpell(cs);
			}

			SpellInfo si = null;

			if (!book.equals(Globals.getDefaultSpellBook()))
			{
				si = cs.addInfo(sLevel, times, book);
			}

			List<Ability> featList = new ArrayList<Ability>();

			while (aTok.hasMoreTokens())
			{
				final String key = aTok.nextToken();
				final Ability anAbility = Globals.getAbilityKeyed("FEAT", key);

				if (anAbility != null)
				{
					featList.add(anAbility);
				}
			}

			if (si != null)
			{
				si.addFeatsToList(featList);
			}

			// just to make sure the spellbook is present
			aPC.addSpellBook(book);
		}

		return i;
	}

	private void parsePortraitLine(String line) throws PCGParseException
	{
		if ((line != null) && line.startsWith("PORTRAIT:"))
		{
			aPC.setPortraitPath(line.substring(9));
		}
		else
		{
			throw new PCGParseException("parsePortraitLine", line,
				"Invalid portrait line ignored.");
		}
	}

	private void parseRaceLine(String line) throws PCGParseException
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");
		int x = 0;
		HashMap<String, Integer> hitPointMap = new HashMap<String, Integer>();
		Race aRace = null;
		String token;

		for (int i = 0; aTok.hasMoreElements(); i++)
		{
			token = aTok.nextToken();

			switch (i)
			{
				case 0:
					aRace = Globals.getRaceKeyed(token);

					if (aRace != null)
					{
						aPC.setRace(aRace);
					}
					else
					{
						final String msg =
								PropertyFactory.getFormattedString(
									"Exceptions.PCGenParser.RaceNotFound", //$NON-NLS-1$
									token);
						throw new PCGParseException("parseRaceLine", line, msg); //$NON-NLS-1$
					}

					break;

				case 1:

					try
					{
						aPC.setAlignment(Integer.parseInt(token), true);
					}
					catch (NumberFormatException ex)
					{
						throw new PCGParseException(
							"parseRaceLine", line, ex.getMessage()); //$NON-NLS-1$
					}

					break;

				case 2:

					try
					{
						aPC.setHeight(Integer.parseInt(token));
					}
					catch (NumberFormatException ex)
					{
						throw new PCGParseException("parseRaceLine", line, ex
							.getMessage());
					}

					break;

				case 3:

					try
					{
						aPC.setWeight(Integer.parseInt(token));
					}
					catch (NumberFormatException ex)
					{
						throw new PCGParseException("parseRaceLine", line, ex
							.getMessage());
					}

					break;

				case 4:

					try
					{
						aPC.setAge(Integer.parseInt(token));
					}
					catch (NumberFormatException ex)
					{
						throw new PCGParseException("parseRaceLine", line, ex
							.getMessage());
					}

					break;

				case 5:
					aPC.setGender(token);

					break;

				case 6:
					aPC.setHanded(token);

					break;

				default:

					try
					{
						hitPointMap.put(Integer.toString(x++), Integer
							.valueOf(token));
					}
					catch (NumberFormatException ex)
					{
						throw new PCGParseException("parseRaceLine", line, ex
							.getMessage());
					}

					if (aRace != null)
					{
						if (x == aRace.hitDice(aPC))
						{
							aPC.getRace().setHitPointMap(hitPointMap);

							return;
						}
					}
			}
		}
	}

	private void parseSkillsLine(String line) throws PCGParseException
	{
		final StringTokenizer skillTokenizer = new StringTokenizer(line, ":");
		String skillName;
		List<String> aRankList;
		Integer outputIndex;

		try
		{
			while (skillTokenizer.hasMoreElements())
			{
				skillName = skillTokenizer.nextToken();

				if (!skillTokenizer.hasMoreTokens())
				{
					return;
				}

				final Float aFloat = new Float(skillTokenizer.nextToken());

				// If newer version, we can load in the order in which the skills should be displayed
				if (pcgVersion >= 268)
				{
					outputIndex = Integer.valueOf(skillTokenizer.nextToken());
				}
				else
				{
					outputIndex = Integer.valueOf(0);
				}

				//
				// If newer version, then we can determine which skill belongs to which class as it
				// is saved in the PCG file
				//
				aRankList = new ArrayList<String>();

				if (pcgVersion >= 2)
				{
					final int iCount =
							Integer.parseInt(skillTokenizer.nextToken());

					for (int i = 0; i < iCount; ++i)
					{
						aRankList.add(skillTokenizer.nextToken() + ":"
							+ skillTokenizer.nextToken());
					}
				}

				// Locate the skill in question, add to list if not already there
				Skill aSkill = aPC.getSkillKeyed(skillName);

				if (aSkill == null)
				{
					for (int i = 0; i < Globals.getSkillList().size(); i++)
					{
						if (skillName.equals(Globals.getSkillList().get(i)
							.toString()))
						{
							aSkill = Globals.getSkillList().get(i);
							aSkill = (Skill) aSkill.clone();
							aPC.getSkillList().add(aSkill);

							break;
						}
					}
				}

				if (aSkill != null)
				{
					for (int i = 0; i < aRankList.size(); i++)
					{
						String bRank = aRankList.get(i);
						int iOffs = bRank.indexOf(':');
						Float fRank = new Float(bRank.substring(iOffs + 1));
						PCClass aClass =
								aPC.getClassKeyed(bRank.substring(0, iOffs));

						if ((aClass != null)
							|| bRank.substring(0, iOffs).equals(
								Constants.s_NONE))
						{
							bRank =
									aSkill.modRanks(fRank.doubleValue(),
										aClass, true, aPC);

							if (bRank.length() != 0)
							{
								Logging.errorPrint("loadSkillsLine: " + bRank);
							}
						}
						else
						{
							Logging.errorPrint("Class not found: "
								+ bRank.substring(0, iOffs));
						}
					}

					if (pcgVersion < 2)
					{
						final String bRank =
								aSkill.modRanks(aFloat.doubleValue(), null,
									true, aPC);

						if (bRank.length() != 0)
						{
							Logging.errorPrint("loadSkillsLine: " + bRank);
						}
					}

					aSkill.setOutputIndex(outputIndex.intValue());
				}
				else
				{
					Logging.errorPrint("Skill not found: " + skillName);

					if (!CoreUtility.doublesEqual(aFloat.doubleValue(), 0.0))
					{
						final String msg =
								PropertyFactory.getFormattedString(
									"Warnings.PCGenParser.SkillNotFound", //$NON-NLS-1$
									skillName, aFloat);
						warnings.add(msg);
					}
				}
			}
		}
		catch (NumberFormatException ex)
		{
			throw new PCGParseException("parseSkillsLine", line, ex
				.getMessage());
		}
	}

	/**
	 * format should be
	 * SPELL:name:times:type:objectname:classname:book:level:feat:feat:feat
	 * @param lines
	 * @param i
	 * @return int
	 */
	private int parseSpellLine(String[] lines, int i)
	{
		if (i >= lines.length)
		{
			return i;
		}

		String aString = lines[i];

		while (aString.startsWith("SPELL:"))
		{
			aString = aString.substring(6);

			StringTokenizer aTok = new StringTokenizer(aString, ":", false);
			i++;
			aString = lines[i];

			String spellKey = aTok.nextToken();
			Spell aSpell = Globals.getSpellKeyed(spellKey);

			if (aSpell == null)
			{
				final String message =
						"Unable to find spell named: " + spellKey;
				warnings.add(message);

				continue;
			}

			int times = Integer.parseInt(aTok.nextToken());
			String typeName = aTok.nextToken(); // e.g. DOMAIN or CLASS
			String objKey = aTok.nextToken(); // e.g. Animal or Commoner
			String classKey = aTok.nextToken(); // could be same as objName... class to which list this spell is added
			String book = aTok.nextToken();
			int sLevel = Integer.parseInt(aTok.nextToken()); // e.g. level of spell (user may select higher than minimum)
			PCClass aClass = aPC.getClassKeyed(classKey);
			PObject aObject;

			if (aClass == null)
			{
				final String message =
						"Bad spell info - no class named " + classKey;
				warnings.add(message);

				continue;
			}

			if ("DOMAIN".equals(typeName))
			{
				aObject = aPC.getCharacterDomainKeyed(objKey);

				if (aObject == null)
				{
					final String message =
							"No Domain named " + objKey + " (" + aString + ")";
					warnings.add(message);

					continue;
				}
			}
			else
			{
				// it's either the class, sub-class or a cast-as class
				// first see if it's the class
				aObject = aPC.getClassKeyed(objKey);

				if (aObject == null)
				{
					aObject = aClass;
				}
			}

			final int level =
					aSpell.getFirstLevelForKey(aObject.getSpellKey(), aPC);

			if (level == -1)
			{
				final String message =
						"Bad spell info - no spell for " + aSpell.getKeyName()
							+ " in " + typeName + " " + objKey;
				warnings.add(message);

				continue;
			}

			// do not load auto knownspells into default spellbook
			if (book.equals(Globals.getDefaultSpellBook())
				&& aClass.isAutoKnownSpell(aSpell.getKeyName(), level, aPC)
				&& aPC.getAutoSpells())
			{
				continue;
			}

			List<Ability> featList = new ArrayList<Ability>();

			while (aTok.hasMoreTokens())
			{
				final String fKey = aTok.nextToken();
				final Ability anAbility = Globals.getAbilityKeyed("FEAT", fKey);

				if (anAbility != null)
				{
					featList.add(anAbility);
				}
			}

			CharacterSpell cs =
					aClass.getSpellSupport().getCharacterSpellForSpell(aSpell,
						aClass);

			if (cs == null)
			{
				cs = new CharacterSpell(aClass, aSpell);

				if (!"DOMAIN".equals(typeName))
				{
					cs.addInfo(level, 1, Globals.getDefaultSpellBook());
				}

				aClass.getSpellSupport().addCharacterSpell(cs);
			}

			SpellInfo si = null;

			if (objKey.equals(classKey)
				|| !book.equals(Globals.getDefaultSpellBook()))
			{
				si = cs.getSpellInfoFor(book, sLevel, -1);

				if ((si == null) || !featList.isEmpty())
				{
					si = cs.addInfo(sLevel, times, book);
				}
			}

			if ((si != null) && !featList.isEmpty())
			{
				si.addFeatsToList(featList);
			}

			// just to make sure the spellbook is present
			aPC.addSpellBook(book);

			if (i >= lines.length)
			{
				return i;
			}
		}

		// now sort each classes spell list
		for (Iterator sp = aPC.getClassList().iterator(); sp.hasNext();)
		{
			final PCClass aClass = (PCClass) sp.next();
			aClass.getSpellSupport().sortCharacterSpellList();
		}

		return i;
	}

	private void parseStatsLine(String line) throws PCGParseException
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");

		// Check for different STAT counts
		int statCount = 6;

		if (line.startsWith("STATS:"))
		{
			aTok.nextToken(); // ignore "STATS:"
			statCount = Integer.parseInt(aTok.nextToken());
		}

		if (statCount != SettingsHandler.getGame().s_ATTRIBLONG.length)
		{
			final String message =
					"Number of Stats for character is " + statCount + ". "
						+ "PCGen is currently using "
						+ SettingsHandler.getGame().s_ATTRIBLONG.length + ". "
						+ "Cannot load character.";
			throw new PCGParseException("parseStatsLine", line, message);
		}

		try
		{
			for (int i = 0; aTok.hasMoreTokens()
				&& (i < SettingsHandler.getGame().s_ATTRIBLONG.length); i++)
			{
				aPC.getStatList().getStatAt(i).setBaseScore(
					Integer.parseInt(aTok.nextToken()));
			}

			if (aTok.hasMoreTokens())
			{
				aPC.setPoolAmount(Integer.parseInt(aTok.nextToken()));
			}

			if (aTok.hasMoreTokens())
			{
				aPC.setCostPool(Integer.parseInt(aTok.nextToken()));
			}
		}
		catch (NumberFormatException ex)
		{
			throw new PCGParseException("parseStatsLine", line, ex.getMessage());
		}
	}

	private void parseTemplateLine(String line)
	{
		if (line == null)
		{
			return;
		}

		String work = line;

		if (work.startsWith("TEMPLATE:"))
		{
			work = work.substring(9);
		}

		final StringTokenizer tokens = new StringTokenizer(work, ":");
		PCTemplate aTemplate;

		while (tokens.hasMoreTokens())
		{
			aTemplate = Globals.getTemplateKeyed(tokens.nextToken());

			/**
			 * bug fix:
			 * do not add (additional) gold on character load
			 *
			 * this is just a quick fix;
			 * actually I do not know how to do this properly
			 * oh well, seems to work --- for now
			 *
			 * author: Thomas Behr 06-01-02
			 */
			if (aTemplate != null)
			{
				aPC.addTemplate(aTemplate);
			}
		}
	}

	private void parseUnusedPointsLine(String line) throws PCGParseException
	{
		final StringTokenizer aTok = new StringTokenizer(line, ":");

		try
		{
			final int remainingSkillPoints = Integer.parseInt(aTok.nextToken());
			aPC.setSkillPoints(remainingSkillPoints);

			//
			// Check to see if unused skill points matches what the classes think
			// they have left. If they don't warn the user, but let him/her fix the
			// problem.
			//
			int classSkillPoints = 0;

			for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
			{
				final PCClass aClass = (PCClass) e.next();
				classSkillPoints += aClass.getSkillPool(aPC);
			}

			if (classSkillPoints != remainingSkillPoints)
			{
				final String message =
						"Remaining class skill points incorrect (i.e. "
							+ classSkillPoints + " instead of "
							+ remainingSkillPoints + ")."
							+ Constants.s_LINE_SEP
							+ "Please correct manually on the Skills tab";
				warnings.add(message);
			}

			aPC.setFeats(Double.parseDouble(aTok.nextToken()));
		}
		catch (NumberFormatException ex)
		{
			throw new PCGParseException("parseUnusedPointsLine", line, ex
				.getMessage());
		}
	}

	private void parseWeaponProfLine(String line)
	{
		//		final StringTokenizer aTok = new StringTokenizer(line, ":");
		//		while (aTok.hasMoreTokens())
		//		{
		//			aPC.addWeaponProf(aTok.nextToken());
		//		}
		int iState = 0;
		final StringTokenizer aTok = new StringTokenizer(line, ":", false);
		Race aRace = null;
		PCClass aClass = null;
		Domain aDomain = null;
		Ability aFeat = null;
		final List<String> myProfs = new ArrayList<String>();

		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();

			if (aString.startsWith("RACE="))
			{
				iState = 1;
				aRace = aPC.getRace();

				continue;
			}
			else if (aString.startsWith("CLASS="))
			{
				iState = 2;
				aString = aString.substring(6);
				aClass = aPC.getClassKeyed(aString);

				continue;
			}
			else if (aString.startsWith("DOMAIN="))
			{
				iState = 3;
				aString = aString.substring(7);
				aDomain = aPC.getCharacterDomainKeyed(aString);

				continue;
			}
			else if (aString.startsWith("FEAT="))
			{
				iState = 4;
				aString = aString.substring(5);
				aFeat = aPC.getFeatNamed(aString);

				continue;
			}

			switch (iState)
			{
				case 1:

					if (aRace != null)
					{
						aRace.addSelectedWeaponProfBonus(aString);
					}

					break;

				case 2:

					if (aClass != null)
					{
						aClass.addSelectedWeaponProfBonus(aString);
					}

					break;

				case 3:

					if (aDomain != null)
					{
						aDomain.addSelectedWeaponProfBonus(aString);
					}

					break;

				case 4:

					if (aFeat != null)
					{
						aFeat.addSelectedWeaponProfBonus(aString);
					}

					break;

				default:
					myProfs.add(aString);

					break;
			}
		}

		//		aPC.setAutomaticFeatsStable(false);
		aPC.setAutomaticAbilitiesStable(null, false);
		//		AbilityUtilities.rebuildAutoAbilityList(aPC); // populate profs array with automatic profs

		final List<String> nonproficient = new ArrayList<String>();

		for (Iterator<String> e = myProfs.iterator(); e.hasNext();)
		{
			final String aString = e.next();

			if (!aPC.hasWeaponProfKeyed(aString))
			{
				nonproficient.add(aString);
			}
		}

		//
		// For some reason, character had a proficiency that they should not have. Inform
		// the user that they no longer have the proficiency.
		//
		if (nonproficient.size() != 0)
		{
			String s = nonproficient.toString();
			s = s.substring(1, s.length() - 1);

			final String message =
					"No longer proficient with following weapon(s):"
						+ Constants.s_LINE_SEP + s;
			warnings.add(message);
		}
	}
}
