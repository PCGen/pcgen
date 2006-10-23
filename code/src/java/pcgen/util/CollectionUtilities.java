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
 * Created on Oct 21, 2006
 *
 * Current Ver: $Revision: 1060 $
 * Last Editor: $Author: boomer70 $
 * Last Edited: $Date: 2006-06-08 23:25:16 -0400 (Thu, 08 Jun 2006) $
 */
package pcgen.util;

import java.util.Collection;

public final class CollectionUtilities {

	public static String joinStringRepresentations(Collection<?> c,
			String jointext) {
		if (c == null) {
			return "";
		}
		boolean needjoin = false;
		StringBuffer sb = new StringBuffer();
		for (Object obj : c) {
			if (needjoin) {
				sb.append(jointext);
			}
			needjoin = true;
			sb.append(obj.toString());
		}
		return sb.toString();
	}
}
