/*
 * Copyright 2015 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package pcgen.gui2.solverview;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;

/**
 * An ObjectNameDisplayer wraps a CDOMObject in order to display an informative
 * String from the toString() method. This allows the ObjectNameDisplayer to be
 * directly used in the UI without worrying about the actual toString() behavior
 * of any given CDOMObject.
 */
class ObjectNameDisplayer
{

	/**
	 * The underlying CDOMObject.
	 */
	private final CDOMObject obj;

	/**
	 * Constructs a new ObjectNameDisplayer with the given CDOMObject
	 * 
	 * @param cdo
	 *            The CDOMObject that this ObjectNameDisplayer will represent
	 */
	public ObjectNameDisplayer(CDOMObject cdo)
	{
		obj = cdo;
	}

	/**
	 * Returns the CDOMObject underlying this ObjectNameDisplayer.
	 * 
	 * @return the CDOMObject underlying this ObjectNameDisplayer
	 */
	public CDOMObject getObject()
	{
		return obj;
	}

	/**
	 * Returns an informative String identifying the CDOMObject underlying this
	 * ObjectNameDisplayer
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		Class<? extends CDOMObject> objClass = obj.getClass();
		String suffix = null;
		CDOMObject object = obj;
		if (EquipmentHead.class.equals(objClass))
		{
			EquipmentHead head = (EquipmentHead) obj;
			int index = head.getHeadIndex();
			objClass = Equipment.class;
			object = (Equipment) head.getOwner();
			suffix = "Part: " + index;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(objClass.getSimpleName()).append(" ");
		sb.append(object.getDisplayName());
		if (suffix != null)
		{
			sb.append(" (").append(suffix).append(")");
		}
		return sb.toString();
	}
}