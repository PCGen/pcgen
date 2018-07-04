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
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.util.Logging;

/**
 * This class represents a particular alignment generator option.
 * 
 */
public class AlignGeneratorOption extends GeneratorOption
{
	private WeightedCollection<PCAlignment> theChoices = null;

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
			for (PCAlignment align : Globals.getContext().getReferenceContext()
				.getSortkeySortedCDOMObjects(PCAlignment.class))
			{
				if (align.getSafe(ObjectKey.VALID_FOR_FOLLOWER) && !theChoices.contains(align))
				{
					theChoices.add(align, aWeight);
				}
			}
			return;
		}
		PCAlignment align =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCAlignment.class, aValue);
		if (align == null)
		{
			Logging.errorPrintLocalised("NPCGen.Options.AlignNotFound", aValue); //$NON-NLS-1$
		}
		else
		{
			theChoices.add(align, aWeight);
		}
	}

	/**
	 * getList
	 *
	 * @return List
	 */
	@Override
	public WeightedCollection<PCAlignment> getList()
	{
		return theChoices;
	}
}
