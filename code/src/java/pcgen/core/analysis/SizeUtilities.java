/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.core.analysis;

import java.util.List;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.SizeAdjustment;

public class SizeUtilities
{

	/**
	 * Get size as an integer
	 * @param aSize
	 * @param defaultValue
	 * @return size as an int
	 */
	public static int sizeInt(final String aSize, final int defaultValue)
	{
		List<SizeAdjustment> list = Globals.getContext().getReferenceContext()
				.getOrderSortedCDOMObjects(SizeAdjustment.class);
		for (int i = 0; i < list.size(); i++)
		{
			if (aSize.equals(list.get(i).getKeyName()))
			{
				return i;
			}
		}
	
		return defaultValue;
	}

	public static int sizeInt(final SizeAdjustment aSize)
	{
		List<SizeAdjustment> list = Globals.getContext().getReferenceContext()
				.getOrderSortedCDOMObjects(SizeAdjustment.class);
		for (int i = 0; i < list.size(); i++)
		{
			if (aSize.equals(list.get(i)))
			{
				return i;
			}
		}
	
		return -1;
	}

	/**
	 * Get size as an int
	 * @param aSize
	 * @return size as an int
	 */
	public static int sizeInt(final String aSize)
	{
		return sizeInt(aSize, 0);
	}

	/**
	 * Get the default size adjustment
	 * @return the default size adjustment
	 */
	public static SizeAdjustment getDefaultSizeAdjustment()
	{
		for (SizeAdjustment s : Globals.getContext().getReferenceContext()
				.getOrderSortedCDOMObjects(SizeAdjustment.class))
		{
			if (s.getSafe(ObjectKey.IS_DEFAULT_SIZE))
			{
				return s;
			}
		}
	
		return null;
	}

	public static int getDefaultSizeInt()
	{
		List<SizeAdjustment> list = Globals.getContext().getReferenceContext()
				.getOrderSortedCDOMObjects(SizeAdjustment.class);
		for (int i = 0; i < list.size(); i++)
		{
			SizeAdjustment s = list.get(i);
			if (s.getSafe(ObjectKey.IS_DEFAULT_SIZE))
			{
				return i;
			}
		}

		return -1;
	}
}
