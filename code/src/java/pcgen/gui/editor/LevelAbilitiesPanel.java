/*
 * LevelAbilitiesPanel.java
 * Copyright 2003 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on January 5, 2003, 10:00 AM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import pcgen.cdom.base.Constants;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.util.PropertyFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>LevelAbilitiesPanel</code>
 *
 * @author  James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
final class LevelAbilitiesPanel extends JPanel
{
	static final long serialVersionUID = -3457383056896786976L;
	private static final String[] levelTypes = new String[]{ "HD", "LEVEL" };
	private static final String[] abilityTypes = new String[]{ "DR", "SR", "CR", "SAB", "FEAT" };
	private JButton btnAdd;
	private JButton btnRemove;
	private JComboBoxEx cmbAbilityType;
	private JComboBoxEx cmbLevelType;
	private JLabel lblHeader;
	private JLabel lblSelected;
	private JList lstSelected;
	private JPanel pnlAddRemove;
	private JPanel pnlAvailable;
	private JPanel pnlHeader;
	private JPanel pnlSelected;
	private JScrollPane scpSelected;
	private JTextField txtAbility;
	private JTextField txtLevel;

	/**
	 * Creates a new LevelAbilitiesPanel
	 */
	LevelAbilitiesPanel()
	{
		super();
		initComponents();
		initComponentContents();
	}

	/**
	 * Method setSelectedList. Sets the selected level abilities values to the
	 * suppliedvalues.
	 *
	 * @param argSelected An arraylist of string level abiltity values.
	 * eg LEVEL:2:FEAT:Evasion or HD:2-7:CR:2
	 */
	void setSelectedList(List argSelected)
	{
		final JListModel lmd = (JListModel) lstSelected.getModel();

		for (int i = 0, x = argSelected.size(); i < x; ++i)
		{
			lmd.addElement(argSelected.get(i));
		}
	}

	/**
	 * Method getSelectedList. Returns the selected level abilities data as an
	 * array of abilities specifications. Each ability spec is a string.
	 *
	 * @return Object[] The selected ability values.
	 */
	Object[] getSelectedList()
	{
		return ((JListModel) lstSelected.getModel()).getElements();
	}

	private void btnAddActionPerformed()
	{
		String levelType = (String) cmbLevelType.getSelectedItem();
		String abilityType = (String) cmbAbilityType.getSelectedItem();

		if ((levelType == null) || (levelType.length() == 0) || (abilityType == null) || (abilityType.length() == 0)
		    || (txtLevel.getText().trim().length() == 0) || (txtAbility.getText().trim().length() == 0))
		{
			ShowMessageDelegate.showMessageDialog(
				"You must enter a level type, level, ability type and ability details.",
				Constants.APPLICATION_NAME,
				MessageType.ERROR);

			return;
		}

		StringBuffer newEntry = new StringBuffer();
		newEntry.append(levelType).append(':');
		newEntry.append(txtLevel.getText().trim()).append(':');
		newEntry.append(abilityType).append(':');
		newEntry.append(txtAbility.getText().trim());

		final JListModel lmd = (JListModel) lstSelected.getModel();
		lmd.addElement(newEntry.toString());
	}

	private void btnRemoveActionPerformed()
	{
		btnRemove.setEnabled(false);

		final JListModel lms = (JListModel) lstSelected.getModel();
		final Object[] x = lstSelected.getSelectedValues();

		for (int i = 0; i < x.length; ++i)
		{
			String[] entry = splitAbilityString((String) x[i]);

			if (entry != null)
			{
				cmbLevelType.setSelectedItem(entry[0]);
				txtLevel.setText(entry[1]);
				cmbAbilityType.setSelectedItem(entry[2]);
				txtAbility.setText(entry[3]);
			}

			lms.removeElement(x[i]);
		}
	}

	private void initComponentContents()
	{
	    // TODO This method currently does nothing?
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;
		JLabel lblTemp;

		cmbLevelType = new JComboBoxEx(levelTypes);
		cmbAbilityType = new JComboBoxEx(abilityTypes);
		txtLevel = new JTextField();
		txtAbility = new JTextField();

		//
		// There's got to be a better/easier way to do this...
		//
		try
		{
			btnAdd = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
			btnRemove = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		}
		catch (Exception exc)
		{
			btnAdd = new JButton(">");
			btnRemove = new JButton("<");
		}

		lblHeader = new JLabel();
		lblSelected = new JLabel();
		lstSelected = new JList(new JListModel(new ArrayList(), true));
		pnlHeader = new JPanel();
		pnlAvailable = new JPanel();
		pnlAddRemove = new JPanel();
		pnlSelected = new JPanel();
		scpSelected = new JScrollPane();

		// Layout the header panel
		pnlHeader.setLayout(new GridBagLayout());

		lblHeader = new JLabel("Level/Hit dice based abilities");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.weightx = 0.1;
		pnlHeader.add(lblHeader, gridBagConstraints);

		// Layout the available panel
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "");
		title1.setTitleJustification(TitledBorder.LEFT);
		pnlAvailable.setBorder(title1);
		pnlAvailable.setLayout(new GridBagLayout());

		lblTemp = new JLabel("Tag Type:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlAvailable.add(cmbLevelType, gridBagConstraints);

		lblTemp = new JLabel("Level Number/Hit dice range:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlAvailable.add(txtLevel, gridBagConstraints);

		lblTemp = new JLabel("Ability type:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlAvailable.add(cmbAbilityType, gridBagConstraints);

		lblTemp = new JLabel("Ability details:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlAvailable.add(txtAbility, gridBagConstraints);

//		lblTemp = new JLabel(" ");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 8;
//		gridBagConstraints.fill = GridBagConstraints.BOTH;
//		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
//		gridBagConstraints.anchor = GridBagConstraints.WEST;
//		gridBagConstraints.weightx = 0.1;
//		gridBagConstraints.weighty = 0.5;
//		pnlAvailable.add(lblTemp, gridBagConstraints);
		// Layout the add/remove panel
		pnlAddRemove.setLayout(new GridBagLayout());

		//btnAdd.setMnemonic(PropertyFactory.getMnemonic("in_mn_add"));
		//btnAdd.setText(PropertyFactory.getString("in_add"));
		//btnAdd.setPreferredSize(new Dimension(81, 26));
		btnAdd.setEnabled(true);
		btnAdd.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnAddActionPerformed();
				}
			});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlAddRemove.add(btnAdd, gridBagConstraints);

		//btnRemove.setMnemonic(PropertyFactory.getMnemonic("in_mn_remove"));
		//btnRemove.setText(PropertyFactory.getString("in_remove"));
		//btnRemove.setPreferredSize(new Dimension(81, 26));
		btnRemove.setEnabled(false);
		btnRemove.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					btnRemoveActionPerformed();
				}
			});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlAddRemove.add(btnRemove, gridBagConstraints);

		// Layout the selected panel
		pnlSelected.setLayout(new GridBagLayout());

		lblSelected.setText(PropertyFactory.getString("in_selected"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlSelected.add(lblSelected, gridBagConstraints);

		lstSelected.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent evt)
				{
					lstSelectedMouseClicked(evt);
				}
			});
		lstSelected.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				if (lstSelected.getSelectedIndex() >= 0)
				{
					lstSelected.ensureIndexIsVisible(lstSelected
						.getSelectedIndex());
				}
			}
		});
		scpSelected.setPreferredSize(new Dimension(90, 20));
		scpSelected.setViewportView(lstSelected);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.weighty = 1.0;
		pnlSelected.add(scpSelected, gridBagConstraints);

		this.setLayout(new GridBagLayout());

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 1, 2, 1);
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.0;
		this.add(pnlHeader, gridBagConstraints);

//		lblTemp.setText(PropertyFactory.getString("in_tag"));
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 1;
//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//		gridBagConstraints.insets = new Insets(2, 1, 2, 1);
//		gridBagConstraints.anchor = GridBagConstraints.WEST;
//		this.add(lblTemp, gridBagConstraints);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 1, 2, 1);
		gridBagConstraints.weightx = 0.4;
		gridBagConstraints.weighty = 1.0;
		this.add(pnlAvailable, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(2, 1, 2, 1);
		gridBagConstraints.weightx = 0.1;
		gridBagConstraints.weighty = 1.0;
		this.add(pnlAddRemove, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(2, 1, 2, 1);
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.weighty = 1.0;
		this.add(pnlSelected, gridBagConstraints);
	}

	//
	// Mouse click on selected list
	//
	private void lstSelectedMouseClicked(MouseEvent evt)
	{
		if (evt.getSource().equals(lstSelected))
		{
			if (EditUtil.isDoubleClick(evt, lstSelected, btnRemove))
			{
				btnRemoveActionPerformed();
			}
		}
	}

	/**
	 * Split a single ability string into an array of its components -
	 * the level type, level spec, ability type and ability details
	 *
	 * @param abilityString The string to be converted
	 * @return A String[4] array containing the elements of the ability string.
	 */
	private static String[] splitAbilityString(String abilityString)
	{
		String[] returnValue = null;

		int idx = abilityString.indexOf(":");

		if (idx >= 0)
		{
			returnValue = new String[4];
			returnValue[0] = abilityString.substring(0, idx);

			int prevIdx = idx;
			idx = abilityString.substring(prevIdx + 1).indexOf(":") + prevIdx + 1;
			returnValue[1] = abilityString.substring(prevIdx + 1, idx);
			prevIdx = idx;
			idx = abilityString.substring(prevIdx + 1).indexOf(":") + prevIdx + 1;
			returnValue[2] = abilityString.substring(prevIdx + 1, idx);
			returnValue[3] = abilityString.substring(idx + 1);
		}

		return returnValue;
	}
}
