/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.facade.core;

import java.util.EventListener;
import java.util.EventObject;

import pcgen.facade.util.ListFacade;

public interface EquipmentListFacade extends ListFacade<EquipmentFacade>
{
	int getQuantity(EquipmentFacade equipment);

	void addEquipmentListListener(EquipmentListListener listener);

	void removeEquipmentListListener(EquipmentListListener listener);

	/**
	 * Signal that the equipment item has been updated in some way.
	 * @param equipment The item of equipment that has been modified.
	 */
    void modifyElement(EquipmentFacade equipment);

	interface EquipmentListListener extends EventListener
	{

		/*
		 * This method should not be called when a piece of equipment is added or removed from
		 * the list.
		 */
		void quantityChanged(EquipmentListEvent e);

	}

	class EquipmentListEvent extends EventObject
	{

		private final EquipmentFacade equipment;
		private final int index;

		public EquipmentListEvent(Object source, EquipmentFacade equipment, int index)
		{
			super(source);
			this.equipment = equipment;
			this.index = index;
		}

		public EquipmentFacade getEquipment()
		{
			return equipment;
		}

		public int getIndex()
		{
			return index;
		}

	}

}
