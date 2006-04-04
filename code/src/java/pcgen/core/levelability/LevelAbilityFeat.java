/*
 * LevelAbilityFeat.java
 * Copyright 2001 (C) Dmitry Jemerov
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
 * Created on July 24, 2001, 10:11 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core.levelability;

import pcgen.core.*;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

import java.util.*;

/**
 * Represents a feat that a character gets when gaining a level (an ADD:FEAT
 * entry in the LST file).
 *
 * @author   Dmitry Jemerov <yole@spb.cityline.ru>
 * @version  $Revision$
 */
class LevelAbilityFeat extends LevelAbility
{
	protected boolean   isVirtual       = false;
	protected int       numFeats        = 1;
	protected boolean   allowDups       = false;
	protected int       dupChoices      = 0;
	protected ArrayList previousChoices = new ArrayList();
	private   boolean	hasPrereqs		= false;

	LevelAbilityFeat(
	    final PObject aowner,
	    final int     aLevel,
	    final String  aString,
	    final boolean aVFeat)
	{
		super(aowner, aLevel, aString);
		isVirtual = aVFeat;
	}

	/**
	 * Whether or not this LevelAbility object represents a Feat
	 *
	 * @return  True if this LevelAbility is a feat
	 */
	public boolean isFeat()
	{
		return true;
	}

	/**
	 * Does whatever is appropriate to remove the effects of this when the
	 * character looses a level.
	 *
	 * @param  aPC  A PlayerCharacter object.
	 */
	public void subForLevel(final PlayerCharacter aPC)
	{
		// If nothing saved, then process as LevelAbility
		if (getAssociatedCount(false) == 0)
		{
			super.subForLevel(aPC);
		}
		else
		{
			for (int j = 0; j < getAssociatedCount(); ++j)
			{
				String        featName  = getAssociatedList().get(j).toString();
				final Ability anAbility = Globals.getAbilityNamed("FEAT", featName);

				if (anAbility == null)
				{
					aPC.adjustFeats(-1);
					Logging.debugPrint("There is no feat '" + featName + "'. Adjusting feat count by -1");
				}
				else
				{
					featName = anAbility.getName();
					aPC.adjustFeats(-anAbility.getCost());
				}

				AbilityUtilities.modFeat(aPC, null, featName, false, false);
			}

			clearAssociated();
		}
	}

	/**
	 * Parses the comma-separated list of the ADD: field and returns the list of
	 * tokens to be shown in the chooser.
	 *
	 * @param   bString
	 * @param   aPC      A PlayerCharacter object.
	 *
	 * @return  List of choices
	 */
	List getChoicesList(final String bString, final PlayerCharacter aPC)
	{
		final List aList;

		if (isVirtual)
		{
			aList = super.getChoicesList(bString.substring(6), aPC);
		}
		else
		{
			aList = super.getChoicesList(bString.substring(5), aPC);
		}

		Collections.sort(aList);

		return aList;
	}

	/**
	 * Performs the initial setup of a chooser.
	 *
	 * @param   chooser
	 * @param   aPC
	 *
	 * @return  String
	 */
	String prepareChooser(final ChooserInterface chooser, PlayerCharacter aPC)
	{
		setNumberofChoices(chooser, aPC);
		numFeats = chooser.getPool();

		if (isVirtual)
		{
			chooser.setTitle("Virtual Feat Selection");
		}
		else
		{
			chooser.setTitle("Feat Choice");
		}

		chooser.setPoolFlag(true);

		return rawTagData;
	}

	/**
	 * Process the choice selected by the user.
	 * @param  selectedList
	 * @param  aPC
	 * @param  pcLevelInfo
	 * @param  aArrayList
	 */
	public boolean processChoice(
	    final List            aArrayList,
	    final List            selectedList,
	    final PlayerCharacter aPC,
	    final PCLevelInfo     pcLevelInfo)
	{
		if (isVirtual)
		{
			final int listSize = selectedList.size();

			for (int index = 0; index < listSize; ++index)
			{
				final String featName = selectedList.get(index).toString();
				previousChoices.add(featName);

				List aList = aPC.getVirtualFeatList();
				aList      = AbilityUtilities.addVirtualFeat(featName, aList, pcLevelInfo, aPC);

				final Ability aFeat = AbilityUtilities.getAbilityFromList(aList, "FEAT", featName, -1);

				if (aFeat != null)
				{
					if (aFeat.isMultiples())
					{
						final double x = aPC.getRawFeats(false);
						aPC.setFeats(1); // temporarily assume 1 choice
						aFeat.modChoices(aPC, true);
						aPC.setFeats(x); // reset to original count
					}

					aFeat.setNeedsSaving(true);
				}
				else
				{
					Logging.errorPrint(
					    "Error:" + featName +
					    " not added, aPC.getFeatNamedInList() == NULL");
				}
			}
		}
		else
		{
			//
			// If automatically choosing all feats in a list, then set then
			// number allowed to the number chosen. i.e. the number in the list
			//
			if (numFeats == Integer.MIN_VALUE)
			{
				numFeats = selectedList.size();
			}
//			aPC.adjustFeats(numFeats);

			//
			// If there have been no selections, then if all the entries have the same cost,
			// increment the feat pool by that amount. If they have different costs, then
			// force the user to make a selection
			//
			if (selectedList.size() == 0)
			{
				double maxVal = Double.NaN;
				double minVal = Double.NaN;
				for (int n = 0; n < aArrayList.size(); ++n)
				{
					final String availItem = aArrayList.get(n).toString();
					final Ability anAbility = Globals.getAbilityNamed("ALL", availItem);
					double c;
					if (anAbility == null)
					{
						c = 1.0;
					}
					else
					{
						c = anAbility.getCost();
					}
					if (Double.isNaN(maxVal) || (c > maxVal))
					{
						maxVal = c;
					}
					if (Double.isNaN(minVal) || (c < minVal))
					{
						minVal = c;
					}
				}
				if (!Double.isNaN(maxVal))
				{
					//
					// Don't want to mess with the feat count if the cost is less than zero
					//
					if (CoreUtility.doublesEqual(minVal, maxVal) && (maxVal > -0.0001))
					{
						aPC.adjustFeats(maxVal);
						return true;
					}
				}
				ShowMessageDelegate.showMessageDialog("Available selections have varying costs.\nYou must make a selection.", Constants.s_APPNAME, MessageType.INFORMATION);
				return false;
			}


			for (int n = 0; n < selectedList.size(); ++n)
			{
				String chosenItem = selectedList.get(n).toString();
				previousChoices.add(chosenItem);

				final String featString        = chosenItem;
				final List   aBonusList        = new ArrayList();
				Ability      anAbility         = Globals.getAbilityNamed("FEAT", featString);
				boolean      spellLevelProcess = false;

				if (
				    (anAbility != null) &&
				    anAbility.getChoiceString().startsWith("SPELLLEVEL"))
				{
					spellLevelProcess = true;

					final StringTokenizer sTok = new StringTokenizer(
						    anAbility.getChoiceString(),
						    "[]",
						    false);
					sTok.nextToken();

					while (sTok.hasMoreTokens())
					{
						aBonusList.add(sTok.nextToken());
					}
				}

				if (anAbility != null)
				{
					//
					// Add the cost of the feat to the pool
					//
					aPC.adjustFeats(anAbility.getCost());
				}
				else
				{
					aPC.adjustFeats(1);
					Logging.debugPrint("There is no feat '" + featString + "'. Adjusting feat count by 1");
				}

				AbilityUtilities.modFeat(aPC, pcLevelInfo, featString, true, false);

				if (spellLevelProcess && (anAbility != null))
				{
					if (chosenItem.indexOf('(') > 0)
					{
						final StringTokenizer cTok = new StringTokenizer(
							    chosenItem,
							    "()",
							    false);
						anAbility  = aPC.getFeatNamed(cTok.nextToken());
						chosenItem = cTok.nextToken();
					}

					for (Iterator bonii = aBonusList.iterator(); bonii.hasNext();)
					{
						if (anAbility != null)
						{
							anAbility.applyBonus((String) bonii.next(), chosenItem, aPC);
						}
					}
				}
			}
		}

		addAllToAssociated(selectedList);
		return true;
	}

	/**
	 * Processes a single token in the comma-separated list of the ADD: field
	 * and adds the choices to be shown in the list to anArrayList.
	 *
	 * @param  aToken        the token to be processed.
	 * @param  anArrayList   the list to add the choice to.
	 * @param  aPC           the PC this Level ability is adding to.
	 */
	void processToken(
	    final String          aToken,
	    final List            anArrayList,
	    final PlayerCharacter aPC)
	{
		if ("STACKS".equals(aToken))
		{
			allowDups = true;
			return;
		}
		else if (aToken.startsWith("STACKS="))
		{
			allowDups  = true;
			try
			{
				dupChoices = Integer.parseInt(aToken.substring(7));
			}
			catch (NumberFormatException nfe)
			{
				// TODO Deal with this Exception?
			}
			return;
		}

		ArrayList featList = getFeatList(aToken, aPC);
		Iterator  fi       = featList.iterator();
		hasPrereqs = false;
		while (fi.hasNext())
		{
			String                theChoice = (String) fi.next();
			final StringTokenizer aTok      = new StringTokenizer(theChoice, ",", false);
			String                featName  = aTok.nextToken().trim();
			String                subName   = "";
			Ability               anAbility = Globals.getAbilityNamed("FEAT", featName);

			if (anAbility == null)
			{
				Logging.errorPrint("LevelAbilityFeat: Feat not found: " + featName);
				continue;
			}

			if (!featName.equalsIgnoreCase(anAbility.getName()))
			{
				subName = adjustNames(featName, anAbility);
			}

			if (allowDups)
			{
				if (anAbility.getPreReqCount() != 0)
				{
					hasPrereqs = true;
				}
			}

			if (
			    allowDups &&
			    (dupChoices > 0) &&
			    (dupChoices <= timesChoiceHasBeenTaken(featName)))
			{
				continue;
			}

			if (
			    isVirtual ||
			    PrereqHandler.passesAll(anAbility.getPreReqList(), aPC, anAbility))
			{
				if (anAbility.isMultiples())
				{
					addMultiplySelectableAbility(anArrayList, aPC, featName, subName, anAbility);
				}
				else if (
				    !aPC.hasRealFeatNamed(featName) &&
				    !aPC.hasFeatAutomatic(featName))
				{
					anArrayList.add(featName);
				}
			}
		}
	}

	/**
	 * Get a list of feat names to be offered by this token
	 * (a levelAbilityFeat may have many different tokens, some of
	 * which specify types of feat and others specify individual feats)
	 * @param aToken
	 * @param aPC
	 * @return a list of feat names
	 */
	private ArrayList getFeatList(final String aToken, final PlayerCharacter aPC) {

		if (aToken.startsWith("TYPE=") || aToken.startsWith("TYPE."))
		{
			ArrayList featList = new ArrayList();
			String    featType = aToken.substring(5);

			if ("REGION".equals(featType))
			{
				addFeatsForRegion(aPC, featList, featType);
			}
			else if ("SUBREGION".equals(featType))
			{
				addFeatsForSubRegion(aPC, featList, featType);
			}
			else if ("ALLREGION".equals(featType))
			{
				addFeatsForRegion(aPC, featList, featType);
				addFeatsForSubRegion(aPC, featList, featType);
			}
			else
			{
				featList.addAll(aPC.getAvailableFeatNames(featType, isVirtual));
			}

			return featList;
		}
		ArrayList featList = new ArrayList();
		featList.add(aToken);
		return featList;
	}

	/**
	 * @param aPC
	 * @param featList
	 * @param featType
	 */
	private void addFeatsForRegion(final PlayerCharacter aPC, ArrayList featList, String featType) {
		featType = findFeatType(aPC, featType, true);
		featList.addAll(aPC.getAvailableFeatNames(featType, isVirtual));
	}

	/**
	 * @param aPC
	 * @param featList
	 * @param featType
	 */
	private void addFeatsForSubRegion(final PlayerCharacter aPC, ArrayList featList, String featType) {
		featType = findFeatType(aPC, featType, false);
		featList.addAll(aPC.getAvailableFeatNames(featType, isVirtual));
	}

	/**
	 * Check all templates for a region (or subregion if parameter region is false), return the
	 * last one found as the type of feats to be added by ADD:FEAT(TYPE=REGION)
	 * This implementation assumes that a PC only has one region and one subregion.
	 *
	 * @param   aPC
	 * @param   featType
	 * @param   region
	 * @return  String
	 */
	private static String findFeatType(final PlayerCharacter aPC, String featType, boolean region)
	{
		final Iterator iterator = aPC.getTemplateList().iterator();

		for (Iterator e = iterator; e.hasNext();)
		{
			final PCTemplate templ      = (PCTemplate) e.next();
			final String     regionType = (region) ? templ.getRegion() : templ.getSubRegion();

			if (!regionType.equals(Constants.s_NONE))
			{
				featType = regionType;
			}
		}

		return featType;
	}

	/**
	 * featName becomes the name of anAbility.  the method returns what should
	 * essentially be the KeyName of the Ability, that is it copies what used
	 * to be in featName, but truncates its length to the new length of
	 * featName.  This should effectively strip off any choice strings at the
	 * end of the featname (e.g. Spell Mastery (Dancing Lights) ->
	 * featName = Spell Mastery | subName = Spell Mastery).  After the truncation,
	 * if subName still contains () then it is truncated until it doesn't
	 * (e.g. Armour Proficiency (Light) -> featName = Armour Proficiency |
	 * subName = Armour Proficiency).
	 *
	 * @param featName
	 * @param anAbility
	 * @return the subName after any necessary truncation
	 */
	private String adjustNames(String featName, Ability anAbility) {
		String subName = featName.substring(anAbility.getName().length());
		featName       = anAbility.getName();

		final int i = subName.indexOf('(');

		return (i > -1) ? subName.substring(i + 1) : subName;
	}

	/**
	 * @param anArrayList
	 * @param aPC
	 * @param featName
	 * @param subName
	 * @param anAbility
	 */
	private void addMultiplySelectableAbility(
			final List            anArrayList,
			final PlayerCharacter aPC,
			String                featName,
			String                subName,
			Ability               anAbility)
	{
		// If already have taken the feat, use it so we can remove
		// any choices already selected
		final Ability pcFeat = aPC.getFeatNamed(featName);

		if (pcFeat != null)
		{
			anAbility = pcFeat;
		}

		final int percIdx = subName.indexOf('%');

		if (percIdx > -1)
		{
			subName = subName.substring(0, percIdx);
		}
		else if (subName.length() != 0)
		{
			final int idx = subName.lastIndexOf(')');

			if (idx > -1)
			{
				subName = subName.substring(0, idx);
			}
		}

		final List availableList  = new ArrayList(); // available list of choices
		final List selectedList   = new ArrayList(); // selected list of choices

		final String choiceString = anAbility.getChoiceString();

		if (
		    (choiceString.indexOf("NUMCHOICES=") < 0) &&
		    (choiceString.indexOf("COUNT=") < 0))
		{
			anAbility.modChoices(
			    aPC,
			    true,
			    availableList,
			    selectedList,
			    false);
		}
		else
		{
			availableList.add("NOCHOICE");
		}

		// Remove any that don't match

		if (subName.length() != 0)
		{
			for (int n = availableList.size() - 1; n >= 0; --n)
			{
				final String aString = (String) availableList.get(n);

				if (!aString.startsWith(subName))
				{
					availableList.remove(n);
				}
			}

			// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving))) If you
			// have no ranks in Craft (Basketweaving), the available list will
			// be empty
			//
			// Make sure that the specified feat is available, even though it
			// does not meet the prerequisite

			if ((percIdx == -1) && (availableList.size() == 0))
			{
				availableList.add(subName);
			}
		}

		// Remove any already selected

		if (!anAbility.isStacks())
		{
			for (Iterator e = selectedList.iterator(); e.hasNext();)
			{
				final int idx = availableList.indexOf(e.next());

				if (idx > -1)
				{
					availableList.remove(idx);
				}
			}
		}

		if (!anAbility.getChoiceString().startsWith("SPELLLIST|"))
		{
			for (Iterator e = availableList.iterator(); e.hasNext();)
			{
				String aString = (String) e.next();

				if (!aString.equalsIgnoreCase("NOCHOICE"))
				{
					anArrayList.add(featName + "(" + aString + ")");
				}
				else
				{
					anArrayList.add(featName);
				}
			}
		}
		else
		{
			anArrayList.add(featName);
		}

		//continue;
	}

	/**
	 * count the number of times a given choice has been taken
	 *
	 * @param   choice  the choice
	 *
	 * @return  the number of times the choice has been taken
	 */
	private int timesChoiceHasBeenTaken(String choice)
	{
		int x = 0;

		for (Iterator i = previousChoices.iterator(); i.hasNext();)
		{
			String aString = (String) i.next();

			if (choice.equalsIgnoreCase(aString))
			{
				x++;
			}
		}

		return x;
	}

	/**
	 * Process this Level ability for the given PC.  This is used in two
	 * separate ways.  Firstly, it builds a list of feats which may be granted.
	 * Then, if a list was passed to availableList (i.e. not null), the choices
	 * are added to this list and the method returns.  If null is passed to the
	 * first argument, the choices are presented to the user.
	 *
	 * @param  availableList  if non null, gets the list of choices for this
	 *                        LevelAbility
	 * @param  aPC            The PC to process the LevelAbility for
	 * @param  pcLevelInfo    If the feats are being added, this represent the
	 *                        level to add them to.
	 */
	public void process(
	    final List            availableList,
	    final PlayerCharacter aPC,
	    final PCLevelInfo     pcLevelInfo)
	{
		aText = rawTagData;

		boolean first = true;
		int choicesRemaining = 1;
		int selectionCount  = 0;
		for (; ;)
		{
			final ChooserInterface ch           = ChooserFactory.getChooserInstance();
			final String           choiceString = prepareChooser(ch, aPC);
			final List             choicesList  = getChoicesList(choiceString, aPC);

			//
			// If duplicates are allowed, then ask for 1 choice at a time if at least one
			// of the feats has a prerequisite (to ensure the PRExxx does not become invalidated).
			// If none of the feats have prerequisites, then we can ask for the maximum number of
			// duplicates allowed the first time. For subsequent passes, we will only ask for 1 selection
			// although we could count the maximum occuring feat and subtract it's count from the
			// maximum allowed selections...
			//
			if (allowDups)
			{
				choicesRemaining -= selectionCount;
				if (choicesRemaining < 1)
				{
					break;
				}
				if (!first || hasPrereqs)
				{
					ch.setPool(1);
				}
				else if (first && (dupChoices > 0))
				{
					choicesRemaining = numFeats;
					ch.setPool(Math.min(choicesRemaining, dupChoices));
				}
				else
				{
//					// Do nothing: count already set to numFeats
				}
				first = false;
			}

			ch.setAllowsDups(allowDups);
			selectionCount = 0;


			/* are we building the choices into a list to return (not null) */
			if (availableList != null)
			{
				availableList.addAll(choicesList);
			}

			/* Or displaying a chooser for the user */
			else if (ch.getPool() == Integer.MIN_VALUE)
			{
				processChoice(choicesList, choicesList, aPC, pcLevelInfo);
				selectionCount = ch.getSelectedList().size();
			}
			else
			{
				ch.setAvailableList(choicesList);
				ch.setVisible(false);

				if (choicesList.size() > 0)
				{
					for(;;)
					{
						ch.setVisible(true);
						if (processChoice(
						    choicesList,
						    ch.getSelectedList(),
						    aPC,
						    pcLevelInfo))
						{
							break;
						}
					}
					selectionCount = ch.getSelectedList().size();
				}
			}
			if (!allowDups)
			{
				break;
			}
		}
	}
}
