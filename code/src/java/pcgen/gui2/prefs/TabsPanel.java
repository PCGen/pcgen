/*
 * Copyright 2010(C) James Dempsey
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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.JComboBoxEx;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * The Class {@code TabsPanel} is responsible for
 * displaying tabs display related preferences and allowing the 
 * preferences to be edited by the user.
 * 
 * 
 */
@SuppressWarnings("serial")
public class TabsPanel extends PCGenPrefsPanel
{
	private static final String IN_TABS = LanguageBundle.getString("in_Prefs_tabs");

	private static final String IN_CHAR_TAB_PLACEMENT = LanguageBundle.getString("in_Prefs_charTabPlacement");
	private static final String IN_CHAR_TAB_LABEL = LanguageBundle.getString("in_Prefs_charTabLabel");
	private static final String IN_MAIN_TAB_PLACEMENT = LanguageBundle.getString("in_Prefs_mainTabPlacement");
	private static final String IN_TAB_LABEL_PLAIN = LanguageBundle.getString("in_Prefs_tabLabelPlain");
	private static final String IN_TAB_LABEL_EPIC = LanguageBundle.getString("in_Prefs_tabLabelEpic");
	private static final String IN_TAB_LABEL_RACE = LanguageBundle.getString("in_Prefs_tabLabelRace");
	private static final String IN_TAB_LABEL_NETHACK = LanguageBundle.getString("in_Prefs_tabLabelNetHack");
	private static final String IN_TAB_LABEL_FULL = LanguageBundle.getString("in_Prefs_tabLabelFull");
	private static final String IN_TAB_POS_TOP = LanguageBundle.getString("in_Prefs_tabPosTop");
	private static final String IN_TAB_POS_BOTTOM = LanguageBundle.getString("in_Prefs_tabPosBottom");
	private static final String IN_TAB_POS_LEFT = LanguageBundle.getString("in_Prefs_tabPosLeft");
	private static final String IN_TAB_POS_RIGHT = LanguageBundle.getString("in_Prefs_tabPosRight");

	private final JComboBoxEx charTabPlacementCombo;
	private final JComboBoxEx mainTabPlacementCombo;
	private final JComboBoxEx tabLabelsCombo;

	/**
	 * Instantiates a new Tabs panel.
	 */
	public TabsPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, IN_TABS);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label = new JLabel(IN_MAIN_TAB_PLACEMENT + ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		mainTabPlacementCombo =
				new JComboBoxEx<>(new String[]{IN_TAB_POS_TOP, IN_TAB_POS_BOTTOM, IN_TAB_POS_LEFT, IN_TAB_POS_RIGHT});
		gridbag.setConstraints(mainTabPlacementCombo, c);
		this.add(mainTabPlacementCombo);

		Utility.buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label = new JLabel(IN_CHAR_TAB_PLACEMENT + ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		charTabPlacementCombo =
				new JComboBoxEx<>(new String[]{IN_TAB_POS_TOP, IN_TAB_POS_BOTTOM, IN_TAB_POS_LEFT, IN_TAB_POS_RIGHT});
		gridbag.setConstraints(charTabPlacementCombo, c);
		this.add(charTabPlacementCombo);

		Utility.buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label = new JLabel(IN_CHAR_TAB_LABEL + ": ");
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		tabLabelsCombo = new JComboBoxEx<>(
			new String[]{IN_TAB_LABEL_PLAIN, IN_TAB_LABEL_EPIC, IN_TAB_LABEL_RACE, IN_TAB_LABEL_NETHACK, IN_TAB_LABEL_FULL});
		gridbag.setConstraints(tabLabelsCombo, c);
		this.add(tabLabelsCombo);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	@Override
	public String getTitle()
	{
		return IN_TABS;
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		switch (mainTabPlacementCombo.getSelectedIndex())
		{
			case 0:
				SettingsHandler.setTabPlacement(SwingConstants.TOP);

				break;

			case 1:
				SettingsHandler.setTabPlacement(SwingConstants.BOTTOM);

				break;

			case 2:
				SettingsHandler.setTabPlacement(SwingConstants.LEFT);

				break;

			case 3:
				SettingsHandler.setTabPlacement(SwingConstants.RIGHT);

				break;

			default:
				Logging.errorPrint("In PreferencesDialog.setOptionsBasedOnControls (mainTabPlacementCombo) the index "
					+ mainTabPlacementCombo.getSelectedIndex() + " is unsupported.");

				break;
		}

		switch (charTabPlacementCombo.getSelectedIndex())
		{
			case 0:
				SettingsHandler.setChaTabPlacement(SwingConstants.TOP);

				break;

			case 1:
				SettingsHandler.setChaTabPlacement(SwingConstants.BOTTOM);

				break;

			case 2:
				SettingsHandler.setChaTabPlacement(SwingConstants.LEFT);

				break;

			case 3:
				SettingsHandler.setChaTabPlacement(SwingConstants.RIGHT);

				break;

			default:
				Logging.errorPrint("In PreferencesDialog.setOptionsBasedOnControls (charTabPlacementCombo) the index "
					+ charTabPlacementCombo.getSelectedIndex() + " is unsupported.");

				break;
		}

		switch (tabLabelsCombo.getSelectedIndex())
		{
			case 0:
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME);

				break;

			case 1:
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_CLASS);

				break;

			case 2:
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_RACE);

				break;

			case 3:
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_RACE_CLASS);

				break;

			case 4:
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_FULL);

				break;

			default:
				Logging.errorPrint("In PreferencesDialog.setOptionsBasedOnControls (tabLabelsCombo) the index "
					+ tabLabelsCombo.getSelectedIndex() + " is unsupported.");

				break;
		}
	}

	@Override
	public void applyOptionValuesToControls()
	{
		switch (SettingsHandler.getTabPlacement())
		{
			case SwingConstants.TOP:
				mainTabPlacementCombo.setSelectedIndex(0);

				break;

			case SwingConstants.BOTTOM:
				mainTabPlacementCombo.setSelectedIndex(1);

				break;

			case SwingConstants.LEFT:
				mainTabPlacementCombo.setSelectedIndex(2);

				break;

			case SwingConstants.RIGHT:
				mainTabPlacementCombo.setSelectedIndex(3);

				break;

			default:
				Logging.errorPrint("In PreferencesDialog.applyOptionValuesToControls (tab placement) the tab option "
					+ SettingsHandler.getTabPlacement() + " is unsupported.");

				break;
		}

		switch (SettingsHandler.getChaTabPlacement())
		{
			case SwingConstants.TOP:
				charTabPlacementCombo.setSelectedIndex(0);

				break;

			case SwingConstants.BOTTOM:
				charTabPlacementCombo.setSelectedIndex(1);

				break;

			case SwingConstants.LEFT:
				charTabPlacementCombo.setSelectedIndex(2);

				break;

			case SwingConstants.RIGHT:
				charTabPlacementCombo.setSelectedIndex(3);

				break;

			default:
				Logging
					.errorPrint("In PreferencesDialog.applyOptionValuesToControls (cha tab placement) the tab option "
						+ SettingsHandler.getChaTabPlacement() + " is unsupported.");

				break;
		}

		switch (SettingsHandler.getNameDisplayStyle())
		{
			case Constants.DISPLAY_STYLE_NAME:
				tabLabelsCombo.setSelectedIndex(0);

				break;

			case Constants.DISPLAY_STYLE_NAME_CLASS:
				tabLabelsCombo.setSelectedIndex(1);

				break;

			case Constants.DISPLAY_STYLE_NAME_RACE:
				tabLabelsCombo.setSelectedIndex(2);

				break;

			case Constants.DISPLAY_STYLE_NAME_RACE_CLASS:
				tabLabelsCombo.setSelectedIndex(3);

				break;

			case Constants.DISPLAY_STYLE_NAME_FULL:
				tabLabelsCombo.setSelectedIndex(4);

				break;

			default:
				Logging
					.errorPrint("In PreferencesDialog.applyOptionValuesToControls (name display style) the tab option "
						+ SettingsHandler.getNameDisplayStyle() + " is unsupported.");

				break;
		}
	}

}
