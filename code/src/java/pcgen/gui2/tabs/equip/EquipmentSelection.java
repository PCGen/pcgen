/*
 * EquipmentSelection.java
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
 * Created on Oct 17, 2011, 8:25:16 PM
 */
package pcgen.gui2.tabs.equip;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import pcgen.core.facade.EquipmentFacade;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class EquipmentSelection implements Transferable
{

	public static final DataFlavor equipmentArrayFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
			+ ";class=\"" + EquipmentFacade[].class.getName() + "\"", null);
	private EquipmentFacade[] equipment;

	public EquipmentSelection(EquipmentFacade[] equipment)
	{
		this.equipment = equipment;
	}

	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[]
				{
					equipmentArrayFlavor
				};
	}

	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return equipmentArrayFlavor == flavor;
	}

	public EquipmentFacade[] getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (!isDataFlavorSupported(flavor))
		{
			throw new UnsupportedFlavorException(flavor);
		}
		return equipment;
	}

}
