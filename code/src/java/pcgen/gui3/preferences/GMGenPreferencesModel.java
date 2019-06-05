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

import pcgen.gui2.prefs.PCGenPrefsPanel;

import javafx.scene.control.TreeItem;

/** This defines the preferences tree
 *
 */
public class GMGenPreferencesModel extends TreeItem<PCGenPrefsPanel>
{
	public GMGenPreferencesModel()
	{
		super(null);
	}

	public void addPanel(final String plugin, final PCGenPrefsPanel panel)
	{
		TreeItem<PCGenPrefsPanel> pluginNode = getPluginNode(plugin);
		pluginNode.getChildren().add(new TreeItem<>(panel));
	}

	private TreeItem<PCGenPrefsPanel> getPluginNode(final String plugin)
	{
		for (final TreeItem<PCGenPrefsPanel> obj : getChildren())
		{
			if (plugin.equals(obj.getValue().getTitle()))
			{
				return obj;
			}
		}

		TreeItem<PCGenPrefsPanel> pluginNode = PCGenPreferencesModel
				.buildEmptyPanel(plugin, plugin);
		getChildren().add(pluginNode);

		return pluginNode;
	}
}
