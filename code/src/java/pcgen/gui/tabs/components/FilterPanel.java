/*
 * FilterPanel.java
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pcgen.gui.tabs.IFilterableView;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.Utility;
import pcgen.system.LanguageBundle;

/**
 * This class implements a panel containing a View Choice combo and Quick Filter
 * edit box.
 * 
 * <p>The class communicates information about the state of the filters through
 * the <code>IFilterableView</code> interface.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class FilterPanel extends JPanel
{
	private IFilterableView theView;

	private int theViewIndex = 0;

	private JComboBoxEx theViewComboBox;
	private JTextField theFilterText;
	private JButton theClearButton;

	/**
	 * Constructs the <tt>FilterPanel</tt> object.
	 * 
	 * @param aView An IFilterableView to receive notification of filter events.
	 * @param aLabel The label text to use for the View Choice combo.  This is
	 * usually either Available or Selected.
	 */
	public FilterPanel(final IFilterableView aView, final String aLabel)
	{
		theView = aView;

		// TODO - GridBag is probably overkill for this simple panel but this 
		// is how it was done and I don't feel like breaking it now.
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		int i = 0;

		// --------------------------------------------------------
		// Label for combo
		// --------------------------------------------------------
		final JLabel treeLabel = new JLabel(aLabel);
		Utility.buildConstraints(c, i++, 0, 1, 1, 0, 0);
		c.insets = new Insets(1, 2, 1, 2);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		add(treeLabel, c);

		// --------------------------------------------------------
		// View Combo
		// --------------------------------------------------------
		theViewComboBox = new JComboBoxEx();
		theViewComboBox.addActionListener(new ActionListener()
		{
			/**
			 * This method is called when the combobox is changed. If the new
			 * selected index is different from the existing one, the listener
			 * will be told of the change.
			 */
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				final int index = theViewComboBox.getSelectedIndex();
				if (theViewIndex != index)
				{
					theViewIndex = index;
					theView.viewChanged(theViewIndex);
				}
			}
		});
		for (final String choice : aView.getViewChoices())
		{
			theViewComboBox.addItem(choice);
		}
		Utility.setDescription(theViewComboBox, LanguageBundle
			.getString("InfoTabs.ViewCombo.Description")); //$NON-NLS-1$

		theViewComboBox.setSelectedIndex(aView.getInitialChoice());

		Utility.buildConstraints(c, i++, 0, 1, 1, 0, 0);
		c.insets = new Insets(1, 2, 1, 2);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		add(theViewComboBox, c);

		// --------------------------------------------------------
		// Filter Label
		// --------------------------------------------------------
		final JLabel filterLabel =
				new JLabel(LanguageBundle.getString("InfoTabs.FilterLabel")); //$NON-NLS-1$
		Utility.buildConstraints(c, i++, 0, 1, 1, 0, 0);
		c.insets = new Insets(1, 2, 1, 2);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		add(filterLabel, c);

		// --------------------------------------------------------
		// Filter text field
		// --------------------------------------------------------
		theFilterText = new JTextField();
		theFilterText.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(@SuppressWarnings("unused")
			DocumentEvent evt)
			{
				setQFilter();
			}

			public void insertUpdate(@SuppressWarnings("unused")
			DocumentEvent evt)
			{
				setQFilter();
			}

			public void removeUpdate(@SuppressWarnings("unused")
			DocumentEvent evt)
			{
				setQFilter();
			}
		});
		Utility.buildConstraints(c, i++, 0, 1, 1, 95, 0);
		c.insets = new Insets(1, 2, 1, 2);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		add(theFilterText, c);

		// --------------------------------------------------------
		// Clear filter button
		// --------------------------------------------------------
		theClearButton = new JButton(LanguageBundle.getString("in_clear")); //$NON-NLS-1$
		theClearButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(@SuppressWarnings("unused")
			ActionEvent evt)
			{
				clearQFilter();
			}
		});
		Utility.buildConstraints(c, i++, 0, 1, 1, 0, 0);
		c.insets = new Insets(0, 2, 0, 2);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		theClearButton.setEnabled(false);
		add(theClearButton, c);
	}

	private void setQFilter()
	{
		final String filter = theFilterText.getText();
		if (filter.length() == 0)
		{
			clearQFilter();
			return;
		}
		theView.setQFilter(filter);
		theClearButton.setEnabled(true);
		theViewComboBox.setEnabled(false);

	}

	private void clearQFilter()
	{
		theClearButton.setEnabled(false);
		theViewComboBox.setEnabled(true);
		theFilterText.setText(null);

		theView.clearQFilter();
	}
}
