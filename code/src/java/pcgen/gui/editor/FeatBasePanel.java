/*
 * FeatBasePanel.java
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
 * Created on January 12, 2003, 11:20 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.Description;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.WholeNumberField;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.util.DecimalNumberField;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Visibility;

/**
 * <code>FeatBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public class FeatBasePanel extends BasePanel
{
//	private static final String[] visibleValues = new String[]{ "No", "Yes", "Export", "Display" };
	private DecimalNumberField txtCost;
	private DescriptionPanel pnlDescription;
	private JCheckBox chkMultiple;
	private JCheckBox chkStack;
	private JComboBoxEx cmbVisible;
	private JLabel lblCost;
	private JLabel lblSpellLevels;
	private JLabel lblVisible;
	private JPanel pnlMisc;

	//private AvailableSelectedPanel pnlFeatType;
	private TypePanel pnlFeatType;
	private WholeNumberField txtSpellLevels;

	/** Creates new form SkillBasePanel */
	public FeatBasePanel()
	{
		initComponents();
		initComponentContents();
	}

	/**
	 * Set cost
	 * @param argCost
	 */
	public void setCost(final double argCost)
	{
		txtCost.setValue(argCost);
	}

	/**
	 * Get cost
	 * @return cost
	 */
	public double getCost()
	{
		return txtCost.getValue();
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
	 * Return TRUE if description is PI
	 * @return TRUE if description is PI
	 */
	public boolean getDescIsPI()
	{
		return pnlDescription.getDescIsPI();
	}

	/**
	 * Set description
	 * @param aString
	 */
	public void setDescriptionText(String aString)
	{
		pnlDescription.setText(aString);
	}

	/**
	 * Get description
	 * @return description
	 */
	public String getDescriptionText()
	{
		return pnlDescription.getText();
	}

	/**
	 * Set multiples flag
	 * @param argMultiples
	 */
	public void setMultiples(final boolean argMultiples)
	{
		chkMultiple.setSelected(argMultiples);
	}

	/**
	 * Return TRUE if multiples is on
	 * @return TRUE if multiples is on
	 */
	public boolean getMultiples()
	{
		return chkMultiple.isSelected();
	}

	/**
	 * Set spell levels
	 * @param argSpellLevels
	 */
	public void setSpellLevels(final int argSpellLevels)
	{
		txtSpellLevels.setValue(argSpellLevels);
	}

	/**
	 * Get spell levels
	 * @return spell levels
	 */
	public int getSpellLevels()
	{
		return txtSpellLevels.getValue();
	}

	/**
	 * Set stacks flag
	 * @param argStacks
	 */
	public void setStacks(final boolean argStacks)
	{
		chkStack.setSelected(argStacks);
	}

	/**
	 * Return TRUE if it stacks
	 * @return TRUE if it stacks
	 */
	public boolean getStacks()
	{
		return chkStack.isSelected();
	}

	/**
	 * Set Types available list
	 * @param aList
	 * @param sort
	 */
	public void setTypesAvailableList(final List<String> aList, final boolean sort)
	{
		pnlFeatType.setAvailableList(aList, sort);
	}

	/**
	 * Set types selected list
	 * @param aList
	 * @param sort
	 */
	public void setTypesSelectedList(final List<String> aList, final boolean sort)
	{
		pnlFeatType.setSelectedList(aList, sort);
	}

	/**
	 * Get types selected list
	 * @return types selected list
	 */
	public Object[] getTypesSelectedList()
	{
		return pnlFeatType.getSelectedList();
	}

	/**
	 * Set visible
	 * @param aNumber
	 */
	public void setVisible(final Visibility vis)
	{
		cmbVisible.setSelectedIndex(vis.ordinal());
	}

	/**
	 * Get visible
	 * @return visible
	 */
	public Visibility getVisible()
	{
		return Visibility.values()[cmbVisible.getSelectedIndex()];
	}

	public void updateData(PObject thisPObject)
	{
		Ability thisFeat = (Ability) thisPObject;
		final String desc = getDescriptionText();
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
			AbilityLstToken.class);
		AbilityLstToken tokenParser = (AbilityLstToken) tokenMap.get("DESC");
		if (tokenParser != null)
		{
			final StringTokenizer tok = new StringTokenizer(desc, "\t");
			while (tok.hasMoreTokens())
			{
				tokenParser.parse(thisFeat, tok.nextToken());
			}
		}

		thisFeat.setDescIsPI(getDescIsPI());
		thisFeat.setMultiples(getMultiples() ? "Y" : "N");
		thisFeat.setStacks(getStacks() ? "Y" : "N");
		thisFeat.setVisibility(getVisible());
		thisFeat.setCost(Double.toString(getCost()));
		thisFeat.setAddSpellLevel(getSpellLevels());

		Object[] sel = getTypesSelectedList();
		thisFeat.setTypeInfo(".CLEAR");

		for (int i = 0; i < sel.length; ++i)
		{
			thisFeat.setTypeInfo(sel[i].toString());
		}
	}

	public void updateView(PObject thisPObject)
	{
		String aString;
		Iterator e;
		Ability thisFeat = (Ability) thisPObject;

		final StringBuffer buf = new StringBuffer();
		for ( final Description desc : thisPObject.getDescriptionList() )
		{
			if ( buf.length() == 0 )
			{
				buf.append("\t");
			}
			buf.append(desc.getPCCText());
		}
		setDescriptionText(buf.toString()); // don't want PI here
		setDescIsPI(thisPObject.getDescIsPI());

		//
		// Populate the types
		//
		List<String> availableList = new ArrayList<String>();
		List<String> selectedList = new ArrayList<String>();

		for (e = Globals.getAbilityKeyIterator("FEAT"); e.hasNext();)
		{
			final Ability anAbility = (Ability) e.next();

			for (int i = anAbility.getMyTypeCount(); i > 0;)
			{
				aString = anAbility.getMyType(--i);

				if (!aString.equals(Constants.s_CUSTOM))
				{
					if (!availableList.contains(aString))
					{
						availableList.add(aString);
					}
				}
			}
		}

		// remove this feat's type from the available list and place into selected list
		for (int i = thisFeat.getMyTypeCount(); i > 0;)
		{
			aString = thisFeat.getMyType(--i);

			if (!aString.equals(Constants.s_CUSTOM))
			{
				selectedList.add(aString);
				availableList.remove(aString);
			}
		}

		setTypesAvailableList(availableList, true);
		setTypesSelectedList(selectedList, true);

		setVisible(thisFeat.getVisibility());
		setMultiples(thisFeat.isMultiples());
		setStacks(thisFeat.isStacks());
		setCost(thisFeat.getCost());
		setSpellLevels(thisFeat.getAddSpellLevel());
	}

	private void initComponentContents()
	{
		Vector<String> visibleValues = new Vector<String>();
		for (Visibility vis : Visibility.values())
		{
			visibleValues.add(vis.toString());
		}
			
		cmbVisible.setModel(new DefaultComboBoxModel(visibleValues));
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gbc;

		pnlDescription = new DescriptionPanel();

		//pnlFeatType = new AvailableSelectedPanel();
		pnlFeatType = new TypePanel(PropertyFactory.getString("in_demEnterNewType"));
		pnlMisc = new JPanel();
		lblCost = new JLabel();
		lblVisible = new JLabel();
		cmbVisible = new JComboBoxEx();
		chkMultiple = new JCheckBox();
		chkStack = new JCheckBox();
		txtCost = new DecimalNumberField(0, 5);
		lblSpellLevels = new JLabel();
		txtSpellLevels = new WholeNumberField(0, 3);

		setLayout(new GridBagLayout());

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		add(pnlDescription, gbc);

		//pnlFeatType.setHeader(PropertyFactory.getString("in_type"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(pnlFeatType, gbc);

		pnlMisc.setLayout(new GridBagLayout());

		lblCost.setText("Cost");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.1;
		pnlMisc.add(lblCost, gbc);

		lblVisible.setText("Visible");
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.1;
		pnlMisc.add(lblVisible, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.4;
		gbc.insets = new Insets(2, 5, 2, 5);
		pnlMisc.add(cmbVisible, gbc);

		chkMultiple.setText("Multiples allowed");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		pnlMisc.add(chkMultiple, gbc);

		chkStack.setText("Stacks");
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		pnlMisc.add(chkStack, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.weightx = 0.4;
		pnlMisc.add(txtCost, gbc);

		lblSpellLevels.setText("Spell Levels");
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		gbc.anchor = GridBagConstraints.EAST;
		pnlMisc.add(lblSpellLevels, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 5, 2, 5);
		pnlMisc.add(txtSpellLevels, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		add(pnlMisc, gbc);
	}
}
