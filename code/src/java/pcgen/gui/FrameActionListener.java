/*
 * FrameActionListener.java
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
 * Created on February 13th, 2002.
 */
package pcgen.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision$
 */
final class FrameActionListener
{
	/**
	 * Reference to main window frame for menu operations, etc.
	 */
	private PCGen_Frame1 main;

	/**
	 * Handle File|Add Kit in the menubar or the Kit button on the
	 * toolbar.
	 */
	ActionListener addKitActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.addKit_actionPerformed();
			}
		};

	/**
	 * Handle File|Close in the menubar or the Close button on the
	 * toolbar.
	 */
	ActionListener closeActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.closeItem_actionPerformed(e);
			}
		};

	/**
	 * Handle File|CloseAll in the menubar or the CloseAll button
	 * on the toolbar.
	 */
	ActionListener closeAllActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.closeAllItem_actionPerformed();
			}
		};

	/**
	 * Handle Close in the tab popup
	 */
	ActionListener closePopupActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.closePopupItem_actionPerformed();
			}
		};

	/**
	 * Handle File|Export|Standard in the menubar or the
	 * ExportToStandard button on the toolbar.
	 */
	ActionListener exportToStandardActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.exportToStandardItem_actionPerformed();
			}
		};

	/**
	 * Handle File|Export|Text in the menubar or the
	 * ExportToStandard button on the toolbar.
	 */
	ActionListener exportToTextActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.exportToTextItem_actionPerformed();
			}
		};

	ActionListener gmgenActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.openGMGen_actionPerformed();
			}
		};

	/**
	 * Handle File|New in the menubar or the New button on the
	 * toolbar.
	 */
	ActionListener newActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.newItem_actionPerformed();
			}
		};

	/**
	 * Handle File|New NPC in the menubar or the New NPC button on the
	 * toolbar.
	 */
	ActionListener newNPCActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.newNPCItem_actionPerformed();
			}
		};

	/**
	 * Handle New in the tab popup
	 */
	ActionListener newPopupActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.newPopupItem_actionPerformed();
			}
		};

	/**
	 * Handle New NPC in the tab popup
	 */
	ActionListener newNPCPopupActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.newNPCPopupItem_actionPerformed();
			}
		};

	/**
	 * Handle File|Open in the menubar or the Open button on the
	 * toolbar.
	 */
	ActionListener openActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.openItem_actionPerformed();
			}
		};

	/**
	 * Handle File|Party|Close in the menubar or the PartyClose
	 * button on the toolbar.
	 */
	ActionListener partyCloseActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.partyCloseItem_actionPerformed();
			}
		};

	/**
	 * Handle File|Party|Open in the menubar or the PartyOpen
	 * button on the toolbar.
	 */
	ActionListener partyOpenActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.partyOpenItem_actionPerformed();
			}
		};

	/**
	 * Handle File|Party|Save in the menubar or the PartySave
	 * button on the toolbar.
	 */
	ActionListener partySaveActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.partySaveItem_actionPerformed();
			}
		};

	/**
	 * Handle File|Party|SaveAs in the menubar or the PartySaveAs
	 * button on the toolbar.
	 */
	ActionListener partySaveAsActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.partySaveAsItem_actionPerformed();
			}
		};

	/**
	 * Handle File|Preferences (in the menubar)
	 */
	ActionListener preferencesActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.preferencesItem_actionPerformed();
			}
		};

	/**
	 * Handle File|Print in the menubar or the Print button on the
	 * toolbar.
	 */
	ActionListener printActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.printItem_actionPerformed();
			}
		};

	/**
	 * Handle File|PrintPreview in the menubar or the PrintPreview
	 * button on the toolbar.
	 */
	ActionListener printPreviewActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.printPreviewItem_actionPerformed();
			}
		};

	/**
	 * Handle File|RevertToSaved in the menubar or the
	 * RevertToSaved button on the toolbar.
	 */
	ActionListener revertToSavedActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.revertToSavedItem_actionPerformed(e);
			}
		};

	/**
	 * Handle Revert in the tab popup
	 */
	ActionListener revertToSavedPopupActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.revertToSavedItem_actionPerformed(e);
			}
		};

	/**
	 * Handle File|Save in the menubar or the Save button on the
	 * toolbar.
	 */
	ActionListener saveActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.saveItem_actionPerformed();
			}
		};

	/**
	 * Handle File|SaveAll in the menubar or the SaveAll button on
	 * the toolbar.
	 */
	ActionListener saveAllActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.saveAllItem_actionPerformed();
			}
		};

	/**
	 * Handle File|SaveAs in the menubar or the SaveAs button on
	 * the toolbar.
	 */
	ActionListener saveAsActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.saveAsItem_actionPerformed();
			}
		};

	/**
	 * Handle Save as (in the tab popup)
	 */
	ActionListener saveAsPopupActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.saveAsPopupItem_actionPerformed();
			}
		};

	/**
	 * Handle Save in the tab popup
	 */
	ActionListener savePopupActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.savePopupItem_actionPerformed();
			}
		};

	/**
	 * Handle Shift left in the tab popup
	 */
	ActionListener shiftLeftPopupActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.shiftLeftPopupItem_actionPerformed();
			}
		};

	/**
	 * Handle Shift right in the tab popup
	 */
	ActionListener shiftRightPopupActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.shiftRightPopupItem_actionPerformed();
			}
		};

	/**
	 * Construct a <code>FrameActionListener</code> with a reference
	 * to the main window frame for menu operations, etc.
	 *
	 * @param aMain PCGen_Frame1 the main window frame
	 */
	FrameActionListener(PCGen_Frame1 aMain)
	{
		main = aMain;
	}
}
