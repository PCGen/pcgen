/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created on July 25, 2005.
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2005/07/26 02:24:19 $
 */
package pcgen.core.utils;

import java.util.List;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 * 
 * An object that implements KeyedListContainer safely encapsulates a ListKeyMap
 */
public interface KeyedListContainer
{
	public boolean containsListFor(ListKey key);

	public List getListFor(ListKey key);

	public int getSizeOfListFor(ListKey key);

	public boolean containsInList(ListKey key, String value);

	public Object getElementInList(ListKey key, int i);

	public List getSafeListFor(ListKey key);
	
	public int getSafeSizeOfListFor(ListKey key);
}
