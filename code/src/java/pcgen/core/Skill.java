/*
 * Skill.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Visibility;

/**
 * <code>Skill</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Skill extends PObject
{
	//constants for Cost Type String
    /** Cost is CLASS cost */
	public static final String COST_CLASS = "CLASS";
    /** Cost is CROSS-CLASS cost */
    public static final String COST_XCLASS = "CROSS-CLASS";
    /** Cost is Exclusive cost */
    public static final String COST_EXCL = "EXCLUSIVE";

	private static final String COST_UNK = "UNKNOWN";
	private static final int ACHECK_NONE = 0; // No
	private static final int ACHECK_YES = 1; // Yes
	private static final int ACHECK_NONPROF = 2; // Only if not proficient
	private static final int ACHECK_WEIGHT = 3; // -1 per 5 lbs carried or equipped
	private static final int ACHECK_DOUBLE = 4; // Double penalty (e.g. for D&D 3.5 swim skill)
	/** Include No skills = 0 */
    public static final int INCLUDE_SKILLS_NONE = 0;
    /** Include Untrained skills = 1 */
    public static final int INCLUDE_SKILLS_UNTRAINED = 1;
    /** Include All skills = 2 */
    public static final int INCLUDE_SKILLS_ALL = 2;
    /** Include skills as per UI choice = 3 */
    public static final int INCLUDE_SKILLS_AS_UI = 3;

	private List<String> classList = new ArrayList<String>(); // list of classes with class-access to this skill
	private List<String> rankList = new ArrayList<String>();
	private String keyStat = "";
	private String rootName = "";

	private boolean skillReadOnly = false;

	private boolean canUseUntrained = true;
	private boolean isExclusive = false;
	private boolean required = false;
	private int aCheck = ACHECK_NONE;
	private int outputIndex = 0;

    /** Constructor */
	public Skill()
	{
		// Empty Constructor
	}

    /**
     * Set what armor/encumerance check is applied
     * @param aString
     */
	public void setACheck(final String aString)
	{
		if (aString.length() != 0)
		{
			switch (aString.charAt(0))
			{
				case 'N':
					aCheck = ACHECK_NONE;

					break;

				case 'Y':
					aCheck = ACHECK_YES;

					break;

				case 'P':
					aCheck = ACHECK_NONPROF;

					break;

				case 'D':
					aCheck = ACHECK_DOUBLE;

					break;

				case 'W':
					aCheck = ACHECK_WEIGHT;

					break;

				default:
					break;
			}
		}
	}

    /**
     * Set Armor/encumerance check
     * @param argACheck
     */
	public void setACheck(final int argACheck)
	{
		aCheck = argACheck;
	}

    /**
     * Get armor/encumberance check
     * @return check
     */
	public int getACheck()
	{
		return aCheck;
	}

	/**
     * Get a list of classes
     * @return classList
     */
	public List<String> getClassList()
	{
		return classList;
	}

	/**
     * Returns true if it is a CLASS skill
     *  
     * @param aClass
     * @param aPC
     * @return true if it is a CLASS skill
	 */
    public boolean isClassSkill(final PCClass aClass, final PlayerCharacter aPC)
	{
		if ((aPC == null) || (aClass == null))
		{
			return false;
		}

		if (aPC.getRace().hasCSkill(keyName))
		{
			return true;
		}

		// hasSkill is a LevelAbility skill
		if (aClass.hasSkill(keyName))
		{
			return true;
		}

		// hasCSkill is a class.lst loader skill
		if (aClass.hasCSkill(keyName))
		{
			return true;
		}

		// test for SKILLLIST skill
		if (aClass.hasClassSkill(keyName))
		{
			return true;
		}

		if (aClass.isMonster())
		{
			if (aPC.getRace().hasMonsterCSkill(keyName))
			{
				return true;
			}
		}

		for (String aString : classList)
		{
			if ((aString.length() > 0) && (aString.charAt(0) == '!')
				&& (aString.substring(1).equalsIgnoreCase(aClass.getKeyName())
				|| aString.substring(1).equalsIgnoreCase(aClass.getSubClassKey())))
			{
				return false; // this is an excluded-from-class-skill list
			}

			if ("ALL".equals(aString) || aString.equalsIgnoreCase(aClass.getKeyName())
				|| aString.equalsIgnoreCase(aClass.getSubClassKey())
				|| ((aClass.getClassSkillList() != null) && aClass.getClassSkillList().contains(aString)))
			{
				return true;
			}
		}

		for (CharacterDomain aCD : aPC.getCharacterDomainList())
		{
			if ((aCD.getDomain() != null) && aCD.isFromPCClass(aClass.getKeyName()) && aCD.getDomain().hasCSkill(keyName))
			{
				return true;
			}
		}

		if ((aPC.getDeity() != null) && aPC.getDeity().hasCSkill(keyName))
		{
			return true;
		}

		for (Ability aFeat : aPC.aggregateFeatList())
		{
			if (aFeat.hasCSkill(keyName))
			{
				return true;
			}
		}

		for (Skill aSkill : aPC.getSkillList())
		{
			if (aSkill.hasCSkill(keyName))
			{
				return true;
			}
		}

		for (Equipment eq : aPC.getEquipmentList())
		{
			if (eq.isEquipped())
			{
				if (eq.hasCSkill(keyName))
				{
					return true;
				}

				for (EquipmentModifier eqMod : eq.getEqModifierList(true))
				{
					if (eqMod.hasCSkill(keyName))
					{
						return true;
					}
				}

				for (EquipmentModifier eqMod : eq.getEqModifierList(false))
				{
					if (eqMod.hasCSkill(keyName))
					{
						return true;
					}
				}
			}
		}

		for (PCTemplate aTemplate : aPC.getTemplateList())
		{
			if (aTemplate.hasCSkill(keyName))
			{
				return true;
			}
		}

		return false;
	}

	// Convenience methods
    /**
     * @return "Y" if skill is exclusive 
     */
	public String getExclusive()
	{
		return isExclusive ? "Y" : "N";
	}

    /**
     * Return true if exclusive
     * @return true if exclusive
     */
	public boolean isExclusive()
	{
		return isExclusive;
	}

	/**
     * Set whether skill is exclusive or not 
     * @param argExclusive
	 */
    public void setIsExclusive(final boolean argExclusive)
	{
		isExclusive = argExclusive;
	}

	/**
     * Set key stat for the skill 
     * @param aString
	 */
    public void setKeyStat(final String aString)
	{
		keyStat = aString;
	}

	/**
     * Get the key stat for a skill 
     * @return key stat
	 */
    public String getKeyStat()
	{
		return keyStat;
	}

	/**
	 * Set this skill's output index, which controls the order
	 * in which the skills appear on a character sheet.
	 * Note: -1 means hidden and 0 means nto set
	 *
	 * <br>author: James Dempsey 14-Jun-02
	 *
	 * @param newIndex the new output index for this skill (-1=hidden, 0=not set)
	 */
	public void setOutputIndex(final int newIndex)
	{
		outputIndex = newIndex;
	}

	/**
	 * Return the output index, which controls the order in
	 * which the skills appear on a character sheet
	 * Note: -1 means hidden and 0 means nto set
	 *
	 * <br>author: James Dempsey 14-Jun-02
	 *
	 * @return the output index for this skill (-1=hidden, 0=not set)
	 */
	public int getOutputIndex()
	{
		return outputIndex;
	}

	/**
	 * Made public on 10 Dec 2002 by sage_sam to match PObject method
	 * @return String
	 */
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getDisplayName());

		if (keyStat.length() != 0)
		{
			txt.append("\tKEYSTAT:").append(keyStat);
		}

		if (isExclusive)
		{
			txt.append("\tEXCLUSIVE:YES");
		}

		if (!isUntrained())
		{
			txt.append("\tUSEUNTRAINED:NO");
		}

		final StringBuffer aString = new StringBuffer(100);

		for (String s : getClassList())
		{
			if (aString.length() != 0)
			{
				aString.append('|');
			}

			aString.append(s);
		}

		if (aString.length() != 0)
		{
			txt.append("\tCLASSES:").append(aString);
		}

		if (aCheck != ACHECK_NONE)
		{
			txt.append("\tACHECK:");

			switch (aCheck)
			{
				case ACHECK_YES: // Yes
					txt.append("YES");

					break;

				case ACHECK_NONPROF: // Only if not proficient
					txt.append("PROFICIENT");

					break;

				case ACHECK_WEIGHT: // -1 per 5 lbs carried or equipped
					txt.append("WEIGHT");

					break;

				case ACHECK_DOUBLE: // Double penalty
					txt.append("DOUBLE");

					break;

				default:
					txt.append("ERROR");

					break;
			}
		}

		if (getVisibility() != Visibility.DEFAULT)
		{
			txt.append("\tVISIBLE:");
			switch (getVisibility())
			{
				case OUTPUT_ONLY:
					txt.append("EXPORT");
					break;

				case DISPLAY_ONLY:
					txt.append("GUI");
					break;

				default:
					txt.append("YES");
					break;
			}
		}

		txt.append(super.getPCCText(false));

		return txt.toString();
	}

	/** returns ranks taken specifically in skill
	 * @return ranks taken in skill
	 */
	public Float getRank()
	{
		double rank = 0.0;

		for (int i = 0; i < rankList.size(); i++)
		{
			final String bSkill = rankList.get(i);
			final int iOffs = bSkill.indexOf(':');

			//
			// Ignore -1 return code (as -1 + 1 = 0 and that's the start of the string)
			//
			rank += Double.parseDouble(bSkill.substring(iOffs + 1));
		}

		return new Float(rank);
	}

    /**
     * Get list of ranks
     * @return rankList
     */
	public List<String> getRankList()
	{
		return rankList;
	}

    /**
     * Set required attribute
     * @param argRequired
     */
	public void setRequired(final boolean argRequired)
	{
		required = argRequired;
	}

    /**
     * Set root name
     * @param aString
     */
	public void setRootName(final String aString)
	{
		rootName = aString;
	}

	/**
     * Get the type of cost for a skill 
     * @param aClass
     * @param aPC
     * @return CLASS, CROSS-CLASS or Exclusive
	 */
    public String getSkillCostType(final PCClass aClass, final PlayerCharacter aPC)
	{
		// This is dippy!  So if the user sets costs to something non-standard, the matching no longer works.  XXX
		// isCrossClassSkill() doesn't appear to work, so just go by actual cost values
		if (costForPCClass(aClass, aPC) == Globals.getGameModeSkillCost_Class())
		{
			return COST_CLASS;
		}
		else if (costForPCClass(aClass, aPC) == Globals.getGameModeSkillCost_CrossClass())
		{
			return COST_XCLASS;
		}
		else if (isExclusive)
		{
			return COST_EXCL;
		}

		return COST_UNK;
	}

    /**
     * Get a count of the sub types
     * @return count of sub types
     */
    public int getSubtypeCount()
	{
		final int i = getMyTypeCount();

		if (i == 0)
		{
			return 0;
		}

		return i - 1; // ignore first entry, the keystat
	}

	/**
     * Get an iterator for the sub types 
     * @return iterator for the sub types
	 */
    public Iterator<String> getSubtypeIterator()
	{
		final Iterator<String> it = getTypeList(false).iterator();

		if (it.hasNext())
		{
			if (keyStat.length() != 0)
			{
				it.next(); // skip first entry, the keystat
			}
		}

		return it;
	}

	/**
	 * Returns the total ranks of a skill
	 *  rank + bonus ranks (racial, class, etc bonuses).
	 * Note that the total ranks could be higher than the max 
	 * ranks if the ranks come from a familiar's master.
	 *
	 * @param aPC
	 * @return rank + bonus ranks (racial, class, etc. bonuses)
	 */
	public Float getTotalRank(final PlayerCharacter aPC)
	{
		double baseRanks = getRank().doubleValue();
		double ranks = baseRanks + getRankAdj(aPC).doubleValue();
		if (!Globals.checkRule(RuleConstants.SKILLMAX)
			&& aPC.getClassList().size() > 0)
		{
			double maxRanks =
					aPC.getMaxRank(getKeyName(), aPC.getClassList().get(0))
						.doubleValue();
			maxRanks = Math.max(maxRanks, baseRanks);
			ranks = Math.min(maxRanks, ranks);
		}
		return new Float(ranks);
	}

    /*
     * Set whether skill can be used untrined or not
     * @param yesNo
     */
	public void setUntrained(final boolean yesNo)
	{
		canUseUntrained = yesNo;
	}

    /**
     * Return true if skill can be used untrained
     * @return true if skill can be used untrained
     */
	public boolean isUntrained()
	{
		return canUseUntrained;
	}

	/** Set the ranks for the specified class to zero
	 *
	 * @param aClass
	 * @param aPC
	 */
	public void setZeroRanks(final PCClass aClass, final PlayerCharacter aPC)
	{
		if (aClass == null)
		{
			return;
		}

		final String aCName = aClass.getKeyName();
		String bSkill = "";
		int idx;

		//
		// Find the skill and class in question
		//
		final String aCNameString = aCName + ":";

		for (idx = 0; idx < rankList.size(); idx++)
		{
			bSkill = rankList.get(idx);

			if (bSkill.startsWith(aCNameString))
			{
				break;
			}
		}

		if (idx >= rankList.size())
		{
			return;
		}

		final double curRankCost = Double.parseDouble(bSkill.substring(aCName.length() + 1));
		final String aResp = modRanks(-curRankCost, aClass, false, aPC);

		if (aResp.length() != 0)
		{
			// error or debug? XXX
			Logging.debugPrint(aResp);
		}
	}

    /**
     * Add to the list of classes
     * @param aString
     */
	public void addClassList(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			// could be "ALL", "some_class_name" or "!some_class_name"
			// sort the !some_class_name to the front of classList
			if ((bString.length() > 0) && (bString.charAt(0) == '!') && (classList.size() > 0))
			{
				classList.add(0, bString);
			}
			else
			{
				classList.add(bString);
			}
		}
	}

	/**
	 * Choose the language that is to be gained for the default language skill.
	 * The default language skill will be the first one found when scanning the
	 * PCs skill list.
	 * Note: This pops up a chooser so should not be used in batch mode.
	 *
	 * @param aPC The character to choose a language for.
	 * @return false if the laguage choice could not be offered. True otherwise.
	 */
	public static boolean chooseLanguageForSkill(final PlayerCharacter aPC)
	{
		Skill languageSkill = null;

		for (Skill aSkill : aPC.getSkillList())
		{
			if (aSkill.getChoiceString().toLowerCase().indexOf("language") >= 0)
			{
				languageSkill = aSkill;
			}
		}

		return chooseLanguageForSkill(aPC, languageSkill);
	}

	/**
	 * Choose the language that is to be gained for the specified language skill.
	 * Note: This pops up a chooser so should not be used in batch mode.
	 *
	 * @param aPC The character to choose a language for.
	 * @param languageSkill The language skill.
	 * @return false if the laguage choice could not be offered. True otherwise.
	 */
	public static boolean chooseLanguageForSkill(final PlayerCharacter aPC,
		final Skill languageSkill)
	{
		if (aPC != null)
		{
			if (languageSkill == null)
			{
				ShowMessageDelegate.showMessageDialog(
					"You do not have enough ranks in Speak Language",
					Constants.s_APPNAME, MessageType.ERROR);

				return false;
			}

			final int numLanguages = languageSkill.getTotalRank(aPC).intValue();
			final List<String> selectedLangNames = new ArrayList<String>();
			final List<Language> selected = new ArrayList<Language>();
			final List<Language> available = new ArrayList<Language>();
			final List<Language> excludedLangs = new ArrayList<Language>();

			String reqType = null;
			if (languageSkill.getChoiceString().toLowerCase().indexOf(
				"language(") >= 0)
			{
				// We expect to have a choice string like Language(foo)
				// where foo is the type we have to limit choices by.
				String choiceParts[] = languageSkill.getChoiceString().split(
					"[\\(\\)]");
				if (choiceParts.length >= 2)
				{
					reqType = choiceParts[1];
				}
			}
			
			String[][] reqTypeArray;
			if (reqType == null)
			{
				reqTypeArray = null;
			}
			else
			{
				String[] rta = reqType.split(",");
				reqTypeArray = new String[rta.length][];
				for (int i = 0; i < rta.length; i++)
				{
					reqTypeArray[i] = rta[i].split("\\.");
				}
			}

			languageSkill.addAssociatedTo(selectedLangNames);

			for ( String aString : selectedLangNames )
			{
				final Language aLang = Globals.getLanguageKeyed(aString);

				if (aLang == null)
				{
					continue;
				}
				if (reqTypeArray == null)
				{
					selected.add(aLang);
					continue;
				}
				SELARRAY: for (String[] types : reqTypeArray)
				{
					for (String type : types)
					{
						if (!aLang.isType(type))
						{
							continue SELARRAY;
						}
					}
					selected.add(aLang);
				}
			}

			for ( Language lang : Globals.getLanguageList() )
			{
				if (!PrereqHandler.passesAll(lang.getPreReqList(), aPC,
						lang))
				{
					continue;
				}
				if (reqTypeArray == null)
				{
					available.add(lang);
					continue;
				}
				AVARRAY: for (String[] types : reqTypeArray)
				{
					for (String type : types)
					{
						if (!lang.isType(type))
						{
							continue AVARRAY;
						}
					}
					available.add(lang);
				}
			}

			//
			// Do not give choice of automatic languages
			//
			for ( Language lang : aPC.getAutoLanguages() )
			{
				available.remove(lang);
				excludedLangs.add(lang);
			}

			//
			// Do not give choice of selected bonus languages
			//
			for ( final Language lang : aPC.getLanguagesList() )
			{
				if (!selected.contains(lang))
				{
					if ((reqType == null || lang.isType(reqType)))
					{
						available.remove(lang);
					}
					excludedLangs.add(lang);
				}
			}

			Globals.sortChooserLists(available, selected);

			final ChooserInterface lc = ChooserFactory.getChooserInstance();
			lc.setVisible(false);
			lc.setAvailableList(available);
			lc.setSelectedList(selected);
			lc.setPool(numLanguages - selected.size());
			lc.setPoolFlag(false);
			lc.setVisible(true);

			aPC.clearLanguages();
			aPC.addLanguages(selected);

			// Add in all choice-excluded languages
			aPC.addLanguages(excludedLangs);
			languageSkill.clearAssociated();
			// TODO Fix this to allow Language objects.
			for ( Iterator<?> i = lc.getSelectedList().iterator(); i.hasNext(); )
			{
				languageSkill.addAssociated( ((Language)i.next()).getKeyName() );
			}
			aPC.setDirty(true);

			return true;
		}

		return false;
	}

    /**
     * Set read only attribute for skill
     * @param argReadOnly
     */
	public void setReadOnly(final boolean argReadOnly)
	{
		skillReadOnly = argReadOnly;
	}
	
    /**
     * Return true if skill is read only
     * @return true if skill is read only
     */
	public boolean isReadOnly()
	{
		return skillReadOnly;
	}

	@Override
	public Skill clone()
	{
		Skill newSkill = null;

		try
		{
			newSkill = (Skill) super.clone();
			newSkill.required = required;
			newSkill.setRootName(rootName);
			newSkill.setKeyStat(getKeyStat());
			newSkill.setIsExclusive(isExclusive());
			newSkill.rankList = new ArrayList<String>(rankList);
			newSkill.setUntrained(isUntrained());
			newSkill.classList = new ArrayList<String>(classList);
			newSkill.aCheck = aCheck;
			newSkill.skillReadOnly = skillReadOnly;

			newSkill.outputIndex = outputIndex;
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}

		return newSkill;
	}

    /**
     * Get the actual cost of a skill point
     * @param aClass
     * @param aPC
     * @return cost of a skill point
     */
	public int costForPCClass(final PCClass aClass, final PlayerCharacter aPC)
	{
		int anInt;
		/*
		if (!PrereqHandler.passesAll(getPreReqList(), aPC, this))
		{
			anInt = Globals.getGameModeSkillCost_Exclusive();	// treat cost of unqualified skills as exclusive
		}
		else*/ if (isClassSkill(aClass, aPC))
		{
			anInt = Globals.getGameModeSkillCost_Class();
		}
		else if (!isCrossClassSkill(aClass, aPC) && isExclusive)
		{
			anInt = Globals.getGameModeSkillCost_Exclusive();
		}
		else
		{
			anInt = Globals.getGameModeSkillCost_CrossClass();
		}

		return anInt;
	}

	/**
     * Modify the rank 
     * 
     * @param rankMod
     * @param aClass
     * @param aPC
     * @return message
	 */
    public String modRanks(final double rankMod, final PCClass aClass, final PlayerCharacter aPC)
	{
		return modRanks(rankMod, aClass, false, aPC);
	}

    /**
     * Modify the rank 
     * 
     * @param rankMod
     * @param aClass
     * @param ignorePrereqs
     * @param aPC
     * @return message
     */
	public String modRanks(double rankMod, final PCClass aClass, final boolean ignorePrereqs, final PlayerCharacter aPC)
	{
		int i = 0;

		if (!ignorePrereqs)
		{
			if (aClass == null)
			{
				return "You must be at least level one before you can purchase skills.";
			}

			if ((rankMod > 0.0) && !PrereqHandler.passesAll(getPreReqList(), aPC, this))
			{
				return "You do not meet the prerequisites required to take this skill.";
			}

			i = costForPCClass(aClass, aPC);

			if (i == Globals.getGameModeSkillCost_Exclusive())
			{
				return "You cannot purchase this exclusive skill.";
			}

			if ((rankMod > 0.0) && (aClass.getSkillPool(aPC) < (rankMod * i)))
			{
				return "You do not have enough skill points.";
			}

			final double maxRank = aPC.getMaxRank(keyName, aClass).doubleValue();

			if (!Globals.checkRule(RuleConstants.SKILLMAX) && (rankMod > 0.0))
			{
				final double ttlRank = getTotalRank(aPC).doubleValue();

				if (ttlRank >= maxRank)
				{
					return "Skill rank at maximum (" + maxRank + ") for your level.";
				}

				if ((ttlRank + rankMod) > maxRank)
				{
					return "Raising skill would make it above maximum (" + maxRank + ") for your level.";
				}
			}
		}

		if ((getRank().doubleValue() + rankMod) < 0.0)
		{
			return "Cannot lower rank below 0";
		}

		String classKey = "None";

		if (aClass != null)
		{
			classKey = aClass.getKeyName();
		}

		String bSkill = "";
		int idx;

		//
		// Find the skill and class in question
		//
		final String classKeyString = classKey + ":";

		for (idx = 0; idx < rankList.size(); idx++)
		{
			bSkill = rankList.get(idx);

			if (bSkill.startsWith(classKeyString))
			{
				break;
			}
		}

		if (idx >= rankList.size())
		{
			//
			// If we are trying to lower a rank, and we happen to be using an older
			// character we've loaded, check to see if there is a value for class "None"
			// and allow the user to modify this.
			//
			if (rankMod < 0.0)
			{
				for (idx = 0; idx < rankList.size(); idx++)
				{
					bSkill = rankList.get(idx);

					if (bSkill.startsWith("None:"))
					{
						break;
					}
				}
			}

			if (idx >= rankList.size())
			{
				bSkill = classKey + ":0";
			}
		}

		final int iOffs = bSkill.indexOf(':');
		final double curRank = Double.parseDouble(bSkill.substring(iOffs + 1));

		if (CoreUtility.doublesEqual(curRank, 0.0) && (rankMod < 0.0))
		{
			return "No more ranks found for class: " + classKey + ". Try a different one.";
		}

		if (idx >= rankList.size())
		{
			rankList.add(idx, bSkill);
		}

		rankMod = modRanks2(rankMod, idx, bSkill, aPC);

		if (!ignorePrereqs)
		{
			if (aClass != null)
			{
				aClass.setSkillPool(aClass.getSkillPool(aPC) - (int) (i * rankMod));
			}

			aPC.setSkillPoints(aPC.getSkillPoints() - (int) (i * rankMod));
		}

		return "";
	}

	/**
     * return the modifier on a skill 
     * @param aPC
     * @return modifier
	 */
    public Integer modifier(final PlayerCharacter aPC)
	{
		int bonus = 0;
		if (aPC == null)
		{
			return Integer.valueOf(0);
		}

		final int stat = SettingsHandler.getGame().getStatFromAbbrev(keyStat);
		if (stat >= 0)
		{
			bonus = aPC.getStatList().getStatModFor(keyStat);
			bonus += aPC.getTotalBonusTo("SKILL", "STAT." + keyStat);
		}
		bonus += aPC.getTotalBonusTo("SKILL", keyName);

		// loop through all current skill types checking for boni
		for (String singleType : getTypeList(false))
		{
			bonus += aPC.getTotalBonusTo("SKILL", "TYPE." + singleType);
		}

		// now check for any lists of skills, etc
		bonus += aPC.getTotalBonusTo("SKILL", "LIST");

		// now check for ALL
		bonus += aPC.getTotalBonusTo("SKILL", "ALL");

		//these next two if-blocks try to get BONUS:[C]CSKILL|TYPE=xxx|y to function
		if (isClassSkill(aPC.getClassList(), aPC))
		{
			bonus += aPC.getTotalBonusTo("CSKILL", keyName);

			//loop through all current skill types checking for boni
			for (String singleType : getTypeList(false))
			{
				bonus += aPC.getTotalBonusTo("CSKILL", "TYPE." + singleType);
			}

			bonus += aPC.getTotalBonusTo("CSKILL", "LIST");
		}

		if (!isClassSkill(aPC.getClassList(), aPC) && !isExclusive())
		{
			bonus += aPC.getTotalBonusTo("CCSKILL", keyName);

			//loop through all current skill types checking for boni
			for (String singleType : getTypeList(false))
			{
				bonus += aPC.getTotalBonusTo("CCSKILL", "TYPE." + singleType);
			}

			bonus += aPC.getTotalBonusTo("CCSKILL", "LIST");
		}

		// the above two if-blocks try to get
		// BONUS:[C]CSKILL|TYPE=xxx|y to function
		// now check for a racial bonus
		bonus += aPC.getRace().bonusForSkill(getKeyName());

		final int aCheckBonus = calcACheckBonus(aPC);
		bonus += aCheckBonus;

		String aString = Globals.getGameModeRankModFormula();
		if (aString.length() != 0)
		{
			aString = CoreUtility.replaceAll(aString, "$$RANK$$", getTotalRank(aPC).toString());
			bonus += aPC.getVariableValue(aString, "").intValue();
		}

		return Integer.valueOf(bonus);
	}

	/**
	 * Calculate the modifier to this skill due to armour check and encumbrance
	 * for the specified character.
	 *
	 * @param aPC The character to be checked.
	 * @return The skill modifier.
	 */
	private int calcACheckBonus(final PlayerCharacter aPC)
	{
		if (aCheck == ACHECK_NONE)
		{
			return 0;
		}

		int minBonus = 0;
		int maxBonus = 0;
		final Float totalWeight = aPC.totalWeight();

		if (!Globals.checkRule(RuleConstants.SYS_WTPSK))
		{
			//Do nothing. This is to simulate taking everything off before going swimming. Freq #505977
		}
		else if ((aCheck == ACHECK_WEIGHT) && Globals.checkRule(RuleConstants.SYS_WTPSK))
		{
			maxBonus = -(int) (totalWeight.doubleValue() / 5.0);
		}
		else if (aCheck == ACHECK_WEIGHT)
		{
			//Do nothing. This is to simulate taking everything off before going swimming. Freq #505977
		}
		else
		{
			if ((aCheck != ACHECK_NONPROF) && Globals.checkRule(RuleConstants.SYS_LDPACSK))
			{
				final Load load = Globals.loadTypeForLoadScore(aPC
					.getVariableValue("LOADSCORE", "").intValue(), aPC
					.totalWeight(), aPC);

				int penalty = 0;
				switch (load)
				{
					case LIGHT:
						penalty = SystemCollections.getLoadInfo().getLoadCheckPenalty("LIGHT");
						break;

					case MEDIUM:
						penalty = SystemCollections.getLoadInfo().getLoadCheckPenalty("MEDIUM");
						break;

					case HEAVY:
					case OVERLOAD:
						penalty = SystemCollections.getLoadInfo().getLoadCheckPenalty("HEAVY");
						break;

					default:
						Logging.errorPrint(getDisplayName()
							+ ":in Skill.modifier the load " + load	+ " is not supported.");
						break;
				}
				minBonus = (aCheck == ACHECK_DOUBLE) ? 2 * penalty : penalty;
			}

			final List<Equipment> itemList = aPC.getEquipmentOfType("Armor", 1);
			for ( Equipment eq : aPC.getEquipmentOfType("Shield", 1) )
			{
				if (!itemList.contains(eq))
				{
					itemList.add(eq);
				}
			}
			for ( Equipment eq : itemList )
			{
				// For when the new BONUS'es are implmented
				/*
				 String qsString = "EQ:" + eq.getName();
				 maxBonus += aPC.getVariableValue(Globals.getNonProfPenaltyFormula(), eqString);
				 */
				if ((aCheck == ACHECK_YES)
					|| ((aCheck == ACHECK_NONPROF) && !aPC.isProficientWith(eq)))
				{
					maxBonus += eq.acCheck(aPC).intValue();
				}
				else if (aCheck == ACHECK_DOUBLE)
				{
					maxBonus += 2 * eq.acCheck(aPC).intValue();
				}
			}
		}
		maxBonus += (int) aPC.getTotalBonusTo("MISC", "ACCHECK");

		return Math.min(maxBonus, minBonus);
	}

    /**
     * Get the qualified name
     * @return qualified name
     */
	public String qualifiedName()
	{
		if (getAssociatedCount() == 0)
		{
			return displayName;
		}

		final StringBuffer buffer = new StringBuffer(getAssociatedCount() * 20);
		buffer.append(displayName).append("(");

		for (int i = 0; i < getAssociatedCount(); i++)
		{
			if (i > 0)
			{
				buffer.append(", ");
			}

			buffer.append(getAssociated(i));
		}

		buffer.append(")");

		return buffer.toString();
	}

	/**
	 * Is this skill a class skill for any class possessed by this character.
	 * @param aPC PlayerCharacter
	 * @return true if this is a class skill
	 */
	public boolean isClassSkill( final PlayerCharacter aPC )
	{
		return isClassSkill( aPC.getClassList(), aPC );
	}

	boolean isClassSkill(final List<PCClass> aList, final PlayerCharacter aPC)
	{
		for ( PCClass pcClass : aList )
		{
			if (isClassSkill(pcClass, aPC))
			{
				return true;
			}
		}

		return false;
	}

	boolean isRequired()
	{
		return required;
	}

    /**
     * Get the root name
     * @return root name
     */
	public String getRootName()
	{
		return rootName;
	}

	/**
	 * return of 0 means exclusive, 1=class-skill, 2=cross-class skill
	 * @param aPCClassList
	 * @param aPC
	 * @return cost for pcc class list
	 */
	public int costForPCClassList(final List<PCClass> aPCClassList, final PlayerCharacter aPC)
	{
		int anInt = Globals.getGameModeSkillCost_Exclusive(); // assume exclusive (can't buy)
		final int classListSize = aPCClassList.size();

		if (classListSize == 0)
		{
			return anInt;
		}

		for ( PCClass pcClass : aPCClassList )
		{
			final int cInt = costForPCClass(pcClass, aPC);

			if (cInt == Globals.getGameModeSkillCost_Class())
			{
				return cInt;
			}

			if (cInt != anInt)
			{
				anInt = cInt; // found a cross-class
			}
		}

		return anInt;
	}

	void replaceClassRank(final String oldClass, final String newClass)
	{
		final String oldClassString = oldClass + ":";

		for (int i = 0; i < rankList.size(); i++)
		{
			final String bSkill = rankList.get(i);

			if (bSkill.startsWith(oldClassString))
			{
				rankList.set(i, newClass + bSkill.substring(oldClass.length()));
			}
		}
	}

	private boolean isCrossClassSkill(final PCClass aClass, final PlayerCharacter aPC)
	{
		if (isClassSkill(aClass, aPC))
		{
			return false;
		}

		if ((aPC == null) || (aClass == null))
		{
			return false;
		}

		if (aPC.getRace().hasCcSkill(keyName))
		{
			return true;
		}

		for ( CharacterDomain aCD : aPC.getCharacterDomainList() )
		{
			if ((aCD.getDomain() != null) && aCD.isFromPCClass(aClass.getKeyName()) && aCD.getDomain().hasCcSkill(keyName))
			{
				return true;
			}
		}

		if ((aPC.getDeity() != null) && aPC.getDeity().hasCcSkill(keyName))
		{
			return true;
		}

		if (aClass.hasCcSkill(keyName))
		{
			return true;
		}

		if (aClass.isMonster())
		{
			if (aPC.getRace().hasMonsterCCSkill(keyName))
			{
				return true;
			}
		}

		for ( Ability feat : aPC.aggregateFeatList() )
		{
			if (feat.hasCcSkill(keyName))
			{
				return true;
			}
		}

		for ( Skill skill : aPC.getSkillList() )
		{
			if (skill.hasCcSkill(keyName))
			{
				return true;
			}
		}

		for ( Equipment eq : aPC.getEquipmentList() )
		{
			if (eq.isEquipped())
			{
				if (eq.hasCcSkill(keyName))
				{
					return true;
				}

				for ( EquipmentModifier eqMod : eq.getEqModifierList(true) )
				{
					if (eqMod.hasCcSkill(keyName))
					{
						return true;
					}
				}

				for ( EquipmentModifier eqMod : eq.getEqModifierList(false) )
				{
					if (eqMod.hasCcSkill(keyName))
					{
						return true;
					}
				}
			}
		}

		for ( PCTemplate template : aPC.getTemplateList() )
		{
			if (template.hasCcSkill(keyName))
			{
				return true;
			}
		}

		return false;
	}

	/** returns the adjustments to rank
	 * @param currentPC TODO
	 *
	 * @return the adjustments to rank
	 */
	private Float getRankAdj(final PlayerCharacter currentPC)
	{
		if (currentPC == null)
		{
			return Float.valueOf(0);
		}
		return new Float(getSkillRankBonusTo(currentPC));
	}

	private double modRanks2(double g, final int idx, String bSkill, final PlayerCharacter aPC)
	{
		final int iOffs = bSkill.indexOf(':');
		final double curRank = Double.parseDouble(bSkill.substring(iOffs + 1)); // current rank for currently selected class
		double newRank = curRank + g;

		if (!aPC.isImporting())
		{
			String choiceString = getChoiceString();
			if ((choiceString.length() > 0) && !CoreUtility.doublesEqual(g, 0)
				&& !CoreUtility.doublesEqual(curRank, (int) newRank))
			{
				final List<String> aArrayList = new ArrayList<String>();
				final double rankAdjustment = 0.0;
				String title = "";
				final StringTokenizer aTok = new StringTokenizer(choiceString, "|");

				if (aTok.hasMoreTokens())
				{
					title = aTok.nextToken();
				}

				if (choiceString.startsWith("Language"))
				{
					bSkill = bSkill.substring(0, iOffs + 1) + newRank;
					rankList.set(idx, bSkill);

					if (!chooseLanguageForSkill(aPC, this))
					{
						newRank = curRank;
					}
					else
					{
						final int selectedLanguages = getAssociatedCount();
						final int maxLanguages = getTotalRank(aPC).intValue();

						if (selectedLanguages > maxLanguages)
						{
							newRank = curRank;
						}
					}

					g = newRank - curRank;
				}
				else
				{
					// TODO This code doesn't seem to do anything real.
					// It passes empty lists as both available and selected.
					final ChooserInterface c = ChooserFactory.getChooserInstance();

					if (title.length() != 0)
					{
						c.setTitle(title);
					}

					c.setPool((int) (g + curRank + rankAdjustment) - getAssociatedCount());
					c.setPoolFlag(false);
					c.setAvailableList(aArrayList);

					final List<String> s = new ArrayList<String>();
					addAssociatedTo(s);
					c.setSelectedList(s);
					c.setVisible(true);

					final int selectedListSize = c.getSelectedList().size();
					newRank = selectedListSize - rankAdjustment;
					g = newRank - getRank().doubleValue(); // change in ranks
				}
			}
		}

		//
		// Modify for the chosen class
		//
		if (CoreUtility.doublesEqual(newRank, 0.0))
		{
			rankList.remove(idx);
		}
		else
		{
			bSkill = bSkill.substring(0, iOffs + 1) + newRank;
			rankList.set(idx, bSkill);
		}

		aPC.calcActiveBonuses();

		return g;
	}

	/**
	 * Builds up a string describing what makes up the misc modifier for
	 * a skill for a character. This can either be in long form
	 * '+2[skill TUMBLE gteq 5|TYPE=SYNERGY.STACK]' or in short form
	 * '+2[TUMBLE]'. Any modifiers that cannot be determined will be
	 * displayed as a single entry of 'OTHER'.
	 *
	 * @param aPC The character associated with this skill.
	 * @param shortForm True if the abbreviated form should be used.
	 * @return The explaination fo the misc modifier's make-up.
	 */
	public String getModifierExplanation(final PlayerCharacter aPC, final boolean shortForm)
	{
		double bonusObjTotal = 0.0;
		final StringBuffer bonusDetails = new StringBuffer();
		for ( BonusObj bonus : getBonusList() )
		{
			final double bonusVal = bonus.getCalculatedValue(aPC);
			if (bonus.isApplied() && !CoreUtility.doublesEqual(bonusVal,0.0) && !"VAR".equals(bonus.getBonusName()))
			{
				if (bonusDetails.length() > 0)
				{
					bonusDetails.append(' ');
				}
				bonusDetails.append(bonus.getDescription(shortForm, aPC));
				bonusObjTotal += bonusVal;
			}
		}

		//TODO: Need to add other bonuses which are not encoded as bonus objects
		//       - familiars, racial, feats - and add them to bonusObjTotal

		double bonus;
		if (SettingsHandler.getGame().getStatFromAbbrev(keyStat) >= 0)
		{
			bonus = aPC.getStatList().getStatModFor(keyStat);
			bonus += aPC.getTotalBonusTo("SKILL", "STAT." + keyStat);
			appendBonusDesc(bonusDetails, bonus, "STAT");
		}

		// The catch-all for non-bonusObj modifiers.
		bonus = aPC.getTotalBonusTo("SKILL", keyName) - bonusObjTotal;
		appendBonusDesc(bonusDetails, bonus, "OTHER");

		// loop through all current skill types checking for boni
		for (String singleType : getTypeList(false))
		{
			bonus = aPC.getTotalBonusTo("SKILL", "TYPE." + singleType);
			appendBonusDesc(bonusDetails, bonus, "TYPE." + singleType);
		}

		// now check for any lists of skills, etc
		bonus = aPC.getTotalBonusTo("SKILL", "LIST");
		appendBonusDesc(bonusDetails, bonus, "LIST");

		// now check for ALL
		bonus = aPC.getTotalBonusTo("SKILL", "ALL");
		appendBonusDesc(bonusDetails, bonus, "ALL");

		//these next two if-blocks try to get BONUS:[C]CSKILL|TYPE=xxx|y to function
		if (isClassSkill(aPC.getClassList(), aPC))
		{
			bonus = aPC.getTotalBonusTo("CSKILL", keyName);
			appendBonusDesc(bonusDetails, bonus, "CSKILL");

			//loop through all current skill types checking for boni
			for (String singleType : getTypeList(false))
			{
				bonus = aPC.getTotalBonusTo("CSKILL", "TYPE." + singleType);
				appendBonusDesc(bonusDetails, bonus, "CSKILL");
			}

			bonus = aPC.getTotalBonusTo("CSKILL", "LIST");
			appendBonusDesc(bonusDetails, bonus, "CSKILL");
		}

		if (!isClassSkill(aPC.getClassList(), aPC) && !isExclusive())
		{
			bonus = aPC.getTotalBonusTo("CCSKILL", keyName);
			appendBonusDesc(bonusDetails, bonus, "CCSKILL");

			//loop through all current skill types checking for boni
			for (String singleType : getTypeList(false))
			{
				bonus = aPC.getTotalBonusTo("CCSKILL", "TYPE." + singleType);
				appendBonusDesc(bonusDetails, bonus, "CCSKILL");
			}

			bonus = aPC.getTotalBonusTo("CCSKILL", "LIST");
			appendBonusDesc(bonusDetails, bonus, "CCSKILL");
		}

		// Race
		bonus = aPC.getRace().bonusForSkill(getKeyName());
		appendBonusDesc(bonusDetails, bonus, "RACE");

		// Encumbrance
		final int aCheckMod = calcACheckBonus(aPC);
		appendBonusDesc(bonusDetails, aCheckMod, "ARMOR");

		String aString = Globals.getGameModeRankModFormula();
		if (aString.length() != 0)
		{
			aString = CoreUtility.replaceAll(aString, "$$RANK$$", getTotalRank(aPC).toString());
			bonus = aPC.getVariableValue(aString, "").intValue();
			appendBonusDesc(bonusDetails, bonus, "RANKS");
		}

		return bonusDetails.toString();
	}

	/**
	 * Append a description of the bonus to the supplied buffer if
	 * the bonus value is not 0.
	 *
	 * @param bonusDetails The StringBuffer being built up. NB: May be modified.
	 * @param bonus The value of the bonus.
	 * @param description The description of the bonus.
	 */
	private void appendBonusDesc(final StringBuffer bonusDetails, final double bonus,
		final String description)
	{
		if (CoreUtility.doublesEqual(bonus,0.0))
		{
			return;
		}

		if (bonusDetails.length() > 0)
		{
			bonusDetails.append(' ');
		}
		String value = Delta.toString((float)bonus);
		if (value.endsWith(".0"))
		{
			value = value.substring(0, value.length()-2);
		}
		bonusDetails.append(value);
		bonusDetails.append('[').append(description).append(']');
	}

    /**
     * Get the bonus to a skill rank
     * @param aPC
     * @return bonus to skill rank
     */
	public double getSkillRankBonusTo(PlayerCharacter aPC)
	{
		double bonus = aPC.getTotalBonusTo("SKILLRANK", getKeyName());
		for (String singleType : getTypeList(false))
		{
			bonus += aPC.getTotalBonusTo("SKILLRANK", "TYPE." + singleType);
		}

		updateAdds(aPC, bonus);

		return bonus;
	}

	private void updateAdds(PlayerCharacter aPC, double bonus)
	{
		// Check for ADDs
		List<LevelAbility> laList = getLevelAbilityList();
		if (laList != null)
		{
			int iCount = 0;
			for ( LevelAbility la : laList )
			{
				iCount += la.getAssociatedCount();
			}

			if (CoreUtility.doublesEqual(getRank().doubleValue() + bonus, 0.0))
			{
				//
				// There was a total (because we've applied the ADD's, but now there isn't.
				// Need to remove the ADDed items
				//
				if (iCount != 0)
				{
					subAddsForLevel(-9, aPC);
				}
			}
			else
			{
				//
				// There wasn't a total (because we haven't applied the ADDs), but now there is
				// Need to apply the ADDed items
				//
				if (iCount == 0)
				{
					addAddsForLevel(-9, aPC, null);
				}
			}
		}
	}


	//
	// Overrides PObject.globalChecks
	//
	protected void globalChecks(final boolean flag, final PlayerCharacter aPC)
	{
		aPC.setArmorProfListStable(false);
		List<String> l = getSafeListFor(ListKey.KITS);
		for (int i = 0; i > l.size(); i++)
		{
			KitUtilities.makeKitSelections(0, l.get(i), i, aPC);
		}
		makeRegionSelection(aPC);

		if (flag)
		{
			makeChoices(aPC);
		}
		activateBonuses(aPC);
	}



	/**
	 * Get the key attribute's description
     * @return description
	 */
	public String getKeyStatFromStats()
	{
		String aKeyStat = getKeyStat();
		if ((aKeyStat.length() == 0) && Globals.getGameModeHasPointPool())
		{
			List<PCStat> statList = getKeyStatList(null);
			for (int i = 0; i < statList.size(); ++i)
			{
				PCStat stat = statList.get(i);
				if (aKeyStat.length() != 0)
				{
					aKeyStat += '/';
				}
				aKeyStat += stat.getAbb();
			}
		}
		return aKeyStat;
	}

	/**
	 * Get the modifier to the skill granted by the key attribute
	 * @param pc 
	 * @return modifier 
	 */
	public int getStatMod(final PlayerCharacter pc)
	{
		String myKeyStat = getKeyStat();
		if (myKeyStat.length() != 0)
		{
			return pc.getStatList().getStatModFor(myKeyStat);
		}

		int statMod = 0;
		if (Globals.getGameModeHasPointPool())
		{
			ArrayList<String> typeList = new ArrayList<String>();
			getKeyStatList(typeList);
			for (int i = 0; i < typeList.size(); ++i)
			{
				statMod += pc.getTotalBonusTo("SKILL", "TYPE." + typeList.get(i));
			}
		}
		return statMod;
	}

	/**
	 * Get a list of PCStat's that apply a SKILL bonus to this skill.
	 * Generates (optionally, if typeList is non-null) a list of String's types
     * 
	 * @param typeList 
	 * @return List of stats that apply
	 */
	public List<PCStat> getKeyStatList(List<String> typeList)
	{
		List<PCStat> aList = new ArrayList<PCStat>();
		if (Globals.getGameModeHasPointPool())
		{
			for (String aType : this.getTypeList(false))
			{
				List<PCStat> statList = SettingsHandler.getGame().getUnmodifiableStatList();
				for (int idx = statList.size() - 1; idx >= 0; --idx)
				{
					final PCStat stat = statList.get(idx);
					//
					// Get a list of all BONUS:SKILL|TYPE.<type>|x for this skill that would come from current stat
					//
					List<BonusObj> bonusList = getBonusListOfType(stat, Bonus.getBonusTypeFromName("SKILL"), "TYPE." + aType);
					if (bonusList.size() > 0)
					{
						for(int iCount = bonusList.size() - 1; iCount >= 0; --iCount)
						{
							aList.add(stat);
						}
						if ((typeList != null) && !typeList.contains(aType))
						{
							typeList.add(aType);
						}
					}
				}
			}
		}
		return aList;
	}

	//
	// Get a list of all BonusObj's from passed stat that apply a bonus of the passed type and name
	//
	private List<BonusObj> getBonusListOfType(final PCStat aStat, final int iType, final String aName)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for ( BonusObj bonus : aStat.getBonusList() )
		{
			if (bonus.getTypeOfBonusAsInt() != iType)
			{
				continue;
			}

			if (bonus.getBonusInfoList().size() > 1)
			{
				final StringTokenizer aTok = new StringTokenizer(bonus.getBonusInfo(), ",");

				while (aTok.hasMoreTokens())
				{
					final String aBI = aTok.nextToken();

					if (aBI.equalsIgnoreCase(aName))
					{
						aList.add(bonus);
					}
				}
			}
			else if (bonus.getBonusInfo().equalsIgnoreCase(aName))
			{
				aList.add(bonus);
			}
		}

		return aList;
	}

	boolean isTypeHidden(final String type)
	{
		return Globals.isSkillTypeHidden(type);
	}

}
