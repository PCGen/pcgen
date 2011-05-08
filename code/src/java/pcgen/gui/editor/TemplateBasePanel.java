/*
 * TemplateBasePanel
 * Copyright 2002 (C) James Dempsey <jdempsey@users.sourceforge.net >
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
 * Created on December 13, 2002, 10:33 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubRace;
import pcgen.cdom.enumeration.SubRegion;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.SizeAdjustment;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Visibility;

/**
 * <code>TemplateBasePanel</code>
 *
 * @author  James Dempsey <jdempsey@users.sourceforge.net>
 */
public class TemplateBasePanel extends BasePanel
{
	private static final String[] genderLockValues =
			new String[]{"None", "Male", "Female", "Neuter"};
	private static final String[] visibleValues =
			new String[]{"No", "Yes", "Export", "Display"};
	private static final String[] sizeTitles =
			new String[]{"(no change)", "Fine", "Diminutive", "Tiny", "Small",
				"Medium", "Large", "Huge", "Gargantuan", "Colossal"};
	private static final String[] sizeAbbrev =
			new String[]{"", "F", "D", "T", "S", "M", "L", "H", "G", "C"};
	private JCheckBox chkRemovable;
	private JComboBoxEx cmbBonusSkillPoints;
	private JComboBoxEx cmbGenderLock;
	private JComboBoxEx cmbNonProficiencyPenalty;
	private JComboBoxEx cmbSize;
	private JComboBoxEx cmbVisible;
	private JPanel pnlTemplateMisc;
	private JTextField txtCR;
	private JTextField txtLevelAdj;
	private JTextField txtSubRace;
	private JTextField txtSubRegion;

	//	private AvailableSelectedPanel pnlTemplateTypes;
	private TypePanel pnlTemplateTypes;

	/** Creates new form TemplateBasePanel */
	public TemplateBasePanel()
	{
		initComponents();
		initComponentContents();
	}

	/**
	 * Set the boinus skill points
	 * @param bonusSkillPoints
	 */
	public void setBonusSkillPoints(final int bonusSkillPoints)
	{
		if ((bonusSkillPoints >= 0)
			&& (bonusSkillPoints < cmbBonusSkillPoints.getItemCount()))
		{
			cmbBonusSkillPoints.setSelectedIndex(bonusSkillPoints);
		}
	}

	/**
	 * Get the bonus skill points
	 * @return bonus skill points
	 */
	public int getBonusSkillPoints()
	{
		return cmbBonusSkillPoints.getSelectedIndex();
	}

	/**
	 * Set the CR
	 * @param argCR
	 */
	public void setCR(final float argCR)
	{
		txtCR.setText(String.valueOf(argCR));
	}

	/**
	 * Get the CR
	 * @return the CR
	 */
	public float getCR()
	{
		return Float.parseFloat(txtCR.getText());
	}

	/**
	 * Set the Gender Lock
	 * @param aString
	 */
	public void setGenderLock(final String aString)
	{
		if (aString.length() == 0)
		{
			cmbGenderLock.setSelectedItem(Constants.NONE);
		}
		else
		{
			for (int i = genderLockValues.length - 1; i >= 0; --i)
			{
				if (genderLockValues[i].equals(aString))
				{
					cmbGenderLock.setSelectedItem(genderLockValues[i]);

					break;
				}
			}
		}
	}

	/**
	 * Get the gender lock
	 * @return the gender lock
	 */
	public String getGenderLock()
	{
		return (String) cmbGenderLock.getSelectedItem();
	}

	/**
	 * Set a flag to see if its removable
	 * @param isRemovable
	 */
	public void setIsRemovable(final boolean isRemovable)
	{
		chkRemovable.setSelected(isRemovable);
	}

	/**
	 * Get the removable flag
	 * @return the removable flag
	 */
	public boolean getIsRemovable()
	{
		return chkRemovable.isSelected();
	}

	/**
	 * Set the level adjustment
	 * @param levelAdj
	 */
	public void setLevelAdjustment(final Formula levelAdj)
	{
		txtLevelAdj.setText(levelAdj == null ? "" : levelAdj.toString());
	}

	/**
	 * Get the level adjustment
	 * @return the level adjustment
	 */
	public String getLevelAdjustment()
	{
		return txtLevelAdj.getText();
	}

	/**
	 * Set the penalty for non-proficiency
	 * @param nonProficiencyPenalty
	 */
	public void setNonProficiencyPenalty(final int nonProficiencyPenalty)
	{
		int npIdx;
		if (nonProficiencyPenalty > 0)
		{
			npIdx = 0; // no change
		}
		else
		{
			npIdx = -nonProficiencyPenalty + 1;
		}
		if (npIdx >= cmbNonProficiencyPenalty.getItemCount())
		{
			npIdx = -1; // no selection
		}
		cmbNonProficiencyPenalty.setSelectedIndex(npIdx);
	}

	/**
	 * Get the penalty for non-proficiency
	 * @return the penalty for non-proficiency
	 */
	public int getNonProficiencyPenalty()
	{
		int npIdx = cmbNonProficiencyPenalty.getSelectedIndex();
		if (npIdx == 0)
		{
			npIdx = 1;
		}
		else
		{
			npIdx = -(npIdx - 1);
		}
		return npIdx;
	}

	/**
	 * Set the sub race
	 * @param aString
	 */
	public void setSubRace(final String aString)
	{
		if (Constants.NONE.equals(aString) || (aString == null)
			|| ("".equals(aString)))
		{
			txtSubRace.setText("");
		}
		else
		{
			txtSubRace.setText(aString);
		}
	}

	/**
	 * Get the sub race
	 * @return the sub race
	 */
	public String getSubRace()
	{
		if ((txtSubRace.getText() == null)
			|| (txtSubRace.getText().trim().length() == 0))
		{
			return Constants.NONE;
		}
		return txtSubRace.getText();
	}

	/**
	 * Set the sub region
	 * @param aString
	 */
	public void setSubRegion(final String aString)
	{
		if (Constants.NONE.equals(aString) || (aString == null)
			|| ("".equals(aString)))
		{
			txtSubRegion.setText("");
		}
		else
		{
			txtSubRegion.setText(aString);
		}
	}

	/**
	 * Get the sub region
	 * @return the sub region
	 */
	public String getSubRegion()
	{
		if ((txtSubRegion.getText() == null)
			|| (txtSubRegion.getText().trim().length() == 0))
		{
			return Constants.NONE;
		}
		return txtSubRegion.getText();
	}

	/**
	 * Set the template size
	 * @param aString
	 */
	public void setTemplateSize(final Formula aString)
	{
		cmbSize.setSelectedIndex(0);
		if (aString == null)
		{
			return;
		}

		for (int index = 0; index < sizeAbbrev.length; index++)
		{
			if (sizeAbbrev[index].equals(aString.toString()))
			{
				cmbSize.setSelectedIndex(index);

				break;
			}
		}
	}

	/**
	 * Get the template size
	 * @return the template size
	 */
	public String getTemplateSize()
	{
		int index = cmbSize.getSelectedIndex();

		if ((index >= 0) && (index < sizeAbbrev.length))
		{
			return sizeAbbrev[index];
		}
		return "";
	}

	/**
	 * Set the list of available types
	 * @param aList
	 * @param sort
	 */
	public void setTypesAvailableList(final List<Type> aList, final boolean sort)
	{
		pnlTemplateTypes.setAvailableList(aList, sort);
	}

	/**
	 * Set the list of selected types
	 * @param aList
	 * @param sort
	 */
	public void setTypesSelectedList(final List<Type> aList, final boolean sort)
	{
		pnlTemplateTypes.setSelectedList(aList, sort);
	}

	/** 
	 * Get the list of selected types
	 * @return the list of selected types
	 */
	public Object[] getTypesSelectedList()
	{
		return pnlTemplateTypes.getSelectedList();
	}

	/**
	 * Set whether the tempalte should be visible or not
	 * @param aNumber
	 */
	public void setVisible(final Visibility vis)
	{
		cmbVisible.setSelectedIndex(vis.ordinal());
	}

	/**
	 * Get the visibility of the template
	 * @return the visibility of the template
	 */
	public Visibility getVisible()
	{
		return Visibility.values()[cmbVisible.getSelectedIndex()];
	}

	@Override
	public void updateData(PObject thisPObject)
	{
		PCTemplate thisPCTemplate = (PCTemplate) thisPObject;
		thisPCTemplate.put(ObjectKey.REMOVABLE, getIsRemovable());
		try
		{
			Gender gender = Gender.valueOf(getGenderLock());
			thisPCTemplate.put(ObjectKey.GENDER_LOCK, gender);
		}
		catch (IllegalArgumentException e)
		{
			//This is okay, just indicates it wasn't a Gender
		}
		thisPCTemplate.put(ObjectKey.VISIBILITY, getVisible());
		String subRegion = getSubRegion();
		if (subRegion.equals(thisPCTemplate.getDisplayName()))
		{
			thisPCTemplate.put(ObjectKey.USETEMPLATENAMEFORSUBREGION, true);
			thisPCTemplate.put(ObjectKey.SUBREGION, null);
		}
		else
		{
			thisPCTemplate.put(ObjectKey.USETEMPLATENAMEFORSUBREGION, null);
			thisPCTemplate.put(ObjectKey.SUBREGION, SubRegion
					.getConstant(subRegion));
		}
		String subRace = getSubRace();
		if (subRace.equals(thisPCTemplate.getDisplayName()))
		{
			thisPCTemplate.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, true);
			thisPCTemplate.put(ObjectKey.SUBRACE, null);
		}
		else
		{
			thisPCTemplate.put(ObjectKey.USETEMPLATENAMEFORSUBRACE, null);
			thisPCTemplate.put(ObjectKey.SUBRACE, SubRace.getConstant(subRace));
		}
		if (getBonusSkillPoints() != 0)
		{
			thisPCTemplate.put(IntegerKey.BONUS_CLASS_SKILL_POINTS, getBonusSkillPoints());
		}
		thisPCTemplate.put(ObjectKey.CR_MODIFIER, new BigDecimal(getCR()));
		if (getLevelAdjustment() != null && getLevelAdjustment().length() > 0)
		{
			thisPCTemplate.put(FormulaKey.LEVEL_ADJUSTMENT, FormulaFactory.getFormulaFor(getLevelAdjustment()));
		}
		thisPCTemplate.put(IntegerKey.NONPP, getNonProficiencyPenalty());
		String sz = getTemplateSize();
		SizeAdjustment size = Globals.getContext().ref.getAbbreviatedObject(
				SizeAdjustment.class, sz);
		Formula sizeFormula = null;
		if (size == null)
		{
			if (sz != null && sz.length() > 0)
			{
				sizeFormula = FormulaFactory.getFormulaFor(sz);
			}
		}
		else
		{
			sizeFormula = new FixedSizeFormula(size);
		}
		if (sizeFormula != null)
		{
			thisPCTemplate.put(FormulaKey.SIZE, sizeFormula);
		}

		//
		// Save types
		//
		thisPObject.removeListFor(ListKey.TYPE);
		for (Object o : getTypesSelectedList())
		{
			thisPObject.addToListFor(ListKey.TYPE, Type.getConstant(o.toString()));
		}
	}

	@Override
	public void updateView(PObject thisPObject)
	{
		PCTemplate thisPCTemplate = (PCTemplate) thisPObject;

		//
		// Populate the types
		//
		List<Type> availableList = new ArrayList<Type>();
		List<Type> selectedList = new ArrayList<Type>();

		for (PCTemplate aTemplate : Globals.getContext().ref.getConstructedCDOMObjects(PCTemplate.class))
		{
			for (Type type : aTemplate.getTrueTypeList(false))
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
		// remove this template's type from the available list and place into selected list
		//
		for (Type type : thisPCTemplate.getTrueTypeList(false))
		{
			if (!type.equals(Type.CUSTOM))
			{
				selectedList.add(type);
				availableList.remove(type);
			}
		}

		setTypesAvailableList(availableList, true);
		setTypesSelectedList(selectedList, true);

		setIsRemovable(thisPCTemplate.isRemovable());
		Gender genderLock = thisPCTemplate.get(ObjectKey.GENDER_LOCK);
		if (genderLock == null)
		{
			setGenderLock("None");
		}
		else
		{
			setGenderLock(genderLock.toString());
		}
		setVisible(thisPCTemplate.getSafe(ObjectKey.VISIBILITY));
		setSubRegion(getTemplateSubRegion(thisPCTemplate));
		setSubRace(getTemplateSubRace(thisPCTemplate));
		setBonusSkillPoints(thisPCTemplate.getSafe(IntegerKey.BONUS_CLASS_SKILL_POINTS));
		setNonProficiencyPenalty(thisPCTemplate.getSafe(IntegerKey.NONPP));
		setCR(thisPCTemplate.getCR(-1, -1));
		setLevelAdjustment(thisPCTemplate.get(FormulaKey.LEVEL_ADJUSTMENT));
		setTemplateSize(thisPCTemplate.get(FormulaKey.SIZE));
	}

	private String getTemplateSubRace(PCTemplate thisPCTemplate)
	{
		/*
		 * TODO Note this isn't appropriate for the Editor to consolidate two
		 * behaviors of a token - techincally a bug - should be fixed in new
		 * editor system
		 */
		SubRace sr = thisPCTemplate.get(ObjectKey.SUBRACE);
		if (sr == null)
		{
			if (thisPCTemplate.getSafe(ObjectKey.USETEMPLATENAMEFORSUBRACE))
			{
				return thisPCTemplate.getDisplayName();
			}
			return Constants.NONE;
		}
		return sr.toString();
	}

	private String getTemplateSubRegion(PCTemplate thisPCTemplate)
	{
		/*
		 * TODO Note this isn't appropriate for the Editor to consolidate two
		 * behaviors of a token - techincally a bug - should be fixed in new
		 * editor system
		 */
		SubRegion sr = thisPCTemplate.get(ObjectKey.SUBREGION);
		if (sr == null)
		{
			if (thisPCTemplate.getSafe(ObjectKey.USETEMPLATENAMEFORSUBREGION))
			{
				return thisPCTemplate.getDisplayName();
			}
			return Constants.NONE;
		}
		return sr.toString();
	}

	private void initComponentContents()
	{
		cmbGenderLock.setModel(new DefaultComboBoxModel(genderLockValues));
		cmbVisible.setModel(new DefaultComboBoxModel(visibleValues));

		String[] values = new String[20];

		for (int i = 0; i < values.length; ++i)
		{
			values[i] = String.valueOf(i);
		}

		cmbBonusSkillPoints.setModel(new DefaultComboBoxModel(values));
		values = new String[11];

		values[0] = "(no change)";
		for (int i = 0; i < values.length - 1; ++i)
		{
			values[i + 1] = String.valueOf(-i);
		}

		cmbNonProficiencyPenalty.setModel(new DefaultComboBoxModel(values));
		cmbSize.setModel(new DefaultComboBoxModel(sizeTitles));
	}

	/*
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gridBagConstraints;
		JLabel tempLabel;

		pnlTemplateMisc = new JPanel();
		chkRemovable = new JCheckBox();
		cmbGenderLock = new JComboBoxEx();
		cmbBonusSkillPoints = new JComboBoxEx();
		cmbNonProficiencyPenalty = new JComboBoxEx();
		cmbVisible = new JComboBoxEx();
		txtCR = new JTextField();
		txtLevelAdj = new JTextField();
		cmbSize = new JComboBoxEx();
		txtSubRegion = new JTextField();
		txtSubRace = new JTextField();

		//pnlTemplateTypes = new AvailableSelectedPanel();
		pnlTemplateTypes =
				new TypePanel(PropertyFactory.getString("in_demEnterNewType"));

		setLayout(new GridBagLayout());

		pnlTemplateMisc.setLayout(new GridBagLayout());

		tempLabel = new JLabel("Visible");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbVisible, gridBagConstraints);

		tempLabel = new JLabel("Removable");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(chkRemovable, gridBagConstraints);

		tempLabel = new JLabel("Gender Locked");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbGenderLock, gridBagConstraints);

		tempLabel = new JLabel("Wpn Non Prof Penalty");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbNonProficiencyPenalty, gridBagConstraints);

		tempLabel = new JLabel("Bonus Skill Pts / Level");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbBonusSkillPoints, gridBagConstraints);

		tempLabel = new JLabel("CR");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtCR, gridBagConstraints);

		tempLabel = new JLabel("Level Adjustment");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtLevelAdj, gridBagConstraints);

		tempLabel = new JLabel("Size");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(cmbSize, gridBagConstraints);

		tempLabel = new JLabel("Sub Race");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 5;

		//gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtSubRace, gridBagConstraints);

		tempLabel = new JLabel("Sub Region");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.1;
		pnlTemplateMisc.add(tempLabel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;

		//gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.weightx = 0.4;
		pnlTemplateMisc.add(txtSubRegion, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(pnlTemplateMisc, gridBagConstraints);

		//pnlTemplateTypes.setHeader(PropertyFactory.getString("in_type"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlTemplateTypes, gridBagConstraints);
	}
}
