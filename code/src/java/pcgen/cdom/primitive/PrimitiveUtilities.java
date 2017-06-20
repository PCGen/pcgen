/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.primitive;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import pcgen.cdom.base.PrimitiveCollection;

import org.apache.commons.collections4.CollectionUtils;

public final class PrimitiveUtilities
{
	/**
	 * A COLLATOR used to sort Strings in a locale-aware method.
	 */
	private static final Collator COLLATOR = Collator.getInstance();

	private PrimitiveUtilities()
	{
	}

	public static final Comparator<PrimitiveCollection<?>> COLLECTION_SORTER =
			new Comparator<PrimitiveCollection<?>>()
	{
		@Override
		public int compare(PrimitiveCollection<?> lstw1,
				PrimitiveCollection<?> lstw2)
		{
			return COLLATOR.compare(lstw1.getLSTformat(false), lstw2.getLSTformat(false));
		}
	};

	public static String joinLstFormat(
			Collection<? extends PrimitiveCollection<?>> pcfCollection,
			CharSequence separator, boolean useAny)
	{
		return CollectionUtils.emptyIfNull(pcfCollection)
		                      .stream()
		                      .map(pcf -> pcf.getLSTformat(useAny))
		                      .collect(Collectors.joining(separator));
	}

}
