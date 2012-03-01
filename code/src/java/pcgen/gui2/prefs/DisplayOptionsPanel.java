/*
 * DisplayOptionsPanel.java
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
 *
 * Created on 16/11/2010 08:15:00
 *
 * $Id$
 */
package pcgen.gui2.prefs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import pcgen.gui2.util.JComboBoxEx;
import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;

/**
 * The Class <code>DisplayOptionsPanel</code> is responsible for 
 * displaying experience related preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class DisplayOptionsPanel extends PCGenPrefsPanel
{
	private static String in_displayOpts =
			LanguageBundle.getString("in_Prefs_displayOpts");

//	private static String in_useAutoWaitCursor =
//			LanguageBundle.getString("in_Prefs_useAutoWaitCursor");
	private static String in_useOutputNamesEquipment =
			LanguageBundle.getString("in_Prefs_useOutputNamesEquipment");
	private static String in_useOutputNamesSpells =
			LanguageBundle.getString("in_Prefs_useOutputNamesSpells");
//	private static String in_showMemory =
//			LanguageBundle.getString("in_Prefs_showMemory");
//	private static String in_showImagePreview =
//			LanguageBundle.getString("in_Prefs_showImagePreview");
	private static String in_showSkillModifierBreakdown =
			LanguageBundle.getString("in_Prefs_showSkillModifierBreakdown");
//	private static String in_showSkillRanksBreakdown =
//			LanguageBundle.getString("in_Prefs_showSkillRanksBreakdown");
	private static String in_showToolTips =
			LanguageBundle.getString("in_Prefs_showToolTips");
//	private static String in_showToolBar =
//			LanguageBundle.getString("in_Prefs_showToolBar");
	private static String in_showFeatDescription =
			LanguageBundle.getString("in_Prefs_showFeatDesciption");
	private static String in_singleChoiceOption =
			LanguageBundle.getString("in_Prefs_singleChoiceOption");
	private static String in_cmNone =
			LanguageBundle.getString("in_Prefs_cmNone");
	private static String in_cmSelect =
			LanguageBundle.getString("in_Prefs_cmSelect");
	private static String in_cmSelectExit =
			LanguageBundle.getString("in_Prefs_cmSelectExit");
	private static String[] singleChoiceMethods =
			{in_cmNone, in_cmSelect, in_cmSelectExit};

	private JCheckBox featDescriptionShown = new JCheckBox();
//	private JCheckBox showToolbar = new JCheckBox();
	private JCheckBox showSkillModifier = new JCheckBox();
//	private JCheckBox showSkillRanks = new JCheckBox();
	private JCheckBox toolTipTextShown = new JCheckBox();
//	private JCheckBox showMemory = new JCheckBox();
//	private JCheckBox showImagePreview = new JCheckBox();

	private JCheckBox useOutputNamesEquipment = new JCheckBox();
	private JCheckBox useOutputNamesSpells = new JCheckBox();
//	private JCheckBox waitCursor = new JCheckBox();
	private JComboBoxEx cmbChoiceMethods = new JComboBoxEx(singleChoiceMethods);

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

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		// Automatically sort the options alphabetically.
		final SortedMap<String, JComponent> options =
				new TreeMap<String, JComponent>();

		options.put(in_showFeatDescription, featDescriptionShown);
//		options.put(in_showMemory, showMemory);
//		options.put(in_showImagePreview, showImagePreview);
		options.put(in_showSkillModifierBreakdown, showSkillModifier);
//		options.put(in_showSkillRanksBreakdown, showSkillRanks);
//		options.put(in_showToolBar, showToolbar);
		options.put(in_showToolTips, toolTipTextShown);
		options.put(in_singleChoiceOption, cmbChoiceMethods);
//		options.put(in_useAutoWaitCursor, waitCursor);
		options.put(in_useOutputNamesEquipment, useOutputNamesEquipment);
		options.put(in_useOutputNamesSpells, useOutputNamesSpells);

		for (Map.Entry<String, JComponent> entry : options.entrySet())
		{
			line =
					addDisplayOption(line, c, gridbag, this, entry
						.getKey(), entry.getValue());
		}

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	private int addDisplayOption(final int line,
		final GridBagConstraints constraints, final GridBagLayout gridbag,
		final JPanel panel, final String labelText, final JComponent c)
	{
		final JLabel label = new JLabel(labelText + ": ");

		Utility.buildConstraints(constraints, 0, line, 2, 1, 0, 0);
		gridbag.setConstraints(label, constraints);
		panel.add(label);

		// Clicking on the label is just as good as clicking on the c.
		// This is closer to how selection boxes work elsewhere as well.
		if (c instanceof JCheckBox)
		{
			final JCheckBox checkbox = (JCheckBox) c;

			label.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(final MouseEvent e)
				{
					checkbox.setSelected(!checkbox.isSelected());
				}
			});
		}

		Utility.buildConstraints(constraints, 2, line, 1, 1, 0, 0);
		gridbag.setConstraints(c, constraints);
		panel.add(c);

		return line + 1;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return in_displayOpts;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.prefs.PreferencesPanel#applyPreferences()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		SettingsHandler.setToolTipTextShown(toolTipTextShown.isSelected());
//		SettingsHandler.setShowMemoryArea(showMemory.isSelected());
//		SettingsHandler.setShowImagePreview(showImagePreview.isSelected());
//		SettingsHandler.setToolBarShown(showToolbar.isSelected());
//		SettingsHandler.setUseWaitCursor(waitCursor.isSelected());
		SettingsHandler.setGUIUsesOutputNameEquipment(useOutputNamesEquipment
			.isSelected());
		SettingsHandler.setGUIUsesOutputNameSpells(useOutputNamesSpells
			.isSelected());
		SettingsHandler.setSingleChoicePreference(cmbChoiceMethods
			.getSelectedIndex());
		SettingsHandler.setUseFeatBenefits(!featDescriptionShown.isSelected());
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_SHOW_SKILL_MOD_BREAKDOWN,
			showSkillModifier.isSelected());
//		SettingsHandler.setShowSkillRanks(showSkillRanks.isSelected());
	}

	/* (non-Javadoc)
	 * @see pcgen.gui2.prefs.PreferencesPanel#initPreferences()
	 */
	@Override
	public void applyOptionValuesToControls()
	{
		cmbChoiceMethods.setSelectedIndex(SettingsHandler
			.getSingleChoicePreference());
		featDescriptionShown.setSelected(!SettingsHandler.useFeatBenefits());
//		showMemory.setSelected(SettingsHandler.isShowMemoryArea());
//		showImagePreview.setSelected(SettingsHandler.isShowImagePreview());
		showSkillModifier.setSelected(PCGenSettings.OPTIONS_CONTEXT.getBoolean(
			PCGenSettings.OPTION_SHOW_SKILL_MOD_BREAKDOWN, false));
//		showSkillRanks.setSelected(SettingsHandler.getShowSkillRanks());
//		showToolbar.setSelected(SettingsHandler.isToolBarShown());
		toolTipTextShown.setSelected(SettingsHandler.isToolTipTextShown());
		useOutputNamesEquipment.setSelected(SettingsHandler
			.guiUsesOutputNameEquipment());
		useOutputNamesSpells.setSelected(SettingsHandler
			.guiUsesOutputNameSpells());
//		waitCursor.setSelected(SettingsHandler.getUseWaitCursor());
	}

}
