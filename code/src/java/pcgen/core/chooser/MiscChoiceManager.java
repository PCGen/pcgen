package pcgen.core.chooser;

import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.chooser.ChooserInterface;

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

		choiceString   = pobject.getChoiceString();
		chooserHandled = "MISC";
	}

	/**
	 * Apply the choices selected to the associated PObject (the one passed
	 * to the constructor)
	 * @param aPC
	 * @param chooser
	 * @param selectedBonusList	unused parameter
	 *
	 */
	protected void applyChoices(
			PlayerCharacter  aPC,
			ChooserInterface chooser,
			List             selectedBonusList)
	{
		String objPrefix = (pobject instanceof Domain)
				? chooserHandled + '?'
				: "";

		if (pobject instanceof Ability)
		{
		    ((Ability)pobject).clearSelectedWeaponProfBonus(); //Cleans up the feat
		}

		Iterator it = chooser.getSelectedList().iterator();
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
					: ((maxSelections - chooser.getSelectedList().size()) * cost);
		}

		aPC.adjustFeats(featCount - aPC.getFeats());

		if (objPrefix.length() != 0)
		{
			aPC.setAutomaticFeatsStable(false);
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 *
	 * @param availableList
	 * @param selectedList
	 * @param aPC
	 */
	public void getChoices(
			List            availableList,
			List            selectedList,
			PlayerCharacter aPC)
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
