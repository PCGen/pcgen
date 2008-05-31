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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.SubRace;
import pcgen.cdom.enumeration.SubRegion;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.Visibility;

/**
 * <code>PCTemplate</code>.
 * 
 * @author Mark Hulsman <hulsmanm@purdue.edu>
 * @version $Revision$
 */
public final class PCTemplate extends PObject
{
	// /////////////////////////////////////////////////////////////////////
	// Static properties
	// /////////////////////////////////////////////////////////////////////

	private List<String> featStrings = null;
	private List<String> hitDiceStrings = null;
	private List<String> templates = new ArrayList<String>();

	private HashMap<String, String> chosenFeatStrings = null;

	/**
	 * A Map storing a Map of chosen Ability keys keyed on the level it is
	 * granted at with a prefix of L (level-based) or H (hitdice-based) keyed on
	 * AbilityCategory.
	 */
	private Map<AbilityCategory, Map<String, String>> theChosenAbilityKeys =
			null;

	private List<String> templatesAdded = null;

	private String favoredClass = "";

	/**
	 * A DoubleKeyMap storing abilities to be granted at a certain level. The
	 * Map uses level as a primary key and the ability type as the secondary
	 * key.
	 */
	private DoubleKeyMap<Integer, String, String> theLevelAbilities = null;

	/**
	 * Creates a new PCTemplate object.
	 */
	public PCTemplate()
	{
		// Empty Constructor
	}

	/**
	 * Get the total adjustment to Challenge rating of a character at a given
	 * level (Class and Hit Dice). This will include the absolute adjustment
	 * made with CR:, LEVEL:<num>:CR and HD:<num>:CR tags
	 * 
	 * @param level
	 *            The level to calculate the adjustment for
	 * @param hitdice
	 *            The Hit dice to calculate the adjustment for
	 * 
	 * @return a Challenge Rating adjustment
	 */
	public float getCR(final int level, final int hitdice)
	{
		float localCR = getSafe(ObjectKey.CR_MODIFIER).floatValue();

		if (theLevelAbilities != null)
		{
			for (int lvl = 0; lvl < level; lvl++)
			{
				final String crValue = theLevelAbilities.get(lvl, "CR");
				if (crValue != null)
				{
					localCR += Float.parseFloat(crValue);
				}
			}
		}

		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x), "CR:")
				&& doesHitDiceQualify(hitdice, x))
			{
				localCR +=
						Float.parseFloat(getStringAfter("CR:", hitDiceStrings
							.get(x)));
			}
		}

		return localCR;
	}

	/**
	 * Get a list of Feats chosen (from those potentially granted by this
	 * Template) by the Character it was applied to.
	 * 
	 * @return a hashmap of Feat names
	 */
	public HashMap<String, String> getChosenFeatStrings()
	{
		return chosenFeatStrings;
	}

	// TODO - This is rather gross. The class should not give out this sort of
	// internal information.
	public Map<String, String> getChosenAbilityKeys(
		final AbilityCategory aCategory)
	{
		if (theChosenAbilityKeys != null)
		{
			return theChosenAbilityKeys.get(aCategory);
		}
		return null;
	}

	/**
	 * Set the name of a favoured class to add to the Character this Template is
	 * applied to
	 * 
	 * @param newClass
	 *            the name of the class
	 */
	public void setFavoredClass(final String newClass)
	{
		favoredClass = newClass;
	}

	/**
	 * Get a string that is the name of a single favoured class to be added to
	 * the character this Template is applied to. Each Template can only add a
	 * single favoured class.
	 * 
	 * @return the name of the favoured class to add
	 */
	public String getFavoredClass()
	{
		return favoredClass;
	}

	/**
	 * Takes an integer input which it uses to access Games mode's "statlist"
	 * array. If that stat has been locked at 10 then it is considered a
	 * non-ability. 
	 * XXX This is insanely bad design, it's completely arse about
	 * face. What should have been done was find a way to mark a stat as a
	 * non-ability and then have the stat checking code interpret that as "no
	 * bonus or penalty - treat like it was locked at 10". Doing it this way
	 * means there is no way to actually lock a stat at 10. TODO: Fix this mess!
	 * disparaging comments Andrew Wilson 20060308
	 * 
	 * @param statIdx
	 *            index number of the stat in question
	 * 
	 * @return Whether this has been defined as a non-ability
	 */
	public boolean isNonAbility(final int statIdx)
	{
		final List<PCStat> statList =
				SettingsHandler.getGame().getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}

		// An unlock will always override a lock, so check it first
		if (isUnlocked(statIdx))
		{
			return false;
		}

		String aStat = "|LOCK." + statList.get(statIdx).getAbb() + "|10";
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
	 * Takes an integer input which it uses to access Games mode's "statlist"
	 * array. Test if that stat has been unlocked via a DEFINE|UNLOCK 
	 * 
	 * @param statIdx
	 *            index number of the stat in question
	 * 
	 * @return Whether this has been unlocked
	 */
	public boolean isUnlocked(final int statIdx)
	{
		final List<PCStat> statList =
				SettingsHandler.getGame().getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return false;
		}

		String aStat = "|UNLOCK." + statList.get(statIdx).getAbb() + "|";
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
	 * Produce a tailored PCC output, used for saving custom templates.
	 * 
	 * @return PCC Text
	 */
	@Override
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getDisplayName());
		txt.append("\t");
		txt.append(StringUtil.joinToStringBuffer(Globals.getContext().unparse(
				this), "\t"));
		txt.append("\t");

		if ((getChooseLanguageAutos() != null)
			&& (getChooseLanguageAutos().length() > 0))
		{
			txt.append("\tCHOOSE:LANGAUTO:").append(getChooseLanguageAutos());
		}

		if ((favoredClass != null) && (favoredClass.length() > 0))
		{
			txt.append("\tFAVOREDCLASS:").append(favoredClass);
		}

		if (getListSize(featStrings) > 0)
		{
			final StringBuffer buffer = new StringBuffer();

			for (String feat : featStrings)
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}

				buffer.append(feat);
			}

			txt.append("\tFEAT:").append(buffer.toString());
		}

		if (getListSize(hitDiceStrings) > 0)
		{
			for (String s : hitDiceStrings)
			{
				txt.append("\tHD:").append(s);
			}
		}

		final List<String> las = getLevelAbilities();
		for (final String la : las)
		{
			txt.append("\t").append(la);
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

		txt.append(super.getPCCText(false));

		return txt.toString();
	}

	/**
	 * Answers the question does the PC I've passed in meet the prerequisites of
	 * this template.
	 * 
	 * @param aPC
	 *            The PC that we're asking the question about
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
	 * Get the override that this template applies to subracetype
	 * 
	 * @return The new subracetype
	 */
	public String getSubRace()
	{
		SubRace sr = get(ObjectKey.SUBRACE);
		if (sr == null)
		{
			if (getSafe(ObjectKey.USETEMPLATENAMEFORSUBRACE))
			{
				return this.getDisplayName();
			}
			return "None";
		}
		return sr.toString();
	}

	/**
	 * Get the override that this template applies to Region
	 * 
	 * @return The new Region
	 */
	public String getRegion()
	{
		Region sr = get(ObjectKey.REGION);
		if (sr == null)
		{
			if (getSafe(ObjectKey.USETEMPLATENAMEFORREGION))
			{
				return this.getDisplayName();
			}
			return "None";
		}
		return sr.toString();
	}

	/**
	 * Get the override that this template applies to SubRegion
	 * 
	 * @return The new SubRegion
	 */
	public String getSubRegion()
	{
		SubRegion sr = get(ObjectKey.SUBREGION);
		if (sr == null)
		{
			if (getSafe(ObjectKey.USETEMPLATENAMEFORSUBREGION))
			{
				return this.getDisplayName();
			}
			return "None";
		}
		return sr.toString();
	}

	/**
	 * Query whether this Template is removable. Factors in the visibility of
	 * the Template
	 * 
	 * @return whether this Template is removable
	 */
	public boolean isRemovable()
	{
		boolean result = false;

		if ((getVisibility() == Visibility.DEFAULT)
			|| (getVisibility() == Visibility.DISPLAY_ONLY))
		{
			result = getSafe(ObjectKey.REMOVABLE);
		}

		return result;
	}

	/**
	 * Get the Spell Resistance granted by this template to a character at a
	 * given level (Class and Hit Dice). This will include the absolute
	 * adjustment made with SR:, LEVEL:<num>:SR and HD:<num>:SR tags
	 * 
	 * Note: unlike DR and CR, the value returned here includes the PCs own
	 * Spell Resistance.
	 * 
	 * @param level
	 *            The level to calculate the SR for
	 * @param hitdice
	 *            The Hit dice to calculate the SR for
	 * @param aPC
	 *            DOCUMENT ME!
	 * 
	 * @return the Spell Resistance granted by this Template at the given level
	 *         and HD
	 */
	public int getSR(final int level, final int hitdice,
		final PlayerCharacter aPC)
	{
		int aSR = getSR(aPC);

		if (theLevelAbilities != null)
		{
			for (int lvl = 0; lvl < level; lvl++)
			{
				final String srValue = theLevelAbilities.get(lvl, "SR");
				if (srValue != null)
				{
					final int sr = Integer.parseInt(srValue);
					aSR = Math.max(aSR, sr);
				}
			}
		}

		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x), "SR:")
				&& doesHitDiceQualify(hitdice, x))
			{
				aSR =
						Math.max(Integer.parseInt(getStringAfter("SR:",
							hitDiceStrings.get(x))), aSR);
			}
		}

		return aSR;
	}

	/**
	 * Method getTemplateList. Returns an array list containing the raw
	 * templates granted by this template. This includes CHOOSE: strings which
	 * list templates a user will be asked to choose from.
	 * 
	 * @return ArrayList of granted templates
	 */
	@Override
	public List<String> getTemplateList()
	{
		return templates;
	}

	/**
	 * Grants the character an ability at the Hit die or hit die range
	 * specified. The text may contain the following tags: CR - Challenge
	 * Rating, DR - Damage Reduction, FEAT - Feat, SA - Special Ability, SR -
	 * Spell Resistance
	 * 
	 * 1-3:DR:5/1 Grants Damage Reduction of 5/+1 if natural hit dice is between
	 * one and three.
	 * 
	 * 1+:SR:15 Grants Spell Resistance of 15 if natural hit dice is greater
	 * than one.
	 * 
	 * 2-7:CR:2 Grants an increase in Challenge Rating of two if natural hit
	 * dice is between two and seven.
	 * 
	 * 15+:SA:Uncanny Dodge Grants the "Uncanny Dodge" special ability if
	 * natural hit dice is grater than fifteen.
	 * 
	 * 10+:FEAT:Alertness Grants the "Alertness" feat if natural hit dice is
	 * greater than ten.
	 * 
	 * @param hitDiceString
	 *            a string in the format specified above
	 */
	public void addHitDiceString(final String hitDiceString)
	{
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
	 * Clear the list of HD strings for this template. 
	 */
	public void clearHitDiceStrings()
	{
		if (hitDiceStrings != null)
		{
			hitDiceStrings.clear();
		}
	}

	/**
	 * Get a list of strings which may grant the following abilities at a given
	 * hit die:
	 * 
	 * 1-3:DR:5/1 Grants Damage Reduction of 5/+1 if natural hit dice is between
	 * one and three.
	 * 
	 * 1+:SR:15 Grants Spell Resistance of 15 if natural hit dice is greater
	 * than one.
	 * 
	 * 2-7:CR:2 Grants an increase in Challenge Rating of two if natural hit
	 * dice is between two and seven.
	 * 
	 * 15+:SA:Uncanny Dodge Grants the "Uncanny Dodge" special ability if
	 * natural hit dice is grater than fifteen.
	 * 
	 * 10+:FEAT:Alertness Grants the "Alertness" feat if natural hit dice is
	 * greater than ten.
	 * 
	 * @return a list of strings in the format specified above
	 */
	public List<String> getHitDiceStrings()
	{
		List<String> returnList;
		if (hitDiceStrings != null)
		{
			returnList = hitDiceStrings;
		}
		else
		{
			returnList = Collections.emptyList();
		}
		return returnList;

	}

	/**
	 * Grants the character an ability at the level specified (total character
	 * level).
	 * 
	 * <p>
	 * The type parameter may contain the following tags:
	 * <ul>
	 * <li>CR - Challenge Rating</li>
	 * <li>DR - Damage Reduction</li>
	 * <li>FEAT - Feat</li>
	 * <li>SA - Special Ability</li>
	 * <li>SR - Spell Resistance</li>
	 * </ul>
	 * 
	 * <p>
	 * Feats added by this tag are considered automatic feats and do not count
	 * against a PC's feat pool.
	 * 
	 * <p>
	 * Example:<br />
	 * <code>addLevelAbility(1,&quot;DR&quot;, &quot;5/+1&quot;);<br/>	
	 * Grants Damage Reduction of 5/+1 at Level 1.
	 * <p>
	 * <code>addLevelAbility(6, &quot;FEAT&quot;, &quot;TYPE.Fighter&quot;);<br />
	 * Produces a popup menu at Level 6 from which a PC can choose a fighter feat.
	 *
	 * @param aLevel The level at which this ability will be granted
	 * @param aType One of the types listed above
	 * @param aValue A String to use as the value for that type
	 *
	 */
	public void addLevelAbility(final int aLevel, final String aType,
		final String aValue)
	{
		if (theLevelAbilities == null)
		{
			theLevelAbilities = new DoubleKeyMap<Integer, String, String>();
		}
		if ("DR".equals(aType))
		{
			String[] values = aValue.split("/");
			if (values.length == 2)
			{
				DamageReduction dr = new DamageReduction(values[0], values[1]);

				Prerequisite r = null;
				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();
					r = factory.parse("PRELEVEL:MIN=" + aLevel);
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
		theLevelAbilities.put(aLevel, aType, aValue);
	}

	public void clearLevelAbilities()
	{
		theLevelAbilities = null;
	}

	public List<String> getLevelAbilities()
	{
		final List<String> ret = new ArrayList<String>();

		if (theLevelAbilities != null)
		{
			for (final int lvl : theLevelAbilities.getKeySet())
			{
				for (final String type : theLevelAbilities
					.getSecondaryKeySet(lvl))
				{
					final String value = theLevelAbilities.get(lvl, type);
					final StringBuffer txt = new StringBuffer(200);
					txt.append("LEVEL:").append(lvl).append(":").append(type)
						.append(":").append(value);
					ret.add(txt.toString());
				}
			}
		}
		return ret;
	}

	/**
	 * Add a list of subsidiary Templates to this template i.e. Templates (or
	 * choices of templates) that this Template will grant.
	 * 
	 * @param templateList
	 *            the templates/template choices to add
	 */
	@Override
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
	 * @return a clone of this Template
	 * 
	 * @throws CloneNotSupportedException
	 */
	@Override
	public PCTemplate clone() throws CloneNotSupportedException
	{
		final PCTemplate aTemp = (PCTemplate) super.clone();
		aTemp.templates = new ArrayList<String>(templates);

		if (theLevelAbilities != null)
		{
			aTemp.theLevelAbilities =
					new DoubleKeyMap<Integer, String, String>(theLevelAbilities);
		}

		if (getListSize(hitDiceStrings) != 0)
		{
			aTemp.hitDiceStrings = new ArrayList<String>(hitDiceStrings);
		}

		if (getListSize(featStrings) != 0)
		{
			aTemp.featStrings = new ArrayList<String>(featStrings);
		}

		if (chosenFeatStrings != null)
		{
			aTemp.chosenFeatStrings =
					new HashMap<String, String>(chosenFeatStrings);
		}

		if (theChosenAbilityKeys != null)
		{
			aTemp.theChosenAbilityKeys =
					new HashMap<AbilityCategory, Map<String, String>>(
						theChosenAbilityKeys);
		}
		return aTemp;
	}

	/**
	 * Generate a string that represents the changes this Template will apply.
	 * 
	 * @param aPC
	 *            the Pc we'd like the string generated with reference to
	 * 
	 * @return a string explaining the Template
	 */
	public String modifierString(final PlayerCharacter aPC)
	{
		final StringBuffer mods = new StringBuffer(50); // More likely to be
		// true than 16
		// (the default)

		for (int x = 0; x < SettingsHandler.getGame().getUnmodifiableStatList()
			.size(); ++x)
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
					mods.append(statName(x)).append(':').append(statMod)
						.append(' ');
				}
			}
		}

		if (getDRList().size() != 0)
		{
			mods.append("DR:").append(
				DamageReduction.getDRString(aPC, getDRList()));
		}

		if (aPC == null)
		{
			BigDecimal cr = get(ObjectKey.CR_MODIFIER);

			if (cr != null)
			{
				mods.append("CR:").append(cr).append(' ');
			}

			final int x = getSR(aPC);

			if (x != 0)
			{
				mods.append("SR:").append(x).append(' ');
			}

			// if ((getDR() != null) && !"".equals(getDR()))
			// {
			// mods.append("DR:").append(getDR()).append(' ');
			// }

			return mods.toString();
		}

		final int nat = (int) bonusTo("COMBAT", "AC", aPC, aPC);

		if (nat != 0)
		{
			mods.append("AC BONUS:").append(nat);
		}

		if (getCR(aPC.getTotalLevels(), aPC.totalHitDice()) != 0)
		{
			mods.append("CR:").append(
				getCR(aPC.getTotalLevels(), aPC.totalHitDice())).append(' ');
		}

		if (getSR(aPC.getTotalLevels(), aPC.totalHitDice(), aPC) != 0)
		{
			mods.append("SR:").append(
				getSR(aPC.getTotalLevels(), aPC.totalHitDice(), aPC)).append(
				' ');
		}

		// if (!getDR(aPC.getTotalLevels(), aPC.totalHitDice()).equals(""))
		// {
		// mods.append("DR:").append(getDR(aPC.getTotalLevels(),
		// aPC.totalHitDice()))
		// .append(' ');
		// }

		return mods.toString();
	}

	/**
	 * Returns an array list containing the templates granted by this template.
	 * If there are choices to be made
	 * 
	 * @param isImporting
	 *            Whether the PC is being imported
	 * @param aPC
	 * 
	 * @return a list of templates
	 */
	@Override
	List<String> getTemplates(final boolean isImporting,
		final PlayerCharacter aPC)
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
					templateKey =
							chooseTemplate(this, templateKey.substring(7),
								true, aPC);
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
	 * Modify the list passed in to include any special abilities granted by
	 * this Template
	 * 
	 * @param aList
	 *            The list to be modified
	 * @param level
	 *            The level to add Special abilities for
	 * @param hitdice
	 *            the hit die (/range) to add Special Abilities for
	 * 
	 * @return the list passed in with any special abilities this template
	 *         grants added to it
	 */
	@Override
	public List<SpecialAbility> addSpecialAbilitiesToList(
		final List<SpecialAbility> aList, PlayerCharacter pc)
	{
		super.addSpecialAbilitiesToList(aList, pc);

		if (theLevelAbilities != null)
		{
			int level = pc.getTotalLevels();
			for (int lvl = 0; lvl < level; lvl++)
			{
				final String saString = theLevelAbilities.get(lvl, "SA");
				if (saString != null)
				{
					aList.add(new SpecialAbility(saString));
				}
			}
		}

		int hitdice = pc.totalHitDice();
		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			if (contains(hitDiceStrings.get(x), "SA:")
				&& doesHitDiceQualify(hitdice, x))
			{
				final String saString =
						getStringAfter("SA:", hitDiceStrings.get(x));
				final SpecialAbility sa = new SpecialAbility(saString);

				aList.add(sa);
			}
		}

		return aList;
	}

	/**
	 * Choose a template from template list, allow the chooser to be closed
	 * without choosing a template
	 * 
	 * @param templateList
	 *            List of available templates
	 * @param aPC
	 *            The Pc that prerequisites will be checked against
	 * 
	 * @return the chosen template
	 */
	// static String chooseTemplate(final String templateList, final
	// PlayerCharacter aPC)
	// {
	// return chooseTemplate(templateList, false, aPC);
	// }
	/**
	 * Construct a chooser and ask the operator to choose a template
	 * 
	 * @param templateList
	 *            List of available templates
	 * @param forceChoice
	 *            Whether the user is permitted to close the chooser without
	 *            making a choice
	 * @param aPC
	 *            The Pc that prerequisites will be checked against
	 * 
	 * @return the chosen template
	 */
	static String chooseTemplate(final PObject anOwner,
		final String templateList, final boolean forceChoice,
		final PlayerCharacter aPC)
	{
		final List<PCTemplate> availableList = new ArrayList<PCTemplate>();
		final StringTokenizer strTok = new StringTokenizer(templateList, "|");
		while (strTok.hasMoreTokens())
		{
			PCTemplate template = Globals.getTemplateKeyed(strTok.nextToken());
			if (template != null
				&& PrereqHandler.passesAll(template.getPreReqList(), aPC,
					template))
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
		// If we are left without a choice, don't show the chooser.
		if (availableList.size() < 1)
		{
			return "";
		}
		Globals.getChoiceFromList(title, availableList, selectedList, 1,
			forceChoice);
		if (selectedList != null && selectedList.size() == 1)
		{
			return selectedList.get(0).getKeyName();
		}

		return "";
	}

	/**
	 * Add a subsidiary Template that this Template will add
	 * 
	 * @param templateName
	 *            the name of the Template to add
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
	 * @return a list of Templates
	 */
	@Override
	public List<String> templatesAdded()
	{
		List<String> returnList;
		if (templatesAdded != null)
		{
			returnList = templatesAdded;
		}
		else
		{
			returnList = Collections.emptyList();
		}
		return returnList;

	}

	/**
	 * Get the size of a list, guaranteeing not to return null
	 * 
	 * @param al
	 *            The list whose size w'd like
	 * 
	 * @return size of the list or zero if list undefined
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
	 * @param stuff
	 *            the substring
	 * @param string
	 *            The string to search
	 * 
	 * @return the trailing portion of string following substring stuff
	 */
	private static String getStringAfter(final String stuff, final String string)
	{
		final int index = string.indexOf(stuff) + stuff.length();

		return string.substring(index);
	}

	/**
	 * Does the string contains the given substring
	 * 
	 * @param string
	 *            The string to search
	 * @param stuff
	 *            the substring being looked for
	 * 
	 * @return true if stuff is a substring of string
	 */
	private static boolean contains(final String string, final String stuff)
	{
		return string.indexOf(stuff) > -1;
	}

	/**
	 * convert a STAT index to a STAT name
	 * 
	 * @param x
	 *            the index of the STAT
	 * 
	 * @return the name of the STAT
	 */
	private static String statName(final int x)
	{
		return SettingsHandler.getGame().s_ATTRIBSHORT[x];
	}

	/**
	 * Does the hit die fall within the range of the Hit dice string specified
	 * by the index into the HitDiceStrings array
	 * 
	 * @param hitdice
	 *            the hit die
	 * @param index
	 *            of the Hit Die expression to test
	 * 
	 * @return true if the hit hide qualifies
	 */
	private boolean doesHitDiceQualify(final int hitdice, final int index)
	{
		if (index >= getListSize(hitDiceStrings))
		{
			return false;
		}

		StringTokenizer tokens =
				new StringTokenizer(hitDiceStrings.get(index), ":");
		final String hitDiceString = tokens.nextToken();

		if (hitDiceString.endsWith("+"))
		{
			return Integer.parseInt(hitDiceString.substring(0, hitDiceString
				.length() - 1)) <= hitdice;
		}

		tokens = new StringTokenizer(hitDiceString, "-");

		return (hitdice >= Integer.parseInt(tokens.nextToken()))
			&& (hitdice <= Integer.parseInt(tokens.nextToken()));
	}

	/**
	 * This is the function that implements a chooser for Feats granted by level
	 * and/or HD by Templates.
	 * 
	 * @param levelString
	 *            The string to be parsed for the choices to offer
	 * @param lvl
	 *            The level this is being added at
	 * @param featKey
	 *            either L<lvl> or H<lvl>
	 * @param aPC
	 *            The PC that this Template is appled to
	 */
	private void getLevelFeat(final String featString, final int lvl,
		final String aKey, final PlayerCharacter aPC)
	{
		String featKe = null;
		while (true)
		{
			List<String> featList = new ArrayList<String>();
			final LevelAbility la =
					LevelAbility.createAbility(this, lvl, "FEAT(" + featString
						+ ")");

			la.process(featList, aPC, null);

			switch (featList.size())
			{
				case 1:
					featKe = featList.get(0);

					break;

				default:

					if ((aPC != null) && !aPC.isImporting())
					{
						Collections.sort(featList);

						final ChooserInterface c =
								ChooserFactory.getChooserInstance();
						c.setTotalChoicesAvail(1);
						c.setTitle("Feat Choice");
						c.setAvailableList(featList);
						c.setVisible(true);
						featList = c.getSelectedList();

						if ((featList != null) && (featList.size() != 0))
						{
							featKe = featList.get(0);

							break;
						}
					}

					// fall-through intentional
				case 0:
					return;
			}

			break;
		}

		addChosenFeat(aKey, featKe);
	}

	/**
	 * Add a chosen feat to the Template
	 * 
	 * @param mapKey
	 *            The key to store the feat under
	 * @param mapValue
	 *            The name of the feat
	 */
	public void addChosenFeat(final String mapKey, final String mapValue)
	{
		if (chosenFeatStrings == null)
		{
			chosenFeatStrings = new HashMap<String, String>();
		}

		chosenFeatStrings.put(mapKey, mapValue);
	}

	public void addChosenAbility(final AbilityCategory aCategory,
		final String aKey, final String aChoice)
	{
		if (aCategory == AbilityCategory.FEAT)
		{
			addChosenFeat(aKey, aChoice);
			return;
		}
		if (theChosenAbilityKeys == null)
		{
			theChosenAbilityKeys =
					new HashMap<AbilityCategory, Map<String, String>>();
		}
		Map<String, String> choices = theChosenAbilityKeys.get(aCategory);
		if (choices == null)
		{
			choices = new HashMap<String, String>();
			theChosenAbilityKeys.put(aCategory, choices);
		}
		choices.put(aKey, aChoice);
	}

	/**
	 * Add a | separated list of available feats that this Template may grant.
	 * This is the function called by the Lst parser to make the feats available
	 * to this Template
	 * 
	 * @param abilityString
	 *            The | separated list of feats
	 */
	public void addFeatString(final String abilityString)
	{
		if (".CLEAR".equals(abilityString))
		{
			featStrings = null;
			return;
		}

		if (featStrings == null)
		{
			featStrings = new ArrayList<String>();
		}

		final StringTokenizer aTok =
				new StringTokenizer(abilityString, "|", false);

		while (aTok.hasMoreTokens())
		{
			final String fs = aTok.nextToken();

			if (featStrings == null)
			{
				featStrings = new ArrayList<String>();
			}

			featStrings.add(fs);
		}
	}

	/**
	 * Retrieve the list of the keynames of any feats
	 * that the PC qualifies for at the supplied level and
	 * hit dice. 
	 * 
	 * @param level
	 *            TODO DOCUMENT ME!
	 * @param hitdice
	 *            TODO DOCUMENT ME!
	 * @param aPC
	 *            TODO DOCUMENT ME!
	 * @param addNew
	 *            TODO DOCUMENT ME!
	 * 
	 * @return TODO DOCUMENT ME!
	 */
	public List<String> feats(final int level, final int hitdice,
		final PlayerCharacter aPC, final boolean addNew)
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

		// arknight modified this back in 1.27 with the comment: Added support
		// for
		// Spycraft Game Mode we no longer support Spycraft (at this time), and
		// this
		// breaks other modes, so I've reverting back to the old method. I am
		// also fixing
		// a bug in the code I'm commenting out. levelStrings is used in the 2nd
		// loop
		// instead of hitDiceStrings. - Byngl Sept 25, 2003
		//
		// Scrap all that. I'm using a HashMap to save those feats that have
		// been taken when
		// the required level/hitdie has been met. We need to do this so that
		// removing the
		// template will also remove the selected feat(s). PCTemplate instances
		// will also
		// need to be cloned() when adding them to PlayerCharacter.
		if (chosenFeatStrings != null)
		{
			feats.addAll(chosenFeatStrings.values());
		}

		if (theLevelAbilities != null)
		{
			for (int lvl = 0; lvl <= level; lvl++)
			{
				// Check for an already selected value
				final String lvlKey = "L" + String.valueOf(lvl);
				String featKey = null;
				if (chosenFeatStrings != null)
				{
					featKey = chosenFeatStrings.get(lvlKey);
				}

				// We haven't selected one yet. Ask for one if we are allowed.
				if (featKey == null && addNew == true)
				{
					final String featString =
							theLevelAbilities.get(lvl, "FEAT");
					if (featString != null)
					{
						getLevelFeat(featString, lvl, lvlKey, aPC);
					}
				}
			}
		}

		for (int x = 0; x < getListSize(hitDiceStrings); ++x)
		{
			final String featKey = "H" + Integer.toString(x);
			String featName = null;

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
	 * Get face
	 * 
	 * @return face
	 */
	public Point2D.Double getFace()
	{
		BigDecimal width = get(ObjectKey.FACE_WIDTH);
		BigDecimal height = get(ObjectKey.FACE_HEIGHT);
		if (width == null && height == null)
		{
			return null;
		}
		return new Point2D.Double(width.doubleValue(), height.doubleValue());
	}

	/**
	 * Retrieve this object's visibility in the GUI and on the output sheet
	 * @return Visibility in the GUI and on the output sheet 
	 */
	@Override
	public Visibility getVisibility()
	{
		return getSafe(ObjectKey.VISIBILITY);
	}
}
