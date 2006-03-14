/*
 * ExportTextPopup.java
 * Copyright 2005 (C) M. Verburg (karianna) <karianna@clear.net.nz>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on August 26th, 2005.
 */
package pcgen.gui;

import pcgen.util.PropertyFactory;

import javax.swing.JTabbedPane;

/**
 * Export Text popup dialog.  The real work goes on in the panel.
 *
 * @author <a href="mailto:martijnverburg@gmail.com">M. Verburg (karianna)</a>
 */
final class ExportTextPopup extends PCGenPopup
{
	private JTabbedPane baseTabbedPanel = null;
	private MainExport mainExport = null;

	ExportTextPopup(JTabbedPane aPanel)
	{
		super(PropertyFactory.getString("in_exportPCParty"));
		// We are exporting to text
		mainExport = new MainExport(GuiConstants.EXPORT_AS_TEXT);
		setPanel(mainExport);
		baseTabbedPanel = aPanel;
		pack();
		setVisible(true);
	}

	/**
	 * Set the pc by the tab we are on
	 */
	public void setCurrentPCSelectionByTab()
	{
		if (mainExport != null)
		{
			mainExport.setCurrentPCSelection(baseTabbedPanel.getSelectedIndex());
			pack();
			setVisible(true);
		}
	}
}
