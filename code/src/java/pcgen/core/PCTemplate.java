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
 * Current Ver: $Revision: 1.233 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006/01/29 00:02:14 $
 *
 */
package pcgen.core;

import pcgen.core.levelability.LevelAbility;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.util.PropertyFactory;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

import java.util.*;

/**
 * <code>PCTemplate</code>.
 *
 * @author Mark Hulsman <hulsmanm@purdue.edu>
 * @version $Revision: 1.233 $
 */
public final class PCTemplate extends PObject implements HasCost
{
	///////////////////////////////////////////////////////////////////////
	// Static properties
	///////////////////////////////////////////////////////////////////////
	/** Visibility is Hidden */
	public static final int VISIBILITY_HIDDEN = 0;
	/** Visibility is Default */
	public static final int VISIBILITY_DEFAULT = 1;
	/** Visibility is Output Sheets Only */
	public static final int VISIBILITY_OUTPUT_ONLY = 2;
	/** Visibility is GUI Only */
	public static final int VISIBILITY_DISPLAY_ONLY = 3;

	private ArrayList featStrings = null;
	private ArrayList hitDiceStrings = null;
	private ArrayList levelStrings = null;
	private ArrayList templates = new ArrayList();

//	private ArrayList sizeStrings = null;			// never populated--removing Byngl Oct 23,2002
	private ArrayList weaponProfBonus = null;
	private HashMap chosenFeatStrings = null;
	private List templatesAdded = null;
	private String ageString = Constants.s_NONE;
	private String chooseLanguageAutos = "";
	private String cost = "1";

//	private String kit = "";
	private String favoredClass = "";

	// If set these two will override any other choices.
	private String gender = Constants.s_NONE;
	private String handed = Constants.s_NONE;
	private String heightString = Constants.s_NONE;
	private String levelAdjustment = "0"; //now a string so that we can handle formulae
	private String region = Constants.s_NONE;
	private String subRace = Constants.s_NONE;
	private String subregion = Constants.s_NONE;
	private String templateSize = "";
	private String weightString = Constants.s_NONE;
	private TreeSet languageBonus = new TreeSet();
	private boolean removable = true;
	private int CR = 0;
	private int bonusInitialFeats = 0;
	private int bonusSkillsPerLevel = 0;
	private String hitDieLock = "";
	private int levelsPerFeat = 3;
	private int nonProficiencyPenalty = 1;
	private int templateVisible = VISIBILITY_DEFAULT;
	private String raceType = "";

	private ArrayList addedSubTypes = new ArrayList();
	private ArrayList removedSubTypes = new ArrayList();

	public PCTemplate()
	{
		// Empty Constructor
	}

	public void setAgeString(final String argAgeString)
	{
		ageString = argAgeString;
	}

	public void setBonusInitialFeats(final int argBonusInitialFeats)
	{
		bonusInitialFeats = argBonusInitialFeats;
	}

	public int getBonusInitialFeats()
	{
		return bonusInitialFeats;
	}

	public void setBonusSkillsPerLevel(final int argBonusSkillsPerLevel)
	{
		bonusSkillsPerLevel = argBonusSkillsPerLevel;
	}

	public int getBonusSkillsPerLevel()
	{
		return bonusSkillsPerLevel;
	}

	public void setCR(final int argCR)
	{
		CR = argCR;
	}

	public int getCR(final int level, final int hitdice)
	{
		int _CR = CR;

		for (int x = 0; x < getListSize(levelStrings); ++x)
		{
			if (contains(levelStrings.get(x).toString(), "CR:") && doesLevelQualify(level, x))
			{
				_CR += Integer.parseInt(getStringAfter("CR:", levelStrings.get(x).toString()));
			}
		}

		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x).toString(), "CR:") && doesHitDiceQualify(hitdice, x))
			{
				_CR += Integer.parseInt(getStringAfter("CR:", hitDiceStrings.get(x).toString()));
			}
		}

		return _CR;
	}

	public void setChooseLanguageAutos(final String argChooseLanguageAutos)
	{
		chooseLanguageAutos = argChooseLanguageAutos;
	}

	public String getChooseLanguageAutos()
	{
		return chooseLanguageAutos;
	}

	public HashMap getChosenFeatStrings()
	{
		return chosenFeatStrings;
	}

	public void setCost(final String argCost)
	{
		cost = argCost;
	}

	public double getCost()
	{
		return Double.parseDouble(cost);
	}

	public String getDR(final int level, final int hitdice)
	{
		final StringBuffer drString = new StringBuffer();
		boolean isEmpty = true;

		if (getDR() != null)
		{
			drString.append(getDR().trim());
			isEmpty = false;
		}

		int x;

		for (x = 0; x < getListSize(levelStrings); ++x)
		{
			if (contains(levelStrings.get(x).toString(), "DR:") && doesLevelQualify(level, x))
			{
				if (!isEmpty)
				{
					drString.append('|');
				}
				drString.append(getStringAfter("DR:", levelStrings.get(x).toString()));
				isEmpty = false;
			}
		}

		for (x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x).toString(), "DR:") && doesHitDiceQualify(hitdice, x))
			{
				if (!isEmpty)
				{
					drString.append('|');
				}
				drString.append(getStringAfter("DR:", hitDiceStrings.get(x).toString()));
				isEmpty = false;
			}
		}

		return drString.toString();
	}

	public void setFavoredClass(final String newClass)
	{
		favoredClass = newClass;
	}

	public String getFavoredClass()
	{
		return favoredClass;
	}

	/**
	 * <code>setGenderLock</code> locks gender to appropriate PropertyFactory setting if String matches 'Male','Female', or 'Neuter'.
	 *
	 * author arcady <arcady@users.sourceforge.net>
	 * @param genderString
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

	public String getGenderLock()
	{
		return gender;
	}

	public void setHandedLock(final String handedString)
	{
		handed = handedString;
	}

	public void setHeightString(final String argHeightString)
	{
		heightString = argHeightString;
	}

	/*public void setHitDiceSize(int argHitDiceSize)
	{
		hitDiceSize = argHitDiceSize;
	}*/

	public void setHitDieLock(final String hitDieLock)
	{
		this.hitDieLock = hitDieLock;
	}

	protected String getHitDieLock()
	{
		return hitDieLock;
	}

	public ArrayList getHitDiceStrings()
	{
		if (hitDiceStrings == null)
		{
			hitDiceStrings = new ArrayList();
		}

		return hitDiceStrings;
	}

	/**
	 * Identical function exists in PCClass.java. Refactor. XXX
	 * @param aString
	 */
	public void setLanguageBonus(final String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);

		while (aTok.hasMoreTokens())
		{
			final String token = aTok.nextToken();

			if (".CLEAR".equals(token))
			{
				getLanguageBonus().clear();
			}
			else
			{
				final Language aLang = Globals.getLanguageNamed(token);

				if (aLang != null)
				{
					getLanguageBonus().add(aLang);
				}
			}
		}
	}

	public Set getLanguageBonus()
	{
		return languageBonus;
	}

	public void setLevelAdjustment(final String argLevelAdjustment)
	{
		levelAdjustment = argLevelAdjustment;
	}

	public int getLevelAdjustment(final PlayerCharacter aPC)
	{
		int lvlAdjust;

		//if there's a current PC, go ahead and evaluate the formula
		if (aPC != null)
		{
			return aPC.getVariableValue(levelAdjustment, "").intValue();
		}

		//otherwise do what we can
		try
		{
			//try to convert the string to an int to return
			lvlAdjust = Integer.parseInt(levelAdjustment);
		}
		catch (NumberFormatException nfe)
		{
			//if the parseInt failed then just punt... return 0
			lvlAdjust = 0;
		}

		return lvlAdjust;
	}

	public String getLevelAdjustmentFormula()
	{
		return levelAdjustment;
	}

	public ArrayList getLevelStrings()
	{
		if (levelStrings == null)
		{
			levelStrings = new ArrayList();
		}

		return levelStrings;
	}

	public void setLevelsPerFeat(final int argLevelsPerFeat)
	{
		levelsPerFeat = argLevelsPerFeat;
	}

	public boolean isNonAbility(final int statIdx)
	{
		final List statList = SettingsHandler.getGame().getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}

		final String aStat = "|LOCK." + ((PCStat) statList.get(statIdx)).getAbb() + "|10";

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

	public void setNonProficiencyPenalty(final int npp)
	{
		nonProficiencyPenalty = npp;
	}

	/**
	 * <br>author: arcady June 4, 2002
	 *
	 * @return nonProficiencyPenalty
	 */
	public int getNonProficiencyPenalty()
	{
		return nonProficiencyPenalty;
	}

	/**
	 * Produce a tailored PCC output, used for saving custom templates.
	 * @return PCC Text
	 */
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getName());

		if (!Constants.s_NONE.equals(ageString))
		{
			txt.append("\tAGE:").append(ageString);
		}

		if (bonusInitialFeats != 0)
		{
			txt.append("\tBONUSFEATS:").append(bonusInitialFeats);
		}

		if (bonusSkillsPerLevel != 0)
		{
			txt.append("\tBONUSSKILLPOINTS:").append(bonusSkillsPerLevel);
		}

		if ((chooseLanguageAutos != null) && (chooseLanguageAutos.length() > 0))
		{
			txt.append("\tCHOOSE:LANGAUTO:").append(chooseLanguageAutos);
		}

		if (!CoreUtility.doublesEqual(getCost(), 1.0d))
		{
			txt.append("\tCOST:").append(String.valueOf(getCost()));
		}

		if (CR != 0)
		{
			txt.append("\tCR:").append(CR);
		}

		if ((favoredClass != null) && (favoredClass.length() > 0))
		{
			txt.append("\tFAVOREDCLASS:").append(favoredClass);
		}

		if (getListSize(featStrings) > 0)
		{
			final StringBuffer buffer = new StringBuffer();

			for (Iterator e = featStrings.iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}

				buffer.append((String) e.next());
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
			for (Iterator e = hitDiceStrings.iterator(); e.hasNext();)
			{
				txt.append("\tHD:").append((String) e.next());
			}
		}

		if (!Constants.s_NONE.equals(heightString))
		{
			txt.append("\tHEIGHT:").append(heightString);
		}

		if(!hitDieLock.equals(""))
		{
			txt.append("\tHITDIE:").append(hitDieLock);
		}

		if ((languageBonus != null) && !languageBonus.isEmpty())
		{
			final StringBuffer buffer = new StringBuffer();

			for (Iterator e = languageBonus.iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append(',');
				}

				buffer.append((String) e.next());
			}

			txt.append("\tLANGBONUS:").append(buffer.toString());
		}

		if (getListSize(levelStrings) > 0)
		{
			for (Iterator e = levelStrings.iterator(); e.hasNext();)
			{
				txt.append("\tLEVEL:").append((String) e.next());
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

		if (!Constants.s_NONE.equals(weightString))
		{
			txt.append("\tWEIGHT:").append(weightString);
		}

		if (!"alwaysValid".equals(getQualifyString()))
		{
			txt.append("\tQUALIFY:").append(getQualifyString());
		}

		if (!Constants.s_NONE.equals(region))
		{
			txt.append("\tREGION:");

			if (region.equals(getName()))
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

			if (subRace.equals(getName()))
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

			if (subregion.equals(getName()))
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
			for (Iterator e = templates.iterator(); e.hasNext();)
			{
				txt.append("\tTEMPLATE:").append((String) e.next());
			}
		}

		switch (templateVisible)
		{
			case PCTemplate.VISIBILITY_DISPLAY_ONLY:
				txt.append("\tVISIBLE:DISPLAY");

				break;

			case PCTemplate.VISIBILITY_OUTPUT_ONLY:
				txt.append("\tVISIBLE:EXPORT");

				break;

			case PCTemplate.VISIBILITY_HIDDEN:
				txt.append("\tVISIBLE:NO");

				break;

			default:
				txt.append("\tVISIBLE:YES");

				break;
		}

		if (getListSize(weaponProfBonus) > 0)
		{
			final StringBuffer buffer = new StringBuffer();

			for (Iterator e = weaponProfBonus.iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}

				buffer.append((String) e.next());
			}

			txt.append("\tWEAPONBONUS:").append(buffer.toString());
		}

		txt.append(super.getPCCText(false));

		return txt.toString();
	}

	public boolean isQualified(final PlayerCharacter aPC)
	{
		if (aPC == null)
		{
			return false;
		}

		return PrereqHandler.passesAll( getPreReqList(), aPC, this );
	}

	public String getRaceType()
	{
		return raceType;
	}

	public void setRaceType(final String aType)
	{
		raceType = aType;
	}

	public void setRegion(final String argRegion)
	{
		region = argRegion;
	}

	public String getRegion()
	{
		return region;
	}

	public void setRemovable(final boolean argRemovable)
	{
		removable = argRemovable;
	}

	public boolean isRemovable()
	{
		boolean result = false;

		if ((templateVisible == VISIBILITY_DEFAULT) || (templateVisible == VISIBILITY_DISPLAY_ONLY))
		{
			result = removable;
		}

		return result;
	}

	public int getSR(final int level, final int hitdice, final PlayerCharacter aPC)
	{
		int aSR = getSR(aPC);

		for (int x = 0; x < getListSize(levelStrings); ++x)
		{
			if (contains(levelStrings.get(x).toString(), "SR:") && doesLevelQualify(level, x))
			{
				aSR = Math.max(Integer.parseInt(getStringAfter("SR:", levelStrings.get(x).toString())), aSR);
			}
		}

		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x).toString(), "SR:") && doesHitDiceQualify(hitdice, x))
			{
				aSR = Math.max(Integer.parseInt(getStringAfter("SR:", hitDiceStrings.get(x).toString())), aSR);
			}
		}

		return aSR;
	}

	public List getSpecialAbilityList(final int level, final int hitdice)
	{
		final List specialAbilityList = getListFor(ListKey.SPECIAL_ABILITY);

		if (specialAbilityList == null)
		{
			return specialAbilityList;
		}

		for (int x = 0; x < getListSize(levelStrings); ++x)
		{
			if (contains(levelStrings.get(x).toString(), "SA:") && doesLevelQualify(level, x))
			{
				final String saString = getStringAfter("SA:", levelStrings.get(x).toString());
				final SpecialAbility sa = new SpecialAbility(saString);
				specialAbilityList.add(sa);
			}
		}

		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x).toString(), "SA:") && doesHitDiceQualify(hitdice, x))
			{
				final String saString = getStringAfter("SA:", hitDiceStrings.get(x).toString());
				final SpecialAbility sa = new SpecialAbility(saString);
				specialAbilityList.add(sa);
			}
		}

		return specialAbilityList;
	}

	public void setSubRace(final String argSubRace)
	{
		subRace = argSubRace;
	}

	public String getSubRace()
	{
		return subRace;
	}

	public void setSubRegion(final String argSubregion)
	{
		subregion = argSubregion;
	}

	public String getSubRegion()
	{
		return subregion;
	}

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

	public List getAddedSubTypes()
	{
		return Collections.unmodifiableList(addedSubTypes);
	}

	public List getRemovedSubTypes()
	{
		return Collections.unmodifiableList(removedSubTypes);
	}

	/**
	 * Method getTemplateList. Returns an array list containing the raw
	 * templates granted by this template. This includes CHOOSE: strings
	 * which list templates a user will be asked to choose from.
	 *
	 * @return ArrayList of granted templates
	 */
	public List getTemplateList()
	{
		return templates;
	}

	public void setTemplateSize(final String argSize)
	{
		templateSize = argSize;
	}

	public String getTemplateSize()
	{
		return templateSize;
	}

	public void setVisible(final int argTemplateVisible)
	{
		templateVisible = argTemplateVisible;
	}

	//
	// This was never called. New functionality has been added to PObject that will
	// supercede this anyways.
	// -Byngl Oct 23, 2002
	//
	//public String getKit()
	//{
	//	return kit;
	//}
	//public void setKit(String argKit)
	//{
	//	kit = argKit;
	//}
	public int isVisible()
	{
		return templateVisible;
	}

	public void setWeaponProfBonus(final String aString)
	{
		if (weaponProfBonus == null)
		{
			weaponProfBonus = new ArrayList();
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);

		while (aTok.hasMoreTokens())
		{
			weaponProfBonus.add(aTok.nextToken());
		}
	}

	public ArrayList getWeaponProfBonus()
	{
		if (weaponProfBonus == null)
		{
			return new ArrayList();
		}

		return weaponProfBonus;
	}

	public int getWeaponProfBonusSize()
	{
		return getListSize(weaponProfBonus);
	}

	public void setWeightString(final String argWeightString)
	{
		weightString = argWeightString;
	}

	public void addChosenFeat(final String mapKey, final String mapValue)
	{
		if (chosenFeatStrings == null)
		{
			chosenFeatStrings = new HashMap();
		}

		chosenFeatStrings.put(mapKey, mapValue);
	}

	public void addFeatString(final String featString)
	{
		if (".CLEAR".equals(featString))
		{
			if (featStrings != null)
			{
				featStrings.clear();
			}

			return;
		}

		final StringTokenizer aTok = new StringTokenizer(featString, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String fs = aTok.nextToken();

			if (featStrings == null)
			{
				featStrings = new ArrayList();
			}

			featStrings.add(fs);
		}
	}

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

		if (hitDiceStrings == null)
		{
			hitDiceStrings = new ArrayList();
		}

		hitDiceStrings.add(hitDiceString);
	}

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

		if (levelStrings == null)
		{
			levelStrings = new ArrayList();
		}

		levelStrings.add(levelString);
	}

	/**
	 * @param templateList	A string containing a pipe-delimited list of templates to add
	 */
	public void addTemplate(final String templateList)
	{
		if(templateList.startsWith("CHOOSE:")) {
			templates.add(templateList);
		}
		else {
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
					for(int i = 0; i < templates.size(); ++i)
					{
						if (templateName.equalsIgnoreCase((String) templates.get(i)))
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
						String aString = (String) templates.get(i);
		
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

	public Object clone() throws CloneNotSupportedException
	{
		final PCTemplate aTemp = (PCTemplate) super.clone();
		aTemp.templateVisible = templateVisible;
		aTemp.templates = (ArrayList) templates.clone();
		aTemp.languageBonus = (TreeSet) languageBonus.clone();

		if (getListSize(levelStrings) != 0)
		{
			aTemp.levelStrings = (ArrayList) levelStrings.clone();
		}

		if (getListSize(hitDiceStrings) != 0)
		{
			aTemp.hitDiceStrings = (ArrayList) hitDiceStrings.clone();
		}

		//if (getArrayListSize(sizeStrings) != 0)
		//{
		//	aTemp.sizeStrings = (ArrayList) sizeStrings.clone();
		//}
		if (getListSize(weaponProfBonus) != 0)
		{
			aTemp.weaponProfBonus = (ArrayList) weaponProfBonus.clone();
		}

		if (getListSize(featStrings) != 0)
		{
			aTemp.featStrings = (ArrayList) featStrings.clone();
		}

		if (chosenFeatStrings != null)
		{
			aTemp.chosenFeatStrings = (HashMap) chosenFeatStrings.clone();
		}

		return aTemp;
	}

	public List feats(final int level, final int hitdice, final PlayerCharacter aPC, final boolean addNew)
	{
		final List feats;

		if (getListSize(featStrings) != 0)
		{
			feats = (ArrayList) featStrings.clone();
		}
		else
		{
			feats = new ArrayList();
		}

		// arknight modified this back in 1.27 with the comment: Added support for Spycraft Game Mode
		// we no longer support Spycraft (at this time), and this breaks other modes, so I've reverting back to
		// the old method. I am also fixing a bug in the code I'm commenting out. levelStrings is used in the 2nd loop instead of hitDiceStrings.
		// - Byngl Sept 25, 2003
		//
		// Scrap all that. I'm using a HashMap to save those feats that have been taken when the required level/hitdie has been met.
		// We need to do this so that removing the template will also remove the selected feat(s).
		// PCTemplate instances will also need to be cloned() when adding them to PlayerCharacter.
		//
		if (chosenFeatStrings != null)
		{
			feats.addAll(chosenFeatStrings.values());
		}

		for (int x = 0; x < getListSize(levelStrings); ++x)
		{
			final String featKey = "L" + Integer.toString(x);
			String featName = null;

			if (chosenFeatStrings != null)
			{
				featName = (String) chosenFeatStrings.get(featKey);
			}

			if (featName == null && addNew)
			{
				if (doesLevelQualify(level, x))
				{
					getLevelFeat(levelStrings.get(x).toString(), level, featKey, aPC);
				}
			}
		}

		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			final String featKey = "H" + Integer.toString(x);
			String featName = null;

			if (chosenFeatStrings != null)
			{
				featName = (String) chosenFeatStrings.get(featKey);
			}

			if (featName == null && addNew)
			{
				if (doesHitDiceQualify(hitdice, x))
				{
					getLevelFeat(hitDiceStrings.get(x).toString(), -1, featKey, aPC);
				}
			}
		}

		return feats;
	}

	public String modifierString(final PlayerCharacter aPC)
	{
		final StringBuffer mods = new StringBuffer(50); //More likely to be true than 16 (the default)

		for (int x = 0; x < SettingsHandler.getGame().getUnmodifiableStatList().size(); ++x)
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

		if (!hitDieLock.equals("")) {
			mods.append("HITDIE:" + hitDieLock);
		}

		if (aPC == null)
		{
			if (CR != 0)
			{
				mods.append("CR:").append(CR).append(' ');
			}

			final int x = getSR(aPC);

			if (x != 0)
			{
				mods.append("SR:").append(x).append(' ');
			}

			if ((getDR() != null) && !"".equals(getDR()))
			{
				mods.append("DR:").append(getDR()).append(' ');
			}

			return mods.toString();
		}

		final int nat = (int) bonusTo("COMBAT", "AC", aPC, aPC);

		if (nat != 0)
		{
			mods.append("AC BONUS:").append(nat);
		}

		if (getCR(aPC.getTotalLevels(), aPC.totalHitDice()) != 0)
		{
			mods.append("CR:").append(getCR(aPC.getTotalLevels(), aPC.totalHitDice())).append(' ');
		}

		if (getSR(aPC.getTotalLevels(), aPC.totalHitDice(), aPC) != 0)
		{
			mods.append("SR:").append(getSR(aPC.getTotalLevels(), aPC.totalHitDice(), aPC)).append(' ');
		}

		if (!getDR(aPC.getTotalLevels(), aPC.totalHitDice()).equals(""))
		{
			mods.append("DR:").append(getDR(aPC.getTotalLevels(), aPC.totalHitDice())).append(' ');
		}

		return mods.toString();
	}

	List getTemplates(final boolean isImporting, final PlayerCharacter aPC)
	{
		final List newTemplates = new ArrayList();
		templatesAdded = new ArrayList();

		if (!isImporting)
		{
			for (Iterator e = templates.iterator(); e.hasNext();)
			{
				String templateName = (String) e.next();

				if (templateName.startsWith("CHOOSE:"))
				{
					templateName = chooseTemplate(templateName.substring(7), true, aPC);
				}

				if (templateName.length() != 0)
				{
					newTemplates.add(templateName);
					templatesAdded.add(templateName);
				}
			}
		}

		return newTemplates;
	}

	List addSpecialAbilitiesToList(final List aList, final int level, final int hitdice)
	{
		/*
		 * CONSIDER Is this really proper behavior?!?  If the PObject has anything,
		 * then do the detailed work?  That doesn't terribly make sense to me - TRP
		 */
		if (containsListFor(ListKey.SPECIAL_ABILITY))
		{
			aList.addAll(getSpecialAbilityList(level, hitdice));
		}

		return aList;
	}

	/** Adds one chosen language.
	 * TODO: Identical method in Race.java. Refactor. XXX
	 * @param flag
	 * @param aPC
	 */
	void chooseLanguageAutos(final boolean flag, final PlayerCharacter aPC)
	{
		if (!flag && !"".equals(chooseLanguageAutos))
		{
			final StringTokenizer tokens = new StringTokenizer(chooseLanguageAutos, "|", false);
			final List selectedList; // selected list of choices

			final ChooserInterface c = ChooserFactory.getChooserInstance();
			c.setPool(1);
			c.setPoolFlag(false);
			c.setTitle("Pick a Language: ");

			SortedSet list = new TreeSet();

			while (tokens.hasMoreTokens())
			{
				list.add(tokens.nextToken());
			}

			list = Globals.extractLanguageListNames(list);
			c.setAvailableList(new ArrayList(list));
			c.setVisible(true);
			selectedList = c.getSelectedList();

			if ((selectedList != null) && (selectedList.size() != 0))
			{
				aPC.addFreeLanguage((String) selectedList.get(0));
			}
		}
	}

	/*
	 * Returns:
	 *    null for no choice made
	 *    "" for no choice available
	 *    templateName of chosen template
	 */
	static String chooseTemplate(final String templateList, final PlayerCharacter aPC)
	{
		return chooseTemplate(templateList, false, aPC);
	}

	static String chooseTemplate(final String templateList, final boolean forceChoice, final PlayerCharacter aPC)
	{
		final List choiceTemplates = CoreUtility.split(templateList, '|');

		for (int i = choiceTemplates.size() - 1; i >= 0; i--)
		{
			final String templateName = (String) choiceTemplates.get(i);
			final PCTemplate template = Globals.getTemplateNamed(templateName);

			if ((template == null) || !PrereqHandler.passesAll( template.getPreReqList(), aPC, template ) )
			{
				choiceTemplates.remove(i);
			}
		}

		//
		// If only 1 choice, use it without asking
		//
		if (choiceTemplates.size() == 1)
		{
			return (String) choiceTemplates.get(0);
		}
		else if (choiceTemplates.size() > 0)
		{
			return Globals.chooseFromList("Template Choice", choiceTemplates, null, 1, forceChoice);
		}

		return "";
	}

	public void addTemplateName(final String templateName)
	{
		if (templatesAdded == null)
		{
			templatesAdded = new ArrayList();
		}
		templatesAdded.add(templateName);
	}

	public List templatesAdded()
	{
		if (templatesAdded == null)
		{
			return new ArrayList();
		}

		return templatesAdded;
	}

	private static int getListSize(final List al)
	{
		int result = 0;

		if (al != null)
		{
			result = al.size();
		}

		return result;
	}

	private static String getStringAfter(final String stuff, final String string)
	{
		final int index = string.indexOf(stuff) + stuff.length();

		return string.substring(index);

		/*String string = (String) object;
		   while (string.length() >= stuff.length() && !string.startsWith(stuff))
			   string = string.substring(1);
		   if (string.length() <= stuff.length())
			   return "";
		   return string.substring(stuff.length());*/
	}

	private static boolean contains(final String string, final String stuff)
	{
		return string.indexOf(stuff) > -1;
	}

	private static String statName(final int x)
	{
		return SettingsHandler.getGame().s_ATTRIBSHORT[x];
	}

	private void getLevelFeat(final String levelString, final int lvl, final String featKey, final PlayerCharacter aPC)
	{
		if (contains(levelString, "FEAT:"))
		{
			String featName = getStringAfter("FEAT:", levelString);

			while (true)
			{
				ArrayList featList = new ArrayList();
				final LevelAbility la = LevelAbility.createAbility(this, lvl, "FEAT(" + featName + ")");
				la.process(featList, aPC, null);

				switch (featList.size())
				{
					case 1:
						featName = featList.get(0).toString();

						break;

					default:

						if (aPC != null && !aPC.isImporting())
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
								featName = featList.get(0).toString();

								continue;
							}
						}

						// fall-through intentional
					case 0:
						return;
				}

				break;
			}

			addChosenFeat(featKey, featName);
		}
	}

	private boolean doesHitDiceQualify(final int hitdice, final int x)
	{
		if (x >= getListSize(hitDiceStrings))
		{
			return false;
		}

		StringTokenizer tokens = new StringTokenizer((String) hitDiceStrings.get(x), ":");
		final String hitDiceString = tokens.nextToken();

		if (hitDiceString.endsWith("+"))
		{
			return Integer.parseInt(hitDiceString.substring(0, hitDiceString.length() - 1)) <= hitdice;
		}

		tokens = new StringTokenizer(hitDiceString, "-");

		return (hitdice >= Integer.parseInt(tokens.nextToken())) && (hitdice <= Integer.parseInt(tokens.nextToken()));
	}

	private boolean doesLevelQualify(final int level, final int x)
	{
		if (x >= getListSize(levelStrings))
		{
			return false;
		}

		final StringTokenizer stuff = new StringTokenizer((String) levelStrings.get(x), ":");

		return level >= Integer.parseInt(stuff.nextToken());
	}
}
