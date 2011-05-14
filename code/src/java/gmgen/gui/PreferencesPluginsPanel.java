/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 * PreferencesDamagePanel.java
 *
 * Created on July 11, 2003, 4:34 PM
 */
package gmgen.gui;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.HashMap;

/**
 *
 * @author  soulcatcher
 */
public class PreferencesPluginsPanel extends gmgen.gui.PreferencesPanel {
	public static final HashMap<String, PluginRef> pluginMap = new HashMap<String, PluginRef>();

	private JPanel mainPanel;
	private JScrollPane jScrollPane1;

	/** Creates new form PreferencesDamagePanel */
	public PreferencesPluginsPanel() {
		initComponents();
		initPreferences();
	}

	public void applyPreferences() {
		for ( String key : pluginMap.keySet() )
		{
			pluginMap.get(key).applyPreferences();
		}
	}

	public void initPreferences() {
		for ( String key : pluginMap.keySet() )
		{
			pluginMap.get(key).initPreferences();
		}
	}

	@Override
	public String toString() {
		return "Plugin Launch";
	}

	private void initComponents() {
		jScrollPane1 = new JScrollPane();
		mainPanel = new JPanel();

		setLayout(new BorderLayout());

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		for ( String key : pluginMap.keySet() )
		{
			mainPanel.add( pluginMap.get(key) );
		}

		jScrollPane1.setViewportView(mainPanel);
		add(jScrollPane1, BorderLayout.CENTER);
		add(new JLabel("All changes will take effect the next time PCGen is restarted"), BorderLayout.SOUTH);
	}

	public static void addPanel(String pluginName, String pluginTitle, String defaultSystem) {
		if(!pluginMap.containsKey(pluginName)) {
			PluginRef pluginRef = new PluginRef(pluginName, pluginTitle, defaultSystem);
			pluginMap.put(pluginName, pluginRef);
		}
	}

	private static class PluginRef extends JPanel {
		private String pluginName;
		private String pluginTitle;
		private String defaultSystem;
		private JCheckBox checkBox;
		private JRadioButton pcgenButton;
		private JRadioButton gmgenButton;

		public PluginRef(String pluginName, String pluginTitle, String defaultSystem) {
			this.pluginName = pluginName;
			this.pluginTitle = pluginTitle;
			this.defaultSystem = defaultSystem;
			initComponents();
		}

		private void initComponents() {
			checkBox = new JCheckBox();
			pcgenButton = new JRadioButton();
			gmgenButton = new JRadioButton();
			ButtonGroup pluginGroup = new ButtonGroup();

			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setBorder(new TitledBorder(null, pluginTitle,
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 11)));

			checkBox.setText("Run this plugin?");
			add(checkBox);

			pcgenButton.setText("PCGen Window");
			pluginGroup.add(pcgenButton);
			add(pcgenButton);

			gmgenButton.setText("GMGen Window");
			pluginGroup.add(gmgenButton);
			add(gmgenButton);
		}

		public void initPreferences() {
			checkBox.setSelected(SettingsHandler.getGMGenOption(pluginName + ".Load", true));
			String system = SettingsHandler.getGMGenOption(pluginName + ".System", defaultSystem);
			if(system.equals(Constants.SYSTEM_PCGEN)) {
				pcgenButton.setSelected(true);
			}
			else {
				gmgenButton.setSelected(true);
			}
		}

		public void applyPreferences() {
			SettingsHandler.setGMGenOption(pluginName + ".Load", checkBox.isSelected());
			if(pcgenButton.isSelected()) {
				SettingsHandler.setGMGenOption(pluginName + ".System", Constants.SYSTEM_PCGEN);
			}
			else {
				SettingsHandler.setGMGenOption(pluginName + ".System", Constants.SYSTEM_GMGEN);
			}
		}
	}
}
