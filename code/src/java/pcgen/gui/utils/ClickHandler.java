/*
 * JTreeTableMouseAdapter.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * @author  Aaron Divinsky <boomer70@yahoo.com>
 * Created on Oct 11, 2005
 *
 * Current Ver: $Revision: 1.2 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2005/10/12 14:05:19 $
 *
 */
package pcgen.gui.utils;

/**
 * Handles click
 */
public interface ClickHandler
{
	/**
	 * Called when a user single clicks in the table.
	 */
	public void singleClickEvent();

	/**
	 * Called when a user double clicks in the table.
	 */
	public void doubleClickEvent();

	/**
	 * Check to see if this is a leaf node.
	 *
	 * @param obj Object
	 * @return boolean
	 */
	public boolean isSelectable(Object obj);
}
