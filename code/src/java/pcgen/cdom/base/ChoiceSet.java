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

public class ChoiceSet<T> extends ConcretePrereqObject implements PrereqObject,
		LSTWriteable
{

	private final PrimitiveChoiceSet<T> pcs;

	private final String setName;

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
