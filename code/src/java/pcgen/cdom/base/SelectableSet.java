/*
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Collection;

import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;

/**
 * A ChoiceSet is a named container of a Collection of objects (stored in a
 * PrimitiveChoiceSet).
 * 
 * It is expected that a ChoiceSet will be useful in situations where a
 * pre-defined list of choices is available.
 * 
 * If the set of choices is dynamic, consider using the List infrastructure,
 * including classes like CDOMList.
 * 
 * @see pcgen.cdom.base.CDOMList
 * 
 * @param <T>
 *            the Class contained within this ChoiceSet
 */
public interface SelectableSet<T>
{

	String getLSTformat();

	Class<? super T> getChoiceClass();

	String getName();

	GroupingState getGroupingState();

	String getTitle();

	void setTitle(String string);

	Collection<? extends T> getSet(PlayerCharacter pc);

}
