/*
 * PObjectUpdater.java
 * Copyright 2003 (C) Bryan McRoberts <merton.monk@codemonkeypublishing.com>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.gui.editor;

import pcgen.cdom.base.CDOMObject;

/**
 * ???
 *
 * @author  ???
 * @version $Revision$
 */
interface PObjectUpdater<T extends CDOMObject>
{
	/** updateData takes the GUI components and updates the
	 * PObject obj with those values
	 * @param obj
	 */
	void updateData(T obj);

	/** updateView takes the values from PObject obj
	 * and updates the GUI components
	 * @param obj
	 */
	void updateView(T obj);
}
