/*
 *  GMGen - A role playing utility
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/** This defines the preferences tree
 *
 */
public class PreferencesRootTreeNode extends DefaultMutableTreeNode
{
	private final List<PreferencesPanel> panelList = new ArrayList<>();

	public PreferencesRootTreeNode()
	{
		super("Hide me"); //$NON-NLS-1$
	}

	List<PreferencesPanel> getPanelList()
	{
		return Collections.unmodifiableList(panelList);
	}

	public void addPanel(final String plugin, final PreferencesPanel panel)
	{
		DefaultMutableTreeNode pluginNode = getPluginNode(plugin);
		pluginNode.add(new DefaultMutableTreeNode(panel));
		panelList.add(panel);
	}

	private DefaultMutableTreeNode getPluginNode(final String plugin)
	{
		if(children != null)
		{
			for (final Object obj : children)
			{
				if (obj instanceof DefaultMutableTreeNode)
				{
					DefaultMutableTreeNode mnode = (DefaultMutableTreeNode) obj;

					if (plugin.equals(mnode.getUserObject().toString()))
					{
						return mnode;
					}
				}
			}
		}

		DefaultMutableTreeNode pluginNode = new DefaultMutableTreeNode(plugin);
		add(pluginNode);

		return pluginNode;
	}
}
