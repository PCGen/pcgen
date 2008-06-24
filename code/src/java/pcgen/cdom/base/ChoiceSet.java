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
 * Created on October 29, 2006.
 * 
 * Current Ver: $Revision: 1111 $ Last Editor: $Author: boomer70 $ Last Edited:
 * $Date: 2006-06-22 21:22:44 -0400 (Thu, 22 Jun 2006) $
 */
package pcgen.cdom.base;

import java.util.Set;

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
 * TODO Why is this necessary? It certainly doesn't add anything other than a
 * name to the Trunk structure (as of SVN 6665), and it's even dangerously
 * semantic in the sense that it takes in the PrimitiveChoiceSet and keeps a
 * reference to it. getSet also asks for a PlayerCharacter, yet none is ever
 * actually used in any implementation contained within Trunk (again 6665) -
 * thpr 6/15/08
 * 
 * @param <T>
 *            the Class contained within this ChoiceSet
 */
public class ChoiceSet<T> extends ConcretePrereqObject implements PrereqObject
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
	 * Creates a new ChoiceSet with the given name and given underlying
	 * PrimitiveChoiceSet.
	 * 
	 * @param name
	 *            The name of this ChoiceSet
	 * @param choice
	 *            The PrimitiveChoiceSet indicating the Collection of objects
	 *            for this ChoiceSet
	 */
	public ChoiceSet(String name, PrimitiveChoiceSet<T> choice)
	{
		if (choice == null)
		{
			throw new IllegalArgumentException();
		}
		if (name == null)
		{
			throw new IllegalArgumentException();
		}
		pcs = choice;
		setName = name;
	}

	/**
	 * Returns a representation of this ChoiceSet, suitable for storing in an
	 * LST file.
	 */
	public String getLSTformat()
	{
		return pcs.getLSTformat();
	}

	/**
	 * Returns the Class contained within this ChoiceSet
	 * 
	 * @return the Class contained within this ChoiceSet
	 */
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
	public Set<T> getSet(PlayerCharacter pc)
	{
		return pcs.getSet(pc);
	}

	/**
	 * Returns the name of this ChoiceSet. Note that this name is suitable for
	 * display, but it does not represent information that should be stored in a
	 * persistent state (it is not sufficient information to reconstruct this
	 * ChoiceSet)
	 * 
	 * @return
	 */
	public String getName()
	{
		return setName;
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
		if (o instanceof ChoiceSet)
		{
			ChoiceSet<?> other = (ChoiceSet<?>) o;
			return setName.equals(other.setName) && pcs.equals(other.pcs);
		}
		return false;
	}
}
