/*
 * Copyright James Dempsey, 2013
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
 */
package pcgen.gui2.equip;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import pcgen.core.EquipmentModifier;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.util.FontManipulation;
import pcgen.gui2.util.TreeColumnCellRenderer;
import pcgen.gui3.utilty.ColorUtilty;
import pcgen.system.LanguageBundle;

/**
 * EquipQualifiedTreeCellRenderer renders an equipment modifier in a table
 * cell using color to show if the modifier can be used.
 */
public class EquipQualifiedTreeCellRenderer extends TreeColumnCellRenderer
{

    private final CharacterFacade character;
    private final EquipmentFacade equip;

    /**
     * Create a new instance of QualifiedTreeCellRenderer
     *
     * @param character The character for which this instance is rendering.
     * @param equip     The item of equipment for which this instance is rendering.
     */
    public EquipQualifiedTreeCellRenderer(CharacterFacade character, EquipmentFacade equip)
    {
        this.character = character;
        this.equip = equip;
        setTextNonSelectionColor(ColorUtilty.colorToAWTColor(UIPropertyContext.getQualifiedColor()));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
            int row, boolean focus)
    {
        Object obj = ((DefaultMutableTreeNode) value).getUserObject();
        if ("".equals(obj)) //$NON-NLS-1$
        {
            obj = LanguageBundle.getString("in_none"); //$NON-NLS-1$
        }
        super.getTreeCellRendererComponent(tree, obj, sel, expanded, leaf, row, focus);
        if (obj instanceof EquipmentModifier && !character.isQualifiedFor(equip, (EquipmentModifier) obj))
        {
            setForeground(ColorUtilty.colorToAWTColor(UIPropertyContext.getNotQualifiedColor()));
        }
        if (obj instanceof InfoFacade && ((InfoFacade) obj).isNamePI())
        {
            setFont(FontManipulation.bold_italic(getFont()));
        } else
        {
            setFont(FontManipulation.plain(getFont()));
        }
        return this;
    }

}
