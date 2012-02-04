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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CategorizedCDOMReference;
import pcgen.cdom.reference.Qualifier;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.rules.context.LoadContext;
import pcgen.system.LanguageBundle;
import pcgen.util.StringPClassUtil;
import pcgen.util.enumeration.Visibility;

/**
 * <code>ClassBasePanel</code>
 *
 * @author  Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 */
class ClassBasePanel extends BasePanel<PCClass>
{
	private JCheckBox chkVisible;
	private JCheckBox modToSkills;
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
	public void setTypesAvailableList(final List<Type> aList, final boolean sort)
	{
		pnlTemplateTypes.setAvailableList(aList, sort);
	}

	/**
	 * Set the selected types list
	 * @param aList
	 * @param sort
	 */
	public void setTypesSelectedList(final List<Type> aList, final boolean sort)
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

	@Override
	public void updateData(PCClass obj)
	{
		obj.put(StringKey.OUTPUT_NAME, txtDisplayName.getText().trim());
		LoadContext context = Globals.getContext();
		context.ref.registerAbbreviation(obj, abbreviation.getText().trim());
		if (exchangeLevel.getText().trim().length() > 0)
		{
			context.unconditionallyProcess(obj, "EXCHANGELEVEL", exchangeLevel.getText().trim());
		}
		String form = startSkillPoints.getText().trim();
		if (form.length() > 0)
		{
			Formula f = FormulaFactory.getFormulaFor(form);
			obj.put(FormulaKey.START_SKILL_POINTS, f);
		}
		obj.removeListFor(ListKey.QUALIFY);
		if (qualify.getText().trim().length() > 0)
		{
			context.unconditionallyProcess(obj, "QUALIFY", qualify.getText());
		}
		if (exClass.getText().trim().length() > 0)
		{
			context.unconditionallyProcess(obj, "EXCLASS", exClass.getText().trim());
		}
		obj.put(ObjectKey.MOD_TO_SKILLS, modToSkills.getSelectedObjects() != null);
		obj.put(ObjectKey.VISIBILITY, chkVisible.getSelectedObjects() == null ? Visibility.HIDDEN : Visibility.DEFAULT);

		obj.removeListFor(ListKey.TYPE);
		for (Object o : getTypesSelectedList())
		{
			obj.addToListFor(ListKey.TYPE, Type.getConstant(o.toString()));
		}
	}

	@Override
	public void updateView(PCClass thisPObject)
	{
		//
		// Populate the types
		//
		List<Type> availableList = new ArrayList<Type>();
		List<Type> selectedList = new ArrayList<Type>();

		for (PCClass obj : Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class))
		{
			for (Type type : obj.getTrueTypeList(false))
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

		// remove this class's type from the available list and place into selected list
		for (Type type : thisPObject.getTrueTypeList(false))
		{
			if (!type.equals(Type.CUSTOM))
			{
				selectedList.add(type);
				availableList.remove(type);
			}
		}

		PCClass obj = (PCClass) thisPObject;
		setTypesAvailableList(availableList, true);
		setTypesSelectedList(selectedList, true);
		txtDisplayName.setText(OutputNameFormatting.getOutputName(obj));
		abbreviation.setText(obj.getAbbrev());
		LoadContext context = Globals.getContext();
		String[] le = context.unparseSubtoken(obj, "EXCHANGELEVEL");
		exchangeLevel.setText(le == null ? "" : le[0]);
		Formula spf = obj.get(FormulaKey.START_SKILL_POINTS);
		startSkillPoints.setText(spf == null ? "" : spf.toString());
		List<Qualifier> qualList = obj.getListFor(ListKey.QUALIFY);
		if (qualList != null)
		{
			List<String> ol = new ArrayList<String>();
			for (Qualifier qual : qualList)
			{
				String cl = StringPClassUtil.getStringFor(qual.getQualifiedClass());
				CDOMReference<? extends Loadable> ref = qual.getQualifiedReference();
				if (ref instanceof CategorizedCDOMReference)
				{
					Category<?> cat =
							((CategorizedCDOMReference<?>) ref).getCDOMCategory();
					cl += '=' + cat.toString();
				}
				ol.add(cl + "|" + ref.getLSTformat(false));
			}
			qualify.setText(StringUtil.join(ol, "|"));
		}
		String[] exc = context.unparseSubtoken(obj, "EXCLASS");
		exClass.setText(exc == null ? "" : exc[0]);
		Boolean mts = obj.get(ObjectKey.MOD_TO_SKILLS);
		modToSkills.setSelected(mts == null ? true : mts);
		chkVisible.setSelected(obj.getSafe(ObjectKey.VISIBILITY).equals(Visibility.DEFAULT));
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
		modToSkills = new JCheckBox();
		chkVisible = new JCheckBox();

		//pnlTemplateTypes = new AvailableSelectedPanel();
		pnlTemplateTypes = new TypePanel(LanguageBundle.getString("in_demEnterNewType"));

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

		//gridBagConstraints = buildConstraints(gridBagConstraints, 3, 3, true);

		tempLabel = new JLabel("Visible:");
		gridBagConstraints = buildConstraints(gridBagConstraints, 4, 3, true);
		add(tempLabel, gridBagConstraints);

		gridBagConstraints = buildConstraints(gridBagConstraints, 5, 3, true);
		add(chkVisible, gridBagConstraints);

		//pnlTemplateTypes.setHeader(LanguageBundle.getString("in_type"));
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
