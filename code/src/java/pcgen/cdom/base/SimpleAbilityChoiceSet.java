/**
 * 
 */
package pcgen.cdom.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;

public class SimpleAbilityChoiceSet implements SelectableSet<AbilitySelection>
{

	/**
	 * The PrimitiveChoiceSet containing the Collection of Objects in this
	 * ChoiceSet
	 */
	private final PrimitiveChoiceSet<Ability> pcs;

	/**
	 * The name of this ChoiceSet
	 */
	private final String setName;

	/**
	 * The title (presented to the user) of this ChoiceSet
	 */
	private String title = null;

	private final Category<Ability> category;

	/**
	 * Creates a new ChoiceSet with the given name and given underlying
	 * PrimitiveChoiceSet.
	 * 
	 * @param name
	 *            The name of this ChoiceSet
	 * @param choice
	 *            The PrimitiveChoiceSet indicating the Collection of objects
	 *            for this ChoiceSet
	 * @throws IllegalArgumentException
	 *             if the given name or PrimitiveChoiceSet is null
	 */
	public SimpleAbilityChoiceSet(String name, Category<Ability> cat,
			PrimitiveChoiceSet<Ability> choice)
	{
		category = cat;
		if (choice == null)
		{
			throw new IllegalArgumentException(
					"PrimitiveChoiceSet cannot be null");
		}
		if (name == null)
		{
			throw new IllegalArgumentException("Name cannot be null");
		}
		pcs = choice;
		setName = name;
	}

	public Category<Ability> getCategory()
	{
		return category;
	}

	public Nature getNature()
	{
		return Nature.NORMAL;
	}

	/**
	 * Returns a representation of this ChoiceSet, suitable for storing in an
	 * LST file.
	 */
	public String getLSTformat()
	{
		return pcs.getLSTformat(false);
	}

	/**
	 * Returns the Class contained within this ChoiceSet
	 * 
	 * @return the Class contained within this ChoiceSet
	 */
	public Class<AbilitySelection> getChoiceClass()
	{
		return AbilitySelection.class;
	}

	/**
	 * Returns a Set of objects contained within this ChoiceSet for the given
	 * PlayerCharacter.
	 * 
	 * @param pc
	 *            The PlayerCharacter for which the choices in this ChoiceSet
	 *            should be returned.
	 * @return a Set of objects contained within this ChoiceSet for the given
	 *         PlayerCharacter.
	 */
	public Collection<AbilitySelection> getSet(PlayerCharacter pc)
	{
		Collection<Ability> ab = pcs.getSet(pc);
		List<AbilitySelection> list = new ArrayList<AbilitySelection>(ab.size());
		for (Ability a : ab)
		{
			list.add(new AbilitySelection(a, Nature.NORMAL));
		}
		return list;
	}

	/**
	 * Returns the name of this ChoiceSet. Note that this name is suitable for
	 * display, but it does not represent information that should be stored in a
	 * persistent state (it is not sufficient information to reconstruct this
	 * ChoiceSet)
	 * 
	 * @return The name of this ChoiceSet
	 */
	public String getName()
	{
		return setName;
	}

	/**
	 * Sets the title of this ChoiceSet. Note that this should be the name that
	 * is displayed to the user when a selection from this ChoiceSet is made,
	 * but it does not represent information that should be stored in a
	 * persistent state (it is not sufficient information to reconstruct this
	 * ChoiceSet)
	 * 
	 * @return The title of this ChoiceSet
	 */
	public void setTitle(String choiceTitle)
	{
		title = choiceTitle;
	}

	/**
	 * Returns the title of this ChoiceSet. Note that this should be the name
	 * that is displayed to the user when a selection from this ChoiceSet is
	 * made, but it does not represent information that should be stored in a
	 * persistent state (it is not sufficient information to reconstruct this
	 * ChoiceSet)
	 * 
	 * @return The title of this ChoiceSet
	 */
	public String getTitle()
	{
		return title;
	}

	public GroupingState getGroupingState()
	{
		return pcs.getGroupingState();
	}

	/**
	 * Returns the consistent-with-equals hashCode for this ChoiceSet
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return setName.hashCode() ^ pcs.hashCode();
	}

	/**
	 * Returns true if this ChoiceSet is equal to the given Object. Equality is
	 * defined as being another ChoiceSet object with an equal name and equal
	 * underlying PrimitiveChoiceSet.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof SimpleAbilityChoiceSet)
		{
			SimpleAbilityChoiceSet other = (SimpleAbilityChoiceSet) o;
			return setName.equals(other.setName) && pcs.equals(other.pcs);
		}
		return false;
	}
}