/*
 * PCGVer2Creator.java
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
 * Created on March 19, 2002, 4:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.io;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.FixedStringList;
import pcgen.base.util.NamedValue;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.SelectableSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.cdom.helper.ClassSource;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.list.ClassSpellList;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.BonusManager;
import pcgen.core.BonusManager.TempBonusInfo;
import pcgen.core.Campaign;
import pcgen.core.ChronicleEntry;
import pcgen.core.Deity;
import pcgen.core.Description;
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
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.SpellProhibitor;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.analysis.SpellLevel;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.pclevelinfo.PCLevelInfoStat;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceManager;
import pcgen.system.PCGenPropBundle;
import pcgen.util.Logging;
import pcgen.util.StringPClassUtil;

/**
 * <code>PCGVer2Creator</code><br>
 * Creates a line oriented format.
 * Each line should adhere to the following grammar:<br>
 *
 * <i>line</i> := EMPTY | <i>comment</i> | <i>taglist</i>
 * <i>comment</i> := '#' STRING
 * <i>taglist</i> := tag ('|' tag)*
 * <i>tag</i> := simpletag | nestedtag
 * <i>nestedtag</i> := TAGNAME ':' '[' taglist ']'
 * <i>simpletag</i> := TAGNAME ':' TAGVALUE
 *
 * @author Thomas Behr 19-03-02
 * @version $Revision$
 */
final class PCGVer2Creator implements IOConstants
{
	/*
	 * DO NOT CHANGE line separator.
	 * Need to keep the Unix line separator to ensure cross-platform portability.
	 *
	 * author: Thomas Behr 2002-11-13
	 */

	private PlayerCharacter thePC;
	private GameMode mode;
	private List<Campaign> campaigns;

	/**
	 * Constructor
	 * @param aPC
	 */
	PCGVer2Creator(final PlayerCharacter aPC, GameMode mode, List<Campaign> campaigns)
	{
		thePC = aPC;
		this.mode = mode;
		this.campaigns = campaigns;
	}

	/**
	 * create PCG string for a given PlayerCharacter
	 *
	 * <br>author: Thomas Behr 19-03-02
	 *
	 * @return a String in PCG format, containing all information
	 *         PCGen associates with a given PlayerCharacter
	 */
	public String createPCGString()
	{
		// Guess that this should be about 1000
		StringBuffer buffer = new StringBuffer(1000);

		appendPCGVersionLine(buffer);

		/*
		 * #System Information
		 * CAMPAIGNS:>:-delimited list<
		 * VERSION:x.x.x
		 * ROLLMETHOD:xxx
		 * PURCHASEPOINTS:Y or N|TYPE:>living City, Living greyhawk, etc<
		 * UNLIMITEDPOOLCHECKED:Y or N
		 * POOLPOINTS:>numeric value 0-?<
		 * GAMEMODE:DnD
		 * TABLABEL:0
		 * AUTOSPELLS:Y or N
		 * AUTOCOMPANIONS:Y or N
		 *
		 * hmmm, better have
		 * CAMPAIGNS:>campaign_name<|CAMPAIGNS:>campaign_name<|...
		 */
		appendNewline(buffer);
		appendComment("System Information", buffer); //$NON-NLS-1$

		//appendCampaignLineOldFormat(buffer);
		appendCampaignLine(buffer);
		appendVersionLine(buffer);
		appendRollMethodLine(buffer);
		appendPurchasePointsLine(buffer);

		//appendUnlimitedPoolCheckedLine(buffer);
		appendPoolPointsLine(buffer);
		appendGameModeLine(buffer);
		appendTabLabelLine(buffer);
		appendAutoSpellsLine(buffer);
		appendUseHigherSpellSlotsLines(buffer);
		appendLoadCompanionLine(buffer);
		appendUseTempModsLine(buffer);
		appendOutputSheetsLines(buffer);
		appendAutoSortLines(buffer);
		appendGearCostSizeLines(buffer);

		/*
		 * #Character Bio
		 * CHARACTERNAME:Code Monkey
		 * TABNAME:Code Monkey the Best Ever No Really!
		 * PLAYERNAME:Jason Monkey
		 * HEIGHT:75
		 * WEIGHT:198
		 * AGE:17
		 * GENDER:text
		 * HANDED:text
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
		appendNewline(buffer);
		appendComment("Character Bio", buffer); //$NON-NLS-1$
		appendCharacterNameLine(buffer);
		appendTabNameLine(buffer);
		appendPlayerNameLine(buffer);
		appendHeightLine(buffer);
		appendWeightLine(buffer);
		appendAgeLine(buffer);
		appendGenderLine(buffer);
		appendHandedLine(buffer);
		appendSkinColorLine(buffer);
		appendEyeColorLine(buffer);
		appendHairColorLine(buffer);
		appendHairStyleLine(buffer);
		appendLocationLine(buffer);
		appendResidenceLine(buffer);
		appendBirthdayLine(buffer);
		appendBirthplaceLine(buffer);
		appendPersonalityTrait1Line(buffer);
		appendPersonalityTrait2Line(buffer);
		appendSpeechPatternLine(buffer);
		appendPhobiasLine(buffer);
		appendInterestsLine(buffer);
		appendCatchPhraseLine(buffer);
		appendPortraitLine(buffer);

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
		 *
		 * hmmm better have
		 * STAT:STR|SCORE:18
		 */
		appendNewline(buffer);
		appendComment("Character Attributes", buffer); //$NON-NLS-1$
		appendStatLines(buffer);
		appendAlignmentLine(buffer);
		appendRaceLine(buffer);
		appendFavoredClassLine(buffer);

		/*
		 * #Character Class(es)
		 * CLASS:Fighter|LEVEL=3
		 * CLASSABILITIESLEVEL:Fighter=1(>This would only display up to the level the character has already,)
		 * CLASSABILITIESLEVEL:Fighter=2(>with any special abilities not covered by other areas,)
		 * CLASSABILITIESLEVEL:Fighter=3(>such as skills, feats, etc., but would list SA's, and the like<)
		 * CLASS:Wizard|LEVEL=1
		 * CLASSABILITIESLEVEL:Wizard=1(SA's, MEMORIZE:Y, etc)
		 *
		 * hmmm, better have
		 * CLASS:Fighter|LEVEL:3|SKILLPOOL:0
		 * CLASS:Wizard|LEVEL:1|SKILLPOOL:0|CANCASTPERDAY:1,1
		 */
		appendNewline(buffer);
		appendComment("Character Class(es)", buffer); //$NON-NLS-1$
		appendClassLines(buffer);

		/*
		 * #Character Experience
		 * EXPERIENCE:6000
		 */
		appendNewline(buffer);
		appendComment("Character Experience", buffer); //$NON-NLS-1$
		appendExperienceLine(buffer);

		/*
		 * #Character Templates
		 * TEMPLATESAPPLIED:If any, else this would just have the comment line, and skip to the next
		 */
		appendNewline(buffer);
		appendComment("Character Templates", buffer); //$NON-NLS-1$
		appendTemplateLines(buffer);

		appendNewline(buffer);
		appendComment("Character Region", buffer); //$NON-NLS-1$
		appendRegionLine(buffer);

		/*
		 * #Character Skills
		 * CLASSBOUGHT:Fighter
		 * SKILL:Alchemy|CROSSCLASS:Y|COST:2|RANK:7  (Should be Obvious what each of these does, I hope ;p)
		 * SKILL:Survival|CLASS:Y|COST:1|SYNERGY:Wilderness Lore=5=2|RANK:10
		 * CLASSBOUGHT:Wizard
		 * SKILL:Spellcraft|CLASS:Y|COST:1|RANK7
		 *
		 *
		 * hmmm, better have
		 * SKILL:Alchemy|SYNERGY:....|OUTPUTORDER:1|CLASSBOUGHT:[CLASS:FIGHTER|RANKS:7|COST:2|CLASSSKILL:N]
		 * SKILL:Spellcraft|SYNERGY:....|OUTPUTORDER:1|CLASSBOUGHT:[CLASS:WIZARD|RANKS:7|COST:1|CLASSSKILL:Y]
		 */
		appendNewline(buffer);
		appendComment("Character Skills", buffer); //$NON-NLS-1$
		appendSkillLines(buffer);

		/*
		 * #Character Languages
		 */
		appendNewline(buffer);
		appendComment("Character Languages", buffer); //$NON-NLS-1$
		appendLanguageLine(buffer);

		/*
		 * Anything that is already Pipe Delimited should be in
		 * parenthesis to avoid confusion on PCGen's part
		 *
		 * #Character Feats
		 * FEAT:Alertness|TYPE:General|(BONUS:SKILL|Listen,Spot|2)|DESC:+2 on Listen and Spot checks
		 *
		 * hmmm, better have colons and pipes encoded as entities
		 * FEAT:Alertness|TYPE:General|SAVE:BONUS&colon;SKILL&pipe;Listen,Spot&pipe;2|DESC:+2 on Listen and Spot checks
		 */
		appendNewline(buffer);
		appendComment("Character Feats", buffer); //$NON-NLS-1$
		appendFeatLines(buffer);

		appendNewline(buffer);
		appendComment("Character Abilities", buffer); //$NON-NLS-1$
		appendAbilityLines(buffer);

		/*
		 * #Character Weapon proficiencies
		 */
		appendNewline(buffer);
		appendComment("Character Weapon proficiencies", buffer); //$NON-NLS-1$
		appendWeaponProficiencyLines(buffer);

		/*
		 * This is the REALLY ugly part for all characters as it should contain ALL the information for the equipment
		 * Money goes here as well
		 *
		 * #Character Equipment
		 * EQUIPNAME:Longsword|OUTPUTORDER:2|COST:5|WT:5|QTY:1|>other info<
		 * EQUIPNAME:Backpack|OUTPUTORDER:9|COST:5|WT:5
		 * EQUIPNAME:Rope (Silk)|OUTPUTORDER:-1|COST:5|WT:5
		 */
		appendNewline(buffer);
		appendComment("Character Equipment", buffer); //$NON-NLS-1$
		appendMoneyLine(buffer);
		appendEquipmentLines(buffer);
		appendEquipmentSetLines(buffer);

		/*
		 * Append Temporary Bonuses
		 */
		appendNewline(buffer);
		appendComment("Temporary Bonuses", buffer); //$NON-NLS-1$
		appendTempBonuses(buffer);

		/*
		 * Append EquipSet Temp Bonuses
		 */
		appendNewline(buffer);
		appendComment("EquipSet Temp Bonuses", buffer); //$NON-NLS-1$
		appendEqSetBonuses(buffer);

		/*
		 * #Character Deity/Domain
		 * DEITY:Yondalla|DEITYDOMAINS:Good,Law,Protection|ALIGNALLOW:013|DESC:Halflings, Protection, Fertility|SYMBOL:None|DEITYFAVWEAP:Sword (Short)|DEITYALIGN:ALIGN:LG
		 * DOMAIN:GOOD|DOMAINGRANTS:>list of abilities<
		 * DOMAINSPELLS:GOOD(>list of level by level spells)
		 *
		 * hmmm, better have
		 * DEITY:Yondalla|DEITYDOMAINS:[DOMAIN:Good|DOMAIN:Law|DOMAIN:Protection]|...
		 * DOMAINSPELLS:GOOD|SPELLLIST:(>list of level by level spells)
		 */
		appendNewline(buffer);
		appendComment("Character Deity/Domain", buffer); //$NON-NLS-1$
		appendDeityLine(buffer);
		appendDomainLines(buffer);

		/*
		 * This one is what will make spellcasters U G L Y!!!
		 *
		 * #Character Spells Information
		 * CLASS:Wizard|CANCASTPERDAY:2,4(Totals the levels all up + includes attribute bonuses)
		 * SPELLNAME:Blah|SCHOOL:blah|SUBSCHOOL:blah|Etc
		 *
		 * hmmm, moved CANCASTPERDAY to standard class line
		 */
		appendNewline(buffer);
		appendComment("Character Spells Information", buffer); //$NON-NLS-1$
		appendSpellBookLines(buffer);
		appendSpellLines(buffer);
		appendSpellListLines(buffer);

		/*
		 * #Character Description/Bio/History
		 * CHARACTERBIO:any text that's in the BIO field
		 * CHARACTERDESC:any text that's in the BIO field
		 */
		appendNewline(buffer);
		appendComment("Character Description/Bio/History", buffer); //$NON-NLS-1$
		appendCharacterBioLine(buffer);
		appendCharacterDescLine(buffer);
		appendCharacterCompLine(buffer);
		appendCharacterAssetLine(buffer);
		appendCharacterMagicLine(buffer);
		appendCharacterDmNotesLine(buffer);

		/*
		 * #Kits
		 */
		appendNewline(buffer);
		appendComment("Kits", buffer); //$NON-NLS-1$
		appendKitLines(buffer);

		/*
		 * #Character Master/Followers
		 * MASTER:Mynex|TYPE:Follower|HITDICE:20|FILE:E$\DnD\dnd-chars\ravenlock.pcg
		 * FOLLOWER:Raven|TYPE:Animal Companion|HITDICE:5|FILE:E$\DnD\dnd-chars\raven.pcg
		 */
		appendNewline(buffer);
		appendComment("Character Master/Follower", buffer); //$NON-NLS-1$
		appendFollowerLines(buffer);

		/*
		 * #Character Notes Tab
		 */
		appendNewline(buffer);
		appendComment("Character Notes Tab", buffer); //$NON-NLS-1$
		appendNotesLines(buffer);

		/*
		 * #AgeSet Kit selections
		 */
		appendNewline(buffer);
		appendComment("Age Set Selections", buffer); //$NON-NLS-1$
		appendAgeSetLine(buffer);

		/*
		 * #Campaign History
		 */
		appendNewline(buffer);
		appendComment("Campaign History", buffer); //$NON-NLS-1$
		appendCampaignHistoryLines(buffer);

		/*
		 * #Suppressed fields
		 */
		appendNewline(buffer);
		appendComment("Suppressed Biography Fields", buffer); //$NON-NLS-1$
		appendSuppressBioFieldLines(buffer);

		/*
		 * Add one more newline at end of file
		 */
		appendNewline(buffer);

		// All done!
		return buffer.toString();
	}

	private void appendCampaignLine(StringBuffer buffer)
	{
		String del = Constants.EMPTY_STRING;
		Collection<Campaign> campList;
		if (campaigns != null)
		{
			campList = campaigns;
		}
		else
		{
			campList = PersistenceManager.getInstance().getLoadedCampaigns();
		}
		for (Campaign campaign : campList)
		{
			buffer.append(del);
			buffer.append(TAG_CAMPAIGN).append(':');
			buffer.append(campaign.getKeyName());
			del = "|"; //$NON-NLS-1$
		}

		buffer.append(LINE_SEP);
	}

	/**
	 * @param buffer
	 */
	private void appendSuppressBioFieldLines(StringBuffer buffer)
	{
		buffer.append(TAG_SUPPRESS_BIO_FIELDS).append(':');
		String delim = Constants.EMPTY_STRING;
		for (BiographyField field : BiographyField.values())
		{
			if (thePC.getSuppressBioField(field))
			{
				buffer.append(delim);
				buffer.append(field);
				delim = "|"; //$NON-NLS-1$
			}
		}
		buffer.append(LINE_SEP);
	}

	private GameMode getGameMode()
	{
		if (mode != null)
		{
			return mode;
		}
		else
		{
			return SettingsHandler.getGame();
		}
	}

	private void appendGameModeLine(StringBuffer buffer)
	{
		buffer.append(TAG_GAMEMODE).append(':');
		buffer.append(getGameMode().getName());
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * private helper methods
	 * ###############################################################
	 */
	private static void appendPCGVersionLine(StringBuffer buffer)
	{
		buffer.append(TAG_PCGVERSION).append(':');
		buffer.append("2.0"); //$NON-NLS-1$
		buffer.append(LINE_SEP);
	}

	private void appendPurchasePointsLine(StringBuffer buffer)
	{
		buffer.append(TAG_PURCHASEPOINTS).append(':');
		if (getGameMode().isPurchaseStatMode())
		{
			buffer.append('Y');
			buffer.append('|');
			buffer.append(TAG_TYPE).append(':');
			buffer
				.append(getGameMode().getPurchaseModeMethodName());
		}
		else
		{
			buffer.append('N');
		}
		buffer.append(LINE_SEP);
	}

	private void appendRollMethodLine(StringBuffer buffer)
	{
		final GameMode game = getGameMode();
		buffer.append(TAG_ROLLMETHOD).append(':');
		buffer.append(game.getRollMethod());
		buffer.append('|');
		buffer.append(TAG_EXPRESSION).append(':');
		switch (game.getRollMethod())
		{
			case Constants.CHARACTER_STAT_METHOD_ALL_THE_SAME:
				buffer.append(game.getAllStatsValue());
				break;

			case Constants.CHARACTER_STAT_METHOD_PURCHASE:
				buffer.append(game.getPurchaseModeMethodName());
				break;

			case Constants.CHARACTER_STAT_METHOD_ROLLED:
				buffer.append(game.getRollMethodExpression());
				break;

			default:
				buffer.append(0);
				break;
		}
		buffer.append(LINE_SEP);
	}

	/*
	 * modified this function to output the version number
	 * as displayed in pcgenprop.properties instead of a simple int.
	 * This will record the version more accurately.
	 */
	private static void appendVersionLine(StringBuffer buffer)
	{
		buffer.append(TAG_VERSION).append(':');
		buffer.append(PCGenPropBundle.getVersionNumber());
		buffer.append(LINE_SEP);
	}

	private void appendAgeLine(StringBuffer buffer)
	{
		buffer.append(TAG_AGE).append(':');
		buffer.append(thePC.getAge());
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * AgeSet
	 * ###############################################################
	 */
	private void appendAgeSetLine(StringBuffer buffer)
	{
		buffer.append(TAG_AGESET);

		for (int i = 0; i < 10; i++)
		{
			buffer.append(':');

			if (thePC.hasMadeKitSelectionForAgeSet(i))
			{
				buffer.append('1');
			}
			else
			{
				buffer.append('0');
			}
		}
		buffer.append(LINE_SEP);
	}

	private void appendAlignmentLine(StringBuffer buffer)
	{
		//
		// Only save alignment if game mode supports it
		//
		if (Globals.getGameModeAlignmentText().length() != 0 && thePC.getPCAlignment() != null)
		{
			buffer.append(TAG_ALIGNMENT).append(':');
			buffer.append(thePC.getPCAlignment().getAbb());
			buffer.append(LINE_SEP);
		}
	}

	private void appendBirthdayLine(StringBuffer buffer)
	{
		buffer.append(TAG_BIRTHDAY).append(':');
		buffer.append(EntityEncoder.encode(thePC.getBirthday()));
		buffer.append(LINE_SEP);
	}

	private void appendBirthplaceLine(StringBuffer buffer)
	{
		buffer.append(TAG_BIRTHPLACE).append(':');
		buffer.append(EntityEncoder.encode(thePC.getBirthplace()));
		buffer.append(LINE_SEP);
	}

	/**
	 * @param buffer
	 */
	private void appendCampaignHistoryLines(StringBuffer buffer)
	{
		for (ChronicleEntry ce : thePC.getChronicleEntries())
		{
			buffer.append(TAG_CHRONICLE_ENTRY).append(':');
			buffer.append(ce.isOutputEntry()?"Y":"N:");
			buffer.append('|');
			buffer.append(TAG_CAMPAIGN).append(':');
			buffer.append(EntityEncoder.encode(ce.getCampaign()));
			buffer.append('|');
			buffer.append(TAG_ADVENTURE).append(':');
			buffer.append(EntityEncoder.encode(ce.getAdventure()));
			buffer.append('|');
			buffer.append(TAG_PARTY).append(':');
			buffer.append(EntityEncoder.encode(ce.getParty()));
			buffer.append('|');
			buffer.append(TAG_DATE).append(':');
			buffer.append(EntityEncoder.encode(ce.getDate()));
			buffer.append('|');
			buffer.append(TAG_EXPERIENCE).append(':');
			buffer.append(ce.getXpField());
			buffer.append('|');
			buffer.append(TAG_GM).append(':');
			buffer.append(EntityEncoder.encode(ce.getGmField()));
			buffer.append('|');
			buffer.append(TAG_CHRONICLE).append(':');
			buffer.append(EntityEncoder.encode(ce.getChronicle()));
			buffer.append(LINE_SEP);
			
		}
	}

	private void appendCatchPhraseLine(StringBuffer buffer)
	{
		buffer.append(TAG_CATCHPHRASE).append(':');
		buffer.append(EntityEncoder.encode(thePC.getCatchPhrase()));
		buffer.append(LINE_SEP);
	}

	private void appendCharacterAssetLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERASSET).append(':');
		buffer.append(EntityEncoder.encode(thePC.getSafeStringFor(StringKey.MISC_ASSETS)));
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Character Description/Bio/History methods
	 * ###############################################################
	 */
	private void appendCharacterBioLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERBIO).append(':');
		buffer.append(EntityEncoder.encode(thePC.getBio()));
		buffer.append(LINE_SEP);
	}

	private void appendCharacterCompLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERCOMP).append(':');
		buffer.append(EntityEncoder.encode(thePC.getSafeStringFor(StringKey.MISC_COMPANIONS)));
		buffer.append(LINE_SEP);
	}

	private void appendCharacterDescLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERDESC).append(':');
		buffer.append(EntityEncoder.encode(thePC.getDescription()));
		buffer.append(LINE_SEP);
	}

	private void appendCharacterMagicLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERMAGIC).append(':');
		buffer.append(EntityEncoder.encode(thePC.getSafeStringFor(StringKey.MISC_MAGIC)));
		buffer.append(LINE_SEP);
	}

	private void appendCharacterDmNotesLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERDMNOTES).append(':');
		buffer.append(EntityEncoder.encode(thePC.getSafeStringFor(StringKey.MISC_DM)));
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Character Class(es) methods
	 * ###############################################################
	 */
	private void appendClassLines(StringBuffer buffer)
	{
		Cache specials = new Cache();

		for (PCClass pcClass : thePC.getClassSet())
		{
			int classLevel = thePC.getLevel(pcClass);

			buffer.append(TAG_CLASS).append(':');
			buffer.append(EntityEncoder.encode(pcClass.getKeyName()));

			final String subClassKey = thePC.getSubClassName(pcClass);

			if (subClassKey != null && !Constants.EMPTY_STRING.equals(subClassKey))
			{
				buffer.append('|');
				buffer.append(TAG_SUBCLASS).append(':');
				buffer.append(EntityEncoder.encode(subClassKey));
			}

			buffer.append('|');
			buffer.append(TAG_LEVEL).append(':');
			buffer.append(classLevel);
			buffer.append('|');
			buffer.append(TAG_SKILLPOOL).append(':');
			Integer currentPool = thePC.getAssoc(pcClass, AssociationKey.SKILL_POOL);
			buffer.append(currentPool == null ? 0 : currentPool);

			// determine if this class can cast spells
			boolean isCaster = false;

			/*
			 * TODO WARNING: Technically, this is WRONG. A 4th level Ranger with
			 * good stats will show up here as as NOT being a caster, but is
			 * lucky enough to be considered Psionic! zeroCastSpells() is not
			 * accurate because it is based on the base list, not on the list
			 * with adjustments for stats. The method should be destroyed, and
			 * spell casting made more explicit.
			 */
			if (!thePC.getSpellSupport(pcClass).zeroCastSpells())
			{
				isCaster = true;
			}

			boolean isPsionic = thePC.getSpellSupport(pcClass).hasKnownList() && !isCaster;

			if (isCaster || isPsionic)
			{
				buffer.append('|');
				buffer.append(TAG_SPELLBASE).append(':');
				buffer.append(EntityEncoder.encode(pcClass.getSpellBaseStat()));
				buffer.append('|');
				buffer.append(TAG_CANCASTPERDAY).append(':');
				buffer.append(StringUtil.join(thePC.getSpellSupport(pcClass)
					.getCastListForLevel(classLevel), ","));
			}

			Collection<? extends SpellProhibitor> prohib = thePC
					.getProhibitedSchools(pcClass);
			if (prohib != null)
			{
				Set<String> set = new TreeSet<String>();
				for (SpellProhibitor sp : prohib)
				{
					set.addAll(sp.getValueList());
				}
				if (!set.isEmpty())
				{
					buffer.append('|');
					buffer.append(TAG_PROHIBITED).append(':');
					buffer.append(EntityEncoder.encode(StringUtil
							.join(set, ",")));
				}
			}
			appendAddTokenInfo(buffer, pcClass);

			buffer.append(LINE_SEP);

			String spec = thePC.getAssoc(pcClass, AssociationKey.SPECIALTY);
			if (spec != null)
			{
				specials.put(pcClass.getKeyName() + TAG_SPECIALTY + '0', spec);
			}

			String key;
			key = pcClass.getKeyName() + TAG_SAVE + '0';

			List<? extends SpecialAbility> salist = thePC.getUserSpecialAbilityList(pcClass);
			if (salist != null)
			{
				for (SpecialAbility sa : salist)
				{
					specials.put(pcClass.getKeyName() + TAG_SA + 0, sa
							.getKeyName());
					break;
				}
			}

			for (BonusObj save : thePC.getAddedBonusList(pcClass))
			{
				if (save.saveToPCG())
				{
					specials.put(key, "BONUS|" + save);
				}
			}
			for (int i = 1; i <= thePC.getLevel(pcClass); i++)
			{
				key = pcClass.getKeyName() + TAG_SAVE + (i - 1);
				PCClassLevel pcl = thePC.getActiveClassLevel(pcClass, i);
				for (BonusObj save : thePC.getAddedBonusList(pcl))
				{
					if (save.saveToPCG())
					{
						specials.put(key, "BONUS|" + save);
					}
				}
			}
		}

		//
		// Save level up information in the order of levelling
		//
		for (PCLevelInfo pcl : thePC.getLevelInfo())
		{
			final String classKeyName = pcl.getClassKeyName();
			int lvl = pcl.getClassLevel() - 1;
			PCClass pcClass = thePC.getClassKeyed(classKeyName);
			buffer.append(TAG_CLASSABILITIESLEVEL).append(':');

			if (pcClass == null)
			{
				pcClass =
						Globals.getContext().ref
							.silentlyGetConstructedCDOMObject(PCClass.class,
								classKeyName);

				if (pcClass != null)
				{
					pcClass =
							thePC.getClassKeyed(pcClass.get(ObjectKey.EX_CLASS)
								.resolvesTo().getKeyName());
				}
			}

			if (pcClass != null)
			{
				buffer.append(EntityEncoder.encode(pcClass.getKeyName()));
			}
			else
			{
				buffer.append(EntityEncoder.encode("???")); //$NON-NLS-1$
			}

			buffer.append('=').append(lvl + 1);

			if (pcClass != null)
			{
				String aKey = thePC.getAssoc(thePC.getActiveClassLevel(pcClass, lvl + 1),
						AssociationKey.SUBSTITUTIONCLASS_KEY);
				if (aKey != null)
				{
					buffer.append('|');
					buffer.append(TAG_SUBSTITUTIONLEVEL).append(':');
					buffer.append(aKey);
				}

				buffer.append('|');
				buffer.append(TAG_HITPOINTS).append(':');
				PCClassLevel classLevel = thePC.getActiveClassLevel(pcClass, lvl);
				Integer hp = thePC.getHP(classLevel);
				buffer.append(hp == null ? 0 : hp);
				appendSpecials(buffer, specials.get(pcClass.getKeyName()
					+ TAG_SAVE + lvl), TAG_SAVES, TAG_SAVE, lvl);
				appendSpecials(buffer, specials.get(pcClass.getKeyName()
					+ TAG_SPECIALTY + lvl), TAG_SPECIALTIES, TAG_SPECIALTY, lvl);
				appendSpecials(buffer, specials.get(pcClass.getKeyName()
					+ TAG_SA + lvl), TAG_SPECIALABILITIES, TAG_SA, lvl);

				if (lvl == 0)
				{
					appendSpecials(buffer, specials.get(pcClass.getKeyName()
						+ TAG_SA + (lvl - 1)), TAG_SPECIALABILITIES, TAG_SA, -1);
				}

				//
				// Remember what choices were made for each of the ADD: tags
				//
				appendAddTokenInfo(buffer, thePC.getActiveClassLevel(pcClass, lvl + 1));
			}

			List<PCLevelInfoStat> statList = pcl.getModifiedStats(true);

			if (statList != null)
			{
				for (PCLevelInfoStat stat : statList)
				{
					buffer.append('|').append(TAG_PRESTAT).append(':').append(
						stat.toString());
				}
			}

			statList = pcl.getModifiedStats(false);

			if (statList != null)
			{
				for (PCLevelInfoStat stat : statList)
				{
					buffer.append('|').append(TAG_PRESTAT).append(':').append(
						stat.toString());
				}
			}

			int sp = pcl.getSkillPointsGained(thePC);

			//if (sp != 0)
			{
				buffer.append('|').append(TAG_SKILLPOINTSGAINED).append(':')
					.append(sp);
			}

			sp = pcl.getSkillPointsRemaining();

			//if (sp != 0)
			{
				buffer.append('|').append(TAG_SKILLPOINTSREMAINING).append(':')
					.append(sp);
			}

			buffer.append(LINE_SEP);
		}
	}

	/**
	 * Convenience Method
	 *
	 * <br>author: Thomas Behr 19-03-02
	 *
	 * @param comment
	 * @param buffer
	 */
	private static void appendComment(String comment, StringBuffer buffer)
	{
		buffer.append(createComment(comment));
	}

	/*
	 * ###############################################################
	 * Character Deity/Domain methods
	 * ###############################################################
	 */
	private void appendDeityLine(StringBuffer buffer)
	{
		if (thePC.getDeity() != null)
		{
			final Deity aDeity = thePC.getDeity();

			buffer.append(TAG_DEITY).append(':');
			buffer.append(EntityEncoder.encode(aDeity.getKeyName()));

			/*
			 * currently unused information
			 *
			 * author: Thomas Behr 09-09-02
			 */
			buffer.append('|');
			buffer.append(TAG_DEITYDOMAINS).append(':');
			buffer.append('[');

			String del = Constants.EMPTY_STRING;

			for (CDOMReference<Domain> ref : aDeity
				.getSafeListMods(Deity.DOMAINLIST))
			{
				for (Domain d : ref.getContainedObjects())
				{
					buffer.append(del);
					buffer.append(TAG_DOMAIN).append(':');
					buffer.append(EntityEncoder.encode(d.getKeyName()));
					del = "|"; //$NON-NLS-1$
				}
			}

			buffer.append(']');

			buffer.append('|');
			buffer.append(TAG_ALIGNALLOW).append(':');
			//TODO Need to clean this up?
			for (final Description desc : aDeity
				.getSafeListFor(ListKey.DESCRIPTION))
			{
				buffer.append('|');
				buffer.append(TAG_DESC).append(':');
				buffer.append(desc.getPCCText());
			}
			buffer.append('|');
			buffer.append(TAG_HOLYITEM).append(':');
			buffer.append(EntityEncoder.encode(aDeity.getSafe(StringKey.HOLY_ITEM)));

			buffer.append('|');
			buffer.append(TAG_DEITYFAVWEAP).append(':');
			buffer.append('[');

			List<CDOMReference<WeaponProf>> dwp =
					aDeity.getListFor(ListKey.DEITYWEAPON);
			if (dwp != null)
			{
				del = Constants.EMPTY_STRING;
				for (CDOMReference<WeaponProf> ref : dwp)
				{
					buffer.append(del);
					buffer.append(TAG_WEAPON).append(':');
					buffer.append(EntityEncoder.encode(ref.getLSTformat(false)));
					del = "|"; //$NON-NLS-1$
				}
			}

			buffer.append(']');

			buffer.append('|');
			buffer.append(TAG_DEITYALIGN).append(':');
			PCAlignment al = aDeity.get(ObjectKey.ALIGNMENT);
			if (al != null)
			{
				buffer.append(al.getKeyName());
			}

			buffer.append(LINE_SEP);
		}
	}

	private void appendDomainLines(StringBuffer buffer)
	{
		for (final Domain domain : thePC.getDomainSet())
		{
			// TODO is any of this commented out code any use anymore?:
			//
			//  			// improve here - performance and concept!!!!
			//  			domainSpells.clear();
			//  			for (Iterator it2 = Globals.getSpellMap().values().iterator(); it2.hasNext();)
			//  			{
			//  				aSpell = (Spell)it2.next();
			//  //			levelString = aSpell.levelForClass(aDomain.getName());
			//  				if ((levelString.length() > 0) &&
			//  				  (levelString.indexOf("-1") < 0))
			//  				{
			//  					tokens = new StringTokenizer(levelString, ",");
			//  					while (tokens.hasMoreTokens())
			//  					{
			//  						if (tokens.nextToken().equals(aDomain.getName()))
			//  						{
			//  							break;
			//  						}
			//  					}
			//  					domainSpells.add(((tokens.hasMoreTokens()) ? tokens.nextToken() + " " : "") +
			//  					  aSpell.getName());
			//  				}
			//  			}
			buffer.append(TAG_DOMAIN).append(':');
			buffer.append(EntityEncoder.encode(domain.getKeyName()));

			for (String assoc : thePC.getAssociationList(domain))
			{
				buffer.append('|');
				buffer.append(TAG_ASSOCIATEDDATA).append(':');
				buffer.append(EntityEncoder.encode(assoc));
			}

			for (final Description desc : domain
				.getSafeListFor(ListKey.DESCRIPTION))
			{
				buffer.append('|');
				buffer.append(TAG_DOMAINGRANTS).append(':');
				buffer.append(desc.getPCCText());
			}
			buffer.append('|');
			appendSourceInTaggedFormat(buffer, getDomainSourcePcgString(domain));

			//			buffer.append('|');
			//			buffer.append(TAG_DOMAINFEATS).append(':');
			//			buffer.append(aDomain.getFeatList());
			//			buffer.append('|');
			//			buffer.append(TAG_DOMAINSKILLS).append(':');
			//			buffer.append(aDomain.getSkillList());
			//			buffer.append('|');
			//			buffer.append(TAG_DOMAINSPECIALS).append(':');
			//			buffer.append(aDomain.getSpecialAbility());
			//			buffer.append('|');
			//			buffer.append(TAG_DOMAINSPELLS).append(':');
			//			buffer.append(aDomain.getSpellList());
			buffer.append(LINE_SEP);

			/*
			 * not working yet anyways
			 *
			 * author: Thomas Behr 09-09-02
			 */

			//  			buffer.append(TAG_DOMAINSPELLS).append(':');
			//  			buffer.append(EntityEncoder.encode(aDomain.getKeyName()));
			//  			buffer.append('|');
			//  			buffer.append(TAG_SPELLLIST).append(':');
			//			buffer.append('[');
			//  			del = "";
			//  			Collections.sort(domainSpells);
			//  			for (Iterator it2 = domainSpells.iterator(); it2.hasNext();)
			//  			{
			//  				buffer.append(del);
			//  				buffer.append(TAG_SPELL).append(':');
			//				buffer.append(EntityEncoder.encode((String)it2.next()));
			//  				del = "|";
			//  			}
			//			buffer.append(']');
			//  			buffer.append(LINE_SEP);
		}
	}

	/**
	 * Returns the source of the domain in the format "PObject|name[|level]"
	 * For example, "PCClass|Cleric|1"
	 * (since the level is relevant)
	 * For example, "Feat|Awesome Divinity" to attach a domain to a feat
	 *
	 * This method should NOT be called outside of file i/o routines
	 * DO NOT perform comparisons on this String
	 *
	 * @return String the source of the domain
	 */
	private String getDomainSourcePcgString(Domain domain)
	{
		final StringBuffer buff = new StringBuffer(30);
		ClassSource source = thePC.getDomainSource(domain);
		buff.append("PCClass");
		buff.append('|');
		buff.append(source.getPcclass().getKeyName());

		if (source.getLevel() > 0)
		{
			buff.append('|');
			buff.append(source.getLevel());
		}

		return buff.toString();
	}

	/**
	 * For each EquipSet, check for a tempBonusList and if found, save each
	 * bonus
	 * 
	 * @param buffer
	 */
	private void appendEqSetBonuses(StringBuffer buffer)
	{
		for (EquipSet eSet : thePC.getEquipSet())
		{
			if (eSet.useTempBonusList())
			{
				buffer.append(TAG_EQSETBONUS).append(':');
				buffer.append(eSet.getIdPath());

				List<String> trackList = new ArrayList<String>();

				for (Map.Entry<BonusObj, BonusManager.TempBonusInfo> me : eSet
						.getTempBonusMap().entrySet())
				{
					BonusObj bObj = me.getKey();
					TempBonusInfo tbi = me.getValue();
					Object cObj = tbi.source;
					Object tObj = tbi.target;
					final String aName = tempBonusName(cObj, tObj);

					if (trackList.contains(aName))
					{
						continue;
					}

					trackList.add(aName);

					buffer.append('|');
					buffer.append(TAG_TEMPBONUSBONUS).append(':');
					buffer.append(EntityEncoder.encode(aName));
				}

				buffer.append(LINE_SEP);
			}
		}
	}

	private void appendEquipmentLines(StringBuffer buffer)
	{
		for (final Equipment eq : thePC.getEquipmentMasterList())
		{
			buffer.append(TAG_EQUIPNAME).append(':');
			buffer.append(EntityEncoder.encode(eq.getName()));
			buffer.append('|');
			buffer.append(TAG_OUTPUTORDER).append(':');
			buffer.append(eq.getOutputIndex());
			buffer.append('|');
			buffer.append(TAG_COST).append(':');
			buffer.append(eq.getCost(thePC).toString());
			buffer.append('|');
			buffer.append(TAG_WT).append(':');
			buffer.append(eq.getWeight(thePC).toString());
			buffer.append('|');
			buffer.append(TAG_QUANTITY).append(':');
			buffer.append(eq.qty());

			final String customization = eq.formatSaveLine('$', '=').trim();
			final int delimiterIndex = customization.indexOf('$');

			if ((customization.length() > 0) && (delimiterIndex >= 0))
			{
				buffer.append('|');
				buffer.append(TAG_CUSTOMIZATION).append(':');
				buffer.append('[');
				buffer.append(TAG_BASEITEM).append(':');
				buffer.append(EntityEncoder.encode(customization.substring(0,
					delimiterIndex)));
				buffer.append('|');
				buffer.append(TAG_DATA).append(':');
				buffer.append(EntityEncoder.encode(customization
					.substring(delimiterIndex + 1)));
				buffer.append(']');
			}

			buffer.append(LINE_SEP);
		}
	}

	private void appendEquipmentSetLines(StringBuffer buffer)
	{
		// Output all the EquipSets
		final List<EquipSet> eqSetList = new ArrayList<EquipSet>(thePC.getEquipSet());
		Collections.sort(eqSetList);

		for (EquipSet eqSet : eqSetList)
		{
			buffer.append(TAG_EQUIPSET).append(':');
			buffer.append(EntityEncoder.encode(eqSet.getName()));
			buffer.append('|');
			buffer.append(TAG_ID).append(':');
			buffer.append(eqSet.getIdPath());

			if (eqSet.getValue().length() > 0)
			{
				buffer.append('|');
				buffer.append(TAG_VALUE).append(':');
				buffer.append(EntityEncoder.encode(eqSet.getValue()));
				buffer.append('|');
				buffer.append(TAG_QUANTITY).append(':');
				buffer.append(eqSet.getQty());
			}

			if (eqSet.getNote().length() > 0)
			{
				buffer.append('|');
				buffer.append(TAG_NOTE).append(':');
				buffer.append(eqSet.getNote());
			}

			if (eqSet.getUseTempMods())
			{
				buffer.append('|');
				buffer.append(TAG_USETEMPMODS).append(':');
				buffer.append(eqSet.getUseTempMods() ? 'Y' : 'N');
			}

			buffer.append(LINE_SEP);
		}

		// Then output EquipSet used for "working" equipmentList
		final String calcEquipSet = thePC.getCalcEquipSetId();
		buffer.append(TAG_CALCEQUIPSET).append(':');
		buffer.append(calcEquipSet);
		buffer.append(LINE_SEP);
	}

	private void appendEyeColorLine(StringBuffer buffer)
	{
		buffer.append(TAG_EYECOLOR).append(':');
		buffer.append(EntityEncoder.encode(thePC.getEyeColor()));
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Character Feats methods
	 * Only need to output pool, other FEAT info is stored in the ABILITY lines.
	 * ###############################################################
	 */
	private void appendFeatLines(StringBuffer buffer)
	{
		buffer.append(TAG_FEATPOOL).append(':');
		buffer.append(thePC.getRemainingFeatPoints(false));
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Character Ability methods
	 * ###############################################################
	 */
	private void appendAbilityLines(StringBuffer buffer)
	{
		ArrayList<AbilityCategory> categories = new ArrayList<AbilityCategory>(
				getGameMode().getAllAbilityCategories());
		categories.add(AbilityCategory.LANGBONUS);
		
		for (final AbilityCategory cat : categories)
		{
			final List<Ability> abilitiesToSave =
					new ArrayList<Ability>(thePC.getRealAbilitiesList(cat));
			for (final Ability vability : thePC.getVirtualAbilityList(cat))
			{
				Boolean needsSaving = thePC.getAssoc(vability, AssociationKey.NEEDS_SAVING);
				if (needsSaving != null && needsSaving)
				{
					abilitiesToSave.add(vability);
				}
			}
			// ABILITY:FEAT|NORMAL|Feat Key|APPLIEDTO:xxx|TYPE:xxx|SAVE:xxx|DESC:xxx
			for (final Ability ability : abilitiesToSave)
			{
				buffer.append(TAG_ABILITY).append(TAG_END);
				buffer.append(EntityEncoder.encode(cat.getKeyName())).append(
					TAG_SEPARATOR);
				buffer.append(TAG_TYPE).append(TAG_END);
				buffer
					.append(
						EntityEncoder.encode(thePC.getAbilityNature(cat, ability)
							.toString())).append(TAG_SEPARATOR);
				buffer.append(TAG_CATEGORY).append(TAG_END);
				buffer.append(EntityEncoder.encode(ability.getCategory()))
					.append(TAG_SEPARATOR);
				buffer.append(TAG_MAPKEY).append(TAG_END);
				buffer.append(EntityEncoder.encode(ability.getKeyName()))
					.append(TAG_SEPARATOR);
				if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
				{
					buffer.append(TAG_APPLIEDTO).append(TAG_END);
					List<FixedStringList> assocList =
							thePC.getDetailedAssociations(ability);
					boolean first = true;
					for (FixedStringList assocArray : assocList)
					{
						if (assocArray.size() > 1)
						{
							buffer.append(TAG_MULTISELECT).append(':');
						}
						for (String assoc : assocArray)
						{
							if (!first)
							{
								buffer.append(Constants.COMMA);
							}
							first = false;
							buffer.append(EntityEncoder.encode(assoc));
						}
					}
					buffer.append(TAG_SEPARATOR);
				}
				buffer.append(TAG_TYPE).append(TAG_END);
				buffer.append(EntityEncoder.encode(ability.getType()));

				for (final BonusObj save : thePC.getAddedBonusList(ability))
				{
					if (save.saveToPCG())
					{
						buffer.append('|');
						buffer.append(TAG_SAVE).append(':');
						buffer.append(EntityEncoder.encode("BONUS|" + save));
					}
				}

				for (final Description desc : ability
					.getSafeListFor(ListKey.DESCRIPTION))
				{
					buffer.append(Constants.PIPE);
					buffer.append(TAG_DESC).append(':');
					buffer.append(EntityEncoder.encode(desc.getPCCText()));
				}

				buffer.append(LINE_SEP);
			}
			buffer.append(TAG_USERPOOL).append(TAG_END);
			buffer.append(EntityEncoder.encode(cat.getKeyName())).append(
				TAG_SEPARATOR);
			buffer.append(TAG_POOLPOINTS).append(TAG_END);
			buffer.append(thePC.getUserPoolBonus(cat));
			buffer.append(LINE_SEP);
		}
	}

	/*
	 * ###############################################################
	 * Character Follower methods
	 * ###############################################################
	 */
	private void appendFollowerLines(StringBuffer buffer)
	{
		final Follower aMaster = thePC.getMaster();

		if (aMaster != null)
		{
			buffer.append(TAG_MASTER).append(':');
			buffer.append(EntityEncoder.encode(aMaster.getName()));
			buffer.append('|');
			buffer.append(TAG_TYPE).append(':');
			buffer.append(EntityEncoder.encode(aMaster.getType().getKeyName()));
			buffer.append('|');
			buffer.append(TAG_HITDICE).append(':');
			buffer.append(aMaster.getUsedHD());
			buffer.append('|');
			buffer.append(TAG_FILE).append(':');
			buffer.append(EntityEncoder.encode(aMaster.getRelativeFileName()));
			buffer.append('|');
			buffer.append(TAG_ADJUSTMENT).append(':');
			buffer.append(aMaster.getAdjustment());
			buffer.append(LINE_SEP);
		}

		if (thePC.hasFollowers())
		{
			for (Follower follower : thePC.getFollowerList())
			{
				buffer.append(TAG_FOLLOWER).append(':');
				buffer.append(EntityEncoder.encode(follower.getName()));
				buffer.append('|');
				buffer.append(TAG_TYPE).append(':');
				buffer.append(EntityEncoder.encode(follower.getType()
					.getKeyName()));
				buffer.append('|');
				buffer.append(TAG_RACE).append(':');
				buffer.append(EntityEncoder.encode(follower.getRace()
						.getKeyName().toUpperCase()));
				buffer.append('|');
				buffer.append(TAG_HITDICE).append(':');
				buffer.append(follower.getUsedHD());
				buffer.append('|');
				buffer.append(TAG_FILE).append(':');
				buffer.append(EntityEncoder.encode(follower
					.getRelativeFileName()));
				buffer.append(LINE_SEP);
			}
		}
	}

	private void appendGenderLine(StringBuffer buffer)
	{
		buffer.append(TAG_GENDER).append(':');
		buffer.append(EntityEncoder.encode(thePC.getGenderObject().toString()));
		buffer.append(LINE_SEP);
	}

	private void appendHairColorLine(StringBuffer buffer)
	{
		buffer.append(TAG_HAIRCOLOR).append(':');
		buffer.append(EntityEncoder.encode(thePC.getHairColor()));
		buffer.append(LINE_SEP);
	}

	private void appendHairStyleLine(StringBuffer buffer)
	{
		buffer.append(TAG_HAIRSTYLE).append(':');
		buffer.append(EntityEncoder.encode(thePC.getHairStyle()));
		buffer.append(LINE_SEP);
	}

	private void appendHandedLine(StringBuffer buffer)
	{
		buffer.append(TAG_HANDED).append(':');
		buffer.append(EntityEncoder.encode(thePC.getHanded()));
		buffer.append(LINE_SEP);
	}

	private void appendInterestsLine(StringBuffer buffer)
	{
		buffer.append(TAG_INTERESTS).append(':');
		buffer.append(EntityEncoder.encode(thePC.getInterests()));
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Kit Information methods
	 * ###############################################################
	 */
	/*
	 * #Kits
	 * KIT:KitType|Region|KitName
	 *
	 * TODO: Do we need to support the below?
	 * KIT:KitName|TYPE:KitType|REGION:Region
	 */
	private void appendKitLines(StringBuffer buffer)
	{
		for (final Kit kit : thePC.getKitInfo())
		{
			buffer.append(TAG_KIT).append(':').append(kit.getKeyName())
				.append(LINE_SEP);
		}
	}

	/*
	 * ###############################################################
	 * Character Language methods
	 * ###############################################################
	 */
	private void appendLanguageLine(StringBuffer buffer)
	{
		String del = Constants.EMPTY_STRING;

		for (final Language lang : thePC.getLanguageSet())
		{
			buffer.append(del);
			buffer.append(TAG_LANGUAGE).append(':');
			buffer.append(EntityEncoder.encode(lang.getKeyName()));
			del = "|"; //$NON-NLS-1$
		}

		buffer.append(LINE_SEP);
	}

	private void appendLocationLine(StringBuffer buffer)
	{
		buffer.append(TAG_LOCATION).append(':');
		buffer.append(EntityEncoder.encode(thePC.getLocation()));
		buffer.append(LINE_SEP);
	}

	/**
	 * Convenience Method
	 *
	 * <br>author: Thomas Behr 19-03-02
	 *
	 * @param buffer
	 */
	private static void appendNewline(StringBuffer buffer)
	{
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Character Notes Tab methods
	 * ###############################################################
	 */
	private void appendNotesLines(StringBuffer buffer)
	{
		for (NoteItem ni : thePC.getNotesList())
		{
			buffer.append(TAG_NOTE).append(':');
			buffer.append(EntityEncoder.encode(ni.getName()));
			buffer.append('|');
			buffer.append(TAG_ID).append(':');
			buffer.append(ni.getId());
			buffer.append('|');
			buffer.append(TAG_PARENTID).append(':');
			buffer.append(ni.getParentId());
			buffer.append('|');
			buffer.append(TAG_VALUE).append(':');
			buffer.append(EntityEncoder.encode(ni.getValue()));
			buffer.append(LINE_SEP);
		}
	}

	private void appendPersonalityTrait1Line(StringBuffer buffer)
	{
		buffer.append(TAG_PERSONALITYTRAIT1).append(':');
		buffer.append(EntityEncoder.encode(thePC.getTrait1()));
		buffer.append(LINE_SEP);
	}

	private void appendPersonalityTrait2Line(StringBuffer buffer)
	{
		buffer.append(TAG_PERSONALITYTRAIT2).append(':');
		buffer.append(EntityEncoder.encode(thePC.getTrait2()));
		buffer.append(LINE_SEP);
	}

	private void appendPhobiasLine(StringBuffer buffer)
	{
		buffer.append(TAG_PHOBIAS).append(':');
		buffer.append(EntityEncoder.encode(thePC.getPhobias()));
		buffer.append(LINE_SEP);
	}

	//private void appendUnlimitedPoolCheckedLine(StringBuffer buffer)
	//{
	//buffer.append(TAG_UNLIMITEDPOOLCHECKED).append(':');
	//buffer.append((SettingsHandler.isStatPoolUnlimited()) ? "Y" : "N");
	//buffer.append(LINE_SEP);
	//}
	private void appendPoolPointsLine(StringBuffer buffer)
	{
		buffer.append(TAG_POOLPOINTS).append(':');
		buffer.append(thePC.getPoolAmount());
		buffer.append(LINE_SEP);
		buffer.append(TAG_POOLPOINTSAVAIL).append(':');
		buffer.append(thePC.getPointBuyPoints());
		buffer.append(LINE_SEP);
	}

	private static void appendTabLabelLine(StringBuffer buffer)
	{
		buffer.append(TAG_TABLABEL).append(':');
		buffer.append(SettingsHandler.getNameDisplayStyle());
		buffer.append(LINE_SEP);
	}

	private void appendAutoSortLines(StringBuffer buffer)
	{
		buffer.append(TAG_AUTOSORTGEAR).append(':');
		buffer.append(thePC.isAutoSortGear() ? 'Y' : 'N');
		buffer.append(LINE_SEP);
		buffer.append(TAG_SKILLSOUTPUTORDER).append(':');
		buffer.append(thePC.getSkillsOutputOrder());
		buffer.append(LINE_SEP);
	}

	private void appendGearCostSizeLines(StringBuffer buffer)
	{
		buffer.append(TAG_IGNORECOST).append(':');
		buffer.append(thePC.isIgnoreCost() ? 'Y' : 'N');
		buffer.append(LINE_SEP);
		buffer.append(TAG_ALLOWDEBT).append(':');
		buffer.append(thePC.isAllowDebt() ? 'Y' : 'N');
		buffer.append(LINE_SEP);
		buffer.append(TAG_AUTORESIZEGEAR).append(':');
		buffer.append(thePC.isAutoResize() ? 'Y' : 'N');
		buffer.append(LINE_SEP);
	}

	private void appendAutoSpellsLine(StringBuffer buffer)
	{
		buffer.append(TAG_AUTOSPELLS).append(':');
		buffer.append(thePC.getAutoSpells() ? 'Y' : 'N');
		buffer.append(LINE_SEP);
	}

	/**
	 * Append the settings related to higher level slot use for spells.
	 * @param buffer The buffer to append to.
	 */
	private void appendUseHigherSpellSlotsLines(StringBuffer buffer)
	{
		buffer.append(TAG_USEHIGHERKNOWN).append(':');
		buffer.append(thePC.getUseHigherKnownSlots() ? 'Y' : 'N');
		buffer.append(LINE_SEP);
		buffer.append(TAG_USEHIGHERPREPPED).append(':');
		buffer.append(thePC.getUseHigherPreppedSlots() ? 'Y' : 'N');
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Character Bio methods
	 * ###############################################################
	 */
	private void appendCharacterNameLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERNAME).append(':');
		buffer.append(EntityEncoder.encode(thePC.getName()));
		buffer.append(LINE_SEP);
	}

	private void appendHeightLine(StringBuffer buffer)
	{
		buffer.append(TAG_HEIGHT).append(':');
		buffer.append(thePC.getHeight());
		buffer.append(LINE_SEP);
	}

	private void appendLoadCompanionLine(StringBuffer buffer)
	{
		buffer.append(TAG_LOADCOMPANIONS).append(':');
		buffer.append(thePC.getLoadCompanion() ? 'Y' : 'N');
		buffer.append(LINE_SEP);
	}

	private void appendOutputSheetsLines(StringBuffer buffer)
	{
		if (SettingsHandler.getSaveOutputSheetWithPC())
		{
			buffer.append(TAG_HTMLOUTPUTSHEET).append(':');
			buffer.append(EntityEncoder.encode(SettingsHandler
				.getSelectedCharacterHTMLOutputSheet(null)));
			buffer.append(LINE_SEP);
			buffer.append(TAG_PDFOUTPUTSHEET).append(':');
			buffer.append(EntityEncoder.encode(SettingsHandler
				.getSelectedCharacterPDFOutputSheet(null)));
			buffer.append(LINE_SEP);
		}
	}

	private void appendPlayerNameLine(StringBuffer buffer)
	{
		buffer.append(TAG_PLAYERNAME).append(':');
		buffer.append(EntityEncoder.encode(thePC.getPlayersName()));
		buffer.append(LINE_SEP);
	}

	private void appendPortraitLine(StringBuffer buffer)
	{
		buffer.append(TAG_PORTRAIT).append(':');
		buffer.append(EntityEncoder.encode(thePC.getPortraitPath()));
		buffer.append(LINE_SEP);
		
		Rectangle rect = thePC.getPortraitThumbnailRect();
		if (rect != null)
		{
			buffer.append(TAG_PORTRAIT_THUMBNAIL_RECT).append(':');
			buffer.append(rect.x).append(',');
			buffer.append(rect.y).append(',');
			buffer.append(rect.width).append(',');
			buffer.append(rect.height);
			buffer.append(LINE_SEP);
		}
	}

	/**
	 * @param buffer
	 */
	private void appendRaceLine(StringBuffer buffer)
	{
		buffer.append(TAG_RACE).append(':');
		buffer.append(EntityEncoder.encode(thePC.getRace().getKeyName()));
		List<FixedStringList> assocList =
				thePC.getDetailedAssociations(thePC.getRace());
		if (assocList != null && !assocList.isEmpty())
		{
			buffer.append(TAG_SEPARATOR);
			buffer.append(TAG_APPLIEDTO).append(TAG_END);
			boolean first = true;
			for (FixedStringList assocArray : assocList)
			{
				if (assocArray.size() > 1)
				{
					buffer.append(TAG_MULTISELECT).append(':');
				}
				for (String assoc : assocArray)
				{
					if (!first)
					{
						buffer.append(Constants.COMMA);
					}
					first = false;
					buffer.append(EntityEncoder.encode(assoc));
				}
			}
		}

		buffer.append(LINE_SEP);
	}

	private void appendFavoredClassLine(StringBuffer buffer)
	{
		PCClass sfc = thePC.getLegacyFavoredClass();
		if (sfc != null)
		{
			buffer.append(TAG_FAVOREDCLASS).append(':');
			buffer.append(EntityEncoder.encode(sfc.getKeyName()));
			buffer.append(LINE_SEP);
		}
	}

	private void appendResidenceLine(StringBuffer buffer)
	{
		buffer.append(TAG_CITY).append(':');
		buffer.append(EntityEncoder.encode(thePC.getResidence()));
		buffer.append(LINE_SEP);
	}

	private void appendSkinColorLine(StringBuffer buffer)
	{
		buffer.append(TAG_SKINCOLOR).append(':');
		buffer.append(EntityEncoder.encode(thePC.getSkinColor()));
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Miscellaneous methods
	 * ###############################################################
	 */
	/*
	 * currently source is either empty or
	 * PCCLASS|classname|classlevel (means it's a chosen special ability)
	 * PCCLASS=classname|classlevel (means it's a defined special ability)
	 * DEITY=deityname|totallevels
	 */
	private static void appendSourceInTaggedFormat(StringBuffer buffer,
		String source)
	{
		final StringTokenizer tokens = new StringTokenizer(source, "|="); //$NON-NLS-1$
		buffer.append(TAG_SOURCE).append(':');
		buffer.append('[');
		buffer.append(TAG_TYPE).append(':');
		buffer.append(tokens.nextToken());
		buffer.append('|');
		buffer.append(TAG_NAME).append(':');
		buffer.append(tokens.nextToken());

		if (tokens.hasMoreTokens())
		{
			buffer.append('|');
			buffer.append(TAG_LEVEL).append(':');
			buffer.append(tokens.nextToken());
		}

		if (source.indexOf('=') >= 0)
		{
			buffer.append('|');
			buffer.append(TAG_DEFINED).append(':');
			buffer.append('Y');
		}

		buffer.append(']');
	}

	/*
	 * currently source is either empty or
	 * PCCLASS|classname|classlevel (means it's a chosen special ability)
	 * PCCLASS=classname|classlevel (means it's a defined special ability)
	 * DEITY=deityname|totallevels
	 */
	private static void appendSourceInTaggedFormat(StringBuffer buffer,
			CDOMObject source)
	{
		buffer.append(TAG_SOURCE).append(':');
		buffer.append('[');
		buffer.append(TAG_TYPE).append(':');

		// I love reflection :-)
		final Class<? extends CDOMObject> srcClass = source.getClass();
		final String pckName = srcClass.getPackage().getName();
		final String srcName =
				srcClass.getName().substring(pckName.length() + 1);

		buffer.append(srcName.toUpperCase());
		buffer.append('|');
		buffer.append(TAG_NAME).append(':');
		buffer.append(source.getKeyName());
		buffer.append(']');
	}

	private static void appendSpecials(StringBuffer buffer,
		List<String> specials, String tag_group, String tag_item, int lvl)
	{
		if ((specials != null) && (!specials.isEmpty()))
		{
			buffer.append('|');
			buffer.append(tag_group).append(':');
			buffer.append('[');

			String del = Constants.EMPTY_STRING;

			for (String special : specials)
			{
				buffer.append(del);
				buffer.append(tag_item).append(':');
				buffer.append(EntityEncoder.encode(special));

				if (lvl == -1)
				{
					buffer.append(":-1"); //$NON-NLS-1$
				}

				del = "|"; //$NON-NLS-1$
			}

			buffer.append(']');
		}
	}

	/*
	 * ###############################################################
	 * Character Experience methods
	 * ###############################################################
	 */
	private void appendExperienceLine(StringBuffer buffer)
	{
		buffer.append(TAG_EXPERIENCE).append(':');
		buffer.append(thePC.getXP());
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Character Region method
	 * ###############################################################
	 */
	private void appendRegionLine(StringBuffer buffer)
	{
		final String r = thePC.getRegionString();

		if (r != null)
		{
			buffer.append(TAG_REGION).append(':').append(r).append(LINE_SEP);
		}
	}

	/*
	 * ###############################################################
	 * Character Skills methods
	 * ###############################################################
	 */
	private void appendSkillLines(StringBuffer buffer)
	{
		int includeSkills = SettingsHandler.getIncludeSkills();

		if (includeSkills == 3)
		{
			includeSkills = SettingsHandler.getSkillsTab_IncludeSkills();
		}

		thePC.populateSkills(includeSkills);

		for (Skill skill : thePC.getSkillSet())
		{
			Integer outputIndex =
					thePC.getAssoc(skill, AssociationKey.OUTPUT_INDEX);
			if ((SkillRankControl.getRank(thePC, skill).doubleValue() > 0)
				|| (outputIndex != null && outputIndex != 0))
			{
				buffer.append(TAG_SKILL).append(':');
				buffer.append(EntityEncoder.encode(skill.getKeyName()));

				buffer.append('|');
				buffer.append(TAG_OUTPUTORDER).append(':');
				buffer.append(outputIndex == null ? 0 : outputIndex);
				buffer.append('|');

				List<NamedValue> rankList =
						thePC
							.getAssocList(skill, AssociationListKey.SKILL_RANK);
				if (rankList != null)
				{
					for (NamedValue sd : rankList)
					{
						final PCClass pcClass = thePC.getClassKeyed(sd.name);

						buffer.append(TAG_CLASSBOUGHT).append(':');
						buffer.append('[');
						buffer.append(TAG_CLASS).append(':');
						buffer.append(EntityEncoder.encode(sd.name));
						buffer.append('|');
						buffer.append(TAG_RANKS).append(':');
						buffer.append(sd.getWeight());
						buffer.append('|');
						buffer.append(TAG_COST).append(':');
						buffer.append(Integer.toString(thePC
							.getSkillCostForClass(skill, pcClass).getCost()));
						buffer.append('|');
						buffer.append(TAG_CLASSSKILL).append(':');
						buffer.append((thePC.isClassSkill(skill, pcClass))
							? 'Y' : 'N');
						buffer.append(']');
					}
				}

				for (String assoc : thePC.getAssociationList(skill))
				{
					buffer.append('|');
					buffer.append(TAG_ASSOCIATEDDATA).append(':');
					buffer.append(EntityEncoder.encode(assoc));
				}

				appendLevelAbilityInfo(buffer, skill);

				buffer.append(LINE_SEP);
			}
		}
	}

	private void appendSpeechPatternLine(StringBuffer buffer)
	{
		buffer.append(TAG_SPEECHPATTERN).append(':');
		buffer.append(EntityEncoder.encode(thePC.getSpeechTendency()));
		buffer.append(LINE_SEP);
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
	private void appendSpellBookLines(StringBuffer buffer)
	{
		for (SpellBook book : thePC.getSpellBooks())
		{
			String bookName = book.getName();
			if (!bookName.equals(Globals.getDefaultSpellBook())
				&& !bookName.equals(Globals.INNATE_SPELL_BOOK_NAME))
			{
				buffer.append(TAG_SPELLBOOK).append(':');
				buffer.append(book.getName());
				buffer.append('|');
				buffer.append(TAG_TYPE).append(':');
				buffer.append(book.getType());
				if (book.getName().equals(
					thePC.getSpellBookNameToAutoAddKnown()))
				{
					buffer.append('|');
					buffer.append(TAG_AUTOADDKNOWN).append(':');
					buffer.append('Y');
				}
				buffer.append(LINE_SEP);
			}

		}
	}

	/*
	 * ###############################################################
	 * Character Spells Information methods
	 * ###############################################################
	 */
	/*
	 * #Character Spells Information
	 * CLASS:Wizard|CANCASTPERDAY:2,4(Totals the levels all up + includes attribute bonuses)
	 * SPELLNAME:Blah|SCHOOL:blah|SUBSCHOOL:blah|Etc
	 *
	 * completely changed due to new Spell API
	 */
	private void appendSpellLines(StringBuffer buffer)
	{
		String del;

		for (PCClass pcClass : thePC.getClassSet())
		{
			for (CharacterSpell cSpell : thePC.getCharacterSpells(pcClass))
			{
				for (SpellInfo spellInfo : cSpell.getInfoList())
				{
					CDOMObject owner = cSpell.getOwner();
					List<? extends CDOMList<Spell>> lists =
							thePC.getSpellLists(owner);

					if (SpellLevel.getFirstLevelForKey(
						cSpell.getSpell(), lists, thePC) < 0)
					{
						Logging.errorPrint("Ignoring unqualified spell " + cSpell.getSpell() + " in list for class " + pcClass + ".");
						continue;
					}
					if (spellInfo.getBook().equals(
						Globals.getDefaultSpellBook())
						&& thePC.getSpellSupport(pcClass).isAutoKnownSpell(cSpell.getSpell(), SpellLevel.getFirstLevelForKey(
						cSpell.getSpell(), lists, thePC), false, thePC)
						&& thePC.getAutoSpells())
					{
						continue;
					}

					buffer.append(TAG_SPELLNAME).append(':');
					buffer.append(EntityEncoder.encode(cSpell.getSpell()
						.getKeyName()));
					buffer.append('|');
					buffer.append(TAG_TIMES).append(':');
					buffer.append(spellInfo.getTimes());
					buffer.append('|');
					buffer.append(TAG_CLASS).append(':');
					buffer.append(EntityEncoder.encode(pcClass.getKeyName()));
					buffer.append('|');
					buffer.append(TAG_SPELL_BOOK).append(':');
					buffer.append(EntityEncoder.encode(spellInfo.getBook()));
					buffer.append('|');
					buffer.append(TAG_SPELLLEVEL).append(':');
					buffer.append(spellInfo.getActualLevel());
					if (Globals.hasSpellPPCost())
					{
						buffer.append('|');
						buffer.append(TAG_SPELLPPCOST).append(':');
						buffer.append(spellInfo.getActualPPCost());
					}
					if (spellInfo.getNumPages() > 0)
					{
						buffer.append('|');
						buffer.append(TAG_SPELLNUMPAGES).append(':');
						buffer.append(spellInfo.getNumPages());
					}

					final List<Ability> metaFeats = spellInfo.getFeatList();

					if ((metaFeats != null) && (!metaFeats.isEmpty()))
					{
						buffer.append('|');
						buffer.append(TAG_FEATLIST).append(':');
						buffer.append('[');
						del = Constants.EMPTY_STRING;

						for (Ability feat : metaFeats)
						{
							buffer.append(del);
							buffer.append(TAG_FEAT).append(':');
							buffer.append(EntityEncoder.encode(feat
								.getKeyName()));
							del = "|"; //$NON-NLS-1$
						}

						buffer.append(']');
					}

					buffer.append('|');
					appendSourceInTaggedFormat(buffer,
						StringPClassUtil.getStringFor(owner.getClass()) + "|"
							+ owner.getKeyName());
					buffer.append(LINE_SEP);
				}
			}
		}
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
	private void appendSpellListLines(StringBuffer buffer)
	{
		for (PCClass pcClass : thePC.getClassSet())
		{
			TransitionChoice<CDOMListObject<Spell>> csc =
					pcClass.get(ObjectKey.SPELLLIST_CHOICE);
			if (csc != null)
			{
				List<? extends CDOMList<Spell>> assocList =
						thePC.getSpellLists(pcClass);
				buffer.append(TAG_SPELLLIST).append(':');
				buffer.append(pcClass.getKeyName());

				for (CDOMList<Spell> spell : assocList)
				{
					buffer.append('|');
					if (ClassSpellList.class.equals(spell.getClass()))
					{
						buffer.append("CLASS");
					}
					else
					{
						buffer.append("DOMAIN");
					}
					buffer.append('.').append(spell.getLSTformat());
				}

				buffer.append(LINE_SEP);
			}
		}
	}

	/*
	 * ###############################################################
	 * Character Attributes methods
	 * ###############################################################
	 */
	private void appendStatLines(StringBuffer buffer)
	{
		for (PCStat aStat : thePC.getStatSet())
		{
			buffer.append(TAG_STAT).append(':');
			buffer.append(aStat.getAbb());
			buffer.append('|');
			buffer.append(TAG_SCORE).append(':');
			buffer.append((int) thePC.getAssoc(aStat, AssociationKey.STAT_SCORE));
			buffer.append(LINE_SEP);
		}
	}

	private void appendTabNameLine(StringBuffer buffer)
	{
		buffer.append(TAG_TABNAME).append(':');
		buffer.append(EntityEncoder.encode(thePC.getTabName()));
		buffer.append(LINE_SEP);
	}

	private void appendTempBonuses(StringBuffer buffer)
	{
		final List<String> trackList = new ArrayList<String>();
		for (BonusManager.TempBonusInfo tbi : thePC.getTempBonusMap().values())
		{
			Object creObj = tbi.source;
			Object tarObj = tbi.target;
			final String outString = tempBonusName(creObj, tarObj);
			if (trackList.contains(outString))
			{
				continue;
			}
			trackList.add(outString);
			buffer.append(outString);
			
			/*
			 * Why do we loop through the bonuses again? It is looped through
			 * again so that only items associated with this source (e.g.
			 * Template and Target object) are written, but that ALL of the
			 * items are written on one line.
			 */
			for (Map.Entry<BonusObj, BonusManager.TempBonusInfo> subme : thePC
					.getTempBonusMap().entrySet())
			{
				BonusObj subBonus = subme.getKey();
				TempBonusInfo subtbi = subme.getValue();
				Object cObj = subtbi.source;
				Object tObj = subtbi.target;
				final String inString = tempBonusName(cObj, tObj);
				if (inString.equals(outString))
				{
					buffer.append('|');
					buffer.append(TAG_TEMPBONUSBONUS).append(':');
					buffer.append(EntityEncoder.encode(subBonus.getPCCText()));
				}
			}

			buffer.append(LINE_SEP);
		}
	}

	/*
	 * ###############################################################
	 * Character Templates methods
	 * ###############################################################
	 */
	private void appendTemplateLines(StringBuffer buffer)
	{
		for (PCTemplate template : thePC.getTemplateSet())
		{
			//
			// TEMPLATESAPPLIED:[NAME:<template_name>]
			// TEMPLATESAPPLIED:[NAME:<template_name>|CHOSENFEAT:[KEY:<key>|VALUE:<value>]CHOSENFEAT:[KEY:<key>|VALUE:<value>]...CHOSENFEAT:[KEY:<key>|VALUE:<value>]]
			//
			buffer.append(TAG_TEMPLATESAPPLIED).append(':').append('[');
			buffer.append(TAG_NAME).append(':').append(
				EntityEncoder.encode(template.getKeyName()));

			final String chosenFeats = chosenFeats(template);

			if (chosenFeats.length() != 0)
			{
				buffer.append('|').append(chosenFeats);
			}

			//
			// Save list of template names 'owned' by current template
			//
			Collection<PCTemplate> templatesAdded =
					thePC.getTemplatesAdded(template);
			if (templatesAdded != null)
			{
				for (PCTemplate ownedTemplate : templatesAdded)
				{
					buffer.append('|').append(TAG_CHOSENTEMPLATE).append(':')
						.append('[');
					buffer.append(TAG_NAME).append(':').append(
						EntityEncoder.encode(ownedTemplate.getKeyName()));
					buffer.append(']');
				}
			}
			List<FixedStringList> assocList =
					thePC.getDetailedAssociations(template);
			if (assocList != null && !assocList.isEmpty())
			{
				buffer.append(TAG_SEPARATOR);
				buffer.append(TAG_APPLIEDTO).append(TAG_END);
				boolean first = true;
				for (FixedStringList assocArray : assocList)
				{
					if (assocArray.size() > 1)
					{
						buffer.append(TAG_MULTISELECT).append(':');
					}
					for (String assoc : assocArray)
					{
						if (!first)
						{
							buffer.append(Constants.COMMA);
						}
						first = false;
						buffer.append(EntityEncoder.encode(assoc));
					}
				}
			}

			buffer.append(']').append(LINE_SEP);
		}
	}

	private void appendUseTempModsLine(StringBuffer buffer)
	{
		buffer.append(TAG_USETEMPMODS).append(':');
		buffer.append(thePC.getUseTempMods() ? 'Y' : 'N');
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Character Weapon proficiencies methods
	 * ###############################################################
	 */
	private void appendWeaponProficiencyLines(StringBuffer buffer)
	{
		final int size = thePC.getWeaponProfSet().size();

		if (size > 0)
		{
			/*
			 * since aPC.getWeaponProfList() returns a TreeSet,
			 * we have to put them into an array first.
			 * we do not use TreeSet's toArray()-method since it
			 * makes no guarantees on element order.
			 *
			 * author: Thomas Behr 08-09-02
			 */
			final String[] weaponProficiencies = new String[size];

			int j = 0;

			for (WeaponProf wp : thePC.getSortedWeaponProfs())
			{
				weaponProficiencies[j++] = wp.getKeyName();
			}

			// as per Mynex's request do not write more than 10 weapons per line
			final int step = 10;
			final int times = (size / step) + (((size % step) > 0) ? 1 : 0);

			for (int k = 0; k < times; ++k)
			{
				buffer.append(TAG_WEAPONPROF).append(':');
				buffer.append('[');

				String del = Constants.EMPTY_STRING;
				int stop = Math.min(size, (k * step) + 10);

				for (int i = k * step; i < stop; ++i)
				{
					buffer.append(del);
					buffer.append(TAG_WEAPON).append(':');
					buffer.append(EntityEncoder.encode(weaponProficiencies[i]));
					del = "|"; //$NON-NLS-1$
				}

				buffer.append(']');
				buffer.append(LINE_SEP);
			}
		}

		//
		// Save any selected racial bonus weapons
		//
		appendWeaponProficiencyLines(buffer, thePC.getRace());

		//
		// Save any selected template bonus weapons
		//
		for (PCTemplate pct : thePC.getTemplateSet())
		{
			appendWeaponProficiencyLines(buffer, pct);
		}

		//
		// Save any selected class bonus weapons
		//
		for (final PCClass pcClass : thePC.getClassSet())
		{
			appendWeaponProficiencyLines(buffer, pcClass);
		}

		//
		// Save any selected domain bonus weapons
		//
		for (final Domain d : thePC.getDomainSet())
		{
			appendWeaponProficiencyLines(buffer, d);
		}

		//
		// Save any selected feat bonus weapons
		//
		for (final Ability feat : thePC
			.getRealAbilitiesList(AbilityCategory.FEAT))
		{
			appendWeaponProficiencyLines(buffer, feat);
		}
	}

	private void appendWeaponProficiencyLines(StringBuffer buffer,
			CDOMObject source)
	{
		if (source == null)
		{
			return;
		}
		final List<? extends WeaponProf> profs = thePC.getBonusWeaponProfs(source);
		if (profs == null || profs.isEmpty())
		{
			return;
		}

		// TODO refactor this section and the code above that calls it so share this

		// As per Mynex's request do not write more than 10 weapons per line
		final int step = 10;
		final int times = (profs.size() / step) + 1;

		for (int k = 0; k < times; ++k)
		{
			buffer.append(TAG_WEAPONPROF).append(':');
			buffer.append('[');

			String del = Constants.EMPTY_STRING;
			int stop = Math.min(profs.size(), (k * step) + 10);

			for (int i = k * step; i < stop; ++i)
			{
				buffer.append(del);
				buffer.append(TAG_WEAPON).append(':');
				buffer.append(EntityEncoder.encode(profs.get(i).getLSTformat()));
				del = "|"; //$NON-NLS-1$
			}

			buffer.append(']');
			buffer.append('|');
			appendSourceInTaggedFormat(buffer, source);
			buffer.append(LINE_SEP);
		}
	}

	/*
	 * ###############################################################
	 * Character Equipment methods
	 * ###############################################################
	 */
	private void appendMoneyLine(StringBuffer buffer)
	{
		buffer.append(TAG_MONEY).append(':');
		buffer.append(thePC.getGold().toString());
		buffer.append(LINE_SEP);
	}

	private void appendWeightLine(StringBuffer buffer)
	{
		buffer.append(TAG_WEIGHT).append(':');
		buffer.append(thePC.getWeight());
		buffer.append(LINE_SEP);
	}

	private String chosenFeats(PCTemplate pct)
	{
		final StringBuilder aString = new StringBuilder(50);
		for (PCTemplate rlt : pct.getSafeListFor(ListKey.REPEATLEVEL_TEMPLATES))
		{
			for (PCTemplate lt : rlt.getSafeListFor(ListKey.LEVEL_TEMPLATES))
			{
				List<AbilitySelection> featList = thePC.getAssocList(lt,
						AssociationListKey.TEMPLATE_FEAT);
				if (featList != null)
				{
					writeTemplateFeat(aString, lt, featList);
				}
			}
		}
		for (PCTemplate lt : pct.getSafeListFor(ListKey.LEVEL_TEMPLATES))
		{
			List<AbilitySelection> featList = thePC.getAssocList(lt,
					AssociationListKey.TEMPLATE_FEAT);
			if (featList != null)
			{
				writeTemplateFeat(aString, lt, featList);
			}
		}

		for (PCTemplate lt : pct.getSafeListFor(ListKey.HD_TEMPLATES))
		{
			List<AbilitySelection> featList = thePC.getAssocList(lt,
					AssociationListKey.TEMPLATE_FEAT);
			if (featList != null)
			{
				writeTemplateFeat(aString, lt, featList);
			}
		}
		return aString.toString();
	}

	private void writeTemplateFeat(StringBuilder aString, PCTemplate pct, List<AbilitySelection> featList)
	{
		for (AbilitySelection s : featList)
		{
			if (aString.length() != 0)
			{
				aString.append('|');
			}

			String featKey = Compatibility.getKeyFor(pct);

			aString.append(TAG_CHOSENFEAT).append(':');
			aString.append('[');
			aString.append(TAG_MAPKEY).append(':').append(
				EntityEncoder.encode(featKey)).append('|');
			aString.append(TAG_MAPVALUE).append(':').append(
				EntityEncoder.encode(s.getPersistentFormat()));
			aString.append(']');
		}
	}

	/**
	 * Convenience Method
	 *
	 * <br>author: Thomas Behr 19-03-02
	 *
	 * @param s   the String which will be converted into a comment;
	 *            i.e. '#','\r' will be removed,
	 *                 '\t','\f' will be replaced with ' ',
	 *            and each line will start with "# "
	 * @return the newly created comment
	 */
	private static String createComment(String s)
	{
		String work = s + LINE_SEP;
		work = work.replace('\t', ' ');
		work = work.replace('\f', ' ');

		StringBuffer buffer = new StringBuffer();
		StringTokenizer tokens = new StringTokenizer(work, "#"); //$NON-NLS-1$

		while (tokens.hasMoreTokens())
		{
			buffer.append(tokens.nextToken());
		}

		work = buffer.toString();

		buffer = new StringBuffer();

		/*
		 * Need to keep the Windows line separator as newline delimiter to ensure
		 * cross-platform portability.
		 *
		 * author: Thomas Behr 2002-11-13
		 */
		tokens = new StringTokenizer(work, "\r\n"); //$NON-NLS-1$

		while (tokens.hasMoreTokens())
		{
			buffer.append("# ").append(tokens.nextToken()).append(LINE_SEP); //$NON-NLS-1$
		}

		return buffer.toString();
	}

	/**
	 * creates a unique tuple based on the creator and target getName()
	 * @param creator
	 * @param target
	 * @return temp bonus name
	 **/
	private String tempBonusName(final Object creator, Object target)
	{
		final StringBuffer cb = new StringBuffer();

		cb.append(TAG_TEMPBONUS).append(':');
		if (creator instanceof CDOMObject)
		{
			final CDOMObject oCreator = (CDOMObject) creator;

			if (oCreator instanceof Ability)
			{
				cb.append(TAG_FEAT).append('=');
			}
			else if (oCreator instanceof Spell)
			{
				cb.append(TAG_SPELL).append('=');
			}
			else if (oCreator instanceof Equipment)
			{
				cb.append(TAG_EQUIPMENT).append('=');
			}
			else if (oCreator instanceof PCClass)
			{
				cb.append(TAG_CLASS).append('=');
			}
			else if (oCreator instanceof PCTemplate)
			{
				cb.append(TAG_TEMPLATE).append('=');
			}
			else if (oCreator instanceof Skill)
			{
				cb.append(TAG_SKILL).append('=');
			}
			else
			{
				cb.append(TAG_ERROR).append('=');
			}

			cb.append(EntityEncoder.encode(oCreator.getKeyName()));
			// Hmm, need to get the Type of oCreater also?
			// Might be required so the PCGVer2Parser can search correct type to re-create
		}
		else
		{
			return Constants.EMPTY_STRING;
		}
		cb.append('|');
		cb.append(TAG_TEMPBONUSTARGET).append(':');

		if (target instanceof PlayerCharacter)
		{
			cb.append(TAG_PC);
		}
		else if (target instanceof Equipment)
		{
			cb.append(EntityEncoder.encode(((Equipment) target).getName()));
		}

		return cb.toString();
	}

	//
	// Remember what choices were made for each of the ADD: tags
	//
	private void appendLevelAbilityInfo(StringBuffer buffer, CDOMObject pObj)
	{
		appendAddTokenInfo(buffer, pObj);
	}

	private void appendAddTokenInfo(StringBuffer buffer, CDOMObject pObj)
	{
		List<PersistentTransitionChoice<?>> addList =
				pObj.getListFor(ListKey.ADD);
		if (addList == null)
		{
			return;
		}
		for (PersistentTransitionChoice<?> tc : addList)
		{
			addChoices(buffer, tc);
		}
	}

	private <T> void addChoices(StringBuffer buffer,
			PersistentTransitionChoice<T> tc)
	{
		List<Object> assocList = thePC.getAssocList(tc, AssociationListKey.ADD);
		if (assocList == null)
		{
			return;
		}
		//
		// |ADD:[PROMPT:SUBTOKEN|blah|CHOICE:choice1|CHOICE:choice2|CHOICE:choice3...]
		//
		SelectableSet<?> choices = tc.getChoices();
		buffer.append('|').append(TAG_ADDTOKEN).append(':').append('[');
		buffer.append(EntityEncoder.encode(choices.getName())).append(':');
		buffer.append(EntityEncoder.encode(choices.getLSTformat()));

		for (Object assoc : assocList)
		{
			buffer.append('|').append(TAG_CHOICE).append(':')
					.append(EntityEncoder.encode(tc.encodeChoice(tc
									.castChoice(assoc))));
		}

		buffer.append(']');
	}

}
