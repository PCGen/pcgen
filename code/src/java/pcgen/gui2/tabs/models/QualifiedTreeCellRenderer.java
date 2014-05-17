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
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.ref.WeakReference;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.InfoFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class QualifiedTreeCellRenderer extends DefaultTreeCellRenderer
{

	/**
	 * Java Swing seems to have issues garbage collecting Cell Renderers so we
	 * are using a weak reference here to make sure the character can always be
	 * garbage collected
	 */
	private final WeakReference<CharacterFacade> characterRef;

	/**
	 * Create a new instance of QualifiedTreeCellRenderer
	 *
	 * @param character The character for which this instance is rendering.
	 */
	public QualifiedTreeCellRenderer(CharacterFacade character)
	{
		this.characterRef = new WeakReference<CharacterFacade>(character);
		setTextNonSelectionColor(UIPropertyContext.getQualifiedColor());
		setClosedIcon(null);
		setLeafIcon(null);
		setOpenIcon(null);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row, boolean focus)
	{
		Object obj = ((DefaultMutableTreeNode) value).getUserObject();
		if ("".equals(obj)) //$NON-NLS-1$
		{
			obj = LanguageBundle.getString("in_none"); //$NON-NLS-1$
		}
		super.getTreeCellRendererComponent(tree, obj, sel, expanded, leaf, row, focus);
		if (obj instanceof InfoFacade && !characterRef.get().isQualifiedFor((InfoFacade) obj))
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

	/**
	 * This is necessary because Java's Swing automatically adds this component
	 * to a container when it is drawn but does not ever remove it. This means
	 * that the component will exist forever in the component hierarchy and thus
	 * never be garbage collected. We must remove it from the hierarchy
	 * ourselves to solve the problem.
	 */
	public void uninstall()
	{
		Container parent = getParent();
		if (parent != null)
		{
			parent.remove(this);
		}
	}

}
