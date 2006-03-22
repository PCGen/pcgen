package pcgen.core.chooser;

import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

import java.util.Iterator;
import java.util.List;

/**
 * This is the chooser that deals with choosing from among a set 
 * of supplied strings.
 */
public class MiscChoiceManager extends AbstractComplexChoiceManager {

	/**
	 * Make a new Miscellaneous chooser.  This is the chooser that deals
	 * with choosing from among a set of supplied strings.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public MiscChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);

		chooserHandled = "MISC";
	}

	/**
	 * Apply the choices selected to the associated PObject (the one passed
	 * to the constructor)
	 * @param aPC
	 * @param selected
	 * @param selectedBonusList	unused parameter
	 *
	 */
	public void applyChoices(
			PlayerCharacter  aPC,
			List             selected,
			List             selectedBonusList)
	{
		String objPrefix = (pobject instanceof Domain)
				? chooserHandled + '?'
				: "";

		if (pobject instanceof Ability)
		{
		    ((Ability)pobject).clearSelectedWeaponProfBonus(); //Cleans up the feat
		}

		Iterator it = selected.iterator();
		while (it.hasNext())
		{
			final String chosenItem = (String) it.next();
			final String name       = objPrefix + chosenItem;

			if (!multiples || dupsAllowed || !pobject.containsAssociated(name))
			{
				pobject.addAssociated(name);
			}
		}

		double featCount = aPC.getFeats();

		if (cost > 0)
		{
			featCount = (numberOfChoices > 0)
					? featCount - cost
					: ((maxSelections - selected.size()) * cost);
		}

		aPC.adjustFeats(featCount - aPC.getFeats());

		if (objPrefix.length() != 0)
		{
			aPC.setAutomaticFeatsStable(false);
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public void getChoices(
			PlayerCharacter aPc,
			List            availableList,
			List            selectedList)
	{
		Iterator it = choices.iterator();
		while (it.hasNext())
		{
			final String aString = (String) it.next();

			if (dupsAllowed || !availableList.contains(aString))
			{
				availableList.add(aString);
			}
		}
		pobject.addAssociatedTo(selectedList);
	}

}
