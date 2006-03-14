/*
 * ClassBasePanel
 * Copyright 2003 (C) Bryan McRoberts <merton.monk@codemonkeypublishing.com >
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
 * Created on January 8, 2003, 8:15 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.util.PropertyFactory;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>ClassBasePanel</code>
 *
 * @author  Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 */
class ClassBasePanel extends BasePanel
{
	private JCheckBox chkVisible;
	private JCheckBox hasSubClass;
	private JCheckBox modToSkills;
	private JCheckBox multiPreReq;
	private JTextField abbreviation;
	private JTextField exClass;
	private JTextField exchangeLevel;
	private JTextField qualify;
	private JTextField startSkillPoints;
	private JTextField txtDisplayName;
	private TypePanel pnlTemplateTypes;

	/** Creates new form ClassBasePanel */
	public ClassBasePanel()
	{
		initComponents();
	}

	/**
	 * Set the available types list
	 * @param aList
	 * @param sort
	 */
	public void setTypesAvailableList(final List aList, final boolean sort)
	{
		pnlTemplateTypes.setAvailableList(aList, sort);
	}

	/**
	 * Set the selected types list
	 * @param aList
	 * @param sort
	 */
	public void setTypesSelectedList(final List aList, final boolean sort)
	{
		pnlTemplateTypes.setSelectedList(aList, sort);
	}

	/**
	 * Get the selected types list
	 * @return the selected types list
	 */
	public Object[] getTypesSelectedList()
	{
		return pnlTemplateTypes.getSelectedList();
	}

	public void updateData(PObject thisPObject)
	{
		if (!(thisPObject instanceof PCClass))
		{
			return;
		}

		PCClass obj = (PCClass) thisPObject;
		obj.setOutputName(txtDisplayName.getText().trim());
		obj.setAbbrev(abbreviation.getText().trim());
		obj.setLevelExchange(exchangeLevel.getText().trim());
//		obj.setSkillPoints(Integer.parseInt(startSkillPoints.getText().trim()));
		obj.setSkillPointFormula(startSkillPoints.getText().trim());
		obj.setQualifyString(qualify.getText().trim());
		obj.setExClass(exClass.getText().trim());
		obj.setHasSubClass(hasSubClass.getSelectedObjects() != null);
		obj.setModToSkills(modToSkills.getSelectedObjects() != null);
		obj.setMultiPreReqs(multiPreReq.getSelectedObjects() != null);
		obj.setVisible(chkVisible.getSelectedObjects() != null);

		Object[] sel = getTypesSelectedList();
		thisPObject.setTypeInfo(".CLEAR");

		for (int i = 0; i < sel.length; ++i)
		{
			thisPObject.setTypeInfo(sel[i].toString());
		}
	}

	public void updateView(PObject thisPObject)
	{
		if (!(thisPObject instanceof PCClass))
		{
			return;
		}

		Iterator e;
		String aString;

		//
		// Populate the types
		//
		List availableList = new ArrayList();
		List selectedList = new ArrayList();

		for (e = Globals.getClassList().iterator(); e.hasNext();)
		{
			final PCClass obj = (PCClass) e.next();

			for (int i = obj.getMyTypeCount(); i > 0;)
			{
				aString = obj.getMyType(--i);

				if (!aString.equals(Constants.s_CUSTOM))
				{
					if (!availableList.contains(aString))
					{
						availableList.add(aString);
					}
				}
			}
		}

		// remove this class's type from the available list and place into selected list
		for (int i = thisPObject.getMyTypeCount(); i > 0;)
		{
			aString = thisPObject.getMyType(--i);

			if (!aString.equals(Constants.s_CUSTOM))
			{
				selectedList.add(aString);
				availableList.remove(aString);
			}
		}

		PCClass obj = (PCClass) thisPObject;
		setTypesAvailableList(availableList, true);
		setTypesSelectedList(selectedList, true);
		txtDisplayName.setText(obj.getOutputName());
		abbreviation.setText(obj.getAbbrev());
		exchangeLevel.setText(obj.getLevelExchange());
//		startSkillPoints.setText(String.valueOf(obj.getSkillPoints()));
		startSkillPoints.setText(obj.getSkillPointFormula());
		qualify.setText(obj.getQualifyString());
		exClass.setText(obj.getExClass());
		hasSubClass.setSelected(obj.hasSubClass());
		modToSkills.setSelected(obj.getModToSkills());
		multiPreReq.setSelected(obj.multiPreReqs());
		chkVisible.setSelected(obj.isVisible());
	}

	private static GridBagConstraints buildConstraints(GridBagConstraints gridBagConstraints, int gridx, int gridy,
	    boolean useInsets)
	{
		gridBagConstraints.gridx = gridx;
		gridBagConstraints.gridy = gridy;

		if (useInsets)
		{
			gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		}

		return gridBagConstraints;
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		JLabel tempLabel;

		txtDisplayName = new JTextField();
		abbreviation = new JTextField();
		qualify = new JTextField();
		exchangeLevel = new JTextField();
		startSkillPoints = new JTextField();
		exClass = new JTextField();
		hasSubClass = new JCheckBox();
		modToSkills = new JCheckBox();
		chkVisible = new JCheckBox();
		multiPreReq = new JCheckBox();

		//pnlTemplateTypes = new AvailableSelectedPanel();
		pnlTemplateTypes = new TypePanel(PropertyFactory.getString("in_demEnterNewType"));

		setLayout(new GridBagLayout());

		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;

		tempLabel = new JLabel("Display Name");
		gridBagConstraints = buildConstraints(gridBagConstraints, 0, 0, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 1, 0, true);
		gridBagConstraints.gridwidth = 5;
		add(txtDisplayName, gridBagConstraints);

		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 0.16;

		tempLabel = new JLabel("ABB:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 0, 1, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 1, 1, true);
		add(abbreviation, gridBagConstraints);

		tempLabel = new JLabel("Ex-Class:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 2, 1, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 3, 1, true);
		add(exClass, gridBagConstraints);

		tempLabel = new JLabel("Exchange Level:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 4, 1, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 5, 1, true);
		add(exchangeLevel, gridBagConstraints);

		gridBagConstraints.weightx = 0.0;

		tempLabel = new JLabel("Has SubClass:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 0, 2, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 1, 2, true);
		add(hasSubClass, gridBagConstraints);

		tempLabel = new JLabel("Mod To Skills:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 2, 2, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 3, 2, true);
		add(modToSkills, gridBagConstraints);

		tempLabel = new JLabel("Starting Skill Points:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 4, 2, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 5, 2, true);
		add(startSkillPoints, gridBagConstraints);

		tempLabel = new JLabel("Qualify:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 0, 3, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 1, 3, true);
		add(qualify, gridBagConstraints);

		tempLabel = new JLabel("Multi-Class Pre-Reqs:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 2, 3, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 3, 3, true);
		add(multiPreReq, gridBagConstraints);

		tempLabel = new JLabel("Visible:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 4, 3, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 5, 3, true);
		add(chkVisible, gridBagConstraints);

		//pnlTemplateTypes.setHeader(PropertyFactory.getString("in_type"));
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 6;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlTemplateTypes, gridBagConstraints);
	}
}
