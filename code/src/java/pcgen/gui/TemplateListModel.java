/*
 * MainPrint.java
 * Copyright 2003 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 */
package pcgen.gui;

import pcgen.core.Globals;
import pcgen.core.SettingsHandler;

import javax.swing.AbstractListModel;
import javax.swing.JOptionPane;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * When first created, this class will cache the contents of the "Templates" directory.
 *
 * @author Jonas Karlsson
 * @version $Revision$
 */
class TemplateListModel extends AbstractListModel
{
	private CsheetFilter csheetFilter = null;
	private CsheetFilter psheetFilter = null;
	private String fileType = null;
	private String[] cSheets;
	private String[] pSheets;
	private boolean partyMode = false;
	private int attempts = 0;

	/**
	 * Constructor
	 * @param argCsheetFilter
	 * @param argPsheetFilter
	 * @param argPartyMode
	 * @param argFileType
	 */
	public TemplateListModel(CsheetFilter argCsheetFilter, CsheetFilter argPsheetFilter, boolean argPartyMode,
		String argFileType)
	{
		super();
		csheetFilter = argCsheetFilter;
		psheetFilter = argPsheetFilter;
		partyMode = argPartyMode;
		fileType = argFileType;

		updateTemplateList();
	}

	public Object getElementAt(int index)
	{
		if (partyMode)
		{
			if (index >= pSheets.length)
			{
				return "No templates found";
			}
			return pSheets[index];
		}
		if (index >= cSheets.length)
		{
			return "No templates found";
		}
		return cSheets[index];
	}

	/**
	 * Returns number of list elements.  Will always be at least one since we
	 * add the message.
	 * @return int
	 */
	public int getSize()
	{
		if (partyMode)
		{
			return Math.max(1,pSheets.length);
		}
		return Math.max(1,cSheets.length);
	}

	/**
	 * Returns the actual number of files managed
	 * @return int number of files
	 */
	public int getNumFiles()
	{
		if (partyMode)
		{
			return pSheets.length;
		}
		return cSheets.length;
	}

	/**
	 * Retuirn the index of
	 * @param o
	 * @return the index of
	 */
	public int indexOf(Object o)
	{
		if (partyMode)
		{
			return Arrays.binarySearch(pSheets, o);
		}
		return Arrays.binarySearch(cSheets, o);
	}

	/**
	 * Update the list of templates
	 */
	public void updateTemplateList()
	{
		csheetFilter.setDirFilter(fileType);
		csheetFilter.setIgnoreExtension(".");

		List<String> aList = csheetFilter.getAccepted();

		if ((aList.size() == 0) && (attempts == 0))
		{
			Object[] options = { "OK", "CANCEL" };

			if (JOptionPane.showOptionDialog(null,
					"No templates found. Attempt to change to " + Globals.getDefaultPath() + File.separator
					+ "outputsheets ?", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
					options[0]) == JOptionPane.YES_OPTION)
			{
				SettingsHandler.setPcgenOutputSheetDir(new File(Globals.getDefaultPath() + File.separator
						+ "outputsheets"));
				attempts = 1;
				aList = csheetFilter.getAccepted();
			}
		}

		cSheets = new String[aList.size()];

		for (int i = 0; i < aList.size(); i++)
		{
			cSheets[i] = aList.get(i).toString();
		}

		psheetFilter.setDirFilter(fileType);
		psheetFilter.setIgnoreExtension(".");
		aList = psheetFilter.getAccepted();
		pSheets = new String[aList.size()];

		for (int i = 0; i < aList.size(); i++)
		{
			pSheets[i] = aList.get(i).toString();
		}

		Arrays.sort(pSheets);
		Arrays.sort(cSheets);
	}

	/**
	 * Are we currently in party mode (exporting a full party)?
	 * @return true if in party mode.
	 */
	public boolean isPartyMode()
	{
		return partyMode;
	}

	/**
	 * Set the party mode flag, true means we will be exporting the full party.
	 *
	 * @param b
	 */
	public void setPartyMode(boolean b)
	{
		partyMode = b;
	}

}
