/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 */
package pcgen.core.npcgen;

import pcgen.base.util.WeightedCollection;
import pcgen.cdom.enumeration.Gender;

/**
 * This class represents a particular gender generator option.
 * 
 */
public class GenderGeneratorOption extends GeneratorOption
{
	private WeightedCollection<Gender> theChoices = null;

	/**
	 * @see pcgen.core.npcgen.GeneratorOption#addChoice(int, java.lang.String)
	 */
	@Override
	public void addChoice(final int aWeight, final String aValue)
	{
		if (theChoices == null)
		{
			theChoices = new WeightedCollection<>();
		}

		if (aValue.equals("*")) //$NON-NLS-1$
		{
			for (final Gender gender : Gender.values())
			{
				if (!theChoices.contains(gender))
				{
					theChoices.add(gender, aWeight);
				}
			}
			return;
		}

		for (final Gender gender : Gender.values())
		{
			if (gender.toString().equalsIgnoreCase(aValue))
			{
				theChoices.add(gender, aWeight);
			}
		}

	}

	/**
	 * getList
	 *
	 * @return WeightedCollection of gender choices
	 */
	@Override
	public WeightedCollection<Gender> getList()
	{
		return theChoices;
	}
}
