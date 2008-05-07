/*
 * SkillBasePanel.java
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
 * Created on November 5, 2002, 2:03 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import pcgen.cdom.base.Constants;
import pcgen.core.*;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.util.PropertyFactory;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>SkillBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
final class SkillBasePanel extends BasePanel
{
	private static final String[] acheckValues = new String[]{ "No", "Yes", "Non-proficiency", "Weight" };
	private JCheckBox chkExclusive;
	private JCheckBox chkUntrained;
	private JComboBoxEx cmbArmorCheck;
	private JComboBoxEx cmbKeyStat;
	private JLabel lblArmorCheck;
	private JLabel lblKeyStat;
	private JPanel pnlSkillMisc;

//	private AvailableSelectedPanel pnlSkillType;
	private TypePanel pnlSkillType;

	/** Creates new form SkillBasePanel */
	public SkillBasePanel()
	{
		initComponents();
		initComponentContents();
	}

	public void setArmorCheck(final int aCheck)
	{
		if ((aCheck >= 0) && (aCheck < acheckValues.length))
		{
			cmbArmorCheck.setSelectedItem(acheckValues[aCheck]);
		}
	}

	public int getArmorCheck()
	{
		return cmbArmorCheck.getSelectedIndex();
	}

	public void setIsExclusive(final boolean isExclusive)
	{
		chkExclusive.setSelected(isExclusive);
	}

	public boolean getIsExclusive()
	{
		return chkExclusive.isSelected();
	}

	public void setIsUntrained(final boolean isUntrained)
	{
		chkUntrained.setSelected(isUntrained);
	}

	public boolean getIsUntrained()
	{
		return chkUntrained.isSelected();
	}

	public void setKeyStat(final String aString)
	{
		if (aString.length() == 0)
		{
			cmbKeyStat.setSelectedItem(Constants.s_NONE);
		}
		else
		{
			for (int i = SettingsHandler.getGame().getUnmodifiableStatList().size() - 1; i >= 0; --i)
			{
				PCStat aStat = SettingsHandler.getGame().getUnmodifiableStatList().get(i);

				if (aStat.getAbb().equals(aString))
				{
					cmbKeyStat.setSelectedItem(aStat.getKeyName());

					break;
				}
			}
		}
	}

	public String getKeyStat()
	{
		final int idx = cmbKeyStat.getSelectedIndex() - 1;

		if (idx < 0)
		{
			return "";
		}

		return (SettingsHandler.getGame().getUnmodifiableStatList().get(idx)).getAbb();
	}

	public void setTypesAvailableList(final List<String> aList, final boolean sort)
	{
		pnlSkillType.setAvailableList(aList, sort);
	}

	public void setTypesSelectedList(final List<String> aList, final boolean sort)
	{
		pnlSkillType.setSelectedList(aList, sort);
	}

	public Object[] getTypesSelectedList()
	{
		return pnlSkillType.getSelectedList();
	}

	public void updateData(PObject thisPObject)
	{
		Skill thisSkill = (Skill) thisPObject;
		Object[] sel = getTypesSelectedList();
		thisPObject.setTypeInfo(".CLEAR");

		for (int i = 0; i < sel.length; ++i)
		{
			thisSkill.setTypeInfo(sel[i].toString());
		}

		thisSkill.setUntrained(getIsUntrained());
		thisSkill.setIsExclusive(getIsExclusive());
		thisSkill.setKeyStat(getKeyStat());
		thisSkill.setACheck(getArmorCheck());
	}

	public void updateView(PObject thisPObject)
	{
		Iterator e;
		String aString;
		Skill thisSkill = (Skill) thisPObject;

		//
		// Populate the types
		//
		List<String> availableList = new ArrayList<String>();
		List<String> selectedList = new ArrayList<String>();

		for (e = Globals.getSkillList().iterator(); e.hasNext();)
		{
			final Skill aSkill = (Skill) e.next();

			for (String type : aSkill.getTypeList(false))
			{
				if (!type.equals(Constants.s_CUSTOM))
				{
					if (!availableList.contains(type))
					{
						availableList.add(type);
					}
				}
			}
		}

		//
		// remove this skill's type from the available list and place into selected list
		//
		for (String type : thisSkill.getTypeList(false))
		{
			if (!type.equals(Constants.s_CUSTOM))
			{
				selectedList.add(type);
				availableList.remove(type);
			}
		}

		setTypesAvailableList(availableList, true);
		setTypesSelectedList(selectedList, true);

		setKeyStat(thisSkill.getKeyStat());
		setArmorCheck(thisSkill.getACheck());
		setIsExclusive(thisSkill.isExclusive());
		setIsUntrained(thisSkill.isUntrained());
	}

	private void initComponentContents()
	{
		//
		// Initialize the contents of the skill's key stat combo
		//
		List<String> availableList = new ArrayList<String>(SettingsHandler.getGame().getUnmodifiableStatList().size() + 1);
		availableList.add(Constants.s_NONE);

		for (Iterator e = SettingsHandler.getGame().getUnmodifiableStatList().iterator(); e.hasNext();)
		{
			availableList.add(((PCStat) e.next()).getKeyName());
		}

		cmbKeyStat.setModel(new DefaultComboBoxModel(availableList.toArray()));
		cmbArmorCheck.setModel(new DefaultComboBoxModel(acheckValues));
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;

		pnlSkillMisc = new JPanel();
		lblKeyStat = new JLabel();
		cmbKeyStat = new JComboBoxEx();
		lblArmorCheck = new JLabel();
		cmbArmorCheck = new JComboBoxEx();
		chkUntrained = new JCheckBox();
		chkExclusive = new JCheckBox();

		//pnlSkillType = new AvailableSelectedPanel();
		pnlSkillType = new TypePanel(PropertyFactory.getString("in_demEnterNewType"));

		setLayout(new GridBagLayout());

		pnlSkillMisc.setLayout(new GridBagLayout());

		lblKeyStat.setText("Key Stat");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlSkillMisc.add(lblKeyStat, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlSkillMisc.add(cmbKeyStat, gridBagConstraints);

		lblArmorCheck.setText("Armor Check");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.1;
		pnlSkillMisc.add(lblArmorCheck, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlSkillMisc.add(cmbArmorCheck, gridBagConstraints);

		chkUntrained.setText("Use Untrained");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlSkillMisc.add(chkUntrained, gridBagConstraints);

		chkExclusive.setText("Exclusive");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		pnlSkillMisc.add(chkExclusive, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlSkillMisc, gridBagConstraints);

		//pnlSkillType.setHeader(PropertyFactory.getString("in_type"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlSkillType, gridBagConstraints);
	}
}
