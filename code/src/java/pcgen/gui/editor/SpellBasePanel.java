/*
 * SkillBasePanel.java
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on January 20, 2003, 3:36 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Description;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.core.spell.Spell;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.WholeNumberField;
import pcgen.rules.context.LoadContext;
import pcgen.util.DecimalNumberField;
import pcgen.util.PropertyFactory;

/**
 * <code>SpellBasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public class SpellBasePanel extends BasePanel
{
	private DecimalNumberField txtCost;
	private DescriptionPanel pnlDescription;
	private JButton btnChooseDescriptor;
	private JCheckBox chkPotionAllowed;
	private JCheckBox chkRingAllowed;
	private JCheckBox chkScrollAllowed;
	private JCheckBox chkWandAllowed;
	private JComboBoxEx cmbCastingTime;
	private JComboBoxEx cmbComponents;
	private JComboBoxEx cmbDuration;
	private JComboBoxEx cmbRange;
	private JComboBoxEx cmbSavingThrow;
	private JComboBoxEx cmbSchool;
	private JComboBoxEx cmbSpellRes;
	private JComboBoxEx cmbStat;
	private JComboBoxEx cmbSubschool;
	private JComboBoxEx cmbTarget;
	private JLabel lblCastingTime;
	private JLabel lblComponents;
	private JLabel lblCost;
	private JLabel lblDescriptor;
	private JLabel lblDuration;
	private JLabel lblPotionAllowed;
	private JLabel lblRange;
	private JLabel lblRingAllowed;
	private JLabel lblSavingThrow;
	private JLabel lblSchool;
	private JLabel lblScrollAllowed;
	private JLabel lblSpellRes;
	private JLabel lblStat;
	private JLabel lblSubschool;
	private JLabel lblTarget;
	private JLabel lblWandAllowed;
	private JLabel lblXpCost;
	private JPanel pnlMagicTypes;
	private JTextField txtDescriptor;
	private WholeNumberField txtXpCost;

	/** Creates new form SpellBasePanel */
	public SpellBasePanel()
	{
		initComponents();
		initComponentContents();
	}

	@Override
	public void updateData(PObject thisPObject)
	{
		String aString;
		final Spell s = (Spell) thisPObject;

		LoadContext context = Globals.getContext();
		final String desc = pnlDescription.getText();
		final StringTokenizer tok = new StringTokenizer(".CLEAR\t"+desc, "\t");
		while (tok.hasMoreTokens())
		{
			context.unconditionallyProcess(thisPObject, "DESC", tok.nextToken());
		}
		s.put(ObjectKey.DESC_PI, pnlDescription.getDescIsPI());

		s.removeListFor(ListKey.COMPONENTS);
		context.unconditionallyProcess(s, "COMPS", (String) cmbComponents
				.getSelectedItem());
		s.removeListFor(ListKey.CASTTIME);
		context.unconditionallyProcess(s, "CASTTIME", (String) cmbCastingTime
				.getSelectedItem());
		s.removeListFor(ListKey.RANGE);
		context.unconditionallyProcess(s, "RANGE", (String) cmbRange
				.getSelectedItem());
		s.removeListFor(ListKey.SAVE_INFO);
		context.unconditionallyProcess(s, "SAVEINFO", (String) cmbSavingThrow
				.getSelectedItem());
		s.removeListFor(ListKey.SPELL_RESISTANCE);
		context.unconditionallyProcess(s, "SPELLRES", (String) cmbSpellRes
				.getSelectedItem());
		s.removeListFor(ListKey.DURATION);
		context.unconditionallyProcess(s, "DURATION", (String) cmbDuration
				.getSelectedItem());
		s.removeListFor(ListKey.SPELL_SCHOOL);
		context.unconditionallyProcess(s, "SCHOOL", (String) cmbSchool
				.getSelectedItem());
		
		s.removeListFor(ListKey.SPELL_SUBSCHOOL);
		aString = (String) cmbSubschool.getSelectedItem();
		if ((aString != null) && !aString.equals("(None)"))
		{
			context.unconditionallyProcess(s, "SUBSCHOOL", (String) cmbSubschool
					.getSelectedItem());
		}

		aString = (String) cmbTarget.getSelectedItem();
		s.put(StringKey.TARGET_AREA, aString);

		// Added to support spell descriptors, Michael Osterlie
		s.removeListFor(ListKey.SPELL_DESCRIPTOR);
		aString = txtDescriptor.getText();
		StringTokenizer tokenizer = new StringTokenizer(aString, ",");
		while(tokenizer.hasMoreTokens())
		{
			s.addToListFor(ListKey.SPELL_DESCRIPTOR, tokenizer.nextToken());
		}
		if (txtCost.getValue() > 0.0d)
		{
			s.put(ObjectKey.COST, new BigDecimal(txtCost.getValue()));
		}
		else
		{
			s.remove(ObjectKey.COST);
		}
		s.put(IntegerKey.XP_COST, txtXpCost.getValue());
		Object stat = cmbStat.getSelectedItem();
		if (stat instanceof PCStat)
		{
			s.put(ObjectKey.SPELL_STAT, (PCStat) stat);
		}

		//
		// potion defaults to not-creatable if not in list; scroll and wand to creatable
		//
		s.removeListFor(ListKey.ITEM);
		s.removeListFor(ListKey.PROHIBITED_ITEM);
		if (chkPotionAllowed.isSelected())
		{
			s.addToListFor(ListKey.ITEM, Type.POTION);
		}

		if (!chkRingAllowed.isSelected())
		{
			s.addToListFor(ListKey.PROHIBITED_ITEM, Type.RING);
		}

		if (!chkScrollAllowed.isSelected())
		{
			s.addToListFor(ListKey.PROHIBITED_ITEM, Type.SCROLL);
		}

		if (!chkWandAllowed.isSelected())
		{
			s.addToListFor(ListKey.PROHIBITED_ITEM, Type.WAND);
		}
	}

	@Override
	public void updateView(PObject thisPObject)
	{
		Spell thisSpell = (Spell) thisPObject;
		final StringBuffer buf = new StringBuffer();
		for ( final Description desc : thisPObject.getSafeListFor(ListKey.DESCRIPTION) )
		{
			if ( buf.length() != 0 )
			{
				buf.append("\t");
			}
			buf.append(desc.getPCCText());
		}
		pnlDescription.setText(buf.toString()); // don't want PI here
		pnlDescription.setDescIsPI(thisSpell.getSafe(ObjectKey.DESC_PI));
		cmbComponents.setSelectedItem(thisSpell.getListAsString(ListKey.COMPONENTS));
		cmbCastingTime.setSelectedItem(thisSpell.getListAsString(ListKey.CASTTIME));
		cmbRange.setSelectedItem(thisSpell.getListAsString(ListKey.RANGE));
		cmbTarget.setSelectedItem(thisSpell.getSafe(StringKey.TARGET_AREA));
		cmbDuration.setSelectedItem(thisSpell.getListAsString(ListKey.DURATION));
		cmbSavingThrow.setSelectedItem(thisSpell.getListAsString(ListKey.SAVE_INFO));
		cmbSpellRes.setSelectedItem(thisSpell.getListAsString(ListKey.SPELL_RESISTANCE));
		cmbSchool.setSelectedItem(thisSpell.getListAsString(ListKey.SPELL_SCHOOL));
		cmbSubschool.setSelectedItem(thisSpell.getListAsString(ListKey.SPELL_SUBSCHOOL));
		txtDescriptor.setText(StringUtil.join(thisSpell
				.getListFor(ListKey.SPELL_DESCRIPTOR), ","));

		if (cmbSubschool.getSelectedIndex() < 0)
		{
			cmbSubschool.setSelectedIndex(0);
		}

		cmbStat.setSelectedItem(thisSpell.get(ObjectKey.SPELL_STAT));

		txtCost.setValue(thisSpell.getSafe(ObjectKey.COST).doubleValue());
		txtXpCost.setValue(thisSpell.getSafe(IntegerKey.XP_COST));

		chkPotionAllowed.setSelected(thisSpell.isAllowed(Type.POTION));
		chkRingAllowed.setSelected(thisSpell.isAllowed(Type.RING));
		chkScrollAllowed.setSelected(thisSpell.isAllowed(Type.SCROLL));
		chkWandAllowed.setSelected(thisSpell.isAllowed(Type.WAND));
	}

	private void initComponentContents()
	{
		cmbCastingTime.setModel(new DefaultComboBoxModel(Globals.getCastingTimesSet().toArray()));
		cmbRange.setModel(new DefaultComboBoxModel(Globals.getRangesSet().toArray()));
		cmbSpellRes.setModel(new DefaultComboBoxModel(Globals.getSrSet().toArray()));
		cmbSchool.setModel(new DefaultComboBoxModel(SettingsHandler.getGame().getUnmodifiableSchoolsList().toArray()));
		cmbSavingThrow.setModel(new DefaultComboBoxModel(Globals.getSaveInfoSet().toArray()));
		cmbTarget.setModel(new DefaultComboBoxModel(Globals.getTargetSet().toArray()));
		cmbComponents.setModel(new DefaultComboBoxModel(Globals.getComponentSet().toArray()));
		cmbDuration.setModel(new DefaultComboBoxModel(Globals.getDurationSet().toArray()));
		cmbStat.setModel(new DefaultComboBoxModel(Globals.getContext().ref.getOrderSortedCDOMObjects(PCStat.class).toArray()));

		List<String> subschools = new ArrayList<String>(10);
		subschools.add("(None)");
		subschools.addAll(Globals.getSubschools());
		cmbSubschool.setModel(new DefaultComboBoxModel(subschools.toArray()));
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		GridBagConstraints gbc;

		pnlDescription = new DescriptionPanel();
		lblComponents = new JLabel();
		cmbComponents = new JComboBoxEx();
		lblCastingTime = new JLabel();
		cmbCastingTime = new JComboBoxEx();
		lblRange = new JLabel();
		cmbRange = new JComboBoxEx();
		lblTarget = new JLabel();
		cmbTarget = new JComboBoxEx();
		lblDuration = new JLabel();
		cmbDuration = new JComboBoxEx();
		lblSavingThrow = new JLabel();
		cmbSavingThrow = new JComboBoxEx();
		lblSpellRes = new JLabel();
		cmbSpellRes = new JComboBoxEx();
		lblCost = new JLabel();
		txtCost = new DecimalNumberField(0.0, 6);
		lblSchool = new JLabel();
		cmbSchool = new JComboBoxEx();
		lblSubschool = new JLabel();
		cmbSubschool = new JComboBoxEx();
		lblDescriptor = new JLabel();
		txtDescriptor = new JTextField(10);
		btnChooseDescriptor = new JButton(new ChooseDescriptorAction(this));
		lblStat = new JLabel();
		cmbStat = new JComboBoxEx();
		lblXpCost = new JLabel();
		txtXpCost = new WholeNumberField(0, 6);
		pnlMagicTypes = new JPanel();
		lblPotionAllowed = new JLabel();
		chkPotionAllowed = new JCheckBox();
		chkRingAllowed = new JCheckBox();
		lblRingAllowed = new JLabel();
		lblScrollAllowed = new JLabel();
		chkScrollAllowed = new JCheckBox();
		lblWandAllowed = new JLabel();
		chkWandAllowed = new JCheckBox();

		setLayout(new GridBagLayout());

		pnlDescription.setPreferredSize(new Dimension(150, 40));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 8;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(2, 4, 2, 2);
		gbc.weightx = 1.0;
		gbc.weighty = 0.3;
		add(pnlDescription, gbc);

		lblComponents.setText(PropertyFactory.getString("in_demComponents"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblComponents, gbc);

		lblCastingTime.setText(PropertyFactory.getString("in_demCastingTime"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblCastingTime, gbc);

		cmbCastingTime.setEditable(true);
		cmbCastingTime.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.25;
		add(cmbCastingTime, gbc);

		lblRange.setText(PropertyFactory.getString("in_demRange"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblRange, gbc);

		cmbRange.setEditable(true);
		cmbRange.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbRange, gbc);

		lblTarget.setText(PropertyFactory.getString("in_demTargetArea"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblTarget, gbc);

		cmbTarget.setEditable(true);
		cmbTarget.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbTarget, gbc);

		lblDuration.setText(PropertyFactory.getString("in_demDuration"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblDuration, gbc);

		cmbDuration.setEditable(true);
		cmbDuration.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbDuration, gbc);

		lblSavingThrow.setText(PropertyFactory.getString("in_demSavingThrow"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblSavingThrow, gbc);

		cmbSavingThrow.setEditable(true);
		cmbSavingThrow.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 7;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbSavingThrow, gbc);

		lblSpellRes.setText(PropertyFactory.getString("in_demSpellResistance"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblSpellRes, gbc);

		cmbSpellRes.setEditable(true);
		cmbSpellRes.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 8;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbSpellRes, gbc);

		lblCost.setText(PropertyFactory.getString("in_demComponentCost"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblCost, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 9;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(txtCost, gbc);

		lblSchool.setText(PropertyFactory.getString("in_demSchool"));
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 2;
		gbc.insets = new Insets(2, 10, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblSchool, gbc);

		cmbSchool.setEditable(true);
		cmbSchool.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbSchool, gbc);

		lblSubschool.setText(PropertyFactory.getString("in_demSubschool"));
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 3;
		gbc.insets = new Insets(2, 10, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblSubschool, gbc);

		cmbSubschool.setEditable(true);
		cmbSubschool.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.25;
		add(cmbSubschool, gbc);

		lblDescriptor.setText(PropertyFactory.getString( "in_demDescriptor"));
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 4;
		gbc.insets = new Insets(2, 10, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblDescriptor, gbc);

		JPanel pnlDescriptor = new JPanel(new GridBagLayout());
		pnlDescriptor.setPreferredSize(new Dimension(80, 27));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 0, 0, 2);
		pnlDescriptor.add(txtDescriptor, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 2, 0, 0);
		pnlDescriptor.add(btnChooseDescriptor, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 0.25;
		add(pnlDescriptor, gbc);

		lblStat.setText(PropertyFactory.getString("in_demStat"));
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 5;
		gbc.insets = new Insets(2, 10, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblStat, gbc);

		cmbStat.setEditable(true);
		cmbStat.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.25;
		add(cmbStat, gbc);

		lblXpCost.setText(PropertyFactory.getString("in_demXPCost"));
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 9;
		gbc.insets = new Insets(2, 10, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		add(lblXpCost, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 9;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(txtXpCost, gbc);

		cmbComponents.setEditable(true);
		cmbComponents.setPreferredSize(new Dimension(120, 25));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		add(cmbComponents, gbc);

		pnlMagicTypes.setLayout(new GridBagLayout());

		pnlMagicTypes.setBorder(new TitledBorder(PropertyFactory.getString("in_demAllowedMagicItemType")));
		lblPotionAllowed.setLabelFor(chkPotionAllowed);
		lblPotionAllowed.setText(PropertyFactory.getString("in_demPotion"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 0, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		pnlMagicTypes.add(lblPotionAllowed, gbc);

		chkPotionAllowed.setHorizontalTextPosition(SwingConstants.LEADING);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.5;
		pnlMagicTypes.add(chkPotionAllowed, gbc);

		lblScrollAllowed.setLabelFor(chkScrollAllowed);
		lblScrollAllowed.setText(PropertyFactory.getString("in_demScroll"));
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 0, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		pnlMagicTypes.add(lblScrollAllowed, gbc);

		chkScrollAllowed.setHorizontalTextPosition(SwingConstants.LEADING);
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.5;
		pnlMagicTypes.add(chkScrollAllowed, gbc);

		lblWandAllowed.setLabelFor(chkWandAllowed);
		lblWandAllowed.setText(PropertyFactory.getString("in_demWand"));
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 0, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		pnlMagicTypes.add(lblWandAllowed, gbc);

		chkWandAllowed.setHorizontalTextPosition(SwingConstants.LEADING);
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.5;
		pnlMagicTypes.add(chkWandAllowed, gbc);

		lblRingAllowed.setLabelFor(chkRingAllowed);
		lblRingAllowed.setText(PropertyFactory.getString("in_demRing"));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 0, 2, 2);
		gbc.anchor = GridBagConstraints.EAST;
		pnlMagicTypes.add(lblRingAllowed, gbc);

		chkRingAllowed.setHorizontalTextPosition(SwingConstants.LEADING);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 0.5;
		pnlMagicTypes.add(chkRingAllowed, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 6;
		gbc.gridwidth = 3;
		gbc.gridheight = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(pnlMagicTypes, gbc);
	}

	/**
	 * Displays a dialog to select the Descriptors for a Spell.
	 * @author <a href="mailto:dj.sourceforge@outerrim.org">Michael Osterlie</a>
	 */
	private class ChooseDescriptorAction extends AbstractAction
	{
		private JComponent parentComponent;

		public ChooseDescriptorAction(JComponent parent) {
			super("...");
			parentComponent = parent;
		}

		@Override
		public void actionPerformed(ActionEvent ae)
		{
			String currentDescriptor = txtDescriptor.getText();
			SelectDescriptorDialog dialog = new SelectDescriptorDialog(parentComponent, currentDescriptor);
			dialog.setVisible(true);
			String descriptors = dialog.getDescriptor();
			txtDescriptor.setText(descriptors);
			dialog.dispose();
		}
	}

	private class SelectDescriptorDialog extends JDialog
	{
		private AvailableSelectedPanel pnlSelectDescriptor;
		private JButton btnSave;
		private JButton btnCancel;

		private String descriptorList;

		public SelectDescriptorDialog(JComponent parent, String descriptors)
		{
			super();
			setModal(true);

			descriptorList = descriptors;

			initComponents();
			initComponentsContents();

			addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent we)
				{
					closeDialog();
				}
			});
			setLocationRelativeTo(parent);
		}

		public String getDescriptor()
		{
			return descriptorList;
		}

		private void closeDialog()
		{
			setVisible(false);
		}

		private void initComponents()
		{
			JPanel pnlDialog = new JPanel(new BorderLayout());
			pnlSelectDescriptor = new AvailableSelectedPanel();

			btnSave = new JButton(new AbstractAction(PropertyFactory.getString("in_save"))
			{
				@Override
				public void actionPerformed(ActionEvent ae)
				{
					Object[] selectedList = pnlSelectDescriptor.getSelectedList();
					StringBuffer selected = new StringBuffer();
					for(int i = 0; i < selectedList.length; ++i)
					{
						selected.append(selectedList[i]).append(",");
					}
					selected.deleteCharAt(selected.length() - 1);
					descriptorList = selected.toString();
					closeDialog();
				}
			});
			btnCancel = new JButton(new AbstractAction(PropertyFactory.getString("in_cancel"))
			{
				@Override
				public void actionPerformed(ActionEvent ae)
				{
					closeDialog();
				}
			});

			JPanel pnlButtons = new JPanel(new FlowLayout( FlowLayout.RIGHT, 3, 0));
			pnlButtons.add(btnCancel);
			pnlButtons.add(btnSave);

			pnlDialog.add(pnlSelectDescriptor, BorderLayout.CENTER);
			pnlDialog.add(pnlButtons, BorderLayout.SOUTH);

			getContentPane().add(pnlDialog);
			setSize(new Dimension(400, 400));
		}

		private void initComponentsContents()
		{
			pnlSelectDescriptor.setAvailableList(new ArrayList<String>(Globals.getDescriptorSet()), true);

			if(!descriptorList.equals(""))
			{
				StringTokenizer tokenizer = new StringTokenizer(descriptorList, ",");
				while(tokenizer.hasMoreTokens())
				{
					pnlSelectDescriptor.addItemToSelected(tokenizer.nextToken());
				}
			}
		}
	}
}
