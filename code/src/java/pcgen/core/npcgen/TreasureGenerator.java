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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pcgen.core.GameMode;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

public final class TreasureGenerator
{
	private static final TreasureGenerator theInstance = new TreasureGenerator();

	private static final HashMap<GameMode, List<EquipmentTable>> theTreasureTables = new HashMap<>();

	private static final File tablesDir =
			new File(new File(ConfigurationSettings.getSystemsDir()) + File.separator + "npcgen" //$NON-NLS-1$ 
				+ File.separator + "treasure"); //$NON-NLS-1$

	//	private static File tablesDir = new File(Globals.getDefaultPath() 
	//		+ File.separator + "system" //$NON-NLS-1$
	//		+ File.separator + "npcgen"  //$NON-NLS-1$ 
	//		+ File.separator + "treasure"); //$NON-NLS-1$

	private TreasureGenerator()
	{
		// Private so it can't be constructed.
	}

	public static TreasureGenerator getInstance()
	{
		return theInstance;
	}

	public List<EquipmentTable> getTables(final GameMode aMode)
	{
		List<EquipmentTable> tables = theTreasureTables.get(aMode);

		if (tables == null)
		{
			try
			{
				final EquipmentTableParser parser = new EquipmentTableParser(aMode);
				final File[] fileNames = tablesDir.listFiles(new FilenameFilter()
				{
					@Override
					public boolean accept(final File aDir, final String aName)
					{
						if (aName.toLowerCase().endsWith(".xml")) //$NON-NLS-1$
						{
							return true;
						}
						return false;
					}
				});

				tables = new ArrayList<>();
				tables.addAll(parser.parse(fileNames));
				theTreasureTables.put(aMode, tables);
				return tables;
			}
			catch (Exception ex)
			{
				Logging.errorPrint("Error loading tables", ex);
			}
		}
		return tables;
	}

	public static void addTable(final GameMode aMode, final EquipmentTable aTable)
	{
		List<EquipmentTable> tables = theTreasureTables.get(aMode);
		if (tables == null)
		{
			tables = new ArrayList<>();
			theTreasureTables.put(aMode, tables);
		}
		tables.add(aTable);
	}
}
