/*
 * Copyright 2008 (C) James Dempsey
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
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.util.JComboBoxEx;
import pcgen.gui2.tools.Utility;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code DefaultsPanel} is responsible for
 * setting various defaults for characters that can be changed
 * on a per character basis, such as experience table, character
 * type, and so on. 
 * 
 * 
 */
@SuppressWarnings("serial")
public class DefaultsPanel extends PCGenPrefsPanel
{
	private static final String DEFAULT_PREVIEW_SHEET_KEY = "CharacterSheetInfoTab.defaultPreviewSheet.";
	private static final String in_defaults = LanguageBundle.getString("in_Prefs_defaults");
	private final JComboBoxEx xpTableCombo = new JComboBoxEx();
	private final JComboBoxEx characterTypeCombo = new JComboBoxEx();
	private final JComboBoxEx previewSheetCombo = new JComboBoxEx();

	/**
	 * Instantiates a new defaults panel.
	 */
	public DefaultsPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_defaults);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
//		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_xpTable"));
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		gridbag.setConstraints(xpTableCombo, c);
		this.add(xpTableCombo);

		Utility.buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_characterType"));
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		gridbag.setConstraints(characterTypeCombo, c);
		this.add(characterTypeCombo);

		Utility.buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_previewSheet"));
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		gridbag.setConstraints(previewSheetCombo, c);
		this.add(previewSheetCombo);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		this.add(label);
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return in_defaults;
	}
	
	/**
	 * @see pcgen.gui2.prefs.PreferencesPanel#applyPreferences()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		final GameMode gameMode = SettingsHandler.getGame();
		gameMode.setDefaultXPTableName(String.valueOf(xpTableCombo.getSelectedItem()));
		gameMode.setDefaultCharacterType(String.valueOf(characterTypeCombo.getSelectedItem()));
		gameMode.setDefaultPreviewSheet(String.valueOf(previewSheetCombo.getSelectedItem()));

		UIPropertyContext.getInstance().setProperty(
				DEFAULT_PREVIEW_SHEET_KEY + gameMode.getName(), String.valueOf(previewSheetCombo.getSelectedItem()));
	}

	/**
	 * @see pcgen.gui2.prefs.PreferencesPanel#initPreferences()
	 */
	@Override
	public void applyOptionValuesToControls()
	{
		final GameMode gameMode = SettingsHandler.getGame();

		final String xpTableName = gameMode.getDefaultXPTableName();
		List<String> xpTableNames = gameMode.getXPTableNames();
		xpTableCombo.removeAllItems();
		for (String name : xpTableNames)
		{
			xpTableCombo.addItem(name);
		}
		xpTableCombo.setSelectedItem(xpTableName);

		final String characterType = gameMode.getDefaultCharacterType();
		List<String> characterTypes = gameMode.getCharacterTypeList();
		characterTypeCombo.removeAllItems();
		for (String name : characterTypes)
		{
			characterTypeCombo.addItem(name);
		}
		characterTypeCombo.setSelectedItem(characterType);
		
		final String previewSheet = UIPropertyContext.getInstance().initProperty(
			DEFAULT_PREVIEW_SHEET_KEY + gameMode, gameMode.getDefaultPreviewSheet());
			
		String previewDir = ConfigurationSettings.getPreviewDir();
		File sheetDir = new File(previewDir, gameMode.getCharSheetDir());
		if (sheetDir.exists() && sheetDir.isDirectory())
		{
			String[] files = sheetDir.list((path, filename) -> {
                File file = new File(path, filename);
                return file.isFile() && !file.isHidden();
            });
			//String[] files = sheetDir.list();
			previewSheetCombo.removeAllItems();
			previewSheetCombo.setModel(new DefaultComboBoxModel(files));
			previewSheetCombo.sortItems();
			previewSheetCombo.setSelectedItem(previewSheet);
		}
	}
}
