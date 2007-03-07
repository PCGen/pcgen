/*
 * VariableProcessorgetPc().java
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 * Created on 13-Dec-2004
 */
package pcgen.core;

import pcgen.core.character.Follower;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.io.exporttoken.EqTypeToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>VariableProcessorPC</code> is a processor for variables
 * associated with a character. This class converts formulas or
 * variables into values and is used extensively both in
 * defintions of objects and for output to output sheets.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author Chris Ward <frugal@purplewombat.co.uk>
 * @version $Revision$
 */
public class VariableProcessorPC extends VariableProcessor
{

	/**
	 * Create a new VariableProcessorPC instance for the character.
	 *
	 * @param pc The character to be processed.
	 */
	public VariableProcessorPC(PlayerCharacter pc)
	{
		super(pc);
	}

	/**
	 * @see pcgen.core.VariableProcessor#lookupVariable(java.lang.String, java.lang.String, pcgen.core.spell.Spell)
	 */
	public Float lookupVariable(String element, String src, Spell spell)
	{
		Float retVal = null;
		if (getPc().hasVariable(element))
		{
			final Float value = getPc().getVariable(element, true, true, src, "", decrement);
			Logging.debugPrint(jepIndent + "variable for: '" + element + "' = " + value);
			retVal = new Float(value.doubleValue());
		}

		if (retVal==null) {
			final String foo = getInternalVariable(spell, element, src);
			if (foo != null)
			{
				Float d=null;
				try
				{
					d = new Float(foo);
				}
				catch (NumberFormatException nfe)
				{
					// What we got back was not a number
				}
				if (d != null) {
					if (!d.isNaN())
					{
						retVal = d;
						Logging.debugPrint(jepIndent + "internal variable for: '" + element + "' = " + d);
					}
				}
			}
		}

		if (retVal==null) {
			final String foo = getExportVariable(element);
			if (foo != null)
			{
				Float d=null;
				try
				{
					d = new Float(foo);
				}
				catch (NumberFormatException nfe)
				{
					// What we got back was not a number
				}
				if (d != null) {
					if (!d.isNaN())
					{
						retVal = d;
						Logging.debugPrint(jepIndent + "export variable for: '" + element + "' = " + d);
					}
				}
			}
		}

		return retVal;
	}

	/**
	 * Count the number of times the character has the feat. This can be limited
	 * to either hidden or visible feats, and can be limited to only counting
	 * once per feat rather than once per time taken (e.g. Weapon specialisation
	 * in two weapons would count as 2 unless the onceOnly flag was true).
	 *
	 * @param feat The feat to be counted.
	 * @param countVisible Should it be counted if it is visible?
	 * @param countHidden Should it be counted if it is hidden?
	 * @param onceOnly Should it be counted as one if was taken multiple times?
	 * @return The number of occurrences of the feat.
	 */
	private int countVisibleFeat(final Ability feat, final boolean countVisible, final boolean countHidden, final boolean onceOnly)
	{
		int count = 0;

		if (countVisible)
		{
			if ((feat.getVisibility() != Visibility.DISPLAY_ONLY) && (feat.getVisibility() != Visibility.HIDDEN))
			{
				if (onceOnly)
				{
					count++;
				}
				else
				{
					count += Math.max(1, feat.getAssociatedCount());
				}
			}
		}

		if (countHidden)
		{
			if ((feat.getVisibility() == Visibility.DISPLAY_ONLY) || (feat.getVisibility() == Visibility.HIDDEN))
			{
				if (onceOnly)
				{
					count++;
				}
				else
				{
					count += Math.max(1, feat.getAssociatedCount());
				}
			}
		}

		return count;
	}

	/**
	 * This function takes a list of feats and a list of types and returns
	 * the number of visible, or hidden feats that are in the list and of
	 * one of the specified types. The visible flag determines if
	 * the result should be the number of hidden feats, or the number of
	 * visible feats
	 *
	 * @param featsList The feats to look through.
	 * @param featTypesList The feat types to limit the search to.
	 * @param countVisible  Count visible feats
	 * @param countHidden Count hidden feats
	 * @return  An int containing the number of matching feats in the list
	 */
	private int countVisibleFeatTypes(final List<Ability> featsList, final List<String> featTypesList, final boolean countVisible, final boolean countHidden) {
		int count = 0;

		for ( Ability feat : featsList )
		{
			// for each feat, look to see if it has any of the required types.
			for ( String featType : featTypesList )
			{
				if (feat.isType(featType))
				{
					count += countVisibleFeat(feat, countVisible, countHidden, false);

					break;
				}
			}
		}

		return count;
	}

	/**
	 * This function takes a list of feats and a name and returns
	 * the number of visible, or hidden feats that are in the list and
	 * have the requested name. The visible flag determines if
	 * the result should be the number of hidden feats, or the number of
	 * visible feats
	 *
	 * @param argFeatList The feats to look through.
	 * @param featName The name of the feat to find.
	 * @param countVisible  Count visible feats
	 * @param countHidden Count hidden feats
	 * @return  An int containing the number of matching feats in the list
	 */
	private int countVisibleFeatsOfKey(final List<Ability> argFeatList, final String featKey, final boolean countVisible, final boolean countHidden) {
		int count = 0;

		for ( Ability feat : argFeatList )
		{
			if (feat.getKeyName().equalsIgnoreCase(featKey))
			{
				count += countVisibleFeat(feat, countVisible, countHidden, false);

				break;
			}
		}

		return count;
	}

	/**
	 * This function takes a list of feats and returns the number of visible,
	 * or hidden feats that are in the list The visible flag determines if
	 * the result should be the number of hidden feats, or the number of
	 * visible feats
	 *
	 * @param aList a list of the feats to look through.
	 * @param countVisible  Count visible feats
	 * @param countHidden Count hidden feats
	 * @return  An int containing the number of feats in the list
	 */
	private int countVisibleFeats(final List<Ability> aList, final boolean countVisible, final boolean countHidden) {
		int count = 0;

		for (Ability feat : aList)
		{
			count += countVisibleFeat(feat, countVisible, countHidden, true);
		}

		return count;
	}

	/**
	 * Retrieve a pre-coded variable for a character. These are known properties of
	 * all character.
	 *
	 * @param aSpell  This is specifically to compute bonuses to CASTERLEVEL for a specific spell.
	 * @param valString The variable to be evaluated
	 * @param src The source within which the variable is evaluated
	 * @return The value of the variable
	 */
	protected String getInternalVariable(final Spell aSpell, String valString, final String src)
	{
		if (!Globals.checkRule(RuleConstants.SYS_LDPACSK))
		{
			// Need to make sure the MXDXEN value
			// is 1000 to ensure no Dex penalty
			// to AC for load > LIGHT
			if (valString.equals(getPc().getStatList().getPenaltyVar("DEX")))
			{
				valString = "1000";
			}
		}


		if ("SCORE".equals(valString) && src.startsWith("STAT:"))
		{
			valString = String.valueOf(getPc().getStatList().getTotalStatFor(src.substring(5)));
		}
		else if ("SPELLBASESTATSCORE".equals(valString))
		{
			final PCClass aClass = getPc().getClassKeyed(src.substring(6));

			if (aClass != null)
			{
				valString = aClass.getSpellBaseStat() + "SCORE";

				if ("SPELLSCORE".equals(valString))
				{
					valString = "10";
				}
			}
			else
			{
				valString = "0";
			}
		}
		else if ("SPELLBASESTAT".equals(valString))
		{
			final PCClass aClass = getPc().getClassKeyed(src.substring(6));

			if (aClass != null)
			{
				valString = aClass.getSpellBaseStat();

				if ("SPELL".equals(valString))
				{
					valString = "0";
				}
			}
			else
			{
				valString = "0";
			}
		}
		else if ("BASESPELLSTAT".equals(valString))
		{
			PCClass aClass = null;

			// if there's no class, then assume a basespellstat modifier of 0
			if (src.length() < 7)
			{
				valString = "0";
			}
			else
			{
				// src should be CLASS. something
				aClass = getPc().getClassKeyed(src.substring(6));
			}

			if (aClass != null)
			{
				valString = String.valueOf(getPc().getBaseSpellStatBonus(aClass));
			}
		}
		else if ("SPELLLEVEL".equals(valString))
		{
			valString = String.valueOf(getPc().getSpellLevelTemp());
		}

		else if ((valString.length() > 0) && (SettingsHandler.getGame().getStatFromAbbrev(valString) > -1))
		{
			// TODO: JSC -- more testing!
			// Big Changes here!
			final int iX = SettingsHandler.getGame().getStatFromAbbrev(valString);
			final int statNum = getPc().getStatList().getTotalStatFor(valString);
			final int statMod = getPc().getStatList().getModForNumber(statNum, iX);
			valString = Integer.toString(statMod);

			//valString = Integer.toString(statList.getStatModFor(valString));
		}
		else if ((valString.length() == 8) && (SettingsHandler.getGame().getStatFromAbbrev(valString.substring(0, 3)) > -1)
				&& valString.endsWith(".BASE"))
		{
			valString = Integer.toString(getPc().getStatList().getBaseStatFor(valString.substring(0, 3)));
		}
		else if ((valString.length() >= 8) && valString.substring(3).startsWith("SCORE"))
		{
			if (valString.endsWith(".BASE"))
			{
				valString = Integer.toString(getPc().getStatList().getBaseStatFor(valString.substring(0, 3)));
			}
			else
			{
				valString = Integer.toString(getPc().getStatList().getTotalStatFor(valString.substring(0, 3)));
			}
		}
		else if ("CASTERLEVEL".equals(valString) && src.startsWith("RACE:"))
		{
			final int iLev = getPc().getTotalCasterLevelWithSpellBonus(aSpell, Constants.s_NONE, "RACE." + src.substring(5), 0);

			if (iLev > 0)
			{
				valString = Integer.toString(iLev);
			}

			// If no CASTERLEVEL has been defined for this race then don't substitute
			// so it will be apparent on the output sheet that it needs to be defined..
		}
		else if ("CASTERLEVEL.TOTAL".equals(valString) || ("CASTERLEVEL".equals(valString) && !src.startsWith("CLASS:")))
		{

			int iLev = 0;

			for ( PCClass pcClass : getPc().getClassList() )
			{
				if (!pcClass.getSpellType().equals(Constants.s_NONE))
				{
					final String classKey = pcClass.getKeyName();
					String spellType = Constants.s_NONE;
					final int pcBonusLevel = (int) getPc().getTotalBonusTo("PCLEVEL", classKey);

					if ((pcClass != null) && (!pcClass.getSpellType().equals(Constants.s_NONE)))
					{
						spellType = pcClass.getSpellType();
					}

					if (CoreUtility.doublesEqual(getPc().getTotalBonusTo("CASTERLEVEL", classKey), 0.0))
					{
						final int iClass = Integer.parseInt(getPc().getClassLevelString(classKey, false));
						iLev += getPc().getTotalCasterLevelWithSpellBonus(aSpell, spellType, classKey, iClass + pcBonusLevel);
					}
					else
					{
						iLev += getPc().getTotalCasterLevelWithSpellBonus(aSpell, spellType, classKey, pcBonusLevel);
					}
				}
			}

			valString = Integer.toString(iLev);
		}
		else if ("CASTERLEVEL".equals(valString) && src.startsWith("CLASS:"))
		{
			String classKey = src.substring(6);

			// check if this is a domain spell
			final CharacterDomain aCD = getPc().getCharacterDomainForDomain(classKey);
			if (aCD != null)  //yup, it's a domain alright
			{
				classKey = aCD.getObjectName(); //returns Domain source (e.g, "Cleric")
			}

			final PCClass spClass = Globals.getClassKeyed(classKey);

			String spellType = Constants.s_NONE;
			if ((spClass != null) && (!spClass.getSpellType().equals(Constants.s_NONE)))
			{
				spellType = spClass.getSpellType();
			}

			final int pcBonusLevel = (int) getPc().getTotalBonusTo("PCLEVEL", classKey);

			int iLev = (int) getPc().getTotalBonusTo("CASTERLEVEL", classKey);

			if (iLev == 0)
			{
				// If no CASTERLEVEL has been
				// defined for this class then
				// use total class level instead
				iLev = Integer.parseInt(getPc().getClassLevelString(classKey, false));
				iLev = getPc().getTotalCasterLevelWithSpellBonus(aSpell, spellType, classKey, iLev + pcBonusLevel);
			}
			else
			{
				iLev = getPc().getTotalCasterLevelWithSpellBonus(aSpell, spellType, classKey, pcBonusLevel);
			}

			valString = Integer.toString(iLev);
		}
		else if ("CASTERLEVEL".equals(valString))
		{
			Logging.debugPrint("src for CASTERLEVEL: " + src);
		}
		else if ("CL".equals(valString) && src.startsWith("CLASS:"))
		{
			valString = getPc().getClassLevelString(src.substring(6), false);
		}
		else if (valString.startsWith("CL=") || valString.startsWith("CL."))
		{
			valString = getPc().getClassLevelString(valString.substring(3), false);
		}
		else if ((valString.startsWith("CL;BEFORELEVEL=") || valString.startsWith("CL;BEFORELEVEL."))
		&& src.startsWith("CLASS:"))
		{
			valString = getPc().getClassLevelString(src.substring(6) + valString.substring(2), false);
		}
		else if ("BL".equals(valString) && src.startsWith("CLASS:"))
		{
			valString = Integer.toString((int) getPc().getTotalBonusTo("PCLEVEL", src.substring(6)));
		}
		else if (valString.startsWith("BL=") || valString.startsWith("BL."))
		{
			valString = Integer.toString((int) getPc().getTotalBonusTo("PCLEVEL", valString.substring(3)));
		}
		else if ("BAB".equals(valString))
		{
			valString = Integer.toString(getPc().baseAttackBonus());
		}
		else if (valString.startsWith("CLASSLEVEL=") || valString.startsWith("CLASSLEVEL."))
		{
			valString = getPc().getClassLevelString(valString.substring(11), true);
		}
		else if (valString.startsWith("CLASS=") || valString.startsWith("CLASS."))
		{
			PCClass aClass = null;

			if (valString.length() > 6)
			{
				aClass = getPc().getClassKeyed(valString.substring(6));
			}
			else
			{
				Logging.errorPrint("Error! Cannot determine CLASS!");
			}

			if (aClass != null)
			{
				valString = "1";
			}
			else
			{
				valString = "0";
			}
		}
		else if (valString.startsWith("SKILLRANK=") || valString.startsWith("SKILLRANK."))
		{
			final Skill aSkill = getPc().getSkillKeyed(valString.substring(10).replace('{', '(').replace('}', ')'));
			if (aSkill != null)
			{
				valString = aSkill.getRank().toString();
			}
			else
			{
				valString = "0";
			}
		}
		else if (valString.startsWith("SKILLTOTAL=") || valString.startsWith("SKILLTOTAL."))
		{
			final Skill aSkill = getPc().getSkillKeyed(valString.substring(11).replace('{', '(').replace('}', ')'));
			if (aSkill != null)
			{
				valString = Integer.toString(aSkill.getTotalRank(getPc()).intValue() + aSkill.modifier(getPc()).intValue());
			}
			else
			{
				valString = "0";
			}
		}
		else if ("TL".equals(valString))
		{
			valString = Integer.toString(getPc().getTotalLevels());
		}
		else if ("HD".equals(valString))
		{
			// check companionModList?
			valString = Integer.toString(getPc().totalHitDice());
		}
		else if ("PROFACCHECK".equals(valString) && src.startsWith("EQ:"))
		{
			final Equipment eq = EquipmentList.getEquipmentNamed(src.substring(3));

			if ((eq != null) && !getPc().isProficientWith(eq))
			{
				valString = Integer.toString(eq.acCheck(getPc()).intValue());
			}
			else
			{
				valString = "0";
			}
		}
		else if ("ACCHECK".equals(valString) || "ACHECK".equals(valString))
		{
			int maxCheck = 0;

			for ( Equipment eq : getPc().getEquipmentOfType("Armor", 1) )
			{
				maxCheck += eq.acCheck(getPc()).intValue();
			}

			for ( Equipment eq : getPc().getEquipmentOfType("Shield", 1) )
			{
				maxCheck += eq.acCheck(getPc()).intValue();
			}

			valString = Integer.toString(maxCheck);
		}
		else if ("ARMORACCHECK".equals(valString) || "ARMORACHECK".equals(valString))
		{
			int maxCheck = 0;

			for ( Equipment eq : getPc().getEquipmentOfType("Armor", 1) )
			{
				maxCheck += eq.acCheck(getPc()).intValue();
			}

			valString = Integer.toString(maxCheck);
		}
		else if ("SHIELDACCHECK".equals(valString) || "SHIELDACHECK".equals(valString))
		{
			int maxCheck = 0;

			for ( Equipment eq : getPc().getEquipmentOfType("Shield", 1) )
			{
				maxCheck += eq.acCheck(getPc()).intValue();
			}

			valString = Integer.toString(maxCheck);
		}
		else if ("SIZE".equals(valString))
		{
			valString = String.valueOf(getPc().sizeInt());
		}
		else if ("SIZEMOD".equals(valString))
		{
			valString = String.valueOf((int) getPc().getSizeAdjustmentBonusTo("COMBAT", "AC"));
		}
		else if ("RACESIZE".equals(valString))
		{
			valString = String.valueOf(Globals.sizeInt(getPc().getRace().getSize()));
		}
		else if ("ENCUMBERANCE".equals(valString))
		{
			final int loadScore = getVariableValue(null, "LOADSCORE", "", getPc().getSpellLevelTemp()).intValue();
			valString = String.valueOf(Globals.loadTypeForLoadScore( loadScore, getPc().totalWeight(), getPc()));
		}
		else if ("MOVEBASE".equals(valString))
		{
			valString = getPc().getRace().getMovement().getDoubleMovement().toString();
		}
		else if (valString.startsWith("MOVE["))
		{
			final String moveString = valString.substring(5, valString.lastIndexOf("]"));
			valString = String.valueOf(getPc().movementOfType(moveString));
		}
		else if ("COUNT[ATTACKS]".equals(valString))
		{
			valString = Integer.toString(getPc().getNumAttacks());
		}
		else if ("COUNT[CHECKS]".equals(valString))
		{
			valString = String.valueOf(SettingsHandler.getGame().getUnmodifiableCheckList().size());
		}
		else if ("COUNT[FOLLOWERS]".equals(valString))
		{
			valString = Integer.toString(getPc().getFollowerList().size());
		}
		else if ("COUNT[STATS]".equals(valString))
		{
			valString = Integer.toString(SettingsHandler.getGame().s_ATTRIBLONG.length);
		}
		else if ("COUNT[SKILLS]".equals(valString))
		{
			// We use the list in output order to ensure the size
			// does not include hidden skills
			final ArrayList<Skill> skillList = getPc().getSkillListInOutputOrder();
			skillList.trimToSize();
			valString = Integer.toString(skillList.size());
		}
		else if (valString.startsWith("COUNT[SKILLTYPE=") || valString.startsWith("COUNT[SKILLTYPE."))
		{
			if (valString.endsWith("]"))
			{
				final List<Skill> skillList = getPc().getSkillListInOutputOrder( new ArrayList<Skill>(getPc().getAllSkillList(true)));

				int typeCount = 0;
				valString = valString.substring(16);
				valString = valString.substring(0, valString.length() - 1);
				for ( Skill skill : skillList )
				{
					if (skill.isType(valString))
					{
						++typeCount;
					}
				}
				valString = Integer.toString(typeCount);
			}
		}
		else if ("COUNT[FEATS.ALL]".equals(valString))
		{
			valString = Integer.toString(getPc().getNumberOfRealAbilities(AbilityCategory.FEAT));
		}
		else if ("COUNT[FEATS.HIDDEN]".equals(valString))
		{
			valString = Integer.toString(countVisibleFeats(getPc().getRealFeatList(), false, true));
		}
		else if ("COUNT[FEATS]".equals(valString) || "COUNT[FEATS.VISIBLE]".equals(valString))
		{
			valString = Integer.toString(countVisibleFeats(getPc().getRealFeatList(), true, false));
		}
		else if ("COUNT[VFEATS.ALL]".equals(valString))
		{
			valString = Integer.toString(getPc().getVirtualFeatList().size());
		}
		else if ("COUNT[VFEATS.HIDDEN]".equals(valString))
		{
			valString = Integer.toString(countVisibleFeats(getPc().getVirtualFeatList(), false, true));
		}
		else if ("COUNT[VFEATS]".equals(valString) || "COUNT[VFEATS.VISIBLE]".equals(valString))
		{
			valString = Integer.toString(countVisibleFeats(getPc().getVirtualFeatList(), true, false));
		}
		else if ("COUNT[FEATSAUTO.ALL]".equals(valString))
		{
			valString = Integer.toString(getPc().featAutoList().size());
		}
		else if ("COUNT[FEATSAUTO]".equals(valString) || "COUNT[FEATSAUTO.VISIBLE]".equals(valString))
		{
			valString = Integer.toString(countVisibleFeats(getPc().featAutoList(), true, false));
		}
		else if ("COUNT[FEATSAUTO.HIDDEN]".equals(valString))
		{
			valString = Integer.toString(countVisibleFeats(getPc().featAutoList(), false, true));
		}
		else if ("COUNT[FEATSALL]".equals(valString) || "COUNT[FEATSALL.VISIBLE]".equals(valString))
		{
			valString = Integer.toString(getPc().aggregateVisibleFeatList().size());
		}
		else if ("COUNT[FEATSALL.ALL]".equals(valString))
		{
			valString = Integer.toString(getPc().aggregateFeatList().size());
		}
		else if ("COUNT[FEATSALL.HIDDEN]".equals(valString))
		{
			valString = Integer.toString(countVisibleFeats(getPc().aggregateFeatList(), false, true));
		}
		else if ((valString.startsWith("COUNT[FEATTYPE=") || valString.startsWith("COUNT[FEATTYPE."))
		&& valString.endsWith(".ALL]"))
		{
			final List<String> featTypes = CoreUtility.split(valString.substring(15, valString.length() - 5), '.');
			valString = Integer.toString(countVisibleFeatTypes(getPc().aggregateFeatList(), featTypes, true, true));
		}
		else if ((valString.startsWith("COUNT[FEATTYPE=") || valString.startsWith("COUNT[FEATTYPE."))
		&& valString.endsWith(".HIDDEN]"))
		{
			final List<String> featTypes = CoreUtility.split(valString.substring(15, valString.length() - 8), '.');
			valString = Integer.toString(countVisibleFeatTypes(getPc().aggregateFeatList(), featTypes, false, true));
		}
		else if ((valString.startsWith("COUNT[FEATTYPE=") || valString.startsWith("COUNT[FEATTYPE."))
		&& valString.endsWith(".VISIBLE]"))
		{
			final List<String> featTypes = CoreUtility.split(valString.substring(15, valString.length() - 9), '.');
			valString = Integer.toString(countVisibleFeatTypes(getPc().aggregateFeatList(), featTypes, true, false));
		}
		else if ((valString.startsWith("COUNT[FEATTYPE=") || valString.startsWith("COUNT[FEATTYPE."))
		&& valString.endsWith("]"))
		{
			final List<String> featTypes = CoreUtility.split(valString.substring(15, valString.length() - 1), '.');
			valString = Integer.toString(countVisibleFeatTypes(getPc().aggregateFeatList(), featTypes, true, false));
		}

		//
		else if ((valString.startsWith("COUNT[VFEATTYPE=") || valString.startsWith("COUNT[VFEATTYPE."))
		&& valString.endsWith(".ALL]"))
		{
			final List<String> featTypes = CoreUtility.split(valString.substring(16, valString.length() - 5), '.');
			valString = Integer.toString(countVisibleFeatTypes(getPc().getVirtualFeatList(), featTypes, true, true));
		}
		else if ((valString.startsWith("COUNT[VFEATTYPE=") || valString.startsWith("COUNT[VFEATTYPE."))
		&& valString.endsWith(".HIDDEN]"))
		{
			final List<String> featTypes = CoreUtility.split(valString.substring(16, valString.length() - 8), '.');
			valString = Integer.toString(countVisibleFeatTypes(getPc().getVirtualFeatList(), featTypes, false, true));
		}
		else if ((valString.startsWith("COUNT[VFEATTYPE=") || valString.startsWith("COUNT[VFEATTYPE."))
		&& valString.endsWith(".VISIBLE]"))
		{
			final List<String> featTypes = CoreUtility.split(valString.substring(16, valString.length() - 9), '.');
			valString = Integer.toString(countVisibleFeatTypes(getPc().getVirtualFeatList(), featTypes, true, false));
		}
		else if ((valString.startsWith("COUNT[VFEATTYPE=") || valString.startsWith("COUNT[VFEATTYPE."))
		&& valString.endsWith("]"))
		{
			final List<String> featTypes = CoreUtility.split(valString.substring(16, valString.length() - 1), '.');
			valString = Integer.toString(countVisibleFeatTypes(getPc().getVirtualFeatList(), featTypes, true, false));
		}

		//
		else if ((valString.startsWith("COUNT[FEATAUTOTYPE=") || valString.startsWith("COUNT[FEATAUTOTYPE."))
		&& valString.endsWith(".ALL]"))
		{
			final List<String> featTypes = CoreUtility.split(valString.substring(19, valString.length() - 5), '.');
			valString = Integer.toString(countVisibleFeatTypes(getPc().featAutoList(), featTypes, true, true));
		}
		else if ((valString.startsWith("COUNT[FEATAUTOTYPE=") || valString.startsWith("COUNT[FEATAUTOTYPE."))
		&& valString.endsWith(".HIDDEN]"))
		{
			final List<String> featTypes = CoreUtility.split(valString.substring(19, valString.length() - 8), '.');
			valString = Integer.toString(countVisibleFeatTypes(getPc().featAutoList(), featTypes, false, true));
		}
		else if ((valString.startsWith("COUNT[FEATAUTOTYPE=") || valString.startsWith("COUNT[FEATAUTOTYPE."))
		&& valString.endsWith(".VISIBLE]"))
		{
			final List<String> featTypes = CoreUtility.split(valString.substring(19, valString.length() - 9), '.');
			valString = Integer.toString(countVisibleFeatTypes(getPc().featAutoList(), featTypes, true, false));
		}
		else if ((valString.startsWith("COUNT[FEATAUTOTYPE=") || valString.startsWith("COUNT[FEATAUTOTYPE."))
		&& valString.endsWith("]"))
		{
			final List<String> featTypes = CoreUtility.split(valString.substring(19, valString.length() - 1), '.');
			valString = Integer.toString(countVisibleFeatTypes(getPc().featAutoList(), featTypes, true, false));
		}

		//
		else if ((valString.startsWith("COUNT[FEATNAME=") || valString.startsWith("COUNT[FEATNAME."))
		&& valString.endsWith(".ALL]"))
		{
			final String featKey = valString.substring(15, valString.length() - 5);
			valString = Integer.toString(countVisibleFeatsOfKey(getPc().aggregateFeatList(), featKey, true, true));
		}
		else if ((valString.startsWith("COUNT[FEATNAME=") || valString.startsWith("COUNT[FEATNAME."))
		&& valString.endsWith(".HIDDEN]"))
		{
			final String featKey = valString.substring(15, valString.length() - 8);
			valString = Integer.toString(countVisibleFeatsOfKey(getPc().aggregateFeatList(), featKey, false, true));
		}
		else if ((valString.startsWith("COUNT[FEATNAME=") || valString.startsWith("COUNT[FEATNAME."))
		&& valString.endsWith(".VISIBLE]"))
		{
			final String featKey = valString.substring(15, valString.length() - 9);
			valString = Integer.toString(countVisibleFeatsOfKey(getPc().aggregateFeatList(), featKey, true, false));
		}
		else if ((valString.startsWith("COUNT[FEATNAME=") || valString.startsWith("COUNT[FEATNAME."))
		&& valString.endsWith("]"))
		{
			final String featKey = valString.substring(15, valString.length() - 1);
			valString = Integer.toString(countVisibleFeatsOfKey(getPc().aggregateFeatList(), featKey, true, false));
		}
		else if (valString.startsWith("COUNT[SPELLSKNOWN") && valString.endsWith("]"))
		{
			int spellCount = 0;

			if (SettingsHandler.getPrintSpellsWithPC())
			{
				spellCount = getPc().countSpellListBook(valString);
			}

			valString = Integer.toString(spellCount);
		}
		else if (valString.startsWith("COUNT[SPELLSINBOOK") && valString.endsWith("]"))
		{
			valString = valString.substring(18);
			valString = valString.substring(0, valString.length() - 1);

			int sbookCount = 0;

			if (SettingsHandler.getPrintSpellsWithPC())
			{
				sbookCount = getPc().countSpellsInBook(valString);
			}

			valString = Integer.toString(sbookCount);
		}
		else if (valString.startsWith("COUNT[SPELLSLEVELSINBOOK") && valString.endsWith("]"))
		{
			valString = valString.substring(24);
			valString = valString.substring(0, valString.length() - 1);

			final int sbookCount = getPc().countSpellLevelsInBook(valString);
			valString = Integer.toString(sbookCount);
		}
		else if (valString.startsWith("COUNT[SPELLTIMES") && valString.endsWith("]"))
		{
			valString = valString.substring(6);
			valString = valString.substring(0, valString.length() - 1);
			valString = String.valueOf(getPc().countSpellTimes(valString));
		}
		else if (valString.startsWith("COUNT[SPELLBOOKS") && valString.endsWith("]"))
		{
			valString = Integer.toString(getPc().getSpellBooks().size());
		}
		else if ("COUNT[SPELLCLASSES]".equals(valString))
		{
			valString = String.valueOf(getPc().getSpellClassCount());
		}
		else if ("COUNT[SPELLRACE]".equals(valString))
		{
			final PObject aSpellRace = getPc().getSpellClassAtIndex(0);
			valString = (aSpellRace instanceof Race) ? "1" : "0";
		}
		else if ("COUNT[TEMPBONUSNAMES]".equals(valString))
		{
			valString = String.valueOf(getPc().getNamedTempBonusList().size());
		}
		else if ("COUNT[CLASSES]".equals(valString))
		{
			getPc().getClassList().trimToSize();

			int iCount = getPc().getClassList().size();

			if (SettingsHandler.hideMonsterClasses())
			{
				for ( PCClass pcClass : getPc().getClassList() )
				{
					if (pcClass.isMonster())
					{
						--iCount;
					}
				}
			}

			valString = Integer.toString(iCount);
		}
		else if ("COUNT[DOMAINS]".equals(valString))
		{
			valString = Integer.toString(getPc().getCharacterDomainList().size());
		}
		else if (valString.startsWith("COUNT[EQUIPMENT") && valString.endsWith("]"))
		{
			int merge = Constants.MERGE_ALL;

			// check to see how we are merging
			if (valString.indexOf("MERGENONE") > 0)
			{
				merge = Constants.MERGE_NONE;
			}
			else if (valString.indexOf("MERGELOC") > 0)
			{
				merge = Constants.MERGE_LOCATION;
			}

			ArrayList<Equipment> aList = new ArrayList<Equipment>();
			final List<Equipment> equipList = getPc().getEquipmentListInOutputOrder(merge);

			for ( Equipment eq : equipList )
			{
				aList.add(eq);
			}

			if ("COUNT[EQUIPMENT]".equals(valString))
			{
				valString = Integer.toString(aList.size());
			}
			else
			{
				final StringTokenizer bTok = new StringTokenizer(valString.substring(16, valString.length() - 1), ".");

				while (bTok.hasMoreTokens()) //should be ok, assumes last two fields are # and a Param
				{
					final String bString = bTok.nextToken();

					if ("NOT".equalsIgnoreCase(bString))
					{
						aList = new ArrayList<Equipment>(EquipmentUtilities.removeEqType(aList, bTok.nextToken()));
					}
					else if ("ADD".equalsIgnoreCase(bString))
					{
						aList = new ArrayList<Equipment>(getPc().addEqType(aList, bTok.nextToken()));
					}
					else if ("IS".equalsIgnoreCase(bString))
					{
						aList = new ArrayList<Equipment>(EquipmentUtilities.removeNotEqType(aList, bTok.nextToken()));
					}
				}

				valString = Integer.toString(aList.size());
			}

			aList.clear();
		}
		else if (valString.startsWith("COUNT[EQTYPE.") && valString.endsWith("]"))
		{
			int merge = Constants.MERGE_ALL;
			List<Equipment> aList = new ArrayList<Equipment>();
			final StringTokenizer bTok = new StringTokenizer(valString.substring(13, valString.length() - 1), ".");
			String aType = bTok.nextToken();

			// check to see how we are merging equipment
			if ("MERGENONE".equals(aType))
			{
				merge = Constants.MERGE_NONE;
				aType = bTok.nextToken();
			}
			else if ("MERGELOC".equals(aType))
			{
				merge = Constants.MERGE_LOCATION;
				aType = bTok.nextToken();
			}

			if ("CONTAINER".equals(aType))
			{
				aList.clear();

				final List<Equipment> equipList = getPc().getEquipmentListInOutputOrder(merge);
				for ( Equipment eq : equipList )
				{
					if (eq.acceptsChildren())
					{
						aList.add(eq);
					}
				}
			}
			else
			{
				if ("WEAPON".equalsIgnoreCase(aType))
				{
					aList = getPc().getExpandedWeapons(merge);
				}
				else if ("ACITEM".equalsIgnoreCase(aType))
				{
					// special check for ACITEM
					// which is realy anything
					// with AC in the bonus section,
					// but is not type SHIELD or ARMOR
					final List<Equipment> equipList = getPc().getEquipmentListInOutputOrder(merge);
					for ( Equipment eq : equipList )
					{
						if (eq.getBonusListString("AC") && !eq.isArmor() && !eq.isShield())
						{
							aList.add(eq);
						}
					}
				}
				else
				{
					aList = getPc().getEquipmentOfTypeInOutputOrder(aType, 3, merge);
				}
			}

			while (bTok.hasMoreTokens())
			{
				final String bString = bTok.nextToken();

				if ("NOT".equalsIgnoreCase(bString))
				{
					aList = new ArrayList<Equipment>(EquipmentUtilities.removeEqType(aList, bTok.nextToken()));
				}
				else if ("ADD".equalsIgnoreCase(bString))
				{
					aList = new ArrayList<Equipment>(getPc().addEqType(aList, bTok.nextToken()));
				}
				else if ("IS".equalsIgnoreCase(bString))
				{
					aList = new ArrayList<Equipment>(EquipmentUtilities.removeNotEqType(aList, bTok.nextToken()));
				}
				else if ("EQUIPPED".equalsIgnoreCase(bString) || "NOTEQUIPPED".equalsIgnoreCase(bString))
				{
					final boolean eFlag = "EQUIPPED".equalsIgnoreCase(bString);

					for (int ix = aList.size() - 1; ix >= 0; --ix)
					{
						final Equipment anEquip = aList.get(ix);

						if (anEquip.isEquipped() != eFlag)
						{
							aList.remove(anEquip);
						}
					}
				}
			}

			valString = Integer.toString(aList.size());
			aList.clear();
		}
		else if ("COUNT[CONTAINERS]".equals(valString))
		{
			final int merge = Constants.MERGE_ALL;

			final ArrayList<Equipment> aList = new ArrayList<Equipment>();
			final List<Equipment> equipList = getPc().getEquipmentListInOutputOrder(merge);

			for ( Equipment eq : equipList )
			{
				if (eq.acceptsChildren())
				{
					aList.add(eq);
				}
			}

			valString = Integer.toString(aList.size());
		}
		else if ("COUNT[SA]".equals(valString))
		{
			valString = String.valueOf(getPc().getSpecialAbilityTimesList().size());
		}
		else if ("COUNT[TEMPLATES]".equals(valString))
		{
			getPc().getTemplateList().trimToSize();
			valString = String.valueOf(getPc().getTemplateList().size());
		}
		else if ("COUNT[RACESUBTYPES]".equals(valString))
		{
			valString = Integer.toString(getPc().getRacialSubTypes().size());
		}
		else if ("COUNT[VISIBLETEMPLATES]".equals(valString))
		{
			int count = 0;

			for ( PCTemplate template : getPc().getTemplateList() )
			{
				final Visibility vis = template.getVisibility();

				if ((vis == Visibility.DEFAULT)
				|| (vis == Visibility.OUTPUT_ONLY))
				{
					++count;
				}
			}

			valString = Integer.toString(count);
		}
		else if ("COUNT[LANGUAGES]".equals(valString))
		{
			valString = Integer.toString(getPc().getLanguagesList().size());
		}
		else if ("COUNT[MOVE]".equals(valString))
		{
			valString = Integer.toString(getPc().getNumberOfMovements());
		}
		else if ("COUNT[NOTES]".equals(valString))
		{
			valString = Integer.toString(getPc().getNotesList().size());
		}
		else if ("COUNT[VISION]".equals(valString))
		{
			valString = Integer.toString(getPc().getVisionList().size());
		}
		else if ("COUNT[MISC.FUNDS]".equals(valString))
		{
			valString = Integer.toString(Arrays.asList(getPc().getMiscList().get(0).split("\r?\n")).size());
		}
		else if ("COUNT[MISC.COMPANIONS]".equals(valString))
		{
			valString = Integer.toString(Arrays.asList(getPc().getMiscList().get(1).split("\r?\n")).size());
		}
		else if ("COUNT[MISC.MAGIC]".equals(valString))
		{
			valString = Integer.toString(Arrays.asList(getPc().getMiscList().get(2).split("\r?\n")).size());
		}
		else if (valString.startsWith("COUNT[FOLLOWERTYPE.") && valString.endsWith("]"))
		{
			if (valString.indexOf(".") == valString.lastIndexOf("."))
			{
				// This covers COUNT[FOLLOWERTYPE.Animal Companions] syntax
				int countFollower = 0;
				String bString = valString.substring(19);
				bString = bString.substring(0, bString.length() - 1);

				for ( Follower follower : getPc().getFollowerList() )
				{
					if (follower.getType().equalsIgnoreCase(bString))
					{
						++countFollower;
					}
				}

				valString = String.valueOf(countFollower);
			}
			else
			{
				// This will do COUNT[FOLLOWERTYPE.Animal Companions.0.xxx],
				// returning the same as COUNT[xxx] if applied to the right follower
				final List<Follower> followers = getPc().getFollowerList();

				if (!followers.isEmpty())
				{
					StringTokenizer aTok = new StringTokenizer(valString, "[]");
					aTok.nextToken(); // Remove the COUNT
					final String aString = aTok.nextToken();
					aTok = new StringTokenizer(aString, ".");
					aTok.nextToken(); // FOLLOWERTYPE

					final String typeString = aTok.nextToken();
					String restString = "";
					int followerIndex = -1;

					if (aTok.hasMoreTokens())
					{
						restString = aTok.nextToken();

						followerIndex = Integer.parseInt(restString);
						restString = "";

						while (aTok.hasMoreTokens())
						{
							restString = restString + "." + aTok.nextToken();
						}

						if (restString.indexOf(".") == 0)
						{
							restString = restString.substring(1);
						}
					}

					restString = "COUNT[" + restString + "]";

					final ArrayList<Follower> aList = new ArrayList<Follower>();

					for ( Follower follower : followers )
					{
						if (follower.getType().equalsIgnoreCase(typeString))
						{
							aList.add(follower);
						}
					}

					if (followerIndex < aList.size())
					{
						final Follower follower = aList.get(followerIndex);
						PlayerCharacter currentPC;

						for ( PlayerCharacter pc : Globals.getPCList() )
						{
							if (follower.getFileName().equals(pc.getFileName()))
							{
								currentPC = getPc();
								Globals.setCurrentPC(pc);
								valString = pc.getVariableValue(restString, "").toString();
								Globals.setCurrentPC(currentPC);
							}
						}
					}
				}
			}
		}
		else if (valString.startsWith("EQTYPE"))
		{
			//valString = ExportHandler.returnReplacedTokenEq(this, valString);
			final EqTypeToken token = new EqTypeToken();
			valString = token.getToken(valString, getPc(), null);

		}
		else if (valString.startsWith("VARDEFINED:"))
		{
			if (getPc().hasVariable(valString.substring(11).trim()))
			{
				valString = "1";
			}
			else
			{
				valString = "0";
			}
		}
		else if (valString.startsWith("HASFEAT:"))
		{
			valString = valString.substring(8).trim();

			if (getPc().hasRealFeatNamed(valString))
			{
				valString = "1";
			}
			else
			{
				valString = "0";
			}
		}
		else if (valString.startsWith("HASDEITY:"))
		{
			valString = valString.substring(9).trim();

			if (getPc().hasDeity(valString))
			{
				valString = "1";
			}
			else
			{
				valString = "0";
			}
		}
		else if (valString.startsWith("MODEQUIP"))
		{
			valString = String.valueOf(getPc().modToFromEquipment(valString.substring(8)));
		}
		else if (valString.startsWith("WEIGHT."))
		{
			valString = valString.substring(7);

			if ("CARRIED".equals(valString))
			{
				// all carried equipment
				valString = getPc().totalWeight().toString();
			}
			else if ("EQUIPPED".equals(valString))
			{
				// TODO: not carried, equipped!
				// all equipped
				valString = getPc().totalWeight().toString();
			}
			else if ("PC".equals(valString))
			{
				// Characters weight
				valString = String.valueOf(getPc().getWeight());
			}
			else if ("TOTAL".equals(valString))
			{
				// total weight of PC and all
				// carried equipment
				final Float aTotal = new Float(getPc().totalWeight().floatValue() + new Float(getPc().getWeight()).floatValue());
				valString = aTotal.toString();
			}
		}
		else if (valString.startsWith("PC.SIZE"))
		{
			int modSize = 0;

			if (src.startsWith("EQ:"))
			{
				final Equipment eq = getPc().getEquipmentNamed(src.substring(3));

				if (eq != null)
				{
					modSize = (int) getPc().getTotalBonusTo("WEAPONPROF=" + eq.profKey(getPc()), "PCSIZE");

					// loops for each equipment type
					for ( String eqType : eq.typeList() )
					{
						// get the type bonus (ex TYPE.MARTIAL)
						int tempModSize = (int) getPc().getTotalBonusTo("WEAPONPROF=TYPE." + eqType, "PCSIZE");
						// get the highest bonus
						if (modSize < tempModSize) {
							modSize = tempModSize;
						}
					}
				}
			}

			if (valString.equals("PC.SIZE"))
			{
				valString = getPc().getSize();
			}
			else if (valString.substring(8).equals("INT"))
			{
				valString = String.valueOf(getPc().sizeInt() + modSize);
			}
		}
		else if (valString.startsWith("PC.HEIGHT"))
		{
			valString = Integer.toString(getPc().getHeight());
		}
		else if (valString.startsWith("PC.WEIGHT"))
		{
			valString = Integer.toString(getPc().getWeight());
		}
		else
		{
			valString = null;
		}

		return valString;
	}
}
