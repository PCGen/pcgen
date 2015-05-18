/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.base.lang.StringUtil;

public final class ChooseInformationUtilities
{

	private ChooseInformationUtilities()
	{
		//Utility class should not be constructed
	}

	static  <T> CharSequence buildEncodedString(ChooseInformation<T> info,
		Collection<? extends T> collection)
	{
		if (collection == null)
		{
			return Constants.EMPTY_STRING;
		}
		List<String> list = new ArrayList<String>(collection.size());
		for (T sl : collection)
		{
			list.add(info.encodeChoice(sl));
		}
		Collections.sort(list);
		return StringUtil.joinToStringBuilder(list, ", ");
	}

}
