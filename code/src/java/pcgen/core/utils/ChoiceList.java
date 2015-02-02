/*
 * Copyright 2006 (C) Tom Parker <thpr@sourceforge.net>
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
 *
 * Created on October 29, 2006.
 *
 * Current Ver: $Revision: 1111 $
 * Last Editor: $Author: boomer70 $
 * Last Edited: $Date: 2006-06-22 21:22:44 -0400 (Thu, 22 Jun 2006) $
 */
package pcgen.core.utils;

import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;

public class ChoiceList<T> {

	private final List<T> list;

	private final int count;

	public ChoiceList(int choiceCount, List<T> choiceList) {
		count = choiceCount;
		list = choiceList;
	}

	public int getCount() {
		return count;
	}

	public List<T> getList() {
		return list;
	}

	@Override
	public String toString() {
		return count + Constants.PIPE + StringUtil.join(list, Constants.PIPE);
	}

	public static <E> ChoiceList<E> getChoiceList(int choiceCount,
			List<E> choiceList) {
		return new ChoiceList<E>(choiceCount, choiceList);
	}

}
