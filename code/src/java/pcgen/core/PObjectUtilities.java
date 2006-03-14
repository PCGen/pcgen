/*
 * PObjectUtilities.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * PObject Created on April 21, 2001, 2:15 PM
 * Refactored out of PObject June 13, 2005
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.util.*;

import pcgen.core.character.CharacterSpell;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.io.PCGIOHandler;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

/**
 * Modify an Ability object for a given PC (or remove an Ability
 * Object from a given PC)
 */
public class PObjectUtilities
{

	/**
	 * @param anAbility
	 * @return true or false
	 */
	private static int chooseAbility(final Ability anAbility)
	{
		int i;
		final List aList = new ArrayList();
		aList.add("New");

		FeatMultipleChoice fmc;
		final StringBuffer sb = new StringBuffer(100);
		for (int j = 0; j < anAbility.getAssociatedCount(); ++j)
		{
			fmc = (FeatMultipleChoice) anAbility.getAssociatedList().get(j);
			sb.append(anAbility.getName()).append(" (");
			sb.append(fmc.getChoiceCount());
			sb.append(" of ").append(fmc.getMaxChoices()).append(") ");

			for (i = 0; i < fmc.getChoiceCount(); ++i)
			{
				if (i != 0)
				{
					sb.append(',');
				}

				sb.append(fmc.getChoice(i));
			}

			aList.add(sb.toString());
			sb.setLength(0);
		}

		final Object selectedValue;

		if (aList.size() > 1)
		{
			final InputInterface ii = InputFactory.getInputInstance();
			selectedValue = ii.showInputDialog(null,
				"Please select the instance of the feat you wish to" + Constants.s_LINE_SEP
				+ "modify, or New, from the list below.", Constants.s_APPNAME,
				MessageType.INFORMATION, aList.toArray(), aList.get(0));
		}
		else
		{
			selectedValue = aList.get(0);
		}

		if (selectedValue == null)
		{
			return -1;
		}

		return aList.indexOf(selectedValue);
	}

	public static final boolean modChoices(
			final PObject         obj,
				  List            availableList,
			final List            selectedList,
			final boolean         process,
			final PlayerCharacter aPC,
			final boolean         addIt)
	{
		availableList.clear();
		selectedList.clear();

		final String aChoiceString = obj.getChoiceString();
		if (aChoiceString.startsWith("WEAPONPROF|") ||
			aChoiceString.startsWith("ARMORPROF|")  ||
			aChoiceString.startsWith("SHIELDPROF|"))
		{
			obj.getChoices(aChoiceString, null, availableList, selectedList, aPC);

			return false;
		}

		StringTokenizer aTok = new StringTokenizer(aChoiceString, "|");

		if ((aTok.countTokens() < 1) || (aPC == null) || aPC.isImporting())
		{
			return false;
		}

		int numChoices = -1;

		double cost       = 1.0;
		Ability anAbility = null;
		boolean stacks    = false;
		boolean multiples = false;

		if (obj instanceof Ability)
		{
			anAbility = (Ability) obj;
			cost      = anAbility.getCost();
			stacks    = anAbility.isStacks();
			multiples = anAbility.isMultiples();
		}

		int i;
		int maxSelections = (int) ((aPC.getFeats() + obj.getAssociatedCount()) / cost);

		if (cost <= 0)
		{
			maxSelections = (int) (aPC.getFeats() + obj.getAssociatedCount());
		}

		final List uniqueList    = new ArrayList();
		final List aBonusList    = new ArrayList();
		final List rootArrayList = new ArrayList();
		String choiceType        = aTok.nextToken();

		Iterator iter;
		String title = "Choices";

		int idxSelected = -1;

		int maxNewSelections = (int) (aPC.getFeats() / cost);

		if (cost <= 0)
		{
			maxNewSelections = (int) (aPC.getRawFeats(false));
		}

		int requestedSelections = -1;
////////////////////////////////
// This was moved into its own subroutine, but Java is NOT call-by-reference, so it broke both
// COUNT=, and NUMCHOICES= so I am re-instating it.
// Here is a semi-decent article on Java and parameters http://javadude.com/articles/passbyvalue.htm
// - Byngl Oct 4, 2005
//
		for (; ;)
		{
			if (choiceType.startsWith("COUNT="))
			{
				requestedSelections = aPC.getVariableValue(choiceType.substring(6), "").intValue();
			}
			else if (choiceType.startsWith("NUMCHOICES="))
			{
				//
				// If removing, then don't allow an extra 'freebie' (and force the user to enter it)
				//
				if (addIt)
				{
					numChoices = aPC.getVariableValue(choiceType.substring(11), "").intValue();
				}
			}
			else
			{
				break;
			}
			if (!aTok.hasMoreTokens())
			{
				Logging.errorPrint("not enough tokens: " + aChoiceString);

				return false;
			}

			choiceType = aTok.nextToken();
		}
////////////////////////////////
		if (Globals.weaponTypesContains(choiceType))
		{
			title = choiceType + " Weapon Choice";
			setWeaponProfTypeSelections(obj, availableList, selectedList, aPC, choiceType);
		}

		else if ("ARMORTYPE".equals(choiceType))
		{
			title = "Armor Type Choice";
			setArmorTypeSelections(obj, availableList, selectedList, aPC);
		}
		else if ("CSKILLS".equals(choiceType))
		{
			title = "Skill Choice";
			setCSkillsSelections(obj, availableList, selectedList, aPC);
		}
		else if ("DOMAIN".equals(choiceType))
		{
			title = "Domain Choice";
			setDomainSelections(obj, availableList, selectedList, aPC, aTok);
		}
		else if ("EQUIPTYPE".equals(choiceType))
		{
			title = "Equipment Choice";
			setEquipTypeSelections(obj, availableList, selectedList, aTok);
		}
		else if ("FEATADD".equals(choiceType))
		{
			title = "Add a Feat";
			anAbility = setFeatAddSelections(availableList, selectedList, aPC, aTok);
		}
		else if ("FEATLIST".equals(choiceType))
		{
			setFeatListSelections(obj, availableList, selectedList, aPC, aTok, stacks);
		}
		else if (choiceType.startsWith("FEAT=") || choiceType.startsWith("FEAT."))
		{
			setFeatSelections(obj, availableList, selectedList, aPC, choiceType);
		}
		else if ("FEATSELECT".equals(choiceType))
		{
			setFeatSelectSelections(obj, availableList, selectedList, aPC, aTok, stacks);
		}
		else if ("HP".equals(choiceType))
		{
			setHPSelections(obj, availableList, selectedList, aTok);
		}
		else if ("RACE".equals(choiceType))
		{
			title = "Choose Race";
			setRaceSelections(obj, availableList, selectedList, aTok);
		}
		else if ("SALIST".equals(choiceType))
		{
			// SALIST:Smite|VAR|%|1
			title = "Special Ability Choice";
			setSAListSelections(obj, availableList, selectedList, aPC, aChoiceString, aBonusList);
		}
		else if ("SCHOOLS".equals(choiceType))
		{
			title = "School Choice";
			setSchoolsSelections(obj, availableList, selectedList);
		}
		else if (
				"SKILLLIST".equals(choiceType)   ||
				"CCSKILLLIST".equals(choiceType) ||
				"NONCLASSSKILLLIST".equals(choiceType))
		{
			title = "Skill Choice";
			setSkillListSelections(
					obj, availableList, selectedList,
					aPC, aTok, rootArrayList, choiceType);
		}
		else if (
				"SKILLSNAMED".equals(choiceType)         ||
				"SKILLSNAMEDTOCSKILL".equals(choiceType) ||
				"SKILLSNAMEDTOCCSKILL".equals(choiceType))
		{
			// SKILLSNAMEDTOCSKILL --- Make one of the named skills a class skill.
			title = "Skill Choice";
			setSkillsNamedSelections(obj, availableList, selectedList, aPC, aTok);
		}
		else if ("SKILLS".equals(choiceType))
		{
			title = "Skill Choice";
			setSkillsSelections(obj, availableList, selectedList, aPC);
		}
		else if ("SPELLCLASSES".equals(choiceType))
		{
			title = "Spellcaster Classes";
			setSpellClassesSelections(obj, availableList, selectedList, aPC);
		}
		else if ("SPELLLEVEL".equals(choiceType))
		{
			setSpellLevelSelections(
					obj, availableList, selectedList,
					process, aPC, aChoiceString, uniqueList, aBonusList);
		}
		else if ("SPELLLIST".equals(choiceType))
		{
			title = "Spell Choice";
			if (process && (anAbility != null))
			{
				idxSelected = chooseAbility(anAbility);
				if (idxSelected < 0)
				{
					return false;
				}
				--idxSelected;
			}

			maxNewSelections =
				setSpellListSelections(obj, availableList, selectedList, aPC, aTok, idxSelected);
			// Set up remaining choices for pre-existing selection

			if (idxSelected >= 0)
			{
				final FeatMultipleChoice fmc = (FeatMultipleChoice) obj.getAssociatedObject(idxSelected);
				maxNewSelections    = fmc.getMaxChoices();
				requestedSelections = maxNewSelections;
			}
		}
		else if ("SPELLS".equals(choiceType))
		{
			setSpellsSelection(obj, availableList, selectedList, aTok);
		}
		else if ("STAT".equals(choiceType))
		{
			// CHOOSE:COUNT=1|STAT|Con
			title = "Stat Choice";
			setStatSelections(obj, availableList, selectedList, aTok);
		}
		else if ("WEAPONFOCUS".equals(choiceType))
		{
			title = "Weapon Focus Choice";
			setWeaponFocusSelections(obj, availableList, selectedList, aPC, aTok);
		}
		else if ("WEAPONPROFS".equals(choiceType))
		{
			title = "Weapon Prof Choice";
			setWeaponProfSelections(obj, availableList, selectedList, aPC, aTok);
		}
		else
		{
			title = "Selections";
			setMiscSelections(obj, availableList, selectedList, aTok, stacks, choiceType);
		}

		if (!process)
		{
			return false;
		}

		/* XXX Start of do the chooser bit */

		if (requestedSelections < 0)
		{
			requestedSelections = maxNewSelections;
		}
		else
		{
			requestedSelections -= selectedList.size();
			requestedSelections = Math.min(requestedSelections, maxNewSelections);
		}

		final int preSelectedSize = selectedList.size();

		if (numChoices > 0)
		{
			// Make sure that we don't try to make the user choose more selections
			// than are available or we'll be in an infinite loop...

			numChoices = Math.min(numChoices, availableList.size() - preSelectedSize);
			requestedSelections = numChoices;
		}

		boolean bNoChoice = false;
		if (availableList.size() == 1)
		{
			if ("NOCHOICE".equals(availableList.get(0).toString()))
			{
				if (!addIt)
				{
					try
					{
						selectedList.remove(0);
					}
					catch (IndexOutOfBoundsException ioobe)
					{
						// ignore
					}
				}
				else
				{
					selectedList.add("");
				}
				bNoChoice = true;
				numChoices = 0;			// Make sure we are processing only 1 selection
			}
		}

		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(false); // user is not required to make any changes
		chooser.setAllowsDups(stacks); // only stackable feats can be duped
		chooser.setVisible(false);
		chooser.setPool(requestedSelections);

		title = title + " (" + obj.getName() + ')';
		chooser.setTitle(title);
		Globals.sortChooserLists(availableList, selectedList);

		while (true)
		{
			chooser.setAvailableList(availableList);
			chooser.setSelectedList(selectedList);
			if (!bNoChoice)
			{
				chooser.setVisible(true);
			}

			final int selectedSize = chooser.getSelectedList().size() - preSelectedSize;

			if (numChoices > 0)
			{
				if (selectedSize != numChoices)
				{
					ShowMessageDelegate.showMessageDialog("You must make " +
							(numChoices - selectedSize) + " more selection(s).",
							Constants.s_APPNAME, MessageType.INFORMATION);
					continue;
				}
			}

			break;
		}

		/* XXX end of choosy bit, now deal with the choices XXX */

		if ("SPELLLIST".equals(choiceType))
		{
			if (idxSelected >= 0)
			{
				obj.removeAssociated(idxSelected);

				if (chooser.getSelectedList().size() == 0)
				{
					aPC.adjustFeats(1);
				}
			}
			else if (chooser.getSelectedList().size() != 0)
			{
				aPC.adjustFeats(-1);
			}
		}
		else if ("SALIST".equals(choiceType))
		{
			// remove previous selections from special abilities
			// aBonusList contains all possible selections in form: <displayed info>|<special ability>
			for (int e = 0; e < obj.getAssociatedCount(); ++e)
			{
				final String aString = obj.getAssociated(e);
				final String prefix = aString + "|";

				for (int x = 0; x < aBonusList.size(); ++x)
				{
					final String bString = (String) aBonusList.get(x);

					if (bString.startsWith(prefix))
					{
						obj.removeBonus(bString.substring(bString.indexOf('|') + 1), "", aPC);

						break;
					}
				}
			}
		}

		if ("SKILLSNAMEDTOCSKILL".equals(choiceType))
		{
			for (iter = anAbility.getCSkillList().iterator(); iter.hasNext();)
			{
				final String tempString = (String) iter.next();

				if (!"LIST".equals(tempString))
				{
					String tempName = obj.getName();
					final Ability tempAbility = Globals.getAbilityNamed("FEAT", tempName);

					if (tempAbility != null)
					{
						if (tempAbility.getCSkillList() != null)
						{
							if (tempAbility.getCSkillList().contains(tempString))
							{
								iter.remove();
							}
						}
					}
				}
			}

			anAbility.setCcSkillList(".CLEAR");
		}

		if (!"SPELLLIST".equals(choiceType))
		{
			obj.clearAssociated();
		}

		String objPrefix = "";

		if (obj instanceof Domain)
		{
			objPrefix = choiceType + '?';
		}

		FeatMultipleChoice fmc = null;
		if (obj instanceof Ability) {
			((Ability)obj).clearSelectedWeaponProfBonus(); //Cleans up the feat
		}

		for (i = 0; i < chooser.getSelectedList().size(); ++i)
		{
			final String chosenItem = (String) chooser.getSelectedList().get(i);

			if ("HP".equals(choiceType))
			{
				//obj.addAssociated(objPrefix + "CURRENTMAX");
				obj.addAssociated(objPrefix + chosenItem);
			}
			else if ("SPELLLEVEL".equals(choiceType))
			{
				for (Iterator e = aBonusList.iterator(); e.hasNext();)
				{
					final String bString = (String) e.next();
					obj.addAssociated(objPrefix + chosenItem);
					obj.applyBonus(bString, chosenItem, aPC);
				}
			}
			else if ("SPELLLIST".equals(choiceType))
			{
				if (fmc == null)
				{
					fmc = new FeatMultipleChoice();
					fmc.setMaxChoices(maxNewSelections);
					obj.addAssociated(fmc);
				}

				fmc.addChoice(chosenItem);
			}
			else if ("ARMORTYPE".equals(choiceType))
			{
				for (Iterator e = aBonusList.iterator(); e.hasNext();)
				{
					final String bString = (String) e.next();
					obj.addAssociated(objPrefix + chosenItem);
					obj.applyBonus("ARMORPROF=" + bString, chosenItem, aPC);
				}
			}
			else if (multiples && !stacks)
			{
				if (!obj.containsAssociated(objPrefix + chosenItem))
				{
					obj.addAssociated(objPrefix + chosenItem);
				}
			}
			else
			{
				final String prefix = chosenItem + "|";
				obj.addAssociated(objPrefix + chosenItem);

				// SALIST: aBonusList contains all possible selections in form: <displayed info>|<special ability>
				for (int x = 0; x < aBonusList.size(); ++x)
				{
					final String bString = (String) aBonusList.get(x);

					if (bString.startsWith(prefix))
					{
						obj.addBonusList(bString.substring(bString.indexOf('|') + 1));

						break;
					}
				}
			}

			if (anAbility != null)
			{
				if ("SKILLLIST".equals(choiceType) || "SKILLSNAMEDTOCSKILL".equals(choiceType)
					|| "NONCLASSSKILLLIST".equals(choiceType))
				{
					if (rootArrayList.contains(chosenItem))
					{
						for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
						{
							final Skill aSkill = (Skill) e2.next();

							if (aSkill.getRootName().equalsIgnoreCase(chosenItem))
							{
								anAbility.setCSkillList(aSkill.getName());
							}
						}
					}
					else
					{
						anAbility.setCSkillList(chosenItem);
					}
				}
				else if ("CCSKILLLIST".equals(choiceType) || "SKILLSNAMEDTOCCSKILL".equals(choiceType))
				{
					if (rootArrayList.contains(chosenItem))
					{
						for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
						{
							final Skill aSkill = (Skill) e2.next();

							if (aSkill.getRootName().equalsIgnoreCase(chosenItem))
							{
								anAbility.setCcSkillList(aSkill.getName());
							}
						}
					}
					else
					{
						anAbility.setCcSkillList(chosenItem);
					}
				}
				else if ("FEATADD".equals(choiceType))
				{
					if (!aPC.hasRealFeatNamed(chosenItem))
					{
						aPC.adjustFeats(1);
					}

					AbilityUtilities.modFeat(aPC, null, chosenItem, true, false);
				}
			}
			if (Globals.weaponTypesContains(choiceType))
			{
				aPC.addWeaponProf(objPrefix + chosenItem);
			}
		}

		if (!"SPELLLIST".equals(choiceType))
		{
			double featCount = aPC.getFeats();
			if (numChoices > 0)
			{
				if (cost > 0)
				{
					featCount -= cost;
				}
			}
			else
			{
				if (cost > 0)
				{
					featCount = ((maxSelections - selectedList.size()) * cost);
				}
			}

			aPC.adjustFeats(featCount - aPC.getFeats());
		}

		// This will get assigned by autofeat (if a feat)

		if (objPrefix.length() != 0)
		{
			aPC.setAutomaticFeatsStable(false);
		}

		return true;
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 */
	private static void setArmorTypeSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC)
	{
		String temptype;

		for (Iterator it = Globals.getAbilityKeyIterator("FEAT"); it.hasNext(); ) {
			final Ability tempAbility = (Ability) it.next();

			if (tempAbility.getName().startsWith("Armor Proficiency ("))
			{
				final int idxbegin = tempAbility.getName().indexOf("(");
				final int idxend = tempAbility.getName().indexOf(")");
				temptype = tempAbility.getName().substring((idxbegin + 1), idxend);

				if (aPC.getFeatNamed(tempAbility.getName()) != null)
				{
					availableList.add(temptype);
				}
			}
		}

		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 */
	private static void setCSkillsSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC)
	{
		Iterator iter;
		Skill aSkill;

		for (iter = Globals.getSkillList().iterator(); iter.hasNext();)
		{
			aSkill = (Skill) iter.next();

			if (aSkill.costForPCClassList(aPC.getClassList(), aPC) == Globals.getGameModeSkillCost_Class())
			{
				availableList.add(aSkill.getName());
			}
		}

		obj.addAssociatedTo(selectedList);
	}

	private static void setRaceSelections(
			final PObject   obj,
			final List      availableList,
			final List      selectedList,
			StringTokenizer aTok)
	{
		// CHOOSE:RACE|RACETYPE=x,RACESUBTYPE=y,<racename>,TYPE=z
		// or CHOOSE:RACE|[RACETYPE=x,RACESUBTYPE=y]
		Collection races = Globals.getRaceMap().values();

		while (aTok.hasMoreTokens())
		{
			String choice = aTok.nextToken();
			// All top-level comma-separated items are added to the list.
			if (choice.indexOf("[") != -1)
			{
				ArrayList raceTypes = new ArrayList();
				ArrayList raceSubTypes = new ArrayList();
				ArrayList types = new ArrayList();

				choice = choice.substring(1,choice.length()-1);
				StringTokenizer options = new StringTokenizer(choice, ",");
				while (options.hasMoreTokens())
				{
					String option = options.nextToken();
					if (option.startsWith("RACETYPE=") || option.startsWith("RACETYPE."))
					{
						raceTypes.add(option.substring(9));
					}
					else if (option.startsWith("RACESUBTYPE=") || option.startsWith("RACESUBTYPE."))
					{
						raceSubTypes.add(option.substring(12));
					}
					else if (option.startsWith("TYPE=") || option.startsWith("TYPE."))
					{
						types.add(option.substring(5));
					}
				}
				for (Iterator i = races.iterator(); i.hasNext(); )
				{
					Race race = (Race) i.next();
					if (checkRace(race, raceTypes, raceSubTypes, types))
					{
						availableList.add(race.getName());
					}
				}
			}
			if (choice.startsWith("RACETYPE=") || choice.startsWith("RACETYPE."))
			{
				// Add all races matching this racetype
				for (Iterator i = races.iterator(); i.hasNext(); )
				{
					Race race = (Race) i.next();
					if (race.getRaceType().equals(choice.substring(9)))
					{
						availableList.add(race.getName());
					}
				}
			}
			else if (choice.startsWith("RACESUBTYPE=") || choice.startsWith("RACESUBTYPE."))
			{
				// Add all races matching this racetype
				for (Iterator i = races.iterator(); i.hasNext(); )
				{
					Race race = (Race) i.next();
					if (race.getRacialSubTypes().contains(choice.substring(9)))
					{
						availableList.add(race.getName());
					}
				}
			}
			else if (choice.startsWith("TYPE=") || choice.startsWith("TYPE."))
			{
				// Add all races matching this racetype
				for (Iterator i = races.iterator(); i.hasNext(); )
				{
					Race race = (Race) i.next();
					if (race.getType().equals(choice.substring(5)))
					{
						availableList.add(race.getName());
					}
				}
			}
			else
			{
				Race race = Globals.getRaceNamed(choice);
				if (race != null)
				{
					availableList.add(race.getName());
				}
			}
		}
	}

	private static boolean checkRace(Race race, List raceTypes, List raceSubTypes, List types)
	{
		for (Iterator i = raceTypes.iterator(); i.hasNext(); )
		{
			String raceType = (String)i.next();
			if (!race.getRaceType().equals(raceType))
			{
				return false;
			}
		}
		for (Iterator i = raceSubTypes.iterator(); i.hasNext(); )
		{
			String raceSubType = (String)i.next();
			if (!race.getRacialSubTypes().contains(raceSubType))
			{
				return false;
			}
		}
		for (Iterator i = types.iterator(); i.hasNext(); )
		{
			String rType = (String)i.next();
			if (!race.getType().equals(rType))
			{
				return false;
			}
		}
		return true;
	}

	private static void setDomainSelections(
			final PObject   obj,
			final List      availableList,
			final List      selectedList,
			final PlayerCharacter aPC,
			StringTokenizer aTok)
	{
		while (aTok.hasMoreTokens())
		{
			String option = aTok.nextToken();
			if ("ANY".equals(option))
			{
				// returns a list of all loaded Domains.
				List domains = Globals.getDomainList();
				for (Iterator i = domains.iterator(); i.hasNext(); )
				{
					Domain domain = (Domain)i.next();
					availableList.add(domain.getName());
				}
				break;
			}
			else if ("QUALIFY".equals(option))
			{
				// returns a list of loaded Domains the PC qualifies for
				// but does not have.
				List allDomains = Globals.getDomainList();
				for (Iterator i = allDomains.iterator(); i.hasNext(); )
				{
					Domain domain = (Domain)i.next();
					if (domain.qualifiesForDomain(aPC))
					{
						boolean found = false;
						List pcDomainList = aPC.getCharacterDomainList();
						for (Iterator j = pcDomainList.iterator(); j.hasNext();)
						{
							CharacterDomain cd = (CharacterDomain)j.next();
							if (domain.equals(cd.getDomain()))
							{
								found = true;
								break;
							}
						}
						if (found == false)
						{
							availableList.add(domain.getName());
						}
					}
				}
				break;
			}
			else if ("PC".equals(option))
			{
				// returns a list of all domains a character actually has.
				List pcDomainList = aPC.getCharacterDomainList();
				for (Iterator i = pcDomainList.iterator(); i.hasNext();)
				{
					CharacterDomain cd = (CharacterDomain)i.next();
					availableList.add(cd.getDomain().getName());
				}
				break;
			}
			else if (option.startsWith("DEITY"))
			{
				// returns a list of Domains granted by specified Diety.
				String deityName = option.substring(6);
				Deity deity = Globals.getDeityNamed(deityName);
				if (deity != null)
				{
					List domainList = deity.getDomainList();
					for (Iterator i = domainList.iterator(); i.hasNext();)
					{
						Domain domain = (Domain)i.next();
						availableList.add(domain.getName());
					}
				}
				break;
			}
			else
			{
				// returns a list of the specified domains.
				Domain domain = Globals.getDomainNamed(option);
				if (domain != null)
				{
					availableList.add(option);
				}
			}
		}
		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aTok
	 */
	private static void setEquipTypeSelections(
			final PObject   obj,
			final List      availableList,
			final List      selectedList,
			StringTokenizer aTok)
	{
		String choiceSec = (aTok.hasMoreTokens())
				? aTok.nextToken()
				: obj.getName();

		availableList.addAll(EquipmentList.getEquipmentOfType(choiceSec, ""));
		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 * @param aTok
	 * @return anAbility
	 */
	private static Ability setFeatAddSelections(
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC,
			final StringTokenizer aTok)
	{
		Ability anAbility = null;
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();

			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				final String featType = aString.substring(5);

				for (Iterator it = Globals.getAbilityKeyIterator("FEAT"); it.hasNext(); )
				{
					final Ability ability = (Ability) it.next();

					if (
						ability.isType(featType) &&
						aPC.canSelectAbility(ability) &&
						!availableList.contains(ability.getName())
					   ) {

						availableList.add(ability.getName());
					}
				}
			}

			else
			{
				final StringTokenizer bTok = new StringTokenizer(aString, ",");
				String featName = bTok.nextToken().trim();
				String subName = "";
				anAbility = Globals.getAbilityNamed("FEAT", featName);

				if (anAbility == null)
				{
					Logging.errorPrint("Feat not found: " + featName);

					//return false;
				}

				if (!featName.equalsIgnoreCase(anAbility.getName()))
				{
					subName = featName.substring(anAbility.getName().length());
					featName = anAbility.getName();

					final int si = subName.indexOf('(');

					if (si > -1)
					{
						subName = subName.substring(si + 1);
					}
				}

				if (PrereqHandler.passesAll(anAbility.getPreReqList(), aPC, anAbility))
				{
					if (anAbility.isMultiples())
					{
						//
						// If already have taken the feat, use it so we can remove
						// any choices already selected
						//
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

						final List aavailableList = new ArrayList(); // available list of choices
						final List sselectedList = new ArrayList(); // selected list of choices
						anAbility.modChoices(aPC, true, availableList, selectedList, false);

						//
						// Remove any that don't match
						//
						if (subName.length() != 0)
						{
							for (int n = aavailableList.size() - 1; n >= 0; --n)
							{
								final String bString = (String) aavailableList.get(n);

								if (!bString.startsWith(subName))
								{
									aavailableList.remove(n);
								}
							}

							//
							// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving)))
							// If you have no ranks in Craft (Basketweaving), the available list will be empty
							//
							// Make sure that the specified feat is available, even though it does not meet the prerequisite
							//
							if ((percIdx == -1) && (aavailableList.size() == 0))
							{
								aavailableList.add(subName);
							}
						}

						//
						// Remove any already selected
						//
						if (!anAbility.isStacks())
						{
							for (Iterator e = sselectedList.iterator(); e.hasNext();)
							{
								final int idx = aavailableList.indexOf(e.next().toString());

								if (idx > -1)
								{
									aavailableList.remove(idx);
								}
							}
						}

						for (Iterator e = aavailableList.iterator(); e.hasNext();)
						{
							availableList.add(featName + "(" + (String) e.next() + ")");
						}

						//return false;
					}
					else if (!aPC.hasRealFeatNamed(featName) && !aPC.hasFeatAutomatic(featName))
					{
						availableList.add(aString);
					}
				}
			}
		}
		return anAbility;
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 * @param aTok
	 * @param stacks
	 */
	private static void setFeatListSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC,
			StringTokenizer       aTok,
			final boolean         stacks)
	{
		String aString;

		while (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();

			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				aString = aString.substring(5);

				if (!stacks && availableList.contains(aString))
				{
					continue;
				}

				for (Iterator e1 = aPC.aggregateFeatList().iterator(); e1.hasNext();)
				{
					final Ability theFeat = (Ability) e1.next();

					if (theFeat.isType(aString)
						&& (stacks || (!stacks && !availableList.contains(theFeat.getName()))))
					{
						availableList.add(theFeat.getName());
					}
				}
			}
			else if (aPC.getFeatNamed(aString) != null)
			{
				if (stacks || (!stacks && !availableList.contains(aString)))
				{
					availableList.add(aString);
				}
			}
		}
		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 * @param choiceType
	 */
	private static void setFeatSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC,
			String                choiceType)
	{
		final Ability theFeat = aPC.getFeatNamed(choiceType.substring(5));

		if (theFeat != null)
		{
			theFeat.addAssociatedTo(availableList);
		}

		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 * @param aTok
	 * @param stacks
	 */
	private static void setFeatSelectSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC,
			StringTokenizer       aTok,
			final boolean         stacks)
	{
		obj.addAssociatedTo(selectedList);

		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();

			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
			{
				aString = aString.substring(5);

				if (!stacks && availableList.contains(aString))
				{
					continue;
				}

				for (Iterator it = Globals.getAbilityKeyIterator("FEAT"); it.hasNext(); ) {
					final Ability ability = (Ability) it.next();

					if (ability.isType(aString) &&
							(stacks || !availableList.contains(ability.getName()
							  )))

					{
						availableList.add(ability.getName());
					}
				}
			}
			else
			{
				Ability theAbility = Globals.getAbilityNamed("ALL", aString);

				if (theAbility != null)
				{
					String subName = "";

					if (!aString.equalsIgnoreCase(theAbility.getName()))
					{
						subName = aString.substring(theAbility.getName().length());
						aString = theAbility.getName();

						final int idx = subName.indexOf('(');

						if (idx > -1)
						{
							subName = subName.substring(idx + 1);
						}
					}

					if (theAbility.isMultiples())
					{
						//
						// If already have taken the feat, use it so we can remove
						// any choices already selected
						//
						final Ability pcFeat = aPC.getFeatNamed(aString);

						if (pcFeat != null)
						{
							theAbility = pcFeat;
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

						final List xavailableList = new ArrayList(); // available list of choices
						final List xselectedList = new ArrayList(); // selected list of choices
						theAbility.modChoices(aPC, true, xavailableList, xselectedList, false);

						//
						// Remove any that don't match
						//
						if (subName.length() != 0)
						{
							for (int n = xavailableList.size() - 1; n >= 0; --n)
							{
								final String xString = (String) xavailableList.get(n);

								if (!xString.startsWith(subName))
								{
									xavailableList.remove(n);
								}
							}

							//
							// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving)))
							// If you have no ranks in Craft (Basketweaving), the available list will be empty
							//
							// Make sure that the specified feat is available, even though it does not meet the prerequisite
							//
							if ((percIdx == -1) && (xavailableList.size() == 0))
							{
								xavailableList.add(aString + "(" + subName + ")");
							}
						}

						//
						// Remove any already selected
						//
						if (!theAbility.isStacks())
						{
							for (Iterator e = xselectedList.iterator(); e.hasNext();)
							{
								final int idx = xavailableList.indexOf(e.next().toString());

								if (idx > -1)
								{
									xavailableList.remove(idx);
								}
							}
						}

						for (Iterator e = xavailableList.iterator(); e.hasNext();)
						{
							availableList.add(aString + "(" + (String) e.next() + ")");
						}
					}
					else
					{
						availableList.add(aString);
					}
				}
			}
		}
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aTok
	 */
	private static void setHPSelections(
			final PObject   obj,
			final List      availableList,
			final List      selectedList,
			StringTokenizer aTok)
	{
		String choiceSec = (aTok.hasMoreTokens())
				? aTok.nextToken()
				: obj.getName();

		availableList.add(choiceSec);

		for (int e1 = 0; e1 < obj.getAssociatedCount(); ++e1)
		{
			selectedList.add(choiceSec);
		}
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aTok
	 * @param stacks
	 * @param choiceType
	 */
	private static void setMiscSelections(
			final PObject   obj,
			final List      availableList,
			final List      selectedList,
			StringTokenizer aTok,
			boolean         stacks,
			final String    choiceType)
	{
		availableList.add(choiceType);

		String aString;

		while (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();

			if (stacks || (!stacks && !availableList.contains(aString)))
			{
				availableList.add(aString);
			}
		}

		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 * @param aChoiceString
	 * @param aBonusList
	 */
	private static void setSAListSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC,
			final String          aChoiceString,
			final List            aBonusList)
	{
		PCGIOHandler.buildSALIST(aChoiceString, availableList, aBonusList, aPC);
		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 */
	private static void setSchoolsSelections(
			final PObject obj,
			final List availableList,
			final List selectedList)
	{
		availableList.addAll(SettingsHandler.getGame().getUnmodifiableSchoolsList());
		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 * @param aTok
	 * @param rootArrayList
	 * @param choiceType
	 */
	private static void setSkillListSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC,
			StringTokenizer       aTok,
			final List            rootArrayList,
			final String          choiceType)
	{
		Iterator iter;

		final String choiceSec = (aTok.hasMoreTokens())
				? aTok.nextToken()
				: obj.getName();

		if ((choiceSec.length() > 0) && !"LIST".equals(choiceSec))
		{
			aTok = new StringTokenizer(choiceSec, ",");

			while (aTok.hasMoreTokens())
			{
				availableList.add(aTok.nextToken());
			}
		}
		else // if it was LIST
		{
			Skill aSkill;

			for (iter = Globals.getSkillList().iterator(); iter.hasNext();)
			{
				aSkill = (Skill) iter.next();

				if ("NONCLASSSKILLLIST".equals(choiceType)
					&& ((aSkill.costForPCClassList(aPC.getClassList(), aPC) == Globals.getGameModeSkillCost_Class()) || aSkill.isExclusive()))
				{
					continue; // builds a list of Cross class skills
				}

				final int rootNameLength = aSkill.getRootName().length();

				if ((rootNameLength == 0) || aSkill.getRootName().equals(aSkill.getName())) //all skills have ROOTs now, so go ahead and add it if the name and root are identical
				{
					availableList.add(aSkill.getName());
				}

				final boolean rootArrayContainsRootName = rootArrayList.contains(aSkill.getRootName());

				if ((rootNameLength > 0) && !rootArrayContainsRootName)
				{
					rootArrayList.add(aSkill.getRootName());
				}

				if ((rootNameLength > 0) && rootArrayContainsRootName)
				{
					availableList.add(aSkill.getName());
				}
			}
		}

		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 * @param aTok
	 */
	private static void setSkillsNamedSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC,
			StringTokenizer       aTok)
	{
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			boolean startsWith = false;

			if (aString.startsWith("TYPE.") || aString.startsWith("TYPE="))
			{
				Skill aSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();

					if (aSkill.isType(aString.substring(5)))
					{
						availableList.add(aSkill.getName());
					}
				}
			}

			if ("ALL".equals(aString))
			{
				Skill aSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();
					availableList.add(aSkill.getName());
				}
			}

			if ("CLASS".equals(aString))
			{
				Skill aSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();

					if (aSkill.costForPCClassList(aPC.getClassList(), aPC) == Globals.getGameModeSkillCost_Class())
					{
						availableList.add(aSkill.getName());
					}
				}
			}

			if ("CROSSCLASS".equals(aString))
			{
				Skill aSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();

					if (aSkill.costForPCClassList(aPC.getClassList(), aPC) > Globals.getGameModeSkillCost_Class())
					{
						availableList.add(aSkill.getName());
					}
				}
			}

			if ("EXCLUSIVE".equals(aString))
			{
				Skill aSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();

					if (aSkill.costForPCClassList(aPC.getClassList(), aPC) == Globals.getGameModeSkillCost_Exclusive())
					{
						availableList.add(aSkill.getName());
					}
				}
			}

			if ("NORANK".equals(aString))
			{
				Skill aSkill;
				Skill pcSkill;

				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();
					pcSkill = aPC.getSkillNamed(aSkill.getName());

					if (pcSkill == null || Double.compare(pcSkill.getRank().doubleValue(), 0.0) == 0)
					{
						availableList.add(aSkill.getName());
					}
				}
			}

			if (aString.endsWith("%"))
			{
				startsWith = true;
				aString = aString.substring(0, aString.length() - 1);
			}

			Skill aSkill;

			for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
			{
				aSkill = (Skill) e1.next();

				if (aSkill.getKeyName().equals(aString) || (startsWith && aSkill.getKeyName().startsWith(aString)))
				{
					availableList.add(aSkill.getName());
				}
			}
		}

		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 */
	private static void setSkillsSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC)
	{
		Iterator iter;
		for (iter = aPC.getSkillList().iterator(); iter.hasNext();)
		{
			final Skill aSkill = (Skill) iter.next();
			availableList.add(aSkill.getName());
		}

		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 */
	private static void setSpellClassesSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC)
	{
		Iterator iter;
		PCClass aClass;

		for (iter = aPC.getClassList().iterator(); iter.hasNext();)
		{
			aClass = (PCClass) iter.next();

			if (!aClass.getSpellBaseStat().equals(Constants.s_NONE))
			{
				availableList.add(aClass.getName());
			}
		}

		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param process
	 * @param aPC
	 * @param aChoiceString
	 * @param uniqueList
	 * @param aBonusList
	 */
	private static void setSpellLevelSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final boolean         process,
			final PlayerCharacter aPC,
			final String          aChoiceString,
			final List            uniqueList,
			final List            aBonusList)
	{
		// this will need to be re-worked at some point when I can think
		// of a better way.  This feat is different from the others in that
		// it requires a bonus to be embedded in the choice.  Probably this
		// whole feat methodology needs to be re-thought as its getting a bit
		// bloated - a generic way to embed bonuses could be done to simplify
		// this all tremendously instead of so many special cases.
		final StringTokenizer cTok = new StringTokenizer(aChoiceString, "[]");
		final String choices = cTok.nextToken();

		while (cTok.hasMoreTokens())
		{
			aBonusList.add(cTok.nextToken());
		}

		final StringTokenizer choicesTok = new StringTokenizer(choices, "|");

		// get appropriate choices for chooser
		ChooserUtilities.buildSpellTypeChoices(availableList, uniqueList, aPC, choicesTok);
		obj.addAssociatedTo(selectedList);

		if (!process)
		{
			availableList.clear();
			availableList.addAll(aBonusList);
		}
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 * @param aTok
	 * @param idxSelected
	 * @return maxNewSelections
	 */
	private static int setSpellListSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC,
			StringTokenizer       aTok,
			final int             idxSelected)
	{
		int maxNewSelections = 0;
		int i;
		Iterator iter;
		final boolean needSpellbook;

		switch (aTok.nextToken().charAt(0))
		{
			case '1':
			case 'Y':
				needSpellbook = true;

				break;

			default:
				needSpellbook = false;

				break;
		}

		PObject aClass;
		List classes = null;

		for (int j = 0; ; ++j)
		{
			aClass = aPC.getSpellClassAtIndex(j);

			if (aClass == null)
			{
				break;
			}

			if ((aClass instanceof PCClass) && (((PCClass) aClass).getSpellBookUsed() == needSpellbook))
			{
				if (classes == null)
				{
					classes = new ArrayList();
				}

				classes.add(aClass);
			}
		}

		// Add all spells from all classes that match the spellbook
		// requirement.  Allow the number of selections to be the
		// maximum allowed by the classes' spell base stat

		if (classes != null)
		{
			maxNewSelections = 0;

			for (int j = 0; j < classes.size(); ++j)
			{
				aClass = (PObject) classes.get(j);

				final List aList = aClass.getSpellSupport().getCharacterSpell(null, Globals.getDefaultSpellBook(), -1);

				for (iter = aList.iterator(); iter.hasNext();)
				{
					final CharacterSpell cs = (CharacterSpell) iter.next();
					final Spell aSpell = cs.getSpell();

					if (!obj.containsAssociated(aSpell.getKeyName()))
					{
						if (!availableList.contains(aSpell.getName()))
						{
							availableList.add(aSpell.getName());
						}
					}
				}

				i = aPC.getStatList().getStatModFor(((PCClass) aClass).getSpellBaseStat());

				if (i > maxNewSelections)
				{
					maxNewSelections = i;
				}
			}

			// Remove all previously selected items from the available list

			final List assocList = obj.getAssociatedList();

			if (assocList != null)
			{
				for (int j = 0; j < assocList.size(); ++j)
				{
					final FeatMultipleChoice fmc = (FeatMultipleChoice) assocList.get(j);
					final List choices = fmc.getChoices();

					if (choices != null)
					{
						for (int k = 0; k < choices.size(); ++k)
						{
							if (j == idxSelected)
							{
								selectedList.add(choices.get(k));
							}
							else
							{
								availableList.remove(choices.get(k));
							}
						}
					}
				}
			}
		}
		return maxNewSelections;
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aTok
	 */
	private static void setSpellsSelection(
			final PObject   obj,
			final List      availableList,
			final List      selectedList,
			StringTokenizer aTok)
	{
		while (aTok.hasMoreTokens())
		{
			final String line = aTok.nextToken();
			String domainName = "";
			String className = "";

			if (line.startsWith("DOMAIN=") || line.startsWith("DOMAIN."))
			{
				domainName = line.substring(7);
			}
			else if (line.startsWith("CLASS=") || line.startsWith("CLASS."))
			{
				className = line.substring(6);
			}

			// 20 level cap XXX
			for (int lvl = 0; lvl < 20; ++lvl)
			{
				final List aList = Globals.getSpellsIn(lvl, className, domainName);
				availableList.addAll(aList);
			}
		}
		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aTok
	 */
	private static void setStatSelections(
			final PObject   obj,
			final List      availableList,
			final List      selectedList,
			StringTokenizer aTok)
	{
		final List excludeList = new ArrayList();

		while (aTok.hasMoreTokens())
		{
			final String sExclude = aTok.nextToken();
			final int iStat = SettingsHandler.getGame().getStatFromAbbrev(sExclude);

			if (iStat >= 0)
			{
				excludeList.add(SettingsHandler.getGame().s_ATTRIBSHORT[iStat]);
			}
		}

		for (int x = 0; x < SettingsHandler.getGame().s_ATTRIBSHORT.length; ++x)
		{
			if (!excludeList.contains(SettingsHandler.getGame().s_ATTRIBSHORT[x]))
			{
				availableList.add(SettingsHandler.getGame().s_ATTRIBSHORT[x]);
			}
		}

		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 * @param aTok
	 */
	private static void setWeaponFocusSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC,
			StringTokenizer       aTok)
	{
		final Ability wfFeat = aPC.getFeatNamed("Weapon Focus");

		if (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();

			if (aString.startsWith("TYPE."))
			{
				final List   aList = wfFeat.getAssociatedList();
				final String aType = aString.substring(5);

				for (Iterator e = aList.iterator(); e.hasNext();)
				{
					final Object aObj = e.next();
					final WeaponProf wp;
					wp = Globals.getWeaponProfNamed(aObj.toString());

					if (wp == null)
					{
						continue;
					}

					final Equipment eq;
					eq = EquipmentList.getEquipmentKeyed(wp.getKeyName());

					if (eq == null)
					{
						continue;
					}

					if (eq.isType(aType))
					{
						availableList.add(aObj);
					}
				}
			}
		}
		else
		{
			wfFeat.addAssociatedTo(availableList);
		}

		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 * @param aTok
	 */
	private static void setWeaponProfSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC,
			StringTokenizer       aTok)
	{
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();

			if ("LIST".equals(aString))
			{
				String bString;

				for (Iterator setIter = aPC.getWeaponProfList().iterator(); setIter.hasNext();)
				{
					bString = (String) setIter.next();

					if (!availableList.contains(bString))
					{
						availableList.add(bString);
					}
				}
			}
			else if (aString.equals("DEITYWEAPON"))
			{
				if (aPC.getDeity() != null)
				{
					String weaponList = aPC.getDeity().getFavoredWeapon();

					if ("ALL".equalsIgnoreCase(weaponList) || "ANY".equalsIgnoreCase(weaponList))
					{
						weaponList = Globals.getWeaponProfNames("|", false);
					}

					final StringTokenizer bTok = new StringTokenizer(weaponList, "|");

					while (bTok.hasMoreTokens())
					{
						final String bString = bTok.nextToken();
						availableList.add(bString);
					}
				}
			}
			else if (aString.startsWith("Size."))
			{
				if ((aPC.sizeInt() >= Globals.sizeInt(aString.substring(5, 6)))
					&& aPC.getWeaponProfList().contains(aString.substring(7))
					&& !availableList.contains(aString.substring(7)))
				{
					availableList.add(aString.substring(7));
				}
			}
			else if (aString.startsWith("WSize."))
			{
				String bString;
				WeaponProf wp;
				final StringTokenizer bTok = new StringTokenizer(aString, ".");
				bTok.nextToken(); // should be WSize

				final String sString = bTok.nextToken(); // should be Light, 1 handed, 2 handed choices above
				final List typeList = new ArrayList();

				while (bTok.hasMoreTokens()) // any additional constraints
				{
					final String dString = bTok.nextToken().toUpperCase();
					typeList.add(dString);
				}

				for (Iterator setIter = aPC.getWeaponProfList().iterator(); setIter.hasNext();)
				{
					bString = (String) setIter.next();
					wp = Globals.getWeaponProfNamed(bString);

					if (wp == null)
					{
						continue;
					}

					//
					// get an Equipment object based on the named WeaponProf
					//
					Equipment eq = EquipmentList.getEquipmentNamed(wp.getName());

					if (eq == null)
					{
						//
						// Sword (Bastard/Exotic), Sword (Bastard/Martial), Katana (Martial), Katana(Exotic)
						//
						int len = 0;

						if (bString.endsWith("Exotic)"))
						{
							len = 7;
						}

						if ((len == 0) && bString.endsWith("Martial)"))
						{
							len = 8;
						}

						if (len != 0)
						{
							if (bString.charAt(bString.length() - len - 1) == '/')
							{
								++len;
							}

							String tempString = bString.substring(0, bString.length() - len) + ")";

							if (tempString.endsWith("()"))
							{
								tempString = tempString.substring(0, tempString.length() - 3).trim();
							}

							eq = EquipmentList.getEquipmentNamed(tempString);
						}
						else
						{
							//
							// Couldn't find equipment with matching name, look for 1st weapon that uses it
							//
							for (Iterator eqIter = EquipmentList.getEquipmentListIterator(); eqIter.hasNext(); )
							{
								final Map.Entry entry = (Map.Entry)eqIter.next();
								final Equipment tempEq = (Equipment) entry.getValue();

								if (tempEq.isWeapon())
								{
									if (tempEq.profName(aPC).equals(wp.getName()))
									{
										eq = tempEq;

										break;
									}
								}
							}
						}
					}

					boolean isValid = false; // assume we match unless...

					if (eq != null)
					{
						if (typeList.size() == 0)
						{
							isValid = true;
						}
						else
						{
							//
							// search all the optional type strings, just one match passes the test
							//
							for (Iterator wpi = typeList.iterator(); wpi.hasNext();)
							{
								final String wpString = (String) wpi.next();

								if (eq.isType(wpString))
								{
									isValid = true; // if it contains even one of the TYPE strings, it passes

									break;
								}
							}
						}
					}

					if (!isValid)
					{
						continue;
					}

					if (!availableList.contains(bString))
					{
						if ("Light".equals(sString) && Globals.isWeaponLightForPC(aPC, eq))
						{
							availableList.add(bString);
						}

						if ("1 handed".equals(sString) && Globals.isWeaponOneHanded(aPC, eq, wp))
						{
							availableList.add(bString);
						}

						if ("2 handed".equals(sString) && Globals.isWeaponTwoHanded(aPC, eq, wp))
						{
							availableList.add(bString);
						}
					}
				}
			}
			else if (aString.startsWith("SpellCaster."))
			{
				if (aPC.isSpellCaster(1) && !availableList.contains(aString.substring(12)))
				{
					availableList.add(aString.substring(12));
				}
			}
			else if (aString.startsWith("ADD."))
			{
				if (!availableList.contains(aString.substring(4)))
				{
					availableList.add(aString.substring(4));
				}
			}
			else if (aString.startsWith("TYPE.") || aString.startsWith("TYPE="))
			{
				String sString = aString.substring(5);
				boolean adding = true;
				Iterator setIter = aPC.getWeaponProfList().iterator();

				if (sString.startsWith("Not."))
				{
					sString = sString.substring(4);
					setIter = availableList.iterator();
					adding = false;
				}

				String bString;
				WeaponProf wp;
				Equipment eq;

				while (setIter.hasNext())
				{
					bString = (String) setIter.next();
					wp = Globals.getWeaponProfNamed(bString);

					if (wp == null)
					{
						continue;
					}

					eq = EquipmentList.getEquipmentKeyed(wp.getKeyName());

					if (eq == null)
					{
						if (!wp.isType("Natural")) //natural weapons are not in the global eq.list
						{
							continue;
						}

						if (adding && !availableList.contains(wp.getName()))
						{
							availableList.add(wp.getName());
						}
					}
					else if (eq.typeStringContains(sString))
					{
						// if this item is of the desired type, add it to the list
						if (adding && !availableList.contains(wp.getName()))
						{
							availableList.add(wp.getName());
						}

						// or try to remove it and reset the iterator since remove cause fits
						else if (!adding && availableList.contains(wp.getName()))
						{
							availableList.remove(wp.getName());
							setIter = availableList.iterator();
						}
					}
					else if (sString.equalsIgnoreCase("LIGHT"))
					{
						// if this item is of the desired type, add it to the list
						if (adding && !availableList.contains(wp.getName()) && Globals.isWeaponLightForPC(aPC, eq))
						{
							availableList.add(wp.getName());
						}
						// or try to remove it and reset the iterator since remove cause fits
						else if (!adding && availableList.contains(wp.getName()) && Globals.isWeaponLightForPC(aPC, eq))
						{
							availableList.remove(wp.getName());
							setIter = availableList.iterator();
						}
					}
				}
			}
			else
			{
				if (aPC.getWeaponProfList().contains(aString) && !availableList.contains(aString))
				{
					availableList.add(aString);
				}
			}
		}

		obj.addAssociatedTo(selectedList);
	}

	/**
	 * @param obj
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 * @param choiceType
	 */
	private static void setWeaponProfTypeSelections(
			final PObject         obj,
			final List            availableList,
			final List            selectedList,
			final PlayerCharacter aPC,
			final String          choiceType)
	{
		Iterator iter;
		final List tArrayList = Globals.getWeaponProfs(choiceType, aPC);
		WeaponProf tempProf;

		for (iter = tArrayList.iterator(); iter.hasNext();)
		{
			tempProf = (WeaponProf) iter.next();
			availableList.add(tempProf.getName());
		}

		obj.addAssociatedTo(selectedList);
	}
}
