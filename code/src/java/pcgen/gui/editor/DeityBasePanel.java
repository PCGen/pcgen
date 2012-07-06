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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Deity;
import pcgen.core.Description;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.WeaponProf;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.rules.context.LoadContext;
import pcgen.system.LanguageBundle;

/**
 * <code>DeityBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
final class DeityBasePanel extends BasePanel<Deity>
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
	 * @param alignment the deity alignment to set
	 */
	public void setDeityAlignment(PCAlignment alignment)
	{
		if (alignment != null && alignment.getSafe(ObjectKey.VALID_FOR_DEITY))
		{
			cmbDeityAlignment.setSelectedItem(alignment);
		}
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
	public void setFavoredWeaponsAvailableList(List<WeaponProf> aList, boolean sort)
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
	public void setFavoredWeaponsSelectedList(List<WeaponProf> aList, boolean sort)
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

	@Override
	public void updateData(Deity thisPObject)
	{
		if (getHolyItemText() == null || getHolyItemText().trim().length()>0)
		{
			thisPObject.put(StringKey.HOLY_ITEM, getHolyItemText());
		}
		else
		{
			thisPObject.remove(StringKey.HOLY_ITEM);
		}
		PCAlignment align = (PCAlignment) cmbDeityAlignment.getSelectedItem();
		thisPObject.put(ObjectKey.ALIGNMENT, align);

		LoadContext context = Globals.getContext();
		final String desc = getDescriptionText();
		final StringTokenizer tok = new StringTokenizer(".CLEAR\t"+desc, "\t");
		while (tok.hasMoreTokens())
		{
			context.unconditionallyProcess(thisPObject, "DESC", tok.nextToken());
		}
	
		thisPObject.put(ObjectKey.DESC_PI, getDescIsPI());

		//
		// Save favored weapon(s)
		//
		thisPObject.removeListFor(ListKey.DEITYWEAPON);
		if (getFavoredWeaponsAvailableList().length == 0)
		{
			thisPObject.addToListFor(ListKey.DEITYWEAPON,
					Globals.getContext().ref
							.getCDOMAllReference(WeaponProf.class));
		}
		else
		{
			for (Object o : getFavoredWeaponsSelectedList())
			{
				CDOMReference<WeaponProf> ref = CDOMDirectSingleRef
						.getRef(Globals.getContext().ref.silentlyGetConstructedCDOMObject(WeaponProf.class, o.toString()));
				thisPObject.addToListFor(ListKey.DEITYWEAPON, ref);
			}
		}
	}

	@Override
	public void updateView(Deity thisPObject)
	{
		setHolyItemText(thisPObject.get(StringKey.HOLY_ITEM));
		final StringBuffer buf = new StringBuffer();
		for ( final Description desc : thisPObject.getSafeListFor(ListKey.DESCRIPTION) )
		{
			if ( buf.length() != 0 )
			{
				buf.append("\t");
			}
			buf.append(desc.getPCCText());
		}
		setDescriptionText(buf.toString()); // don't want PI here
		setDescIsPI(thisPObject.getSafe(ObjectKey.DESC_PI));

		//
		// Initialize the contents of the deity's alignment combo
		//
		setDeityAlignment(thisPObject.get(ObjectKey.ALIGNMENT));

		//
		// Initialize the contents of the available and selected favored weapons lists
		//
		List<WeaponProf> selectedList = new ArrayList<WeaponProf>();
		List<WeaponProf> availableList = new ArrayList<WeaponProf>(Globals
				.getContext().ref.getConstructedCDOMObjects(WeaponProf.class));
		
		List<CDOMReference<WeaponProf>> dwp = thisPObject
				.getSafeListFor(ListKey.DEITYWEAPON);
		for (CDOMReference<WeaponProf> ref : dwp)
		{
			for (WeaponProf wp : ref.getContainedObjects())
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
		List<PCAlignment> availableList = new ArrayList<PCAlignment>();

		for (PCAlignment anAlignment : Globals.getContext().ref.getOrderSortedCDOMObjects(PCAlignment.class))
		{
			if (anAlignment.getSafe(ObjectKey.VALID_FOR_DEITY))
			{
				availableList.add(anAlignment);
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
		lblHolyItem.setText(LanguageBundle.getString("in_demHolyItem"));
		lblHolyItem.setDisplayedMnemonic(LanguageBundle.getMnemonic("in_mn_demHolyItem"));
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
		lblDeityAlignment.setText(LanguageBundle.getString("in_demDeityAlign"));
		lblDeityAlignment.setDisplayedMnemonic(LanguageBundle.getMnemonic("in_mn_demDeityAlign"));
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

		pnlFavoredWeapons.setHeader(LanguageBundle.getString("in_demFavWea"));

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlFavoredWeapons, gridBagConstraints);
	}
}
