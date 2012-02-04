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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.Skill;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.system.LanguageBundle;

/**
 * <code>SkillBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
final class SkillBasePanel extends BasePanel<Skill>
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

	public void setKeyStat(PCStat stat)
	{
		if (stat == null)
		{
			cmbKeyStat.setSelectedItem(Constants.NONE);
		}
		else
		{
			for (PCStat aStat : Globals.getContext().ref.getConstructedCDOMObjects(PCStat.class))
			{
				if (aStat.equals(stat))
				{
					cmbKeyStat.setSelectedItem(aStat.getKeyName());

					break;
				}
			}
		}
	}

	public PCStat getKeyStat()
	{
		final int idx = cmbKeyStat.getSelectedIndex() - 1;

		if (idx < 0)
		{
			return null;
		}

		return Globals.getContext().ref.getOrderSortedCDOMObjects(PCStat.class).get(idx);
	}

	public void setTypesAvailableList(final List<Type> aList, final boolean sort)
	{
		pnlSkillType.setAvailableList(aList, sort);
	}

	public void setTypesSelectedList(final List<Type> aList, final boolean sort)
	{
		pnlSkillType.setSelectedList(aList, sort);
	}

	public Object[] getTypesSelectedList()
	{
		return pnlSkillType.getSelectedList();
	}

	@Override
	public void updateData(Skill thisPObject)
	{
		thisPObject.removeListFor(ListKey.TYPE);

		for (Object o : getTypesSelectedList())
		{
			thisPObject.addToListFor(ListKey.TYPE, Type.getConstant(o.toString()));
		}

		thisPObject.put(ObjectKey.USE_UNTRAINED, getIsUntrained());
		thisPObject.put(ObjectKey.EXCLUSIVE, getIsExclusive());
		thisPObject.put(ObjectKey.KEY_STAT, getKeyStat());
		thisPObject.put(ObjectKey.ARMOR_CHECK, SkillArmorCheck.values()[getArmorCheck()]);
	}

	@Override
	public void updateView(Skill thisSkill)
	{
		//
		// Populate the types
		//
		List<Type> availableList = new ArrayList<Type>();
		List<Type> selectedList = new ArrayList<Type>();

		for (Skill aSkill : Globals.getContext().ref.getConstructedCDOMObjects(Skill.class))
		{
			for (Type type : aSkill.getTrueTypeList(false))
			{
				if (!type.equals(Type.CUSTOM))
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
		for (Type type : thisSkill.getTrueTypeList(false))
		{
			if (!type.equals(Type.CUSTOM))
			{
				selectedList.add(type);
				availableList.remove(type);
			}
		}

		setTypesAvailableList(availableList, true);
		setTypesSelectedList(selectedList, true);

		setKeyStat(thisSkill.get(ObjectKey.KEY_STAT));
		setArmorCheck(thisSkill.getSafe(ObjectKey.ARMOR_CHECK).ordinal());
		setIsExclusive(thisSkill.getSafe(ObjectKey.EXCLUSIVE));
		Boolean untrained = thisSkill.get(ObjectKey.USE_UNTRAINED);
		if (untrained != null)
		{
			setIsUntrained(untrained);
		}
	}

	private void initComponentContents()
	{
		//
		// Initialize the contents of the skill's key stat combo
		//
		List<PCStat> statList = Globals.getContext().ref.getOrderSortedCDOMObjects(PCStat.class);
		List<String> availableList = new ArrayList<String>(statList.size() + 1);
		availableList.add(Constants.NONE);

		for (PCStat stat : statList)
		{
			availableList.add(stat.getKeyName());
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
		pnlSkillType = new TypePanel(LanguageBundle.getString("in_demEnterNewType"));

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

		//pnlSkillType.setHeader(LanguageBundle.getString("in_type"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlSkillType, gridBagConstraints);
	}
}
