/*
 * QualifiedTreeCellRenderer.java
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 * 
 * Created on Feb 16, 2011, 12:00:54 PM
 */
package pcgen.gui2.tabs.models;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.InfoFacade;
import pcgen.gui2.UIPropertyContext;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class QualifiedTreeCellRenderer extends DefaultTreeCellRenderer
{

	private CharacterFacade character;

	public QualifiedTreeCellRenderer(CharacterFacade character)
	{
		this.character = character;
		setTextNonSelectionColor(UIPropertyContext.getQualifiedColor());
		setClosedIcon(null);
		setLeafIcon(null);
		setOpenIcon(null);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		Object obj = ((DefaultMutableTreeNode) value).getUserObject();
		if ("".equals(obj))
		{
			obj = "None";
		}
		super.getTreeCellRendererComponent(tree, obj, sel, expanded, leaf, row, hasFocus);
		if (obj instanceof InfoFacade && !character.isQualifiedFor((InfoFacade) obj))
		{
			setForeground(UIPropertyContext.getNotQualifiedColor());
		}
		if (obj instanceof InfoFacade && ((InfoFacade) obj).isNamePI())
		{
			setFont(getFont().deriveFont(Font.BOLD + Font.ITALIC));
		}
		else
		{
			setFont(getFont().deriveFont(Font.PLAIN));
		}
		return this;
	}

}
