/*
 * RemoveItemPanel.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.gui.tabs.components;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.Utility;
import pcgen.system.LanguageBundle;

/**
 * This class creates and manages a &quot;Remove&quot; button panel.  The class
 * exposes the <tt>AddActionListener</tt> method to allow listening for events
 * from the contained button.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class RemoveItemPanel extends JPanel
{
	private JButton theRemoveButton;

	/**
	 * Constructs the panel containing a single &quot;Remove&quot; button.
	 *
	 */
	public RemoveItemPanel()
	{
		theRemoveButton =
				new JButton(IconUtilitities.getImageIcon("Back16.gif")); //$NON-NLS-1$

		this.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		Utility.setDescription(theRemoveButton, LanguageBundle
			.getString("InfoTabs.RemoveButton.Description")); //$NON-NLS-1$
		theRemoveButton.setEnabled(false);
		theRemoveButton.setMargin(new Insets(1, 14, 1, 14));
		this.add(theRemoveButton);
	}

	/**
	 * Enable or disable the button.
	 * 
	 * @param yesNo <tt>true</tt> enables the button <tt>false</tt> disables it.
	 */
	@Override
	public void setEnabled(final boolean yesNo)
	{
		theRemoveButton.setEnabled(yesNo);
	}

	/**
	 * Add a listener to the contained button.
	 * 
	 * @param aListener The listener to add.
	 */
	public void addActionListener(final ActionListener aListener)
	{
		theRemoveButton.addActionListener(aListener);
	}
}
