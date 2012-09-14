/*
 *  GMGen - A role playing utility
 *  Copyright (C) 2003 Devon D Jones
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
 * AboutBox.java
 *
 * Created on September 18, 2002, 5:38 PM
 */
package gmgen.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import pcgen.system.LanguageBundle;

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
