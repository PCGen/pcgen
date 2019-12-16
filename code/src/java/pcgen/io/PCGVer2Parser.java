/*
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
 *
 *
 */
package pcgen.io;

import java.awt.Rectangle;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.SelectableSet;
import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.NumericPCAttribute;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.SkillFilter;
import pcgen.cdom.enumeration.SkillsOutputOrder;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.input.DomainInputFacet;
import pcgen.cdom.facet.input.RaceInputFacet;
import pcgen.cdom.facet.input.TemplateInputFacet;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.CompanionList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.BonusManager;
import pcgen.core.BonusManager.TempBonusInfo;
import pcgen.core.Campaign;
import pcgen.core.ChronicleEntry;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.NoteItem;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.SpellProhibitor;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.core.SystemCollections;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.BonusAddition;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.analysis.DomainApplication;
import pcgen.core.analysis.RaceAlignment;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.analysis.SpellLevel;
import pcgen.core.analysis.SubClassApplication;
import pcgen.core.analysis.SubstitutionLevelSupport;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.display.BonusDisplay;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.io.migration.AbilityMigration;
import pcgen.io.migration.AbilityMigration.CategorisedKey;
import pcgen.io.migration.EquipSetMigration;
import pcgen.io.migration.EquipmentMigration;
import pcgen.io.migration.RaceMigration;
import pcgen.io.migration.SourceMigration;
import pcgen.io.migration.SpellMigration;
import pcgen.output.channel.compat.AlignmentCompat;
import pcgen.output.channel.compat.HandedCompat;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;
import pcgen.util.enumeration.ProhibitedSpellType;

import org.apache.commons.lang3.StringUtils;

/**
 * {@code PCGVer2Parser}
 * Parses a line oriented format.
 * Each line should adhere to the following grammar:<br>
 *
 * <i>line</i> := EMPTY | <i>comment</i> | <i>taglist</i>
 * <i>comment</i> := '#' STRING
 * <i>taglist</i> := tag ('|' tag)*
 * <i>tag</i> := simpletag | nestedtag
 * <i>nestedtag</i> := TAGNAME ':' '[' taglist ']'
 * <i>simpletag</i> := TAGNAME ':' TAGVALUE
 *
 *
 */
final class PCGVer2Parser implements PCGParser
{
	private static final Class<Domain> DOMAIN_CLASS = Domain.class;

	private static final String TAG_PCTEMPLATE = "PCTEMPLATE";

	private RaceInputFacet raceInputFacet = FacetLibrary.getFacet(RaceInputFacet.class);
	private DomainInputFacet domainInputFacet = FacetLibrary.getFacet(DomainInputFacet.class);
	private TemplateInputFacet templateInputFacet = FacetLibrary.getFacet(TemplateInputFacet.class);

	/**
	 * DO NOT CHANGE line separator.
	 * Need to keep the Unix line separator to ensure cross-platform portability.
	 *
	 * author: Thomas Behr 2002-11-13
	 */
	private final List<String> warnings = new ArrayList<>();
	private Cache cache;
	private PlayerCharacter thePC;
	private final Set<String> seenStats = new HashSet<>();
	private final Set<Language> cachedLanguages = new HashSet<>();

	//
	// MAJOR.MINOR.REVISION
	//
	private int[] pcgenVersion = {0, 0, 0};
	private String pcgenVersionSuffix;
	private boolean calcFeatPoolAfterLoad = false;
	private double baseFeatPool = 0.0;
	private boolean featsPresent = false;

	/**
	 * Constructor
	 * @param aPC
	 */
	PCGVer2Parser(PlayerCharacter aPC)
	{
		thePC = aPC;
	}

	/**
	 * Selector
	 *
	 * <br>author: Thomas Behr 22-03-02
	 *
	 * @return a list of warning messages
	 */
	@Override
	public List<String> getWarnings()
	{
		return warnings;
	}

	/**
	 * parse a String in PCG format
	 *
	 * <br>author: Thomas Behr 20-07-02
	 *
	 * @param lines   the String to parse
	 * @throws PCGParseException
	 */
	@Override
	public void parsePCG(String[] lines) throws PCGParseException
	{
		buildPcgLineCache(lines);

		parseCachedLines();
		resolveLanguages();
	}

	/**
	 * Check the game mode and then build a list of campaigns the character 
	 * requires to be loaded.
	 *   
	 * @param lines The PCG lines to be parsed.
	 * @return The list of campaigns.
	 * @throws PCGParseException If the lines are invalid 
	 */
	@Override
	public SourceSelectionFacade parcePCGSourceOnly(String[] lines) throws PCGParseException
	{
		buildPcgLineCache(lines);

		/*
		 * VERSION:x.x.x
		 */
		if (cache.containsKey(IOConstants.TAG_VERSION))
		{
			parseVersionLine(cache.get(IOConstants.TAG_VERSION).get(0));
		}

		if (!cache.containsKey(IOConstants.TAG_GAMEMODE))
		{
			Logging.errorPrint("Character does not have game mode information.");
			return null;
		}
		String line = cache.get(IOConstants.TAG_GAMEMODE).get(0);
		String requestedMode = line.substring(IOConstants.TAG_GAMEMODE.length() + 1);
		GameMode mode = SystemCollections.getGameModeNamed(requestedMode);
		if (mode == null)
		{
			for (GameMode gameMode : SystemCollections.getUnmodifiableGameModeList())
			{
				if (gameMode.getAllowedModes().contains(requestedMode))
				{
					mode = gameMode;
					break;
				}
			}
		}
		//if mode == null still then a game mode was not found
		if (mode == null)
		{
			Logging.errorPrint("Character's game mode entry was not valid: " + line);
			return null;
		}
		if (!cache.containsKey(IOConstants.TAG_CAMPAIGN))
		{
			Logging.errorPrint("Character does not have campaign information.");
			return FacadeFactory.createSourceSelection(mode, new ArrayList<>());
		}
		/*
		 * #System Information
		 * CAMPAIGN:CMP - Monkey Book I - Book For Monkeys
		 * CAMPAIGN:CMP - Monkey Book II - Book By Monkeys
		 * ...
		 *
		 * first thing to do is checking campaigns - no matter what!
		 */
		List<Campaign> campaigns = getCampaignList(cache.get(IOConstants.TAG_CAMPAIGN), mode.getName());
		if (campaigns.isEmpty())
		{
			Logging.errorPrint("Character's campaign entry was empty.");
		}
		return FacadeFactory.createSourceSelection(mode, campaigns);
	}

	/**
	 * @param lines
	 */
	private void buildPcgLineCache(String[] lines)
	{
		initCache(lines.length);

		for (String line : lines)
		{
			if ((!line.trim().isEmpty()) && !isComment(line))
			{
				cacheLine(line.trim());
			}
		}
	}

	/*
	 * ###############################################################
	 * Miscellaneous methods
	 * ###############################################################
	 */

	/**
	 * Convenience Method
	 *
	 * <br>author: Thomas Behr 28-04-02
	 *
	 * @param line
	 * @return true if it is a comment
	 */
	private static boolean isComment(String line)
	{
		return line.trim().startsWith(IOConstants.TAG_COMMENT);
	}

	/*
	 * Given a Source string and Target string,
	 * return a List of BonusObj's
	 */
	private Map<BonusObj, TempBonusInfo> getBonusFromName(String sName, String tName)
	{
		//sName = NAME=Haste
		//tName = PC
		String sourceStr = sName.substring(IOConstants.TAG_TEMPBONUS.length() + 1);
		String targetStr = tName.substring(IOConstants.TAG_TEMPBONUSTARGET.length() + 1);
		CDOMObject oSource = null;

		if (sourceStr.startsWith(IOConstants.TAG_FEAT + '='))
		{
			sourceStr = sourceStr.substring(5);
			oSource = Globals.getContext().getReferenceContext().getManufacturerId(AbilityCategory.FEAT)
				.getActiveObject(sourceStr);
			oSource = thePC.getAbilityKeyed(AbilityCategory.FEAT, sourceStr);
			if (oSource == null)
			{
				for (final AbilityCategory cat : SettingsHandler.getGame().getAllAbilityCategories())
				{
					Ability abilSourceObj = Globals.getContext().getReferenceContext().getManufacturerId(cat)
						.getActiveObject(sourceStr);
					if (abilSourceObj != null)
					{
						oSource = abilSourceObj;
					}
				}
			}
		}
		else if (sourceStr.startsWith(IOConstants.TAG_SPELL + '='))
		{
			sourceStr = sourceStr.substring(6);

			//oSource = aPC.getSpellNamed(sourceStr);
			oSource =
					Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Spell.class, sourceStr);
		}
		else if (sourceStr.startsWith(IOConstants.TAG_EQUIPMENT + '='))
		{
			sourceStr = sourceStr.substring(10);
			oSource = thePC.getEquipmentNamed(sourceStr);
		}
		else if (sourceStr.startsWith(IOConstants.TAG_CLASS + '='))
		{
			sourceStr = sourceStr.substring(6);
			oSource = thePC.getClassKeyed(sourceStr);
		}
		else if (sourceStr.startsWith(IOConstants.TAG_TEMPLATE + '='))
		{
			sourceStr = sourceStr.substring(9);
			oSource = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCTemplate.class,
				sourceStr);
		}
		else if (sourceStr.startsWith(IOConstants.TAG_SKILL + '='))
		{
			sourceStr = sourceStr.substring(6);
			Skill aSkill =
					Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Skill.class, sourceStr);
			if (thePC.hasSkill(aSkill))
			{
				oSource = aSkill;
			}
			else
			{
				// TODO Error message
			}
		}
		else
		{
			// TODO Error message
			// Hmm, not a supported type
		}

		if (oSource != null)
		{
			sourceStr = oSource.getKeyName();
		}

		if (targetStr.equals(IOConstants.TAG_PC))
		{
			targetStr = thePC.getName();
		}
		else
		{
			CDOMObject oTarget = thePC.getEquipmentNamed(targetStr);
			targetStr = oTarget.getDisplayName();
		}

		return thePC.getTempBonusMap(sourceStr, targetStr);
	}

	private void addKeyedTemplate(PCTemplate template, String choice)
	{
		if (ChooseActivation.hasNewChooseToken(template) && choice == null)
		{
			final String message =
					"Template ignored: " + template + " as a choice was expected but none was present in character.";
			warnings.add(message);
			return;
		}
		final int preXP = thePC.getXP();
		templateInputFacet.importSelection(thePC.getCharID(), template, choice);
		thePC.addTemplate(template);

		//
		// XP written to file contains leveladjustment XP. If template modifies
		// XP, then
		// it will have already been added into total. Need to make sure it is
		// not doubled.
		//
		if (thePC.getXP() != preXP)
		{
			thePC.setXP(preXP);
		}
	}

	private void cacheLine(String s)
	{
		cache.put(s.substring(0, s.indexOf(':')), s);
	}


	private void checkStats() throws PCGParseException
	{
		if (seenStats.size() != Globals.getContext().getReferenceContext().getConstructedObjectCount(PCStat.class))
		{
			final String message =
					LanguageBundle.getFormattedString("Exceptions.PCGenParser.WrongNumAttributes", //$NON-NLS-1$
				seenStats.size(), Globals.getContext().getReferenceContext().getConstructedObjectCount(PCStat.class));
			throw new PCGParseException("parseStatLines", "N/A", message, null); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	/*
	 * ###############################################################
	 * private helper methods
	 * ###############################################################
	 */
	private void initCache(int capacity)
	{
		cache = new Cache((capacity * 4) / 3);
	}

	private void parseAgeLine(String line)
	{
		try
		{
			thePC.setPCAttribute(NumericPCAttribute.AGE,
				Integer.parseInt(line.substring(IOConstants.TAG_AGE.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message =
					LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalAgeLine", //$NON-NLS-1$
						line);
			warnings.add(message);
		}
	}

	private void parseAgeSet(String line)
	{
		final StringTokenizer aTok = new StringTokenizer(line, IOConstants.TAG_END, false);
		int i = 0;
		aTok.nextToken(); // skip tag

		while (aTok.hasMoreTokens() && (i < 10))
		{
			thePC.setHasMadeKitSelectionForAgeSet(i++, aTok.nextToken().equals("1")); //$NON-NLS-1$
		}
	}

	private void parseAlignmentLine(String line)
	{
		final String alignment = line.substring(IOConstants.TAG_ALIGNMENT.length() + 1);
		PCAlignment align = Globals.getContext().getReferenceContext()
			.silentlyGetConstructedCDOMObject(PCAlignment.class, alignment);

		if (align != null)
		{
			if (!RaceAlignment.canBeAlignment(thePC.getRace(), align))
			{
				ShowMessageDelegate.showMessageDialog("Invalid alignment. Setting to <none selected>",
					Constants.APPLICATION_NAME, MessageType.INFORMATION);
				align = getNoAlignment();
			}
			AlignmentCompat.setCurrentAlignment(thePC.getCharID(), align);

			return;
		}

		final String message = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalAlignment", //$NON-NLS-1$
			line);
		warnings.add(message);
	}

	/**
	 * Ignore cost for gear (Y/N)
	 * @param line Line of saved data to be processed.
	 **/
	private void parseIgnoreCostLine(String line)
	{
		thePC.setIgnoreCost(line.endsWith(IOConstants.VALUE_Y));
	}

	/**
	 * Allow debt for gear (Y/N)
	 * @param line Line of saved data to be processed.
	 **/
	private void parseAllowDebtLine(String line)
	{
		thePC.setAllowDebt(line.endsWith(IOConstants.VALUE_Y));
	}

	/**
	 * Auto resize gear (Y/N)
	 * @param line Line of saved data to be processed.
	 **/
	private void parseAutoResizeGearLine(String line)
	{
		thePC.setAutoResize(line.endsWith(IOConstants.VALUE_Y));
	}

	/**
	 * # Auto sort skills - transition only, line is no longer saved
	 * @param line
	 **/
	private void parseAutoSortSkillsLine(String line)
	{
		if (line.endsWith(IOConstants.VALUE_Y))
		{
			thePC.setSkillsOutputOrder(SkillsOutputOrder.NAME_ASC);
		}
		else
		{
			thePC.setSkillsOutputOrder(SkillsOutputOrder.MANUAL);
		}
	}

	/**
	 * # Auto known spells
	 * @param line
	 **/
	private void parseAutoSpellsLine(String line)
	{
		thePC.setAutoSpells(line.endsWith(IOConstants.VALUE_Y));
	}

	/**
	 * Process the Use Higher Known Spell Slot line.
	 * @param line The buffer to append to.
	 */
	private void parseUseHigherKnownSpellSlotsLine(String line)
	{
		thePC.setUseHigherKnownSlots(line.endsWith(IOConstants.VALUE_Y));
	}

	/**
	 * Process the Use Higher Prepped Spell Slot line.
	 * @param line The buffer to append to.
	 */
	private void parseUseHigherPreppedSpellSlotsLine(String line)
	{
		thePC.setUseHigherPreppedSlots(line.endsWith(IOConstants.VALUE_Y));
	}

	private void parseBirthdayLine(String line)
	{
		thePC.setPCAttribute(PCStringKey.BIRTHDAY,
			EntityEncoder.decode(line.substring(IOConstants.TAG_BIRTHDAY.length() + 1)));
	}

	private void parseBirthplaceLine(String line)
	{
		thePC.setPCAttribute(PCStringKey.BIRTHPLACE,
			EntityEncoder.decode(line.substring(IOConstants.TAG_BIRTHPLACE.length() + 1)));
	}

	/**
	 * Does the actual work:<br>
	 * Retrieves cached lines and parses each line.
	 *
	 * Note: May have to change parse order!
	 *
	 * <br>author: Thomas Behr 31-07-02
	 *
	 * @throws PCGParseException
	 */
	private void parseCachedLines() throws PCGParseException
	{
		/*
		 * VERSION:x.x.x
		 */
		if (cache.containsKey(IOConstants.TAG_VERSION))
		{
			parseVersionLine(cache.get(IOConstants.TAG_VERSION).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_GAMEMODE))
		{
			parseGameMode(cache.get(IOConstants.TAG_GAMEMODE).get(0));
		}

		/*
		 * #System Information
		 * CAMPAIGN:CMP - Monkey Book I - Book For Monkeys
		 * CAMPAIGN:CMP - Monkey Book II - Book By Monkeys
		 * ...
		 *
		 * first thing to do is checking campaigns - no matter what!
		 */
		if (cache.containsKey(IOConstants.TAG_CAMPAIGN))
		{
			checkDisplayListsHappy();
		}

		/*
		 * #Character Attributes
		 * STAT:STR=18
		 * STAT:DEX=18
		 * STAT:CON=18
		 * STAT:INT=18
		 * STAT:WIS=18
		 * STAT:CHA=18
		 * ALIGN:LG
		 * RACE:Human
		 */
		if (cache.containsKey(IOConstants.TAG_STAT))
		{
			for (final String stat : cache.get(IOConstants.TAG_STAT))
			{
				parseStatLine(stat);
			}

			checkStats();
		}

		if (cache.containsKey(IOConstants.TAG_ALIGNMENT))
		{
			parseAlignmentLine(cache.get(IOConstants.TAG_ALIGNMENT).get(0));
		}

		/*
		 * # Kits - Just adds a reference to the character that the template
		 * was picked.
		 */
		if (cache.containsKey(IOConstants.TAG_KIT))
		{
			for (final String line : cache.get(IOConstants.TAG_KIT))
			{
				parseKitLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_RACE))
		{
			parseRaceLine(cache.get(IOConstants.TAG_RACE).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_FAVOREDCLASS))
		{
			parseFavoredClassLine(cache.get(IOConstants.TAG_FAVOREDCLASS).get(0));
		}

		/*
		 * #System Information
		 * CAMPAIGNS:>:-delimited list<
		 * VERSION:x.x.x
		 * ROLLMETHOD:xxx
		 * PURCHASEPOINTS:Y or N|TYPE:>living City, Living greyhawk, etc<
		 * UNLIMITEDPOOLCHECKED:Y or N
		 * POOLPOINTS:>numeric value 0-?<
		 * POOLPOINTSAVAIL:>numeric value 0-?<
		 * GAMEMODE:DnD
		 * TABLABEL:0
		 * AUTOSPELLS:Y or N
		 * LOADCOMPANIONS:Y or N
		 * USETEMPMODS:Y or N
		 * SKILLSOUTPUTORDER:0
		 */
		if (cache.containsKey(IOConstants.TAG_POOLPOINTS))
		{
			parsePoolPointsLine(cache.get(IOConstants.TAG_POOLPOINTS).get(0));
		}
		if (cache.containsKey(IOConstants.TAG_POOLPOINTSAVAIL))
		{
			parsePoolPointsLine2(cache.get(IOConstants.TAG_POOLPOINTSAVAIL).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_CHARACTERTYPE))
		{
			parseCharacterTypeLine(cache.get(IOConstants.TAG_CHARACTERTYPE).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_PREVIEWSHEET))
		{
			parsePreviewSheetLine(cache.get(IOConstants.TAG_PREVIEWSHEET).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_AUTOSPELLS))
		{
			parseAutoSpellsLine(cache.get(IOConstants.TAG_AUTOSPELLS).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_USEHIGHERKNOWN))
		{
			parseUseHigherKnownSpellSlotsLine(cache.get(IOConstants.TAG_USEHIGHERKNOWN).get(0));
		}
		if (cache.containsKey(IOConstants.TAG_USEHIGHERPREPPED))
		{
			parseUseHigherPreppedSpellSlotsLine(cache.get(IOConstants.TAG_USEHIGHERPREPPED).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_LOADCOMPANIONS))
		{
			parseLoadCompanionLine(cache.get(IOConstants.TAG_LOADCOMPANIONS).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_USETEMPMODS))
		{
			parseUseTempModsLine(cache.get(IOConstants.TAG_USETEMPMODS).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_HTMLOUTPUTSHEET))
		{
			parseHTMLOutputSheetLine(cache.get(IOConstants.TAG_HTMLOUTPUTSHEET).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_PDFOUTPUTSHEET))
		{
			parsePDFOutputSheetLine(cache.get(IOConstants.TAG_PDFOUTPUTSHEET).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_IGNORECOST))
		{
			parseIgnoreCostLine(cache.get(IOConstants.TAG_IGNORECOST).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_ALLOWDEBT))
		{
			parseAllowDebtLine(cache.get(IOConstants.TAG_ALLOWDEBT).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_AUTORESIZEGEAR))
		{
			parseAutoResizeGearLine(cache.get(IOConstants.TAG_AUTORESIZEGEAR).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_AUTOSORTSKILLS))
		{
			parseAutoSortSkillsLine(cache.get(IOConstants.TAG_AUTOSORTSKILLS).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_SKILLSOUTPUTORDER))
		{
			parseSkillsOutputOrderLine(cache.get(IOConstants.TAG_SKILLSOUTPUTORDER).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_SKILLFILTER))
		{
			parseSkillFilterLine(cache.get(IOConstants.TAG_SKILLFILTER).get(0));
		}

		/*
		 * #Character Class(es)
		 * CLASS:Fighter|LEVEL=3
		 * CLASSABILITIESLEVEL:Fighter=1(>This would only display up to the level the character has already,)
		 * CLASSABILITIESLEVEL:Fighter=2(>with any special abilities not covered by other areas,)
		 * CLASSABILITIESLEVEL:Fighter=3(>such as skills, feats, etc., but would list SA's, and the like<)
		 * CLASS:Wizard|LEVEL=1
		 * CLASSABILITIESLEVEL:Wizard=1(SA's, MEMORIZE:Y, etc)
		 */
		if (cache.containsKey(IOConstants.TAG_CLASS))
		{
			for (String line : cache.get(IOConstants.TAG_CLASS))
			{
				parseClassLine(line);
			}
			thePC.setDirty(true);
		}

		final List<PCLevelInfo> pcLevelInfoList = new ArrayList<>(thePC.getLevelInfo());
		if (cache.containsKey(IOConstants.TAG_CLASSABILITIESLEVEL))
		{
			thePC.clearLevelInfo();
			for (String line : cache.get(IOConstants.TAG_CLASSABILITIESLEVEL))
			{
				parseClassAbilitiesLevelLine(line, pcLevelInfoList);
			}
		}

		/*
		 * #Character Experience
		 * EXPERIENCE:6000
		 * EXPERIENCETABLE:Medium
		 */
		if (cache.containsKey(IOConstants.TAG_EXPERIENCE))
		{
			parseExperienceLine(cache.get(IOConstants.TAG_EXPERIENCE).get(0));
		}
		if (cache.containsKey(IOConstants.TAG_EXPERIENCETABLE))
		{
			parseExperienceTableLine(cache.get(IOConstants.TAG_EXPERIENCETABLE).get(0));
		}

		/*
		 * #Character Templates
		 * TEMPLATESAPPLIED:If any, else this would just have the comment line, and skip to the next
		 */
		if (cache.containsKey(IOConstants.TAG_TEMPLATESAPPLIED))
		{
			for (String line : cache.get(IOConstants.TAG_TEMPLATESAPPLIED))
			{
				parseTemplateLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_REGION))
		{
			for (String line : cache.get(IOConstants.TAG_REGION))
			{
				parseRegionLine(line);
			}
		}

		/*
		 * ###############################################################
		 * Character Skills methods
		 * ###############################################################
		 */
		/*
		 * #Character Skills
		 * CLASSBOUGHT:Fighter
		 * SKILL:Alchemy|CLASS:N|COST:2|RANK:7  (Should be Obvious what each of these does, I hope ;p)
		 * SKILL:Survival|CLASS:Y|COST:1|SYNERGY:Wilderness Lore=5=2|RANK:10
		 * CLASSBOUGHT:Wizard
		 * SKILL:Spellcraft|CLASS:Y|COST:1|RANK:7
		 *
		 * CLASSBOUGHT not supported
		 */
		if (cache.containsKey(IOConstants.TAG_SKILL))
		{
			for (final String line : cache.get(IOConstants.TAG_SKILL))
			{
				parseSkillLine(line);
			}
		}

		/*
		 * #Character Languages
		 * LANGUAGE:Chondathan|LANGUAGE:Common|LANGUAGE:Literacy
		 */
		if (cache.containsKey(IOConstants.TAG_LANGUAGE))
		{
			for (final String line : cache.get(IOConstants.TAG_LANGUAGE))
			{
				parseLanguageLine(line);
			}
		}

		/*
		 * Anything that is already Pipe Delimited should be in
		 * parenthesis to avoid confusion on PCGen's part
		 *
		 * #Character Feats
		 * FEAT:Alertness|TYPE:General|(BONUS:SKILL|Listen,Spot|2)|DESC:+2 on Listen and Spot checks
		 * FEATPOOL:>number of remaining feats<
		 */
		if (cache.containsKey(IOConstants.TAG_FEAT))
		{
			for (final String line : cache.get(IOConstants.TAG_FEAT))
			{
				parseFeatLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_VFEAT))
		{
			for (final String line : cache.get(IOConstants.TAG_VFEAT))
			{
				parseVFeatLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_FEATPOOL))
		{
			for (final String line : cache.get(IOConstants.TAG_FEATPOOL))
			{
				parseFeatPoolLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_ABILITY))
		{
			for (final String line : cache.get(IOConstants.TAG_ABILITY))
			{
				parseAbilityLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_USERPOOL))
		{
			for (final String line : cache.get(IOConstants.TAG_USERPOOL))
			{
				parseUserPoolLine(line);
			}
		}

		/*
		 * #Character Deity/Domain
		 * DEITY:Yondalla|DEITYDOMAINS:[DOMAIN:Good|DOMAIN:Law|DOMAIN:Protection]|ALIGNALLOW:013|DESC:Halflings, 
		 *             Protection, Fertility|SYMBOL:None|DEITYFAVWEAP:Sword (Short)|DEITYALIGN:ALIGN:LG
		 * DOMAIN:GOOD|DOMAINGRANTS:>list of abilities<
		 * DOMAINSPELLS:GOOD|SPELLLIST:[SPELL:bla|SPELL:blubber|...]
		 */
		if (cache.containsKey(IOConstants.TAG_DEITY))
		{
			for (final String line : cache.get(IOConstants.TAG_DEITY))
			{
				parseDeityLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_DOMAIN))
		{
			for (final String line : cache.get(IOConstants.TAG_DOMAIN))
			{
				parseDomainLine(line);
			}
		}

		//We ignore domain spells now

		if (cache.containsKey(IOConstants.TAG_SPELLBOOK))
		{
			for (final String line : cache.get(IOConstants.TAG_SPELLBOOK))
			{
				parseSpellBookLines(line);
			}
		}
		/*
		 * This one is what will make spellcasters U G L Y!!!
		 *
		 * #Character Spells Information
		 * CLASS:Wizard|CANCASTPERDAY:2,4(Totals the levels all up + includes attribute bonuses)
		 * SPELLNAME:Blah|SCHOOL:blah|SUBSCHOOL:blah|Etc
		 */
		if (cache.containsKey(IOConstants.TAG_SPELLLIST))
		{
			for (final String line : cache.get(IOConstants.TAG_SPELLLIST))
			{
				parseSpellListLines(line);
			}
		}

		//For those that weren't explicitly specified, insert them
		insertDefaultClassSpellLists();

		if (cache.containsKey(IOConstants.TAG_SPELLNAME))
		{
			// Calculate what has been granted so far, particularly any ability granted spells
			thePC.setImporting(false);
			thePC.calcActiveBonuses();
			thePC.setImporting(true);

			for (final String line : cache.get(IOConstants.TAG_SPELLNAME))
			{
				parseSpellLine(line);
			}
		}

		/*
		 * #Character Description/Bio/Historys
		 * CHARACTERBIO:any text that's in the BIO field
		 * CHARACTERDESC:any text that's in the BIO field
		 */
		if (cache.containsKey(IOConstants.TAG_CHARACTERBIO))
		{
			parseCharacterBioLine(cache.get(IOConstants.TAG_CHARACTERBIO).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_CHARACTERDESC))
		{
			parseCharacterDescLine(cache.get(IOConstants.TAG_CHARACTERDESC).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_CHARACTERCOMP))
		{
			for (final String line : cache.get(IOConstants.TAG_CHARACTERCOMP))
			{
				parseCharacterCompLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_CHARACTERASSET))
		{
			for (final String line : cache.get(IOConstants.TAG_CHARACTERASSET))
			{
				parseCharacterAssetLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_CHARACTERMAGIC))
		{
			for (final String line : cache.get(IOConstants.TAG_CHARACTERMAGIC))
			{
				parseCharacterMagicLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_CHARACTERDMNOTES))
		{
			for (final String line : cache.get(IOConstants.TAG_CHARACTERDMNOTES))
			{
				parseCharacterDmNotesLine(line);
			}
		}

		/*
		 * #Character Master/Followers
		 * MASTER:Mynex|TYPE:Follower|HITDICE:20|FILE:E$\DnD\dnd-chars\ravenlock.pcg
		 * FOLLOWER:Raven|TYPE:Animal Companion|HITDICE:5|FILE:E$\DnD\dnd-chars\raven.pcg
		 */
		if (cache.containsKey(IOConstants.TAG_MASTER))
		{
			for (final String line : cache.get(IOConstants.TAG_MASTER))
			{
				parseMasterLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_FOLLOWER))
		{
			for (final String line : cache.get(IOConstants.TAG_FOLLOWER))
			{
				parseFollowerLine(line);
			}
		}

		/*
		 * Contains information about PC's equipment
		 * Money goes here as well
		 *
		 * #Character Equipment
		 * EQUIPNAME:Longsword|OUTPUTORDER:1|COST:5|WT:5|NOTE:It's very sharp!|>other info<
		 * EQUIPNAME:Backpack|OUTPUTORDER:-1|COST:5|WT:5|NOTE:on my back
		 * EQUIPNAME:Rope (Silk)|OUTPUTORDER:3|COST:5|WT:5
		 */
		if (cache.containsKey(IOConstants.TAG_MONEY))
		{
			for (final String line : cache.get(IOConstants.TAG_MONEY))
			{
				parseMoneyLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_EQUIPNAME))
		{
			// We process the bonuses loaded so far so that natural weapons from 
			// conditional abilities can be found. 
			thePC.setImporting(false);
			thePC.setCalcFollowerBonus();
			thePC.calcActiveBonuses();
			thePC.setImporting(true);

			for (final String line : cache.get(IOConstants.TAG_EQUIPNAME))
			{
				parseEquipmentLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_EQUIPSET))
		{
			/*
			 * strangely enough this works even if we create a
			 * EquipSet for content whose container EquipSet
			 * has not been created yet
			 * author: Thomas Behr 10-09-02
			 *
			 * Comment from EquipSet author:
			 * It only works because I've already sorted on output
			 * in PCGVer2Creator
			 * author: Jayme Cox 01-16-03
			 */

			//Collections.sort(cache.get(TAG_EQUIPSET), new EquipSetLineComparator());
			for (final String line : cache.get(IOConstants.TAG_EQUIPSET))
			{
				parseEquipmentSetLine(line);
			}
			EquipSetMigration.migrateEquipSets(thePC, pcgenVersion);
		}

		/*
		 * CALCEQUIPSET line contains the "working" equipment list
		 */
		if (cache.containsKey(IOConstants.TAG_CALCEQUIPSET))
		{
			for (final String line : cache.get(IOConstants.TAG_CALCEQUIPSET))
			{
				parseCalcEquipSet(line);
			}
		}

		/*
		 * #Character Notes Tab
		 */
		if (cache.containsKey(IOConstants.TAG_NOTE))
		{
			for (final String line : cache.get(IOConstants.TAG_NOTE))
			{
				parseNoteLine(line);
			}
		}

		/*
		 * #Character Bio
		 * CHARACTERNAME:Code Monkey
		 * TABNAME:Code Monkey the Best Ever No Really!
		 * PLAYERNAME:Jason Monkey
		 * HEIGHT:75
		 * WEIGHT:198
		 * AGE:17
		 * GENDER:enum name
		 * HANDED:enum name
		 * SKIN:text
		 * EYECOLOR:text
		 * HAIRCOLOR:text
		 * HAIRSTYLE:text
		 * LOCATION:text
		 * CITY:text
		 * PERSONALITYTRAIT1:text
		 * PERSONALITYTRAIT2:text
		 * SPEECHPATTERN:text
		 * PHOBIAS:text
		 * INTERESTS:text
		 * CATCHPHRASE:text
		 */
		if (cache.containsKey(IOConstants.TAG_CHARACTERNAME))
		{
			parseCharacterNameLine(cache.get(IOConstants.TAG_CHARACTERNAME).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_TABNAME))
		{
			parseTabNameLine(cache.get(IOConstants.TAG_TABNAME).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_PLAYERNAME))
		{
			parsePlayerNameLine(cache.get(IOConstants.TAG_PLAYERNAME).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_HEIGHT))
		{
			parseHeightLine(cache.get(IOConstants.TAG_HEIGHT).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_WEIGHT))
		{
			parseWeightLine(cache.get(IOConstants.TAG_WEIGHT).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_AGE))
		{
			parseAgeLine(cache.get(IOConstants.TAG_AGE).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_GENDER))
		{
			parseGenderLine(cache.get(IOConstants.TAG_GENDER).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_HANDED))
		{
			parseHandedLine(cache.get(IOConstants.TAG_HANDED).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_SKINCOLOR))
		{
			parseSkinColorLine(cache.get(IOConstants.TAG_SKINCOLOR).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_EYECOLOR))
		{
			parseEyeColorLine(cache.get(IOConstants.TAG_EYECOLOR).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_HAIRCOLOR))
		{
			parseHairColorLine(cache.get(IOConstants.TAG_HAIRCOLOR).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_HAIRSTYLE))
		{
			parseHairStyleLine(cache.get(IOConstants.TAG_HAIRSTYLE).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_LOCATION))
		{
			parseLocationLine(cache.get(IOConstants.TAG_LOCATION).get(0));
		}

		//this tag is obsolete, but left in for backward-compatibility, replaced by TAG_CITY
		if (cache.containsKey(IOConstants.TAG_RESIDENCE))
		{
			parseResidenceLine(cache.get(IOConstants.TAG_RESIDENCE).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_CITY))
		{
			parseCityLine(cache.get(IOConstants.TAG_CITY).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_BIRTHDAY))
		{
			parseBirthdayLine(cache.get(IOConstants.TAG_BIRTHDAY).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_BIRTHPLACE))
		{
			parseBirthplaceLine(cache.get(IOConstants.TAG_BIRTHPLACE).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_PERSONALITYTRAIT1))
		{
			for (final String line : cache.get(IOConstants.TAG_PERSONALITYTRAIT1))
			{
				parsePersonalityTrait1Line(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_PERSONALITYTRAIT2))
		{
			for (final String line : cache.get(IOConstants.TAG_PERSONALITYTRAIT2))
			{
				parsePersonalityTrait2Line(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_SPEECHPATTERN))
		{
			parseSpeechPatternLine(cache.get(IOConstants.TAG_SPEECHPATTERN).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_PHOBIAS))
		{
			parsePhobiasLine(cache.get(IOConstants.TAG_PHOBIAS).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_INTERESTS))
		{
			parseInterestsLine(cache.get(IOConstants.TAG_INTERESTS).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_CATCHPHRASE))
		{
			parseCatchPhraseLine(cache.get(IOConstants.TAG_CATCHPHRASE).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_PORTRAIT))
		{
			parsePortraitLine(cache.get(IOConstants.TAG_PORTRAIT).get(0));
		}

		if (cache.containsKey(IOConstants.TAG_PORTRAIT_THUMBNAIL_RECT))
		{
			parsePortraitThumbnailRectLine(cache.get(IOConstants.TAG_PORTRAIT_THUMBNAIL_RECT).get(0));
		}

		/*
		 * #Character Weapon proficiencies
		 */
		if (cache.containsKey(IOConstants.TAG_WEAPONPROF))
		{
			for (final String line : cache.get(IOConstants.TAG_WEAPONPROF))
			{
				parseWeaponProficienciesLine(line);
			}

			// This is not reliable during character load, and the warning is 
			// of little value, so I'm disabling the check for now. 
			//checkWeaponProficiencies();
		}

		/*
		 * # Temporary Bonuses
		 */
		if (cache.containsKey(IOConstants.TAG_TEMPBONUS))
		{
			for (final String line : cache.get(IOConstants.TAG_TEMPBONUS))
			{
				parseTempBonusLine(line);
			}
		}

		/*
		 * # EquipSet Temporary bonuses
		 * Must be done after both EquipSet and TempBonuses are parsed
		 */
		if (cache.containsKey(IOConstants.TAG_EQSETBONUS))
		{
			for (final String line : cache.get(IOConstants.TAG_EQSETBONUS))
			{
				parseEquipSetTempBonusLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_AGESET))
		{
			for (final String line : cache.get(IOConstants.TAG_AGESET))
			{
				parseAgeSet(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_CHRONICLE_ENTRY))
		{
			for (final String line : cache.get(IOConstants.TAG_CHRONICLE_ENTRY))
			{
				parseChronicleEntryLine(line);
			}
		}

		if (cache.containsKey(IOConstants.TAG_SUPPRESS_BIO_FIELDS))
		{
			for (final String line : cache.get(IOConstants.TAG_SUPPRESS_BIO_FIELDS))
			{
				parseSupressBioFieldsLine(line);
			}
		}

	}

	/*
	 * ###############################################################
	 * System Information methods
	 * ###############################################################
	 */
	private void checkDisplayListsHappy() throws PCGParseException
	{
		if (!Globals.displayListsHappy())
		{
			throw new PCGParseException("parseCampaignLines", "N/A", //$NON-NLS-1$ //$NON-NLS-2$
				LanguageBundle.getString("Exceptions.PCGenParser.NoCampaignInfo")); //$NON-NLS-1$
		}
	}

	/**
	 * Retrieve a list of campaigns named on the supplied lines. 
	 * @param lines The campaign lines from the PCG file.
	 * @param gameModeName The name of the charater's game mode.
	 * @return The list of campaigns.
	 * @throws PCGParseException 
	 */
	private List<Campaign> getCampaignList(List<String> lines, String gameModeName) throws PCGParseException
	{

		final List<Campaign> campaigns = new ArrayList<>();

		for (final String line : lines)
		{
			PCGTokenizer tokens;
			try
			{
				tokens = new PCGTokenizer(line);
			}
			catch (PCGParseException pcgpex)
			{
				/*
				 * Campaigns are critical for characters,
				 * need to stop the load process
				 *
				 * Thomas Behr 14-08-02
				 */
				throw new PCGParseException("parseCampaignLines", line, pcgpex.getMessage(), pcgpex);
			}

			for (PCGElement element : tokens.getElements())
			{
				String sourceKey = SourceMigration.getNewSourceKey(element.getText(), pcgenVersion, gameModeName);
				final Campaign aCampaign = Globals.getCampaignKeyed(sourceKey);

				if (aCampaign != null)
				{
					campaigns.add(aCampaign);
				}
			}
		}

		return campaigns;
	}

	private void parseCatchPhraseLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.CATCHPHRASE,
			EntityEncoder.decode(line.substring(IOConstants.TAG_CATCHPHRASE.length() + 1)));
	}

	private void parseCharacterAssetLine(final String line)
	{
		thePC.setStringFor(PCStringKey.ASSETS,
			EntityEncoder.decode(line.substring(IOConstants.TAG_CHARACTERASSET.length() + 1)));
	}

	private void parseCharacterCompLine(final String line)
	{
		thePC.setStringFor(PCStringKey.COMPANIONS,
			EntityEncoder.decode(line.substring(IOConstants.TAG_CHARACTERCOMP.length() + 1)));
	}

	private void parseCharacterDescLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.DESCRIPTION,
			EntityEncoder.decode(line.substring(IOConstants.TAG_CHARACTERDESC.length() + 1)));
	}

	private void parseCharacterMagicLine(final String line)
	{
		thePC.setStringFor(PCStringKey.MAGIC,
			EntityEncoder.decode(line.substring(IOConstants.TAG_CHARACTERMAGIC.length() + 1)));
	}

	private void parseCharacterDmNotesLine(final String line)
	{
		thePC.setStringFor(PCStringKey.GMNOTES,
			EntityEncoder.decode(line.substring(IOConstants.TAG_CHARACTERDMNOTES.length() + 1)));
	}

	/*
	 * ###############################################################
	 * Character Bio methods
	 * ###############################################################
	 */
	private void parseCharacterNameLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.NAME,
			EntityEncoder.decode(line.substring(IOConstants.TAG_CHARACTERNAME.length() + 1)));
	}

	private void parseCityLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.CITY,
			EntityEncoder.decode(line.substring(IOConstants.TAG_CITY.length() + 1)));
	}

	private void parseClassAbilitiesLevelLine(final String line, final List<PCLevelInfo> pcLevelInfoList)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					LanguageBundle.getFormattedString(
						"Warnings.PCGenParser.IllegalClassAbility", //$NON-NLS-1$
				line, pcgpex.getMessage());
			warnings.add(message);

			return;
		}

		int level = -1;
		PCClass aPCClass = null;
		PCGElement element;
		PCLevelInfo pcl = null;

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		// the first element defines the class key name and level
		// eg: Cleric=4
		if (it.hasNext())
		{
			element = it.next();

			final int index = element.getText().indexOf('=');

			if (index < 0)
			{
				final String message =
						LanguageBundle.getFormattedString(
							"Warnings.PCGenParser.InvalidClassLevel", //$NON-NLS-1$
					element.getText());
				warnings.add(message);

				return;
			}

			final String classKeyName = EntityEncoder.decode(element.getText().substring(0, index));
			aPCClass = thePC.getClassKeyed(classKeyName);

			if (aPCClass == null)
			{
				final String message =
						LanguageBundle.getFormattedString(
							"Warnings.PCGenParser.ClassNotFound", //$NON-NLS-1$
					classKeyName);
				warnings.add(message);

				return;
			}

			try
			{
				level = Integer.parseInt(element.getText().substring(index + 1));
			}
			catch (NumberFormatException nfe)
			{
				final String message =
						LanguageBundle.getFormattedString(
							"Warnings.PCGenParser.InvalidClassLevel", //$NON-NLS-1$
					element.getText());
				warnings.add(message);

				return;
			}

			if (level < 1)
			{
				final String message =
						LanguageBundle.getFormattedString(
							"Warnings.PCGenParser.InvalidClassLevel", //$NON-NLS-1$
					element.getText());
				warnings.add(message);

				return;
			}

			for (PCLevelInfo info : pcLevelInfoList)
			{
				if (classKeyName.equalsIgnoreCase(info.getClassKeyName()) && level == info.getClassLevel())
				{
					pcl = info;
					break;
				}
			}
			if (pcl == null)
			{
				pcl = thePC.addLevelInfo(classKeyName);
				pcl.setClassLevel(level);
			}
			else
			{
				thePC.addLevelInfo(pcl);
			}
			pcl.setSkillPointsRemaining(0);
		}

		while (it.hasNext())
		{
			element = it.next();
			String tag = element.getName();

			if (IOConstants.TAG_SUBSTITUTIONLEVEL.equals(tag))
			{
				final String substitutionClassKeyName = EntityEncoder.decode(element.getText());
				SubstitutionClass aSubstitutionClass = aPCClass.getSubstitutionClassKeyed(substitutionClassKeyName);

				if (aSubstitutionClass == null)
				{
					final String message =
							LanguageBundle.getFormattedString(
								"Warnings.PCGenParser.ClassNotFound", //$NON-NLS-1$
						substitutionClassKeyName);
					warnings.add(message);

					return;
				}
				SubstitutionLevelSupport.applyLevelArrayModsToLevel(aSubstitutionClass, aPCClass, level, thePC);
				thePC.setSubstitutionClassName(thePC.getActiveClassLevel(aPCClass, level), substitutionClassKeyName);
			}
			else if (IOConstants.TAG_HITPOINTS.equals(tag))
			{
				try
				{
					PCClassLevel classLevel = thePC.getActiveClassLevel(aPCClass, level - 1);
					thePC.setHP(classLevel, Integer.valueOf(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					final String message =
							LanguageBundle.getFormattedString(
								"Warnings.PCGenParser.InvalidHP", //$NON-NLS-1$
						tag, element.getText());
					warnings.add(message);
				}
			}
			else if (IOConstants.TAG_SAVES.equals(tag))
			{
				for (final PCGElement child : element.getChildren())
				{
					final String dString = EntityEncoder.decode(child.getText());
					if (dString.startsWith(IOConstants.TAG_BONUS + IOConstants.TAG_SEPARATOR))
					{
						String bonusString = dString.substring(6);
						int pipeLoc = bonusString.indexOf('|');
						if (pipeLoc != -1)
						{
							CDOMObject target = aPCClass;
							String potentialInt = bonusString.substring(0, pipeLoc);
							try
							{
								int bonusLevel = Integer.parseInt(potentialInt);
								if (bonusLevel > 0)
								{
									target = thePC.getActiveClassLevel(aPCClass, bonusLevel);
								}
								bonusString = bonusString.substring(pipeLoc + 1);
							}
							catch (NumberFormatException e)
							{
								//OK (no level embedded in file)
								target = thePC.getActiveClassLevel(aPCClass, level);
							}
							BonusAddition.applyBonus(bonusString, "", thePC, target);
						}
					}
				}
			}
			else if (IOConstants.TAG_SPECIALTIES.equals(tag))
			{
				for (final PCGElement child : element.getChildren())
				{
					thePC.setAssoc(aPCClass, AssociationKey.SPECIALTY, EntityEncoder.decode(child.getText()));
				}
			}
			else if (IOConstants.TAG_SPECIALABILITIES.equals(tag))
			{
				for (PCGElement child : element.getChildren())
				{
					String specialAbilityName = EntityEncoder.decode(child.getText());
					if (pcgenVersion[0] <= 5 && pcgenVersion[1] <= 5 && pcgenVersion[2] < 6)
					{
						if (specialAbilityName.equals("Turn Undead")) //$NON-NLS-1$
						{
							parseFeatLine("FEAT:Turn Undead|TYPE:SPECIAL.TURNUNDEAD|DESC:"); //$NON-NLS-1$
							continue;
						}
						else if (specialAbilityName.equals("Rebuke Undead")) //$NON-NLS-1$
						{
							parseFeatLine("FEAT:Rebuke Undead|TYPE:SPECIAL.TURNUNDEAD|DESC:"); //$NON-NLS-1$
							continue;
						}
					}
					SpecialAbility specialAbility = new SpecialAbility(specialAbilityName);

					CDOMObject target = thePC.getActiveClassLevel(aPCClass, level);

					if (!thePC.hasSpecialAbility(specialAbilityName))
					{
						thePC.addUserSpecialAbility(specialAbility, target);
					}
				}
			}
			else if (tag.equals(IOConstants.TAG_LEVELABILITY))
			{
				parseLevelAbilityInfo(element, aPCClass, level);
			}
			else if (tag.equals(IOConstants.TAG_ADDTOKEN))
			{
				parseAddTokenInfo(element, thePC.getActiveClassLevel(aPCClass, level));
			}

			//
			// abbrev=score
			//
			else if (tag.equals(IOConstants.TAG_PRESTAT) || tag.equals(IOConstants.TAG_POSTSTAT))
			{
				boolean isPre = false;

				if (tag.equals(IOConstants.TAG_PRESTAT))
				{
					isPre = true;
				}

				final int idx = element.getText().indexOf('=');

				if (idx > 0)
				{
					String statAbb = element.getText().substring(0, idx);
					final PCStat pcstat = Globals.getContext().getReferenceContext()
						.silentlyGetConstructedCDOMObject(PCStat.class, statAbb);

					if (pcstat != null)
					{
						try
						{
							thePC.saveStatIncrease(pcstat, Integer.parseInt(element.getText().substring(idx + 1)),
								isPre);
						}
						catch (NumberFormatException nfe)
						{
							final String msg =
									LanguageBundle.getFormattedString(
										"Warnings.PCGenParser.InvalidStatMod", //$NON-NLS-1$
								tag, element.getText());
							warnings.add(msg);
						}
					}
					else
					{
						final String msg =
								LanguageBundle.getFormattedString(
									"Warnings.PCGenParser.UnknownStat", //$NON-NLS-1$
							tag, element.getText());
						warnings.add(msg);
					}
				}
				else
				{
					final String msg =
							LanguageBundle.getFormattedString(
								"Warnings.PCGenParser.MissingEquals", //$NON-NLS-1$
						tag, element.getText());
					warnings.add(msg);
				}
			}
			else if (IOConstants.TAG_SKILLPOINTSGAINED.equals(tag))
			{
				pcl.setFixedSkillPointsGained(Integer.parseInt(element.getText()));
			}
			else if (IOConstants.TAG_SKILLPOINTSREMAINING.equals(tag))
			{
				pcl.setSkillPointsRemaining(Integer.parseInt(element.getText()));
			}
			else if (IOConstants.TAG_DATA.equals(tag))
			{
				// TODO
				// for now it's ok to ignore it!
			}
			else
			{
				final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.UnknownTag", //$NON-NLS-1$
					tag, element.getText());
				warnings.add(msg);
			}
		}

		// TODO:
		// process data
		//
		// need to add some consistency checks here to avoid
		// - duplicate entries for one and the same class/level pair
		// - missing entries for a given class/level pair
	}

	private void parseAddTokenInfo(PCGElement element, CDOMObject cdo)
	{
		Iterator<PCGElement> it2 = element.getChildren().iterator();
		if (!it2.hasNext())
		{
			warnings.add(cdo.getDisplayName() + "(" + cdo.getClass().getName() + ")\nInvalid save structure in ADD:");
			return;
		}
		PCGElement addType = it2.next();
		String name = addType.getName();
		String dString = EntityEncoder.decode(addType.getText());

		List<PersistentTransitionChoice<?>> addList = cdo.getListFor(ListKey.ADD);
		if (addList == null)
		{
			warnings.add(cdo.getDisplayName() + "(" + cdo.getClass().getName() + ")\nCould not find any ADD: " + name
				+ "|" + dString);
			return;
		}
		boolean found = false;
		for (PersistentTransitionChoice<?> tc : addList)
		{
			found |= processTransitionChoice(cdo, it2, name, dString, tc);
		}
		if (!found)
		{
			warnings.add(cdo.getDisplayName() + "(" + cdo.getClass().getName() + ")\nCould not find matching ADD: "
				+ name + "|" + dString);
		}
	}

	private <T> boolean processTransitionChoice(CDOMObject cdo, Iterator<PCGElement> it2, String name, String dString,
		PersistentTransitionChoice<T> tc)
	{
		SelectableSet<? extends T> choices = tc.getChoices();
		if (dString.equals(choices.getLSTformat()))
		{
			//Match
			while (it2.hasNext())
			{
				String choice = EntityEncoder.decode(it2.next().getText());
				Object obj = tc.decodeChoice(Globals.getContext(), choice);
				if (obj == null)
				{
					warnings.add(cdo.getDisplayName() + "(" + cdo.getClass().getName() + ")\nCould not decode " + choice
						+ " for ADD: " + name + "|" + dString);
				}
				else
				{
					tc.restoreChoice(thePC, cdo, tc.castChoice(obj));
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	/*
	 * ###############################################################
	 * Character Class(es) methods
	 * ###############################################################
	 */
	private void parseClassLine(final String line) throws PCGParseException
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Classes are critical for characters,
			 * need to stop the load process
			 *
			 * Thomas Behr 14-08-02
			 */
			throw new PCGParseException("parseClassLine", line, pcgpex.getMessage(), pcgpex); //$NON-NLS-1$
		}

		PCClass aPCClass = null;
		String tag;
		PCGElement element;

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		// the first element defines the class key name!!!
		if (it.hasNext())
		{
			element = it.next();

			String classKey = EntityEncoder.decode(element.getText());
			// First check for an existing class, say from a racial casting ability
			aPCClass = thePC.getClassKeyed(classKey);
			if (aPCClass == null)
			{
				aPCClass = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class,
					classKey);

				if (aPCClass != null)
				{
					// Icky: Need to redesign the way classes work!
					// Icky: Having to clone the class here is UGLY!
					aPCClass = aPCClass.clone();
				}
				else
				{
					final String msg =
							LanguageBundle.getFormattedString(
								"Warnings.PCGenParser.CouldntAddClass", //$NON-NLS-1$
						element.getText());
					warnings.add(msg);

					return;
				}
			}
		}

		int level = -1;
		int skillPool = -1;
		String subClassKey = Constants.NONE;

		while (it.hasNext())
		{
			element = it.next();
			tag = element.getName();

			if (IOConstants.TAG_SUBCLASS.equals(tag))
			{
				subClassKey = EntityEncoder.decode(element.getText());
				if ((!subClassKey.isEmpty()) && !subClassKey.equals(Constants.NONE))
				{
					SubClass sc = aPCClass.getSubClassKeyed(subClassKey);
					if (sc == null)
					{
						if (subClassKey.equals(aPCClass.getKeyName()))
						{
							subClassKey = Constants.NONE;
						}
						else
						{
							final String msg =
									LanguageBundle.getFormattedString(
										"Warnings.PCGenParser.InvalidSubclass", //$NON-NLS-1$
								element.getText());
							warnings.add(msg);
						}
					}
				}
			}

			if (IOConstants.TAG_LEVEL.equals(tag))
			{
				try
				{
					level = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					final String msg =
							LanguageBundle.getFormattedString(
								"Warnings.PCGenParser.InvalidLevel", //$NON-NLS-1$
						element.getText());
					warnings.add(msg);
				}
			}
			else if (IOConstants.TAG_SKILLPOOL.equals(tag))
			{
				try
				{
					skillPool = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					final String msg =
							LanguageBundle.getFormattedString(
								"Warnings.PCGenParser.InvalidSkillPool", //$NON-NLS-1$
						element.getText());
					warnings.add(msg);
				}
			}
			else if (IOConstants.TAG_CANCASTPERDAY.equals(tag))
			{
				// TODO
			}
			else if (IOConstants.TAG_SPELLBASE.equals(tag))
			{
				final String spellBase = EntityEncoder.decode(element.getText());
				if (!Constants.NONE.equals(spellBase))
				{
					Globals.getContext().unconditionallyProcess(aPCClass, "SPELLSTAT", spellBase);
				}
			}
			else if (IOConstants.TAG_PROHIBITED.equals(tag))
			{
				String prohib = EntityEncoder.decode(element.getText());
				StringTokenizer st = new StringTokenizer(prohib, Constants.COMMA);
				while (st.hasMoreTokens())
				{
					String choice = st.nextToken();
					if (!"None".equalsIgnoreCase(choice))
					{
						SpellProhibitor prohibSchool = new SpellProhibitor();
						prohibSchool.setType(ProhibitedSpellType.SCHOOL);
						prohibSchool.addValue(choice);
						SpellProhibitor prohibSubSchool = new SpellProhibitor();
						prohibSubSchool.setType(ProhibitedSpellType.SUBSCHOOL);
						prohibSubSchool.addValue(choice);
						thePC.addProhibitedSchool(prohibSchool, aPCClass);
						thePC.addProhibitedSchool(prohibSubSchool, aPCClass);
					}
				}
			}
		}

		if (level > -1)
		{
			thePC.addClass(aPCClass);

			if (StringUtils.isNotBlank(subClassKey) && !subClassKey.equals(Constants.NONE))
			{
				SubClassApplication.setSubClassKey(thePC, aPCClass, subClassKey);
			}

			for (int i = 0; i < level; ++i)
			{
				thePC.addLevelInfo(aPCClass.getKeyName());
				aPCClass.addLevel(false, false, thePC, true);

			}
		}

		//Must process ADD after CLASS is added to the PC
		for (PCGElement e : new PCGTokenizer(line).getElements())
		{
			tag = e.getName();
			if (tag.equals(IOConstants.TAG_ADDTOKEN))
			{
				parseAddTokenInfo(e, aPCClass);
			}
		}

		if (skillPool > -1)
		{
			thePC.setSkillPool(aPCClass, skillPool);
		}
	}

	/**
	 * ###############################################################
	 * Character Deity/Domain methods
	 * ###############################################################
	 * @param line
	 **/
	private void parseDeityLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalDeity", //$NON-NLS-1$
				line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final String deityKey = EntityEncoder.decode(tokens.getElements().get(0).getText());

		Deity aDeity =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Deity.class, deityKey);
		if (aDeity != null)
		{
			thePC.setDeity(aDeity);
		}
		else if (!Constants.NONE.equals(deityKey))
		{
			// TODO
			// create Deity object from information contained in pcg
			// for now issue a warning
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.DeityNotFound", //$NON-NLS-1$
				deityKey);
			warnings.add(msg);
		}
	}

	private void parseDomainLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalDomain", //$NON-NLS-1$
				line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		if (it.hasNext())
		{
			PCGElement element = it.next();

			// the first element defines the domain name
			final String domainKey = EntityEncoder.decode(element.getText());
			final Domain aDomain = Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(Domain.class, domainKey);

			if ((aDomain == null) && (!Constants.NONE.equals(domainKey)))
			{
				// TODO
				// create Domain object from
				// information contained in pcg
				// But for now just issue a warning
				final String msg = 
						LanguageBundle.getFormattedString("Warnings.PCGenParser.DomainNotFound", //$NON-NLS-1$
							domainKey);
				warnings.add(msg);
			}
			else if (!thePC.hasDomain(aDomain) && (!Constants.NONE.equals(domainKey)))
			{
				// PC doesn't have the domain, so create a new
				// one and add it to the PC domain list
				ClassSource source = null;

				String fullassoc = null;
				while (it.hasNext())
				{
					element = it.next();
					String tag = element.getName();

					if (IOConstants.TAG_SOURCE.equals(tag))
					{
						source = getDomainSource(sourceElementToString(element));
					}
					else if (IOConstants.TAG_ASSOCIATEDDATA.equals(tag))
					{
						if (fullassoc != null)
						{
							warnings.add("Found multiple selections for Domain: " + aDomain.getKeyName());
						}
						fullassoc = EntityEncoder.decode(element.getText());
					}
					else if (tag.equals(IOConstants.TAG_DOMAINGRANTS))
					{
						//Can safely ignore
					}
					else if (!tag.equals(IOConstants.TAG_ADDTOKEN))
					{
						final String msg =
								LanguageBundle.getFormattedString(
									"Warnings.PCGenParser.UnknownDomainInfo", //$NON-NLS-1$
							tag + ":" + element.getText());
						warnings.add(msg);
					}
				}
				if (source == null)
				{
					warnings.add("Domain not added due to no source: " + domainKey);
				}
				else
				{
					domainInputFacet.importSelection(thePC.getCharID(), aDomain, fullassoc, source);
					DomainApplication.applyDomain(thePC, aDomain);
				}
				try
				{
					//Must process ADD after DOMAIN is added to the PC
					for (PCGElement e : new PCGTokenizer(line).getElements())
					{
						String tag = e.getName();
						if (tag.equals(IOConstants.TAG_ADDTOKEN))
						{
							parseAddTokenInfo(e, aDomain);
						}
					}
				}
				catch (PCGParseException pcgpex)
				{
					final String msg =
							LanguageBundle.getFormattedString(
								"Warnings.PCGenParser.IllegalDomain", //$NON-NLS-1$
						line, pcgpex.getMessage());
					warnings.add(msg);

					return;
				}
			}
			else
			{
				// PC already has this domain
				Logging.errorPrintLocalised("Errors.PCGenParser.DuplicateDomain", //$NON-NLS-1$
					domainKey);
			}
		}
	}

	/**
	 * ###############################################################
	 * EquipSet Temp Bonuses
	 * ###############################################################
	 * @param line
	 **/
	private void parseEquipSetTempBonusLine(final String line)
	{
		PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					LanguageBundle.getFormattedString(
						"Warnings.PCGenParser.IllegalEquipSetTempBonus", //$NON-NLS-1$
				line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		String tag;
		String tagString = null;

		for (PCGElement element : tokens.getElements())
		{
			tag = element.getName();

			if (IOConstants.TAG_EQSETBONUS.equals(tag))
			{
				tagString = EntityEncoder.decode(element.getText());
			}
		}

		if (tagString == null)
		{
			final String msg =
					LanguageBundle.getFormattedString(
						"Warnings.PCGenParser.InvalidEquipSetTempBonus", //$NON-NLS-1$
				line);
			warnings.add(msg);

			return;
		}

		final EquipSet eSet = thePC.getEquipSetByIdPath(tagString);

		if (eSet == null)
		{
			return;
		}

		//# EquipSet Temp Bonuses
		//EQSETBONUS:0.2|TEMPBONUS:NAME=Haste|TBTARGET:PC|TEMPBONUS:SPELL=Shield of Faith|TBTARGET:PC
		final Map<BonusObj, BonusManager.TempBonusInfo> aList = new IdentityHashMap<>();

		for (final PCGElement element : tokens.getElements())
		{
			tag = element.getName();

			if (IOConstants.TAG_TEMPBONUSBONUS.equals(tag))
			{
				final String aString = EntityEncoder.decode(element.getText());

				// Parse aString looking for
				// TEMPBONUS and TBTARGET pairs
				StringTokenizer aTok = new StringTokenizer(aString, IOConstants.TAG_SEPARATOR);

				if (aTok.countTokens() < 2)
				{
					continue;
				}

				String sName = aTok.nextToken();
				String tName = aTok.nextToken();
				aList.putAll(getBonusFromName(sName, tName));
			}
		}

		eSet.setTempBonusList(aList);
	}

	private void parseCharacterTypeLine(final String line)
	{
		final StringTokenizer stok = new StringTokenizer(line.substring(IOConstants.TAG_CHARACTERTYPE.length() + 1),
			IOConstants.TAG_END, false);

		String characterType = stok.nextToken();
		if (!SettingsHandler.getGame().getCharacterTypeList().contains(characterType))
		{
			String wantedType = characterType;
			characterType = SettingsHandler.getGame().getDefaultCharacterType();
			final String message = "Character type " + wantedType + " not found. Using " + characterType; //$NON-NLS-1$
			warnings.add(message);
		}
		thePC.setCharacterType(characterType);
	}

	private void parsePreviewSheetLine(final String line)
	{
		final StringTokenizer stok = new StringTokenizer(line.substring(IOConstants.TAG_PREVIEWSHEET.length() + 1),
			IOConstants.TAG_END, false);

		thePC.setPreviewSheet(stok.nextToken());
	}

	/*
	 * ###############################################################
	 * Character Experience methods
	 * ###############################################################
	 */
	private void parseExperienceLine(final String line) throws PCGParseException
	{

		try
		{
			final StringTokenizer stok = new StringTokenizer(line.substring(IOConstants.TAG_EXPERIENCE.length() + 1),
				IOConstants.TAG_END, false);
			thePC.setXP(Integer.parseInt(stok.nextToken()));
		}
		catch (NumberFormatException nfe)
		{
			throw new PCGParseException("parseExperienceLine", line, nfe.getMessage(), nfe); //$NON-NLS-1$
		}
	}

	private void parseExperienceTableLine(final String line)
	{
		final StringTokenizer stok = new StringTokenizer(line.substring(IOConstants.TAG_EXPERIENCETABLE.length() + 1),
			IOConstants.TAG_END, false);

		String xpTableName = stok.nextToken();
		if (!SettingsHandler.getGame().getXPTableNames().contains(xpTableName))
		{
			String wantedName = xpTableName;
			xpTableName = SettingsHandler.getGame().getDefaultXPTableName();
			final String message = "XP table " + wantedName + " not found. Using " + xpTableName; //$NON-NLS-1$
			warnings.add(message);
		}
		thePC.setXPTable(xpTableName);
	}

	private void parseEyeColorLine(final String line)
	{
		thePC.setEyeColor(EntityEncoder.decode(line.substring(IOConstants.TAG_EYECOLOR.length() + 1)));
	}

	/*
	 * ###############################################################
	 * Character Ability methods
	 * ###############################################################
	 */

	private void parseAbilityLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalAbility", //$NON-NLS-1$
				line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		AbilityCategory category = null;
		Nature nature = Nature.NORMAL;
		String abilityCat = null;
		Ability ability = null;
		String missingCat = null;

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		// the first element defines the AbilityCategory key name
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			final String categoryKey = EntityEncoder.decode(element.getText());
			category = SettingsHandler.getGame().getAbilityCategory(categoryKey);
			if (category == null)
			{
				missingCat = categoryKey;
			}
		}

		// The next element will be the nature
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			final String natureKey = EntityEncoder.decode(element.getText());
			nature = Nature.valueOf(natureKey);
		}

		// The next element will be the ability's innate category
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			abilityCat = EntityEncoder.decode(element.getText());
		}

		// The next element will be the ability key
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			String abilityKey = EntityEncoder.decode(element.getText());
			// Check for an ability that has been updated.
			CategorisedKey categorisedKey = AbilityMigration.getNewAbilityKey(abilityCat, abilityKey, pcgenVersion,
				SettingsHandler.getGame().getName());
			abilityCat = categorisedKey.getCategory();
			abilityKey = categorisedKey.getKey();
			AbilityCategory innateCategory = SettingsHandler.getGame().getAbilityCategory(abilityCat);
			if (innateCategory == null)
			{
				missingCat = abilityCat;
			}

			if (innateCategory == null || category == null)
			{
				final String msg =
						LanguageBundle.getFormattedString(
							"Warnings.PCGenParser.AbilityCategoryNotFound", //$NON-NLS-1$
					abilityKey, missingCat);
				warnings.add(msg);
				return;
			}
			ability = Globals.getContext().getReferenceContext().getManufacturerId(innateCategory)
				.getActiveObject(abilityKey);
			if (ability == null)
			{
				warnings.add("Unable to Find Ability: " + abilityKey);
				return;
			}
		}
		List<String> associations = new ArrayList<>();
		List<BonusObj> bonuses = new ArrayList<>();
		while (it.hasNext())
		{
			final PCGElement element = it.next();
			final String tag = element.getName();

			if (tag.equals(IOConstants.TAG_APPLIEDTO))
			{
				associations.add(EntityEncoder.decode(element.getText()));
			}
			else if (IOConstants.TAG_SAVE.equals(tag))
			{
				final String saveKey = EntityEncoder.decode(element.getText());

				// TODO - This never gets written to the file
				if (saveKey.startsWith(IOConstants.TAG_BONUS) && (saveKey.length() > 6))
				{
					final BonusObj aBonus = Bonus.newBonus(Globals.getContext(), saveKey.substring(6));

					if (aBonus != null)
					{
						bonuses.add(aBonus);
					}
				}
				else
				{
					if (Logging.isDebugMode())
					{
						Logging.debugPrint("Ignoring SAVE:" + saveKey);
					}
				}
			}
		}
		if (ability != null && category != null)
		{
			CNAbility cna = null;
			boolean needError = true;
			if (nature == Nature.NORMAL)
			{
				// If we weren't loading an old character who had feats stored as seperate
				// lines, save the feat now.
				if (!featsPresent || category != AbilityCategory.FEAT)
				{
					try
					{
						cna = CNAbilityFactory.getCNAbility(category, nature, ability);
					}
					catch (IllegalArgumentException e)
					{
						Logging.log(Logging.INFO, "Unabe to parse ability line: " + e.getMessage());
					}
				}
				else
				{
					needError = false;
				}
			}
			else if (nature == Nature.VIRTUAL)
			{
				cna = CNAbilityFactory.getCNAbility(category, nature, ability);
			}
			if (cna == null)
			{
				if (needError)
				{
					warnings.add("Unable to build Ability: " + ability);
				}
			}
			else
			{
				if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
				{
					for (String appliedToKey : associations)
					{
						String[] assoc = appliedToKey.split(Constants.COMMA, -1);
						for (String string : assoc)
						{
							CNAbilitySelection cnas = new CNAbilitySelection(cna, string);
							try
							{
								if (nature == Nature.VIRTUAL)
								{
									thePC.addSavedAbility(cnas, UserSelection.getInstance(),
										UserSelection.getInstance());
								}
								else
								{
									thePC.addAbility(cnas, UserSelection.getInstance(), UserSelection.getInstance());
								}
							}
							catch (IllegalArgumentException e)
							{
								Logging.errorPrint("PCGVer2Parser.parseAbilityLine failed", e);

								warnings.add(cna + " with selection: " + string + " is no longer valid.");
							}
						}
					}
				}
				else
				{
					if (!associations.isEmpty())
					{
						warnings.add(cna + " found with selections: " + associations + " but is MULT:NO in the data");
					}
					CNAbilitySelection cnas = new CNAbilitySelection(cna);
					if (nature == Nature.VIRTUAL)
					{
						thePC.addSavedAbility(cnas, UserSelection.getInstance(), UserSelection.getInstance());
					}
					else
					{
						thePC.addAbility(cnas, UserSelection.getInstance(), UserSelection.getInstance());
					}
				}
				for (BonusObj b : bonuses)
				{
					thePC.addSaveableBonus(b, cna.getAbility());
				}
			}
		}
	}

	/*
	 * ###############################################################
	 * Character Feats methods
	 * ###############################################################
	 */
	private void parseFeatLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalFeat", //$NON-NLS-1$
				line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		// the first element defines the Ability key name
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			final String abilityKey = EntityEncoder.decode(element.getText());

			/* First, check to see if the PC already has this ability. If so,
			 * then we just need to mod it. Otherwise we need to create a new
			 * one and add it using non-aggregate (when using aggregate, we
			 * get clones of the PCs actual feats, which don't get saved or
			 * preserved) */
			Ability anAbility = Globals.getContext().getReferenceContext().getManufacturerId(AbilityCategory.FEAT)
				.getActiveObject(abilityKey);
			if (anAbility == null)
			{
				final String msg =
						LanguageBundle.getFormattedString(
							"Warnings.PCGenParser.CouldntAddAbility", //$NON-NLS-1$
					abilityKey);
				warnings.add(msg);

				return;
			}

			CNAbility pcAbility = CNAbilityFactory.getCNAbility(AbilityCategory.FEAT, Nature.NORMAL, anAbility);
			if (!anAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				thePC.addAbility(new CNAbilitySelection(pcAbility), UserSelection.getInstance(),
					UserSelection.getInstance());
			}
			parseFeatsHandleAppliedToAndSaveTags(it, pcAbility);
			featsPresent = true;
		}
	}

	private void parseFeatPoolLine(final String line)
	{
		try
		{
			double featPool = Double.parseDouble(line.substring(IOConstants.TAG_FEATPOOL.length() + 1));
			// In earlier versions the featpool included the bonus, so we need to counter it
			if (compareVersionTo(new int[]{5, 11, 1}) < 0)
			{
				calcFeatPoolAfterLoad = true;
				baseFeatPool = featPool;
			}
			else
			{
				thePC.setUserPoolBonus(AbilityCategory.FEAT, new BigDecimal(String.valueOf(featPool)));
			}
		}
		catch (NumberFormatException nfe)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalFeatPool", //$NON-NLS-1$
				line);
			warnings.add(msg);
		}
	}

	private void parseUserPoolLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalAbilityPool", //$NON-NLS-1$
						line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final Iterator<PCGElement> it = tokens.getElements().iterator();
		final String cat = EntityEncoder.decode(it.next().getText());
		final AbilityCategory category = SettingsHandler.getGame().getAbilityCategory(cat);
		try
		{
			thePC.setUserPoolBonus(category, new BigDecimal(it.next().getText()));
		}
		catch (NumberFormatException nfe)
		{
			final String msg =
					LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalAbilityPool", //$NON-NLS-1$
						line);
			warnings.add(msg);
		}
	}

	private void parseFeatsHandleAppliedToAndSaveTags(final Iterator<PCGElement> it, final CNAbility cna)
	{
		Ability aFeat = cna.getAbility();
		while (it.hasNext())
		{
			final PCGElement element = it.next();
			final String tag = element.getName();

			if (IOConstants.TAG_APPLIEDTO.equals(tag))
			{
				final String appliedToKey = EntityEncoder.decode(element.getText());

				if (aFeat.getSafe(ObjectKey.MULTIPLE_ALLOWED))
				{
					String[] assoc = appliedToKey.split(Constants.COMMA, -1);
					for (String string : assoc)
					{
						CNAbilitySelection cnas = new CNAbilitySelection(cna, string);
						if (cna.getNature() == Nature.VIRTUAL)
						{
							thePC.addSavedAbility(cnas, UserSelection.getInstance(), UserSelection.getInstance());
						}
						else
						{
							thePC.addAbility(cnas, UserSelection.getInstance(), UserSelection.getInstance());
						}
					}
				}
			}
			else if (IOConstants.TAG_SAVE.equals(tag))
			{
				final String saveKey = EntityEncoder.decode(element.getText());

				if (saveKey.startsWith(IOConstants.TAG_BONUS) && (saveKey.length() > 6))
				{
					final BonusObj aBonus = Bonus.newBonus(Globals.getContext(), saveKey.substring(6));

					if (aBonus != null)
					{
						thePC.addSaveableBonus(aBonus, aFeat);
					}
				}
				else
				{
					if (Logging.isDebugMode())
					{
						Logging.debugPrint("Ignoring SAVE:" + saveKey);
					}
				}
			}
			else if (tag.equals(IOConstants.TAG_LEVELABILITY))
			{
				parseLevelAbilityInfo(element, aFeat);
			}
			else if (tag.equals(IOConstants.TAG_ADDTOKEN))
			{
				parseAddTokenInfo(element, aFeat);
			}
		}
	}

	/*
	 * ###############################################################
	 * Character Follower methods
	 * ###############################################################
	 */
	private void parseFollowerLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalFollower", //$NON-NLS-1$
				line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final Follower aFollower = new Follower(Constants.EMPTY_STRING, Constants.EMPTY_STRING, null);

		for (final PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (IOConstants.TAG_FOLLOWER.equals(tag))
			{
				aFollower.setName(EntityEncoder.decode(element.getText()));
			}
			else if (IOConstants.TAG_TYPE.equals(tag))
			{
				String cType = EntityEncoder.decode(element.getText());
				CompanionList cList = Globals.getContext().getReferenceContext()
					.silentlyGetConstructedCDOMObject(CompanionList.class, cType);
				if (cList == null)
				{
					Logging.errorPrint("Cannot find CompanionList: " + cType);
				}
				else
				{
					aFollower.setType(cList);
				}
			}
			else if (IOConstants.TAG_RACE.equals(tag))
			{
				String raceText = EntityEncoder.decode(element.getText());
				Race r = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Race.class,
					raceText);
				if (r == null)
				{
					Logging.errorPrint("Cannot find Race: " + raceText);
				}
				else
				{
					aFollower.setRace(r);
				}
			}
			else if (IOConstants.TAG_HITDICE.equals(tag))
			{
				try
				{
					aFollower.setUsedHD(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (IOConstants.TAG_FILE.equals(tag))
			{
				String inputFileName = EntityEncoder.decode(element.getText());
				String masterFileName = makeFilenameAbsolute(inputFileName);
				if (masterFileName == null)
				{
					final String msg =
							LanguageBundle.getFormattedString(
								"Warnings.PCGenParser.CantFindFollower", //$NON-NLS-1$
						inputFileName);
					warnings.add(msg);
				}
				else
				{
					aFollower.setFileName(masterFileName);
				}
			}
		}

		if (!Constants.EMPTY_STRING.equals(aFollower.getFileName())
			&& !Constants.EMPTY_STRING.equals(aFollower.getName()) && aFollower.getType() != null
			&& !Constants.EMPTY_STRING.equals(aFollower.getType().toString()))
		{
			thePC.addFollower(aFollower);
		}
	}

	private void parseGameMode(final String line) throws PCGParseException
	{
		final String requestedMode = line.substring(IOConstants.TAG_GAMEMODE.length() + 1);

		final GameMode currentGameMode = SettingsHandler.getGame();
		final String currentMode = currentGameMode.getName();

		if (!requestedMode.equals(currentMode))
		{
			final String msg = LanguageBundle.getFormattedString("Exceptions.PCGenParser.WrongGameMode", //$NON-NLS-1$
				requestedMode, currentMode);
			throw new PCGParseException("ParseGameMode", line, msg); //$NON-NLS-1$
		}
	}

	private void parseGenderLine(final String line)
	{
		String genderString = EntityEncoder.decode(line.substring(IOConstants.TAG_GENDER.length() + 1));
		Gender gender;
		if ("M".equals(genderString))
		{
			gender = Gender.Male;
		}
		else if ("F".equals(genderString))
		{
			gender = Gender.Female;
		}
		else
		{
			try
			{
				gender = Gender.getGenderByName(genderString);
			}
			catch (IllegalArgumentException e)
			{
				gender = Gender.getDefaultValue();
				final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalGender", //$NON-NLS-1$
					line);
				warnings.add(msg);

			}
		}
		thePC.setGender(gender);
	}

	/**
	 * # HTML Output Sheet location
	 * 
	 * @param line
	 */
	private void parseHTMLOutputSheetLine(final String line)
	{
		String aFileName = EntityEncoder.decode(line.substring(IOConstants.TAG_HTMLOUTPUTSHEET.length() + 1));

		if (aFileName.length() <= 0)
		{
			aFileName = SettingsHandler.getSelectedCharacterHTMLOutputSheet(thePC);
		}

		thePC.setSelectedCharacterHTMLOutputSheet(aFileName);
	}

	private void parseHairColorLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.HAIRCOLOR,
			EntityEncoder.decode(line.substring(IOConstants.TAG_HAIRCOLOR.length() + 1)));
	}

	private void parseHairStyleLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.HAIRSTYLE,
			EntityEncoder.decode(line.substring(IOConstants.TAG_HAIRSTYLE.length() + 1)));
	}

	private void parseHandedLine(final String line)
	{
		String handedString = EntityEncoder.decode(line.substring(IOConstants.TAG_HANDED.length() + 1));
		Handed handed;
		try
		{
			handed = HandedCompat.HANDED_MANAGER.convert(handedString);
		}
		catch (IllegalArgumentException e)
		{
			handed = HandedCompat.getDefaultHanded();
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalHandedness", //$NON-NLS-1$
				line, handed);
			warnings.add(msg);

		}
		HandedCompat.setCurrentHandedness(thePC.getCharID(), handed);
	}

	private void parseHeightLine(final String line)
	{
		try
		{
			thePC.setHeight(Integer.parseInt(line.substring(IOConstants.TAG_HEIGHT.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalHeight", //$NON-NLS-1$
				line);
			warnings.add(msg);
		}
	}

	private void parseInterestsLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.INTERESTS,
			EntityEncoder.decode(line.substring(IOConstants.TAG_INTERESTS.length() + 1)));
	}

	private void parseKitLine(final String line)
	{
		final StringTokenizer stok =
				new StringTokenizer(line.substring(IOConstants.TAG_KIT.length() + 1), IOConstants.TAG_SEPARATOR, false);

		if (stok.countTokens() != 2)
		{
			// TODO This if switch currently does nothing?
		}

		/* final String region = */
		stok.nextToken(); //TODO: Is this intended to be thrown away? The value is never used.

		/* final String kit = stok.nextToken(); */

		final Kit aKit = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Kit.class,
			line.substring(IOConstants.TAG_KIT.length() + 1));

		if (aKit == null)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.KitNotFound", //$NON-NLS-1$
				line);
			warnings.add(msg);

			return;
		}

		thePC.addKit(aKit);
	}

	/*
	 * ###############################################################
	 * Character Languages methods
	 * ###############################################################
	 */
	private void parseLanguageLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalLanguage", //$NON-NLS-1$
				line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		for (PCGElement element : tokens.getElements())
		{
			final Language aLang = Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(Language.class, EntityEncoder.decode(element.getText()));
			if (aLang == null)
			{
				final String message = "No longer speaks language: " + element.getText(); //$NON-NLS-1$
				warnings.add(message);
				continue;
			}
			cachedLanguages.add(aLang);
		}
	}

	/**
	 * # Load companions with master?
	 * @param line
	 **/
	private void parseLoadCompanionLine(final String line)
	{
		thePC.setLoadCompanion(line.endsWith(IOConstants.VALUE_Y));
	}

	private void parseLocationLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.LOCATION,
			EntityEncoder.decode(line.substring(IOConstants.TAG_LOCATION.length() + 1)));
	}

	private void parseMasterLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalMaster", //$NON-NLS-1$
				line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final Follower aMaster = new Follower(Constants.EMPTY_STRING, Constants.EMPTY_STRING, null);

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (IOConstants.TAG_MASTER.equals(tag))
			{
				aMaster.setName(EntityEncoder.decode(element.getText()));
			}
			else if (IOConstants.TAG_TYPE.equals(tag))
			{
				String cType = EntityEncoder.decode(element.getText());
				CompanionList cList = Globals.getContext().getReferenceContext()
					.silentlyGetConstructedCDOMObject(CompanionList.class, cType);
				if (cList == null)
				{
					Logging.errorPrint("Cannot find CompanionList: " + cType);
				}
				else
				{
					aMaster.setType(cList);
				}
			}
			else if (IOConstants.TAG_HITDICE.equals(tag))
			{
				try
				{
					aMaster.setUsedHD(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (IOConstants.TAG_FILE.equals(tag))
			{
				String inputFileName = EntityEncoder.decode(element.getText());
				String masterFileName = makeFilenameAbsolute(inputFileName);
				if (masterFileName == null)
				{
					final String msg =
							LanguageBundle.getFormattedString(
								"Warnings.PCGenParser.CantFindMaster", //$NON-NLS-1$
						inputFileName);
					warnings.add(msg);
				}
				else
				{
					aMaster.setFileName(masterFileName);
				}
			}
			else if (IOConstants.TAG_ADJUSTMENT.equals(tag))
			{
				aMaster.setAdjustment(Integer.parseInt(element.getText()));
			}
		}

		if (!Constants.EMPTY_STRING.equals(aMaster.getFileName()) && !Constants.EMPTY_STRING.equals(aMaster.getName())
			&& !Constants.EMPTY_STRING.equals(aMaster.getType().toString()))
		{
			thePC.setMaster(aMaster);
		}
	}

	/**
	 * Convert the passed in file name to an absolute file name. The file name 
	 * may be relative to the PCG file being loaded, to the PCG directory or 
	 * it may be absolute.
	 * @param inFileName The file name to be converted.
	 * @return The absolute file name, or null if the file cannot be found.
	 */
	private String makeFilenameAbsolute(String inFileName)
	{
		// Is it relative to this character file?
		File pcFile = new File(thePC.getFileName());
		File inFile = new File(pcFile.getParentFile(), inFileName);
		if (inFile.exists())
		{
			return inFile.getAbsolutePath();
		}

		// Is it relative to the PCG directory?
		File pcgDir = new File(PCGenSettings.getPcgDir());
		inFile = new File(pcgDir, inFileName);
		if (inFile.exists())
		{
			return inFile.getAbsolutePath();
		}

		// Is it absolute?
		inFile = new File(inFileName);
		if (inFile.exists())
		{
			return inFile.getAbsolutePath();
		}

		// We can't find it!
		return null;
	}

	/*
	 * ###############################################################
	 * Character Notes Tab methods
	 * ###############################################################
	 */
	private void parseNoteLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.IllegalNotes", //$NON-NLS-1$
				line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final NoteItem ni = new NoteItem(-1, -1, Constants.EMPTY_STRING, Constants.EMPTY_STRING);

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (IOConstants.TAG_NOTE.equals(tag))
			{
				ni.setName(EntityEncoder.decode(element.getText()));
			}
			else if (IOConstants.TAG_ID.equals(tag))
			{
				try
				{
					ni.setIdValue(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					ni.setIdValue(-1);

					final String msg =
							LanguageBundle.getFormattedString(
								"Warnings.PCGenParser.InvalidNotes", //$NON-NLS-1$
						line);
					warnings.add(msg);

					break;
				}
			}
			else if (IOConstants.TAG_PARENTID.equals(tag))
			{
				try
				{
					ni.setParentId(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					ni.setIdValue(-1);

					final String msg =
							LanguageBundle.getFormattedString(
								"Warnings.PCGenParser.InvalidNotes", //$NON-NLS-1$
						line);
					warnings.add(msg);

					break;
				}
			}
			else if (IOConstants.TAG_VALUE.equals(tag))
			{
				ni.setValue(EntityEncoder.decode(element.getText()));
			}
		}

		if (ni.getId() > -1)
		{
			thePC.addNotesItem(ni);
		}
	}

	/*
	 * ###############################################################
	 * Character Chronicle Entry methods
	 * ###############################################################
	 */
	private void parseChronicleEntryLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					LanguageBundle.getFormattedString(
						"Warnings.PCGenParser.IllegalChronicleEntry", //$NON-NLS-1$
				line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final ChronicleEntry ce = new ChronicleEntry();

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (IOConstants.TAG_CHRONICLE_ENTRY.equals(tag))
			{
				ce.setOutputEntry("Y".equals(element.getText()));
			}
			else if (IOConstants.TAG_EXPERIENCE.equals(tag))
			{
				try
				{
					ce.setXpField(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					ce.setXpField(0);

					final String msg =
							LanguageBundle.getFormattedString(
								"Warnings.PCGenParser.InvalidChronicleEntry", //$NON-NLS-1$
						line);
					warnings.add(msg);

					break;
				}
			}
			else if (IOConstants.TAG_CAMPAIGN.equals(tag))
			{
				ce.setCampaign(EntityEncoder.decode(element.getText()));
			}
			else if (IOConstants.TAG_ADVENTURE.equals(tag))
			{
				ce.setAdventure(EntityEncoder.decode(element.getText()));
			}
			else if (IOConstants.TAG_PARTY.equals(tag))
			{
				ce.setParty(EntityEncoder.decode(element.getText()));
			}
			else if (IOConstants.TAG_DATE.equals(tag))
			{
				ce.setDate(EntityEncoder.decode(element.getText()));
			}
			else if (IOConstants.TAG_GM.equals(tag))
			{
				ce.setGmField(EntityEncoder.decode(element.getText()));
			}
			else if (IOConstants.TAG_CHRONICLE.equals(tag))
			{
				ce.setChronicle(EntityEncoder.decode(element.getText()));
			}
		}

		thePC.addChronicleEntry(ce);
	}

	/**
	 * Biography fields that are to be hidden from output.
	 * @param line The SUPPRESS_BIO_FIELDS line
	 */
	private void parseSupressBioFieldsLine(final String line)
	{
		String fieldNames = EntityEncoder.decode(line.substring(IOConstants.TAG_SUPPRESS_BIO_FIELDS.length() + 1));
		if (!fieldNames.isEmpty())
		{
			String[] names = fieldNames.split("\\|");
			for (String field : names)
			{
				thePC.setSuppressBioField(BiographyField.valueOf(field), true);
			}
		}
	}

	/**
	 * # PDF Output Sheet location
	 * @param line
	 **/
	private void parsePDFOutputSheetLine(final String line)
	{
		String aFileName = EntityEncoder.decode(line.substring(IOConstants.TAG_PDFOUTPUTSHEET.length() + 1));

		if (aFileName.length() <= 0)
		{
			aFileName = SettingsHandler.getSelectedCharacterPDFOutputSheet(thePC);
		}

		thePC.setSelectedCharacterPDFOutputSheet(aFileName);
	}

	private void parsePersonalityTrait1Line(final String line)
	{
		thePC.setPCAttribute(PCStringKey.PERSONALITY1,
			EntityEncoder.decode(line.substring(IOConstants.TAG_PERSONALITYTRAIT1.length() + 1)));
	}

	private void parsePersonalityTrait2Line(final String line)
	{
		thePC.setPCAttribute(PCStringKey.PERSONALITY2,
			EntityEncoder.decode(line.substring(IOConstants.TAG_PERSONALITYTRAIT2.length() + 1)));
	}

	private void parsePhobiasLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.PHOBIAS,
			EntityEncoder.decode(line.substring(IOConstants.TAG_PHOBIAS.length() + 1)));
	}

	private void parsePlayerNameLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.PLAYERSNAME,
			EntityEncoder.decode(line.substring(IOConstants.TAG_PLAYERNAME.length() + 1)));
	}

	private void parsePoolPointsLine(final String line)
	{
		try
		{
			final int poolPoints = Integer.parseInt(line.substring(IOConstants.TAG_POOLPOINTS.length() + 1));
			thePC.setPoolAmount(poolPoints);
			thePC.setCostPool(poolPoints);
		}
		catch (NumberFormatException nfe)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.InvalidPoolPoints", //$NON-NLS-1$
				line);
			warnings.add(msg);
		}
	}

	private void parsePoolPointsLine2(final String line)
	{
		try
		{
			thePC.setPointBuyPoints(Integer.parseInt(line.substring(IOConstants.TAG_POOLPOINTSAVAIL.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String msg = LanguageBundle.getFormattedString("Warnings.PCGenParser.InvalidPoolPoints", //$NON-NLS-1$
				line);
			warnings.add(msg);
		}
	}

	private void parsePortraitLine(final String line)
	{
		thePC.setPortraitPath(EntityEncoder.decode(line.substring(IOConstants.TAG_PORTRAIT.length() + 1)));
	}

	private void parsePortraitThumbnailRectLine(final String line)
	{
		String[] dim = line.substring(IOConstants.TAG_PORTRAIT_THUMBNAIL_RECT.length() + 1).split(",");
		Rectangle rect = new Rectangle(Integer.parseInt(dim[0]), Integer.parseInt(dim[1]), Integer.parseInt(dim[2]),
			Integer.parseInt(dim[3]));
		thePC.setPortraitThumbnailRect(rect);
	}

	private void parseRaceLine(final String line) throws PCGParseException
	{
		List<PCGElement> elements = new PCGTokenizer(line).getElements();
		PCGElement raceElement = elements.get(0);
		String raceName = EntityEncoder.decode(raceElement.getText());
		// Check for a race key that has been updated.
		raceName = RaceMigration.getNewRaceKey(raceName, pcgenVersion, SettingsHandler.getGame().getName());
		final Race aRace =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Race.class, raceName);

		if (aRace == null)
		{
			final String msg = LanguageBundle.getFormattedString("Exceptions.PCGenParser.RaceNotFound", //$NON-NLS-1$
				raceName);
			throw new PCGParseException("parseRaceLine", line, msg); //$NON-NLS-1$
		}
		String selection = null;
		//Yes, start at 1, 0 was the race
		for (int i = 1; i < elements.size(); i++)
		{
			PCGElement thisElement = elements.get(i);
			final String aString = thisElement.getName();
			if (aString.startsWith(IOConstants.TAG_APPLIEDTO))
			{
				if (selection != null)
				{
					warnings.add("Found multiple selections for Race: " + aRace.getKeyName());
				}
				selection = thisElement.getText();
			}
			else if (!aString.startsWith(IOConstants.TAG_ADDTOKEN))
			{
				final String msg =
						LanguageBundle.getFormattedString("Warnings.PCGenParser.UnknownRaceInfo", //$NON-NLS-1$
							aString + ":" + thisElement.getText());
				warnings.add(msg);
			}
		}
		raceInputFacet.importSelection(thePC.getCharID(), aRace, selection);
		thePC.setDirty(true);

		//Must process ADD after RACE is added to the PC
		for (PCGElement e : new PCGTokenizer(line).getElements())
		{
			String tag = e.getName();
			if (tag.equals(IOConstants.TAG_ADDTOKEN))
			{
				parseAddTokenInfo(e, aRace);
			}
		}

		// TODO
		// adjust for more information according to
		// PCGVer1Creator.appendRaceLine
	}

	private void parseFavoredClassLine(final String line)
	{
		String favClass = EntityEncoder.decode(line.substring(IOConstants.TAG_FAVOREDCLASS.length() + 1));
		PCClass cl =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class, favClass);
		if (cl != null)
		{
			thePC.addFavoredClass(cl, thePC);
		}
	}

	/*
	 * ###############################################################
	 * Character Region methods
	 * ###############################################################
	 */
	private void parseRegionLine(final String line)
	{
		final String r = EntityEncoder.decode(line.substring(IOConstants.TAG_REGION.length() + 1));
		thePC.setRegion(Region.getConstant(r));
	}

	//this method is obsolete, but left in for backward-compatibility, replaced by parseCityLine()
	private void parseResidenceLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.RESIDENCE,
			EntityEncoder.decode(line.substring(IOConstants.TAG_RESIDENCE.length() + 1)));
		thePC.setDirty(true); // trigger a save prompt so that the PCG will be updated
	}

	/*
	 * ###############################################################
	 * Character Skills methods
	 * ###############################################################
	 */
	private void parseSkillLine(final String line)
	{
		final PCGTokenizer tokens;
		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					"Illegal Skill line ignored: " + line + Constants.LINE_SEPARATOR + "Error: " + pcgpex.getMessage();
			warnings.add(message);
			return;
		}
		Skill aSkill = null;
		final Iterator<PCGElement> it = tokens.getElements().iterator();
		// the first element defines the skill key name!!!
		String skillKey = "";
		if (it.hasNext())
		{
			final PCGElement element = it.next();
			skillKey = EntityEncoder.decode(element.getText());
			aSkill = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Skill.class, skillKey);
		}
		while (it.hasNext())
		{
			final PCGElement element = it.next();
			final String tag = element.getName();

			if (IOConstants.TAG_SYNERGY.equals(tag))
			{
				// TODO
				// for now it's ok to ignore it!
			}
			else if (IOConstants.TAG_OUTPUTORDER.equals(tag))
			{
				int outputindex = 0;
				try
				{
					outputindex = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// This is not critical.
					// Maybe warn the user?
				}

				if (aSkill != null)
				{
					thePC.setSkillOrder(aSkill, outputindex);
				}
			}
			else if (IOConstants.TAG_CLASSBOUGHT.equals(tag))
			{
				PCGElement childClass = null;
				PCGElement childRanks = null;
				for (PCGElement child : element.getChildren())
				{
					if (IOConstants.TAG_CLASS.equals(child.getName()))
					{
						childClass = child;
					}
					else if (IOConstants.TAG_RANKS.equals(child.getName()))
					{
						childRanks = child;
					}
				}
				if (childClass == null)
				{
					final String message = "Invalid class/ranks specification: " + line;
					warnings.add(message);

					continue;
				}
				if (childRanks == null)
				{
					final String message = "Invalid class/ranks specification: " + line;
					warnings.add(message);
					continue;
				}
				//None for a class is a special key word.  It is used when a familiar inherits a skill from its master
				PCClass aPCClass = null;
				if (!childClass.getText().equals("None")) //$NON-NLS-1$
				{
					final String childClassKey = EntityEncoder.decode(childClass.getText());
					aPCClass = thePC.getClassKeyed(childClassKey);
					if (aPCClass == null)
					{
						final String message = "Could not find class: " + childClassKey;
						warnings.add(message);
						continue;
					}
				}
				if (aSkill == null)
				{
					// We only need to report this if the skill had ranks.
					final String message = "Could not add skill: " + skillKey;
					warnings.add(message);
					return;
				}
				try
				{
					double ranks = Double.parseDouble(childRanks.getText());
					SkillRankControl.modRanks(ranks, aPCClass, true, thePC, aSkill);
				}
				catch (NumberFormatException nfe)
				{
					final String message = "Invalid ranks specification: " + childRanks.getText();
					warnings.add(message);

					continue;
				}
			}
			else if (aSkill != null && IOConstants.TAG_ASSOCIATEDDATA.equals(tag))
			{
				String key = EntityEncoder.decode(element.getText());
				ChoiceManagerList<Object> controller =
						ChooserUtilities.getConfiguredController(aSkill, thePC, null, new ArrayList<>());
				if (controller != null)
				{
					String[] assoc = key.split(Constants.COMMA, -1);
					for (String string : assoc)
					{
						controller.restoreChoice(thePC, aSkill, string);
					}
				}
				else
				{
					warnings.add("Failed to find choose controller for skill " + aSkill);
				}
			}
			else if (aSkill != null && tag.equals(IOConstants.TAG_LEVELABILITY))
			{
				parseLevelAbilityInfo(element, aSkill);
			}
			else if (aSkill != null && tag.equals(IOConstants.TAG_ADDTOKEN))
			{
				parseAddTokenInfo(element, aSkill);
			}
		}
	}

	/**
	 * # Skills Output order
	 * @param line
	 **/
	private void parseSkillsOutputOrderLine(final String line)
	{
		try
		{
			int orderNum = Integer.parseInt(line.substring(IOConstants.TAG_SKILLSOUTPUTORDER.length() + 1));
			SkillsOutputOrder order = SkillsOutputOrder.values()[orderNum];
			// setting is correct in some  6.1.8-dev characters
			if (compareVersionTo(new int[]{6, 1, 9}) < 0)
			{
				order = SkillsOutputOrder.NAME_ASC;
			}

			thePC.setSkillsOutputOrder(order);
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Skills Output Order line ignored: " + line;
			warnings.add(message);
		}
	}

	/**
	 * # Skills Filter
	 * @param line
	 **/
	private void parseSkillFilterLine(final String line)
	{
		try
		{
			int value = Integer.parseInt(line.substring(IOConstants.TAG_SKILLFILTER.length() + 1));
			thePC.setSkillFilter(SkillFilter.getByValue(value));
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Skill Filter line ignored: " + line;
			warnings.add(message);
		}
	}

	private void parseSkinColorLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.SKINCOLOR,
			EntityEncoder.decode(line.substring(IOConstants.TAG_SKINCOLOR.length() + 1)));
	}

	private void parseSpeechPatternLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.SPEECHTENDENCY,
			EntityEncoder.decode(line.substring(IOConstants.TAG_SPEECHPATTERN.length() + 1)));
	}

	/*
	 * ###############################################################
	 * Spell Book Information methods
	 * ###############################################################
	 */
	/*
	 * #Spell Book Information
	 * SPELLBOOK:bookname|TYPE:spellbooktype
	 */
	private void parseSpellBookLines(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					"Illegal Spell book ignored: " + line + Constants.LINE_SEPARATOR + "Error: " + pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		SpellBook aSpellBook = null;

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (IOConstants.TAG_SPELLBOOK.equals(tag))
			{
				final String bookName = EntityEncoder.decode(element.getText());

				aSpellBook = new SpellBook(bookName, SpellBook.TYPE_PREPARED_LIST);
			}
			else if (IOConstants.TAG_TYPE.equals(tag))
			{
				try
				{
					aSpellBook.setType(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
					final String message = "Spell book " + aSpellBook.getName() + " had an illegal type: "
						+ element.getText() + " in line " + line;
					warnings.add(message);
				}
			}
			else if (IOConstants.TAG_AUTOADDKNOWN.equals(tag))
			{
				if (IOConstants.VALUE_Y.equals(element.getText()))
				{
					thePC.setSpellBookNameToAutoAddKnown(aSpellBook.getName());
				}
			}
		}
		if (aSpellBook == null)
		{
			warnings.add("Internal Error: Did not build Spell Book from SPELLBOOK line");
		}
		else
		{
			thePC.addSpellBook(aSpellBook);
		}
	}

	/*
	 * ###############################################################
	 * Character Spells Information methods
	 * ###############################################################
	 */
	private void parseSpellLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					"Illegal Spell line ignored: " + line + Constants.LINE_SEPARATOR + "Error: " + pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		Spell aSpell = null;
		PCClass aPCClass = null;
		PObject source = null;

		String spellBook = null;

		int times = 1;
		int spellLevel = 0;
		int numPages = 0;

		final List<Ability> metaFeats = new ArrayList<>();

		int ppCost = -1;

		for (final PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (IOConstants.TAG_SPELLNAME.equals(tag))
			{
				String spellName = EntityEncoder.decode(element.getText());
				spellName = SpellMigration.getNewSpellKey(spellName, pcgenVersion, SettingsHandler.getGame().getName());

				// either NULL (no spell) a Spell instance,
				aSpell = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Spell.class,
					spellName);

				if (aSpell == null)
				{
					final String message = "Could not find spell named: " + spellName;
					warnings.add(message);

					return;
				}
			}
			else if (IOConstants.TAG_TIMES.equals(tag))
			{
				try
				{
					times = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (IOConstants.TAG_CLASS.equals(tag))
			{
				final String classKey = EntityEncoder.decode(element.getText());
				aPCClass = thePC.getClassKeyed(classKey);

				if (aPCClass == null)
				{
					final String message = "Invalid class specification: " + classKey;
					warnings.add(message);

					return;
				}
			}
			else if (IOConstants.TAG_SPELL_BOOK.equals(tag))
			{
				spellBook = EntityEncoder.decode(element.getText());
			}
			else if (IOConstants.TAG_SPELLLEVEL.equals(tag))
			{
				try
				{
					spellLevel = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (IOConstants.TAG_SPELLPPCOST.equals(tag))
			{
				try
				{
					ppCost = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (IOConstants.TAG_SPELLNUMPAGES.equals(tag))
			{
				try
				{
					numPages = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (IOConstants.TAG_SOURCE.equals(tag))
			{
				String typeName = Constants.EMPTY_STRING;
				String objectKey = Constants.EMPTY_STRING;

				for (final PCGElement child : element.getChildren())
				{
					final String childTag = child.getName();

					if (IOConstants.TAG_TYPE.equals(childTag))
					{
						typeName = child.getText().toUpperCase();
					}
					else if (IOConstants.TAG_NAME.equals(childTag))
					{
						objectKey = child.getText();
					}
				}

				if (IOConstants.TAG_DOMAIN.equals(typeName))
				{
					Domain domain = Globals.getContext().getReferenceContext()
						.silentlyGetConstructedCDOMObject(DOMAIN_CLASS, objectKey);
					ClassSource cs = thePC.getDomainSource(domain);

					if (cs == null)
					{
						final String message = "Could not find domain: " + objectKey;
						warnings.add(message);

						return;
					}
					source = domain;
				}
				else
				{
					// it's either the class, sub-class or a cast-as class
					// first see if it's the class
					ClassSpellList csl = Globals.getContext().getReferenceContext()
						.silentlyGetConstructedCDOMObject(ClassSpellList.class, objectKey);
					if (((aPCClass != null) && objectKey.equals(aPCClass.getKeyName()))
						|| (aPCClass != null && thePC.getSpellLists(aPCClass).contains(csl)))
					{
						source = aPCClass;
					}
					else
					{
						source = thePC.getClassKeyed(objectKey); // see if PC has the class
					}
				}
			}
			else if (IOConstants.TAG_FEATLIST.equals(tag))
			{
				for (PCGElement child : element.getChildren())
				{
					final String featKey = EntityEncoder.decode(child.getText());
					final Ability anAbility = Globals.getContext().getReferenceContext()
						.getManufacturerId(AbilityCategory.FEAT).getActiveObject(featKey);

					if (anAbility != null)
					{
						metaFeats.add(anAbility);
					}
				}
			}
		}

		if ((aPCClass == null) || (spellBook == null))
		{
			final String message = "Illegal Spell line ignored: " + line;
			warnings.add(message);

			return;
		}

		/*
		 * this can only happen if the source type was NOT DOMAIN!
		 */
		if (source == null)
		{
			source = aPCClass;
		}

		//		if (obj instanceof List)
		//		{
		//			// find the instance of Spell in this class
		//			// best suited to this spell
		//			for (final Spell spell : (ArrayList<Spell>) obj)
		//			{
		//				// valid spell has a non-negative spell level
		//				if ((spell != null)
		//					&& (SpellLevel.getFirstLevelForKey(spell,
		//						thePC.getSpellLists(source), thePC) >= 0))
		//				{
		//					aSpell = spell;
		//					break;
		//				}
		//			}
		//			if (aSpell == null)
		//			{
		//				Logging.errorPrint("Could not resolve spell " + obj.toString());
		//			}
		//		}

		//		if (aSpell == null)
		//		{
		//			final String message =
		//					"Could not find spell named: " + String.valueOf(obj);
		//			warnings.add(message);
		//
		//			return;
		//		}

		// just to make sure the spellbook is present
		thePC.addSpellBook(spellBook);
		final SpellBook book = thePC.getSpellBookByName(spellBook);

		thePC.calculateKnownSpellsForClassLevel(aPCClass);
		final Integer[] spellLevels = SpellLevel.levelForKey(aSpell, thePC.getSpellLists(source), thePC);
		boolean found = false;

		for (final Integer level : spellLevels)
		{
			final int metmagicLevels = totalAddedLevelsFromMetamagic(metaFeats);

			if (spellLevel > 0 && spellLevel != (level + metmagicLevels))
			{
				// Skip spell in class lists that does not match level the character
				// knows it.
				continue;
			}

			if (level < 0)
			{
				Collection<CDOMReference<Spell>> mods = source.getListMods(Spell.SPELLS);
				if (mods == null)
				{
					continue;
				}
				for (CDOMReference<Spell> ref : mods)
				{
					Collection<Spell> refSpells = ref.getContainedObjects();
					Collection<AssociatedPrereqObject> assocs = source.getListAssociations(Spell.SPELLS, ref);
					for (Spell sp : refSpells)
					{
						if (aSpell.getKeyName().equals(sp.getKeyName()))
						{
							for (AssociatedPrereqObject apo : assocs)
							{
								String sb = apo.getAssociation(AssociationKey.SPELLBOOK);
								if (spellBook.equals(sb))
								{
									found = true;
									break;
								}
							}
						}
					}
				}
				continue;
			}

			found = true;

			// do not load auto knownspells into default spellbook
			if (spellBook.equals(Globals.getDefaultSpellBook())
				&& thePC.getSpellSupport(aPCClass).isAutoKnownSpell(aSpell, level, false, thePC)
				&& thePC.getAutoSpells())
			{
				continue;
			}

			CharacterSpell aCharacterSpell = thePC.getCharacterSpellForSpell(aPCClass, aSpell, source);

			// PC does not have that spell on that classes list
			// so we'll need to add it to the list
			if (aCharacterSpell == null)
			{
				aCharacterSpell = new CharacterSpell(source, aSpell);
				aCharacterSpell.addInfo(level, times, spellBook);
				thePC.addCharacterSpell(aPCClass, aCharacterSpell);
			}

			SpellInfo aSpellInfo = null;

			if (source.getKeyName().equals(aPCClass.getKeyName()) || !spellBook.equals(Globals.getDefaultSpellBook()))
			{
				aSpellInfo = aCharacterSpell.getSpellInfoFor(spellBook, spellLevel);

				// This doesn't make sense. What does the
				// metaFeats list have to do with this?
				if ((aSpellInfo == null) || !metaFeats.isEmpty())
				{
					aSpellInfo = aCharacterSpell.addInfo(spellLevel, times, spellBook);
				}
			}

			if (aSpellInfo != null)
			{
				if (!metaFeats.isEmpty())
				{
					aSpellInfo.addFeatsToList(metaFeats);
				}
				aSpellInfo.setActualPPCost(ppCost);
				aSpellInfo.setNumPages(numPages);
				book.setNumPagesUsed(book.getNumPagesUsed() + numPages);
				book.setNumSpells(book.getNumSpells() + 1);
			}
		}
		// end sindex for loop

		if (!found)
		{
			final String message = "Could not find spell " + aSpell.getDisplayName() + " in " + shortClassName(source)
				+ " " + source.getDisplayName();
			warnings.add(message);
		}
	}

	private int totalAddedLevelsFromMetamagic(List<Ability> metaFeats)
	{
		int addedLevels = 0;

		for (Ability ability : metaFeats)
		{
			Integer featAddSpellLevel = ability.get(IntegerKey.ADD_SPELL_LEVEL);
			if (featAddSpellLevel != null)
			{
				addedLevels += featAddSpellLevel;
			}
		}
		return addedLevels;
	}

	/*
	 * ###############################################################
	 * Spell List Information methods
	 * ###############################################################
	 */
	/*
	 * #Spell List Information
	 * SPELLLIST:sourceclassname|spelllistentry|spelllistentry
	 */
	private void parseSpellListLines(final String line)
	{
		final String subLine = line.substring(IOConstants.TAG_SPELLLIST.length() + 1);
		final StringTokenizer stok = new StringTokenizer(subLine, IOConstants.TAG_SEPARATOR, false);

		final String classKey = stok.nextToken();
		final PCClass aClass = thePC.getClassKeyed(classKey);

		AbstractReferenceContext refContext = Globals.getContext().getReferenceContext();
		while ((aClass != null) && stok.hasMoreTokens())
		{
			final String tok = stok.nextToken();
			if (tok.startsWith("CLASS."))
			{
				ClassSpellList csl =
						refContext.silentlyGetConstructedCDOMObject(ClassSpellList.class, tok.substring(6));
				thePC.addClassSpellList(csl, aClass);
			}
			else if (tok.startsWith("DOMAIN."))
			{
				DomainSpellList dsl =
						refContext.silentlyGetConstructedCDOMObject(DomainSpellList.class, tok.substring(7));
				thePC.addClassSpellList(dsl, aClass);
			}
			else
			{
				/*
				 * This is 5.14-ish, but have to try anyway:
				 */
				ClassSpellList csl = refContext.silentlyGetConstructedCDOMObject(ClassSpellList.class, tok);
				if (csl == null)
				{
					DomainSpellList dsl = refContext.silentlyGetConstructedCDOMObject(DomainSpellList.class, tok);
					if (dsl != null)
					{
						thePC.addClassSpellList(dsl, aClass);
					}
				}
				else
				{
					thePC.addClassSpellList(csl, aClass);
				}
			}
		}
	}

	/*
	 * ###############################################################
	 * Character Attributes methods
	 * ###############################################################
	 */
	private void parseStatLine(final String line) throws PCGParseException
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Ability scores are critical for characters,
			 * need to stop the load process
			 *
			 * Thomas Behr 09-09-02
			 */
			throw new PCGParseException("parseStatLine", line, pcgpex.getMessage(), pcgpex); //$NON-NLS-1$
		}

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		if (it.hasNext())
		{
			PCGElement element = it.next();
			final String statName = element.getText();
			PCStat stat =
					Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCStat.class, statName);

			if ((stat != null) && seenStats.add(statName.toUpperCase()) && (it.hasNext()))
			{
				element = it.next();

				try
				{
					thePC.setStat(stat, Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					throw new PCGParseException("parseStatLine", line, nfe.getMessage(), nfe); //$NON-NLS-1$
				}
			}
			else
			{
				final String message = "Invalid attribute specification. " + "Cannot load character.";
				throw new PCGParseException("parseStatLine", line, message, null); //$NON-NLS-1$
			}
		}
		else
		{
			final String message = "Invalid attribute specification. " + "Cannot load character.";
			throw new PCGParseException("parseStatLine", line, message, null); //$NON-NLS-1$
		}
	}

	private void parseTabNameLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.TABNAME,
			EntityEncoder.decode(line.substring(IOConstants.TAG_TABNAME.length() + 1)));
	}

	/*
	 * ###############################################################
	 * Character Templates methods
	 * ###############################################################
	 */
	private void parseTemplateLine(final String line) throws PCGParseException
	{
		if (line.charAt(IOConstants.TAG_TEMPLATESAPPLIED.length() + 1) == '[')
		{
			final PCGTokenizer tokens;

			try
			{
				tokens = new PCGTokenizer(line);
			}
			catch (PCGParseException pcgpex)
			{
				final String message = "Illegal Template line ignored: " + line + Constants.LINE_SEPARATOR + "Error: "
					+ pcgpex.getMessage();
				warnings.add(message);

				return;
			}

			PCTemplate aPCTemplate = null;

			Iterator<PCGElement> it = tokens.getElements().iterator();

			if (it.hasNext())
			{
				final PCGElement element = it.next();

				String assoc = null;
				//Must deal with APPLIEDTO first (before item is added to the PC)
				for (final PCGElement child : element.getChildren())
				{
					String childTag = child.getName();
					if (IOConstants.TAG_NAME.equals(childTag))
					{
						aPCTemplate = Globals.getContext().getReferenceContext()
							.silentlyGetConstructedCDOMObject(PCTemplate.class, EntityEncoder.decode(child.getText()));

						if (aPCTemplate == null)
						{
							break;
						}
					}
					else if (IOConstants.TAG_APPLIEDTO.equals(childTag))
					{
						assoc = child.getText();
					}
				}

				for (final PCGElement child : element.getChildren())
				{
					final String childTag = child.getName();

					if (IOConstants.TAG_NAME.equals(childTag))
					{
						if (aPCTemplate == null)
						{
							break;
						}
						addKeyedTemplate(aPCTemplate, assoc);
					}
					else if (IOConstants.TAG_CHOSENFEAT.equals(childTag))
					{
						String mapKey = null;
						String mapValue = null;

						for (PCGElement subChild : child.getChildren())
						{
							final String subChildTag = subChild.getName();

							if (IOConstants.TAG_MAPKEY.equals(subChildTag))
							{
								mapKey = subChild.getText();
							}
							else if (IOConstants.TAG_MAPVALUE.equals(subChildTag))
							{
								mapValue = subChild.getText();
							}
						}

						if ((mapKey != null) && (mapValue != null))
						{
							String feat = EntityEncoder.decode(mapValue);
							PCTemplate subt =
									Compatibility.getTemplateFor(aPCTemplate, EntityEncoder.decode(mapKey), feat);
							if (subt != null)
							{
								CNAbilitySelection as = CNAbilitySelection
									.getAbilitySelectionFromPersistentFormat(Globals.getContext(), feat);
								thePC.addTemplateFeat(subt, as);
							}
						}
					}
					else if (IOConstants.TAG_CHOSENTEMPLATE.equals(childTag))
					{
						for (PCGElement subChild : child.getChildren())
						{
							final String subChildTag = subChild.getName();

							if (IOConstants.TAG_NAME.equals(subChildTag))
							{
								final String ownedTemplateKey = EntityEncoder.decode(subChild.getText());
								final PCTemplate ownedTemplate = Globals.getContext().getReferenceContext()
									.silentlyGetConstructedCDOMObject(PCTemplate.class, ownedTemplateKey);
								if (ownedTemplate != null)
								{
									thePC.setTemplatesAdded(aPCTemplate, ownedTemplate);
								}
							}
						}
					}
					//Add handled below, AppliedTo handled in the first loop
					else if (!IOConstants.TAG_ADDTOKEN.equals(childTag) && !IOConstants.TAG_APPLIEDTO.equals(childTag))
					{
						final String msg =
								LanguageBundle.getFormattedString(
									"Warnings.PCGenParser.UnknownTemplateInfo", //$NON-NLS-1$
							childTag + ":" + child.getText());
						warnings.add(msg);
					}
				}
			}
			//Must process ADD after Template is added to the PC
			for (PCGElement e : new PCGTokenizer(line).getElements())
			{
				String tag = e.getName();
				if (tag.equals(IOConstants.TAG_ADDTOKEN))
				{
					parseAddTokenInfo(e, aPCTemplate);
				}
			}
		}
		else
		{
			String key = EntityEncoder.decode(line.substring(IOConstants.TAG_TEMPLATESAPPLIED.length() + 1));
			PCTemplate aPCTemplate =
					Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCTemplate.class, key);
			addKeyedTemplate(aPCTemplate, null);
		}
	}

	/**
	 * # Use temporary mods/bonuses?
	 * 
	 * @param line
	 */
	private void parseUseTempModsLine(final String line)
	{
		thePC.setUseTempMods(line.endsWith(IOConstants.VALUE_Y));
	}

	private void parseVFeatLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					"Illegal VFeat line ignored: " + line + Constants.LINE_SEPARATOR + "Error: " + pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		// the first element defines the Feat key name
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			final String abilityKey = EntityEncoder.decode(element.getText());
			Ability anAbility = Globals.getContext().getReferenceContext().getManufacturerId(AbilityCategory.FEAT)
				.getActiveObject(abilityKey);

			if (anAbility == null)
			{
				final String message = "Could not add vfeat: " + abilityKey;
				warnings.add(message);

				return;
			}
			CNAbility cna = CNAbilityFactory.getCNAbility(AbilityCategory.FEAT, Nature.VIRTUAL, anAbility);
			parseFeatsHandleAppliedToAndSaveTags(it, cna);
			thePC.setDirty(true);
		}
	}

	/**
	 * Parses the version information from a PCG file.
	 * @param line The line containing version information
	 * @throws PCGParseException if the line is not a valid version line
	 */
	void parseVersionLine(final String line) throws PCGParseException
	{
		int[] version = {0, 0, 0};

		// Check to make sure that this is a version line
		if (!line.startsWith(IOConstants.TAG_VERSION + IOConstants.TAG_END))
		{
			throw new PCGParseException("parseVersionLine", line, "Not a Version Line."); //$NON-NLS-1$
		}

		// extract the tokens from the version line
		String[] tokens = line.substring(IOConstants.TAG_VERSION.length() + 1).split("[ \\.-]", 4); //$NON-NLS-1$

		for (int idx = 0; idx < 3 && idx < tokens.length; idx++)
		{

			try
			{
				version[idx] = Integer.parseInt(tokens[idx]);
			}
			catch (NumberFormatException e)
			{
				if (idx == 2 && (tokens[idx].startsWith("RC")))
				{
					pcgenVersionSuffix = tokens[2];
				}
				else
				{
					// Something in the first 3 digits was not an integer
					throw new PCGParseException("parseVersionLine", line, "Invalid PCGen version.", e); //$NON-NLS-1$
				}
			}
		}
		if (tokens.length == 4)
		{
			pcgenVersionSuffix = tokens[3];
		}
		pcgenVersion = version;
	}

	/*
	 * ###############################################################
	 * Character Weapon proficiencies methods
	 * ###############################################################
	 */
	private void parseWeaponProficienciesLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message = "Illegal Weapon proficiencies line ignored: " + line + Constants.LINE_SEPARATOR
				+ "Error: " + pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		CDOMObject source = null;
		boolean hadSource = false;

		for (PCGElement element : tokens.getElements())
		{
			if (IOConstants.TAG_SOURCE.equals(element.getName()))
			{
				hadSource = true;
				String type = Constants.EMPTY_STRING;
				String key = Constants.EMPTY_STRING;

				for (final PCGElement child : element.getChildren())
				{
					final String tag = child.getName();

					if (IOConstants.TAG_TYPE.equals(tag))
					{
						type = child.getText().toUpperCase();
					}
					else if (IOConstants.TAG_NAME.equals(tag))
					{
						key = child.getText();
					}
				}

				if (Constants.EMPTY_STRING.equals(type) || Constants.EMPTY_STRING.equals(key))
				{
					final String message = "Illegal Weapon proficiencies line ignored: " + line;
					warnings.add(message);

					return;
				}

                switch (type)
                {
                    case IOConstants.TAG_RACE:
                        source = thePC.getRace();
                        break;
                    case TAG_PCTEMPLATE:
                        PCTemplate template = Globals.getContext().getReferenceContext()
                                .silentlyGetConstructedCDOMObject(PCTemplate.class, key);
                        if (thePC.hasTemplate(template))
                        {
                            source = template;
                        } else
                        {
                            warnings.add("PC does not have Template: " + key);
                        }
                        break;
                    case IOConstants.TAG_PCCLASS:
                        source = thePC.getClassKeyed(key);
                        break;
                }

				if (source == null)
				{
					final String message = "Invalid source specification: " + line;
					warnings.add(message);
				}

				break;
			}
		}

		final PCGElement element = tokens.getElements().get(0);

		boolean processed = false;
		if (source != null)
		{
			List<PersistentTransitionChoice<?>> adds = source.getListFor(ListKey.ADD);
			if (adds != null)
			{
				for (PersistentTransitionChoice<?> ptc : adds)
				{
					if (ptc.getChoiceClass().equals(WeaponProf.class))
					{
						for (PCGElement child : element.getChildren())
						{
							WeaponProf wp = getWeaponProf(child.getText());
							Set c = Collections.singleton(wp);
							ptc.act(c, source, thePC);
						}
						processed = true;
						break;
					}
				}
			}
		}
		if (hadSource && !processed)
		{
			final String message = "Unable to apply WeaponProfs: " + line;
			warnings.add(message);
		}
	}

	private void parseWeightLine(final String line)
	{
		try
		{
			thePC.setPCAttribute(NumericPCAttribute.WEIGHT,
				Integer.parseInt(line.substring(IOConstants.TAG_WEIGHT.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Weight line ignored: " + line;
			warnings.add(message);
		}
	}

	private static String shortClassName(final Object o)
	{
		final Class<?> objClass = o.getClass();
		final String pckName = objClass.getPackage().getName();

		return objClass.getName().substring(pckName.length() + 1);
	}

	private WeaponProf getWeaponProf(final String aString)
	{
		WeaponProf wp = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(WeaponProf.class,
			EntityEncoder.decode(aString));
		if (wp == null)
		{
			if (Logging.isDebugMode())
			{
				final String message = "Unable to find Weapon Proficiency in Rules Data:" + aString;
				Logging.debugPrint(message);
			}
		}
		return wp;
	}

	/**
	 * ###############################################################
	 * Character EquipSet Stuff
	 * ###############################################################
	 * @param line
	 **/
	private void parseCalcEquipSet(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * EquipSet is not critical for characters,
			 * no need to stop the load process
			 */
			final String message = "Illegal Calc EquipSet line ignored: " + line + Constants.LINE_SEPARATOR + "Error: "
				+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		final String calcEQId = EntityEncoder.decode(tokens.getElements().get(0).getText());

		thePC.setCalcEquipSetId(calcEQId);
	}

	/*
	 * ###############################################################
	 * Character Description/Bio/History methods
	 * ###############################################################
	 */
	private void parseCharacterBioLine(final String line)
	{
		thePC.setPCAttribute(PCStringKey.BIO,
			EntityEncoder.decode(line.substring(IOConstants.TAG_CHARACTERBIO.length() + 1)));
	}

	private void parseEquipmentLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message = "Illegal Equipment line ignored: " + line + Constants.LINE_SEPARATOR + "Error: "
				+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		String itemKey;
		Equipment aEquip;

		PCGElement element;

		// the first element defines the item key name
		element = tokens.getElements().get(0);
		itemKey = EntityEncoder.decode(element.getText());
		// Check for an equipment key that has been updated.
		itemKey = EquipmentMigration.getNewEquipmentKey(itemKey, pcgenVersion, SettingsHandler.getGame().getName());

		// might be dynamically created container
		aEquip = thePC.getEquipmentNamed(itemKey);

		if (aEquip == null)
		{
			// Must load custom equipment from the .pcg file
			// before we check the Global list (which may get
			// loaded from customeEquipment.lst) as equipment
			// in the PC's .pcg may contain additional info
			// such as Charges on a wand, etc
			//
			// Make sure that we are not picking up custom items!
			aEquip = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Equipment.class,
				itemKey);
			if (aEquip != null)
			{
				if (aEquip.isType(Constants.TYPE_CUSTOM))
				{
					aEquip = null;
				}
				else
				{
					// standard item
					aEquip = aEquip.clone();
				}
			}
			if (line.contains(IOConstants.TAG_CUSTOMIZATION))
			{
				// might be customized item
				for (PCGElement pcgElement : tokens.getElements())
				{
					element = pcgElement;

					if (IOConstants.TAG_CUSTOMIZATION.equals(element.getName()))
					{
						String baseItemKey = Constants.EMPTY_STRING;
						String customProperties = Constants.EMPTY_STRING;

						for (PCGElement child : element.getChildren())
						{
							final String childTag = child.getName();

							if (IOConstants.TAG_BASEITEM.equals(childTag))
							{
								baseItemKey = EntityEncoder.decode(child.getText());
								// Check for an equipment key that has been updated.
								baseItemKey = EquipmentMigration.getNewEquipmentKey(baseItemKey, pcgenVersion,
									SettingsHandler.getGame().getName());
							}
							else if (IOConstants.TAG_DATA.equals(childTag))
							{
								customProperties = EntityEncoder.decode(child.getText());
							}
						}

						if (aEquip != null && baseItemKey.equals(aEquip.getBaseItemName()))
						{
							// We clear out any eqmods that the base item has as the
							// EQMODs on the saved item override them.
							EquipmentHead head = aEquip.getEquipmentHeadReference(1);
							if (head != null)
							{
								head.removeListFor(ListKey.EQMOD);
								head.removeListFor(ListKey.EQMOD_INFO);
							}
							aEquip.setBase();
							aEquip.load(customProperties, "$", "=", thePC);
							aEquip.setToCustomSize(thePC);
						}
						else
						{
							// Make sure that we are not picking up custom items!
							Equipment aEquip2 = Globals.getContext().getReferenceContext()
								.silentlyGetConstructedCDOMObject(Equipment.class, baseItemKey);
							if (aEquip2 != null)
							{
								// Make sure we are not getting a custom item
								if (aEquip2.isType(Constants.TYPE_CUSTOM))
								{
                                }
								else
								{
									// standard item
									aEquip = aEquip2.clone();
									// We clear out any eqmods that the base item has as
									// the
									// EQMODs on the saved item override them.
									EquipmentHead head = aEquip.getEquipmentHeadReference(1);
									if (head != null)
									{
										head.removeListFor(ListKey.EQMOD);
										head.removeListFor(ListKey.EQMOD_INFO);
									}
									aEquip.setBase();
									aEquip.load(customProperties, "$", "=", thePC);
									aEquip.setToCustomSize(thePC);
									aEquip.remove(StringKey.OUTPUT_NAME);
									if (!aEquip.isType(Constants.TYPE_CUSTOM))
									{
										aEquip.addType(Type.CUSTOM);
									}
									Globals.getContext().getReferenceContext().importObject(aEquip.clone());
								}
							}
						}

						break;
					}
				}

			}

			if (aEquip == null)
			{
				final String msg =
						LanguageBundle.getFormattedString("Warnings.PCGenParser.EquipmentNotFound", //$NON-NLS-1$
					itemKey);
				warnings.add(msg);

				return;
			}

			thePC.addEquipment(aEquip);
		}

		for (PCGElement pcgElement : tokens.getElements())
		{
			element = pcgElement;
			String tag = element.getName();

			if (IOConstants.TAG_QUANTITY.equals(tag))
			{
				float oldQty = aEquip.getQty();
				aEquip.setQty(element.getText());
				thePC.updateEquipmentQty(aEquip, oldQty, aEquip.getQty());
			}
			else if (IOConstants.TAG_OUTPUTORDER.equals(tag))
			{
				int index = 0;

				try
				{
					index = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can or have to do about this
				}

				aEquip.setOutputIndex(index);
				if (aEquip.isAutomatic())
				{
					thePC.cacheOutputIndex(aEquip);
				}
			}
			else if (IOConstants.TAG_COST.equals(tag))
			{
				// TODO This else if switch currently does nothing?
			}
			else if (IOConstants.TAG_WT.equals(tag))
			{
				// TODO This else if switch currently does nothing?
			}
			else if (IOConstants.TAG_NOTE.equals(tag))
			{
				aEquip.setNote(element.getText());
			}
		}
	}

	private void parseEquipmentSetLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message = "Illegal EquipSet line ignored: " + line + Constants.LINE_SEPARATOR + "Error: "
				+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		String setName = null;
		String setID = null;
		String itemKey = null;
		String setNote = null;
		Float itemQuantity = null;
		boolean useTempMods = false;

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (IOConstants.TAG_EQUIPSET.equals(tag))
			{
				setName = EntityEncoder.decode(element.getText());
			}
			else if (IOConstants.TAG_ID.equals(tag))
			{
				setID = element.getText();
			}
			else if (IOConstants.TAG_VALUE.equals(tag))
			{
				itemKey = EntityEncoder.decode(element.getText());
			}
			else if (IOConstants.TAG_QUANTITY.equals(tag))
			{
				try
				{
					itemQuantity = Float.valueOf(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					itemQuantity = 0.0f;
				}
			}
			else if (IOConstants.TAG_NOTE.equals(tag))
			{
				setNote = EntityEncoder.decode(element.getText());
			}
			else if (IOConstants.TAG_USETEMPMODS.equals(tag))
			{
				useTempMods = element.getText().endsWith(IOConstants.VALUE_Y);
			}
		}

		if ((setName == null) || Constants.EMPTY_STRING.equals(setName) || (setID == null)
			|| Constants.EMPTY_STRING.equals(setID))
		{
			final String message = "Illegal EquipSet line ignored: " + line;
			warnings.add(message);

			return;
		}

		final EquipSet aEquipSet;

		aEquipSet = new EquipSet(setID, setName);

		if (setNote != null)
		{
			aEquipSet.setNote(setNote);
		}

		if (itemKey != null)
		{
			aEquipSet.setValue(itemKey);
			Equipment eqI = thePC.getEquipmentNamed(itemKey);

			if (eqI == null)
			{
				eqI = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Equipment.class,
					itemKey);
			}

			if (eqI == null)
			{
				final String message = "Could not find equipment: " + itemKey;
				warnings.add(message);

				return;
			}
			Equipment aEquip = eqI.clone();

			if (itemQuantity != null)
			{
				aEquipSet.setQty(itemQuantity);
				aEquip.setQty(itemQuantity);
				aEquip.setNumberCarried(itemQuantity);
			}

			// if the idPath is longer than 3
			// it's inside a container
			if ((new StringTokenizer(setID, ".")).countTokens() > 3) //$NON-NLS-1$
			{
				// get parent EquipSet
				final EquipSet aEquipSet2 = thePC.getEquipSetByIdPath(aEquipSet.getParentIdPath());

				// get the container
				Equipment aEquip2 = null;

				if (aEquipSet2 != null)
				{
					aEquip2 = aEquipSet2.getItem();
				}

				// add the child to container
				if (aEquip2 != null)
				{
					aEquip2.insertChild(thePC, aEquip);
					aEquip.setParent(aEquip2);
				}
			}

			aEquipSet.setItem(aEquip);
		}

		aEquipSet.setUseTempMods(useTempMods);

		thePC.addEquipSet(aEquipSet);
	}

	/*
	 * ###############################################################
	 * Character Equipment methods
	 * ###############################################################
	 */
	private void parseMoneyLine(final String line)
	{
		thePC.setGold(line.substring(IOConstants.TAG_MONEY.length() + 1));
	}

	/**
	 * ###############################################################
	 * Temporary Bonuses
	 * ###############################################################
	 * @param line
	 **/
	private void parseTempBonusLine(final String line)
	{
		PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message = "Illegal TempBonus line ignored: " + line + Constants.LINE_SEPARATOR + "Error: "
				+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		String cTag = null;
		String tName = null;
		boolean active = true;

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (IOConstants.TAG_TEMPBONUS.equals(tag))
			{
				cTag = EntityEncoder.decode(element.getText());
			}
			else if (IOConstants.TAG_TEMPBONUSTARGET.equals(tag))
			{
				tName = EntityEncoder.decode(element.getText());
			}
			else if (IOConstants.TAG_TEMPBONUSACTIVE.equals(tag))
			{
				active = element.getText().endsWith(IOConstants.VALUE_Y);
			}
		}

		if ((cTag == null) || (tName == null))
		{
			warnings.add("Illegal TempBonus line ignored: " + line);

			return;
		}

		final StringTokenizer aTok = new StringTokenizer(cTag, "=", false); //$NON-NLS-1$

		if (aTok.countTokens() < 2)
		{
			return;
		}

		final String cType = aTok.nextToken();
		final String cKey = aTok.nextToken();

		Equipment aEq = null;

		if (!tName.equals(IOConstants.TAG_PC))
		{
			// bonus is applied to an equipment item
			// so create a new one and add to PC
			final Equipment eq = thePC.getEquipmentNamed(tName);

			if (eq == null)
			{
				return;
			}

			aEq = eq.clone();

			//aEq.setWeight("0");
			aEq.resetTempBonusList();
		}

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			final String bonus;
			if (IOConstants.TAG_TEMPBONUSBONUS.equals(tag))
			{
				bonus = EntityEncoder.decode(element.getText());
			}
			else
			{
				continue;
			}

			if (bonus.length() <= 0)
			{
				continue;
			}

			BonusObj newB = null;
			Object creator = null;
			LoadContext context = Globals.getContext();
			// Check the Creator type so we know what
			// type of object to set as the creator
            switch (cType)
            {
                case IOConstants.TAG_FEAT:
                    for (AbilityCategory aCat : SettingsHandler.getGame().getAllAbilityCategories())
                    {
                        Ability a =
                                Globals.getContext().getReferenceContext().getManufacturerId(aCat).getActiveObject(cKey);
                        if (a != null)
                        {
                            newB = Bonus.newBonus(context, bonus);
                            creator = a;
                            break;
                        }
                    }
                    break;
                case IOConstants.TAG_EQUIPMENT:
                    Equipment aEquip = thePC.getEquipmentNamed(cKey);

                    if (aEquip == null)
                    {
                        aEquip = context.getReferenceContext().silentlyGetConstructedCDOMObject(Equipment.class, cKey);
                    }

                    if (aEquip != null)
                    {
                        newB = Bonus.newBonus(context, bonus);
                        creator = aEquip;
                    }
                    break;
                case IOConstants.TAG_CLASS:
                    final PCClass aClass = thePC.getClassKeyed(cKey);

                    if (aClass == null)
                    {
                        continue;
                    }

                    int idx = bonus.indexOf('|');
                    newB = Bonus.newBonus(context, bonus.substring(idx + 1));
                    creator = aClass;
                    break;
                case IOConstants.TAG_TEMPLATE:
                    PCTemplate aTemplate =
                            context.getReferenceContext().silentlyGetConstructedCDOMObject(PCTemplate.class, cKey);

                    if (aTemplate != null)
                    {
                        newB = Bonus.newBonus(context, bonus);
                        creator = aTemplate;
                    }
                    break;
                case IOConstants.TAG_SKILL:
                    Skill aSkill = context.getReferenceContext().silentlyGetConstructedCDOMObject(Skill.class, cKey);

                    if (aSkill != null)
                    {
                        newB = Bonus.newBonus(context, bonus);
                        creator = aSkill;
                    }
                    break;
                case IOConstants.TAG_SPELL:
                    final Spell aSpell = context.getReferenceContext().silentlyGetConstructedCDOMObject(Spell.class, cKey);

                    if (aSpell != null)
                    {
                        newB = Bonus.newBonus(context, bonus);
                        creator = aSpell;
                    }
                    break;
                case IOConstants.TAG_NAME:
                    newB = Bonus.newBonus(context, bonus);
                    //newB.setCreatorObject(thePC);
                    break;
            }

			if (newB == null)
			{
				return;
			}

			TempBonusInfo tempBonusInfo;
			// Check to see if the target was the PC or an Item
			if (tName.equals(IOConstants.TAG_PC))
			{
				thePC.setApplied(newB, true);
				tempBonusInfo = thePC.addTempBonus(newB, creator, thePC);
			}
			else
			{
				thePC.setApplied(newB, true);
				aEq.addTempBonus(newB);
				tempBonusInfo = thePC.addTempBonus(newB, creator, aEq);
			}

			if (!active)
			{
				String bonusName = BonusDisplay.getBonusDisplayName(tempBonusInfo);
				thePC.setTempBonusFilter(bonusName);
			}
		}

		if (aEq != null)
		{
			aEq.setAppliedName(cKey);
			thePC.addTempBonusItemList(aEq);
		}

	}

	/*
	 * currently source is either empty or
	 * PCCLASS|classname|classlevel (means it's a chosen special ability)
	 * PCCLASS=classname|classlevel (means it's a defined special ability)
	 * DEITY=deityname|totallevels
	 */
	private static String sourceElementToString(final PCGElement source)
	{
		String type = Constants.EMPTY_STRING;
		String name = Constants.EMPTY_STRING;
		String level = Constants.EMPTY_STRING;
		String defined = Constants.EMPTY_STRING;

		for (PCGElement child : source.getChildren())
		{
			final String tag = child.getName();

			if (IOConstants.TAG_TYPE.equals(tag))
			{
				type = child.getText();
			}
			else if (IOConstants.TAG_NAME.equals(tag))
			{
				name = child.getText();
			}
			else if (IOConstants.TAG_LEVEL.equals(tag))
			{
				level = child.getText();
			}
			else if (IOConstants.TAG_DEFINED.equals(tag))
			{
				defined = child.getText().toUpperCase();
			}
		}

		//TODO:gorm - guestimate good starting buffer size
		final StringBuilder buffer = new StringBuilder(1000);
		buffer.append(type);
		buffer.append((IOConstants.VALUE_Y.equals(defined)) ? '=' : '|');
		buffer.append(name);

		if (!Constants.EMPTY_STRING.equals(level))
		{
			buffer.append('|');
			buffer.append(level);
		}

		return buffer.toString();
	}

	/*
	 * ###############################################################
	 * Inner classes
	 * ###############################################################
	 */
	private static final class PCGElement
	{
		private final String name;
		private List<PCGElement> children;
		private String text;

		private PCGElement(final String aName)
		{
			this.name = aName;
		}

		/**
		 * Returns a string representation of the element.  This string is
		 * written in XML format.
		 * @return An XML formatted string.
		 */
		@Override
		public String toString()
		{
			//TODO:gorm - optimize StringBuilder size
			final StringBuilder buffer = new StringBuilder(1000);
			buffer.append('<').append(getName()).append('>').append(IOConstants.LINE_SEP);
			buffer.append("<text>").append(getText()) //$NON-NLS-1$
				.append("</text>").append(IOConstants.LINE_SEP); //$NON-NLS-1$

			for (PCGElement child : getChildren())
			{
				buffer.append(child).append(IOConstants.LINE_SEP);
			}

			buffer.append("</").append(getName()).append('>'); //$NON-NLS-1$

			return buffer.toString();
		}

		/**
		 * Returns all the children of this element.
		 * <p><b>Note</b>: This has a side effect of initializing the children
		 * list for the element.
		 * @return A {@code List} of children
		 */
		public List<PCGElement> getChildren()
		{
			if (children == null)
			{
				this.children = new ArrayList<>(0);
			}

			return children;
		}

		private String getName()
		{
			return name;
		}

		private String getText()
		{
			return (text != null) ? text : Constants.EMPTY_STRING;
		}

		private void addContent(final PCGElement child)
		{
			if (children == null)
			{
				this.children = new ArrayList<>(0);
			}

			children.add(child);
		}

		private void addContent(final String argText)
		{
			text = argText;
		}
	}

	private static final class PCGTokenizer
	{
		private final List<PCGElement> elements;
		private final String innerDelimiter;
		private final String nestedStartDelimiter;
		private final String nestedStopDelimiter;
		private final String outerDelimiter;
		private final char nestedStartDelimiterChar;
		private final char nestedStopDelimiterChar;

		/**
		 * Constructor
		 * @param line
		 * @throws PCGParseException
		 */
		private PCGTokenizer(final String line) throws PCGParseException
		{
			this(line, ":|[]"); //$NON-NLS-1$
		}

		/**
		 * Constructor
		 * @param line           a String to tokenize
		 * @param delimiters     a FOUR-character String specifying the four needed delimiters:
		 *                       <ol>
		 *                           <li>the inner delimiter for a PCGElement</li>
		 *                           <li>the outer delimiter for a PCGElement</li>
		 *                           <li>the start delimiter for nested PCGElements</li>
		 *                           <li>the stop delimiter for nested PCGElement</li>
		 *                       </ol>
		 * @throws PCGParseException
		 */
		private PCGTokenizer(final String line, final String delimiters) throws PCGParseException
		{
			final char[] dels = delimiters.toCharArray();

			this.innerDelimiter = String.valueOf(dels[0]);
			this.outerDelimiter = String.valueOf(dels[1]);
			this.nestedStartDelimiter = String.valueOf(dels[2]);
			this.nestedStopDelimiter = String.valueOf(dels[3]);

			this.nestedStartDelimiterChar = nestedStartDelimiter.charAt(0);
			this.nestedStopDelimiterChar = nestedStopDelimiter.charAt(0);

			this.elements = new ArrayList<>(0);

			tokenizeLine(line);
		}

		private List<PCGElement> getElements()
		{
			return elements;
		}

		private void checkSyntax(final String line) throws PCGParseException
		{
			final char[] chars = line.toCharArray();

			int delimCount = 0;

			for (char aChar : chars)
			{
				if (aChar == nestedStartDelimiterChar)
				{
					++delimCount;
				}
				else if (aChar == nestedStopDelimiterChar)
				{
					--delimCount;
				}
			}

			if (delimCount < 0)
			{
				final String message = "Missing " + nestedStartDelimiter;
				throw new PCGParseException("PCGTokenizer::checkSyntax", line, message);
			}
			else if (delimCount > 0)
			{
				final String message = "Missing " + nestedStopDelimiter;
				throw new PCGParseException("PCGTokenizer::checkSyntax", line, message);
			}
		}

		private void tokenizeLine(final String line) throws PCGParseException
		{
			checkSyntax(line);

			final PCGElement root = new PCGElement("root"); //$NON-NLS-1$
			tokenizeLine(root, line);
			elements.addAll(root.getChildren());
		}

		private void tokenizeLine(final PCGElement parent, final String line) throws PCGParseException
		{
			final String dels = outerDelimiter + nestedStartDelimiter + nestedStopDelimiter;
			final StringTokenizer tokens = new StringTokenizer(line, dels, true);

			int nestedDepth = 0;
			String tag = null;
			final StringBuilder buffer = new StringBuilder(1000);

			while (tokens.hasMoreTokens())
			{
				String token = tokens.nextToken().trim();

				if (token.equals(outerDelimiter))
				{
					if (nestedDepth == 0)
					{
						if (buffer.length() > 0)
						{
							token = buffer.toString();

							int index = token.indexOf(innerDelimiter);

							if (index >= 0)
							{
								buffer.delete(0, buffer.length());

								final PCGElement element = new PCGElement(token.substring(0, index));
								element.addContent(token.substring(index + 1));
								parent.addContent(element);
							}
							else
							{
								final String message = "Malformed PCG element: " + token;
								throw new PCGParseException("PCGTokenizer::tokenizeLine", line, message);
							}
						}
					}
					else
					{
						buffer.append(token);
					}
				}
				else if (token.equals(nestedStartDelimiter))
				{
					if (nestedDepth == 0)
					{
						token = buffer.toString();

						int index = token.indexOf(innerDelimiter);

						if ((index >= 0) && (index == (token.length() - 1)))
						{
							buffer.delete(0, buffer.length());

							tag = token.substring(0, index);
						}
						else
						{
							final String message = "Malformed PCG element: " + token;
							throw new PCGParseException("PCGTokenizer::tokenizeLine", line, message);
						}
					}
					else
					{
						buffer.append(token);
					}

					++nestedDepth;
				}
				else if (token.equals(nestedStopDelimiter))
				{
					--nestedDepth;

					if (nestedDepth == 0)
					{
						final PCGElement element = new PCGElement(tag);
						tokenizeLine(element, buffer.toString());
						parent.addContent(element);
						buffer.delete(0, buffer.length());
					}
					else
					{
						buffer.append(token);
					}
				}
				else
				{
					buffer.append(token);
				}
			}

			if (buffer.length() > 0)
			{
				final String token = buffer.toString();

				final int index = token.indexOf(innerDelimiter);

				if (index >= 0)
				{
					buffer.delete(0, buffer.length());

					final PCGElement element = new PCGElement(token.substring(0, index));
					element.addContent(token.substring(index + 1));
					parent.addContent(element);
				}
				else
				{
					final String message = "Malformed PCG element: " + token;
					throw new PCGParseException("PCGTokenizer::tokenizeLine", line, message);
				}
			}
		}
	}

	/**
	 * Returns the version of the application that wrote the file
	 * @return An {@code int} array containing the 3 digit version
	 */
	int[] getPcgenVersion()
	{
		return pcgenVersion;
	}

	/**
	 * Compare the PCG version with a supplied version number 
	 * @param inVer The version to compare with the PCG version. Must have at least 3 elements.
	 * @return the value 0 if the PCG version is equal to the supplied version; a 
	 * value less than 0 if the PCG version is less than the supplied version; 
	 * and a value greater than 0 if the PCG version is greater than the supplied version.
	 */
	int compareVersionTo(int[] inVer)
	{
		return CoreUtility.compareVersions(pcgenVersion, inVer);
	}

	/**
	 * Returns any extra version info after the regular version number.
	 * @return String extra version information
	 */
	String getPcgenVersionSuffix()
	{
		return pcgenVersionSuffix;
	}

	private void parseLevelAbilityInfo(final PCGElement element, final CDOMObject pObj)
	{
		parseLevelAbilityInfo(element, pObj, -9);
	}

	private void parseLevelAbilityInfo(final PCGElement element, final CDOMObject pObj, final int level)
	{
		final Iterator<PCGElement> it2 = element.getChildren().iterator();

		if (it2.hasNext())
		{
			final String dString = EntityEncoder.decode(it2.next().getText());

			PersistentTransitionChoice<?> ptc;
			ptc = Compatibility.processOldAdd(Globals.getContext(), dString);

			if (ptc == null)
			{
				warnings.add(pObj.getDisplayName() + "(" + pObj.getClass().getName()
					+ ")\nCould not process LevelAbility: " + dString);
				return;
			}

			CDOMObject target = pObj;
			if (pObj instanceof PCClass)
			{
				target = thePC.getActiveClassLevel(((PCClass) pObj), level);
			}
			for (PersistentTransitionChoice<?> tptc : target.getSafeListFor(ListKey.ADD))
			{
				if (tptc.equals(ptc))
				{
					while (it2.hasNext())
					{
						final String choice = EntityEncoder.decode(it2.next().getText());
						thePC.addAssoc(tptc, AssociationListKey.ADD, choice);
					}
				}
			}
		}
	}

	/**
	 * @return the baseFeatPool
	 */
	@Override
	public double getBaseFeatPool()
	{
		return baseFeatPool;
	}

	/**
	 * @return the calcFeatPoolAfterLoad
	 */
	@Override
	public boolean isCalcFeatPoolAfterLoad()
	{
		return calcFeatPoolAfterLoad;
	}

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	private void resolveLanguages()
	{
		CNAbility langbonus = thePC.getBonusLanguageAbility();
		int currentBonusLang = thePC.getDetailedAssociationCount(langbonus);
		boolean foundLang = currentBonusLang > 0;

		Set<Language> foundLanguages = new HashSet<>(thePC.getLanguageSet());
		//Captures Auto (AUTO:LANG) and Persistent choices (ADD ex ability and CHOOSE)
		cachedLanguages.removeAll(foundLanguages);

		HashMapToList<Language, Object> langSources = new HashMapToList<>();
		Map<Object, Integer> actorLimit = new IdentityHashMap<>();
		Map<PersistentTransitionChoice, CDOMObject> ptcSources = new IdentityHashMap<>();

		List<? extends CDOMObject> abilities = thePC.getCDOMObjectList();
		for (CDOMObject a : abilities)
		{
			List<PersistentTransitionChoice<?>> addList = a.getListFor(ListKey.ADD);
			if (addList != null)
			{
				for (PersistentTransitionChoice<?> ptc : addList)
				{
					SelectableSet<?> ss = ptc.getChoices();
					if (ss.getName().equals("LANGUAGE") && LANGUAGE_CLASS.equals(ss.getChoiceClass()))
					{
						Collection<Language> selected = (Collection<Language>) ss.getSet(thePC);
						for (Language l : selected)
						{
							if (cachedLanguages.contains(l))
							{
								String source = SourceFormat.getFormattedString(a, Globals.getSourceDisplay(), true);
								int choiceCount = ptc.getCount().resolve(thePC, source).intValue();
								if (choiceCount > 0)
								{
									langSources.addToListFor(l, ptc);
									ptcSources.put(ptc, a);
									actorLimit.put(ptc, choiceCount);
								}
							}
						}
					}
				}
			}
		}
		if (!foundLang)
		{
			Set<Language> bonusAllowed = thePC.getLanguageBonusSelectionList();
			int count = thePC.getBonusLanguageCount();
			int choiceCount = count - currentBonusLang;
			if (choiceCount > 0)
			{
				for (Language l : bonusAllowed)
				{
					if (cachedLanguages.contains(l))
					{
						langSources.addToListFor(l, langbonus);
						actorLimit.put(langbonus, choiceCount);
					}
				}
			}
		}
		//Try to match them up as best as possible (this matches things with only one possible location...)
		boolean acted = !cachedLanguages.isEmpty();
		while (acted)
		{
			acted = false;
			for (Language l : langSources.getKeySet())
			{
				List<Object> actors = langSources.getListFor(l);
				if ((actors != null) && (actors.size() == 1))
				{
					Object actor = actors.get(0);
					acted = true;
					processRemoval(langbonus, langSources, actorLimit, ptcSources, l, actor);
				}
			}
			if (!acted && !langSources.isEmpty() && !actorLimit.isEmpty())
			{
				//pick one
				Language l = langSources.getKeySet().iterator().next();
				Object source = langSources.getListFor(l).get(0);
				processRemoval(langbonus, langSources, actorLimit, ptcSources, l, source);
				acted = true;
			}
		}

		for (Language l : cachedLanguages)
		{
			warnings.add("Unable to find source: " + "Character no longer speaks language: " + l.getDisplayName());
		}
	}

	private void processRemoval(CNAbility langbonus, HashMapToList<Language, Object> sources,
		Map<Object, Integer> actorLimit, Map<PersistentTransitionChoice, CDOMObject> ptcSources, Language l,
		Object actor)
	{
		Integer limit = actorLimit.get(actor);
		//apply
		processActor(langbonus, ptcSources, l, actor);
		cachedLanguages.remove(l);
		sources.removeListFor(l);
		//Remove this sources from all languages (may create more items with only one source)
		if (limit == 1)
		{
			for (Language lang : sources.getKeySet())
			{
				sources.removeFromListFor(lang, actor);
			}
			//Used up
			actorLimit.remove(actor);
		}
		else
		{
			//Use a slot
			actorLimit.put(actor, limit - 1);
		}
	}

	private void processActor(CNAbility langbonus, Map<PersistentTransitionChoice, CDOMObject> ptcSources, Language l,
		Object actor)
	{
		if (actor instanceof CNAbility)
		{
			thePC.addSavedAbility(new CNAbilitySelection(langbonus, l.getKeyName()), UserSelection.getInstance(),
				UserSelection.getInstance());
		}
		else if (actor instanceof PersistentTransitionChoice)
		{
			PersistentTransitionChoice<Language> ptc = (PersistentTransitionChoice<Language>) actor;
			ptc.restoreChoice(thePC, ptcSources.get(ptc), l);
		}
		else
		{
			warnings.add("Internal Error: Language actor of " + actor.getClass() + " is not understood");
		}
	}

	/**
	 * Set the source of the domain. See getDomainSource() for details.
	 * This method should NOT be called outside of file i/o routines!
	 * @param aSource the source to be set
	 **/
	public ClassSource getDomainSource(String aSource)
	{
		final StringTokenizer aTok = new StringTokenizer(aSource, "|", false);

		if (aTok.countTokens() < 2)
		{
			Logging.errorPrint("Invalid Domain Source:" + aSource);
			return null;
		}

		aTok.nextToken(); //Throw away "PCClass"

		String classString = aTok.nextToken();
		PCClass cl = thePC.getClassKeyed(classString);
		if (cl == null)
		{
			Logging.errorPrint("Invalid Class in Domain Source:" + aSource);
			return null;
		}
		ClassSource cs;
		if (aTok.hasMoreTokens())
		{
			int level = Integer.parseInt(aTok.nextToken());
			cs = new ClassSource(cl, level);
		}
		else
		{
			cs = new ClassSource(cl);
		}
		return cs;
	}

	private void insertDefaultClassSpellLists()
	{
		for (PCClass pcc : thePC.getClassList())
		{
			thePC.addDefaultSpellList(pcc);
		}
	}

	public PCAlignment getNoAlignment()
	{
		return Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCAlignment.class,
			Constants.NONE);
	}

}
