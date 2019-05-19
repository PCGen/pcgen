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

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.system.LanguageBundle;

class PreferencesPluginsPanel extends gmgen.gui.PreferencesPanel
{
	private static final Map<String, PluginRef> PLUGIN_MAP = new HashMap<>();

	private JPanel mainPanel;

	/** Creates new form PreferencesDamagePanel */
	PreferencesPluginsPanel()
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
		JScrollPane jScrollPane1 = new JScrollPane();
		mainPanel = new JPanel();

		setLayout(new BorderLayout());

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		PreferencesPluginsPanel.PLUGIN_MAP.forEach((key, pluginRef) -> mainPanel.add(pluginRef));

		jScrollPane1.setViewportView(mainPanel);
		add(jScrollPane1, BorderLayout.CENTER);
		add(new JLabel(LanguageBundle.getString("in_Prefs_restartInfo")), BorderLayout.PAGE_END);
	}

	private static final class PluginRef extends JPanel
	{
		private final String pluginName;
		private AbstractButton checkBox;

		private PluginRef(String pluginName)
		{
			this.pluginName = pluginName;
			initComponents();
		}

		private void initComponents()
		{
			checkBox = new JCheckBox();

			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			checkBox.setText(LanguageBundle.getString(pcgen.gui2.dialog.PreferencesDialog.LB_PREFS_PLUGINS_RUN));
			add(checkBox);
		}

		public void initPreferences()
		{
			checkBox.setSelected(SettingsHandler.getGMGenOption(pluginName + ".Load", true));
		}

		public void applyPreferences()
		{
			SettingsHandler.setGMGenOption(pluginName + ".Load", checkBox.isSelected());
			SettingsHandler.setGMGenOption(pluginName + ".System", Constants.SYSTEM_GMGEN);
		}
	}
}
