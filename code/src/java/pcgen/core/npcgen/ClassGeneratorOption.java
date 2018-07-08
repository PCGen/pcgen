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
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.util.Logging;

/**
 * This class represents a particular class generator option.
 * 
 */
public class ClassGeneratorOption extends GeneratorOption
{
	private WeightedCollection<PCClass> theChoices = null;

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
			for (final PCClass pcClass : Globals.getContext().getReferenceContext()
				.getConstructedCDOMObjects(PCClass.class))
			{
				if (!theChoices.contains(pcClass))
				{
					theChoices.add(pcClass, aWeight);
				}
			}
			return;
		}
		if (aValue.startsWith("TYPE")) //$NON-NLS-1$
		{
			for (final PCClass pcClass : Globals.getPObjectsOfType(
				Globals.getContext().getReferenceContext().getConstructedCDOMObjects(PCClass.class),
				aValue.substring(5)))
			{
				if (!theChoices.contains(pcClass))
				{
					theChoices.add(pcClass, aWeight);
				}
			}
			return;
		}
		final PCClass pcClass =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class, aValue);
		if (pcClass == null)
		{
			Logging.errorPrintLocalised("NPCGen.Options.ClassNotFound", aValue); //$NON-NLS-1$
		}
		else
		{
			theChoices.add(pcClass, aWeight);
		}
	}

	/**
	 * @see pcgen.core.npcgen.GeneratorOption#getList()
	 */
	@Override
	public WeightedCollection<PCClass> getList()
	{
		return theChoices;
	}
}
