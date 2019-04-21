/*
 *  Copyright (C) 2003 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 */
package gmgen.gui;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.system.LanguageBundle;

/**
 *
 */
class PreferencesPluginsPanel extends gmgen.gui.PreferencesPanel
{
	private static final Map<String, PluginRef> PLUGIN_MAP = new HashMap<>();

	private JPanel mainPanel;
	private JScrollPane jScrollPane1;

	/** Creates new form PreferencesDamagePanel */
	public PreferencesPluginsPanel()
	{
		initComponents();
		initPreferences();
	}

	@Override
	public void applyPreferences()
	{
		PLUGIN_MAP.forEach((key, pluginRef) -> pluginRef.applyPreferences());
	}

	@Override
	public void initPreferences()
	{
		PreferencesPluginsPanel.PLUGIN_MAP
			.forEach((key, pluginRef) -> pluginRef.initPreferences());
	}

	@Override
	public String toString()
	{
		return "Plugin Launch";
	}

	private void initComponents()
	{
		jScrollPane1 = new JScrollPane();
		mainPanel = new JPanel();

		setLayout(new BorderLayout());

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		PreferencesPluginsPanel.PLUGIN_MAP.forEach((key, pluginRef) -> mainPanel.add(pluginRef));

		jScrollPane1.setViewportView(mainPanel);
		add(jScrollPane1, BorderLayout.CENTER);
		add(new JLabel(LanguageBundle.getString("in_Prefs_restartInfo")), BorderLayout.SOUTH);
	}

	private static final class PluginRef extends JPanel
	{
		private final String pluginName;
		private final String pluginTitle;
		private final String defaultSystem;
		private JCheckBox checkBox;
		private JRadioButton pcgenButton;
		private JRadioButton gmgenButton;

		private PluginRef(String pluginName, String pluginTitle, String defaultSystem)
		{
			this.pluginName = pluginName;
			this.pluginTitle = pluginTitle;
			this.defaultSystem = defaultSystem;
			initComponents();
		}

		private void initComponents()
		{
			checkBox = new JCheckBox();
			pcgenButton = new JRadioButton();
			gmgenButton = new JRadioButton();
			ButtonGroup pluginGroup = new ButtonGroup();

			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setBorder(
				new TitledBorder(null, pluginTitle, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION));

			checkBox.setText(LanguageBundle.getString(pcgen.gui2.dialog.PreferencesDialog.LB_PREFS_PLUGINS_RUN));
			add(checkBox);

			pcgenButton
				.setText(LanguageBundle.getString(pcgen.gui2.dialog.PreferencesDialog.LB_PREFS_PLUGIN_PCGEN_WIN));
			pluginGroup.add(pcgenButton);
			add(pcgenButton);

			gmgenButton
				.setText(LanguageBundle.getString(pcgen.gui2.dialog.PreferencesDialog.LB_PREFS_PLUGIN_GMGEN_WIN));
			pluginGroup.add(gmgenButton);
			add(gmgenButton);
		}

		public void initPreferences()
		{
			checkBox.setSelected(SettingsHandler.getGMGenOption(pluginName + ".Load", true));
			String system = SettingsHandler.getGMGenOption(pluginName + ".System", defaultSystem);
			if (system.equals(Constants.SYSTEM_PCGEN))
			{
				pcgenButton.setSelected(true);
			}
			else
			{
				gmgenButton.setSelected(true);
			}
		}

		public void applyPreferences()
		{
			SettingsHandler.setGMGenOption(pluginName + ".Load", checkBox.isSelected());
			if (pcgenButton.isSelected())
			{
				SettingsHandler.setGMGenOption(pluginName + ".System", Constants.SYSTEM_PCGEN);
			}
			else
			{
				SettingsHandler.setGMGenOption(pluginName + ".System", Constants.SYSTEM_GMGEN);
			}
		}
	}
}
