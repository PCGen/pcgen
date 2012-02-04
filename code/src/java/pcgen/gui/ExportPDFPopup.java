/*
 * ExportPDFPopup.java
 * Copyright 2002 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
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
 * Created on February 14th, 2002.
 */
package pcgen.gui;

import pcgen.system.LanguageBundle;

import javax.swing.JTabbedPane;

/**
 * Export PDF popup dialog.  The real work goes on in the panel.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision$
 */
final class ExportPDFPopup extends PCGenPopup
{
	private JTabbedPane baseTabbedPanel = null;
	private MainPrint mainPrint = null;

	ExportPDFPopup(JTabbedPane aPanel)
	{
		super(LanguageBundle.getString("in_exportPCParty"));
		mainPrint = new MainPrint(this, MainPrint.EXPORT_MODE);
		setPanel(mainPrint);
		baseTabbedPanel = aPanel;
		pack();
		setVisible(true);
	}

	/**
	 * Set the current pc by the tab
	 */
	public void setCurrentPCSelectionByTab()
	{
		if (mainPrint != null)
		{
			mainPrint.setCurrentPCSelection(baseTabbedPanel.getSelectedIndex());
			pack();
			setVisible(true);
		}
	}
}
