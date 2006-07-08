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
import pcgen.core.pclevelinfo.PCLevelInfoStat;

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
		 * #Kits
		 */
		appendNewline(buffer);
		appendComment("Kits", buffer);
		appendKitLines(buffer);

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

		for ( Campaign campaign : Globals.getCampaignList() )
		{
			if (campaign.isLoaded())
			{
				buffer.append(del);
				buffer.append(TAG_CAMPAIGN).append(':');
				buffer.append(campaign.getKeyName());
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
		ListKey<String> selectedArmorProfListKey = ListKey.SELECTED_ARMOR_PROF;
		if ((aPC.getDeity() != null) && aPC.getDeity().containsListFor(selectedArmorProfListKey))
		{
			buffer.append(TAG_ARMORPROF).append(':').append(TAG_DEITY).append('=').append(aPC.getDeity().getKeyName())
			.append(':');

			for ( String prof : aPC.getDeity().getListFor(selectedArmorProfListKey) )
			{
				buffer.append(prof).append(':');
			}

			buffer.append(LINE_SEP);
		}

		for ( PCClass pcClass : aPC.getClassList() )
		{
			if (!pcClass.containsListFor(selectedArmorProfListKey))
			{
				continue;
			}

			buffer.append(TAG_ARMORPROF).append(':').append(TAG_CLASS).append('=').append(pcClass.getKeyName()).append(':');

			for ( String prof : pcClass.getListFor(selectedArmorProfListKey) )
			{
				buffer.append(prof).append(':');
			}

			buffer.append(LINE_SEP);
		}

		for ( Ability ability : aPC.aggregateFeatList() )
		{
			if (!ability.containsListFor(selectedArmorProfListKey))
			{
				continue;
			}

			buffer.append(TAG_ARMORPROF).append(':').append(TAG_FEAT).append('=').append(ability.getKeyName()).append(':');

			for ( String prof : ability.getListFor(selectedArmorProfListKey) )
			{
				buffer.append(prof).append(':');
			}

			buffer.append(LINE_SEP);
		}

		for ( Skill skill : aPC.getSkillList() )
		{
			if (!skill.containsListFor(selectedArmorProfListKey))
			{
				continue;
			}

			buffer.append(TAG_ARMORPROF).append(':').append(TAG_SKILL).append('=').append(skill.getKeyName()).append(':');

			for ( String prof : skill.getListFor(selectedArmorProfListKey) )
			{
				buffer.append(prof).append(':');
			}

			buffer.append(LINE_SEP);
		}

		for ( CharacterDomain cd : aPC.getCharacterDomainList() )
		{
			if ((cd.getDomain() == null) || !cd.getDomain().containsListFor(selectedArmorProfListKey))
			{
				continue;
			}

			buffer.append(TAG_ARMORPROF).append(':').append(TAG_DOMAIN).append('=').append(cd.getDomain().getKeyName())
			.append(':');

			for ( String prof : cd.getDomain().getListFor(selectedArmorProfListKey) )
			{
				buffer.append(prof).append(':');
			}

			buffer.append(LINE_SEP);
		}

		for ( Equipment eq : aPC.getEquipmentMasterList() )
		{
			if (!eq.isEquipped() || !eq.containsListFor(selectedArmorProfListKey))
			{
				continue;
			}

			buffer.append(TAG_ARMORPROF).append(':').append(TAG_EQUIPMENT).append('=').append(eq.getName()).append(':');

			for ( String prof : eq.getListFor(selectedArmorProfListKey) )
			{
				buffer.append(prof).append(':');
			}

			buffer.append(LINE_SEP);
		}

		for ( PCTemplate template : aPC.getTemplateList() )
		{
			if (!template.containsListFor(selectedArmorProfListKey))
			{
				continue;
			}

			buffer.append(TAG_ARMORPROF).append(':').append(TAG_TEMPLATE).append('=').append(template.getKeyName())
			.append(':');

			for ( String prof : template.getListFor(selectedArmorProfListKey) )
			{
				buffer.append(prof).append(':');
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
		buffer.append(EntityEncoder.encode(aPC.getMiscList().get(0)));
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
		buffer.append(EntityEncoder.encode(aPC.getMiscList().get(1)));
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
		buffer.append(EntityEncoder.encode(aPC.getMiscList().get(2)));
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

		for ( PCClass pcClass : aPC.getClassList() )
		{
			int classLevel = pcClass.getLevel();

			buffer.append(TAG_CLASS).append(':');
			buffer.append(EntityEncoder.encode(pcClass.getKeyName()));

			final String subClassKey = pcClass.getSubClassKey();

			if (!"".equals(subClassKey))
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
			buffer.append(pcClass.skillPool());

			// determine if this class can cast spells
			boolean isCaster = false;

			if (!pcClass.zeroCastSpells())
			{
				isCaster = true;
			}

			boolean isPsionic = (pcClass.getKnownList().size() > 0) && !isCaster;

			if (isCaster || isPsionic)
			{
				buffer.append('|');
				buffer.append(TAG_SPELLBASE).append(':');
				buffer.append(EntityEncoder.encode(pcClass.getSpellBaseStat()));
				buffer.append('|');
				buffer.append(TAG_CANCASTPERDAY).append(':');
				buffer.append(pcClass.getCastStringForLevel(classLevel));
			}

			final String prohibited = pcClass.getProhibitedString();

			if (!"".equals(prohibited))
			{
				buffer.append('|');
				buffer.append(TAG_PROHIBITED).append(':');
				buffer.append(EntityEncoder.encode(prohibited));
			}

			buffer.append(LINE_SEP);

			String key;
			key = pcClass.getKeyName() + TAG_SPECIALTY + '0';

			for ( String special : pcClass.getSpecialtyList() )
			{
				specials.put(key, special);
			}

			key = pcClass.getKeyName() + TAG_SAVE + '0';

			for ( String save : pcClass.getSafeListFor(ListKey.SAVE) )
			{
				final SpecialAbility specialAbility = pcClass.getSpecialAbilityKeyed(save);

				if (specialAbility != null)
				{
					int relevantLevel = 1;
					final String source = specialAbility.getSASource();

					try
					{
						relevantLevel = Integer.parseInt(source.substring(source.lastIndexOf('|') + 1));

						if (relevantLevel < 0)
						{
							relevantLevel = 1;
						}
					}
					catch (NumberFormatException nfe)
					{
						// nothing we can do about it
					}

					specials.put(pcClass.getKeyName() + TAG_SA + (relevantLevel - 1), specialAbility.getKeyName());
				}
				else
				{
					specials.put(key, save);
				}
			}
		}

		//
		// Save level up information in the order of levelling
		//
		for ( PCLevelInfo pcl : aPC.getLevelInfo() )
		{
			final String classKeyName = pcl.getClassKeyName();
			int lvl = pcl.getLevel() - 1;
			PCClass pcClass = aPC.getClassKeyed(classKeyName);
			buffer.append(TAG_CLASSABILITIESLEVEL).append(':');

			if (pcClass == null)
			{
				pcClass = Globals.getClassKeyed(classKeyName);

				if (pcClass != null)
				{
					pcClass = aPC.getClassKeyed(pcClass.getExClass());
				}
			}

			if (pcClass != null)
			{
				buffer.append(EntityEncoder.encode(pcClass.getKeyName()));
			}
			else
			{
				buffer.append(EntityEncoder.encode("???"));
			}

			buffer.append('=').append(lvl + 1);

			if (pcClass != null)
			{
				buffer.append('|');
				buffer.append(TAG_HITPOINTS).append(':');
				buffer.append(pcClass.getHitPoint(lvl).toString());
				appendSpecials(buffer, specials.get(pcClass.getKeyName() + TAG_SAVE + lvl), TAG_SAVES, TAG_SAVE, lvl);
				appendSpecials(buffer, specials.get(pcClass.getKeyName() + TAG_SPECIALTY + lvl), TAG_SPECIALTIES,
					TAG_SPECIALTY, lvl);
				appendSpecials(buffer, specials.get(pcClass.getKeyName() + TAG_SA + lvl), TAG_SPECIALABILITIES, TAG_SA,
					lvl);

				if (lvl == 0)
				{
					appendSpecials(buffer, specials.get(pcClass.getKeyName() + TAG_SA + (lvl - 1)),
						TAG_SPECIALABILITIES, TAG_SA, -1);
				}

				//
				// Remember what choices were made for each of the ADD: tags
				//
				appendLevelAbilityInfo(buffer, pcClass, lvl);
			}

			List<PCLevelInfoStat> statList = pcl.getModifiedStats(true);

			if (statList != null)
			{
				for ( PCLevelInfoStat stat : statList )
				{
					buffer.append('|').append(TAG_PRESTAT).append(':').append(stat.toString());
				}
			}

			statList = pcl.getModifiedStats(false);

			if (statList != null)
			{
				for ( PCLevelInfoStat stat : statList )
				{
					buffer.append('|').append(TAG_PRESTAT).append(':').append(stat.toString());
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

			for ( Domain domain : aDeity.getDomainList() )
			{
				buffer.append(del);
				buffer.append(TAG_DOMAIN).append(':');

				if (domain == null)
				{
					buffer.append(EntityEncoder.encode(Constants.s_NONE));
				}
				else
				{
					buffer.append(EntityEncoder.encode(domain.getKeyName()));
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

		for ( CharacterDomain cd : aPC.getCharacterDomainList() )
		{
			if (cd == null)
			{
				continue;
			}

			final Domain domain = cd.getDomain();

			if (domain == null)
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
			buffer.append(EntityEncoder.encode(domain.getKeyName()));

			for (int i = 0; i < domain.getAssociatedCount(); ++i)
			{
				buffer.append('|');
				buffer.append(TAG_ASSOCIATEDDATA).append(':');
				buffer.append(EntityEncoder.encode(domain.getAssociated(i)));
			}

			buffer.append('|');
			buffer.append(TAG_DOMAINGRANTS).append(':');
			buffer.append(EntityEncoder.encode(domain.getDescription()));
			buffer.append('|');
			appendSourceInTaggedFormat(buffer, cd.getDomainSourcePcgString());

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
		for ( EquipSet eSet : aPC.getEquipSet() )
		{
			if (eSet.useTempBonusList())
			{
				buffer.append(TAG_EQSETBONUS).append(':');
				buffer.append(eSet.getIdPath());

				List<String> trackList = new ArrayList<String>();

				for ( BonusObj bObj : eSet.getTempBonusList() )
				{
					final Object cObj = bObj.getCreatorObject();
					final Object tObj = bObj.getTargetObject();
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
		Equipment aEquip;

		for ( Equipment eq : aPC.getEquipmentMasterList() )
		{
			buffer.append(TAG_EQUIPNAME).append(':');
			buffer.append(EntityEncoder.encode(eq.getName()));
			buffer.append('|');
			buffer.append(TAG_OUTPUTORDER).append(':');
			buffer.append(eq.getOutputIndex());
			buffer.append('|');
			buffer.append(TAG_COST).append(':');
			buffer.append(eq.getCost(aPC).toString());
			buffer.append('|');
			buffer.append(TAG_WT).append(':');
			buffer.append(eq.getWeight(aPC).toString());
			buffer.append('|');
			buffer.append(TAG_QUANTITY).append(':');
			buffer.append(eq.qty());

			final String customization = eq.formatSaveLine("$", "=").trim();
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
		final List<EquipSet> eqSetList = aPC.getEquipSet();
		Collections.sort(eqSetList);

		for ( EquipSet eqSet : eqSetList )
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
				buffer.append(eqSet.getUseTempMods() ? "Y" : "N");
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
		final List<Ability> allFeats = aPC.getRealFeatsList();

		// Remember if this was a virtual feat or not
		final List<Ability> vFeats = new ArrayList<Ability>();

		for ( Ability vFeat : aPC.getVirtualFeatList() )
		{
			if (vFeat.needsSaving())
			{
				allFeats.add(vFeat); // add virtual feats with needsSaving to aList
				vFeats.add(vFeat);
			}
		}

		final List<String> removeList = buildLevelAbilityFeatList();

		final ListKey<String> saveListKey = ListKey.SAVE;

		for ( Ability feat : allFeats )
		{
			if (feat.isMultiples())
			{
				for (int it2 = 0; it2 < feat.getAssociatedCount(); ++it2)
				{
					// Check that the feat has not been output already.
					if (isInLevelAbilityList(removeList, feat, feat.getAssociated(it2)))
					{
						continue;
					}
					if (vFeats.contains(feat))
					{
						buffer.append(TAG_VFEAT);
					}
					else
					{
						buffer.append(TAG_FEAT);
					}

					buffer.append(':');
					buffer.append(EntityEncoder.encode(feat.getKeyName()));
					buffer.append('|');
					buffer.append(TAG_APPLIEDTO).append(':');

					if (feat.getAssociatedObject(0) instanceof FeatMultipleChoice)
					{
						buffer.append(TAG_MULTISELECT).append(':');
					}

					buffer.append(EntityEncoder.encode(feat.getAssociated(it2)));
					buffer.append('|');
					buffer.append(TAG_TYPE).append(':');
					buffer.append(EntityEncoder.encode(feat.getType()));
					int it3=0;
					int maxit3 = feat.getSizeOfListFor(saveListKey);
					if (feat.getAssociatedCount() == maxit3)
					{
						it3=it2;
						maxit3 = it3+1;
					}

					for (; it3 < maxit3; ++it3)
					{
						buffer.append('|');
						buffer.append(TAG_SAVE).append(':');
						buffer.append(EntityEncoder.encode(feat.getElementInList(saveListKey, it3)));
					}

					appendLevelAbilityInfo(buffer, feat);

					buffer.append('|');
					buffer.append(TAG_DESC).append(':');
					buffer.append(EntityEncoder.encode(feat.getDescription()));

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
				if (isInLevelAbilityList(removeList, feat, ""))
				{
					continue;
				}

				if (vFeats.contains(feat))
				{
					buffer.append(TAG_VFEAT);
				}
				else
				{
					buffer.append(TAG_FEAT);
				}

				buffer.append(':');
				buffer.append(EntityEncoder.encode(feat.getKeyName()));
				buffer.append('|');
				buffer.append(TAG_TYPE).append(':');
				buffer.append(EntityEncoder.encode(feat.getType()));

				if (feat.getAssociatedCount() > 0)
				{
					buffer.append('|');
					buffer.append(TAG_APPLIEDTO).append(':');

					if (feat.getAssociatedObject(0) instanceof FeatMultipleChoice)
					{
						buffer.append(TAG_MULTISELECT).append(':');
					}

					buffer.append(EntityEncoder.encode(feat.getAssociated(0)));
				}
				for ( String save : feat.getSafeListFor(saveListKey) )
				{
					buffer.append('|');
					buffer.append(TAG_SAVE).append(':');
					buffer.append( EntityEncoder.encode(save) );
				}

				appendLevelAbilityInfo(buffer, feat);

				buffer.append('|');
				buffer.append(TAG_DESC).append(':');
				buffer.append(EntityEncoder.encode(feat.getDescription()));

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
		allFeats.removeAll(vFeats);
	}

	/**
	 * Build up a list of names of feat choices that will have been written
	 * out in the class abilities section (i.e. Feats that were class abilities
	 * such as fighter feats.) These should nto be written out again in the
	 * feats section toherwise they can multiply...
	 * @return List of level ability feat choices as Strings
	 */
	private List<String> buildLevelAbilityFeatList()
	{
		List<String> removeList = new ArrayList<String>();
		for ( PCLevelInfo pcl : aPC.getLevelInfo() )
		{
			final String classKeyName = pcl.getClassKeyName();
			final int lvl = pcl.getLevel() - 1;
			final PCClass aClass = aPC.getClassKeyed(classKeyName);
			final List<LevelAbility> laList = aClass.getLevelAbilityList();
			if (aClass != null && laList != null)
			{
				for ( LevelAbility la : laList )
				{
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
	private boolean isInLevelAbilityList(List<String> laList, Ability feat,
		String associated)
	{
		String matchString = feat.getKeyName();
		if (associated != null && associated.length() > 0)
		{
			matchString += "(" + associated + ")";
		}
		for (Iterator<String> laIter = laList.iterator(); laIter.hasNext();)
		{
			final String ability = laIter.next();
			if (matchString.equals(ability)
				&& (feat.getAssociatedCount() == 0 || (associated != null && associated
					.length() > 0)))
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

		final List<Follower> followers = aPC.getFollowerList();

		if (!followers.isEmpty())
		{
			for ( Follower follower : followers )
			{
				buffer.append(TAG_FOLLOWER).append(':');
				buffer.append(EntityEncoder.encode(follower.getName()));
				buffer.append('|');
				buffer.append(TAG_TYPE).append(':');
				buffer.append(EntityEncoder.encode(follower.getType()));
				buffer.append('|');
				buffer.append(TAG_RACE).append(':');
				buffer.append(EntityEncoder.encode(follower.getRace().toUpperCase()));
				buffer.append('|');
				buffer.append(TAG_HITDICE).append(':');
				buffer.append(follower.getUsedHD());
				buffer.append('|');
				buffer.append(TAG_FILE).append(':');
				buffer.append(EntityEncoder.encode(follower.getRelativeFileName()));
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
			for ( Kit kit : aPC.getKitInfo() )
			{
				buffer.append(TAG_KIT).append(':').append(kit.getKeyName()).append(LINE_SEP);
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

		for ( Language lang : aPC.getLanguagesList() )
		{
			buffer.append(del);
			buffer.append(TAG_LANGUAGE).append(':');
			buffer.append(EntityEncoder.encode(lang.getKeyName()));
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
		for ( NoteItem ni : aPC.getNotesList() )
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

	private void appendHeightLine(StringBuffer buffer)
	{
		buffer.append(TAG_HEIGHT).append(':');
		buffer.append(aPC.getHeight());
		buffer.append(LINE_SEP);
	}

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
			buffer.append(EntityEncoder.encode(SettingsHandler.getSelectedCharacterHTMLOutputSheet(null)));
			buffer.append(LINE_SEP);
			buffer.append(TAG_PDFOUTPUTSHEET).append(':');
			buffer.append(EntityEncoder.encode(SettingsHandler.getSelectedCharacterPDFOutputSheet(null)));
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
		buffer.append(source.getKeyName());
		buffer.append(']');
	}

	private static void appendSpecials(StringBuffer buffer, List<String> specials, String tag_group, String tag_item, int lvl)
	{
		if ((specials != null) && (!specials.isEmpty()))
		{
			buffer.append('|');
			buffer.append(tag_group).append(':');
			buffer.append('[');

			String del = "";

			for ( String special : specials )
			{
				buffer.append(del);
				buffer.append(tag_item).append(':');
				buffer.append(EntityEncoder.encode(special));

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

		for ( Skill skill : aPC.getSkillList() )
		{
			if ((skill.getRank().doubleValue() > 0) || (skill.getOutputIndex() != 0))
			{
				buffer.append(TAG_SKILL).append(':');
				buffer.append(EntityEncoder.encode(skill.getKeyName()));

				buffer.append('|');
				buffer.append(TAG_OUTPUTORDER).append(':');
				buffer.append(skill.getOutputIndex());
				buffer.append('|');

				for ( String classRanks : skill.getRankList() )
				{
					int index = classRanks.indexOf(':');
					final String className = classRanks.substring(0, index);
					final String ranks = classRanks.substring(index + 1);

					final PCClass pcClass = aPC.getClassKeyed(className);

					buffer.append(TAG_CLASSBOUGHT).append(':');
					buffer.append('[');
					buffer.append(TAG_CLASS).append(':');
					buffer.append(EntityEncoder.encode(className));
					buffer.append('|');
					buffer.append(TAG_RANKS).append(':');
					buffer.append(ranks);
					buffer.append('|');
					buffer.append(TAG_COST).append(':');
					buffer.append(Integer.toString(skill.costForPCClass(pcClass, aPC)));
					buffer.append('|');
					buffer.append(TAG_CLASSSKILL).append(':');
					buffer.append((skill.isClassSkill(pcClass, aPC)) ? 'Y' : 'N');
					buffer.append(']');
				}

				for (int i = 0; i < skill.getAssociatedCount(); ++i)
				{
					buffer.append('|');
					buffer.append(TAG_ASSOCIATEDDATA).append(':');
					buffer.append(EntityEncoder.encode(skill.getAssociated(i)));
				}

				appendLevelAbilityInfo(buffer, skill);

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
		for ( String bookName : aPC.getSpellBooks() )
		{
			if (!bookName.equals(Globals.getDefaultSpellBook()) &&
					!bookName.equals(Globals.INNATE_SPELL_BOOK_NAME))
			{
				final SpellBook book = aPC.getSpellBookByName(bookName);
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
		String del;

		for ( PCClass pcClass : aPC.getClassList() )
		{
			for ( CharacterSpell cSpell : pcClass.getSpellSupport().getCharacterSpell(null, "", -1) )
			{
				for (Iterator<SpellInfo> it3 = cSpell.getInfoListIterator(); it3.hasNext();)
				{
					final SpellInfo spellInfo = it3.next();
					final String spellKey = cSpell.getOwner().getSpellKey();

					if (spellInfo.getBook().equals(Globals.getDefaultSpellBook())
						&& pcClass.isAutoKnownSpell(cSpell.getSpell().getKeyName(),
							cSpell.getSpell().getFirstLevelForKey(spellKey, aPC), aPC) && aPC.getAutoSpells())
					{
						continue;
					}

					buffer.append(TAG_SPELLNAME).append(':');
					buffer.append(EntityEncoder.encode(cSpell.getSpell().getKeyName()));
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
					if (Spell.hasPPCost())
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
						del = "";

						for ( Ability feat : metaFeats )
						{
							buffer.append(del);
							buffer.append(TAG_FEAT).append(':');
							buffer.append( EntityEncoder.encode(feat.getKeyName()) );
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
		for ( PCClass pcClass : aPC.getClassList() )
		{
			if ((pcClass.getClassSpellList() != null) && (pcClass.getClassSpellList().size() > 0))
			{
				buffer.append("SPELLLIST:");
				buffer.append(pcClass.getKeyName());

				for ( String spell : pcClass.getClassSpellList() )
				{
					buffer.append('|').append(spell);
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
		for (Iterator<PCStat> i = aPC.getStatList().iterator(); i.hasNext();)
		{
			final PCStat aStat = i.next();
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
		final List<String> trackList = new ArrayList<String>();
		for ( BonusObj bonus : aPC.getTempBonusList() )
		{
			final Object creObj = bonus.getCreatorObject();
			final Object tarObj = bonus.getTargetObject();
			final String outString = tempBonusName(creObj, tarObj);

			if (trackList.contains(outString))
			{
				continue;
			}
			trackList.add(outString);

			// TODO Isn't this the same as outString?
			final String tarString = tempBonusName(creObj, tarObj);
			buffer.append(tarString);

			// TODO Why do we loop through the bonuses again?
			for ( BonusObj subBonus : aPC.getTempBonusList() )
			{
				final Object cObj = subBonus.getCreatorObject();
				final Object tObj = subBonus.getTargetObject();
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
		for ( PCTemplate template : aPC.getTemplateList() )
		{
			//
			// TEMPLATESAPPLIED:[NAME:<template_name>]
			// TEMPLATESAPPLIED:[NAME:<template_name>|CHOSENFEAT:[KEY:<key>|VALUE:<value>]CHOSENFEAT:[KEY:<key>|VALUE:<value>]...CHOSENFEAT:[KEY:<key>|VALUE:<value>]]
			//
			buffer.append(TAG_TEMPLATESAPPLIED).append(':').append('[');
			buffer.append(TAG_NAME).append(':').append(EntityEncoder.encode(template.getKeyName()));

			final String chosenFeats = chosenFeats(template);

			if (chosenFeats.length() != 0)
			{
				buffer.append('|').append(chosenFeats);
			}

			//
			// Save list of template names 'owned' by current template
			//
			for ( String ownedTemplate : template.templatesAdded() )
			{
				buffer.append('|').append(TAG_CHOSENTEMPLATE).append(':').append('[');
				buffer.append(TAG_NAME).append(':').append(EntityEncoder.encode(ownedTemplate));
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

			for ( WeaponProf wp : aPC.getWeaponProfList() )
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

		//
		// Save any selected racial bonus weapons
		//
		appendWeaponProficiencyLines(buffer, aPC.getRace());

		//
		// Save any selected class bonus weapons
		//
		for ( PCClass pcClass : aPC.getClassList() )
		{
			appendWeaponProficiencyLines(buffer, pcClass);
		}

		//
		// Save any selected domain bonus weapons
		//
		for ( CharacterDomain cd : aPC.getCharacterDomainList() )
		{
			appendWeaponProficiencyLines(buffer, cd.getDomain());
		}

		//
		// Save any selected feat bonus weapons
		//
		for ( Ability feat : aPC.getRealFeatsList() )
		{
			appendWeaponProficiencyLines(buffer, feat);
		}
	}

	private static void appendWeaponProficiencyLines(StringBuffer buffer, PObject source)
	{
		if (source == null) {
			return;
		}
		final List<String> profs = source.getListFor(ListKey.SELECTED_WEAPON_PROF_BONUS);
		if (profs == null || profs.isEmpty()) {
			return;
		}

		// TODO refactor this and the code above that calls it so share this
		// as per Mynex's request do not write more than 10 weapons per line
		final int step = 10;
		final int times = (profs.size() / step) + 1;

		for (int k = 0; k < times; ++k)
		{
			buffer.append(TAG_WEAPONPROF).append(':');
			buffer.append('[');

			String del = "";
			int stop = Math.min(profs.size(), (k * step) + 10);

			for (int i = k * step; i < stop; ++i)
			{
				buffer.append(del);
				buffer.append(TAG_WEAPON).append(':');
				buffer.append(EntityEncoder.encode(profs.get(i)));
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
		final HashMap<String, String> chosenFeatStrings = aTemplate.getChosenFeatStrings();

		if (chosenFeatStrings != null)
		{
			for ( Map.Entry<String, String> entry : chosenFeatStrings.entrySet() )
			{
				if (aString.length() != 0)
				{
					aString.append('|');
				}

				aString.append(TAG_CHOSENFEAT).append(':');
				aString.append('[');
				aString.append(TAG_MAPKEY).append(':').append(EntityEncoder.encode(entry.getKey())).append('|');
				aString.append(TAG_MAPVALUE).append(':').append(EntityEncoder.encode(entry.getValue()));
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
		String work = s + LINE_SEP;
		work = work.replace('\t', ' ');
		work = work.replace('\f', ' ');

		StringBuffer buffer = new StringBuffer();
		StringTokenizer tokens = new StringTokenizer(work, "#");

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
	private String tempBonusName(final Object creator, Object target)
	{
		final StringBuffer cb = new StringBuffer();

		cb.append(TAG_TEMPBONUS).append(':');

		if (creator instanceof PObject)
		{
			final PObject oCreator = (PObject) creator;

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

			cb.append(EntityEncoder.encode(oCreator.getKeyName()));
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
		final List<LevelAbility> laList = pObj.getLevelAbilityList();
		if (laList != null)
		{
			for ( LevelAbility la : laList )
			{
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
