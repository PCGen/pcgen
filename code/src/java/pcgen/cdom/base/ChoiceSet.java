/*
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * 
 */
package pcgen.cdom.base;

import java.util.Collection;
import java.util.Objects;

import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;

/**
 * A ChoiceSet is a named container of a Collection of objects (stored in a
 * PrimitiveChoiceSet).
 * 
 * It is expected that a ChoiceSet will be useful in situations where a
 * pre-defined list of choices is available.
 * 
 * If the set of choices is dynamic, consider using the List infrastructure,
 * including classes like CDOMList.
 * 
 * @see pcgen.cdom.base.CDOMList
 * 
 * @param <T>
 *            the Class contained within this ChoiceSet
 */
public class ChoiceSet<T> extends ConcretePrereqObject implements SelectableSet<T>
{

	/**
	 * The PrimitiveChoiceSet containing the Collection of Objects in this
	 * ChoiceSet
	 */
	private final PrimitiveChoiceSet<T> pcs;

	/**
	 * The name of this ChoiceSet
	 */
	private final String setName;

	/**
	 * The title (presented to the user) of this ChoiceSet
	 */
	private String title = null;

	/**
	 * An identifier to check if the ChoiceSet (and the underlying
	 * PrimitiveChoiceSet) should use the "ANY" identifier (vs. "ALL") when
	 * referring to the global collection of objects of a certain type.
	 */
	private final boolean useAny;

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
	public ChoiceSet(String name, PrimitiveChoiceSet<T> choice)
	{
		this(name, choice, false);
	}

	/**
	 * Creates a new ChoiceSet with the given name and given underlying
	 * PrimitiveChoiceSet.
	 * 
	 * @param name
	 *            The name of this ChoiceSet
	 * @param choice
	 *            The PrimitiveChoiceSet indicating the Collection of objects
	 *            for this ChoiceSet
	 * @param any
	 *            Use "ANY" for the "ALL" reference if true
	 * @throws IllegalArgumentException
	 *             if the given name or PrimitiveChoiceSet is null
	 */
	public ChoiceSet(String name, PrimitiveChoiceSet<T> choice, boolean any)
	{
		Objects.requireNonNull(choice, "PrimitiveChoiceSet cannot be null");
		Objects.requireNonNull(name, "Name cannot be null");
		pcs = choice;
		setName = name;
		useAny = any;
	}

	/**
	 * Returns a representation of this ChoiceSet, suitable for storing in an
	 * LST file.
	 */
	@Override
	public String getLSTformat()
	{
		return pcs.getLSTformat(useAny);
	}

	/**
	 * Returns the Class contained within this ChoiceSet
	 * 
	 * @return the Class contained within this ChoiceSet
	 */
	@Override
	public Class<? super T> getChoiceClass()
	{
		return pcs.getChoiceClass();
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
	@Override
	public Collection<? extends T> getSet(PlayerCharacter pc)
	{
		return pcs.getSet(pc);
	}

	/**
	 * Returns the name of this ChoiceSet. Note that this name is suitable for
	 * display, but it does not represent information that should be stored in a
	 * persistent state (it is not sufficient information to reconstruct this
	 * ChoiceSet)
	 * 
	 * @return The name of this ChoiceSet
	 */
	@Override
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
	 */
	@Override
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
	@Override
	public String getTitle()
	{
		return title;
	}

	@Override
	public GroupingState getGroupingState()
	{
		return pcs.getGroupingState();
	}

	@Override
	public int hashCode()
	{
		return setName.hashCode() ^ pcs.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof ChoiceSet<?> other)
		{
			return setName.equals(other.setName) && pcs.equals(other.pcs);
		}
		return false;
	}

	public static class AbilityChoiceSet extends ChoiceSet<CNAbilitySelection>
	{

		private final AbilityRefChoiceSet arcs;

		public AbilityChoiceSet(String name, AbilityRefChoiceSet choice)
		{
			super(name, choice);
			arcs = choice;
		}

		public CDOMSingleRef<AbilityCategory> getCategory()
		{
			return arcs.getCategory();
		}

		public Nature getNature()
		{
			return arcs.getNature();
		}
	}
}
