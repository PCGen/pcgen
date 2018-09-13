/*
 * Copyright 2003 (C) Devon Jones
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
package plugin.encounter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.DefaultComboBoxModel;

import gmgen.io.ReadXML;
import gmgen.io.VectorTable;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

public class EnvironmentModel extends DefaultComboBoxModel<Object>
{
	private final String dir;

	/**
	 * Constructor
	 * @param parentDir
	 */
	EnvironmentModel(String parentDir)
	{
		dir = parentDir;
	}

	/**
	 * Update the model
	 */
	public void update()
	{
		VectorTable table;
		ReadXML reader;
		File f = new File(dir, "environments.xml"); //$NON-NLS-1$

		this.removeAllElements();

		if (!f.exists())
		{
			// TODO Make it so that the view also indicate that the file is missing.
			Logging.errorPrintLocalised("in_plugin_encounter_error_missing", f); //$NON-NLS-1$

			return;
		}

		reader = new ReadXML(f);
		table = reader.getTable();

		this.addElement(LanguageBundle.getString("in_plugin_encounter_generic")); //$NON-NLS-1$

		for (int x = 1; x < table.size(); x++)
		{
			try
			{
				List<String> row = (ArrayList<String>)table.get(x);
				this.addElement(row.get(0));
			}
			catch (NoSuchElementException e)
			{
				break;
			}
		}
	}
}
