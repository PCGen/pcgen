/*
 * QualifiedSpellTreeCellRenderer.java
 * Copyright James Dempsey, 2012
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
 * Created on 05/06/2012 9:40:42 PM
 *
 * $Id$
 */
package pcgen.gui2.tabs.spells;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.ClassFacade;
import pcgen.core.facade.InfoFacade;
import pcgen.core.facade.SpellFacade;
import pcgen.core.facade.SpellSupportFacade.SpellNode;
import pcgen.gui2.UIPropertyContext;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>QualifiedSpellTreeCellRenderer</code> renders a spell tree cell 
 * with colouring indicating if the item can be known by the character. It is 
 * heavily based on QualifiedTreeCellRenderer 
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class QualifiedSpellTreeCellRenderer extends DefaultTreeCellRenderer
{

	/** Version for serialisation. */
	private static final long serialVersionUID = -5763535370085434234L;

	private CharacterFacade character;

	/**
	 * Create a new instance of QualifiedSpellTreeCellRenderer
	 * @param character The character for which this instance is rendering.
	 */
	public QualifiedSpellTreeCellRenderer(CharacterFacade character)
	{
		this.character = character;
		setTextNonSelectionColor(UIPropertyContext.getQualifiedColor());
		setClosedIcon(null);
		setLeafIcon(null);
		setOpenIcon(null);
	}

	/**
	 * {@inheritDoc}
	 */
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
		if (obj instanceof SpellNode)
		{
			SpellNode spellNode = (SpellNode) obj;
			SpellFacade spell = spellNode.getSpell();
			ClassFacade pcClass = spellNode.getSpellcastingClass();
			if (!character.isQualifiedFor(spell, pcClass))
			{
				setForeground(UIPropertyContext.getNotQualifiedColor());
			}
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
