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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import pcgen.cdom.base.Constants;
import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.JComboBoxEx;
import pcgen.system.LanguageBundle;

/**
 * The Class {@code CopySettingsPanel} is responsible for
 * allowing game mode dependent settings to be copied from another 
 * gamemode.
 * 
 * 
 */
@SuppressWarnings("serial")
public class CopySettingsPanel extends PCGenPrefsPanel
{
	private static final String IN_COPY_SETTINGS = LanguageBundle.getString("in_Prefs_copy");

	private final JComboBoxEx gameModeSelect = new JComboBoxEx<>();
	private final JButton copyButton = new JButton(LanguageBundle.getString("in_copy"));

	private final List<PCGenPrefsPanel> affectedPanels = new ArrayList<>();

	/**
	 * Instantiates a new copy settings panel.
	 */
	public CopySettingsPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, IN_COPY_SETTINGS);

		title1.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(title1);
		gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(2, 2, 2, 2);

		gameModeSelect.setAllItems(SystemCollections.getUnmodifiableGameModeList().toArray());
		gameModeSelect.sortItems();

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_copyFrom"));
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		gridbag.setConstraints(gameModeSelect, c);
		this.add(gameModeSelect);
		Utility.buildConstraints(c, 3, 1, 1, 1, 0, 0);
		label = new JLabel(LanguageBundle.getFormattedString("in_Prefs_copyTo", SettingsHandler.getGame().getName()));
		gridbag.setConstraints(label, c);
		this.add(label);
		Utility.buildConstraints(c, 4, 1, 1, 1, 0, 0);
		gridbag.setConstraints(copyButton, c);
		this.add(copyButton);

		copyButton.addActionListener(new CopyButtonListener());

		Utility.buildConstraints(c, 0, 2, 4, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_copyDesc"));

		gridbag.setConstraints(label, c);
		this.add(label);

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
		return IN_COPY_SETTINGS;
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#setOptionsBasedOnControls()
	 */
	@Override
	public void setOptionsBasedOnControls()
	{
		// Do nothing
	}

	/**
	 * @see pcgen.gui2.prefs.PCGenPrefsPanel#applyOptionValuesToControls()
	 */
	@Override
	public void applyOptionValuesToControls()
	{
		// Do nothing
	}

	/**
	 * Register the other settings panels that can be affected by this 
	 * class.
	 * 
	 * @param panel The ExperiencePanel instance
	 */
	public void registerAffectedPanel(PCGenPrefsPanel panel)
	{
		affectedPanels.add(panel);
	}

	/**
	 * Handler for the Copy button.
	 */
	private final class CopyButtonListener implements ActionListener
	{

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			GameMode gmFrom = (GameMode) gameModeSelect.getSelectedItem();
			GameMode gmTo = SettingsHandler.getGame();

			// Copy the settings from one mode to the other
			gmTo.setAllStatsValue(gmFrom.getAllStatsValue());
			gmTo.setRollMethodExpressionByName(gmFrom.getRollMethodExpressionName());
			if (gmTo.getPurchaseMethodByName(gmFrom.getPurchaseModeMethodName()) != null)
			{
				gmTo.setPurchaseMethodName(gmFrom.getPurchaseModeMethodName());
			}
			gmTo.setRollMethod(gmFrom.getRollMethod());
			gmTo.selectUnitSet(gmFrom.getUnitSet().getKeyName());
			if (gmTo.getXPTableNames().contains(gmFrom.getDefaultXPTableName()))
			{
				gmTo.setDefaultXPTableName(gmFrom.getDefaultXPTableName());
			}
			String currentICS =
					SettingsHandler.getPCGenOption("InfoCharacterSheet." + gmTo.getName() + ".CurrentSheet", "");
			String fromGmICS = SettingsHandler
				.getPCGenOption("InfoCharacterSheet." + gmFrom.getName() + ".CurrentSheet", currentICS);
			SettingsHandler.setPCGenOption("InfoCharacterSheet." + gmTo.getName() + ".CurrentSheet", fromGmICS);

			// Refresh the affected settings panels
			for (PCGenPrefsPanel panel : affectedPanels)
			{
				panel.applyOptionValuesToControls();
			}

			// Let the user know it is done
			ShowMessageDelegate.showMessageDialog(LanguageBundle.getString("in_Prefs_copyDone"),
				Constants.APPLICATION_NAME, MessageType.INFORMATION);

		}
	}
}
