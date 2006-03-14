/*
 * DeityBasePanel.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on November 1, 2002, 9:27 AM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import pcgen.core.*;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.util.PropertyFactory;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>DeityBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
final class DeityBasePanel extends BasePanel
{
	private AvailableSelectedPanel pnlFavoredWeapons;
	private DescriptionPanel pnlDescription;
	private JComboBoxEx cmbDeityAlignment;
	private JLabel lblDeityAlignment;
	private JLabel lblHolyItem;
	private JPanel pnlDeityAlignment;
	private JPanel pnlHolyItem;
	private JTextField txtHolyItem;

	/** Creates new form DeityBasePanel */
	DeityBasePanel()
	{
		initComponents();
		initComponentContents();
	}

	/**
	 * Set deity alignment
	 * @param aString
	 */
	public void setDeityAlignment(String aString)
	{
		for (Iterator e = SettingsHandler.getGame().getUnmodifiableAlignmentList().iterator(); e.hasNext();)
		{
			final PCAlignment anAlignment = (PCAlignment) e.next();

			if (anAlignment.isValidForDeity())
			{
				if (anAlignment.getKeyName().equals(aString))
				{
					cmbDeityAlignment.setSelectedItem(anAlignment.getName());
				}
			}
		}
	}

	/**
	 * Get deity alignment
	 * @return deity alignment
	 */
	public String getDeityAlignment()
	{
		String aString = (String) cmbDeityAlignment.getSelectedItem();

		if (aString != null)
		{
			final int dix = SettingsHandler.getGame().getIndexOfAlignment(aString);

			if (dix >= 0)
			{
				return SettingsHandler.getGame().getShortAlignmentAtIndex(dix);
			}
		}

		return null;
	}

	/**
	 * Set description is PI flag
	 * @param descIsPI
	 */
	public void setDescIsPI(final boolean descIsPI)
	{
		pnlDescription.setDescIsPI(descIsPI);
	}

	/**
	 * Get description is PI flag
	 * @return true if PI
	 */
	public boolean getDescIsPI()
	{
		return pnlDescription.getDescIsPI();
	}

	/**
	 * Set description text
	 * @param aString
	 */
	public void setDescriptionText(String aString)
	{
		pnlDescription.setText(aString);
	}

	/**
	 * Get description text
	 * @return description text
	 */
	public String getDescriptionText()
	{
		return pnlDescription.getText();
	}

	/**
	 * Set favoured weapons available list
	 * @param aList
	 * @param sort
	 */
	public void setFavoredWeaponsAvailableList(List aList, boolean sort)
	{
		pnlFavoredWeapons.setAvailableList(aList, sort);
	}

	/**
	 * Get the favoured weapons available list
	 * @return List of favoured weapons available
	 */
	public Object[] getFavoredWeaponsAvailableList()
	{
		return pnlFavoredWeapons.getAvailableList();
	}

	/**
	 * setFavoredWeaponsSelectedList
	 * @param aList
	 * @param sort
	 */
	public void setFavoredWeaponsSelectedList(List aList, boolean sort)
	{
		pnlFavoredWeapons.setSelectedList(aList, sort);
	}

	/**
	 * getFavoredWeaponsSelectedList
	 * @return getFavoredWeaponsSelectedList
	 */
	public Object[] getFavoredWeaponsSelectedList()
	{
		return pnlFavoredWeapons.getSelectedList();
	}

	/**
	 * Set holy item text
	 * @param aString
	 */
	public void setHolyItemText(String aString)
	{
		txtHolyItem.setText(aString);
	}

	/**
	 * Get holy item text
	 * @return holy item text
	 */
	public String getHolyItemText()
	{
		return txtHolyItem.getText().trim();
	}

	public void updateData(PObject thisPObject)
	{
		((Deity) thisPObject).setHolyItem(getHolyItemText());
		((Deity) thisPObject).setAlignment(getDeityAlignment());
		thisPObject.setDescription(getDescriptionText());
		thisPObject.setDescIsPI(getDescIsPI());

		//
		// Save favored weapon(s)
		//
		String aString;

		if (getFavoredWeaponsAvailableList().length == 0)
		{
			aString = "Any";
		}
		else
		{
			Object[] sel = getFavoredWeaponsSelectedList();
			aString = EditUtil.delimitArray(sel, '|');
		}

		((Deity) thisPObject).setFavoredWeapon(aString);
	}

	public void updateView(PObject thisPObject)
	{
		setHolyItemText(((Deity) thisPObject).getHolyItem());
		setDescriptionText(thisPObject.getDescription()); // don't want PI here
		setDescIsPI(thisPObject.getDescIsPI());

		//
		// Initialize the contents of the deity's alignment combo
		//
		setDeityAlignment(((Deity) thisPObject).getAlignment());

		//
		// Initialize the contents of the available and selected favored weapons lists
		//
		List selectedList = new ArrayList();
		List availableList = Globals.getWeaponProfArrayCopy();
		final StringTokenizer aTok = new StringTokenizer(((Deity) thisPObject).getFavoredWeapon(), "|", false);

		while (aTok.hasMoreTokens())
		{
			String deityWeap = aTok.nextToken();

			if (deityWeap.equalsIgnoreCase("ALL") || "ANY".equalsIgnoreCase(deityWeap))
			{
				selectedList.addAll(availableList);
				availableList.clear();

				break;
			}

			final WeaponProf wp = Globals.getWeaponProfNamed(deityWeap);
			if (wp != null)
			{
				selectedList.add(wp);
				availableList.remove(wp);
			}
		}

		setFavoredWeaponsAvailableList(availableList, true);
		setFavoredWeaponsSelectedList(selectedList, true);
	}

	private void initComponentContents()
	{
		//
		// Initialize the contents of the deity's alignment combo
		//
		List availableList = new ArrayList();

		for (Iterator e = SettingsHandler.getGame().getUnmodifiableAlignmentList().iterator(); e.hasNext();)
		{
			final PCAlignment anAlignment = (PCAlignment) e.next();

			if (anAlignment.isValidForDeity())
			{
				availableList.add(anAlignment.getName());
			}
		}

		cmbDeityAlignment.setModel(new DefaultComboBoxModel(availableList.toArray()));
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		pnlHolyItem = new JPanel();
		lblHolyItem = new JLabel();
		txtHolyItem = new JTextField();
		pnlDeityAlignment = new JPanel();
		lblDeityAlignment = new JLabel();
		cmbDeityAlignment = new JComboBoxEx();

		pnlDescription = new DescriptionPanel();

		pnlFavoredWeapons = new AvailableSelectedPanel();

		setLayout(new GridBagLayout());

		pnlHolyItem.setLayout(new GridBagLayout());

		lblHolyItem.setLabelFor(txtHolyItem);
		lblHolyItem.setText(PropertyFactory.getString("in_demHolyItem"));
		lblHolyItem.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_demHolyItem"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlHolyItem.add(lblHolyItem, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 1.0;
		pnlHolyItem.add(txtHolyItem, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlHolyItem, gridBagConstraints);

		pnlDeityAlignment.setLayout(new GridBagLayout());

		lblDeityAlignment.setLabelFor(cmbDeityAlignment);
		lblDeityAlignment.setText(PropertyFactory.getString("in_demDeityAlign"));
		lblDeityAlignment.setDisplayedMnemonic(PropertyFactory.getMnemonic("in_mn_demDeityAlign"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlDeityAlignment.add(lblDeityAlignment, gridBagConstraints);

		cmbDeityAlignment.setPreferredSize(new Dimension(180, 25));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlDeityAlignment.add(cmbDeityAlignment, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlDeityAlignment, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlDescription, gridBagConstraints);

		pnlFavoredWeapons.setHeader(PropertyFactory.getString("in_demFavWea"));

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlFavoredWeapons, gridBagConstraints);
	}
}
