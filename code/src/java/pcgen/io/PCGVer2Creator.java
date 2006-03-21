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

import pcgen.core.*;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.core.utils.ListKey;

import java.util.*;

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
	private static final String LINE_SEP = "\n";
	private PlayerCharacter aPC;

	/**
	 * Constructor
	 * @param aPC
	 */
	PCGVer2Creator(PlayerCharacter aPC)
	{
		this.aPC = aPC;
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
		//TODO:gorm - need to guestimate good starting size for this stringbuffer
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
		appendComment("System Information", buffer);

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
		appendComment("Character Bio", buffer);
		appendCharacterNameLine(buffer);
		appendTabNameLine(buffer);
		appendPlayerNameLine(buffer);
		// TODO Deprecated, to be removed in 5.10 Beta 1
		// appendHeightLine(buffer);
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
		appendComment("Character Attributes", buffer);
		appendStatLines(buffer);
		appendAlignmentLine(buffer);
		appendRaceLine(buffer);

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
		appendComment("Character Class(es)", buffer);
		appendClassLines(buffer);

		/*
		 * #Character Experience
		 * EXPERIENCE:6000
		 */
		appendNewline(buffer);
		appendComment("Character Experience", buffer);
		appendExperienceLine(buffer);

		/*
		 * #Character Templates
		 * TEMPLATESAPPLIED:If any, else this would just have the comment line, and skip to the next
		 */
		appendNewline(buffer);
		appendComment("Character Templates", buffer);
		appendTemplateLines(buffer);

		appendNewline(buffer);
		appendComment("Character Region", buffer);
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
		appendComment("Character Skills", buffer);
		appendSkillLines(buffer);

		/*
		 * #Character Languages
		 */
		appendNewline(buffer);
		appendComment("Character Languages", buffer);
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
		appendComment("Character Feats", buffer);
		appendFeatLines(buffer);

		/*
		 * #Character Weapon proficiencies
		 */
		appendNewline(buffer);
		appendComment("Character Weapon proficiencies", buffer);
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
		appendComment("Character Equipment", buffer);
		appendMoneyLine(buffer);
		appendEquipmentLines(buffer);
		appendEquipmentSetLines(buffer);

		/*
		 * Append Temporary Bonuses
		 */
		appendNewline(buffer);
		appendComment("Temporary Bonuses", buffer);
		appendTempBonuses(buffer);

		/*
		 * Append EquipSet Temp Bonuses
		 */
		appendNewline(buffer);
		appendComment("EquipSet Temp Bonuses", buffer);
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
		appendComment("Character Deity/Domain", buffer);
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
		appendComment("Character Spells Information", buffer);
		appendSpellBookLines(buffer);
		appendSpellLines(buffer);
		appendSpellListLines(buffer);

		/*
		 * #Character Description/Bio/History
		 * CHARACTERBIO:any text that's in the BIO field
		 * CHARACTERDESC:any text that's in the BIO field
		 */
		appendNewline(buffer);
		appendComment("Character Description/Bio/History", buffer);
		appendCharacterBioLine(buffer);
		appendCharacterDescLine(buffer);
		appendCharacterCompLine(buffer);
		appendCharacterAssetLine(buffer);
		appendCharacterMagicLine(buffer);

		/*
		 * #Character Master/Followers
		 * MASTER:Mynex|TYPE:Follower|HITDICE:20|FILE:E$\DnD\dnd-chars\ravenlock.pcg
		 * FOLLOWER:Raven|TYPE:Animal Companion|HITDICE:5|FILE:E$\DnD\dnd-chars\raven.pcg
		 */
		appendNewline(buffer);
		appendComment("Character Master/Follower", buffer);
		appendFollowerLines(buffer);

		/*
		 * #Character Notes Tab
		 */
		appendNewline(buffer);
		appendComment("Character Notes Tab", buffer);
		appendNotesLines(buffer);

		/*
		 * #Kits
		 */
		appendNewline(buffer);
		appendComment("Kits", buffer);
		appendKitLines(buffer);

		/*
		 * #ArmorProf lines
		 */
		appendNewline(buffer);
		appendComment("Chosen Armor Profs", buffer);
		appendArmorProfLines(buffer);

		/*
		 * #AgeSet Kit selections
		 */
		appendNewline(buffer);
		appendComment("Age Set Selections", buffer);
		appendAgeSetLine(buffer);

		/*
		 * Add one more newline at end of file
		 */
		appendNewline(buffer);

		// All done!
		return buffer.toString();
	}

	private static void appendCampaignLine(StringBuffer buffer)
	{
		String del = "";
		Campaign aCamp;

		for (Iterator it = Globals.getCampaignList().iterator(); it.hasNext();)
		{
			aCamp = (Campaign) it.next();

			if (aCamp.isLoaded())
			{
				buffer.append(del);
				buffer.append(TAG_CAMPAIGN).append(':');
				buffer.append(aCamp.getName());
				del = "|";
			}
		}

		buffer.append(LINE_SEP);
	}

	private static void appendGameModeLine(StringBuffer buffer)
	{
		buffer.append(TAG_GAMEMODE).append(':');
		buffer.append(SettingsHandler.getGame().getName());
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
		buffer.append("2.0");
		buffer.append(LINE_SEP);
	}

	private static void appendPurchasePointsLine(StringBuffer buffer)
	{
		buffer.append(TAG_PURCHASEPOINTS).append(':');
		if (SettingsHandler.getGame().isPurchaseStatMode())
		{
			buffer.append('Y');
			buffer.append('|');
			buffer.append(TAG_TYPE).append(':');
			buffer.append(SettingsHandler.getGame().getPurchaseModeMethodName());
		}
		else
		{
			buffer.append('N');
		}
		// TODO
		buffer.append(LINE_SEP);
	}

	private static void appendRollMethodLine(StringBuffer buffer)
	{
		final GameMode game = SettingsHandler.getGame();
		buffer.append(TAG_ROLLMETHOD).append(':');
		buffer.append(game.getRollMethod());
		buffer.append('|');
		buffer.append(TAG_EXPRESSION).append(':');
		switch(game.getRollMethod())
		{
			case Constants.CHARACTERSTATMETHOD_ALLSAME:
				buffer.append(game.getAllStatsValue());
				break;

			case Constants.CHARACTERSTATMETHOD_PURCHASE:
				buffer.append(game.getPurchaseModeMethodName());
				break;

			case Constants.CHARACTERSTATMETHOD_ROLLED:
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

		ResourceBundle d_properties;

		try
		{
			d_properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
			buffer.append(d_properties.getString("VersionNumber"));
		}
		catch (MissingResourceException mre)
		{
			d_properties = null;
		}

		buffer.append(LINE_SEP);
	}

	private void appendAgeLine(StringBuffer buffer)
	{
		buffer.append(TAG_AGE).append(':');
		buffer.append(aPC.getAge());
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
			buffer.append(":");

			if (aPC.hasMadeKitSelectionForAgeSet(i))
			{
				buffer.append("1");
			}
			else
			{
				buffer.append("0");
			}
		}
	}

	private void appendAlignmentLine(StringBuffer buffer)
	{
		//
		// Only save alignment if game mode supports it
		//
		if (Globals.getGameModeAlignmentText().length() != 0)
		{
			buffer.append(TAG_ALIGNMENT).append(':');
			buffer.append(SettingsHandler.getGame().getShortAlignmentAtIndex(aPC.getAlignment()));
			buffer.append(LINE_SEP);
		}
	}

	/*
	 * #ArmorProfs
	 * ARMORPROF:Object:prof:prof:prof
	 */
	private void appendArmorProfLines(StringBuffer buffer)
	{
		ListKey selectedArmorProfListKey = ListKey.SELECTED_ARMOR_PROF;
		if ((aPC.getDeity() != null) && aPC.getDeity().containsListFor(selectedArmorProfListKey))
		{
			buffer.append(TAG_ARMORPROF).append(':').append(TAG_DEITY).append('=').append(aPC.getDeity().getName())
			.append(':');

			for (Iterator i = aPC.getDeity().getListFor(selectedArmorProfListKey).iterator(); i.hasNext();)
			{
				buffer.append((String) i.next()).append(':');
			}

			buffer.append(LINE_SEP);
		}

		for (Iterator j = aPC.getClassList().iterator(); j.hasNext();)
		{
			final PCClass aClass = (PCClass) j.next();

			if (!aClass.containsListFor(selectedArmorProfListKey))
			{
				continue;
			}

			buffer.append(TAG_ARMORPROF).append(':').append(TAG_CLASS).append('=').append(aClass.getName()).append(':');

			for (Iterator i = aClass.getListFor(selectedArmorProfListKey).iterator(); i.hasNext();)
			{
				buffer.append((String) i.next()).append(':');
			}

			buffer.append(LINE_SEP);
		}

		for (Iterator j = aPC.aggregateFeatList().iterator(); j.hasNext();)
		{
			final Ability aFeat = (Ability) j.next();

			if (!aFeat.containsListFor(selectedArmorProfListKey))
			{
				continue;
			}

			buffer.append(TAG_ARMORPROF).append(':').append(TAG_FEAT).append('=').append(aFeat.getName()).append(':');

			for (Iterator i = aFeat.getListFor(selectedArmorProfListKey).iterator(); i.hasNext();)
			{
				buffer.append((String) i.next()).append(':');
			}

			buffer.append(LINE_SEP);
		}

		for (Iterator j = aPC.getSkillList().iterator(); j.hasNext();)
		{
			final Skill aSkill = (Skill) j.next();

			if (!aSkill.containsListFor(selectedArmorProfListKey))
			{
				continue;
			}

			buffer.append(TAG_ARMORPROF).append(':').append(TAG_SKILL).append('=').append(aSkill.getName()).append(':');

			for (Iterator i = aSkill.getListFor(selectedArmorProfListKey).iterator(); i.hasNext();)
			{
				buffer.append((String) i.next()).append(':');
			}

			buffer.append(LINE_SEP);
		}

		for (Iterator j = aPC.getCharacterDomainList().iterator(); j.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) j.next();

			if ((aCD.getDomain() == null) || !aCD.getDomain().containsListFor(selectedArmorProfListKey))
			{
				continue;
			}

			buffer.append(TAG_ARMORPROF).append(':').append(TAG_DOMAIN).append('=').append(aCD.getDomain().getName())
			.append(':');

			for (Iterator i = aCD.getDomain().getListFor(selectedArmorProfListKey).iterator(); i.hasNext();)
			{
				buffer.append((String) i.next()).append(':');
			}

			buffer.append(LINE_SEP);
		}

		for (Iterator e = aPC.getEquipmentMasterList().iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();

			if (!eq.isEquipped() || !eq.containsListFor(selectedArmorProfListKey))
			{
				continue;
			}

			buffer.append(TAG_ARMORPROF).append(':').append(TAG_EQUIPMENT).append('=').append(eq.getName()).append(':');

			for (Iterator i = eq.getListFor(selectedArmorProfListKey).iterator(); i.hasNext();)
			{
				buffer.append((String) i.next()).append(':');
			}

			buffer.append(LINE_SEP);
		}

		for (Iterator j = aPC.getTemplateList().iterator(); j.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) j.next();

			if (!aTemplate.containsListFor(selectedArmorProfListKey))
			{
				continue;
			}

			buffer.append(TAG_ARMORPROF).append(':').append(TAG_TEMPLATE).append('=').append(aTemplate.getName())
			.append(':');

			for (Iterator i = aTemplate.getListFor(selectedArmorProfListKey).iterator(); i.hasNext();)
			{
				buffer.append((String) i.next()).append(':');
			}

			buffer.append(LINE_SEP);
		}
	}

	private void appendBirthdayLine(StringBuffer buffer)
	{
		buffer.append(TAG_BIRTHDAY).append(':');
		buffer.append(EntityEncoder.encode(aPC.getBirthday()));
		buffer.append(LINE_SEP);
	}

	private void appendBirthplaceLine(StringBuffer buffer)
	{
		buffer.append(TAG_BIRTHPLACE).append(':');
		buffer.append(EntityEncoder.encode(aPC.getBirthplace()));
		buffer.append(LINE_SEP);
	}

	private void appendCatchPhraseLine(StringBuffer buffer)
	{
		buffer.append(TAG_CATCHPHRASE).append(':');
		buffer.append(EntityEncoder.encode(aPC.getCatchPhrase()));
		buffer.append(LINE_SEP);
	}

	private void appendCharacterAssetLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERASSET).append(':');
		buffer.append(EntityEncoder.encode((String) aPC.getMiscList().get(0)));
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
		buffer.append(EntityEncoder.encode(aPC.getBio()));
		buffer.append(LINE_SEP);
	}

	private void appendCharacterCompLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERCOMP).append(':');
		buffer.append(EntityEncoder.encode((String) aPC.getMiscList().get(1)));
		buffer.append(LINE_SEP);
	}

	private void appendCharacterDescLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERDESC).append(':');
		buffer.append(EntityEncoder.encode(aPC.getDescription()));
		buffer.append(LINE_SEP);
	}

	private void appendCharacterMagicLine(StringBuffer buffer)
	{
		buffer.append(TAG_CHARACTERMAGIC).append(':');
		buffer.append(EntityEncoder.encode((String) aPC.getMiscList().get(2)));
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Character Class(es) methods
	 * ###############################################################
	 */
	private void appendClassLines(StringBuffer buffer)
	{
		Cache specials;

		String aSave;
		String aSource;
		SpecialAbility aSpecialAbility;

		String subClassName;
		String prohibited;

		PCClass aClass;
		specials = new Cache();

		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			aClass = (PCClass) it.next();

			int classLevel = aClass.getLevel();

			buffer.append(TAG_CLASS).append(':');
			buffer.append(EntityEncoder.encode(aClass.getKeyName()));

			subClassName = aClass.getSubClassName();

			if (!"".equals(subClassName))
			{
				buffer.append('|');
				buffer.append(TAG_SUBCLASS).append(':');
				buffer.append(EntityEncoder.encode(subClassName));
			}

			buffer.append('|');
			buffer.append(TAG_LEVEL).append(':');
			buffer.append(classLevel);
			buffer.append('|');
			buffer.append(TAG_SKILLPOOL).append(':');
			buffer.append(aClass.skillPool());

			// determine if this class can cast spells
			boolean isCaster = false;

			if (!aClass.zeroCastSpells())
			{
				isCaster = true;
			}

			boolean isPsionic = (aClass.getKnownList().size() > 0) && !isCaster;

			if (isCaster || isPsionic)
			{
				buffer.append('|');
				buffer.append(TAG_SPELLBASE).append(':');
				buffer.append(EntityEncoder.encode(aClass.getSpellBaseStat()));
				buffer.append('|');
				buffer.append(TAG_CANCASTPERDAY).append(':');
				buffer.append(aClass.getCastStringForLevel(classLevel));
			}

			prohibited = aClass.getProhibitedString();

			if (!"".equals(prohibited))
			{
				buffer.append('|');
				buffer.append(TAG_PROHIBITED).append(':');
				buffer.append(EntityEncoder.encode(prohibited));
			}

			buffer.append(LINE_SEP);

			//specials = new Cache();
			String key;
			key = aClass.getKeyName() + TAG_SPECIALTY + '0';

			for (Iterator it2 = aClass.getSpecialtyList().iterator(); it2.hasNext();)
			{
				specials.put(key, (String) it2.next());
			}

			key = aClass.getKeyName() + TAG_SAVE + '0';

			for (Iterator sit = aClass.getSafeListFor(ListKey.SAVE).iterator(); sit.hasNext();)
			{
				aSave = (String) sit.next();
				aSpecialAbility = aClass.getSpecialAbilityNamed(aSave);

				if (aSpecialAbility != null)
				{
					int relevantLevel = 1;
					aSource = aSpecialAbility.getSASource();

					try
					{
						relevantLevel = Integer.parseInt(aSource.substring(aSource.lastIndexOf('|') + 1));

						if (relevantLevel < 0)
						{
							relevantLevel = 1;
						}

//						relevantLevel = Math.max(1, Integer.parseInt(
//							aSource.substring(aSource.lastIndexOf('|') + 1)));
					}
					catch (NumberFormatException nfe)
					{
						// nothing we can do about it
					}

					specials.put(aClass.getKeyName() + TAG_SA + (relevantLevel - 1), aSpecialAbility.getName());
				}
				else
				{
					specials.put(key, aSave);
				}
			}
		}

		//
		// Save level up information in the order of levelling
		//
		for (Iterator i = aPC.getLevelInfo().iterator(); i.hasNext();)
		{
			final PCLevelInfo pcl = (PCLevelInfo) i.next();
			final String classKeyName = pcl.getClassKeyName();
			int lvl = pcl.getLevel() - 1;
			aClass = aPC.getClassKeyed(classKeyName);
			buffer.append(TAG_CLASSABILITIESLEVEL).append(':');

			if (aClass == null)
			{
				aClass = Globals.getClassNamed(classKeyName);

				if (aClass != null)
				{
					aClass = aPC.getClassNamed(aClass.getExClass());
				}
			}

			if (aClass != null)
			{
				buffer.append(EntityEncoder.encode(aClass.getKeyName()));
			}
			else
			{
				buffer.append(EntityEncoder.encode("???"));
			}

			buffer.append('=').append(lvl + 1);

			if (aClass != null)
			{
				buffer.append('|');
				buffer.append(TAG_HITPOINTS).append(':');
				buffer.append(aClass.getHitPoint(lvl).toString());
				appendSpecials(buffer, specials.get(aClass.getKeyName() + TAG_SAVE + lvl), TAG_SAVES, TAG_SAVE, lvl);
				appendSpecials(buffer, specials.get(aClass.getKeyName() + TAG_SPECIALTY + lvl), TAG_SPECIALTIES,
				    TAG_SPECIALTY, lvl);
				appendSpecials(buffer, specials.get(aClass.getKeyName() + TAG_SA + lvl), TAG_SPECIALABILITIES, TAG_SA,
				    lvl);

				if (lvl == 0)
				{
					appendSpecials(buffer, specials.get(aClass.getKeyName() + TAG_SA + (lvl - 1)),
					    TAG_SPECIALABILITIES, TAG_SA, -1);
				}

				//
				// Remember what choices were made for each of the ADD: tags
				//
				appendLevelAbilityInfo(buffer, aClass, lvl);
			}

			List statList = pcl.getModifiedStats(true);

			if (statList != null)
			{
				for (int j = 0; j < statList.size(); ++j)
				{
					buffer.append('|').append(TAG_PRESTAT).append(':').append(statList.get(j).toString());
				}
			}

			statList = pcl.getModifiedStats(false);

			if (statList != null)
			{
				for (int j = 0; j < statList.size(); ++j)
				{
					buffer.append('|').append(TAG_POSTSTAT).append(':').append(statList.get(j).toString());
				}
			}

			int sp = pcl.getSkillPointsGained();

			if (sp != 0)
			{
				buffer.append('|').append(TAG_SKILLPOINTSGAINED).append(":" + sp);
			}

			sp = pcl.getSkillPointsRemaining();

			if (sp != 0)
			{
				buffer.append('|').append(TAG_SKILLPOINTSREMAINING).append(":" + sp);
			}

//			buffer.append('|');
//			buffer.append(TAG_DATA).append(':');
//			buffer.append('(');
//			// TODO
//			buffer.append(')');
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
		if (aPC.getDeity() != null)
		{
			final Deity aDeity = aPC.getDeity();

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

			String del = "";

			for (Iterator it = aDeity.getDomainList().iterator(); it.hasNext();)
			{
				buffer.append(del);
				buffer.append(TAG_DOMAIN).append(':');

				final Domain aDomain = (Domain) it.next();

				if (aDomain == null)
				{
					buffer.append(EntityEncoder.encode(Constants.s_NONE));
				}
				else
				{
					buffer.append(EntityEncoder.encode(aDomain.getKeyName()));
				}

				del = "|";
			}

			buffer.append(']');

			buffer.append('|');
			buffer.append(TAG_ALIGNALLOW).append(':');
			buffer.append(aDeity.getFollowerAlignments());
			buffer.append('|');
			buffer.append(TAG_DESC).append(':');
			buffer.append(EntityEncoder.encode(aDeity.getDescription()));
			buffer.append('|');
			buffer.append(TAG_HOLYITEM).append(':');
			buffer.append(EntityEncoder.encode(aDeity.getHolyItem()));

//  			buffer.append('|');
//  			buffer.append(TAG_SYMBOL).append(':');
//  			// TODO
//  			buffer.append(Constants.s_NONE);
			buffer.append('|');
			buffer.append(TAG_DEITYFAVWEAP).append(':');
			buffer.append('[');

			final StringTokenizer tokens = new StringTokenizer(aDeity.getFavoredWeapon(), "|");
			del = "";

			while (tokens.hasMoreTokens())
			{
				buffer.append(del);
				buffer.append(TAG_WEAPON).append(':');
				buffer.append(EntityEncoder.encode(tokens.nextToken()));
				del = "|";
			}

			buffer.append(']');

			buffer.append('|');
			buffer.append(TAG_DEITYALIGN).append(':');
			buffer.append(aDeity.getAlignment());
			buffer.append(LINE_SEP);
		}
	}

	private void appendDomainLines(StringBuffer buffer)
	{
		Domain aDomain;
		CharacterDomain aCharDomain;

//  		Spell aSpell;
//  		List domainSpells = new ArrayList();
		for (Iterator it = aPC.getCharacterDomainList().iterator(); it.hasNext();)
		{
			aCharDomain = (CharacterDomain) it.next();

			if (aCharDomain == null)
			{
				continue;
			}

			aDomain = aCharDomain.getDomain();

			if (aDomain == null)
			{
				continue;
			}

// TODO :
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
			buffer.append(EntityEncoder.encode(aDomain.getKeyName()));

			for (int i = 0; i < aDomain.getAssociatedCount(); ++i)
			{
				buffer.append('|');
				buffer.append(TAG_ASSOCIATEDDATA).append(':');
				buffer.append(EntityEncoder.encode(aDomain.getAssociated(i)));
			}

			buffer.append('|');
			buffer.append(TAG_DOMAINGRANTS).append(':');
			buffer.append(EntityEncoder.encode(aDomain.getDescription()));
			buffer.append('|');
			appendSourceInTaggedFormat(buffer, aCharDomain.getDomainSourcePcgString());

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
	 * For each EquipSet, check for a tempBonusList
	 * and if found, save each bonus
	 * @param buffer
	 **/
	private void appendEqSetBonuses(StringBuffer buffer)
	{
		final List eqSetList = aPC.getEquipSet();

		EquipSet eSet;

		for (Iterator it = eqSetList.iterator(); it.hasNext();)
		{
			eSet = (EquipSet) it.next();

			if (eSet.useTempBonusList())
			{
				buffer.append(TAG_EQSETBONUS).append(':');
				buffer.append(eSet.getIdPath());

				List aList = eSet.getTempBonusList();
				List trackList = new ArrayList();

				for (Iterator ab = aList.iterator(); ab.hasNext();)
				{
					BonusObj bObj = (BonusObj) ab.next();
					Object cObj = bObj.getCreatorObject();
					Object tObj = bObj.getTargetObject();
					String aName = tempBonusName(cObj, tObj);

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
		Equipment aEquip;

		for (Iterator it = aPC.getEquipmentMasterList().iterator(); it.hasNext();)
		{
			aEquip = (Equipment) it.next();

			buffer.append(TAG_EQUIPNAME).append(':');
			buffer.append(EntityEncoder.encode(aEquip.getName()));
			buffer.append('|');
			buffer.append(TAG_OUTPUTORDER).append(':');
			buffer.append(aEquip.getOutputIndex());
			buffer.append('|');
			buffer.append(TAG_COST).append(':');
			buffer.append(aEquip.getCost(aPC).toString());
			buffer.append('|');
			buffer.append(TAG_WT).append(':');
			buffer.append(aEquip.getWeight(aPC).toString());
			buffer.append('|');
			buffer.append(TAG_QUANTITY).append(':');
			buffer.append(aEquip.qty());

			final String customization = aEquip.formatSaveLine("$", "=").trim();
			final int delimiterIndex = customization.indexOf('$');

			if ((customization.length() > 0) && (delimiterIndex >= 0))
			{
				buffer.append('|');
				buffer.append(TAG_CUSTOMIZATION).append(':');
				buffer.append('[');
				buffer.append(TAG_BASEITEM).append(':');
				buffer.append(EntityEncoder.encode(customization.substring(0, delimiterIndex)));
				buffer.append('|');
				buffer.append(TAG_DATA).append(':');
				buffer.append(EntityEncoder.encode(customization.substring(delimiterIndex + 1)));
				buffer.append(']');
			}

			buffer.append(LINE_SEP);
		}
	}

	private void appendEquipmentSetLines(StringBuffer buffer)
	{
		// Output all the EquipSets
		final List eqSetList = aPC.getEquipSet();
		Collections.sort(eqSetList);

		EquipSet aEquipSet;

		for (Iterator it = eqSetList.iterator(); it.hasNext();)
		{
			aEquipSet = (EquipSet) it.next();

			buffer.append(TAG_EQUIPSET).append(':');
			buffer.append(EntityEncoder.encode(aEquipSet.getName()));
			buffer.append('|');
			buffer.append(TAG_ID).append(':');
			buffer.append(aEquipSet.getIdPath());

			if (aEquipSet.getValue().length() > 0)
			{
				buffer.append('|');
				buffer.append(TAG_VALUE).append(':');
				buffer.append(EntityEncoder.encode(aEquipSet.getValue()));
				buffer.append('|');
				buffer.append(TAG_QUANTITY).append(':');
				buffer.append(aEquipSet.getQty());
			}

			if (aEquipSet.getNote().length() > 0)
			{
				buffer.append('|');
				buffer.append(TAG_NOTE).append(':');
				buffer.append(aEquipSet.getNote());
			}

			if (aEquipSet.getUseTempMods())
			{
				buffer.append('|');
				buffer.append(TAG_USETEMPMODS).append(':');
				buffer.append(aEquipSet.getUseTempMods() ? "Y" : "N");
			}

			buffer.append(LINE_SEP);
		}

		// Then output EquipSet used for "working" equipmentList
		final String calcEquipSet = aPC.getCalcEquipSetId();
		buffer.append(TAG_CALCEQUIPSET).append(':');
		buffer.append(calcEquipSet);
		buffer.append(LINE_SEP);
	}

	private void appendEyeColorLine(StringBuffer buffer)
	{
		buffer.append(TAG_EYECOLOR).append(':');
		buffer.append(EntityEncoder.encode(aPC.getEyeColor()));
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Character Feats methods
	 * ###############################################################
	 */
	private void appendFeatLines(StringBuffer buffer)
	{
		Ability  aFeat;

		List     aList = new ArrayList();
		Iterator anIt  = aPC.getRealFeatsIterator();

		while (anIt.hasNext())
		{
			aList.add(anIt.next());
		}

		List bList = new ArrayList();
		List removeList = buildLevelAbilityFeatList();

		for (Iterator it = aPC.getVirtualFeatList().iterator(); it.hasNext();)
		{
			aFeat = (Ability) it.next();

			if (aFeat.needsSaving())
			{
				bList.add(aFeat); // add virtual feats with needsSaving to aList
			}
		}

		aList.addAll(bList);

		ListKey saveListKey = ListKey.SAVE;

		for (Iterator it = aList.iterator(); it.hasNext();)
		{
			aFeat = (Ability) it.next();

			if (aFeat.isMultiples())
			{
				for (int it2 = 0; it2 < aFeat.getAssociatedCount(); ++it2)
				{
					// Check that the feat has not been output already.
					if (isInLevelAbilityList(removeList, aFeat, aFeat.getAssociated(it2)))
					{
						continue;
					}
					if (bList.contains(aFeat))
					{
						buffer.append(TAG_VFEAT);
					}
					else
					{
						buffer.append(TAG_FEAT);
					}

					buffer.append(':');
					buffer.append(EntityEncoder.encode(aFeat.getKeyName()));
					buffer.append('|');
					buffer.append(TAG_APPLIEDTO).append(':');

					if (aFeat.getAssociatedObject(0) instanceof FeatMultipleChoice)
					{
						buffer.append(TAG_MULTISELECT).append(':');
					}

					buffer.append(EntityEncoder.encode(aFeat.getAssociated(it2)));
					buffer.append('|');
					buffer.append(TAG_TYPE).append(':');
					buffer.append(EntityEncoder.encode(aFeat.getType()));
					int it3=0;
					int maxit3 = aFeat.getSizeOfListFor(saveListKey);
					if (aFeat.getAssociatedCount() == maxit3)
					{
						it3=it2;
						maxit3 = it3+1;
					}

					for (; it3 < maxit3; ++it3)
					{
						buffer.append('|');
						buffer.append(TAG_SAVE).append(':');
						buffer.append(EntityEncoder.encode(((String) aFeat.getElementInList(saveListKey, it3))));
					}

					appendLevelAbilityInfo(buffer, aFeat);

					buffer.append('|');
					buffer.append(TAG_DESC).append(':');
					buffer.append(EntityEncoder.encode(aFeat.getDescription()));

//  					buffer.append('|');
//  					buffer.append(TAG_DATA).append(':');
//  					buffer.append('(');
//  					// TODO
//  					buffer.append(')');
					buffer.append(LINE_SEP);
				}
			}
			else
			{
				// Check that the feat has not been output already.
				if (isInLevelAbilityList(removeList, aFeat, ""))
				{
					continue;
				}

				if (bList.contains(aFeat))
				{
					buffer.append(TAG_VFEAT);
				}
				else
				{
					buffer.append(TAG_FEAT);
				}

				buffer.append(':');
				buffer.append(EntityEncoder.encode(aFeat.getKeyName()));
				buffer.append('|');
				buffer.append(TAG_TYPE).append(':');
				buffer.append(EntityEncoder.encode(aFeat.getType()));

				for (Iterator it3 = aFeat.getSafeListFor(saveListKey).iterator(); it3.hasNext();)
				{
					buffer.append('|');
					buffer.append(TAG_SAVE).append(':');
					buffer.append(EntityEncoder.encode(((String) it3.next())));
				}

				appendLevelAbilityInfo(buffer, aFeat);

				buffer.append('|');
				buffer.append(TAG_DESC).append(':');
				buffer.append(EntityEncoder.encode(aFeat.getDescription()));

//  				buffer.append('|');
//  				buffer.append(TAG_DATA).append(':');
//  				buffer.append('(');
//  				// TODO
//  				buffer.append(')');
				buffer.append(LINE_SEP);
			}
		}

		buffer.append(TAG_FEATPOOL).append(':');
		buffer.append(aPC.getRawFeats(false));
		buffer.append(LINE_SEP);
		aList.removeAll(bList);
	}

	/**
	 * Build up a list of names of feat choices that will have been written
	 * out in the class abilities section (i.e. Feats that were class abilities
	 * such as fighter feats.) These should nto be written out again in the
	 * feats section toherwise they can multiply...
	 * @return List of level ability feat choices as Strings
	 */
	private List buildLevelAbilityFeatList()
	{
		List removeList = new ArrayList();
		for (Iterator i = aPC.getLevelInfo().iterator(); i.hasNext();)
		{
			final PCLevelInfo pcl = (PCLevelInfo) i.next();
			final String classKeyName = pcl.getClassKeyName();
			int lvl = pcl.getLevel() - 1;
			PCClass aClass = aPC.getClassKeyed(classKeyName);
			List laList = aClass.getLevelAbilityList();
			if (aClass != null && laList != null)
			{
				for (Iterator it = laList.iterator(); it.hasNext();)
				{
					LevelAbility la = (LevelAbility) it.next();

					if ((la.level() - 1) == lvl && la.getAssociatedCount() != 0)
					{
						for (int j = 0; j < la.getAssociatedCount(true); ++j)
						{
							removeList.add(la.getAssociated(j, true));
						}
					}
				}
			}
		}

		return removeList;
	}

	/**
	 * Check if the supplied feat was gained as a result of a level
	 * ability and thus has already been written out to the pcg file.
	 * Note: In order to ensure that feats taken multiple times (such as
	 * Psionic Body taken as a class ability and a regular feat) the supplied
	 * ability list will have a matching entry removed. So on a true result
	 * the laList will have been modified. This means the list must be
	 * rebuilt if you want to check the full list of feats again.
	 *
	 * @param laList The list of chosen level abailities.
	 * @param feat The feat to be checked.
	 * @param associated The feat choice to be checked.
	 * @return true if the feat is part of a level ability.
	 */
	private boolean isInLevelAbilityList(List laList, Ability feat,
		String associated)
	{
		String matchString = feat.getName();
		if (associated != null && associated.length() > 0)
		{
			matchString += "(" + associated + ")";
		}
		for (Iterator laIter = laList.iterator(); laIter.hasNext();)
		{
			String ability = (String) laIter.next();
			if (matchString.equals(ability))
			{
				laIter.remove();
				return true;
			}
		}
		return false;
	}

	/*
	 * ###############################################################
	 * Character Follower methods
	 * ###############################################################
	 */
	private void appendFollowerLines(StringBuffer buffer)
	{
		final Follower aMaster = aPC.getMaster();

		if (aMaster != null)
		{
			buffer.append(TAG_MASTER).append(':');
			buffer.append(EntityEncoder.encode(aMaster.getName()));
			buffer.append('|');
			buffer.append(TAG_TYPE).append(':');
			buffer.append(EntityEncoder.encode(aMaster.getType()));
			buffer.append('|');
			buffer.append(TAG_HITDICE).append(':');
			buffer.append(aMaster.getUsedHD());
			buffer.append('|');
			buffer.append(TAG_FILE).append(':');
			buffer.append(EntityEncoder.encode(aMaster.getRelativeFileName()));
			buffer.append(LINE_SEP);
		}

		final List followers = aPC.getFollowerList();

		if (!followers.isEmpty())
		{
			Follower aFollower;

			for (Iterator it = followers.iterator(); it.hasNext();)
			{
				aFollower = (Follower) it.next();
				buffer.append(TAG_FOLLOWER).append(':');
				buffer.append(EntityEncoder.encode(aFollower.getName()));
				buffer.append('|');
				buffer.append(TAG_TYPE).append(':');
				buffer.append(EntityEncoder.encode(aFollower.getType()));
				buffer.append('|');
				buffer.append(TAG_RACE).append(':');
				buffer.append(EntityEncoder.encode(aFollower.getRace().toUpperCase()));
				buffer.append('|');
				buffer.append(TAG_HITDICE).append(':');
				buffer.append(aFollower.getUsedHD());
				buffer.append('|');
				buffer.append(TAG_FILE).append(':');
				buffer.append(EntityEncoder.encode(aFollower.getRelativeFileName()));
				buffer.append(LINE_SEP);
			}
		}
	}

	private void appendGenderLine(StringBuffer buffer)
	{
		buffer.append(TAG_GENDER).append(':');
		buffer.append(EntityEncoder.encode(aPC.getGender()));
		buffer.append(LINE_SEP);
	}

	private void appendHairColorLine(StringBuffer buffer)
	{
		buffer.append(TAG_HAIRCOLOR).append(':');
		buffer.append(EntityEncoder.encode(aPC.getHairColor()));
		buffer.append(LINE_SEP);
	}

	private void appendHairStyleLine(StringBuffer buffer)
	{
		buffer.append(TAG_HAIRSTYLE).append(':');
		buffer.append(EntityEncoder.encode(aPC.getHairStyle()));
		buffer.append(LINE_SEP);
	}

	private void appendHandedLine(StringBuffer buffer)
	{
		buffer.append(TAG_HANDED).append(':');
		buffer.append(EntityEncoder.encode(aPC.getHanded()));
		buffer.append(LINE_SEP);
	}

	private void appendInterestsLine(StringBuffer buffer)
	{
		buffer.append(TAG_INTERESTS).append(':');
		buffer.append(EntityEncoder.encode(aPC.getInterests()));
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
	 * TODO:
	 * KIT:KitName|TYPE:KitType|REGION:Region
	 */
	private void appendKitLines(StringBuffer buffer)
	{
		Kit aKit;

		if (aPC.getKitInfo() != null)
		{
			for (Iterator it = aPC.getKitInfo().iterator(); it.hasNext();)
			{
				aKit = (Kit) it.next();
				buffer.append(TAG_KIT).append(':').append(aKit.getKeyName()).append(LINE_SEP);
			}
		}
	}

	/*
	 * ###############################################################
	 * Character Language methods
	 * ###############################################################
	 */
	private void appendLanguageLine(StringBuffer buffer)
	{
		String del = "";

		for (Iterator it = aPC.getLanguagesList().iterator(); it.hasNext();)
		{
			buffer.append(del);
			buffer.append(TAG_LANGUAGE).append(':');
			buffer.append(EntityEncoder.encode(it.next().toString()));
			del = "|";
		}

		buffer.append(LINE_SEP);
	}

	private void appendLocationLine(StringBuffer buffer)
	{
		buffer.append(TAG_LOCATION).append(':');
		buffer.append(EntityEncoder.encode(aPC.getLocation()));
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
		for (Iterator i = aPC.getNotesList().iterator(); i.hasNext();)
		{
			NoteItem ni = (NoteItem) i.next();
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
		buffer.append(EntityEncoder.encode(aPC.getTrait1()));
		buffer.append(LINE_SEP);
	}

	private void appendPersonalityTrait2Line(StringBuffer buffer)
	{
		buffer.append(TAG_PERSONALITYTRAIT2).append(':');
		buffer.append(EntityEncoder.encode(aPC.getTrait2()));
		buffer.append(LINE_SEP);
	}

	private void appendPhobiasLine(StringBuffer buffer)
	{
		buffer.append(TAG_PHOBIAS).append(':');
		buffer.append(EntityEncoder.encode(aPC.getPhobias()));
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
		buffer.append(aPC.getPoolAmount());
		buffer.append(LINE_SEP);
		buffer.append(TAG_POOLPOINTSAVAIL).append(':');
		buffer.append(aPC.getPointBuyPoints());
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
		buffer.append(aPC.isAutoSortGear() ? "Y" : "N");
		buffer.append(LINE_SEP);
		buffer.append(TAG_SKILLSOUTPUTORDER).append(':');
		buffer.append(aPC.getSkillsOutputOrder());
		buffer.append(LINE_SEP);
	}

	private void appendAutoSpellsLine(StringBuffer buffer)
	{
		buffer.append(TAG_AUTOSPELLS).append(':');
		buffer.append(aPC.getAutoSpells() ? "Y" : "N");
		buffer.append(LINE_SEP);
	}

	/**
	 * Append the settings related to higher level slot use for spells.
	 * @param buffer The buffer to append to.
	 */
	private void appendUseHigherSpellSlotsLines(StringBuffer buffer)
	{
		buffer.append(TAG_USEHIGHERKNOWN).append(':');
		buffer.append(aPC.getUseHigherKnownSlots() ? "Y" : "N");
		buffer.append(LINE_SEP);
		buffer.append(TAG_USEHIGHERPREPPED).append(':');
		buffer.append(aPC.getUseHigherPreppedSlots() ? "Y" : "N");
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
		buffer.append(EntityEncoder.encode(aPC.getName()));
		buffer.append(LINE_SEP);
	}

	// TODO deprecated Remove in 5.10 Beta 1
	/*
	private void appendHeightLine(StringBuffer buffer)
	{
		buffer.append(TAG_HEIGHT).append(':');
		buffer.append(aPC.getHeight());
		buffer.append(LINE_SEP);
	}
	*/

	private void appendLoadCompanionLine(StringBuffer buffer)
	{
		buffer.append(TAG_LOADCOMPANIONS).append(':');
		buffer.append(aPC.getLoadCompanion() ? "Y" : "N");
		buffer.append(LINE_SEP);
	}

	private void appendOutputSheetsLines(StringBuffer buffer)
	{
		if (SettingsHandler.getSaveOutputSheetWithPC())
		{
			buffer.append(TAG_HTMLOUTPUTSHEET).append(':');
			buffer.append(EntityEncoder.encode(SettingsHandler.getSelectedCharacterHTMLOutputSheet(aPC)));
			buffer.append(LINE_SEP);
			buffer.append(TAG_PDFOUTPUTSHEET).append(':');
			buffer.append(EntityEncoder.encode(SettingsHandler.getSelectedCharacterPDFOutputSheet(aPC)));
			buffer.append(LINE_SEP);
		}
	}

	private void appendPlayerNameLine(StringBuffer buffer)
	{
		buffer.append(TAG_PLAYERNAME).append(':');
		buffer.append(EntityEncoder.encode(aPC.getPlayersName()));
		buffer.append(LINE_SEP);
	}

	private void appendPortraitLine(StringBuffer buffer)
	{
		buffer.append(TAG_PORTRAIT).append(':');
		buffer.append(EntityEncoder.encode(aPC.getPortraitPath()));
		buffer.append(LINE_SEP);
	}

	private void appendRaceLine(StringBuffer buffer)
	{
		buffer.append(TAG_RACE).append(':');
		buffer.append(EntityEncoder.encode(aPC.getRace().getKeyName()));

		final int hitDice = aPC.getRace().hitDice(aPC);

		if (hitDice != 0)
		{
			buffer.append('|').append(TAG_HITPOINTS);

			for (int j = 0; j < hitDice; ++j)
			{
				buffer.append(':').append(aPC.getRace().getHitPoint(j).toString());
			}
		}

		buffer.append(LINE_SEP);

		// TODO
		// don't we want to save more info here?
	}

	private void appendResidenceLine(StringBuffer buffer)
	{
		buffer.append(TAG_CITY).append(':');
		buffer.append(EntityEncoder.encode(aPC.getResidence()));
		buffer.append(LINE_SEP);
	}

	private void appendSkinColorLine(StringBuffer buffer)
	{
		buffer.append(TAG_SKINCOLOR).append(':');
		buffer.append(EntityEncoder.encode(aPC.getSkinColor()));
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
	private static void appendSourceInTaggedFormat(StringBuffer buffer, String source)
	{
		final StringTokenizer tokens = new StringTokenizer(source, "|=");
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
			buffer.append("Y");
		}

		buffer.append(']');
	}

	/*
	 * currently source is either empty or
	 * PCCLASS|classname|classlevel (means it's a chosen special ability)
	 * PCCLASS=classname|classlevel (means it's a defined special ability)
	 * DEITY=deityname|totallevels
	 */
	private static void appendSourceInTaggedFormat(StringBuffer buffer, PObject source)
	{
		buffer.append(TAG_SOURCE).append(':');
		buffer.append('[');
		buffer.append(TAG_TYPE).append(':');

		// I love reflection :-)
		final Class srcClass = source.getClass();
		final String pckName = srcClass.getPackage().getName();
		final String srcName = srcClass.getName().substring(pckName.length() + 1);

		buffer.append(srcName.toUpperCase());
		buffer.append('|');
		buffer.append(TAG_NAME).append(':');
		buffer.append(source.getName());
		buffer.append(']');
	}

	private static void appendSpecials(StringBuffer buffer, List specials, String tag_group, String tag_item, int lvl)
	{
		if ((specials != null) && (!specials.isEmpty()))
		{
			buffer.append('|');
			buffer.append(tag_group).append(':');
			buffer.append('[');

			String del = "";

			for (Iterator it2 = specials.iterator(); it2.hasNext();)
			{
				buffer.append(del);
				buffer.append(tag_item).append(':');
				buffer.append(EntityEncoder.encode((String) it2.next()));

				if (lvl == -1)
				{
					buffer.append(":-1");
				}

				del = "|";
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
		buffer.append(aPC.getXP());
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Character Region method
	 * ###############################################################
	 */
	private void appendRegionLine(StringBuffer buffer)
	{
		final String r = aPC.getRegion(false);

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
		String del;
		Skill aSkill;

		int includeSkills = SettingsHandler.getIncludeSkills();

		if (includeSkills == 3)
		{
			includeSkills = SettingsHandler.getSkillsTab_IncludeSkills();
		}

		aPC.populateSkills(includeSkills);

		for (Iterator it = aPC.getSkillList().iterator(); it.hasNext();)
		{
			aSkill = (Skill) it.next();

			if ((aSkill.getRank().doubleValue() > 0) || (aSkill.getOutputIndex() != 0))
			{
				buffer.append(TAG_SKILL).append(':');
				buffer.append(EntityEncoder.encode(aSkill.getKeyName()));
				del = "|" + TAG_SYNERGY + ":";

				for (Iterator it2 = aSkill.getSynergyList().iterator(); it2.hasNext();)
				{
					buffer.append(del).append((String) it2.next());
					del = ",";
				}

				buffer.append('|');
				buffer.append(TAG_OUTPUTORDER).append(':');
				buffer.append(aSkill.getOutputIndex());
				buffer.append('|');

				String ranks;
				String className;
				String classRanks;
				PCClass aPCClass;

				for (Iterator it2 = aSkill.getRankList().iterator(); it2.hasNext();)
				{
					classRanks = (String) it2.next();

					int index = classRanks.indexOf(':');
					className = classRanks.substring(0, index);
					ranks = classRanks.substring(index + 1);

					aPCClass = aPC.getClassKeyed(className);

					buffer.append(TAG_CLASSBOUGHT).append(':');
					buffer.append('[');
					buffer.append(TAG_CLASS).append(':');
					buffer.append(EntityEncoder.encode(className));
					buffer.append('|');
					buffer.append(TAG_RANKS).append(':');
					buffer.append(ranks);
					buffer.append('|');
					buffer.append(TAG_COST).append(':');
					buffer.append(Integer.toString(aSkill.costForPCClass(aPCClass, aPC)));
					buffer.append('|');
					buffer.append(TAG_CLASSSKILL).append(':');
					buffer.append((aSkill.isClassSkill(aPCClass, aPC)) ? 'Y' : 'N');
					buffer.append(']');
				}

				for (int i = 0; i < aSkill.getAssociatedCount(); ++i)
				{
					buffer.append('|');
					buffer.append(TAG_ASSOCIATEDDATA).append(':');
					buffer.append(EntityEncoder.encode(aSkill.getAssociated(i)));
				}

				appendLevelAbilityInfo(buffer, aSkill);

				buffer.append(LINE_SEP);
			}
		}
	}

	private void appendSpeechPatternLine(StringBuffer buffer)
	{
		buffer.append(TAG_SPEECHPATTERN).append(':');
		buffer.append(EntityEncoder.encode(aPC.getSpeechTendency()));
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
		for (Iterator iter = aPC.getSpellBooks().iterator(); iter.hasNext();)
		{
			final String bookName = (String) iter.next();
			if (!bookName.equals(Globals.getDefaultSpellBook()) &&
					!bookName.equals(Globals.INNATE_SPELL_BOOK_NAME))
			{
				SpellBook book = aPC.getSpellBookByName(bookName);
				buffer.append("SPELLBOOK:");
				buffer.append(book.getName());
				buffer.append("|TYPE:").append(book.getType());
				if (book.getName().equals(aPC.getSpellBookNameToAutoAddKnown()))
				{
					buffer.append("|AUTOADDKNOWN:Y");
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
		PCClass aClass;
		CharacterSpell aCharacterSpell;
		SpellInfo aSpellInfo;
		String spellKey;
		List metaFeats;
		String del;

		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			aClass = (PCClass) it.next();

			for (Iterator it2 = aClass.getSpellSupport().getCharacterSpell(null, "", -1).iterator(); it2.hasNext();)
			{
				aCharacterSpell = (CharacterSpell) it2.next();

				for (Iterator it3 = aCharacterSpell.getInfoListIterator(); it3.hasNext();)
				{
					aSpellInfo = (SpellInfo) it3.next();
					spellKey = aCharacterSpell.getOwner().getSpellKey();

					if (aSpellInfo.getBook().equals(Globals.getDefaultSpellBook())
					    && aClass.isAutoKnownSpell(aCharacterSpell.getSpell().getKeyName(),
					        aCharacterSpell.getSpell().getFirstLevelForKey(spellKey, aPC), aPC) && aPC.getAutoSpells())
					{
						continue;
					}

					buffer.append(TAG_SPELLNAME).append(':');
					buffer.append(EntityEncoder.encode(aCharacterSpell.getSpell().getKeyName()));
					buffer.append('|');
					buffer.append(TAG_TIMES).append(':');
					buffer.append(aSpellInfo.getTimes());
					buffer.append('|');
					buffer.append(TAG_CLASS).append(':');
					buffer.append(EntityEncoder.encode(aClass.getName()));
					buffer.append('|');
					buffer.append(TAG_SPELL_BOOK).append(':');
					buffer.append(EntityEncoder.encode(aSpellInfo.getBook()));
					buffer.append('|');
					buffer.append(TAG_SPELLLEVEL).append(':');
					buffer.append(aSpellInfo.getActualLevel());
					if (Spell.hasPPCost())
					{
						buffer.append('|');
						buffer.append(TAG_SPELLPPCOST).append(':');
						buffer.append(aSpellInfo.getActualPPCost());
					}
					if (aSpellInfo.getNumPages() > 0)
					{
						buffer.append('|');
						buffer.append(TAG_SPELLNUMPAGES).append(':');
						buffer.append(aSpellInfo.getNumPages());
					}

					metaFeats = aSpellInfo.getFeatList();

					if ((metaFeats != null) && (!metaFeats.isEmpty()))
					{
						buffer.append('|');
						buffer.append(TAG_FEATLIST).append(':');
						buffer.append('[');
						del = "";

						for (Iterator it4 = metaFeats.iterator(); it4.hasNext();)
						{
							buffer.append(del);
							buffer.append(TAG_FEAT).append(':');
							buffer.append(EntityEncoder.encode(((Ability) it4.next()).getName()));
							del = "|";
						}

						buffer.append(']');
					}

					buffer.append('|');
					appendSourceInTaggedFormat(buffer, spellKey);
					buffer.append(LINE_SEP);
				}
			}
		}

//                                                  buffer.append((isCaster) ? TAG_SPELLNAME : TAG_POWERNAME).append(':');
//                                                  buffer.append(aSpell.getKeyName());
//                                                  buffer.append('|');
//                                                  buffer.append(TAG_SCHOOL).append(':');
//                                                  buffer.append(aSpell.getSchool());
//                                                  buffer.append('|');
//                                                  buffer.append(TAG_SUBSCHOOL).append(':');
//                                                  buffer.append(aSpell.getSubschool());
//                                                  buffer.append('|');
//                                                  buffer.append(TAG_COMP).append(':');
//                                                  buffer.append(aSpell.getComponentList());
//                                                  buffer.append('|');
//                                                  buffer.append(TAG_CT).append(':');
//                                                  buffer.append(aSpell.getCastingTime());
//                                                  buffer.append('|');
//                                                  buffer.append(TAG_DURATION).append(':');
//                                                  buffer.append(aSpell.getDuration());
//                                                  buffer.append('|');
//                                                  buffer.append(TAG_DESCRIPTOR).append(':');
//                                                  buffer.append(aSpell.getDescriptorList());
//                                                  buffer.append('|');
//                                                  buffer.append(TAG_RANGE).append(':');
//                                                  buffer.append(aSpell.getRange());
//                                                  buffer.append('|');
//                                                  buffer.append(TAG_EFFECT).append(':');
//                                                  buffer.append(aSpell.getEffect());
//                                                  buffer.append('|');
//                                                  buffer.append(TAG_EFFECTTYPE).append(':');
//                                                  buffer.append(aSpell.getEffectType());
//                                                  buffer.append('|');
//                                                  buffer.append(TAG_SAVE).append(':');
//                                                  buffer.append(aSpell.getSaveInfo());
//                                                  buffer.append('|');
//                                                  buffer.append(TAG_SR).append(':');
//                                                  buffer.append(aSpell.getSR());
//                                                  buffer.append(LINE_SEP);
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
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			final PCClass aClass = (PCClass) it.next();

			if ((aClass.getClassSpellList() != null) && (aClass.getClassSpellList().size() > 0))
			{
				buffer.append("SPELLLIST:");
				buffer.append(aClass.getName());

				for (Iterator ic = aClass.getClassSpellList().iterator(); ic.hasNext();)
				{
					buffer.append('|').append(ic.next().toString());
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
		for (Iterator i = aPC.getStatList().getStats().iterator(); i.hasNext();)
		{
			final PCStat aStat = (PCStat) i.next();
			buffer.append(TAG_STAT).append(':');
			buffer.append(aStat.getAbb());
			buffer.append('|');
			buffer.append(TAG_SCORE).append(':');
			buffer.append(aStat.getBaseScore());
			buffer.append(LINE_SEP);
		}
	}

	private void appendTabNameLine(StringBuffer buffer)
	{
		buffer.append(TAG_TABNAME).append(':');
		buffer.append(EntityEncoder.encode(aPC.getTabName()));
		buffer.append(LINE_SEP);
	}

	private void appendTempBonuses(StringBuffer buffer)
	{
		List trackList = new ArrayList();
		final List tmList = aPC.getTempBonusList();

		for (Iterator it = tmList.iterator(); it.hasNext();)
		{
			BonusObj aBonus = (BonusObj) it.next();
			Object creObj = aBonus.getCreatorObject();
			Object tarObj = aBonus.getTargetObject();
			String outString = tempBonusName(creObj, tarObj);

			if (trackList.contains(outString))
			{
				continue;
			}
			trackList.add(outString);

			String tarString = tempBonusName(creObj, tarObj);
			buffer.append(tarString);

			for (Iterator b = aPC.getTempBonusList().iterator(); b.hasNext();)
			{
				BonusObj bObj = (BonusObj) b.next();
				Object cObj = bObj.getCreatorObject();
				Object tObj = bObj.getTargetObject();
				String inString = tempBonusName(cObj, tObj);

				if (inString.equals(outString))
				{
					buffer.append('|');
					buffer.append(TAG_TEMPBONUSBONUS).append(':');
					buffer.append(EntityEncoder.encode(bObj.getPCCText()));
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
		PCTemplate aTemplate;
		String chosenFeats;

		for (Iterator it = aPC.getTemplateList().iterator(); it.hasNext();)
		{
			//
			// TEMPLATESAPPLIED:[NAME:<template_name>]
			// TEMPLATESAPPLIED:[NAME:<template_name>|CHOSENFEAT:[KEY:<key>|VALUE:<value>]CHOSENFEAT:[KEY:<key>|VALUE:<value>]...CHOSENFEAT:[KEY:<key>|VALUE:<value>]]
			//
			aTemplate = (PCTemplate) it.next();
			buffer.append(TAG_TEMPLATESAPPLIED).append(':').append('[');
			buffer.append(TAG_NAME).append(':').append(EntityEncoder.encode(aTemplate.getKeyName()));

			chosenFeats = chosenFeats(aTemplate);

			if (chosenFeats.length() != 0)
			{
				buffer.append('|').append(chosenFeats);
			}

			//
			// Save list of template names 'owned' by current template
			//
			for (Iterator itTempl = aTemplate.templatesAdded().iterator(); itTempl.hasNext();)
			{
				buffer.append('|').append(TAG_CHOSENTEMPLATE).append(':').append('[');
				buffer.append(TAG_NAME).append(':').append(EntityEncoder.encode((String)itTempl.next()));
				buffer.append(']');
			}

			buffer.append(']').append(LINE_SEP);
		}

		// TODO
		// don't we want to save more info here?
	}

	private void appendUseTempModsLine(StringBuffer buffer)
	{
		buffer.append(TAG_USETEMPMODS).append(':');
		buffer.append(aPC.getUseTempMods() ? "Y" : "N");
		buffer.append(LINE_SEP);
	}

	/*
	 * ###############################################################
	 * Character Weapon proficiencies methods
	 * ###############################################################
	 */
	private void appendWeaponProficiencyLines(StringBuffer buffer)
	{
		final int size = aPC.getWeaponProfList().size();

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

			for (Iterator it = aPC.getWeaponProfList().iterator(); it.hasNext(); ++j)
			{
				weaponProficiencies[j] = it.next().toString();
			}

			// as per Mynex's request do not write more than 10 weapons per line
			final int step = 10;
			final int times = (size / step) + (((size % step) > 0) ? 1 : 0);

			for (int k = 0; k < times; ++k)
			{
				buffer.append(TAG_WEAPONPROF).append(':');
				buffer.append('[');

				String del = "";
				int stop = Math.min(size, (k * step) + 10);

				for (int i = k * step; i < stop; ++i)
				{
					buffer.append(del);
					buffer.append(TAG_WEAPON).append(':');
					buffer.append(EntityEncoder.encode(weaponProficiencies[i]));
					del = "|";
				}

				buffer.append(']');
				buffer.append(LINE_SEP);
			}
		}

//		buffer.append(TAG_WEAPONPROF).append(':');
//		buffer.append('[');
//		String del = "";
//  		for (Iterator it = aPC.getWeaponProfList().iterator(); it.hasNext();)
//  		{
//			buffer.append(del);
//			buffer.append(TAG_WEAPON).append(':');
//  			buffer.append(it.next().toString());
//			del = "|";
//		}
//		buffer.append(']');
//		buffer.append(LINE_SEP);
		//
		// Save any selected racial bonus weapons
		//
		appendWeaponProficiencyLines(buffer, aPC.getRace());

		//
		// Save any selected class bonus weapons
		//
		for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
		{
			appendWeaponProficiencyLines(buffer, (PObject) e.next());
		}

		//
		// Save any selected domain bonus weapons
		//
		for (Iterator e = aPC.getCharacterDomainList().iterator(); e.hasNext();)
		{
			appendWeaponProficiencyLines(buffer, ((CharacterDomain) e.next()).getDomain());
		}

		//
		// Save any selected feat bonus weapons
		//
		for (Iterator e = aPC.getRealFeatsIterator(); e.hasNext();)
		{
			appendWeaponProficiencyLines(buffer, (PObject) e.next());
		}
	}

	private static void appendWeaponProficiencyLines(StringBuffer buffer, PObject source)
	{
		if (source == null) {
			return;
		}
		List l = source.getListFor(ListKey.SELECTED_WEAPON_PROF_BONUS);
		if (l == null || l.isEmpty()) {
			return;
		}

		// as per Mynex's request do not write more than 10 weapons per line
		final int step = 10;
		final int times = (l.size() / step) + 1;

		for (int k = 0; k < times; ++k)
		{
			buffer.append(TAG_WEAPONPROF).append(':');
			buffer.append('[');

			String del = "";
			int stop = Math.min(l.size(), (k * step) + 10);

			for (int i = k * step; i < stop; ++i)
			{
				buffer.append(del);
				buffer.append(TAG_WEAPON).append(':');
				buffer.append(EntityEncoder.encode((String) l.get(i)));
				del = "|";
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
		buffer.append(aPC.getGold().toString());
		buffer.append(LINE_SEP);
	}

	private void appendWeightLine(StringBuffer buffer)
	{
		buffer.append(TAG_WEIGHT).append(':');
		buffer.append(aPC.getWeight());
		buffer.append(LINE_SEP);
	}

	private String chosenFeats(PCTemplate aTemplate)
	{
		final StringBuffer aString = new StringBuffer(50);
		HashMap chosenFeatStrings = aTemplate.getChosenFeatStrings();

		if (chosenFeatStrings != null)
		{
			for (Iterator e = chosenFeatStrings.entrySet().iterator(); e.hasNext();)
			{
				final Map.Entry entry = (Map.Entry) e.next();

				if (aString.length() != 0)
				{
					aString.append('|');
				}

				aString.append(TAG_CHOSENFEAT).append(':');
				aString.append('[');
				aString.append(TAG_MAPKEY).append(':').append(EntityEncoder.encode(entry.getKey().toString())).append('|');
				aString.append(TAG_MAPVALUE).append(':').append(EntityEncoder.encode(entry.getValue().toString()));
				aString.append(']');
			}
		}

		return aString.toString();
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
		StringBuffer buffer;
		StringTokenizer tokens;

		String work = s + LINE_SEP;
		work = work.replace('\t', ' ');
		work = work.replace('\f', ' ');

		buffer = new StringBuffer();
		tokens = new StringTokenizer(work, "#");

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
		tokens = new StringTokenizer(work, "\r\n");

		while (tokens.hasMoreTokens())
		{
			buffer.append("# ").append(tokens.nextToken()).append(LINE_SEP);
		}

		return buffer.toString();
	}

	/**
	 * creates a unqiue tuple based on the creator and target getName()
	 * @param creator
	 * @param target
	 * @return temp bonus name
	 **/
	private String tempBonusName(Object creator, Object target)
	{
		StringBuffer cb = new StringBuffer();

		cb.append(TAG_TEMPBONUS).append(':');

		if (creator instanceof PObject)
		{
			PObject oCreator = (PObject) creator;

			if (oCreator instanceof Ability)
			{
				cb.append("FEAT=");
			}
			else if (oCreator instanceof Spell)
			{
				cb.append("SPELL=");
			}
			else if (oCreator instanceof Equipment)
			{
				cb.append("EQUIPMENT=");
			}
			else if (oCreator instanceof PCClass)
			{
				cb.append("CLASS=");
			}
			else if (oCreator instanceof PCTemplate)
			{
				cb.append("TEMPLATE=");
			}
			else if (oCreator instanceof Skill)
			{
				cb.append("SKILL=");
			}
			else
			{
				cb.append("ERROR=");
			}

			cb.append(EntityEncoder.encode(oCreator.getName()));
		}
		else
		{
			return "";
		}

		cb.append("|");
		cb.append(TAG_TEMPBONUSTARGET).append(':');

		if (target instanceof PlayerCharacter)
		{
			cb.append("PC");
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
	private void appendLevelAbilityInfo(StringBuffer buffer, PObject pObj)
	{
		appendLevelAbilityInfo(buffer, pObj, -10);
	}

	private void appendLevelAbilityInfo(StringBuffer buffer, PObject pObj, final int lvl)
	{
		List laList = pObj.getLevelAbilityList();
		if (laList != null)
		{
			for (Iterator it = laList.iterator(); it.hasNext();)
			{
				LevelAbility la = (LevelAbility) it.next();

				if ((la.level() - 1) != lvl)
				{
					continue;
				}

				if (la.getAssociatedCount() != 0)
				{
					//
					// |ABILITY:[PROMPT:blah|CHOICE:choice1|CHOICE:choice2|CHOICE:choice3...]
					//
					buffer.append('|').append(TAG_LEVELABILITY).append(":[").append(TAG_PROMPT).append(':')
					.append(EntityEncoder.encode(la.getTagData()));

					for (int j = 0; j < la.getAssociatedCount(true); ++j)
					{
						buffer.append('|').append(TAG_CHOICE).append(':').append(EntityEncoder.encode(
						        la.getAssociated(j, true)));
					}

					buffer.append(']');
				}
			}
		}
	}

}