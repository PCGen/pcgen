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

import java.util.StringTokenizer;

import pcgen.base.util.WeightedCollection;

/**
 * This class represents a particular level generator option.
 * 
 */
public class LevelGeneratorOption extends GeneratorOption
{
	private WeightedCollection<Integer> theChoices = null;
	
	/**
	 * @see pcgen.core.npcgen.GeneratorOption#addChoice(int, java.lang.String)
	 */
	@Override
	public void addChoice(final int aWeight, final String aValue)
	{
		if ( theChoices == null )
		{
			theChoices = new WeightedCollection<>();
		}
		
		final StringTokenizer tok = new StringTokenizer(aValue, ","); //$NON-NLS-1$
		final int minVal = Integer.parseInt(tok.nextToken());
		int maxVal = minVal;
		if (tok.hasMoreTokens())
		{
			maxVal = Integer.parseInt(tok.nextToken());
		}
		for (int i = minVal; i <= maxVal; i++)
		{
			theChoices.add(i, aWeight);
		}
	}

	/**
	 * getList
	 *
	 * @return List
	 */
	@Override
	public WeightedCollection<Integer> getList()
	{
		return theChoices;
	}
}

