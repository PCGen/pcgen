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
 * AboutBox.java
 *
 * Created on September 18, 2002, 5:38 PM
 */
package gmgen.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import java.util.ArrayList;
import java.util.List;

/** This defines the preferences tree
 *
 * @author  devon
 */
public class PreferencesRootTreeNode extends DefaultMutableTreeNode
{
	private List<PreferencesPanel> panelList = new ArrayList<PreferencesPanel>();

	public PreferencesRootTreeNode()
	{
		super("Hide me"); //$NON-NLS-1$
	}

	public List<PreferencesPanel> getPanelList()
	{
		return panelList;
	}

	public void addPanel(String plugin, PreferencesPanel panel)
	{
		DefaultMutableTreeNode pluginNode = getPluginNode(plugin);
		pluginNode.add(new DefaultMutableTreeNode(panel));
		panelList.add(panel);
	}

	private DefaultMutableTreeNode getPluginNode(String plugin)
	{
		if(children != null)
		{
			for (Object obj : children)
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
