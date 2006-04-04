/*
 * LevelAbilityAbility.java
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
 * Current Version: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.levelability;

import pcgen.core.*;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

import java.util.*;

/**
 * A class to deal with adding Ability Objects as a LevelAbility
 *
 * @author   Andrew Wilson
 * @version  $Revision$
 */
public class LevelAbilityAbility extends LevelAbility
{
	protected class AbilityChoice
	{
		Ability ability;
		String  choice;

		/**
		 * Creates a new AbilityChoice object.
		 *
		 * @param  ability
		 * @param  choice
		 */
		public AbilityChoice(Ability ability, String choice)
		{
			super();
			this.ability = ability;
			this.choice  = choice;
		}

		/**
		 * Get the Ability
		 *
		 * @return  Returns the ability.
		 */
		public final Ability getAbility()
		{
			return ability;
		}

		/**
		 * Get the choice
		 *
		 * @return  Returns the choice.
		 */
		public final String getChoice()
		{
			return choice;
		}
	}

	private boolean   isVirtual        = false;
	private boolean   allowDups        = false;
	private int       dupChoices       = 0;
	private int       numFeats         = 0;
	private ArrayList previousChoices  = new ArrayList();
	private String    lastCategorySeen = "";

	final HashMap nameMap    = new HashMap();
	final HashMap catMap     = new HashMap();
	boolean       useNameMap = true;

	LevelAbilityAbility(
	    final PObject aowner,
	    final int     aLevel,
	    final String  aString,
	    final boolean virtual)
	{
		super(aowner, aLevel, aString);
		this.isVirtual = virtual;
	}

	/**
	 * Does this represent an added Ability
	 *
	 * @return  true if this represents an added Ability
	 */

	public boolean isAbility()
	{
		return true;
	}

	/**
	 * Process this Level ability for the given PC.  This is used in two
	 * separate ways.  Firstly, it builds a list of things which may be granted.
	 * Then, if a list was passed to availableList (i.e. not null), the choices
	 * are added to this list and the method returns.  If null is passed to the
	 * first argument, the choices are presented to the user.
	 *
	 * @param  availableList  if non null, gets the list of choices for this
	 *                        LevelAbility
	 * @param  aPC            The PC to process the LevelAbility for
	 * @param  pcLevelInfo    If the choices are being added, this represent the
	 *                        level to add them to.
	 */

	public void process(
	    final List            availableList,
	    final PlayerCharacter aPC,
	    final PCLevelInfo     pcLevelInfo)
	{
		nameMap.clear();
		catMap.clear();
		useNameMap = true;

		final List choicesList = getChoicesList(rawTagData, aPC);

		if (availableList != null)
		{
			availableList.addAll(choicesList);
		}
		else
		{
			final ChooserInterface chooser = ChooserFactory.getChooserInstance();
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

			if (chooser.getPool() == Integer.MIN_VALUE)
			{
				processChoice(
				    choicesList,
				    choicesList,
				    aPC,
				    pcLevelInfo);
			}
			else
			{
				chooser.setAvailableList(choicesList);
				chooser.setVisible(false);

				if (choicesList.size() > 0)
				{
					for(;;)
					{
						chooser.setVisible(true);
						if (processChoice(
						    choicesList,
						    chooser.getSelectedList(),
						    aPC,
						    pcLevelInfo))
						{
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Parses the comma-separated list of the ADD: field and returns the list of
	 * tokens to be shown in the chooser.
	 *
	 * @param   choiceString
	 * @param   aPC           A PlayerCharacter object.
	 *
	 * @return  List of choices
	 */
	List getChoicesList(final String choiceString, final PlayerCharacter aPC)
	{
		final List split = Arrays.asList(choiceString.split("(", 2));

		/* Ignore the empty List returned by the Super class we've built the
		 * list in local state variables */
		super.getChoicesList((String) split.get(1), aPC);

		// The private state variable useNameMap is set whenever a choice is added
		// to the lists of those available, if it becomes necessary to use the
		// category to uniquely identify a choice it will be set false 
		final List aList = useNameMap ? Arrays.asList(nameMap.keySet().toArray())
			                          : Arrays.asList(catMap.keySet().toArray());

		Collections.sort(aList);

		return aList;
	}

	/**
	 * Processes a single token in the comma-separated list of the ADD: field
	 * and adds the choices to be shown in the list to anArrayList.
	 *
	 * @param  aToken       the token to be processed.
	 * @param  anArrayList  the list to add the choice to.
	 * @param  aPC          the PC this Level ability is adding to.
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
			dupChoices = Integer.parseInt(aToken.substring(7));

			return;
		}
		else if (aToken.startsWith("CATEGORY="))
		{
			lastCategorySeen = aToken.substring(9);
		}

		if (lastCategorySeen.equals(""))
		{
			return;
		}

		ArrayList abilityList = getAbilityList(lastCategorySeen, aToken, aPC);
		Iterator  abLIt       = abilityList.iterator();

		while (abLIt.hasNext())
		{
			Ability anAbility = (Ability) abLIt.next();

			if (
			    allowDups &&
			    (dupChoices > 0) &&
			    (dupChoices <= timesChoiceHasBeenTaken(anAbility)))
			{
				continue;
			}

			if (
			    isVirtual ||
			    PrereqHandler.passesAll(anAbility.getPreReqList(), aPC, anAbility))
			{
				if (anAbility.isMultiples())
				{
					addMultiplySelectableAbility(anArrayList, aPC, anAbility);
				}
				else
				{
					addToAvailableLists(anAbility, null);
				}
			}
		}
	}

	/**
	 * count the number of times a given choice has been taken
	 *
	 * @param   anAbility
	 *
	 * @return  the number of times the choice has been taken
	 */
	private int timesChoiceHasBeenTaken(Ability anAbility)
	{
		int x = 0;

		for (Iterator i = previousChoices.iterator(); i.hasNext();)
		{
			Ability previous = (Ability) i.next();

			if (anAbility.isSameBaseAbility(previous))
			{
				x++;
			}
		}

		return x;
	}

	/**
	 * Add an ability object that can be selected multiple times to the list of
	 * available choices
	 *
	 * @param  anArrayList
	 * @param  aPC
	 * @param  anAbility
	 */
	private void addMultiplySelectableAbility(
	    final List            anArrayList,
	    final PlayerCharacter aPC,
	    Ability               anAbility)
	{
		// If already have taken the feat, use it so we can remove
		// any choices already selected
		final Ability pcAbility = aPC.getAbilityMatching(anAbility);

		if (pcAbility != null)
		{
			anAbility = pcAbility;
		}

		String    subName        = getSubName(anAbility.getName());
		final int indexOfPercent = subName.indexOf('%');

		if (indexOfPercent > -1)
		{
			subName = subName.substring(0, indexOfPercent).trim();
		}
		else if (subName.length() != 0)
		{
			final int idx = subName.lastIndexOf(')');

			if (idx > -1)
			{
				subName = subName.substring(0, idx);
			}
		}

		final List availableList = new ArrayList(); // available list of choices
		final List selectedList  = new ArrayList(); // selected list of choices

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
			Iterator it = availableList.iterator();

			while (it.hasNext())
			{
				final String choice = (String) it.next();

				if (!choice.startsWith(subName))
				{
					it.remove();
				}
			}

			/*
			 * Example: ADD:ABILITY(CATEGORY=FEAT,Skill Focus(Craft (Basketweaving))) If
			 * you have no ranks in Craft (Basketweaving), the available list will be
			 * empty
			 *
			 * Make sure that the specified feat is available, even though it does not
			 * meet the prerequisite
			 */

			if ((indexOfPercent == -1) && (availableList.size() == 0))
			{
				availableList.add(subName);
			}
		}

		// Remove any already selected

		if (!anAbility.isStacks())
		{
			Iterator it = selectedList.iterator();

			while (it.hasNext())
			{
				final int idx = availableList.indexOf(it.next());

				if (idx > -1)
				{
					availableList.remove(idx);
				}
			}
		}

		if (!anAbility.getChoiceString().startsWith("SPELLLIST|"))
		{
			Iterator it = availableList.iterator();

			while (it.hasNext())
			{
				String choice = (String) it.next();
				addToAvailableLists(
				    anAbility,
				    (!choice.equalsIgnoreCase("NOCHOICE")) ? choice : null);
			}
		}
		else
		{
			addToAvailableLists(anAbility, null);
		}
	}

	/**
	 * Add the ability to the lists of available choices
	 *
	 * @param  anAbility
	 * @param  choice
	 */
	private void addToAvailableLists(Ability anAbility, String choice)
	{
		String theName    = anAbility.getName();
		AbilityChoice abC = new AbilityChoice(anAbility, choice);

		Ability abNull = (Ability) nameMap.put(theName, abC);
		catMap.put(anAbility.getCategory() + " " + theName, abC);

		if (abNull != null)
		{
			useNameMap = false;
		}
	}

	/**
	 * subName is the bit from inside brackets of an Ability Name "Armour
	 * Proficiency (Light)" -> subName = "Light".  If the portion inside the
	 * outermost parenthesis contains a %, then subname is truncated just before
	 * that character.  Similarly if subName has nested parenthesis, it will be
	 * truncated after the last closing parenthesis e.g.: "Foo (Bar %Baz) ->
	 * subName = "Bar""Foo (Bar (Baz) Qux)) -> subName = "Bar (Baz)"although
	 * surreally: "Foo (Bar (Baz) Qux %Quux)) -> subName = "Bar (Baz) Qux"
	 *
	 * @param   name
	 *
	 * @return  the subname
	 */
	private static String getSubName(String name)
	{
		int start = name.indexOf('(');
		int end   = name.lastIndexOf(')');

		if ((start > -1) && (end > -1))
		{
			return name.substring(start + 1, end);
		}

		return null;
	}

	/**
	 * Get a list of AbilityInfo Objects to be offered by this token (a
	 * levelAbilityFeat may have many different tokens, some of which specify
	 * types of Ability and others specify individual feats)
	 *
	 * @param   acategory
	 * @param   abilityToken
	 * @param   aPC
	 *
	 * @return  a list of AbilityInfo Objects
	 */
	private ArrayList getAbilityList(
	    final String          acategory,
	    String                abilityToken,
	    final PlayerCharacter aPC)
	{
		ArrayList featList = new ArrayList();

		if (abilityToken.startsWith("TYPE=") || abilityToken.startsWith("TYPE."))
		{
			final String abilityType = abilityToken.substring(5);

			if ("REGION".equals(abilityType))
			{
				addAbilitiesForRegion(aPC, featList, acategory, abilityType);
			}
			else if ("SUBREGION".equals(abilityType))
			{
				addAbilitiesForSubRegion(aPC, featList, acategory, abilityType);
			}
			else if ("ALLREGION".equals(abilityType))
			{
				addAbilitiesForRegion(aPC, featList, acategory, abilityType);
				addAbilitiesForSubRegion(aPC, featList, acategory, abilityType);
			}
			else
			{
				featList.addAll(
				    aPC.getAvailableAbilities(acategory, abilityType, isVirtual));
			}

			return featList;
		}

		final Ability anAbility = AbilityUtilities.retrieveAbilityKeyed(
			    acategory,
			    abilityToken);

		if (anAbility != null)
		{
			featList.add(anAbility);
		}

		return featList;
	}

	/**
	 * add the Abilities of the type that matches the PCs subregion to
	 * abilityList
	 *
	 * @param  aPC
	 * @param  abilityList
	 * @param  category
	 * @param  abilityType
	 */
	private void addAbilitiesForSubRegion(
	    final PlayerCharacter aPC,
	    ArrayList             abilityList,
	    String                category,
	    String                abilityType)
	{
		abilityType = getAbilityTypeFromRegion(aPC, abilityType, false);
		abilityList.addAll(aPC.getAvailableAbilities(category, abilityType, isVirtual));
	}

	/**
	 * add the Abilities of the type that matches the PCs region to abilityList
	 *
	 * @param  aPC
	 * @param  abilityList
	 * @param  category
	 * @param  abilityType
	 */
	private void addAbilitiesForRegion(
	    final PlayerCharacter aPC,
	    ArrayList             abilityList,
	    String                category,
	    String                abilityType)
	{
		abilityType = getAbilityTypeFromRegion(aPC, abilityType, true);
		abilityList.addAll(aPC.getAvailableAbilities(category, abilityType, isVirtual));
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
		Map translation = (useNameMap) ? nameMap : catMap;

		if (isVirtual)
		{
			Iterator it = selectedList.iterator();

			while (it.hasNext())
			{
				final String  abilityKey = (String) it.next();
                final List    choiceList = new ArrayList();

				final Ability ab = ((AbilityChoice) translation.get(abilityKey)).getAbility();
				choiceList.add(((AbilityChoice) translation.get(abilityKey)).getChoice());

				previousChoices.add(ab);

				List aList = aPC.getVirtualFeatList();
				aList = AbilityUtilities.addVirtualFeat(
						ab,
					    choiceList,
					    aList,
					    aPC,
					    pcLevelInfo);

				final Ability pcAbility = AbilityUtilities.getMatchingFeatInList(aList, ab);
//				final Ability pcAbility = AbilityUtilities.getAbilityFromList(aList, ab);

				if (pcAbility != null)
				{
					if (pcAbility.isMultiples())
					{
						final double x = aPC.getRawFeats(false);
						aPC.setFeats(1); // temporarily assume 1 choice
						pcAbility.modChoices(aPC, true);
						aPC.setFeats(x); // reset to original count
					}

					pcAbility.setNeedsSaving(true);
				}
				else
				{
					Logging.errorPrint(
					    "Error:" + abilityKey +
					    " not added, aPC.getFeatNamedInList() == NULL");
				}
			}
		}
		else
		{
			// If automatically choosing all abilities in a list, then set the
			// number allowed to the number available
			if (numFeats == Integer.MIN_VALUE) {numFeats = selectedList.size();}

			aPC.adjustFeats(numFeats);

			Iterator it = selectedList.iterator();

			while (it.hasNext())
			{
				final String        abK    = (String) it.next();
				final AbilityChoice abC    = (AbilityChoice) translation.get(abK);
				final Ability       ab     = abC.getAbility();
                final String        choice = abC.getChoice();

				previousChoices.add(ab);

				final List   aBonusList        = new ArrayList();
				boolean      spellLevelProcess = false;
				final String choiceString      = ab.getChoiceString();

				if ((ab != null) && choiceString.startsWith("SPELLLEVEL"))
				{
					spellLevelProcess = true;
					final List bonuses = Arrays.asList(choiceString.split("[]"));

					Iterator bonusIt = bonuses.iterator();
					bonusIt.next();

					while (bonusIt.hasNext())
					{
						aBonusList.add(bonusIt.next());
					}
				}
				AbilityUtilities.modAbility(aPC, pcLevelInfo, ab, choice, true, false);

				if (spellLevelProcess && (ab != null))
				{

					for (Iterator bonii = aBonusList.iterator(); bonii.hasNext();)
					{
						if (ab != null)
						{
							ab.applyBonus((String) bonii.next(), choice, aPC);
						}
					}
				}
			}
		}

		final List transChoices = new ArrayList();
		Iterator it = selectedList.iterator();
		while (it.hasNext())
		{
			transChoices.add(it.next());
		}

		addAllToAssociated(transChoices);
		return true;
	}

	/**
	 * Check all templates for a region (or subregion if parameter region is
	 * false), return the last one found as the type of feats to be added by
	 * ADD:FEAT(TYPE=REGION) This implementation assumes that a PC only has one
	 * region and one subregion.
	 *
	 * @param   aPC
	 * @param   abilityType
	 * @param   region
	 *
	 * @return  String
	 */
	private static String getAbilityTypeFromRegion(
	    final PlayerCharacter aPC,
	    String                abilityType,
	    boolean               region)
	{
		final Iterator iterator = aPC.getTemplateList().iterator();

		for (Iterator e = iterator; e.hasNext();)
		{
			final PCTemplate templ      = (PCTemplate) e.next();
			final String     regionType = (region) ? templ.getRegion()
				                                   : templ.getSubRegion();

			if (!regionType.equals(Constants.s_NONE))
			{
				abilityType = regionType;
			}
		}

		return abilityType;
	}
}
