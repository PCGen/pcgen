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
package pcgen.gui2.tabs.equip;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import pcgen.facade.core.EquipmentFacade;

public class EquipmentSelection implements Transferable
{

	public static final DataFlavor EQUIPMENT_ARRAY_FLAVOR = new DataFlavor(
		DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + EquipmentFacade[].class.getName() + "\"", null);
	private EquipmentFacade[] equipment;

	public EquipmentSelection(EquipmentFacade[] equipment)
	{
		this.equipment = equipment;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[]{EQUIPMENT_ARRAY_FLAVOR};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return EQUIPMENT_ARRAY_FLAVOR == flavor;
	}

	@Override
	public EquipmentFacade[] getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (!isDataFlavorSupported(flavor))
		{
			throw new UnsupportedFlavorException(flavor);
		}
		return equipment;
	}

}
