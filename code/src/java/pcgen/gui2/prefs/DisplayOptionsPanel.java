/*
 * Copyright 2010 (C) James Dempsey
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
 */
package pcgen.gui2.prefs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.core.SettingsHandler;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.util.JComboBoxEx;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;

/**
 * The Class {@code DisplayOptionsPanel} is responsible for
 * displaying experience related preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * 
 */
@SuppressWarnings("serial")
public class DisplayOptionsPanel extends PCGenPrefsPanel
{
	private static final String in_displayOpts =
			LanguageBundle.getString("in_Prefs_displayOpts"); //$NON-NLS-1$

	private static final String in_useOutputNamesEquipment =
			LanguageBundle.getString("in_Prefs_useOutputNamesEquipment"); //$NON-NLS-1$
	private static final String in_useOutputNamesSpells =
			LanguageBundle.getString("in_Prefs_useOutputNamesSpells"); //$NON-NLS-1$
	private static final String in_useOutputNamesOther =
			LanguageBundle.getString("in_Prefs_useOutputNamesOther"); //$NON-NLS-1$
	private static final String in_showSkillModifierBreakdown =
			LanguageBundle.getString("in_Prefs_showSkillModifierBreakdown"); //$NON-NLS-1$
	private static final String in_showSkillRanksBreakdown =
			LanguageBundle.getString("in_Prefs_showSkillRanksBreakdown"); //$NON-NLS-1$
	private static final String in_singleChoiceOption =
			LanguageBundle.getString("in_Prefs_singleChoiceOption"); //$NON-NLS-1$
	private static final String in_cmNone =
			LanguageBundle.getString("in_Prefs_cmNone"); //$NON-NLS-1$
	private static final String in_cmSelect =
			LanguageBundle.getString("in_Prefs_cmSelect"); //$NON-NLS-1$
	private static final String in_cmSelectExit =
			LanguageBundle.getString("in_Prefs_cmSelectExit"); //$NON-NLS-1$
	private static final String[] singleChoiceMethods =
			{in_cmNone, in_cmSelect, in_cmSelectExit};

	private final JCheckBox showSkillModifier = new JCheckBox();
	private final JCheckBox showSkillRanks = new JCheckBox();

	private final JCheckBox useOutputNamesEquipment = new JCheckBox();
	private final JCheckBox useOutputNamesSpells = new JCheckBox();
	private final JCheckBox useOutputNamesOther = new JCheckBox();
	private final JComboBoxEx<String> cmbChoiceMethods = new JComboBoxEx<>(singleChoiceMethods);

	/**
	 * Instantiates a new display options panel.
	 */
	public DisplayOptionsPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_displayOpts);
		int line = 0;

		title1.setTitleJustification(TitledBorder.LEADING);
		this.setBorder(title1);
		gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(2, 2, 2, 2);

		// Automatically sort the options alphabetically.
		final SortedMap<String, JComponent> options =
                new TreeMap<>();

		options.put(in_showSkillModifierBreakdown, showSkillModifier);
		options.put(in_showSkillRanksBreakdown, showSkillRanks);
		options.put(in_singleChoiceOption, cmbChoiceMethods);
		options.put(in_useOutputNamesEquipment, useOutputNamesEquipment);
		options.put(in_useOutputNamesSpells, useOutputNamesSpells);
		options.put(in_useOutputNamesOther, useOutputNamesOther);
		
		for (Map.Entry<String, JComponent> entry : options.entrySet())
		{
			line =
					addDisplayOption(line, c, gridbag, this, entry
						.getKey(), entry.getValue());
		}

		Utility.buildConstraints(c, 0, line, GridBagConstraints.REMAINDER, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel();
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	private int addDisplayOption(final int line,
		final GridBagConstraints constraints, final GridBagLayout gridbag,
		final JPanel panel, final String labelText, final JComponent c)
	{
		if (c instanceof JCheckBox)
		{
			final JCheckBox checkbox = (JCheckBox) c;
			checkbox.setText(labelText);
			Utility.buildConstraints(constraints, 0, line,
				GridBagConstraints.REMAINDER, 1, 0, 0);
		}
		else
		{
			final JLabel label = new JLabel(labelText);
			Utility.buildConstraints(constraints, 0, line, 1, 1, 0, 0);
			panel.add(label, constraints);
			Utility.buildConstraints(constraints, 1, line,
				GridBagConstraints.REMAINDER, 1, 0, 0);
		}
		panel.add(c, constraints);
		return line + 1;
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return in_displayOpts;
	}

	/**
	 * @see pcgen.gui2.prefs.PreferencesPanel#applyPreferences()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		SettingsHandler.setGUIUsesOutputNameEquipment(useOutputNamesEquipment
			.isSelected());
		SettingsHandler.setGUIUsesOutputNameSpells(useOutputNamesSpells
			.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_SHOW_OUTPUT_NAME_FOR_OTHER_ITEMS,
			useOutputNamesOther.isSelected());
		UIPropertyContext.setSingleChoiceAction(cmbChoiceMethods
			.getSelectedIndex());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_SHOW_SKILL_MOD_BREAKDOWN,
			showSkillModifier.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_SHOW_SKILL_RANK_BREAKDOWN,
			showSkillRanks.isSelected());
	}

	/**
	 * @see pcgen.gui2.prefs.PreferencesPanel#initPreferences()
	 */
	@Override
	public void applyOptionValuesToControls()
	{
		cmbChoiceMethods.setSelectedIndex(UIPropertyContext
			.getSingleChoiceAction());
		showSkillModifier.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(
			PCGenSettings.OPTION_SHOW_SKILL_MOD_BREAKDOWN, false));
		showSkillRanks.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(
			PCGenSettings.OPTION_SHOW_SKILL_RANK_BREAKDOWN, false));
		useOutputNamesEquipment.setSelected(SettingsHandler
			.guiUsesOutputNameEquipment());
		useOutputNamesSpells.setSelected(SettingsHandler
			.guiUsesOutputNameSpells());
		useOutputNamesOther.setSelected(PCGenSettings.OPTIONS_CONTEXT
			.getBoolean(PCGenSettings.OPTION_SHOW_OUTPUT_NAME_FOR_OTHER_ITEMS,
				false));
	}

}
