/*
 * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
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

package pcgen.gui3.preferences;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.base.Constants;
import pcgen.gui2.prefs.PCGenPrefsPanel;
import pcgen.gui3.GuiUtility;
import pcgen.pluginmgr.PluginManager;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public final class PreferencesPluginsPanel extends PCGenPrefsPanel
{
	private static final String LB_PREFS_PLUGINS_RUN = "in_Prefs_pluginsRun"; //$NON-NLS-1$

	private final Map<String, PluginRef> pluginMap = new HashMap<>();

	private VBox mainPanel;

	/** Creates new form PreferencesDamagePanel */
	public PreferencesPluginsPanel()
	{
		for (PluginManager.PluginInfo info : PluginManager.getInstance().getPluginInfoList())
		{
			addPanel(info.logName, info.pluginName);
		}
		initComponents();
	}

	@Override
	public String toString()
	{
		return LanguageBundle.getString("in_Prefs_pluginsTitle"); //$NON-NLS-1$
	}

	private void initComponents()
	{

		mainPanel = new VBox();

		setLayout(new BorderLayout());

		pluginMap.values()
		         .forEach(pluginRef -> mainPanel.getChildren().add(pluginRef));

		mainPanel.getChildren().add(new Text(LanguageBundle.getString("in_Prefs_restartInfo")));
		ScrollPane scrollPane = new ScrollPane(mainPanel);
		scrollPane.setContent(mainPanel);
		this.add(GuiUtility.wrapParentAsJFXPanel(scrollPane));
	}

	private void addPanel(String pluginName, String pluginTitle)
	{
		if (!pluginMap.containsKey(pluginName))
		{
			PluginRef pluginRef = new PluginRef(pluginName, pluginTitle, Constants.SYSTEM_GMGEN);
			pluginMap.put(pluginName, pluginRef);
		}
	}

	@Override
	public String getTitle()
	{
		return LanguageBundle.getString("in_Prefs_plugins");
	}

	@Override
	public void applyOptionValuesToControls()
	{
		pluginMap.values()
		         .forEach(PluginRef::initPreferences);
	}

	@Override
	public void setOptionsBasedOnControls()
	{
		pluginMap.values()
		         .forEach(PluginRef::applyPreferences);
	}

	private static final class PluginRef extends Pane
	{
		private final String pluginName;
		private final String pluginTitle;
		private CheckBox checkBox;

		private PluginRef(String pluginName, String pluginTitle, String defaultSystem)
		{
			this.pluginName = pluginName;
			this.pluginTitle = pluginTitle;
			initComponents();
		}

		private void initComponents()
		{
			checkBox = new CheckBox(LanguageBundle.getString(LB_PREFS_PLUGINS_RUN));
			TitledPane titledPane = new TitledPane(pluginTitle, checkBox);
			getChildren().add(titledPane);
		}

		private void initPreferences()
		{
			checkBox.setSelected(PCGenSettings.GMGEN_OPTIONS_CONTEXT.initBoolean(pluginName + ".Load", true));
		}

		private void applyPreferences()
		{
			PCGenSettings.GMGEN_OPTIONS_CONTEXT.setBoolean(pluginName + ".Load", checkBox.isSelected());
			PCGenSettings.GMGEN_OPTIONS_CONTEXT.setProperty(pluginName + ".System", Constants.SYSTEM_GMGEN);
		}
	}
}
