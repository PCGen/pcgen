/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */
package pcgen.core.npcgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pcgen.core.Equipment;
import pcgen.util.Logging;

public class EquipmentTable extends Table
{
	private static HashMap<String, EquipmentTable> theTables = null;

	public EquipmentTable(final String anId)
	{
		super(anId);
	}

	public List<Equipment> getEquipment()
	{
		final List<Equipment> ret = new ArrayList<>();

		final TableEntry entry = getEntry();
		Logging.debugPrint("Table: " + this + " -> " + entry);
		final List<Object> items = entry.getData();
		for (final Object item : items)
		{
			final EquipmentItem eqItem = (EquipmentItem) item;
			ret.addAll(eqItem.getEquipment());
		}
		return ret;
	}

	public static EquipmentTable get(final String anId)
	{
		if (theTables == null)
		{
			return null;
		}
		return theTables.get(anId);
	}

	public static void addTable(final EquipmentTable aTable)
	{
		if (theTables == null)
		{
			theTables = new HashMap<>();
		}
		theTables.put(aTable.getId(), aTable);
	}
}
