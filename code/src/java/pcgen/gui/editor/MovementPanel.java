/*
 * MovementPanel.java
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
 * Created on January 2, 2003, 12:00 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import pcgen.cdom.base.Constants;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.system.LanguageBundle;

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
 * <code>MovementPanel</code>
 *
 * @author  James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
final class MovementPanel extends JPanel
{
	static final long serialVersionUID = 3024201499870849149L;
	private static final String[] rateTypes = new String[]
		{
			"MOVE (Set movement rate)", "<unused>", "MOVECLONE (Adjust based on default rate)"
		};
	private JButton btnAdd;
	private JButton btnRemove;
	private JComboBoxEx cmbMoveType;
	private JComboBoxEx cmbRateType;
	private JLabel lblHeader;
	private JLabel lblSelected;
	private JList lstSelected;
	private JPanel pnlAddRemove;
	private JPanel pnlAvailable;
	private JPanel pnlHeader;
	private JPanel pnlSelected;
	private JScrollPane scpSelected;
	private JTextField txtMoveAmount;
	private JRadioButton[] rdbAdjustType;

	/**
	 * Creates a new MovementPanel
	 * @param isMoveOnly
	 */
	MovementPanel(boolean isMoveOnly)
	{
		super();
		initComponents(isMoveOnly);
		initComponentContents(isMoveOnly);
	}

	/**
	 * Method setMoveRateType. Sets the selected movement rate type to the
	 * supplied value. The values are defined in PCTemplate and PlayerCharacter.
	 * They are mapped here to MOVE, MOVEA and MOVECLONE respectively.
	 * @param moveRateType The movement rate type value.
	 */
	void setMoveRateType(int moveRateType)
	{
		cmbRateType.setSelectedIndex(moveRateType);
	}

	/**
	 * Method getMoveRateType.
	 *
	 * @return int The movement rate type, defined using the values in
	 * PCTemplate and PlayerCharacter.
	 */
	int getMoveRateType()
	{
		return cmbRateType.getSelectedIndex();
	}

	/**
	 * Method getMoveTypes. Returns an array of the the types of the selected
	 * movement data as a an array of strings. eg ["Walk", "Fly"]
	 *
	 * @return String[] The movement types.
	 */
	String[] getMoveTypes()
	{
		Object[] selected = getSelectedList();
		String[] types = new String[selected.length];

		for (int index = 0; index < selected.length; index++)
		{
			final String moveString = (String) selected[index];
			final int idx = moveString.indexOf(",");
			types[index] = moveString.substring(0, idx);
		}

		return types;
	}

	/**
	 * Method getMoveValues. Returns the selected movement data as a single
	 * comma delimited string.
	 *
	 * @return String The movement value string.
	 */
	String getMoveValues()
	{
		StringBuffer buffer = new StringBuffer();
		Object[] selected = getSelectedList();

		for (int index = 0; index < selected.length; index++)
		{
			if (index > 0)
			{
				buffer.append(',');
			}

			buffer.append(selected[index]);
		}

		return buffer.toString();
	}

	/**
	 * Method setSelectedList. Sets the selected movement values to the supplied
	 * values.
	 *
	 * @param argSelected A list of string movement values. eg Walk,*2 or
	 * Fly,40
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
	 * Method getSelectedList. Returns the selected movement data as an array
	 * of movement specification. Each movement spec is a string.
	 *
	 * @return Object[] The selected movement values.
	 */
	Object[] getSelectedList()
	{
		return ((JListModel) lstSelected.getModel()).getElements();
	}

	/**
	 * Method makeMoveString. Makes a string representation of a movement
	 * specification from the component items.
	 *
	 * @param rateType The type of movement
	 * @param moveRate The rate of the movemement
	 * @param multAmount The amount by which the move should be multiplied.
	 * @param movementMultOp The multiplication operation to be applied.
	 * @return String The resulting string eg Walk,*3 or Fly,40
	 */
	static String makeMoveString(String rateType, Double moveRate, Double multAmount, String movementMultOp)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(rateType).append(',');

		if ("*".equals(movementMultOp) || "/".equals(movementMultOp))
		{
			buffer.append(movementMultOp);
			buffer.append(multAmount);
		}
		else
		{
			buffer.append(moveRate.intValue());
		}

		return buffer.toString();
	}

	private void btnAddActionPerformed()
	{
		String moveType = (String) cmbMoveType.getSelectedItem();

		if ((moveType == null) || (moveType.length() == 0)
		    || (!rdbAdjustType[0].isSelected() && !rdbAdjustType[1].isSelected() && !rdbAdjustType[2].isSelected())
		    || (txtMoveAmount.getText().trim().length() == 0))
		{
			ShowMessageDelegate.showMessageDialog(
				"You must enter a movement type, adjustment and amount.",
				Constants.APPLICATION_NAME,
				MessageType.ERROR);

			return;
		}

		String newEntry = (String) cmbMoveType.getSelectedItem() + ",";

		if (rdbAdjustType[1].isSelected())
		{
			newEntry += "*";
		}
		else if (rdbAdjustType[2].isSelected())
		{
			newEntry += "/";
		}

		newEntry += txtMoveAmount.getText().trim();

		final JListModel lmd = (JListModel) lstSelected.getModel();
		lmd.addElement(newEntry);
	}

	private void btnRemoveActionPerformed()
	{
		btnRemove.setEnabled(false);

		final JListModel lms = (JListModel) lstSelected.getModel();
		final Object[] x = lstSelected.getSelectedValues();

		for (int i = 0; i < x.length; ++i)
		{
			String[] entry = splitMoveString((String) x[i]);

			if (entry != null)
			{
				cmbMoveType.setSelectedItem(entry[0]);

				if (entry[1].equals("*"))
				{
					rdbAdjustType[1].setSelected(true);
				}
				else if (entry[1].equals("/"))
				{
					rdbAdjustType[2].setSelected(true);
				}
				else
				{
					rdbAdjustType[0].setSelected(true);
				}

				txtMoveAmount.setText(entry[2]);
			}

			lms.removeElement(x[i]);
		}
	}

	private void initComponentContents(boolean isMoveOnly)
	{
		if (isMoveOnly)
		{
			cmbRateType.setModel(new DefaultComboBoxModel(new String[]{ rateTypes[0] }));
			cmbRateType.setSelectedIndex(0);
		}
		else
		{
			cmbRateType.setModel(new DefaultComboBoxModel(rateTypes));
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * @param isMoveOnly
	 */
	private void initComponents(boolean isMoveOnly)
	{
		GridBagConstraints gridBagConstraints;
		JLabel lblTemp;

		cmbRateType = new JComboBoxEx();
		cmbMoveType = new JComboBoxEx(new String[]{ "Walk", "Fly", "Swim", "ALL" });
		cmbMoveType.setEditable(true);
		rdbAdjustType = new JRadioButton[]
			{
				new JRadioButton("Add/Set to"), new JRadioButton("Multiply by"), new JRadioButton("Divide by")
			};

		ButtonGroup adjustTypeGroup = new ButtonGroup();
		adjustTypeGroup.add(rdbAdjustType[0]);
		adjustTypeGroup.add(rdbAdjustType[1]);
		adjustTypeGroup.add(rdbAdjustType[2]);

		txtMoveAmount = new JTextField();

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

		if (isMoveOnly)
		{
//			lblHeader = new JLabel("Movement");
//			gridBagConstraints = new GridBagConstraints();
//			gridBagConstraints.gridx = 0;
//			gridBagConstraints.gridy = 0;
//			gridBagConstraints.fill = GridBagConstraints.NONE;
//			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
//			gridBagConstraints.anchor = GridBagConstraints.NORTH;
//			gridBagConstraints.weightx = 0.1;
//			pnlHeader.add(lblHeader, gridBagConstraints);
		}
		else
		{
			lblHeader = new JLabel("Movement Rate Type of ");
			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.weightx = 0.1;
			pnlHeader.add(lblHeader, gridBagConstraints);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			gridBagConstraints.weightx = 0.4;
			pnlHeader.add(cmbRateType, gridBagConstraints);
		}

		// Layout the available panel
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, LanguageBundle.getString("in_demTag"));
		title1.setTitleJustification(TitledBorder.LEFT);
		pnlAvailable.setBorder(title1);
		pnlAvailable.setLayout(new GridBagLayout());

		lblTemp = new JLabel("Movement Type:");
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
		pnlAvailable.add(cmbMoveType, gridBagConstraints);

		if (isMoveOnly)
		{
			rdbAdjustType[0].setSelected(true);
		}
		else
		{
			lblTemp = new JLabel("Adjustment:");
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
			pnlAvailable.add(rdbAdjustType[0], gridBagConstraints);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 4;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			gridBagConstraints.weightx = 0.4;
			pnlAvailable.add(rdbAdjustType[1], gridBagConstraints);

			gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 5;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
			gridBagConstraints.weightx = 0.4;
			pnlAvailable.add(rdbAdjustType[2], gridBagConstraints);
		}

		lblTemp = new JLabel("Amount:");
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
		pnlAvailable.add(txtMoveAmount, gridBagConstraints);

//		lblTemp = new JLabel(" ");
//		gridBagConstraints = new GridBagConstraints();
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 8;
//		gridBagConstraints.fill = GridBagConstraints.BOTH;
//		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
//		gridBagConstraints.anchor = GridBagConstraints.WEST;
//		gridBagConstraints.weightx = 0.1;
//		gridBagConstraints.weighty = 0.5;
		pnlAvailable.add(lblTemp, gridBagConstraints);

		// Layout the add/remove panel
		pnlAddRemove.setLayout(new GridBagLayout());

		//btnAdd.setMnemonic(LanguageBundle.getMnemonic("in_mn_add"));
		//btnAdd.setText(LanguageBundle.getString("in_add"));
		//btnAdd.setPreferredSize(new Dimension(81, 26));
		btnAdd.setEnabled(true);
		btnAdd.addActionListener(new ActionListener()
			{
				@Override
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

		//btnRemove.setMnemonic(LanguageBundle.getMnemonic("in_mn_remove"));
		//btnRemove.setText(LanguageBundle.getString("in_remove"));
		//btnRemove.setPreferredSize(new Dimension(81, 26));
		btnRemove.setEnabled(false);
		btnRemove.addActionListener(new ActionListener()
			{
				@Override
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

		lblSelected.setText(LanguageBundle.getString("in_selected"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		pnlSelected.add(lblSelected, gridBagConstraints);

		lstSelected.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent evt)
				{
					lstSelectedMouseClicked(evt);
				}
			});
		lstSelected.addListSelectionListener(new ListSelectionListener()
			{
				@Override
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

//		lblTemp.setText(LanguageBundle.getString("in_tag"));
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
	 * Split a single move string inot an array of its components - the movement type,
	 * movement multiplier operation and movement amount (or multiplier value).
	 *
	 * @param moveString The string to be converted
	 * @return A String[3] array containing the elements of the movement string.
	 */
	private static String[] splitMoveString(String moveString)
	{
		String[] returnValue = null;

		final int idx = moveString.indexOf(",");

		if (idx >= 0)
		{
			returnValue = new String[3];
			returnValue[0] = moveString.substring(0, idx);

			if ((moveString.charAt(idx + 1) == '*') || (moveString.charAt(idx + 1) == '/'))
			{
				returnValue[1] = moveString.substring(idx + 1, idx + 2);
				returnValue[2] = moveString.substring(idx + 2);
			}
			else
			{
				returnValue[1] = "";
				returnValue[2] = moveString.substring(idx + 1);
			}
		}

		return returnValue;
	}
}
