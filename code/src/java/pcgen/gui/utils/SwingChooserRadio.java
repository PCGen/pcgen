/*
 * SwingChooserRadio.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on Jan 21st, 2003, 11:15 PM
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.gui.utils;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;
import pcgen.util.chooser.ChooserRadio;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This dialog type accepts a list of available items,
 * creates an array of radio buttons, one for each choice
 * This forces the user to choose one and only one
 *
 * The dialog is always modal, so a call
 * to show() will block program execution.
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 **/
public final class SwingChooserRadio extends JDialog implements ChooserRadio
{
	static final long serialVersionUID = -2156072283857697398L;
	private static String in_ok;
	private static String in_cancel;

	/**
	 * Resource bundles
	 **/
	static
	{
		LanguageBundle.getString("in_available");
		in_ok = LanguageBundle.getString("in_ok");
		in_cancel = LanguageBundle.getString("in_cancel");
	}

	private ButtonGroup avaGroup = null;
	private JComboBoxEx cmbCombo;
	private final JPanel avaPane = new JPanel();
	private JButton cancelButton;
	private JButton okButton;
	private JLabelPane mMessageText;
	private JRadioButton lblCombo;

	// The list of available items
	private List mAvailableList = new ArrayList();

	// The list of Selected items (all Strings)
	private List mSelectedList = new ArrayList();
	private JRadioButton[] avaRadioButton = null;

	/**
	 * Chooser constructor.
	 **/
	public SwingChooserRadio()
	{
		super(Globals.getRootFrame());
		initComponents();
	}

	/**
	 * Sets the AvailableList attribute of the Chooser object
	 * @param availableList
	 **/
    @Override
	public void setAvailableList(List availableList)
	{
		mAvailableList = availableList;
	}

    @Override
	public void setComboData(final String cmbLabelText, List cmbData)
	{
		cmbCombo = new JComboBoxEx(cmbData.toArray());
		((DefaultComboBoxModel) cmbCombo.getModel()).insertElementAt("("
			+ Constants.NONE + ")", 0);
		cmbCombo.setSelectedIndex(0);
		cmbCombo.addItemListener(new ItemListener()
		{
            @Override
			public void itemStateChanged(ItemEvent e)
			{
				//
				// If just made a selection from the combo,
				// then make sure it's associated
				// radio button is selected
				//
				if ((e.getStateChange() == ItemEvent.SELECTED)
					&& (cmbCombo.getSelectedIndex() > 0))
				{
					lblCombo.setSelected(true);
				}
			}
		});

		lblCombo = new JRadioButton(cmbLabelText);
		lblCombo.addItemListener(new ItemListener()
		{
            @Override
			public void itemStateChanged(ItemEvent e)
			{
				// If associated radio button becomes not
				// selected, then set the selected
				// in the combo box to "(None)".
				// If select the radio button instead of a
				// selection from the combobox, then set
				// the combo selection to the 1st type
				//
				if (!lblCombo.isSelected())
				{
					cmbCombo.setSelectedIndex(0);
				}
				else if (cmbCombo.getSelectedIndex() == 0)
				{
					cmbCombo.setSelectedIndex(1);
				}
			}
		});
	}

	/**
	 * Sets the message text
	 * @param messageText
	 **/
    @Override
	public void setMessageText(String messageText)
	{
		if ((messageText == null) || (messageText.trim().length() == 0))
		{
			messageText = "<html>&nbsp;</html>";
		}

		mMessageText.setText(messageText);
	}

	/**
	 * Returns the selected item list
	 * @return selected list
	 **/
    @Override
	public ArrayList getSelectedList()
	{
		return new ArrayList(mSelectedList);
	}

	/**
	 * Overrides the default setVisible method to ensure controls
	 * are updated before showing the dialog.
	 * @param b
	 */
    @Override
	public void setVisible(boolean b)
	{
		if (b)
		{
			// Note we should not be updating the available list unless we are
			// showing the panel. Doing this on a 'hide' action will result in
			// duplicates of the radio buttons appearing.
			updateAvailableTable();

			// centre on parent
			//setLocationRelativeTo(getOwner());
			super.pack();
			pack();

			// make sure it's at least 120 wide
			if (getWidth() < 120)
			{
				setSize(120, getHeight());
			}
			Utility.centerDialog(this);
		}
		super.setVisible(b);
	}

	/**
	 * Initializes the table structure and notifies listeners
	 * of the insertion.
	 * @param data
	 **/
	private void setData(List data)
	{
		int mRows = 0;
		avaRadioButton = new JRadioButton[data.size()];
		avaGroup = new ButtonGroup();

		for (Iterator aV = data.iterator(); aV.hasNext();)
		{
			String aString = (String) aV.next();

			if (aString.length() > 0)
			{
				avaRadioButton[mRows] = new JRadioButton(aString, false);
				avaGroup.add(avaRadioButton[mRows]);
				++mRows;
			}
		}

		if (mRows > 0)
		{
			avaRadioButton[0].setSelected(true);
		}

		if (lblCombo != null)
		{
			avaGroup.add(lblCombo);
		}
	}

	/**
	 * Cancels the dialog
	 **/
	private void close()
	{
		this.setVisible(false);
	}

	/**
	 * Initializes the components of the dialog
	 **/
	private void initComponents()
	{
		// Initialize basic dialog settings
		setModal(true);

		//setSize(new Dimension(240, 400));
		//setTitle(in_chooser);
		final Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());

		TitledBorder title =
				BorderFactory.createTitledBorder(null, "Select One");
		avaPane.setBorder(title);

		// Create these labels with " " to force them to layout correctly
		mMessageText = new JLabelPane();
		mMessageText.setBackground(contentPane.getBackground());
		setMessageText(null);

		// Create buttons
		okButton = new JButton(in_ok);
		okButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_ok"));
		cancelButton = new JButton(in_cancel);
		cancelButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_cancel"));

		final ActionListener eventListener = new ActionListener()
		{
            @Override
			public void actionPerformed(ActionEvent evt)
			{
				if (evt.getSource() == okButton)
				{
					selectedOK();
				}
				else if (evt.getSource() == cancelButton)
				{
					close();
				}
			}
		};

		okButton.addActionListener(eventListener);
		cancelButton.addActionListener(eventListener);

		// Add controls to content pane
		GridBagConstraints c;

		// Add message text
		c = new GridBagConstraints();
		Utility.buildConstraints(c, 0, 0, 3, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 4, 4, 4);
		contentPane.add(mMessageText, c);

		// Add available list
		c = new GridBagConstraints();
		Utility.buildConstraints(c, 0, 1, 3, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(4, 4, 4, 4);
		JScrollPane avaScrollPane = new JScrollPane();
		avaScrollPane.setViewportView(avaPane);
		contentPane.add(avaScrollPane, c);

		// Add 'OK' and 'Cancel buttons
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
		btnPanel.add(Box.createHorizontalGlue());
		btnPanel.add(okButton);
		btnPanel.add(Box.createHorizontalStrut(3));
		btnPanel.add(cancelButton);
		c = new GridBagConstraints();
		Utility.buildConstraints(c, 0, 2, 3, 1, 0, 0);
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 4, 4, 4);
		contentPane.add(btnPanel, c);
		
		okButton.setEnabled(true);
		cancelButton.setEnabled(true);

		Utility.installEscapeCloseOperation(this);
	}

	/**
	 * Selects an available item - invoked when the add button is pressed
	 **/
	private void selectedOK()
	{
		Object selectedItem = null;

		for (int i = 0; i < avaRadioButton.length; i++)
		{
			if (avaRadioButton[i].isSelected())
			{
				selectedItem = avaRadioButton[i].getText();

				break;
			}
		}

		if ((lblCombo != null) && lblCombo.isSelected())
		{
			selectedItem = cmbCombo.getSelectedItem();
		}

		mSelectedList.clear();

		if (selectedItem != null)
		{
			mSelectedList.add(selectedItem);
		}

		setVisible(false);
	}

	/**
	 * Updates the available table entries
	 **/
	private void updateAvailableTable()
	{
		final List mAvailableData = new ArrayList();

		for (Iterator it = mAvailableList.iterator(); it.hasNext();)
		{
			mAvailableData.add(it.next().toString());
		}

		setData(mAvailableData);

		int row = avaRadioButton.length;

		GridBagLayout gridbag = new GridBagLayout();
		avaPane.removeAll();
		avaPane.setLayout(gridbag);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		for (int i = 0; i < row; ++i)
		{
			int cr = i;
			c.anchor = GridBagConstraints.WEST;
			Utility.buildConstraints(c, 0, cr, 2, 1, 1, 0);
			gridbag.setConstraints(avaRadioButton[i], c);
			avaPane.add(avaRadioButton[i]);

			// if there are more than 10 items make a double row
			if ((row > 11) && (i < (row - 1)))
			{
				++i;
				c.anchor = GridBagConstraints.EAST;
				Utility.buildConstraints(c, 3, cr, 2, 1, 1, 0);
				gridbag.setConstraints(avaRadioButton[i], c);
				avaPane.add(avaRadioButton[i]);
			}
		}

		if (lblCombo != null)
		{
			Utility.buildConstraints(c, 0, row, 3, 1, 0, 0);
			gridbag.setConstraints(lblCombo, c);
			avaPane.add(lblCombo);

			Utility.buildConstraints(c, 0, row + 1, 3, 1, 0, 0);
			gridbag.setConstraints(cmbCombo, c);
			avaPane.add(cmbCombo);
		}
	}
}
