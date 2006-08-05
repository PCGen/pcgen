/*
 * PCTemplate.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.core.levelability.LevelAbility;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.PropertyFactory;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.Visibility;

/**
 * <code>PCTemplate</code>.
 *
 * @author   Mark Hulsman <hulsmanm@purdue.edu>
 * @version  $Revision$
 */
public final class PCTemplate extends PObject implements HasCost
{
	///////////////////////////////////////////////////////////////////////
	// Static properties
	///////////////////////////////////////////////////////////////////////

	private AbilityStore abilityCatStore     = null;
	private ArrayList<String>    featStrings         = null;
	private ArrayList<String>    hitDiceStrings      = null;
	private ArrayList<String>    levelStrings        = null;
	private ArrayList<String>    templates           = new ArrayList<String>();

	private HashMap<String, String>      chosenFeatStrings   = null;
	private List<String>         templatesAdded      = null;
	private String       cost                = "1";

	private String favoredClass = "";

	// If set these two will override any other choices.
	private String  gender                = Constants.s_NONE;
	private String  handed                = Constants.s_NONE;

	private String  levelAdjustment       = "0"; // now a string so that we can handle
												 // formulae
	private String  region                = Constants.s_NONE;
	private String  subRace               = Constants.s_NONE;
	private String  subregion             = Constants.s_NONE;
	private String  templateSize          = "";
	private boolean removable             = true;
	private int     ChallengeRating       = 0;
	private int     bonusInitialFeats     = 0;
	private int     bonusSkillsPerLevel   = 0;
	private String  hitDieLock            = "";
	private int     levelsPerFeat         = 3;
	private int     nonProficiencyPenalty = 1;
	private String  raceType              = "";
	private Integer hands;
	private Integer legs;
	private Integer reach;

	private ArrayList<String> addedSubTypes   = new ArrayList<String>();

	private Point2D.Double face       = new Point2D.Double(5, 0);

	private ArrayList<String> removedSubTypes = new ArrayList<String>();

	private ArrayList<String> levelMods = new ArrayList<String>();

	/**
	 * Creates a new PCTemplate object.
	 */
	public PCTemplate()
	{
		// Empty Constructor
	}

	/**
	 * Set the number of Bonus feats that this template grants the character it
	 * is applied to at level 0 (i.e. before classes are added).
	 *
	 * @param  argBonusInitialFeats  Number of Bonus feats gained
	 */
	public void setBonusInitialFeats(final int argBonusInitialFeats)
	{
		bonusInitialFeats = argBonusInitialFeats;
	}


	/**
	 * Get the number of Bonus feats that this template grants the character it
	 * is applied to at level 0
	 *
	 * @return the number of Bonus feats
	 */
	public int getBonusInitialFeats()
	{
		return bonusInitialFeats;
	}


	/**
	 * Set a Bonus to the number of skill points per level that this template
	 * grants the character it is applied to.
	 *
	 * @param  argBonusSkillsPerLevel Number of bonus skill points per level.
	 */
	public void setBonusSkillsPerLevel(final int argBonusSkillsPerLevel)
	{
		bonusSkillsPerLevel = argBonusSkillsPerLevel;
	}


	/**
	 * Get the Bonus to the number of skill points per level that this template
	 * grants the character it is applied to.
	 *
	 * @return the number of bonus skill points per level granted by this template
	 */
	public int getBonusSkillsPerLevel()
	{
		return bonusSkillsPerLevel;
	}


	/**
	 * Set an adjustment to the Challenge rating of a Character that this
	 * Template is added to.  This adjustment is independent of and additional
	 * to any adjustment made with LEVEL:<num>:CR and HD:<num>:CR tags
	 *
	 * @param  argCR The adjustment to challenge rating
	 */
	public void setCR(final int argCR)
	{
		ChallengeRating = argCR;
	}


	/**
	 * Get the total adjustment to Challenge rating of a character at a given
	 * level (Class and Hit Dice).  This will include the absolute adjustment
	 * made with CR:, LEVEL:<num>:CR and HD:<num>:CR tags
	 *
	 * @param   level    The level to calculate the adjustment for
	 * @param   hitdice  The Hit dice to calculate the adjustment for
	 *
	 * @return  a Challenge Rating adjustment
	 */
	public int getCR(final int level, final int hitdice)
	{
		int localCR = ChallengeRating;

		for (int x = 0; x < getListSize(levelStrings); ++x)
		{
			if (
				contains(levelStrings.get(x), "CR:") &&
				doesLevelQualify(level, x))
			{
				localCR += Integer.parseInt(
						getStringAfter("CR:", levelStrings.get(x)));
			}
		}

		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (
				contains(hitDiceStrings.get(x), "CR:") &&
				doesHitDiceQualify(hitdice, x))
			{
				localCR += Integer.parseInt(
						getStringAfter("CR:", hitDiceStrings.get(x)));
			}
		}

		return localCR;
	}



	/**
	 * Get a list of Feats chosen (from those potentially granted by this
	 * Template) by the Character it was applied to.
	 *
	 * @return  a hashmap of Feat names
	 */
	public HashMap<String, String> getChosenFeatStrings()
	{
		return chosenFeatStrings;
	}


	/**
	 * Set the COST of things granted by this Template.
	 *
	 * XXX This seems insane to me, it's used for at least two different
	 * unrelated things in the code base.  The tag this is generated from
	 * is undocumented and is not used in the current data.
	 *
	 * @param  argCost the cost as a string, it will be converted to a double
	 * before being used.
	 */
	public void setCost(final String argCost)
	{
		cost = argCost;
	}


	/**
	 * Get the COST of things granted by this Template.
	 *
	 * @return  the cost bonuses granted by this Template
	 */
	public double getCost()
	{
		return Double.parseDouble(cost);
	}

	/**
	 * Set the name of a favoured class to add to the Character this Template
	 * is applied to
	 *
	 * @param  newClass the name of the class
	 */
	public void setFavoredClass(final String newClass)
	{
		favoredClass = newClass;
	}


	/**
	 * Get a string that is the name of a single favoured class to be added to
	 * the character this Template is applied to.  Each Template can only add
	 * a single favoured class.
	 *
	 * @return  the name of the favoured class to add
	 */
	public String getFavoredClass()
	{
		return favoredClass;
	}


	/**
	 * <code>setGenderLock</code> locks gender to appropriate PropertyFactory
	 * setting if String matches 'Male','Female', or 'Neuter'.
	 *
	 * author arcady <arcady@users.sourceforge.net>
	 *
	 * @param  genderString
	 */
	public void setGenderLock(final String genderString)
	{
		if ("Female".equalsIgnoreCase(genderString))
		{
			gender = PropertyFactory.getString("in_genderFemale");
		}
		else if ("Male".equalsIgnoreCase(genderString))
		{
			gender = PropertyFactory.getString("in_genderMale");
		}
		else if ("Neuter".equalsIgnoreCase(genderString))
		{
			gender = PropertyFactory.getString("in_genderNeuter");
		}
	}


	/**
	 * Get the gender that Characters this Template is applied to are locked at.
	 *
	 * @return  the gender at which to lock the character
	 */
	public String getGenderLock()
	{
		return gender;
	}

	/**
	 * Set a lock on the hitdie size of a character that this template is
	 * applied to.   Possible formats for the lock include
	 *
	 * 12
	 *    The character now has a Hit Dice of 12.
	 *
	 * %+2
	 *    Adds 2 to the current Hit Dice size.
	 *
	 * %-4
	 *    Subtracts 4 from the current Hit Dice size.
	 *
	 * %*3
	 *    Multiplies the current Hit Dice size by 3.
	 *
	 * %/2
	 *    Divides the current Hit Dice size by 2.
	 *
	 * %up2
	 *    Steps up the Hit Dice size by two steps. If the creature has a Hit
	 *    Die of d6 it will be stepped up to d10.
	 *
	 * %down1
	 *    Steps down the Hit Dice size by one step. If the creature has a Hit
	 *    Die of d6 it will be stepped down to d4.
	 *
	 * %up1|CLASS.TYPE=Monster
	 *    Steps up the Hit Dice size by one step for any Monster class levels
	 *    the creature has. If the creature has a Monster class Hit Die of d8
	 *    it will be stepped up to d10.
	 *
	 * @param  hitDieLock the sting to lock to
	 */
	public void setHitDieLock(final String hitDieLock)
	{
		this.hitDieLock = hitDieLock;
	}


	/**
	 * Get a string that will be used to manipulate the hit die of any creature
	 * this template is applied to
	 *
	 * @return the hit die manipulation string
	 */
	protected String getHitDieLock()
	{
		return hitDieLock;
	}

	/**
	 * Set a formula for level adjustment (jep) to be applied to any creature
	 * this template is applied to.
	 *
	 * @param  argLevelAdjustment The formula for the level adjustment
	 */
	public void setLevelAdjustment(final String argLevelAdjustment)
	{
		levelAdjustment = argLevelAdjustment;
	}


	/**
	 * Calculate the level adjustment using the variable parser of the PC
	 * object passed in.  If no PC is passed, attempts to convert the string to
	 * an int.
	 *
	 * @param   aPC the PC to get the details of the varible parser from
	 *
	 * @return  a level adjustment
	 */
	public int getLevelAdjustment(final PlayerCharacter aPC)
	{
		int lvlAdjust;

		// if there's a current PC, go ahead and evaluate the formula
		if (aPC != null)
		{
			return aPC.getVariableValue(levelAdjustment, "").intValue();
		}

		// otherwise do what we can
		try
		{
			// try to convert the string to an int to return
			lvlAdjust = Integer.parseInt(levelAdjustment);
		}
		catch (NumberFormatException nfe)
		{
			// if the parseInt failed then just punt... return 0
			lvlAdjust = 0;
		}

		return lvlAdjust;
	}


	/**
	 * Get the formula that would be used to calculate a Level adjustment for
	 * creatures this Template is applied to.
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getLevelAdjustmentFormula()
	{
		return levelAdjustment;
	}


	/**
	 * Set an override for the one feat per 3 levels for creatures this template is applied to
	 *
	 * @param  argLevelsPerFeat  the number of levels between level dependant feats
	 */
	public void setLevelsPerFeat(final int argLevelsPerFeat)
	{
		levelsPerFeat = argLevelsPerFeat;
	}


	/**
	 * Takes an integer input which it uses to access Games mode's "statlist" array.
	 * If that stat has been locked at 10 then it is considered a non-ability.
	 * XXX This is insanely bad design, it's completely arse about face. What should
	 * have been done was find a way to mark a stat as a non-ability and then have the
	 * stat checking code interpret that as "no bonus or penalty - treat like it was
	 * locked at 10".  Doing it this way means there is no way to actually lock a stat
	 * at 10.
	 * TODO: Fix this mess!
	 * disparaging comments Andrew Wilson 20060308
	 *
	 * @param   statIdx  index number of the stat in question
	 *
	 * @return  Whether this has been defined as a non-ability
	 */
	public boolean isNonAbility(final int statIdx)
	{
		final List<PCStat> statList = SettingsHandler.getGame().getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}

		final String aStat = "|LOCK." + statList.get(statIdx).getAbb() + "|10";

		for (int i = 0, x = getVariableCount(); i < x; ++i)
		{
			final String varString = getVariableDefinition(i);

			if (varString.endsWith(aStat))
			{
				return true;
			}
		}

		return false;
	}


	/**
	 * Set up a penalty for being non=proficient with a weapon
	 *
	 * @param  npp the amount of penalty to apply to weapons that the creature
	 * this template was applied to is not proficient with.
	 */
	public void setNonProficiencyPenalty(final int npp)
	{
		nonProficiencyPenalty = npp;
	}


	/**
	 * Get the amount of penalty to apply to weapons that the creature
	 * this template was applied to is not proficient with.
	 *
	 * author: arcady June 4, 2002
	 *
	 * @return  nonProficiencyPenalty
	 */
	public int getNonProficiencyPenalty()
	{
		return nonProficiencyPenalty;
	}


	/**
	 * Produce a tailored PCC output, used for saving custom templates.
	 *
	 * @return  PCC Text
	 */
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getDisplayName());

		if (bonusInitialFeats != 0)
		{
			txt.append("\tBONUSFEATS:").append(bonusInitialFeats);
		}

		if (bonusSkillsPerLevel != 0)
		{
			txt.append("\tBONUSSKILLPOINTS:").append(bonusSkillsPerLevel);
		}

		if ((getChooseLanguageAutos() != null) && (getChooseLanguageAutos().length() > 0))
		{
			txt.append("\tCHOOSE:LANGAUTO:").append(getChooseLanguageAutos());
		}

		if (!CoreUtility.doublesEqual(getCost(), 1.0d))
		{
			txt.append("\tCOST:").append(String.valueOf(getCost()));
		}

		if (ChallengeRating != 0)
		{
			txt.append("\tCR:").append(ChallengeRating);
		}

		if ((favoredClass != null) && (favoredClass.length() > 0))
		{
			txt.append("\tFAVOREDCLASS:").append(favoredClass);
		}

		if (abilityCatStore != null && !abilityCatStore.isEmpty())
		{
			txt.append("\tABILITY:");
			txt.append(abilityCatStore.getParsableStringRepresentation());
		}

		if (getListSize(featStrings) > 0)
		{
			final StringBuffer buffer = new StringBuffer();

			for ( String feat : featStrings )
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}

				buffer.append(feat);
			}

			txt.append("\tFEAT:").append(buffer.toString());
		}

		if (!Constants.s_NONE.equals(gender))
		{
			txt.append("\tGENDERLOCK:").append(gender);
		}

		if (!Constants.s_NONE.equals(handed))
		{
			txt.append("\tHANDEDLOCK:").append(handed);
		}

		if (getListSize(hitDiceStrings) > 0)
		{
			for (String s : hitDiceStrings)
			{
				txt.append("\tHD:").append(s);
			}
		}

		if (!hitDieLock.equals(""))
		{
			txt.append("\tHITDIE:").append(hitDieLock);
		}

		if ((getLanguageBonus() != null) && !getLanguageBonus().isEmpty())
		{
			final StringBuffer buffer = new StringBuffer();

			for ( Language lang : getLanguageBonus() )
			{
				if (buffer.length() != 0)
				{
					buffer.append(',');
				}

				buffer.append(lang);
			}

			txt.append("\tLANGBONUS:").append(buffer.toString());
		}

		if (getListSize(levelStrings) > 0)
		{
			for (Iterator<String> e = levelStrings.iterator(); e.hasNext();)
			{
				txt.append("\tLEVEL:").append(e.next());
			}
		}

		if (!"0".equals(levelAdjustment))
		{
			txt.append("\tLEVELADJUSTMENT:").append(levelAdjustment);
		}

		if (levelsPerFeat != 3)
		{
			txt.append("\tLEVELSPERFEAT:").append(levelsPerFeat);
		}

		if (nonProficiencyPenalty <= 0)
		{
			txt.append("\tNONPP:").append(nonProficiencyPenalty);
		}

		if ((templateSize != null) && (templateSize.length() > 0))
		{
			txt.append("\tSIZE:").append(templateSize);
		}

		if (!"alwaysValid".equals(getQualifyString()))
		{
			txt.append("\tQUALIFY:").append(getQualifyString());
		}

		if (!Constants.s_NONE.equals(region))
		{
			txt.append("\tREGION:");

			if (region.equals(getDisplayName()))
			{
				txt.append("Yes");
			}
			else
			{
				txt.append(region);
			}
		}

		if (!removable)
		{
			txt.append("\tREMOVABLE:No");
		}

		if (!Constants.s_NONE.equals(subRace))
		{
			txt.append("\tSUBRACE:");

			if (subRace.equals(getDisplayName()))
			{
				txt.append("Yes");
			}
			else
			{
				txt.append(subRace);
			}
		}

		if (!Constants.s_NONE.equals(subregion))
		{
			txt.append("\tSUBREGION:");

			if (subregion.equals(getDisplayName()))
			{
				txt.append("Yes");
			}
			else
			{
				txt.append(subregion);
			}
		}

		if (getListSize(templates) > 0)
		{
			for (Iterator<String> e = templates.iterator(); e.hasNext();)
			{
				txt.append("\tTEMPLATE:").append(e.next());
			}
		}

		switch (getVisibility())
		{
			case DISPLAY_ONLY:
				txt.append("\tVISIBLE:DISPLAY");

				break;

			case OUTPUT_ONLY:
				txt.append("\tVISIBLE:EXPORT");

				break;

			case HIDDEN:
				txt.append("\tVISIBLE:NO");

				break;

			default:
				txt.append("\tVISIBLE:YES");

				break;
		}

		if (getWeaponProfBonus().size() > 0)
		{
			final StringBuffer buffer = new StringBuffer();

			for ( final String profKey : getWeaponProfBonus() )
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}

				buffer.append(profKey);
			}

			txt.append("\tWEAPONBONUS:").append(buffer.toString());
		}

		txt.append(super.getPCCText(false));

		return txt.toString();
	}


	/**
	 * Answers the question does the PC I've passed in meet the prerequisites of
	 * this template.
	 *
	 * @param   aPC The PC that we're asking the question about
	 *
	 * @return true if the PC passes the Templates prerequisites
	 */
	public boolean isQualified(final PlayerCharacter aPC)
	{
		if (aPC == null)
		{
			return false;
		}

		return PrereqHandler.passesAll(getPreReqList(), aPC, this);
	}


	/**
	 * Get the override that this template applies to racetype
	 *
	 * @return  The new racetype
	 */
	public String getRaceType()
	{
		return raceType;
	}


	/**
	 * Set the override that this template applies to racetype
	 *
	 * @param  aType The new racetype
	 */
	public void setRaceType(final String aType)
	{
		raceType = aType;
	}


	/**
	 * Get the override that this template applies to subracetype
	 *
	 * @return  The new subracetype
	 */
	public String getSubRace()
	{
		return subRace;
	}


	/**
	 * Set the override that this template applies to subracetype
	 *
	 * @param  argSubRace The new subrace type
	 */
	public void setSubRace(final String argSubRace)
	{
		subRace = argSubRace;
	}



	/**
	 * Set the override that this template applies to Region
	 *
	 * @param  argRegion The new Region
	 */
	public void setRegion(final String argRegion)
	{
		region = argRegion;
	}


	/**
	 * Get the override that this template applies to Region
	 *
	 * @return  The new Region
	 */
	public String getRegion()
	{
		return region;
	}


	/**
	 * Set the override that this template applies to SubRegion
	 *
	 * @param  argSubregion  The new SubRegion
	 */
	public void setSubRegion(final String argSubregion)
	{
		subregion = argSubregion;
	}


	/**
	 * Get the override that this template applies to SubRegion
	 *
	 * @return The new SubRegion
	 */
	public String getSubRegion()
	{
		return subregion;
	}


	/**
	 * Set the property that controls whether this Template is removable
	 *
	 * @param  argRemovable Whether this Template is removable
	 */
	public void setRemovable(final boolean argRemovable)
	{
		removable = argRemovable;
	}


	/**
	 * Query whether this Template is removable.  Factors in the visibility of
	 * the Template
	 *
	 * @return  whether this Template is removable
	 */
	public boolean isRemovable()
	{
		boolean result = false;

		if (
			(getVisibility() == Visibility.DEFAULT) ||
			(getVisibility() == Visibility.DISPLAY_ONLY))
		{
			result = removable;
		}

		return result;
	}


	/**
	 * Get the Spell Resistance granted by this template to a character at a
	 * given level (Class and Hit Dice).  This will include the absolute
	 * adjustment made with SR:, LEVEL:<num>:SR and HD:<num>:SR tags
	 *
	 * Note: unlike DR and CR, the value returned here includes the PCs own
	 * Spell Resistance.
	 *
	 * @param   level    The level to calculate the SR for
	 * @param   hitdice  The Hit dice to calculate the SR for
	 * @param   aPC      DOCUMENT ME!
	 *
	 * @return  the Spell Resistance granted by this Template at the given level and HD
	 */
	public int getSR(final int level, final int hitdice, final PlayerCharacter aPC)
	{
		int aSR = getSR(aPC);

		for (int x = 0; x < getListSize(levelStrings); ++x)
		{
			if (
				contains(levelStrings.get(x), "SR:") &&
				doesLevelQualify(level, x))
			{
				aSR = Math.max(
						Integer.parseInt(
							getStringAfter("SR:", levelStrings.get(x))),
						aSR);
			}
		}

		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (
				contains(hitDiceStrings.get(x), "SR:") &&
				doesHitDiceQualify(hitdice, x))
			{
				aSR = Math.max(
						Integer.parseInt(
							getStringAfter("SR:", hitDiceStrings.get(x))),
						aSR);
			}
		}

		return aSR;
	}


	/**
	 * Get a list of Special Abilities added by this Template at a given
	 * level (Class and Hit Dice).  This will include the absolute adjustment
	 * made with LEVEL:<num>:SA and HD:<num>:SA tags
	 *
	 * @param   level    The level to calculate the adjustment for
	 * @param   hitdice  The Hit dice to calculate the adjustment for
	 *
	 * @return  A list of Special Abilities
	 */
	public List<SpecialAbility> getSpecialAbilityList(final int level, final int hitdice)
	{
		final List<SpecialAbility> specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

		if (specialAbilityList == null)
		{
			return specialAbilityList;
		}

		for (int x = 0; x < getListSize(levelStrings); ++x)
		{
			if (
				contains(levelStrings.get(x), "SA:") &&
				doesLevelQualify(level, x))
			{
				final String         saString = getStringAfter(
						"SA:",
						levelStrings.get(x));
				final SpecialAbility sa       = new SpecialAbility(saString);

				specialAbilityList.add(sa);
			}
		}

		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (
				contains(hitDiceStrings.get(x), "SA:") &&
				doesHitDiceQualify(hitdice, x))
			{
				final String         saString = getStringAfter(
						"SA:",
						hitDiceStrings.get(x));
				final SpecialAbility sa       = new SpecialAbility(saString);

				specialAbilityList.add(sa);
			}
		}

		return specialAbilityList;
	}


	/**
	 * Manipulate the list of subTypes that this Template add or removes from
	 * the creature it is applied to.
	 *
	 * Takes a | separated list of subtypes to add.  may optionally be prefaced
	 * with .REMOVE. in which case the subtype is removed.
	 *
	 * @param  aString the string to process
	 */
	public void addSubTypeString(final String aString)
	{
		StringTokenizer tok = new StringTokenizer(aString, "|");

		while (tok.hasMoreTokens())
		{
			String aType = tok.nextToken();

			if (aType.startsWith(".REMOVE."))
			{
				removedSubTypes.add(aType.substring(8));
			}
			else
			{
				addedSubTypes.add(aType);
			}
		}
	}


	/**
	 * Get the list of added SubTypes
	 *
	 * @return the Subtypes added.
	 */
	public List<String> getAddedSubTypes()
	{
		return Collections.unmodifiableList(addedSubTypes);
	}


	/**
	 * Get the list of removed SubTypes
	 *
	 * @return the Subtypes removed.
	 */
	public List<String> getRemovedSubTypes()
	{
		return Collections.unmodifiableList(removedSubTypes);
	}


	/**
	 * Method getTemplateList. Returns an array list containing the raw
	 * templates granted by this template. This includes CHOOSE: strings which
	 * list templates a user will be asked to choose from.
	 *
	 * @return  ArrayList of granted templates
	 */
	public List<String> getTemplateList()
	{
		return templates;
	}


	/**
	 * Set the override that this template applies to size
	 *
	 * @param  argSize the size of the creature this Template is applied to
	 */
	public void setTemplateSize(final String argSize)
	{
		templateSize = argSize;
	}


	/**
	 * Get the override that this template applies to size
	 *
	 * @return the size of the creature this Template is applied to
	 */
	public String getTemplateSize()
	{
		return templateSize;
	}

	/**
	 * Grants the character an ability at the Hit die or hit die range specified.
	 * The text may contain the following tags: CR - Challenge Rating, DR - Damage
	 * Reduction, FEAT - Feat, SA - Special Ability, SR - Spell Resistance
	 *
	 * 1-3:DR:5/1	Grants Damage Reduction of 5/+1 if natural hit dice is between
	 * 				one and three.
	 *
	 * 1+:SR:15		Grants Spell Resistance of 15 if natural hit dice is greater
	 * 				than one.
	 *
	 * 2-7:CR:2		Grants an increase in Challenge Rating of two if natural hit
	 * 				dice is between two and seven.
	 *
	 * 15+:SA:Uncanny Dodge	Grants the "Uncanny Dodge" special ability if natural
	 * 						hit dice is grater than fifteen.
	 *
	 * 10+:FEAT:Alertness	Grants the "Alertness" feat if natural hit dice is
	 * 						greater than ten.
	 *
	 * @param  hitDiceString a string in the format specified above
	 */
	public void addHitDiceString(final String hitDiceString)
	{
		if (".CLEAR".equals(hitDiceString))
		{
			if (hitDiceStrings != null)
			{
				hitDiceStrings.clear();
			}

			return;
		}
		StringTokenizer tok = new StringTokenizer(hitDiceString, ":");
		String hdStr = tok.nextToken();
		String typeStr = tok.nextToken();
		if ("DR".equals(typeStr))
		{
			String drVal = tok.nextToken();
			String[] values = drVal.split("/");
			if (values.length == 2)
			{
				DamageReduction dr = new DamageReduction(values[0], values[1]);
				Prerequisite r = null;
				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();
					r = factory.parse("PREHD:" + hdStr);
				}
				catch (PersistenceLayerException notUsed)
				{
					// Should never happen
				}
				if (r != null)
				{
					dr.addPreReq(r);
				}
				addDR(dr);
			}
			return;
		}
		if (hitDiceStrings == null)
		{
			hitDiceStrings = new ArrayList<String>();
		}

		hitDiceStrings.add(hitDiceString);
	}


	/**
	 * Get an array of strings which may grant the following abilities at a given hit die:
	 *
	 * 1-3:DR:5/1	Grants Damage Reduction of 5/+1 if natural hit dice is between
	 * 				one and three.
	 *
	 * 1+:SR:15		Grants Spell Resistance of 15 if natural hit dice is greater
	 * 				than one.
	 *
	 * 2-7:CR:2		Grants an increase in Challenge Rating of two if natural hit
	 * 				dice is between two and seven.
	 *
	 * 15+:SA:Uncanny Dodge	Grants the "Uncanny Dodge" special ability if natural
	 * 						hit dice is grater than fifteen.
	 *
	 * 10+:FEAT:Alertness	Grants the "Alertness" feat if natural hit dice is
	 * 						greater than ten.
	 *
	 * @return an array of strings in the format specified above
	 */
	public List<String> getHitDiceStrings()
	{
		return hitDiceStrings != null ? hitDiceStrings : Collections.EMPTY_LIST;
	}

	/**
	 * Grants the character an ability at the level specified (total character level).
	 * The text may contain the following tags: CR - Challenge Rating, DR - Damage
	 * Reduction, FEAT - Feat, SA - Special Ability, SR - Spell Resistance
	 *
	 * Feats added by this tag are considered automatic feats and do not count against
	 * a PC's feat pool.
	 *
	 * 1:DR:5/+1	Grants Damage Reduction of 5/+1 at Level 1.
	 *
	 * 2:SR:15	Grants Spell Resistance of 15 at Level 2.
	 *
	 * 3:CR:2	Grants an increase in Challenge Rating of two at Level 3.
	 *
	 * 4:SA:Uncanny Dodge	Grants the "Uncanny Dodge" special ability at Level 4.
	 *
	 * 5:FEAT:Alertness	Grants the "Alertness" feat at Level 5.
	 *
	 * 6:FEAT:TYPE.Fighter	Produces a popup menu at Level 6 from which a PC can choose a fighter feat.
	 *
	 * @param levelString a sting in the formate specified above
	 */
	public void addLevelString(final String levelString)
	{
		if (".CLEAR".equals(levelString))
		{
			if (levelStrings != null)
			{
				levelStrings.clear();
			}

			return;
		}
		StringTokenizer tok = new StringTokenizer(levelString, ":");
		String levelStr = tok.nextToken();
		String typeStr = tok.nextToken();
		if ("DR".equals(typeStr))
		{
			String drVal = tok.nextToken();
			String[] values = drVal.split("/");
			if (values.length == 2)
			{
				DamageReduction dr = new DamageReduction(values[0], values[1]);

				Prerequisite r = null;
				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();
					r = factory.parse("PRELEVEL:" + levelStr);
				}
				catch (PersistenceLayerException notUsed)
				{
					// Should never happen
				}
				if (r != null)
				{
					dr.addPreReq(r);
				}
				addDR(dr);
			}
			return;
		}

		if (levelStrings == null)
		{
			levelStrings = new ArrayList<String>();
		}

		levelStrings.add(levelString);
	}


	/**
	 * Grants the character an ability at the level specified (total character level).
	 * The text may contain the following tags: CR - Challenge Rating, DR - Damage
	 * Reduction, FEAT - Feat, SA - Special Ability, SR - Spell Resistance
	 *
	 * 1:DR:5/+1	Grants Damage Reduction of 5/+1 at Level 1.
	 *
	 * 2:SR:15	Grants Spell Resistance of 15 at Level 2.
	 *
	 * 3:CR:2	Grants an increase in Challenge Rating of two at Level 3.
	 *
	 * 4:SA:Uncanny Dodge	Grants the "Uncanny Dodge" special ability at Level 4.
	 *
	 * 5:FEAT:Alertness	Grants the "Alertness" feat at Level 5.
	 *
	 * 6:FEAT:TYPE.Fighter	Produces a popup menu at Level 6 from which a PC can choose a fighter feat.
	 *
	 * @return  an array of stings in the format specified
	 */
	public List<String> getLevelStrings()
	{
		return levelStrings != null ? levelStrings : Collections.EMPTY_LIST;
	}

	/**
	 * Add a list of subsidiary Templates to this template i.e. Templates (or
	 * choices of templates) that this Template will grant.
	 *
	 * @param  templateList the templates/template choices to add
	 */
	public void addTemplate(final String templateList)
	{
		if (templateList.startsWith("CHOOSE:"))
		{
			templates.add(templateList);
		}
		else
		{
			final StringTokenizer aTok = new StringTokenizer(templateList, "|");

			while (aTok.hasMoreTokens())
			{
				String templateName = aTok.nextToken();

				//
				// .CLEAR
				//
				if (".CLEAR".equalsIgnoreCase(templateName))
				{
					templates.clear();
				}

				//
				// .CLEAR.<template_name>
				//
				else if (templateName.regionMatches(true, 0, ".CLEAR.", 0, 7))
				{
					templateName = templateName.substring(7);

					for (int i = 0; i < templates.size(); ++i)
					{
						if (templateName.equalsIgnoreCase(templates.get(i)))
						{
							templates.remove(i);

							break;
						}
					}
				}

				//
				// Add a choice to a pre-existing CHOOSE
				//
				else if (templateName.startsWith("ADDCHOICE:"))
				{
					templateName = templateName.substring(10);

					for (int i = 0; i < templates.size(); ++i)
					{
						String aString = templates.get(i);

						if (aString.startsWith("CHOOSE:"))
						{
							aString = aString + "|" + templateName;
							templates.set(i, aString);

							break;
						}
					}
				}
				else
				{
					templates.add(templateName);
				}
			}
		}
	}


	/**
	 * Make a copy of this Template
	 *
	 * @return  a clone of this Template
	 *
	 * @throws  CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException
	{
		final PCTemplate aTemp = (PCTemplate) super.clone();
		aTemp.templates       = new ArrayList<String>(templates);

		if (getListSize(levelStrings) != 0)
		{
			aTemp.levelStrings = new ArrayList<String>(levelStrings);
		}

		if (getListSize(hitDiceStrings) != 0)
		{
			aTemp.hitDiceStrings = new ArrayList<String>(hitDiceStrings);
		}

		// if (getArrayListSize(sizeStrings) != 0)
		// {
		// aTemp.sizeStrings = (ArrayList) sizeStrings.clone();
		// }

		if (abilityCatStore != null) {
			aTemp.abilityCatStore = new AbilityStore();
			aTemp.abilityCatStore.addAbilityInfo(
					abilityCatStore.getParsableStringRepresentation(), "", "|", false, false);
		}

		if (getListSize(featStrings) != 0)
		{
			aTemp.featStrings = new ArrayList<String>(featStrings);
		}

		if (chosenFeatStrings != null)
		{
			aTemp.chosenFeatStrings = new HashMap<String, String>(chosenFeatStrings);
		}

		return aTemp;
	}


	/**
	 * Generate a string that represents the changes this Template will apply.
	 *
	 * @param   aPC  the Pc we'd like the string generated with reference to
	 *
	 * @return a string explaining the Template
	 */
	public String modifierString(final PlayerCharacter aPC)
	{
		final StringBuffer mods = new StringBuffer(50); // More likely to be true than 16
														// (the default)

		for (
			int x = 0;
			x < SettingsHandler.getGame().getUnmodifiableStatList().size();
			++x)
		{
			if (isNonAbility(x))
			{
				mods.append(statName(x)).append(":nonability ");
			}
			else
			{
				final int statMod = getStatMod(x, aPC);

				if (statMod != 0)
				{
					mods.append(statName(x)).append(':').append(statMod).append(' ');
				}
			}
		}

		if (!hitDieLock.equals(""))
		{
			mods.append("HITDIE:" + hitDieLock);
		}

		if (getDRList().size() != 0)
		{
			mods.append("DR:").append(DamageReduction.getDRString(aPC, getDRList()));
		}

		if (aPC == null)
		{
			if (ChallengeRating != 0)
			{
				mods.append("CR:").append(ChallengeRating).append(' ');
			}

			final int x = getSR(aPC);

			if (x != 0)
			{
				mods.append("SR:").append(x).append(' ');
			}

//			if ((getDR() != null) && !"".equals(getDR()))
//			{
//				mods.append("DR:").append(getDR()).append(' ');
//			}

			return mods.toString();
		}

		final int nat = (int) bonusTo("COMBAT", "AC", aPC, aPC);

		if (nat != 0)
		{
			mods.append("AC BONUS:").append(nat);
		}

		if (getCR(aPC.getTotalLevels(), aPC.totalHitDice()) != 0)
		{
			mods.append("CR:").append(getCR(aPC.getTotalLevels(), aPC.totalHitDice()))
				.append(' ');
		}

		if (getSR(aPC.getTotalLevels(), aPC.totalHitDice(), aPC) != 0)
		{
			mods.append("SR:")
				.append(getSR(aPC.getTotalLevels(), aPC.totalHitDice(), aPC)).append(' ');
		}

//		if (!getDR(aPC.getTotalLevels(), aPC.totalHitDice()).equals(""))
//		{
//			mods.append("DR:").append(getDR(aPC.getTotalLevels(), aPC.totalHitDice()))
//			    .append(' ');
//		}

		return mods.toString();
	}


	/**
	 * Returns an array list containing the templates granted by this template.
	 * If there are choices to be made
	 *
	 * @param   isImporting  Whether the PC is being imported
	 * @param   aPC
	 *
	 * @return  a list of templates
	 */
	List<String> getTemplates(final boolean isImporting, final PlayerCharacter aPC)
	{
		final List<String> newTemplates = new ArrayList<String>();
		templatesAdded = new ArrayList<String>();

		if (!isImporting)
		{
			for (Iterator<String> e = templates.iterator(); e.hasNext();)
			{
				String templateKey = e.next();

				if (templateKey.startsWith("CHOOSE:"))
				{
					templateKey = chooseTemplate(this, templateKey.substring(7), true, aPC);
				}

				if (templateKey.length() != 0)
				{
					newTemplates.add(templateKey);
					templatesAdded.add(templateKey);
				}
			}
		}

		return newTemplates;
	}


	/**
	 * Modify the list passed in to include any special abilities granted by this Template
	 *
	 * @param   aList    The list to be modified
	 * @param   level    The level to add Special abilities for
	 * @param   hitdice  the hit die (/range) to add Special Abilities for
	 *
	 * @return  the list passed in with any special abilities this template grants added to it
	 */
	List<SpecialAbility> addSpecialAbilitiesToList(final List<SpecialAbility> aList, final int level, final int hitdice)
	{
		/*
		 * CONSIDER Is this really proper behaviour?!?  If the PObject has anything, then
		 * do the detailed work?  That doesn't terribly make sense to me - TRP
		 */
		if (containsListFor(ListKey.SPECIAL_ABILITY))
		{
			aList.addAll(getSpecialAbilityList(level, hitdice));
		}

		return aList;
	}



	/**
	 * Choose a template from template list, allow the chooser to be closed without
	 * choosing a template
	 *
	 * @param   templateList  List of available templates
	 * @param   aPC           The Pc that prerequisites will be checked against
	 *
	 * @return  the chosen template
	 */
//	static String chooseTemplate(final String templateList, final PlayerCharacter aPC)
//	{
//		return chooseTemplate(templateList, false, aPC);
//	}


	/**
	 * Construct a chooser and ask the operator to choose a template
	 *
	 * @param   templateList  List of available templates
	 * @param   forceChoice   Whether the user is permitted to close the chooser without making a choice
	 * @param   aPC           The Pc that prerequisites will be checked against
	 *
	 * @return  the chosen template
	 */
	static String chooseTemplate(
		   final PObject anOwner,
		final String          templateList,
		final boolean         forceChoice,
		final PlayerCharacter aPC)
	{
		final List<PCTemplate> availableList = new ArrayList<PCTemplate>();
		final StringTokenizer strTok = new StringTokenizer(templateList.substring(7), "|");
		while (strTok.hasMoreTokens())
		{
			PCTemplate template = Globals.getTemplateKeyed(strTok.nextToken());
			if (template != null && PrereqHandler.passesAll(template.getPreReqList(), aPC, template))
			{
				availableList.add(template);
			}
		}

		final List<PCTemplate> selectedList = new ArrayList<PCTemplate>(1);
		String title = "Template Choice";
		if (anOwner != null)
		{
			title += " (" + anOwner.getDisplayName() + ")";
		}

		if (availableList.size() == 1)
		{
			return availableList.get(0).getKeyName();
		}
		Globals.getChoiceFromList(title, availableList, selectedList, 1, forceChoice);
		if (selectedList != null && selectedList.size() == 1)
		{
			return selectedList.get(0).getKeyName();
		}

		return "";
	}


	/**
	 * Add a subsidiary Template that this Template will add
	 *
	 * @param  templateName  the name of the Template to add
	 */
	public void addTemplateName(final String templateName)
	{
		if (templatesAdded == null)
		{
			templatesAdded = new ArrayList<String>();
		}
		templatesAdded.add(templateName);
	}


	/**
	 * Get a list of subsidiary Templates that will be added by this template
	 *
	 * @return  a list of Templates
	 */
	public List<String> templatesAdded()
	{
		return templatesAdded != null ? templatesAdded : Collections.EMPTY_LIST;
	}


	/**
	 * Get the size of a list, guaranteeing not to return null
	 *
	 * @param   al  The list whose size w'd like
	 *
	 * @return  size of the list or zero if list undefined
	 */
	private static int getListSize(final List<?> al)
	{
		int result = 0;

		if (al != null)
		{
			result = al.size();
		}

		return result;
	}


	/**
	 * Get the portion of a string after a given substring
	 *
	 * @param   stuff   the substring
	 * @param   string  The string to search
	 *
	 * @return  the trailing portion of string following substring stuff
	 */
	private static String getStringAfter(final String stuff, final String string)
	{
		final int index = string.indexOf(stuff) + stuff.length();

		return string.substring(index);
	}


	/**
	 * Does the string contains the given substring
	 *
	 * @param   string  The string to search
	 * @param   stuff   the substring being looked for
	 *
	 * @return  true if stuff is a substring of string
	 */
	private static boolean contains(final String string, final String stuff)
	{
		return string.indexOf(stuff) > -1;
	}


	/**
	 * convert a STAT index to a STAT name
	 *
	 * @param   x  the index of the STAT
	 *
	 * @return  the name of the STAT
	 */
	private static String statName(final int x)
	{
		return SettingsHandler.getGame().s_ATTRIBSHORT[x];
	}


	/**
	 * Does the hit die fall within the range of the Hit dice string specified
	 * by the index into the HitDiceStrings array
	 *
	 * @param   hitdice  the hit die
	 * @param   index    of the Hit Die expression to test
	 *
	 * @return  true if the hit hide qualifies
	 */
	private boolean doesHitDiceQualify(final int hitdice, final int index)
	{
		if (index >= getListSize(hitDiceStrings))
		{
			return false;
		}

		StringTokenizer tokens        = new StringTokenizer(
				hitDiceStrings.get(index),
				":");
		final String    hitDiceString = tokens.nextToken();

		if (hitDiceString.endsWith("+"))
		{
			return Integer.parseInt(hitDiceString.substring(0, hitDiceString.length() - 1)) <= hitdice;
		}

		tokens = new StringTokenizer(hitDiceString, "-");

		return (hitdice >= Integer.parseInt(tokens.nextToken())) &&
			(hitdice <= Integer.parseInt(tokens.nextToken()));
	}


	/**
	 * Is the level greater or equal to the level in the levelString
	 * indexed by index
	 *
	 * @param   level  The level to test
	 * @param   index  index of the level String to test
	 *
	 * @return  true if level is >= the level referenced by index
	 */
	private boolean doesLevelQualify(final int level, final int index)
	{
		if (index >= getListSize(levelStrings))
		{
			return false;
		}

		final StringTokenizer stuff = new StringTokenizer(
				levelStrings.get(index),
				":");

		return level >= Integer.parseInt(stuff.nextToken());
	}


	/**
	 * This is the function that implements a chooser for Feats granted by level
	 * and/or HD by Templates.
	 *
	 * @param  levelString  The string to be parsed for the choices to offer
	 * @param  lvl          The level this is being added at
	 * @param  featKey      either L<lvl> or H<lvl>
	 * @param  aPC          The PC that this Template is appled to
	 */
	private void getLevelFeat(
		final String          levelString,
		final int             lvl,
		final String          featKey,
		final PlayerCharacter aPC)
	{
		if (contains(levelString, "FEAT:"))
		{
			String featName = getStringAfter("FEAT:", levelString);

			while (true)
			{
				List<String> featList = new ArrayList<String>();
				final LevelAbility la       = LevelAbility.createAbility(
						this,
						lvl,
						"FEAT(" + featName + ")");

				la.process(featList, aPC, null);

				switch (featList.size())
				{
					case 1:
						featName = featList.get(0);

						break;

					default:

						if ((aPC != null) && !aPC.isImporting())
						{
							Collections.sort(featList);

							final ChooserInterface c = ChooserFactory.getChooserInstance();
							c.setPool(1);
							c.setTitle("Feat Choice");
							c.setAvailableList(featList);
							c.setVisible(true);
							featList = c.getSelectedList();

							if ((featList != null) && (featList.size() != 0))
							{
								featName = featList.get(0);

								continue;
							}
						}

					// fall-through intentional
					case 0:
						return;
				}

				break;
			}

			final LevelAbility la = LevelAbility.createAbility(this, lvl, "FEAT(" + featName + ")");

			aPC.setAllowFeatPoolAdjustment(false);
			la.process(null, aPC, null);
			aPC.setAllowFeatPoolAdjustment(true);

			addChosenFeat(featKey, featName);
		}
	}


	/**
	 * Add a chosen feat to the Template
	 *
	 * @param  mapKey    The key to store the feat under
	 * @param  mapValue  The name of the feat
	 */
	public void addChosenFeat(final String mapKey, final String mapValue)
	{
		if (chosenFeatStrings == null)
		{
			chosenFeatStrings = new HashMap<String, String>();
		}

		chosenFeatStrings.put(mapKey, mapValue);
	}


	/**
	 * Add a | separated list of available abilities that this Template may
	 * grant.  This is the function called by the Lst parser to make the
	 * Abilities available to this Template.
	 *
	 * See AbilityStore.addAbilityInfo for details of the string
	 *
	 * @param abilityString
	 */
	public void addAbilityString (final String abilityString) {

		if (".CLEAR".equals(abilityString))
		{
			abilityCatStore = null;
			return;
		}

		if (abilityString == null || "".equals(abilityString))
		{
			return;
		}

		if (abilityCatStore == null) {
			abilityCatStore = new AbilityStore();
		}

		abilityCatStore.addAbilityInfo(abilityString, "", "|", false, false);
	}

	/**
	 * Add a | separated list of available feats that this Template may grant.
	 * This is the function called by the Lst parser to make the feats
	 * available to this Template
	 *
	 * @param  abilityString  The | separated list of feats
	 */
	public void addFeatString(final String abilityString)
	{
		if (".CLEAR".equals(abilityString))
		{
			abilityCatStore = null;
			return;
		}

		addAbilityString("CATEGORY=FEAT|" + abilityString);
	}


	/**
	 * TODO DOCUMENT ME!
	 *
	 * @param   level    TODO DOCUMENT ME!
	 * @param   hitdice  TODO DOCUMENT ME!
	 * @param   aPC      TODO DOCUMENT ME!
	 * @param   addNew   TODO DOCUMENT ME!
	 *
	 * @return  TODO DOCUMENT ME!
	 */
	public List<String> feats(
		final int             level,
		final int             hitdice,
		final PlayerCharacter aPC,
		final boolean         addNew)
	{
		final List<String> feats;

		if (getListSize(featStrings) != 0)
		{
			feats = new ArrayList<String>(featStrings);
		}
		else
		{
			feats = new ArrayList<String>();
		}

		/* This is very, very temporary.
		 *
		 * This needs to be changed very soon so that this entire routine uses
		 * AbilityInfo objects instead of the names of abilities.
		 */
		if (abilityCatStore != null) {
			Iterator<Categorisable> it = abilityCatStore.getKeyIterator("ALL");

			while (it.hasNext()) {
				feats.add(it.next().getKeyName());
			}
		}

		// arknight modified this back in 1.27 with the comment: Added support for
		// Spycraft Game Mode we no longer support Spycraft (at this time), and this
		// breaks other modes, so I've reverting back to the old method. I am also fixing
		// a bug in the code I'm commenting out. levelStrings is used in the 2nd loop
		// instead of hitDiceStrings. - Byngl Sept 25, 2003
		//
		// Scrap all that. I'm using a HashMap to save those feats that have been taken when
		// the required level/hitdie has been met. We need to do this so that removing the
		// template will also remove the selected feat(s). PCTemplate instances will also
		// need to be cloned() when adding them to PlayerCharacter.
		if (chosenFeatStrings != null)
		{
			feats.addAll(chosenFeatStrings.values());
		}

		for (int x = 0; x < getListSize(levelStrings); ++x)
		{
			final String featKey  = "L" + Integer.toString(x);
			String       featName = null;

			if (chosenFeatStrings != null)
			{
				featName = chosenFeatStrings.get(featKey);
			}

			if ((featName == null) && addNew)
			{
				if (doesLevelQualify(level, x))
				{
					getLevelFeat(levelStrings.get(x), level, featKey, aPC);
				}
			}
		}

		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			final String featKey  = "H" + Integer.toString(x);
			String       featName = null;

			if (chosenFeatStrings != null)
			{
				featName = chosenFeatStrings.get(featKey);
			}

			if ((featName == null) && addNew)
			{
				if (doesHitDiceQualify(hitdice, x))
				{
					getLevelFeat(hitDiceStrings.get(x), -1, featKey, aPC);
				}
			}
		}

		return feats;
	}

	/**
	 * Set face
	 * @param width
	 * @param height
	 */
	public void setFace(final double width, final double height)
	{
		face = new Point2D.Double(width, height);
	}

	/**
	 * Get face
	 * @return face
	 */
	public Point2D.Double getFace()
	{
		return face;
	}

	/**
	 * Set hands
	 * @param newHands
	 */
	public void setHands(final int newHands)
	{
		hands = new Integer(newHands);
	}

	/**
	 * Made public for use on equipping tab -- bug 586332
	 * sage_sam, 22 Nov 2002
	 * @return hands
	 */
	public Integer getHands()
	{
		return hands;
	}

	/**
	 * Set Legs
	 * @param argLegs
	 */
	public void setLegs(final int argLegs)
	{
		legs = new Integer(argLegs);
	}

	/**
	 * Get Legs
	 * @return legs
	 */
	public Integer getLegs()
	{
		return legs;
	}

	/**
	 * Set reach
	 * @param newReach
	 */
	public void setReach(final int newReach)
	{
		reach = new Integer(newReach);
	}

	/**
	 * Get reach
	 * @return reach
	 */
	public Integer getReach()
	{
		return reach;
	}

	/**
	 * Add level modifier
	 * @param aMod
	 */
	public void addLevelMod(final String aMod)
	{
		levelMods.add(aMod);
	}

	/**
	 * Get level modifiers
	 * @return level modifiers
	 */
	public List<String> getLevelMods()
	{
		return Collections.unmodifiableList(levelMods);
	}
}
