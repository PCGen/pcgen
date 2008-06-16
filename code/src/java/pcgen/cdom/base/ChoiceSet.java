/*
 * Copyright 2006 (C) Tom Parker <thpr@sourceforge.net>
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
 * reference to it.
 * 
 * 
 * @param <T>
 */
public class ChoiceSet<T> extends ConcretePrereqObject implements PrereqObject,
		LSTWriteable
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

	public String getLSTformat()
	{
		return pcs.getLSTformat();
	}

	public Class<? super T> getChoiceClass()
	{
		return pcs.getChoiceClass();
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		return pcs.getSet(pc);
	}

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
